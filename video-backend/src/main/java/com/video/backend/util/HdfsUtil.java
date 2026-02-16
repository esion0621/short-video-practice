package com.video.backend.util;

import lombok.RequiredArgsConstructor;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class HdfsUtil {

    private final FileSystem fileSystem;

    public void uploadFile(InputStream in, String hdfsPath) throws IOException {
        Path path = new Path(hdfsPath);
        if (fileSystem.exists(path)) {
            fileSystem.delete(path, true);
        }
        try (FSDataOutputStream out = fileSystem.create(path)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }

    public byte[] downloadFile(String hdfsPath) throws IOException {
        Path path = new Path(hdfsPath);
        try (FSDataInputStream in = fileSystem.open(path)) {
            return in.readAllBytes();
        }
    }

    public void deleteFile(String hdfsPath) throws IOException {
        Path path = new Path(hdfsPath);
        fileSystem.delete(path, true);
    }
}
