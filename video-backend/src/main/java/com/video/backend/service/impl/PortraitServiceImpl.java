package com.video.backend.service.impl;

import com.video.backend.service.PortraitService;
import com.video.backend.util.HBaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortraitServiceImpl implements PortraitService {

    private final HBaseUtil hBaseUtil;

    @Override
    public Map<String, String> getUserPortrait(Long userId) {
        String tableName = "user_portrait";
        String rowKey = String.valueOf(userId);
        Map<String, String> portrait = new HashMap<>();
        // 获取basic列族
        Map<String, String> basic = hBaseUtil.getRow(tableName, rowKey, "basic");
        if (basic != null) portrait.putAll(basic);
        // 获取preference列族
        Map<String, String> preference = hBaseUtil.getRow(tableName, rowKey, "preference");
        if (preference != null) portrait.putAll(preference);
        // 获取behavior列族
        Map<String, String> behavior = hBaseUtil.getRow(tableName, rowKey, "behavior");
        if (behavior != null) portrait.putAll(behavior);
        return portrait;
    }
}
