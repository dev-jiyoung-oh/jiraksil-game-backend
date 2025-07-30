package com.jiraksilgame.repository;

import com.jiraksilgame.entity.WakeUpMissionGameMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WakeUpMissionGameMissionRepository extends JpaRepository<WakeUpMissionGameMission, Long> {
    List<WakeUpMissionGameMission> findByGameId(Long gameId);
}
