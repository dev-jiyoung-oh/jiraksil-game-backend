package com.jiraksilgame.charades.domain;

import com.jiraksilgame.charades.entity.enums.GameMode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TurnOutcome {
    private final Integer timeUsedSec; // LIMITED
    private final Integer elapsedSec;  // UNTIL_CLEAR
    private final int correctCount;
    private final int usedPass;


    public static TurnOutcome of(
            GameMode mode,
            Integer timeUsedSec,
            Integer elapsedSec,
            Integer correctCount,
            Integer usedPass
    ) {
        int cc = Math.max(0, Objects.requireNonNullElse(correctCount, 0));
        int up = Math.max(0, Objects.requireNonNullElse(usedPass, 0));
        
        return (mode == GameMode.LIMITED)
                ? new TurnOutcome(timeUsedSec, null, cc, up)
                : new TurnOutcome(null, elapsedSec, cc, up);
    }

    /** LIMITED 전용 */
    public static TurnOutcome forLimited(Integer timeUsedSec, Integer correctCount, Integer usedPass) {
        int cc = Math.max(0, Objects.requireNonNullElse(correctCount, 0));
        int up = Math.max(0, Objects.requireNonNullElse(usedPass, 0));
        
        return new TurnOutcome(timeUsedSec, null, cc, up);
    }

    /** UNTIL_CLEAR 전용 */
    public static TurnOutcome forUntilClear(Integer elapsedSec, Integer correctCount, Integer usedPass) {
        int cc = Math.max(0, Objects.requireNonNullElse(correctCount, 0));
        int up = Math.max(0, Objects.requireNonNullElse(usedPass, 0));
        
        return new TurnOutcome(null, elapsedSec, cc, up);
    }
}
