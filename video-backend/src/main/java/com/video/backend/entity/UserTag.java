package com.video.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_tags")
public class UserTag {
    @TableId(type = IdType.AUTO)
    private Long tagId;
    private Long userId;
    private String tagType;
    private String tagName;
    private String tagValue;
    private BigDecimal weight;
    private String source;
    private LocalDateTime validUntil;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
