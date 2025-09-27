package com.jiraksilgame.charades.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jiraksilgame.charades.domain.TeamColor;
import com.jiraksilgame.common.error.AppException;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "CHARADES_TEAMS",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_team_code_per_game", columnNames = {"game_id", "code"}),
        @UniqueConstraint(name = "uq_team_order", columnNames = {"game_id", "order_index"})
    }
)
public class CharadesTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private CharadesGame game;

    @Column(name = "code", nullable = false, length = 1)
    private String code; // A~Z

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "color", nullable = false, length = 32)
    private String color; // 'BLUE','RED','GREEN','YELLOW' 등 토큰

    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public static CharadesTeam create(CharadesGame game, int index, String rawName) {
        if (game == null) throw AppException.badRequest("game is required");
        if (index < 0) throw AppException.badRequest("index must be >= 0");
        char codeChar = (char) ('A' + index);
        if (codeChar > 'Z') throw AppException.badRequest("team index exceeds range A~Z");

        String code = String.valueOf(codeChar);
        String name = (rawName == null || rawName.isBlank()) ? ("Team " + code) : rawName.trim();
        String colorToken = TeamColor.nth(index).name(); // 팔레트 순환 배정

        CharadesTeam t = new CharadesTeam();
        t.setGame(game);
        t.setCode(code);
        t.setName(name);
        t.setColor(colorToken);
        t.setScore(0);
        t.setOrderIndex(index);
        return t;
    }
}
