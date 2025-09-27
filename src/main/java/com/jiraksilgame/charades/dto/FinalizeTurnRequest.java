package com.jiraksilgame.charades.dto;

import com.jiraksilgame.charades.entity.enums.GameMode;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class FinalizeTurnRequest {
        private GameMode mode;

        @NotNull
        @Min(0)
        private Integer correctCount;

        @NotNull
        @Min(0)
        private Integer usedPass;

        @Min(1)
        @Max(1000)
        private Integer timeUsedSec; // LIMITED
        
        @Min(1)
        @Max(1000)
        private Integer elapsedSec;  // UNTIL_CLEAR

        private List<TurnWordRequest> turnWords;
}
