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

import java.util.List;

/**
 * 몸으로 말해요 컨트롤러
 */
@Validated
@RestController
@RequestMapping("/api/charades")
@RequiredArgsConstructor
public class CharadesController {

    private final CharadesService service;

    /**
     * 게임 생성
     *
     * @param req 게임 생성 요청
     * @return 생성된 게임 정보
     */
    @PostMapping
    public ResponseEntity<CreateGameResponse> createGame(@Valid @RequestBody CreateGameRequest req) {
        return ResponseEntity.ok(service.createGame(req));
    }

    /**
     * 게임 스냅샷 조회
     *
     * @param gameCode 게임 코드
     * @return 게임의 현재 상태
     */
    @GetMapping("/{gameCode}")
    public ResponseEntity<GameSnapshotResponse> getSnapshot(@PathVariable @GameCode String gameCode) {
        return ResponseEntity.ok(service.getSnapshotByCode(gameCode));
    }

    /**
     * 단어 배치(조회)
     *
     * @param gameCode 게임 코드
     * @param limit 반환할 단어 수 (기본 300, 최대 1000)
     * @param exclude 제외할 단어 ID 목록
     * @return 단어 리스트
     */
    @GetMapping("/{gameCode}/words")
    public ResponseEntity<WordBatchResponse> getWords(
            @PathVariable @GameCode String gameCode,
            @RequestParam(defaultValue = "300") @Min(1) @Max(1000) int limit,
            @RequestParam(required = false) List<Long> exclude
    ) {
        return ResponseEntity.ok(service.getWordsByCode(gameCode, exclude, limit));
    }

    /**
     * 턴 종료 처리
     *
     * @param gameCode 게임 코드
     * @param req 턴 종료 요청 정보
     * @return 최신 스냅샷 정보
     */
    @PostMapping("/{gameCode}/turns/finalize")
    public ResponseEntity<GameSnapshotResponse> finalizeTurn(
            @PathVariable @GameCode String gameCode,
            @Valid @RequestBody FinalizeTurnRequest req
    ) {
        return ResponseEntity.ok(service.finalizeTurnByCode(gameCode, req));
    }

    /**
     * 결과 조회
     *
     * @param gameCode 게임 코드
     * @return 최종 결과
     */
    @GetMapping("/{gameCode}/result")
    public ResponseEntity<GameResultResponse> getResult(@PathVariable @GameCode String gameCode) {
        return ResponseEntity.ok(service.getResultByCode(gameCode));
    }
}
