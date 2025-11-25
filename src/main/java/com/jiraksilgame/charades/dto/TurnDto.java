package com.jiraksilgame.charades.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import com.jiraksilgame.charades.entity.CharadesTurn;

@Getter
@AllArgsConstructor
public class TurnDto {

    private String code;
    private String teamCode;
    private String teamName;
    private int roundIndex;
    private int playNo;
    private int correctCount;
    private int usedPass;
    private int elapsedSec;
    private List<TurnWordDto> words;

    public static TurnDto fromEntity(CharadesTurn t) {
        return new TurnDto(
            t.getCode(),
            t.getTeam().getCode(),
            t.getTeam().getName(),
            t.getRoundIndex(),
            t.getPlayNo(),
            t.getCorrectCount(),
            t.getUsedPass(),
            t.getElapsedSec(),
            null
        );
    }
}
