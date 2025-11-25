package com.jiraksilgame.charades.controller;

import com.jiraksilgame.charades.dto.*;
import com.jiraksilgame.charades.entity.CharadesCategory;
import com.jiraksilgame.charades.service.CharadesService;
import com.jiraksilgame.common.dto.PasswordRequest;
import com.jiraksilgame.common.validation.GameCode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
     * 카테고리 목록 조회
     * @return 카테고리 리스트
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<CharadesCategory> categories = service.getActiveCategories();
        List<CategoryDto> response = categories.stream()
            .map(c -> new CategoryDto(c.getCode(), c.getName()))
            .toList();
        return ResponseEntity.ok(response);
    }
    
    /**
     * 게임 생성
     *
     * @param req 게임 생성 요청
     * @return 생성된 게임 정보
     */
    @PostMapping
    public ResponseEntity<GameInfoResponse> createGame(@Valid @RequestBody CreateGameRequest req) {
        return ResponseEntity.ok(service.createGame(req));
    }

    /**
     * 게임 정보 상세 조회
     *
     * @param gameCode 게임 코드
     * @return 게임 상세 정보
     */
    @PostMapping("/{gameCode}")
    public ResponseEntity<GameInfoResponse> getGameDetailWithPassword(
            @PathVariable @GameCode String gameCode,
            @Valid @RequestBody PasswordRequest req
    ) {
        return ResponseEntity.ok(service.getGameDetailByCodeWithPassword(gameCode, req.getPassword()));
    }

    /**
     * 랜덤 단어 배치 조회
     *
     * @param gameCode 게임 코드
     * @param limit 반환할 단어 수 (기본 300, 최대 1000)
     * @param exclude 제외할 단어 ID 목록 (선택)
     * @return 랜덤 단어 리스트
     */
    @GetMapping("/{gameCode}/word-batch")
    public ResponseEntity<WordBatchResponse> getWordBatch(
            @PathVariable @GameCode String gameCode,
            @RequestParam(defaultValue = "300") @Min(1) @Max(1000) int limit,
            @RequestParam(required = false) List<Long> exclude
    ) {
        return ResponseEntity.ok(service.getWordBatchByCode(gameCode, exclude, limit));
    }

    /**
     * 게임 종료 후 전체 플레이 일괄 저장
     *
     * @param gameCode 게임 코드
     * @param req 전체 턴 및 단어 기록을 담은 요청 정보
     */
    @PostMapping("/{gameCode}/finalize")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void finalizeGame(
            @PathVariable @GameCode String gameCode,
            @Valid @RequestBody FinalizeGameRequest req
    ) {
        service.finalizeGameByCode(gameCode, req);
    }

    /**
     * 결과 조회
     *
     * @param gameCode 게임 코드
     * @return 최종 결과
     */
    @GetMapping("/{gameCode}/result")
    public ResponseEntity<GameResultResponse> getGameResult(@PathVariable @GameCode String gameCode) {
        return ResponseEntity.ok(service.getGameResultByCode(gameCode));
    }
}
