package com.jiraksilgame.charades.dto;

import java.util.List;

import com.jiraksilgame.charades.entity.CharadesGame;
import com.jiraksilgame.charades.entity.CharadesTeam;
import com.jiraksilgame.charades.entity.enums.GameMode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateGameResponse {
    private String code;
    private GameMode mode;
    private Integer durationSec; // LIMITED
    private Integer targetCount; // UNTIL_CLEAR
    private Integer passLimit;
    private Integer roundsPerTeam;
    private List<TeamDto> teams;

    public CreateGameResponse(CharadesGame game, List<CharadesTeam> teamEntities) {
        this.code = game.getCode();
        this.mode = game.getMode();
        this.durationSec = game.getDurationSec();
        this.targetCount = game.getTargetCount();
        this.passLimit = game.getPassLimit();
        this.roundsPerTeam = game.getRoundsPerTeam();
        this.teams = teamEntities.stream()
                .map(TeamDto::fromEntity)
                .toList();
    }
}
