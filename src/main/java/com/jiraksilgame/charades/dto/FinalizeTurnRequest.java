package com.jiraksilgame.charades.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class FinalizeTurnRequest {
    private String teamCode;

    private Integer roundIndex;

    @NotNull
    @Min(0)
    private Integer correctCount;

    @NotNull
    @Min(0)
    private Integer usedPass;

    @Min(1)
    @Max(1000)
    private Integer elapsedSec;

    private LocalDateTime startedAt;
    
    private LocalDateTime endedAt;

    private List<TurnWordRequest> words;
}
