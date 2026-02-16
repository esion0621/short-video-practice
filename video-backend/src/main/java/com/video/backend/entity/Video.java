package com.video.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("videos")
public class Video {
    @TableId(type = IdType.AUTO)
    private Long videoId;
    private String videoTitle;
    private String videoDesc;
    private String videoUrl;
    private String coverUrl;
    private Long userId;
    private Integer categoryId;
    private Integer duration;
    private Integer width;
    private Integer height;
    private String format;
    private Long fileSize;
    private Long viewCount;
    private Long likeCount;
    private Long shareCount;
    private Long commentCount;
    private Long collectCount;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
