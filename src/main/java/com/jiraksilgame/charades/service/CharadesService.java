package com.jiraksilgame.charades.service;

import com.jiraksilgame.charades.domain.TurnOutcome;
import com.jiraksilgame.charades.dto.*;
import com.jiraksilgame.charades.entity.*;
import com.jiraksilgame.charades.entity.enums.GameStatus;
import com.jiraksilgame.charades.repository.*;
import com.jiraksilgame.common.CommonConstants;
import com.jiraksilgame.common.error.AppException;
import com.jiraksilgame.common.error.ErrorUtil;
import com.jiraksilgame.common.util.CodeGenerator;
import com.jiraksilgame.charades.CharadesConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CharadesService {

    private final CharadesGameRepository gameRepo;
    private final CharadesTeamRepository teamRepo;
    private final CharadesCategoryRepository categoryRepo;
    private final CharadesGameCategoryRepository gameCategoryRepo;
    private final CharadesWordRepository wordRepo;
    private final CharadesTurnRepository turnRepo;
    private final CharadesTurnWordRepository turnWordRepo;


    // ========= 유틸(로컬 헬퍼) =========

    private static final class Pointer {
        final int teamIdx, roundIdx;
        private Pointer(int teamIdx, int roundIdx) { this.teamIdx = teamIdx; this.roundIdx = roundIdx; }
    }

    private static Pointer nextPointer(List<CharadesTeam> teams, Map<Long,Integer> done) {
        if (teams.isEmpty()) return new Pointer(0, 0);
        int teamIdx = 0, roundIdx = Integer.MAX_VALUE;
        for (int i = 0; i < teams.size(); i++) {
            int c = done.getOrDefault(teams.get(i).getId(), 0);
            if (c < roundIdx) { teamIdx = i; roundIdx = c; }
        }
        return new Pointer(teamIdx, roundIdx);
    }
    
    // ========= 내부 로직 =========
    
    // 사용 가능한 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<CharadesCategory> getActiveCategories() {
        return categoryRepo.findByIsActiveTrue();
    }

    // 게임 code로 조회
    private CharadesGame getGameByCodeOrThrow(String code) {
        return gameRepo.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found: " + code));
    }

    // 게임 생성(고유코드 생성)
    private CharadesGame createAndPersistGameWithUniqueCode(CreateGameRequest.GameOptions opt) {
        
        for (int attempt = 0; attempt < 3; attempt++) {
            String code = CodeGenerator.randomCode(CommonConstants.GAME_CODE_LENGTH);
            CharadesGame game = CharadesGame.create(
                    code,
                    opt.getMode(),
                    opt.getDurationSec(),
                    opt.getTargetCount(),
                    opt.getPassLimit(),
                    opt.getRoundsPerTeam()
            );

            try {
                return gameRepo.saveAndFlush(game); // 고유코드 충돌 시 즉시 감지
            } catch (DataIntegrityViolationException e) {
                // 관측성 확보용 로그 (원인이 중복이든 다른 무결성이든 재시도)
                log.warn("createGame failed (attempt {}): code={}, cause={}", attempt, code, ErrorUtil.rootMessage(e));

                if (attempt == 2) { // 3회 모두 실패 시 전파
                    throw AppException.internal("게임 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
                }
                // 다음 루프에서 새 코드로 재시도
            }
        }
        
        throw AppException.internal("게임 생성에 실패했습니다."); // 방어적 코드
    }

    // ========= API 로직 =========

    // 게임 생성
    public CreateGameResponse createGame(CreateGameRequest req) {
        CreateGameRequest.GameOptions opt = req.getOptions();

        // 1) 게임 생성
        CharadesGame game = createAndPersistGameWithUniqueCode(opt);

        // 2) 팀 생성 (없으면 1팀)
        List<String> names = (req.getTeamNames() == null || req.getTeamNames().isEmpty())
                ? java.util.Collections.nCopies(1, "")
                : req.getTeamNames();
        if (names.size() > CharadesConstants.MAX_TEAMS) {
            throw AppException.badRequest("Too many teams: max " + CharadesConstants.MAX_TEAMS + " (A~Z)");
        }
        List<CharadesTeam> teams = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            teams.add(CharadesTeam.create(game, i, names.get(i)));
        }
        teamRepo.saveAll(teams);

        // 3) 카테고리 매핑: 빈/ALL → 전체 활성화
        List<String> codes = opt.getCategoryCodes();
        List<Short> catIds;
        if (codes == null || codes.isEmpty() || codes.contains("ALL")) {
            catIds = categoryRepo.findByIsActiveTrue().stream().map(CharadesCategory::getId).toList();
        } else {
            catIds = categoryRepo.findActiveIdsByCodes(codes);
            if (catIds.isEmpty()) throw AppException.badRequest("Invalid category codes: " + codes);
        }
        List<CharadesGameCategory> links = catIds.stream()
                .distinct()
                .map(cid -> CharadesGameCategory.ofCategoryId(game, cid))
                .toList();
        gameCategoryRepo.saveAll(links);

        return new CreateGameResponse(game, teams);
    }

    // 스냅샷(현재 게임의 요약 상태) 조회
    @Transactional(readOnly = true)
    public GameSnapshotResponse getSnapshotByCode(String code) {
        CharadesGame game = getGameByCodeOrThrow(code);
        return getSnapshot(game);
    }
    @Transactional(readOnly = true)
    public GameSnapshotResponse getSnapshot(CharadesGame game) {
        Long gameId = game.getId();

        List<CharadesTeam> teams = teamRepo.findByGameIdOrderByOrderIndexAsc(gameId);

        // 팀별 완료 턴 수
        Map<Long,Integer> counts = turnRepo.countByTeam(gameId).stream()
                .collect(Collectors.toMap(arr -> (Long)arr[0], arr -> ((Long)arr[1]).intValue()));

        Pointer p = nextPointer(teams, counts);

        boolean finished = teams.stream().allMatch(t -> counts.getOrDefault(t.getId(), 0) >= game.getRoundsPerTeam());
        GameStatus status = finished ? GameStatus.FINISHED : game.getStatus();

        List<TeamDto> teamDtos = teams.stream()
                .map(TeamDto::fromEntity)
                .toList();

        return GameSnapshotResponse.builder()
                .code(game.getCode())
                .mode(game.getMode())
                .durationSec(game.getDurationSec())
                .targetCount(game.getTargetCount())
                .passLimit(game.getPassLimit())
                .roundsPerTeam(game.getRoundsPerTeam())
                .status(status)
                .teams(teamDtos)
                .current(new GameSnapshotResponse.CurrentDto(p.teamIdx, p.roundIdx))
                .build();
    }

    // 단어 배치(조회)
    @Transactional(readOnly = true)
    public WordBatchResponse getWordsByCode(String code, List<Long> exclude, int limit) {
        CharadesGame game = getGameByCodeOrThrow(code);
        return getWords(game.getId(), exclude, limit);
    }
    @Transactional(readOnly = true)
    public WordBatchResponse getWords(Long gameId, List<Long> exclude, int limit) {
        List<Short> catIds = gameCategoryRepo.findCategoryIds(gameId);
        if (catIds.isEmpty()) return new WordBatchResponse(List.of());

        PageRequest page = PageRequest.of(0, Math.min(Math.max(limit,1), CharadesConstants.MAX_WORD_BATCH));
        
        boolean excludeEmpty = (exclude == null || exclude.isEmpty());
        List<WordDto> words = wordRepo
                .findBatch(catIds, excludeEmpty ? List.of() : exclude, excludeEmpty, page)
                .stream()
                .map(w -> new WordDto(w.getId(), w.getText(), w.getDescription()))
                .toList();

        return new WordBatchResponse(words);
    }

    // 전체 플레이 일괄 저장
    public void finalizeGameByCode(String code, FinalizeGameRequest req) {
        CharadesGame game = getGameByCodeOrThrow(code);
        finalizeGame(game, req);
    }
    public void finalizeGame(CharadesGame game, FinalizeGameRequest req) {
        // 1) 게임이 이미 FINISHED 상태인지 체크
        if (game.getStatus() == GameStatus.FINISHED) {
            throw AppException.badRequest("Game already finalized");
        }

        // 2) 팀 조회 (빠른 접근 위한 Map 생성)
        List<CharadesTeam> teams = teamRepo.findByGameId(game.getId());
        Map<String, CharadesTeam> teamMap = teams.stream().collect(Collectors.toMap(CharadesTeam::getCode, t -> t));

        // 3) 모든 턴 기록 저장
        Map<String, Integer> totalScores = new HashMap<>(); // 팀별 총 점수 누적용
        for (FinalizeTurnRequest turnReq : req.getTurns()) {

            // 팀 검증
            CharadesTeam team = teamMap.get(turnReq.getTeamCode());
            if (team == null) {
                throw AppException.badRequest("Invalid teamCode: " + turnReq.getTeamCode());
            }

            // 4-1) 기본 Turn 생성
            CharadesTurn turn = CharadesTurn.create(
                    game,
                    team,
                    turnReq.getRoundIndex()
            );

            // 4-2) TurnOutcome 생성
            TurnOutcome outcome = TurnOutcome.of(
                    game.getMode(),
                    turnReq.getTimeUsedSec(),
                    turnReq.getElapsedSec(),
                    turnReq.getCorrectCount(),
                    turnReq.getUsedPass()
            );

            // 4-3) 결과 반영
            turn.applyOutcome(outcome, turnReq.getStartedAt(), turnReq.getEndedAt());

            turnRepo.save(turn);

            // 4-4) TurnWord 전체 저장
            int fallbackIdx = 0;

            for (TurnWordRequest wReq : turnReq.getWords()) {
                CharadesTurnWord tw = new CharadesTurnWord();
                tw.setTurn(turn);
                          
                tw.setIdx(wReq.getIdx() != null ? wReq.getIdx() : fallbackIdx++);
                tw.setWordText(wReq.getWordText());
                tw.setAction(wReq.getAction());
                tw.setAtSec(wReq.getAtSec());

                if (wReq.getWordId() != null) {
                    CharadesWord cw = new CharadesWord();
                    cw.setId(wReq.getWordId());
                    tw.setWord(cw);
                }

                turnWordRepo.save(tw);
            }

            // 4-5) 팀 점수 누적
            totalScores.merge(team.getCode(), turnReq.getCorrectCount(), Integer::sum);
        }

        // 5) 팀 엔티티에도 최종 점수 반영
        for (CharadesTeam team : teams) {
            int score = totalScores.getOrDefault(team.getCode(), 0);
            team.setScore(score);
            teamRepo.save(team);
        }

        // 6) 게임 상태 FINISHED 설정
        game.setStatus(GameStatus.FINISHED);
        gameRepo.save(game);
    }

    // 결과 조회
    @Transactional(readOnly = true)
    public GameResultResponse getResultByCode(String code) {
        CharadesGame game = getGameByCodeOrThrow(code);
        return getResult(game);
    }
    @Transactional(readOnly = true)
    public GameResultResponse getResult(CharadesGame game) {
        List<TeamDto> teams = teamRepo.findByGameIdOrderByOrderIndexAsc(game.getId())
                .stream()
                .map(TeamDto::fromEntity)
                .toList();
        return new GameResultResponse(game.getCode(), teams);
    }
}
