package com.jiraksilgame.wakeupmission.repository;

import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGameMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WakeUpMissionGameMissionRepository extends JpaRepository<WakeUpMissionGameMission, Long> {
    List<WakeUpMissionGameMission> findByGameId(Long gameId);
}
