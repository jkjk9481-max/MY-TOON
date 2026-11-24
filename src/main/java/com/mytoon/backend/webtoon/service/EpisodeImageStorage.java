package com.mytoon.backend.webtoon.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 회차 이미지 파일의 저장 경로 계산 + 디렉토리 삭제만 담당한다.
// EpisodeImageService(업로드)와 EpisodeService(회차 삭제 시 파일 정리)가 함께 사용한다.
@Component
public class EpisodeImageStorage {

    private final Path rootDir;

    public EpisodeImageStorage(@Value("${app.upload.root-dir}") String rootDir) {
        this.rootDir = Paths.get(rootDir);
    }

    public String relativeEpisodeDir(Long webtoonId, Long episodeId) {
        return "webtoons/" + webtoonId + "/episodes/" + episodeId;
    }

    public Path resolveEpisodeDir(Long webtoonId, Long episodeId) {
        return rootDir.resolve(relativeEpisodeDir(webtoonId, episodeId));
    }

    public Path resolve(String relativePath) {
        return rootDir.resolve(relativePath);
    }

    // 파일 정리는 best-effort: 실패해도 예외를 던지지 않는다 (호출하는 쪽의 핵심 동작을 막지 않기 위해)
    public void deleteEpisodeDir(Long webtoonId, Long episodeId) {
        Path dir = resolveEpisodeDir(webtoonId, episodeId);
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }
}
