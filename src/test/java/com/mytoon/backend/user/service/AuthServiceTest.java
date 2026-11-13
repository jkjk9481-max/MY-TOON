package com.mytoon.backend.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mytoon.backend.security.JwtTokenProvider;
import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.entity.RefreshToken;
import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.user.entity.SocialAccount;
import com.mytoon.backend.user.entity.User;
import com.mytoon.backend.user.entity.UserStatus;
import com.mytoon.backend.user.repository.RefreshTokenRepository;
import com.mytoon.backend.user.repository.SocialAccountRepository;
import com.mytoon.backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class) // "이 테스트에서 Mockito 기능 켜줘"
class AuthServiceTest {

    @Mock
    private UserRepository userRepository; // 대역 배우 생성

    @Mock
    private SocialAccountRepository socialAccountRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks // "위에서 만든 가짜 서랍 4개를 생성자에 끼워 넣어서 진짜 AuthService를 조립해줘"
    // 대역들을 넣어서 만든 '진짜' 서비스
    private AuthService authService;

    @Test
    @DisplayName("처음 보는 소셜 계정이면 User와 SocialAccount를 새로 만든다")
    void loginOrRegister_newSocialAccount_createsUserAndSocialAccount() {
        // given: 연결표에 해당 계정이 없음 -> 신규 가입 경로
        // 만약 연결표에서 google-123을 찾으면 -> 빈 봉투를 돌려줘
        when(socialAccountRepository.findByProviderAndProviderUserId(Provider.GOOGLE, "google-123"))
                .thenReturn(Optional.empty());
        // save(x)가 넘겨받은 그 객체를 그대로 돌려주도록 흉내 (실제 DB처럼 id를 채워주진 않지만 로직 검증엔 충분)
        // save에 넘어온 그 User를 그대로 돌려줘
        // any(User.class) = "어떤 User가 들어오든 상관없어". 인자를 정확히 안맞춰도 되게하는 와일드카드
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(socialAccountRepository.save(any(SocialAccount.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtTokenProvider.generateAccessToken(any(), eq(Role.USER))).thenReturn("fake-access-token");

        // when
        LoginResult result = authService.loginOrRegister(Provider.GOOGLE, "google-123", "a@b.com", "홍길동");

        // then: User가 USER/ACTIVE로 저장됐는지 캡처해서 확인
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());// save를 1번 호출했나?
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getNickname()).isEqualTo("홍길동");
        assertThat(savedUser.getEmail()).isEqualTo("a@b.com");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER); // 같아야함
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);

        // SocialAccount도 올바른 provider/providerUserId로 저장됐는지 확인
        // ArgumentCaptor = "save에 넘어간 인자를 몰래 캡처하는 카메라 역할"
        ArgumentCaptor<SocialAccount> accountCaptor = ArgumentCaptor.forClass(SocialAccount.class);
        verify(socialAccountRepository).save(accountCaptor.capture());
        SocialAccount savedAccount = accountCaptor.getValue();
        assertThat(savedAccount.getProvider()).isEqualTo(Provider.GOOGLE);
        assertThat(savedAccount.getProviderUserId()).isEqualTo("google-123");
        assertThat(savedAccount.getUser()).isSameAs(savedUser);

        // 리프레시 토큰도 한 번 발급됐고, 원문이 호출자에게 반환됐는지 확인
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(result.user()).isSameAs(savedUser); // "똑같은 객체"여야함
        assertThat(result.refreshToken()).isNotBlank(); // 비어있지 않아야함
        // Access Token도 JwtTokenProvider가 발급한 값이 그대로 담겨야 한다
        assertThat(result.accessToken()).isEqualTo("fake-access-token");
    }

    @Test
    @DisplayName("이미 있는 소셜 계정이면 기존 User를 재사용하고 새 User를 만들지 않는다")
    void loginOrRegister_existingSocialAccount_reusesUser() {
        // given: 이미 가입된 단골 손님
        User existingUser = User.builder()
                .nickname("기존유저").email("old@b.com")
                .role(Role.USER).status(UserStatus.ACTIVE)
                .build();
        SocialAccount existingAccount = SocialAccount.builder()
                .user(existingUser).provider(Provider.KAKAO).providerUserId("kakao-999")
                .build();
        when(socialAccountRepository.findByProviderAndProviderUserId(Provider.KAKAO, "kakao-999"))
                .thenReturn(Optional.of(existingAccount));
        when(jwtTokenProvider.generateAccessToken(any(), eq(Role.USER))).thenReturn("fake-access-token");

        // when
        LoginResult result = authService.loginOrRegister(Provider.KAKAO, "kakao-999", "ignored@b.com", "무시됨");

        // then: 신규 생성 경로는 전혀 타지 않아야 한다
        verify(userRepository, never()).save(any(User.class)); // save를 한 번도 안불렀나?
        verify(socialAccountRepository, never()).save(any(SocialAccount.class));
        // 하지만 로그인마다 토큰은 '추가' 발급된다 (멀티 디바이스 정책)
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(result.user()).isSameAs(existingUser);
        assertThat(result.accessToken()).isEqualTo("fake-access-token");
    }

    @Test
    @DisplayName("리프레시 토큰은 원문이 아니라 해시로 저장되고, 만료는 30일 뒤로 설정된다")
    void loginOrRegister_storesHashedToken_withThirtyDayExpiry() {
        // given: 신규 가입 경로로 토큰 하나를 발급시킨다
        when(socialAccountRepository.findByProviderAndProviderUserId(any(Provider.class), any(String.class)))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(socialAccountRepository.save(any(SocialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        Instant beforeCall = Instant.now();

        // when
        LoginResult result = authService.loginOrRegister(Provider.GOOGLE, "google-123", "a@b.com", "홍길동");

        // then: DB에 저장된 RefreshToken을 캡처
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken savedToken = tokenCaptor.getValue();

        // 저장된 값은 원문과 달라야 한다 (= 해시가 저장됨)
        assertThat(savedToken.getTokenHash()).isNotEqualTo(result.refreshToken());
        // SHA-256(32바이트)를 표준 Base64로 인코딩하면 길이가 44
        assertThat(savedToken.getTokenHash()).hasSize(44);

        // 만료 시각은 호출 시점 기준 약 30일 뒤
        assertThat(savedToken.getExpiresAt())
                .isAfter(beforeCall.plus(29, ChronoUnit.DAYS))
                .isBefore(beforeCall.plus(31, ChronoUnit.DAYS));
    }

    @Test
    @DisplayName("정상 Refresh Token이면 기존 토큰은 폐기(revoke)되고 새 Access/Refresh Token이 발급된다")
    void refresh_validToken_rotatesAndIssuesNewTokens() {
        // given
        User user = User.builder()
                .nickname("홍길동").email("a@b.com")
                .role(Role.USER).status(UserStatus.ACTIVE)
                .build();
        RefreshToken existingToken = RefreshToken.builder()
                .user(user)
                .tokenHash("old-hash")
                .expiresAt(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(existingToken));
        when(jwtTokenProvider.generateAccessToken(any(), eq(Role.USER))).thenReturn("new-access-token");

        // when
        LoginResult result = authService.refresh("raw-refresh-token");

        // then: 쓴 토큰은 폐기됐어야 한다 (= 재사용 시도 시 바로 걸림)
        assertThat(existingToken.isRevoked()).isTrue();
        // 새 Refresh Token이 하나 더 발급돼서 저장됐어야 한다
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(result.user()).isSameAs(user);
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 Refresh Token이면 401을 던진다")
    void refresh_unknownToken_throwsUnauthorized() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("no-such-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("이미 폐기된(재사용된) Refresh Token이 들어오면 탈취로 간주해 해당 유저의 모든 기기 토큰을 무효화한다")
    void refresh_reusedRevokedToken_revokesAllActiveTokensOfUser() {
        // given: 이미 rotation으로 폐기된 토큰이 다시 들어온 상황
        User user = User.builder()
                .nickname("홍길동").email("a@b.com")
                .role(Role.USER).status(UserStatus.ACTIVE)
                .build();
        RefreshToken reusedToken = RefreshToken.builder()
                .user(user)
                .tokenHash("old-hash")
                .expiresAt(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();
        reusedToken.revoke(); // 이미 한 번 폐기됐던 상태

        RefreshToken otherActiveToken = RefreshToken.builder()
                .user(user)
                .tokenHash("other-hash")
                .expiresAt(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(reusedToken));
        when(refreshTokenRepository.findAllByUserAndRevokedFalse(user)).thenReturn(List.of(otherActiveToken));

        // when / then
        assertThatThrownBy(() -> authService.refresh("stolen-raw-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.UNAUTHORIZED);

        // 같은 유저의 다른 살아있는 토큰(다른 기기)도 전부 폐기됐어야 한다
        assertThat(otherActiveToken.isRevoked()).isTrue();
        verify(jwtTokenProvider, never()).generateAccessToken(any(), any());
    }

    @Test
    @DisplayName("만료된 Refresh Token이면 401을 던지고 새 토큰을 발급하지 않는다")
    void refresh_expiredToken_throwsUnauthorized() {
        User user = User.builder()
                .nickname("홍길동").email("a@b.com")
                .role(Role.USER).status(UserStatus.ACTIVE)
                .build();
        RefreshToken expiredToken = RefreshToken.builder()
                .user(user)
                .tokenHash("old-hash")
                .expiresAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authService.refresh("expired-raw-token"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(jwtTokenProvider, never()).generateAccessToken(any(), any());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    //  Q2  │ empty()=없음→신규, of(계정)=있음→단골 (스위치!)        │
    //├─────┼────────────────────────────────────────────────────────┤
    //│ Q3  │ never().save() = "단골한테 새 User 안 만든다"는 증명   │
    //├─────┼────────────────────────────────────────────────────────┤
    //│ Q4  │ ArgumentCaptor = 내부 객체를 save 순간 낚아채는 카메라
}
