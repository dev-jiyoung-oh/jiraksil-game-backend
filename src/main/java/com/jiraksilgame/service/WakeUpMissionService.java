package com.jiraksilgame.service;

import com.jiraksilgame.dto.CreateWakeUpMissionRequest;
import com.jiraksilgame.dto.WakeUpMissionResponse;
import com.jiraksilgame.entity.WakeUpMissionGame;
import com.jiraksilgame.entity.WakeUpMissionGameMission;
import com.jiraksilgame.entity.WakeUpMissionMission;
import com.jiraksilgame.exception.InvalidRequestException;
import com.jiraksilgame.repository.WakeUpMissionGameMissionRepository;
import com.jiraksilgame.repository.WakeUpMissionGameRepository;
import com.jiraksilgame.repository.WakeUpMissionMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WakeUpMissionService {

    private final WakeUpMissionGameRepository gameRepository;
    private final WakeUpMissionMissionRepository missionRepository;
    private final WakeUpMissionGameMissionRepository gameMissionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 게임 생성
    @Transactional
    public WakeUpMissionResponse createGame(CreateWakeUpMissionRequest request) {
        // 데이터 검증
        if (request.getNumPlayers() <= 0) {
            throw new InvalidRequestException("numPlayers는 1 이상이어야 함");
        }
        List<WakeUpMissionMission> missionPool = missionRepository.findAll();
        if (request.getNumPlayers() > missionPool.size()) {
            throw new InvalidRequestException("미션 개수보다 플레이어 수가 많습니다.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 4) {
            throw new InvalidRequestException("비밀번호는 최소 4자리 이상이어야 합니다.");
        }

        // 비밀번호 해시
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // 기상 시간 계산
        LocalDateTime wakeUpDateTime = calculateWakeUpDateTime(request.getWakeUpTime());

        // 게임 저장
        WakeUpMissionGame game = WakeUpMissionGame.builder()
                .wakeUpTime(wakeUpDateTime)
                .contacts(request.getContacts())
                .passwordHash(passwordHash)
                .build();
        gameRepository.save(game);

        // 랜덤 미션 생성
        Collections.shuffle(missionPool);
        List<WakeUpMissionMission> selectedMissions = missionPool.subList(0, request.getNumPlayers());

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

    // 게임 조회
    public WakeUpMissionResponse getGameByCodeWithPassword(String code, String password) {
        WakeUpMissionGame game = gameRepository.findByCode(code)
                .orElse(null);

        // 게임이 존재하지 않거나 비밀번호가 일치하지 않는 경우 에러 발생
        if (game == null || !passwordEncoder.matches(password, game.getPasswordHash())) {
            System.out.println("password: "+ password);
            System.out.println("passwordHash: "+ game.getPasswordHash());
            throw new InvalidRequestException("인증 실패");
        }

        // 게임의 미션 조회
        List<WakeUpMissionGameMission> missions = gameMissionRepository.findByGameId(game.getId());

        return new WakeUpMissionResponse(game, missions);
    }

    // 기상 시간 계산
    // 입력하신 시간 기준으로 해당 시간이 지나지 않았다면 오늘, 이미 지난 시간이면 내일
    private LocalDateTime calculateWakeUpDateTime(LocalTime wakeUpTime) {
        if (wakeUpTime == null) {
            return null;
        }
        
        LocalDate today = LocalDate.now();
        LocalDateTime candidate = LocalDateTime.of(today, wakeUpTime);

        // 현재 시간이 이미 지난 경우 내일로 설정
        if (candidate.isBefore(LocalDateTime.now())) {
            candidate = candidate.plusDays(1);
        }

        return candidate;
    }
}
