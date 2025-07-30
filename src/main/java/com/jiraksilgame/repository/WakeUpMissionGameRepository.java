package com.jiraksilgame.repository;

import com.jiraksilgame.entity.WakeUpMissionGame;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WakeUpMissionGameRepository extends JpaRepository<WakeUpMissionGame, Long> {
  Optional<WakeUpMissionGame> findByCode(String code);
}
