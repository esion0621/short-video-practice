package com.video.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_actions")
public class UserAction {
    @TableId(type = IdType.AUTO)
    private Long actionId;
    private Long userId;
    private Long videoId;
    private Integer actionType;
    private BigDecimal actionValue;
    private Integer duration;
    private Float progress;
    private String commentContent;
    private String sharePlatform;
    private String deviceType;
    private String ipAddress;
    private String userAgent;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime actionTime;
}
