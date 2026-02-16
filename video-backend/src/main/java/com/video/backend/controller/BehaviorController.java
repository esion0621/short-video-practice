package com.video.backend.controller;

import com.video.backend.dto.BehaviorDTO;
import com.video.backend.service.BehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/behaviors")
@RequiredArgsConstructor
public class BehaviorController {

    private final BehaviorService behaviorService;

    @PostMapping
    public ResponseEntity<Void> recordBehavior(@RequestBody BehaviorDTO dto) {
        behaviorService.recordBehavior(dto);
        return ResponseEntity.ok().build();
    }
}
