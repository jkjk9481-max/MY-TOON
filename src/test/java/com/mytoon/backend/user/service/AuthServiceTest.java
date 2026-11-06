package com.mytoon.backend.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks // "위에서 만든 가짜 서랍 3개를 생성자에 끼워 넣어서 진짜 AuthService를 조립해줘"
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

        // when
        LoginResult result = authService.loginOrRegister(Provider.KAKAO, "kakao-999", "ignored@b.com", "무시됨");

        // then: 신규 생성 경로는 전혀 타지 않아야 한다
        verify(userRepository, never()).save(any(User.class)); // save를 한 번도 안불렀나?
        verify(socialAccountRepository, never()).save(any(SocialAccount.class));
        // 하지만 로그인마다 토큰은 '추가' 발급된다 (멀티 디바이스 정책)
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(result.user()).isSameAs(existingUser);
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

    //  Q2  │ empty()=없음→신규, of(계정)=있음→단골 (스위치!)        │
    //├─────┼────────────────────────────────────────────────────────┤
    //│ Q3  │ never().save() = "단골한테 새 User 안 만든다"는 증명   │
    //├─────┼────────────────────────────────────────────────────────┤
    //│ Q4  │ ArgumentCaptor = 내부 객체를 save 순간 낚아채는 카메라
}
