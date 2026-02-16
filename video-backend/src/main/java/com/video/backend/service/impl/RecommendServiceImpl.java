package com.video.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.backend.entity.Recommendation;
import com.video.backend.entity.Video;
import com.video.backend.mapper.RecommendationMapper;
import com.video.backend.mapper.VideoMapper;
import com.video.backend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {


    private final RecommendationMapper recommendationMapper;
    private final VideoMapper videoMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public Page<Recommendation> getRecommendations(Long userId, Integer page, Integer size) {
        Page<Recommendation> p = new Page<>(page, size);
        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getUserId, userId)
                .orderByDesc(Recommendation::getCreateTime);

        return recommendationMapper.selectPage(p, wrapper);
    }

    @Override
    public List<Video> getHotVideosFromRedis() {
        Set<ZSetOperations.TypedTuple<String>> hotSet = redisTemplate.opsForZSet()
                .reverseRangeWithScores("hot:videos:daily", 0, 9);
        List<Video> videos = new ArrayList<>();
        if (hotSet != null) {
            for (ZSetOperations.TypedTuple<String> tuple : hotSet) {
                String videoIdStr = tuple.getValue().replace("video:", "");
                Video video = videoMapper.selectById(Long.parseLong(videoIdStr));
                if (video != null) videos.add(video);
            }
        }
        return videos;
    }
}
