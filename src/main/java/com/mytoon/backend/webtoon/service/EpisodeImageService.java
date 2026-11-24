package com.mytoon.backend.webtoon.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.mytoon.backend.webtoon.controller.dto.EpisodeImageResponse;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeImage;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.repository.EpisodeImageRepository;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;

import lombok.RequiredArgsConstructor;

// 회차 이미지 업로드(전체 교체 방식).
// 파일을 디스크에 쓰는 동작은 @Transactional 롤백 대상이 아니므로, 새 파일을 전부 임시 이름으로
// 먼저 써보고 성공한 뒤에만 DB를 바꾸고, 그 다음에야 기존 파일을 지우고 임시 파일을 최종 이름으로 바꾼다.
// DB 구간(기존 행 삭제 + 새 행 삽입)만 TransactionTemplate으로 별도 트랜잭션을 묶는다 -
// upload() 메서드 전체를 @Transactional로 걸면 파일 I/O 중에도 DB 커넥션을 쥐고 있게 되고,
// finalize(파일 교체) 중 실패가 나면 이미 commit된 DB와 아직 안 바뀐 파일이 불일치할 위험이 있다.
@Service
@RequiredArgsConstructor
public class EpisodeImageService {

    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;
    private static final int MAX_FILE_COUNT = 300;

    private final EpisodeRepository episodeRepository;
    private final EpisodeImageRepository episodeImageRepository;
    private final EpisodeImageStorage episodeImageStorage;
    private final TransactionTemplate transactionTemplate;

    public List<EpisodeImageResponse> upload(Long webtoonId, Long episodeId, List<MultipartFile> files) {
        Episode episode = episodeRepository.findByIdAndWebtoonId(episodeId, webtoonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회차입니다."));

        if (episode.getStatus() != EpisodeStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "DRAFT 상태의 회차만 이미지를 업로드할 수 있습니다.");
        }

        validateFiles(files);

        List<EpisodeImage> oldImages = episodeImageRepository.findByEpisodeId(episodeId);
        Path episodeDir = episodeImageStorage.resolveEpisodeDir(webtoonId, episodeId);
        String relativeDir = episodeImageStorage.relativeEpisodeDir(webtoonId, episodeId);

        List<PendingImage> pending = writeTempFiles(episodeDir, relativeDir, files);

        try {
            List<EpisodeImage> saved = transactionTemplate.execute(status -> {
                episodeImageRepository.deleteByEpisodeId(episodeId);
                Episode episodeRef = episodeRepository.getReferenceById(episodeId);
                List<EpisodeImage> newImages = pending.stream()
                        .map(p -> EpisodeImage.builder()
                                .episode(episodeRef)
                                .order(p.order())
                                .path(p.finalRelativePath())
                                .build())
                        .toList();
                return episodeImageRepository.saveAll(newImages);
            });

            finalizeFiles(episodeDir, oldImages, pending);

            return saved.stream().map(EpisodeImageResponse::from).toList();
        } catch (RuntimeException e) {
            cleanupTempFiles(pending);
            throw e;
        }
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 이미지가 없습니다.");
        }
        if (files.size() > MAX_FILE_COUNT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "회차당 이미지는 최대 " + MAX_FILE_COUNT + "장까지 업로드할 수 있습니다.");
        }
        for (MultipartFile file : files) {
            if (!EXTENSION_BY_CONTENT_TYPE.containsKey(file.getContentType())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다: " + file.getContentType());
            }
            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일은 장당 10MB를 초과할 수 없습니다.");
            }
        }
    }

    // 새 파일들을 "{순서}.{확장자}.tmp" 임시 이름으로 저장한다. 기존 파일은 전혀 건드리지 않는다.
    private List<PendingImage> writeTempFiles(Path episodeDir, String relativeDir, List<MultipartFile> files) {
        List<Path> written = new ArrayList<>();
        List<PendingImage> pending = new ArrayList<>();
        try {
            Files.createDirectories(episodeDir);
            int order = 1;
            for (MultipartFile file : files) {
                String ext = EXTENSION_BY_CONTENT_TYPE.get(file.getContentType());
                String finalName = order + "." + ext;
                Path tempPath = episodeDir.resolve(finalName + ".tmp");
                file.transferTo(tempPath);
                written.add(tempPath);
                pending.add(new PendingImage(order, tempPath, finalName, relativeDir + "/" + finalName));
                order++;
            }
            return pending;
        } catch (IOException e) {
            written.forEach(this::deleteQuietly);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다.", e);
        }
    }

    // DB 교체가 끝난 뒤에만 호출됨: 기존 파일 삭제 + 임시 파일을 최종 이름으로 교체
    private void finalizeFiles(Path episodeDir, List<EpisodeImage> oldImages, List<PendingImage> pending) {
        oldImages.forEach(old -> deleteQuietly(episodeImageStorage.resolve(old.getPath())));

        for (PendingImage p : pending) {
            try {
                Files.move(p.tempPath(), episodeDir.resolve(p.finalName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 교체에 실패했습니다.", e);
            }
        }
    }

    private void cleanupTempFiles(List<PendingImage> pending) {
        pending.forEach(p -> deleteQuietly(p.tempPath()));
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    private record PendingImage(int order, Path tempPath, String finalName, String finalRelativePath) {
    }
}
