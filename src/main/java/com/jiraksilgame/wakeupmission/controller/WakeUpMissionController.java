package com.jiraksilgame.wakeupmission.controller;

import com.jiraksilgame.wakeupmission.dto.CreateWakeUpMissionRequest;
import com.jiraksilgame.wakeupmission.dto.PasswordRequest;
import com.jiraksilgame.wakeupmission.dto.WakeUpMissionResponse;
import com.jiraksilgame.wakeupmission.service.WakeUpMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 기상 미션 게임
@RestController
@RequestMapping("/api/wake-up-mission")
@RequiredArgsConstructor
public class WakeUpMissionController {

    private final WakeUpMissionService wakeUpMissionService;

    // 게임 생성
    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody CreateWakeUpMissionRequest request) {
        return ResponseEntity.ok(wakeUpMissionService.createGame(request));
    }

    // 게임 조회
    @PostMapping("/{gameCode}")
    public ResponseEntity<WakeUpMissionResponse> getGameByIdWithPassword(
            @PathVariable String gameCode,
            @RequestBody PasswordRequest request
    ) {
        return ResponseEntity.ok(wakeUpMissionService.getGameByCodeWithPassword(gameCode, request.getPassword()));
    }
}
