package com.jiraksilgame.wakeupmission.controller;

import com.jiraksilgame.common.validation.GameCode;
import com.jiraksilgame.wakeupmission.dto.CreateWakeUpMissionRequest;
import com.jiraksilgame.wakeupmission.dto.PasswordRequest;
import com.jiraksilgame.wakeupmission.dto.WakeUpMissionResponse;
import com.jiraksilgame.wakeupmission.service.WakeUpMissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// 기상 미션 게임
@Validated
@RestController
@RequestMapping("/api/wake-up-mission")
@RequiredArgsConstructor
public class WakeUpMissionController {

    private final WakeUpMissionService wakeUpMissionService;

    // 게임 생성
    @PostMapping
    public ResponseEntity<?> createGame(@Valid @RequestBody CreateWakeUpMissionRequest req) {
        return ResponseEntity.ok(wakeUpMissionService.createGame(req));
    }

    // 게임 조회
    @PostMapping("/{gameCode}")
    public ResponseEntity<WakeUpMissionResponse> getGameByIdWithPassword(
            @PathVariable @GameCode String gameCode,
            @Valid @RequestBody PasswordRequest req
    ) {
        return ResponseEntity.ok(wakeUpMissionService.getGameByCodeWithPassword(gameCode, req.getPassword()));
    }
}
