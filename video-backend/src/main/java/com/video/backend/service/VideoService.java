package com.video.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.backend.dto.VideoUploadDTO;
import com.video.backend.entity.Video;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    Video uploadVideo(VideoUploadDTO dto, MultipartFile file, MultipartFile cover);
    Page<Video> getVideoList(Integer page, Integer size, Integer categoryId);
    Video getVideoDetail(Long videoId);
    void incrementViewCount(Long videoId);
    void incrementLikeCount(Long videoId);
    void incrementShareCount(Long videoId);
    void incrementCommentCount(Long videoId);
    void incrementCollectCount(Long videoId);
}
