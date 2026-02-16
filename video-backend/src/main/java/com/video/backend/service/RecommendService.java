package com.video.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.backend.entity.Recommendation;
import com.video.backend.entity.Video;

import java.util.List;

public interface RecommendService {
    Page<Recommendation> getRecommendations(Long userId, Integer page, Integer size);
    List<Video> getHotVideosFromRedis();
}
