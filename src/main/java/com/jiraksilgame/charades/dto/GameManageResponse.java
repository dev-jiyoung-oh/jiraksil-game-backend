package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.enums.GameMode;
import com.jiraksilgame.charades.entity.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GameManageResponse {

    private String code;
    private GameMode mode;
    private Integer durationSec; // LIMITED
    private Integer targetCount; // UNTIL_CLEAR
    private int passLimit;
    private int roundsPerTeam;
    private GameStatus status;

    private List<TeamDto> teams;

    /** 선택된 카테고리 목록 (게임-카테고리 매핑) */
    private List<GameCategoryDto> categories;

    /** 전체 카테고리 마스터 */
    private List<CategoryDto> categoryMaster;

    /** 모든 턴 기록 */
    private List<TurnDto> turns;

    @Getter
    @AllArgsConstructor
    public static class GameCategoryDto {
        private String categoryCode;
    }
}
