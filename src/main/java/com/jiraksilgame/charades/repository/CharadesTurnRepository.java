package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesTurn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharadesTurnRepository extends JpaRepository<CharadesTurn, Long> {
    @Query("select t.team.id, count(t) from CharadesTurn t where t.game.id = :gameId group by t.team.id")
    List<Object[]> countByTeam(@Param("gameId") Long gameId);
}
