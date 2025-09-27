package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesGame;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CharadesGameRepository extends JpaRepository<CharadesGame, Long> {
    Optional<CharadesGame> findByCode(String code);
}
