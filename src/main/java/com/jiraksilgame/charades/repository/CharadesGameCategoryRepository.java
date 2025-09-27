package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesGameCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharadesGameCategoryRepository extends JpaRepository<CharadesGameCategory, Long> {
    @Query("select gc.category.id from CharadesGameCategory gc where gc.game.id = :gameId")
    List<Short> findCategoryIds(@Param("gameId") Long gameId);
}
