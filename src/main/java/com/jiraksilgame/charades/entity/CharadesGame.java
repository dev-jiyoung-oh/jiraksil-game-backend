package com.jiraksilgame.charades.entity;

import com.jiraksilgame.charades.entity.enums.GameMode;
import com.jiraksilgame.charades.entity.enums.GameStatus;
import com.jiraksilgame.common.error.AppException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "CHARADES_GAMES")
public class CharadesGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 32)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 16)
    private GameMode mode;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "target_count")
    private Integer targetCount;

    @Column(name = "pass_limit", nullable = false)
    private Integer passLimit;

    @Column(name = "rounds_per_team", nullable = false)
    private Integer roundsPerTeam;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private GameStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    
    public static CharadesGame create(
            String code,          // 코드(외부 노출용, 사전 생성)
            GameMode mode,
            Integer durationSec,  // LIMITED일 때 필수(>0), 아니면 무시
            Integer targetCount,  // UNTIL_CLEAR일 때 필수(>0), 아니면 무시
            Integer passLimit,    // null → 기본 2
            Integer roundsPerTeam // null → 기본 3
    ) {
        Objects.requireNonNull(mode, "mode must not be null");
        Objects.requireNonNull(code, "code must not be null");

        if (mode == GameMode.LIMITED) {
            if (durationSec == null || durationSec <= 0) {
                throw AppException.badRequest("durationSec must be positive in LIMITED mode");
            }
            targetCount = null;
        } else { // UNTIL_CLEAR
            if (targetCount == null || targetCount <= 0) {
                throw AppException.badRequest("targetCount must be positive in UNTIL_CLEAR mode");
            }
            durationSec = null;
        }

        CharadesGame g = new CharadesGame();
        g.setCode(code);
        g.setMode(mode);
        g.setDurationSec(durationSec);
        g.setTargetCount(targetCount);
        g.setPassLimit(passLimit != null ? passLimit : 2);
        g.setRoundsPerTeam(roundsPerTeam != null ? roundsPerTeam : 3);
        g.setStatus(GameStatus.READY);

        return g;
    }
}
