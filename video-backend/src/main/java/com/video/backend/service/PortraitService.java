package com.video.backend.service;

import java.util.Map;

public interface PortraitService {
    Map<String, String> getUserPortrait(Long userId);
}
