package com.jiraksilgame.charades.dto;

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
}
