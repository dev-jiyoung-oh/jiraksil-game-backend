package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.enums.TurnAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TurnWordDto {

    private int idx;
    private String wordText;
    private TurnAction action;
    private int atSec;
}
