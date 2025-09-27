package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.enums.GameMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class CreateGameRequest {
    private List<String> teamNames;
    private GameOptions options;

    @Getter @Setter
    @NoArgsConstructor
    public static class GameOptions {
        private GameMode mode;
        private Integer durationSec; // LIMITED
        private Integer targetCount; // UNTIL_CLEAR
        private Integer passLimit;
        private Integer roundsPerTeam;
        private List<String> categoryCodes; // [] 또는 null => 전체
    }
}
