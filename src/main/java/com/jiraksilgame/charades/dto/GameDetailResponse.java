package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.enums.GameMode;
import com.jiraksilgame.charades.entity.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GameDetailResponse {
    private String code;
    private GameMode mode;
    private Integer durationSec;
    private Integer targetCount;
    private Integer passLimit;
    private Integer roundsPerTeam;
    private GameStatus status;
    private List<TeamDto> teams;
    private CurrentDto current;

    @Getter @AllArgsConstructor
    public static class CurrentDto {
        private Integer teamIndex;
        private Integer roundIndex;
    }
}
