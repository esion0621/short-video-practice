package com.video.backend.service;

import com.video.backend.dto.BehaviorDTO;
import com.video.backend.entity.UserAction;

public interface BehaviorService {
    void recordBehavior(BehaviorDTO dto);
}
