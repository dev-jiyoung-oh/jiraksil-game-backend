package com.jiraksilgame.charades.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GameResultResponse {
    private String gameCode;
    private List<GameSnapshotResponse.TeamDto> teams;
}
