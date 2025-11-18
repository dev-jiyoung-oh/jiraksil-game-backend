package com.jiraksilgame.charades.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class FinalizeGameRequest {
    private List<FinalizeTurnRequest> turns;
}