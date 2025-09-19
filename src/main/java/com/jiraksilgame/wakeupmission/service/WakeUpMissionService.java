package com.jiraksilgame.wakeupmission.service;

import com.jiraksilgame.common.CommonConstants;
import com.jiraksilgame.common.error.AppException;
import com.jiraksilgame.common.util.CodeGenerator;
import com.jiraksilgame.wakeupmission.dto.CreateWakeUpMissionRequest;
import com.jiraksilgame.wakeupmission.dto.WakeUpMissionResponse;
import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGame;
import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGameMission;
import com.jiraksilgame.wakeupmission.entity.WakeUpMissionMission;
import com.jiraksilgame.wakeupmission.repository.WakeUpMissionGameMissionRepository;
import com.jiraksilgame.wakeupmission.repository.WakeUpMissionGameRepository;
import com.jiraksilgame.wakeupmission.repository.WakeUpMissionMissionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 기상 미션 서비스
 */
@Service
@RequiredArgsConstructor
public class WakeUpMissionService {

    private final WakeUpMissionGameRepository gameRepository;
    private final WakeUpMissionMissionRepository missionRepository;
    private final WakeUpMissionGameMissionRepository gameMissionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /** 고유 코드로 게임 생성 및 저장
     * <p><b>참고</b>: 코드 유니크 충돌 시 새 코드를 생성해 최대 3회까지 재시도</p>
     * 
     *  @param wakeUpTime 기상 시각
     *  @param contacts 연락처
     *  @param passwordHash 비밀번호 해시
     *  @return 저장된 게임 엔티티
     *  @throws AppException 저장 실패 시
     */
    private WakeUpMissionGame createWithUniqueCode(LocalDateTime wakeUpTime, String contacts, String passwordHash) {
        for (int attempt = 0; attempt < 3; attempt++) {
            String code = CodeGenerator.randomCode(CommonConstants.GAME_CODE_LENGTH);
            WakeUpMissionGame game = WakeUpMissionGame.create(code, wakeUpTime, contacts, passwordHash);
            
            try {
                return gameRepository.saveAndFlush(game); // 고유코드 충돌 시 즉시 감지
            } catch (DataIntegrityViolationException e) {
                if (attempt == 2) { // 3회 모두 실패 시 전파
                    throw AppException.internal("게임 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
                }
                // 다음 루프에서 새 코드로 재시도
            }
        }

        throw AppException.internal("게임 생성에 실패했습니다."); // 방어적 코드
    }

    /**
     * HH:mm 입력을 기준으로 기상 시각 계산
     * <p><b>참고</b>: 입력 시각이 현재보다 이르면 익일로 설정, 입력이 null이면 null 반환</p>
     * 
     * @param wakeUpTime 기상 시각 HH:mm, null 가능
     * @return 계산된 기상 시각 또는 null
     */
    private LocalDateTime calculateWakeUpDateTime(LocalTime wakeUpTime) {
        if (wakeUpTime == null) {
            return null;
        }
        
        LocalDate today = LocalDate.now();
        LocalDateTime candidate = LocalDateTime.of(today, wakeUpTime);

        // 입력 시각이 현재보다 이르면 익일로 설정
        if (candidate.isBefore(LocalDateTime.now())) {
            candidate = candidate.plusDays(1);
        }

        return candidate;
    }

    // ========= API 로직 =========

    /**
     * 게임 생성
     * 
     * @param req 생성 요청
     * @return 생성 결과
     * @throws AppException 미션 수 부족 등 검증 실패 시
     */
    @Transactional
    public WakeUpMissionResponse createGame(CreateWakeUpMissionRequest req) {
        // 데이터 검증
        List<WakeUpMissionMission> missionPool = missionRepository.findAll();
        if (req.getNumPlayers() > missionPool.size()) {
            throw AppException.badRequest("미션 개수보다 플레이어 수가 많습니다.");
        }

        // 비밀번호 해시
        String passwordHash = passwordEncoder.encode(req.getPassword());

        // 기상 시간 계산
        LocalDateTime wakeUpDateTime = calculateWakeUpDateTime(req.getWakeUpTime());

        // 게임 저장
        WakeUpMissionGame game = createWithUniqueCode(wakeUpDateTime, req.getContacts(), passwordHash);
        gameRepository.save(game);

        // 랜덤 미션 생성
        Collections.shuffle(missionPool);
        List<WakeUpMissionMission> selectedMissions = missionPool.subList(0, req.getNumPlayers());

        // 게임-미션 매핑 저장
        List<WakeUpMissionGameMission> gameMissions = new ArrayList<>();

        for (int i = 0; i < selectedMissions.size(); i++) {
            gameMissions.add(WakeUpMissionGameMission.builder()
                    .game(game)
                    .mission(selectedMissions.get(i))
                    .assignedPlayer(i)
                    .build());
        }
        gameMissionRepository.saveAll(gameMissions);

        return new WakeUpMissionResponse(game, gameMissions);
    }

    /**
     * 비밀번호 인증 후 게임 조회
     * 
     * @param code 게임 코드
     * @param password 비밀번호
     * @return 게임 정보
     * @throws AppException 인증 실패 시
     */
    public WakeUpMissionResponse getGameByCodeWithPassword(String code, String password) {
        WakeUpMissionGame game = gameRepository.findByCode(code)
                .orElse(null);

        // 게임이 존재하지 않거나 비밀번호가 일치하지 않는 경우 에러 발생
        if (game == null || !passwordEncoder.matches(password, game.getPasswordHash())) {
            throw AppException.badRequest("게임이 존재하지 않거나 비밀번호가 일치하지 않습니다.");
        }

        // 게임의 미션 조회
        List<WakeUpMissionGameMission> missions = gameMissionRepository.findByGameId(game.getId());

        return new WakeUpMissionResponse(game, missions);
    }
}
