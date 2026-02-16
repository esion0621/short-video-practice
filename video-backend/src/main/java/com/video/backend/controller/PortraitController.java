package com.video.backend.controller;

import com.video.backend.service.PortraitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/portrait")
@RequiredArgsConstructor
public class PortraitController {

    private final PortraitService portraitService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, String>> getUserPortrait(@PathVariable Long userId) {
        return ResponseEntity.ok(portraitService.getUserPortrait(userId));
    }
}
