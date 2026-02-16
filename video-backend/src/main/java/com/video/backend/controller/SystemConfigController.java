package com.video.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.video.backend.entity.SystemConfig;
import com.video.backend.mapper.SystemConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class SystemConfigController {


    private final SystemConfigMapper systemConfigMapper;

    @GetMapping("/{key}")
    public ResponseEntity<SystemConfig> getConfig(@PathVariable String key) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        return ResponseEntity.ok(systemConfigMapper.selectOne(wrapper));
    }

    @GetMapping("/group/{group}")
    public ResponseEntity<List<SystemConfig>> getConfigsByGroup(@PathVariable String group) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigGroup, group);
        return ResponseEntity.ok(systemConfigMapper.selectList(wrapper));
    }
}
