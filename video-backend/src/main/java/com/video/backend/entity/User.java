package com.video.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer gender;
    private LocalDateTime birthday;
    private String city;
    private String signature;
    private Integer status;
    private Integer userLevel;
    private Integer totalWatchTime;
    private LocalDateTime lastLoginTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime registerTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
