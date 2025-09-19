package com.jiraksilgame.wakeupmission.repository;

import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGame;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/** 기상미션 - 게임 레포지토리 */
public interface WakeUpMissionGameRepository extends JpaRepository<WakeUpMissionGame, Long> {
    /**
     * 코드로 게임 조회
     * 
     * @param code 외부 노출용 코드
     * @return 존재하면 엔티티, 없으면 빈 Optional
     */
    Optional<WakeUpMissionGame> findByCode(String code);
}
