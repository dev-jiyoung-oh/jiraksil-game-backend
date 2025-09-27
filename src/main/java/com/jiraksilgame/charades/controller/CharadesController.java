package com.jiraksilgame.charades.controller;

import com.jiraksilgame.charades.dto.*;
import com.jiraksilgame.charades.service.CharadesService;
import com.jiraksilgame.common.validation.GameCode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// 몸으로 말해요
@Validated
@RestController
@RequestMapping("/api/charades")
@RequiredArgsConstructor
public class CharadesController {

    private final CharadesService service;
    

    // 게임 생성
    @PostMapping
    public ResponseEntity<CreateGameResponse> createGame(@Valid @RequestBody CreateGameRequest req) {
        CreateGameResponse res = service.createGame(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(res.getCode())
                .toUri();
        return ResponseEntity.created(location).body(res);
    }

    // 스냅샷
    @GetMapping("/{gameCode}")
    public GameSnapshotResponse getSnapshot(@PathVariable @GameCode String gameCode) {
        return service.getSnapshotByCode(gameCode);
    }

    // 단어 배치(조회)
    @GetMapping("/{gameCode}/words")
    public WordBatchResponse getWords(
            @PathVariable @GameCode String gameCode,
            @RequestParam(defaultValue = "300") @Min(1) @Max(1000) int limit,
            @RequestParam(required = false) List<Long> exclude // ?exclude=1&exclude=2...
    ) {
        return service.getWordsByCode(gameCode, exclude, limit);
    }

    // 턴 종료(저장)
    @PostMapping("/{gameCode}/turns/finalize")
    public GameSnapshotResponse finalizeTurn(@PathVariable @GameCode String gameCode, @Valid @RequestBody FinalizeTurnRequest req) {
        return service.finalizeTurnByCode(gameCode, req);
    }

    // 결과 조회
    @GetMapping("/{gameCode}/result")
    public GameResultResponse getResult(@PathVariable @GameCode String gameCode) {
        return service.getResultByCode(gameCode);
    }
}
