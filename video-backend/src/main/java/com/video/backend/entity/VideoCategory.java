package com.video.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("video_categories")
public class VideoCategory {
    @TableId(type = IdType.AUTO)
    private Integer categoryId;
    private String categoryName;
    private Integer parentId;
    private Integer categoryLevel;
    private Integer categoryOrder;
    private Integer status;
    private LocalDateTime createTime;
}
