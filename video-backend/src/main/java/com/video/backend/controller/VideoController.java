package com.video.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.backend.dto.VideoUploadDTO;
import com.video.backend.entity.Video;
import com.video.backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<Video> uploadVideo(
            @RequestPart("video") MultipartFile videoFile,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile,
            @RequestPart("data") VideoUploadDTO dto) {
        Video video = videoService.uploadVideo(dto, videoFile, coverFile);
        return ResponseEntity.ok(video);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Video>> listVideos(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer categoryId) {
        return ResponseEntity.ok(videoService.getVideoList(page, size, categoryId));
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<Video> getVideo(@PathVariable Long videoId) {
        Video video = videoService.getVideoDetail(videoId);
        // 异步增加观看次数（可放在AOP或单独调用）
        videoService.incrementViewCount(videoId);
        return ResponseEntity.ok(video);
    }
}
