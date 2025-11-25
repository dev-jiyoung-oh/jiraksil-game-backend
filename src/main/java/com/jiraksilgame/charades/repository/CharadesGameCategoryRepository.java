package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesGameCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CharadesGameCategoryRepository extends JpaRepository<CharadesGameCategory, Long> {

    /**
     * 게임 ID로 매핑 카테고리 id 목록 조회
     * 
     * @param gameId 게임 PK
     * @return 매핑 카테고리 id 목록
     */
    @Query("select gc.category.id from CharadesGameCategory gc where gc.game.id = :gameId")
    List<Short> findCategoryIdsByGameId(Long gameId);

    /**
     * 게임 ID로 매핑 목록 조회
     * 
     * @param gameId 게임 PK
     * @return 매핑 목록
     */
    List<CharadesGameCategory> findByGameId(Long gameId);
}
