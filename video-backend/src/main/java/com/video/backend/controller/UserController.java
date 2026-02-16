package com.video.backend.controller;

import com.video.backend.dto.UserLoginDTO;
import com.video.backend.dto.UserRegisterDTO;
import com.video.backend.entity.User;
import com.video.backend.entity.Video;
import com.video.backend.entity.UserAction;
import com.video.backend.mapper.UserMapper;
import com.video.backend.mapper.VideoMapper;
import com.video.backend.mapper.UserActionMapper;
import com.video.backend.service.HdfsService;
import com.video.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final VideoMapper videoMapper;
    private final UserActionMapper userActionMapper;
    private final HdfsService hdfsService;  // 新增 HdfsService 用于头像上传

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO dto) {
        String token = userService.register(dto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDTO dto) {
        String token = userService.login(dto);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 头像上传接口
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> uploadAvatar(
            @RequestParam("userId") Long userId,
            @RequestPart("file") MultipartFile file) {
        try {
            // 生成HDFS存储路径
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String hdfsPath = "/video-analysis/avatars/" + fileName;

            // 上传文件到HDFS
            hdfsService.uploadFile(file.getInputStream(), hdfsPath);

            // 更新用户表的 avatar_url
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            user.setAvatarUrl(hdfsPath);
            userMapper.updateById(user);

            return ResponseEntity.ok(user);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 获取用户上传的视频
    @GetMapping("/{userId}/videos")
    public ResponseEntity<List<Video>> getUserVideos(@PathVariable Long userId) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getUserId, userId)
               .orderByDesc(Video::getCreateTime);
        return ResponseEntity.ok(videoMapper.selectList(wrapper));
    }

    // 获取用户点赞的视频
    @GetMapping("/{userId}/likes")
    public ResponseEntity<List<Video>> getUserLikedVideos(@PathVariable Long userId) {
        List<Long> videoIds = userActionMapper.selectList(
            new LambdaQueryWrapper<UserAction>()
                .eq(UserAction::getUserId, userId)
                .eq(UserAction::getActionType, 2)
                .select(UserAction::getVideoId)
        ).stream().map(UserAction::getVideoId).distinct().collect(Collectors.toList());

        if (videoIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Video::getVideoId, videoIds)
               .orderByDesc(Video::getCreateTime);
        return ResponseEntity.ok(videoMapper.selectList(wrapper));
    }

    // 获取用户收藏的视频
    @GetMapping("/{userId}/collects")
    public ResponseEntity<List<Video>> getUserCollectedVideos(@PathVariable Long userId) {
        List<Long> videoIds = userActionMapper.selectList(
            new LambdaQueryWrapper<UserAction>()
                .eq(UserAction::getUserId, userId)
                .eq(UserAction::getActionType, 6)
                .select(UserAction::getVideoId)
        ).stream().map(UserAction::getVideoId).distinct().collect(Collectors.toList());

        if (videoIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Video::getVideoId, videoIds)
               .orderByDesc(Video::getCreateTime);
        return ResponseEntity.ok(videoMapper.selectList(wrapper));
    }
}
