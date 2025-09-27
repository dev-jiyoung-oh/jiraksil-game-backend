package com.jiraksilgame.charades.entity;

import com.jiraksilgame.charades.dto.TurnWordRequest;
import com.jiraksilgame.charades.entity.enums.TurnAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "CHARADES_TURN_WORDS",
    uniqueConstraints = @UniqueConstraint(name = "uq_turn_word", columnNames = {"turn_id", "idx"})
)
public class CharadesTurnWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turn_id", nullable = false)
    private CharadesTurn turn;

    @Column(name = "idx", nullable = false)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "word_id")
    private CharadesWord word; // nullable

    @Column(name = "word_id", insertable = false, updatable = false)
    private Long wordId; // 읽기 전용

    @Column(name = "word_text", nullable = false, length = 255)
    private String wordText;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 8)
    private TurnAction action; // CORRECT / PASS

    @Column(name = "at_sec", nullable = false)
    private Integer atSec;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public static CharadesTurnWord create(CharadesTurn turn, TurnWordRequest tw, int fallbackIdx, LocalDateTime now) {
        CharadesTurnWord e = new CharadesTurnWord();
        e.setTurn(turn);
        e.setIdx(tw.getIdx() != null ? tw.getIdx() : fallbackIdx);
        if (tw.getWordId() != null) {
            CharadesWord w = new CharadesWord();
            w.setId(tw.getWordId());
            e.setWord(w);
        }
        e.setWordText(tw.getWordText());
        e.setAction(tw.getAction());
        e.setAtSec(tw.getAtSec());
        e.setCreatedAt(now);
        return e;
    }

}
