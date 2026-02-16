package com.video.backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

public interface HdfsService {
    // 上传 MultipartFile 文件
    void uploadFile(MultipartFile file, String hdfsPath) throws IOException;
    
    // 上传 InputStream 数据流（新增）
    void uploadFile(InputStream inputStream, String hdfsPath) throws IOException;
    
    byte[] downloadFile(String hdfsPath) throws IOException;
    void deleteFile(String hdfsPath) throws IOException;
}
