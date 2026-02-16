package com.video.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BehaviorDTO {
    private Long userId;
    private Long videoId;
    private Integer actionType;  // 1-观看,2-点赞,3-取消点赞,4-分享,5-评论,6-收藏,7-取消收藏
    private BigDecimal actionValue;
    private Integer duration;    // 观看时长(秒)
    private Float progress;      // 观看进度
    private String commentContent;
    private String sharePlatform;
    private String deviceType;
    private String ipAddress;
    private String userAgent;
    public BehaviorDTO() {}
}
