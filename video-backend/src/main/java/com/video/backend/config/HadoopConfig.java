package com.video.backend.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;

@org.springframework.context.annotation.Configuration
public class HadoopConfig {

    @Value("${hadoop.fs.defaultFS}")
    private String defaultFS;

    @Value("${hadoop.fs.user}")
    private String user;

    @Bean
    public FileSystem hdfsFileSystem() throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", defaultFS);
        return FileSystem.get(URI.create(defaultFS), conf, user);
    }
}
