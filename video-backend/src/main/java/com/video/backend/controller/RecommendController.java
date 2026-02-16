package com.video.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.backend.entity.Recommendation;
import com.video.backend.entity.Video;
import com.video.backend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommends")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/{userId}")
    public ResponseEntity<Page<Recommendation>> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(recommendService.getRecommendations(userId, page, size));
    }

    @GetMapping("/hot")
    public ResponseEntity<List<Video>> getHotVideos() {
        return ResponseEntity.ok(recommendService.getHotVideosFromRedis());
    }
}
