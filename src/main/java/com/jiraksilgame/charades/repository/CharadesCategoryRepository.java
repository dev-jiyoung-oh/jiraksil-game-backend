package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CharadesCategoryRepository extends JpaRepository<CharadesCategory, Short> {
    List<CharadesCategory> findByIsActiveTrue();
    Optional<CharadesCategory> findByCode(String code);

    @Query("select c.id from CharadesCategory c where c.code in :codes and c.isActive = true")
    List<Short> findActiveIdsByCodes(@Param("codes") List<String> codes);
}
