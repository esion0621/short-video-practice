package com.video.backend.service.impl;

import com.video.backend.service.HdfsService;
import com.video.backend.util.HdfsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class HdfsServiceImpl implements HdfsService {

    private final HdfsUtil hdfsUtil;

    @Override
    public void uploadFile(MultipartFile file, String hdfsPath) throws IOException {
        try (InputStream in = file.getInputStream()) {
            hdfsUtil.uploadFile(in, hdfsPath);
        }
    }

    @Override
    public void uploadFile(InputStream inputStream, String hdfsPath) throws IOException {
        hdfsUtil.uploadFile(inputStream, hdfsPath);
    }

    @Override
    public byte[] downloadFile(String hdfsPath) throws IOException {
        return hdfsUtil.downloadFile(hdfsPath);
    }

    @Override
    public void deleteFile(String hdfsPath) throws IOException {
        hdfsUtil.deleteFile(hdfsPath);
    }
}
