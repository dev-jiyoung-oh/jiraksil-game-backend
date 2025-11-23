package com.jiraksilgame.charades.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TurnOutcome {

    private final Integer elapsedSec;
    private final int correctCount;
    private final int usedPass;


    public static TurnOutcome of(Integer elapsedSec, Integer correctCount, Integer usedPass) {
        int es = Math.max(0, Objects.requireNonNullElse(elapsedSec, 0));
        int cc = Math.max(0, Objects.requireNonNullElse(correctCount, 0));
        int up = Math.max(0, Objects.requireNonNullElse(usedPass, 0));
        
        return new TurnOutcome(es, cc, up);
    }
}
