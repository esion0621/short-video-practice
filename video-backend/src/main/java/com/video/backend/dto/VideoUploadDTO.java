package com.video.backend.dto;

import lombok.Data;

@Data
public class VideoUploadDTO {
    private String title;
    private String desc;
    private Long userId;
    private Integer categoryId;
    private Integer duration;
    private String format;
    private Integer coverTime;
}
