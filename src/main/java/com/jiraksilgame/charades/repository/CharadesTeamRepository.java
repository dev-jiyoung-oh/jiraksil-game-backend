package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharadesTeamRepository extends JpaRepository<CharadesTeam, Long> {
    List<CharadesTeam> findByGameIdOrderByOrderIndexAsc(Long gameId);
}
