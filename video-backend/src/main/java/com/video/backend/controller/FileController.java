package com.video.backend.controller;

import com.video.backend.service.HdfsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final HdfsService hdfsService;

    /**
     * 获取文件（视频或封面）
     * @param path HDFS 文件路径（如 /video-analysis/videos/video/xxx.mp4）
     * @return 文件字节流
     */
    @GetMapping
    public ResponseEntity<byte[]> getFile(@RequestParam String path) {
        try {
            byte[] data = hdfsService.downloadFile(path);
            // 根据文件扩展名设置 Content-Type
            String contentType = getContentType(path);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);
        } catch (IOException e) {
            log.error("文件读取失败: {}", path, e);
            return ResponseEntity.notFound().build();
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".mp4")) {
            return "video/mp4";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream";
        }
    }
}
