package com.jiraksilgame.wakeupmission.repository;

import com.jiraksilgame.wakeupmission.entity.WakeUpMissionMission;
import org.springframework.data.jpa.repository.JpaRepository;

/** 기상 미션 - 미션 레포지토리 */
public interface WakeUpMissionMissionRepository extends JpaRepository<WakeUpMissionMission, Long> {
}
