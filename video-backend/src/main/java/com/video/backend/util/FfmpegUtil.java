package com.video.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * FFmpeg 视频截帧工具类
 */
@Slf4j
@Component
public class FfmpegUtil {

    @Value("${ffmpeg.path:ffmpeg}")  // 可配置 ffmpeg 路径，默认为系统环境变量中的 ffmpeg
    private String ffmpegPath;

    /**
     * 从视频中截取指定时间点的帧作为封面
     * @param videoFile 上传的视频文件
     * @param timeSeconds 截取时间点（秒），若为null则默认第3秒
     * @return 生成的封面图片文件（临时文件，调用方需负责清理）
     */
    public File extractFrame(MultipartFile videoFile, Integer timeSeconds) throws IOException, InterruptedException {
        // 创建临时目录（用于存放临时视频和生成的封面）
        String tempDir = System.getProperty("java.io.tmpdir") + "/video-covers/";
        Files.createDirectories(Paths.get(tempDir));

        // 生成唯一文件名
        String originalFilename = videoFile.getOriginalFilename();
        String videoFileName = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "video.mp4");
        String coverFileName = UUID.randomUUID() + ".jpg";

        Path videoPath = Paths.get(tempDir + videoFileName);
        Path coverPath = Paths.get(tempDir + coverFileName);

        try {
            // 将上传的视频保存到临时文件
            Files.write(videoPath, videoFile.getBytes());

            // 默认截取第3秒
            int captureTime = (timeSeconds != null && timeSeconds > 0) ? timeSeconds : 3;

            // 构建 ffmpeg 命令
            CommandLine cmdLine = new CommandLine(ffmpegPath);
            cmdLine.addArgument("-i");
            cmdLine.addArgument(videoPath.toString());
            cmdLine.addArgument("-ss");
            cmdLine.addArgument(String.valueOf(captureTime));
            cmdLine.addArgument("-vframes");
            cmdLine.addArgument("1");
            cmdLine.addArgument("-q:v");
            cmdLine.addArgument("2");      // 图片质量（1-31，数字越小质量越高）
            cmdLine.addArgument("-y");      // 覆盖已存在的文件
            cmdLine.addArgument(coverPath.toString());

            // 执行命令并捕获输出
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(streamHandler);

            int exitCode = executor.execute(cmdLine);
            if (exitCode != 0) {
                String errorMsg = errorStream.toString();
                log.error("FFmpeg 执行失败，退出码: {}, 错误信息: {}", exitCode, errorMsg);
                throw new IOException("视频截帧失败，请检查视频格式或 ffmpeg 安装");
            }

            log.info("视频截帧成功，封面临时文件: {}", coverPath);
            return coverPath.toFile();

        } finally {
            // 清理临时视频文件（封面文件返回给调用方，由调用方决定何时删除）
            Files.deleteIfExists(videoPath);
        }
    }
}
