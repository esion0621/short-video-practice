package com.video.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recommendations")
public class Recommendation {
    @TableId(type = IdType.AUTO)
    private Long recommendId;
    private Long userId;
    private Long videoId;
    private String recommendType;
    private BigDecimal recommendScore;
    private Integer rankPosition;
    private String recommendReason;
    private LocalDateTime exposeTime;
    private LocalDateTime clickTime;
    private Integer isClick;
    private BigDecimal ctr;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
