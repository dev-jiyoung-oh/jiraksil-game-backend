package com.jiraksilgame.charades.repository;

import com.jiraksilgame.charades.entity.CharadesTurn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CharadesTurnRepository extends JpaRepository<CharadesTurn, Long> {

    /**
     * 팀별 턴 수 조회
     * 
     * @param gameId 게임 PK
     * @return 팀별 턴 수 목록
     */
    @Query("select t.team.id, count(t) from CharadesTurn t where t.game.id = :gameId group by t.team.id")
    List<Object[]> countByTeam(Long gameId);

    /**
     * 게임의 최대 플레이 번호(playNo) 조회
     * 
     * @param gameId 게임 PK
     * @return 게임의 최대 플레이 번호(playNo)
     */
    @Query("""
        SELECT MAX(t.playNo) 
        FROM CharadesTurn t 
        WHERE t.game.id = :gameId
    """)
    Integer findMaxPlayNo(Long gameId);
    
    /**
     * 게임의 모든 턴 정보 조회 (team 즉시 로딩 포함)
     *
     * @param gameId 게임 PK
     * @return 정렬된 게임의 모든 턴 목록 (team 정보 포함)
     */
    @Query("""
        select t
        from CharadesTurn t
        join fetch t.team
        where t.game.id = :gameId
        order by t.playNo asc, t.roundIndex asc
    """)
    List<CharadesTurn> findWithTeamByGameIdOrderByPlayNoAscRoundIndexAsc(Long gameId);

}
