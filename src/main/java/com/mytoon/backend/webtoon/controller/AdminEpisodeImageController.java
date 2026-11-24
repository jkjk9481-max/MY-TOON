package com.mytoon.backend.webtoon.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mytoon.backend.webtoon.controller.dto.EpisodeImageResponse;
import com.mytoon.backend.webtoon.service.EpisodeImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/webtoons/{webtoonId}/episodes/{episodeId}/images")
@RequiredArgsConstructor
public class AdminEpisodeImageController {

    private final EpisodeImageService episodeImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<EpisodeImageResponse> upload(
            @PathVariable Long webtoonId,
            @PathVariable Long episodeId,
            @RequestParam("files") List<MultipartFile> files) {
        return episodeImageService.upload(webtoonId, episodeId, files);
    }
}
