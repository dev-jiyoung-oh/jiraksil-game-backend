package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesWord;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharadesWordRepository extends JpaRepository<CharadesWord, Long> {
    @Query("""
        select w from CharadesWord w
         where w.isActive = true
             and w.category.id in :categoryIds
             and (:excludeEmpty = true or w.id not in :excludeIds)
    """)
    List<CharadesWord> findBatch(
            @Param("categoryIds") List<Short> categoryIds,
            @Param("excludeIds") List<Long> excludeIds,
            @Param("excludeEmpty") boolean excludeEmpty,
            Pageable pageable);
}
