package com.jiraksilgame.wakeupmission.repository;

import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGameMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** 기상미션 - 게임-미션 매핑 레포지토리 */
public interface WakeUpMissionGameMissionRepository extends JpaRepository<WakeUpMissionGameMission, Long> {
    /**
     * 게임 ID로 매핑 목록 조회
     * 
     * @param gameId 게임 PK
     * @return 매핑 목록
     */
    List<WakeUpMissionGameMission> findByGameId(Long gameId);
}
