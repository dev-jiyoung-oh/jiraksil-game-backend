package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.enums.TurnAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TurnWordRequest {
    private Integer idx;
    private Long wordId;
    private String wordText;
    private TurnAction action; // CORRECT / PASS
    private Integer atSec;
}
