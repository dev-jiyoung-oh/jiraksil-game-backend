package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.domain.TeamColor;
import com.jiraksilgame.charades.entity.CharadesTeam;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamDto {
    private String code;
    private String name;
    private String color;
    private String colorHex;
    private Integer score;
    private Integer orderIndex;

    public static TeamDto fromEntity(CharadesTeam t) {
        return new TeamDto(
            t.getCode(),
            t.getName(),
            t.getColor(),
            TeamColor.hexOf(t.getColor()),
            t.getScore(),
            t.getOrderIndex()
        );
    }
}
