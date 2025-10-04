package com.jiraksilgame.charades.service;

import com.jiraksilgame.charades.domain.TeamColor;
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

import java.time.LocalDateTime;
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

    private static TeamDto toTeamDto(CharadesTeam t) {
        return new TeamDto(
                t.getCode(),
                t.getName(),
                t.getColor(),
                TeamColor.hexOf(t.getColor()),
                t.getScore(),
                t.getOrderIndex()
        );
    }

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
        for (int i = 0; i < names.size(); i++) {
            CharadesTeam t = CharadesTeam.create(game, i, names.get(i));
            teamRepo.save(t);
        }

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

        return new CreateGameResponse(game.getCode());
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
                .map(CharadesService::toTeamDto)
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
                .map(w -> new WordDto(w.getId(), w.getText()))
                .toList();

        return new WordBatchResponse(words);
    }

    // 턴 종료(저장)
    public GameSnapshotResponse finalizeTurnByCode(String code, FinalizeTurnRequest req) {
        CharadesGame game = getGameByCodeOrThrow(code);
        return finalizeTurn(game, req);
    }
    public GameSnapshotResponse finalizeTurn(CharadesGame game, FinalizeTurnRequest req) {
        // 1) 턴 저장
        Long gameId = game.getId();
        List<CharadesTeam> teams = teamRepo.findByGameIdOrderByOrderIndexAsc(gameId);

        // 팀별 완료 턴 수
        Map<Long,Integer> counts = turnRepo.countByTeam(gameId).stream()
                .collect(Collectors.toMap(arr -> (Long)arr[0], arr -> ((Long)arr[1]).intValue()));
        
        Pointer p = nextPointer(teams, counts);
        if (p.roundIdx >= game.getRoundsPerTeam()) {
            return getSnapshot(game); // 이미 모든 라운드 완료
        }

        CharadesTeam team = teams.get(p.teamIdx);

        LocalDateTime now = LocalDateTime.now();
        TurnOutcome outcome = TurnOutcome.of(
                game.getMode(),
                req.getTimeUsedSec(),
                req.getElapsedSec(),
                req.getCorrectCount(),
                req.getUsedPass()
        );

        CharadesTurn turn = CharadesTurn.create(game, team, p.roundIdx, outcome, now);
        
        // 동시성: 같은 턴이 중복 저장될 수 있으니 예외 시 최신 스냅샷 반환
        try {
            turnRepo.saveAndFlush(turn);
        } catch (DataIntegrityViolationException e) {
            return getSnapshot(game);
        }

        // 2) 턴 내 제시어 저장
        if (req.getTurnWords() != null) {
            int auto = 0;
            List<CharadesTurnWord> words = new ArrayList<>(req.getTurnWords().size());
            for (TurnWordRequest tw : req.getTurnWords()) {
                words.add(CharadesTurnWord.create(turn, tw, ++auto, now));
            }
            turnWordRepo.saveAll(words);
        }

        // 3) 팀 스코어 갱신
        team.setScore(team.getScore() + turn.getCorrectCount());

        // 4) 게임 상태 변경(FINISHED/INTERMISSION)
        boolean finished = teams.stream().allMatch(t ->
                (counts.getOrDefault(t.getId(), 0) + (t.getId().equals(team.getId()) ? 1 : 0))
                 >= game.getRoundsPerTeam()
        );
        game.setStatus(finished ? GameStatus.FINISHED : GameStatus.INTERMISSION);
        game.setUpdatedAt(now);

        return getSnapshot(game);
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
                .map(CharadesService::toTeamDto)
                .toList();
        return new GameResultResponse(game.getCode(), teams);
    }
}
