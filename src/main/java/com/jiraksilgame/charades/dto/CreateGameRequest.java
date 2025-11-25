package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.enums.GameMode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateGameRequest {
    private GameMode mode;
    private Integer durationSec; // LIMITED
    private Integer targetCount; // UNTIL_CLEAR
    private Integer passLimit;
    private Integer roundsPerTeam;

    @NotBlank
    @Size(min = 4, max = 24)
    private String password;

    private List<String> teamNames;
    private List<String> categoryCodes; // [] 또는 null => 전체
}
