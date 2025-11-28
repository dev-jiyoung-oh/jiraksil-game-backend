package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.CharadesGame;
import com.jiraksilgame.charades.entity.enums.GameMode;
import com.jiraksilgame.charades.entity.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GameInfoDto {
    private String code;
    private GameMode mode;
    private Integer durationSec; // LIMITED
    private Integer targetCount; // UNTIL_CLEAR
    private Integer passLimit;
    private Integer roundsPerTeam;
    private GameStatus status;
    private List<TeamDto> teams;

    public static GameInfoDto of(CharadesGame g, List<TeamDto> ts) {
        return new GameInfoDto(
            g.getCode(),
            g.getMode(),
            g.getDurationSec(),
            g.getTargetCount(),
            g.getPassLimit(),
            g.getRoundsPerTeam(),
            g.getStatus(),
            ts
        );
    }
}
