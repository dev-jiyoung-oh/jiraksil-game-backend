package com.jiraksilgame.charades.entity;

import com.jiraksilgame.charades.domain.TurnOutcome;
import com.jiraksilgame.charades.entity.enums.GameMode;
import com.jiraksilgame.common.CommonConstants;
import com.jiraksilgame.common.error.AppException;
import com.jiraksilgame.common.util.CodeGenerator;

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
    uniqueConstraints = @UniqueConstraint(
        name = "uq_turn",
        columnNames = {"game_id", "team_id", "round_index", "play_no"}
    )
)
public class CharadesTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, length = 32, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private CharadesGame game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private CharadesTeam team;

    @Column(name = "round_index", nullable = false)
    private Integer roundIndex;

    @Column(name = "play_no", nullable = false)
    private Integer playNo;


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


    /** 턴 생성 (결과 적용 전) */
    public static CharadesTurn create(CharadesGame game, CharadesTeam team, int roundIdx, int playNo) {
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(team, "team must not be null");
        if (roundIdx < 0) throw AppException.badRequest("roundIdx must be >= 0");

        CharadesTurn t = new CharadesTurn();
        t.setGame(game);
        t.setTeam(team);
        t.setRoundIndex(roundIdx);
        t.setPlayNo(playNo);

        // 턴 코드 자동 생성
        t.setCode(CodeGenerator.randomCode(CommonConstants.GAME_CODE_LENGTH));
        
        t.setMode(game.getMode());
        t.setDurationSec(game.getMode() == GameMode.LIMITED ? game.getDurationSec() : null);
        t.setTargetCount(game.getMode() == GameMode.UNTIL_CLEAR ? game.getTargetCount() : null);
        t.setPassLimit(game.getPassLimit());

        t.setCorrectCount(0);
        t.setUsedPass(0);

        return t;
    }

    /** 결과(시간/카운트) 적용 */
    public void applyOutcome(TurnOutcome out, LocalDateTime startedAt, LocalDateTime endedAt) {
        if (out == null) return;

        // 1) 시간 저장 (프론트 값 그대로)
        this.setStartedAt(startedAt);
        this.setEndedAt(endedAt);

        // 2) 모드별 추가 필드
        if (this.getMode() == GameMode.LIMITED) {
            this.setTimeUsedSec(out.getTimeUsedSec());   // LIMITED 전용
            this.setElapsedSec(null);
        } else {
            this.setElapsedSec(out.getElapsedSec());     // UNTIL_CLEAR 전용
            this.setTimeUsedSec(null);
        }

        // 3) 정답/패스 카운트
        this.setCorrectCount(out.getCorrectCount());
        this.setUsedPass(out.getUsedPass());
    }

}
