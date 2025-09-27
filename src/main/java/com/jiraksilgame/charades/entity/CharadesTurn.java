package com.jiraksilgame.charades.entity;

import com.jiraksilgame.charades.domain.TurnOutcome;
import com.jiraksilgame.charades.entity.enums.GameMode;
import com.jiraksilgame.common.error.AppException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(
    name = "CHARADES_TURNS",
    uniqueConstraints = @UniqueConstraint(name = "uq_turn", columnNames = {"game_id", "team_id", "round_index"})
)
public class CharadesTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, length = 32)
    private String code; // optional external code

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private CharadesGame game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private CharadesTeam team;

    @Column(name = "round_index", nullable = false)
    private Integer roundIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 16)
    private GameMode mode;

    @Column(name = "duration_sec")
    private Integer durationSec;     // LIMITED

    @Column(name = "target_count")
    private Integer targetCount;     // UNTIL_CLEAR

    @Column(name = "pass_limit", nullable = false)
    private Integer passLimit;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "correct_count")
    private Integer correctCount;

    @Column(name = "used_pass")
    private Integer usedPass;

    @Column(name = "time_used_sec")
    private Integer timeUsedSec; // LIMITED

    @Column(name = "elapsed_sec")
    private Integer elapsedSec;  // UNTIL_CLEAR


    /** 게임/팀/라운드 스냅샷으로 턴 생성 (결과 적용 전) */
    public static CharadesTurn create(CharadesGame game, CharadesTeam team, int roundIdx) {
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(team, "team must not be null");
        if (roundIdx < 0) throw AppException.badRequest("roundIdx must be >= 0");

        CharadesTurn t = new CharadesTurn();
        t.setGame(game);
        t.setTeam(team);
        t.setRoundIndex(roundIdx);
        
        t.setMode(game.getMode());
        t.setDurationSec(game.getMode() == GameMode.LIMITED ? game.getDurationSec() : null);
        t.setTargetCount(game.getMode() == GameMode.UNTIL_CLEAR ? game.getTargetCount() : null);
        t.setPassLimit(game.getPassLimit());

        t.setCorrectCount(0);
        t.setUsedPass(0);

        return t;
    }

    /** 결과까지 한 번에 적용해서 생성하고 싶을 때 (오버로드) */
    public static CharadesTurn create(
            CharadesGame game,
            CharadesTeam team,
            int roundIdx,
            TurnOutcome out,
            LocalDateTime now
    ) {
        CharadesTurn t = create(game, team, roundIdx);
        t.applyOutcome(out, now);
        return t;
    }

    /** 결과(시간/카운트) 적용 */
    public void applyOutcome(TurnOutcome out, LocalDateTime now) {
        if (out == null) return;

        if (this.getMode() == GameMode.LIMITED) { // LIMITED
            Integer used = out.getTimeUsedSec();

            if (used != null && used > 0) {
                this.setStartedAt(now.minusSeconds(used));
                this.setEndedAt(now);
                this.setTimeUsedSec(used);
            }
        } else { // UNTIL_CLEAR
            Integer el = out.getElapsedSec();

            if (el != null && el > 0) {
                this.setStartedAt(now.minusSeconds(el));
                this.setEndedAt(now);
                this.setElapsedSec(el);
            }
        }

        this.setCorrectCount(out.getCorrectCount());
        this.setUsedPass(out.getUsedPass());
    }

}
