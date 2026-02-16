package com.video.backend.service.impl;

import com.video.backend.dto.BehaviorDTO;
import com.video.backend.entity.UserAction;
import com.video.backend.kafka.BehaviorProducer;
import com.video.backend.mapper.UserActionMapper;
import com.video.backend.service.BehaviorService;
import com.video.backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BehaviorServiceImpl implements BehaviorService {

    private final UserActionMapper userActionMapper;
    private final BehaviorProducer behaviorProducer;
    private final VideoService videoService;

    @Override
    public void recordBehavior(BehaviorDTO dto) {
        // 1. 保存到 MySQL
        UserAction action = new UserAction();
        action.setUserId(dto.getUserId());
        action.setVideoId(dto.getVideoId());
        action.setActionType(dto.getActionType());
        action.setActionValue(dto.getActionValue() != null ? dto.getActionValue() : BigDecimal.ONE);
        action.setDuration(dto.getDuration());
        action.setProgress(dto.getProgress());
        action.setCommentContent(dto.getCommentContent());
        action.setSharePlatform(dto.getSharePlatform());
        action.setDeviceType(dto.getDeviceType());
        action.setIpAddress(dto.getIpAddress());
        action.setUserAgent(dto.getUserAgent());
        action.setActionTime(LocalDateTime.now());
        userActionMapper.insert(action);

        // 2. 更新视频统计
        switch (dto.getActionType()) {
            case 2: // 点赞
                videoService.incrementLikeCount(dto.getVideoId());
                break;
            case 4: // 分享
                videoService.incrementShareCount(dto.getVideoId());
                break;
            case 5: // 评论
                videoService.incrementCommentCount(dto.getVideoId());
                break;
            case 6: // 收藏
                videoService.incrementCollectCount(dto.getVideoId());
                break;
        }

        // 3. 发送到 Kafka
        behaviorProducer.sendBehavior(action);
    }
}
