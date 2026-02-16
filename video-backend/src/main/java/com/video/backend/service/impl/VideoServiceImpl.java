package com.video.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.backend.dto.VideoUploadDTO;
import com.video.backend.entity.Video;
import com.video.backend.mapper.VideoMapper;
import com.video.backend.service.HdfsService;
import com.video.backend.service.VideoService;
import com.video.backend.util.FfmpegUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoMapper videoMapper;
    private final HdfsService hdfsService;
    private final FfmpegUtil ffmpegUtil;

    @Value("${video.hdfs.upload-dir}")
    private String hdfsUploadDir;

    @Override
    public Video uploadVideo(VideoUploadDTO dto, MultipartFile file, MultipartFile cover) {
        try {
            // 1. 上传视频文件到 HDFS
            String videoPath = hdfsUploadDir + "video/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            hdfsService.uploadFile(file.getInputStream(), videoPath);

            // 2. 处理封面
            String coverPath;
            if (cover != null && !cover.isEmpty()) {
                // 用户上传了封面，直接上传
                coverPath = hdfsUploadDir + "cover/" + System.currentTimeMillis() + "_" + cover.getOriginalFilename();
                hdfsService.uploadFile(cover.getInputStream(), coverPath);
            } else {
                // 用户未上传封面，从视频中截取
                log.info("用户未上传封面，自动从视频中截取");
                File capturedCover = null;
                try {
                    // 截取第3秒的帧（可配置）
                    capturedCover = ffmpegUtil.extractFrame(file, dto.getCoverTime() != null ? dto.getCoverTime() : 3);
                    // 上传截取的封面到 HDFS
                    coverPath = hdfsUploadDir + "cover/" + System.currentTimeMillis() + "_captured.jpg";
                    try (FileInputStream fis = new FileInputStream(capturedCover)) {
                        hdfsService.uploadFile(fis, coverPath);
                    }
                } finally {
                    // 清理临时封面文件
                    if (capturedCover != null && capturedCover.exists()) {
                        Files.deleteIfExists(capturedCover.toPath());
                    }
                }
            }

            // 3. 保存视频信息到数据库
            Video video = new Video();
            video.setVideoTitle(dto.getTitle());
            video.setVideoDesc(dto.getDesc());
            video.setVideoUrl(videoPath);
            video.setCoverUrl(coverPath);
            video.setUserId(dto.getUserId());
            video.setCategoryId(dto.getCategoryId());
            video.setDuration(dto.getDuration());
            video.setFormat(dto.getFormat());
            video.setFileSize(file.getSize());
            video.setStatus(1);
            video.setCreateTime(LocalDateTime.now());
            video.setViewCount(0L);
            video.setLikeCount(0L);
            video.setShareCount(0L);
            video.setCommentCount(0L);
            video.setCollectCount(0L);

            videoMapper.insert(video);
            return video;

        } catch (Exception e) {
            log.error("视频上传失败", e);
            throw new RuntimeException("视频上传失败: " + e.getMessage());
        }
    }

    @Override
    public Page<Video> getVideoList(Integer page, Integer size, Integer categoryId) {
        Page<Video> p = new Page<>(page, size);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Video::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Video::getCreateTime);
        return videoMapper.selectPage(p, wrapper);
    }

    @Override
    public Video getVideoDetail(Long videoId) {
        return videoMapper.selectById(videoId);
    }

    @Override
    public void incrementViewCount(Long videoId) {
        videoMapper.incrementViewCount(videoId);
    }
    @Override
    public void incrementLikeCount(Long videoId) {
        videoMapper.incrementLikeCount(videoId);
    }

    @Override
    public void incrementShareCount(Long videoId) {
        videoMapper.incrementShareCount(videoId);
    }

    @Override
    public void incrementCommentCount(Long videoId) {
        videoMapper.incrementCommentCount(videoId);
    }

    @Override
    public void incrementCollectCount(Long videoId) {
        videoMapper.incrementCollectCount(videoId);
    }
}
