package com.jiraksilgame.charades.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(
    name = "CHARADES_GAME_CATEGORIES",
    uniqueConstraints = @UniqueConstraint(name = "uq_game_category", columnNames = {"game_id", "category_id"})
)
public class CharadesGameCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private CharadesGame game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CharadesCategory category;


    public static CharadesGameCategory of(CharadesGame game, CharadesCategory category) {
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(category, "category must not be null");

        CharadesGameCategory gc = new CharadesGameCategory();
        gc.setGame(game);
        gc.setCategory(category);

        return gc;
    }
    
    public static CharadesGameCategory ofCategoryId(CharadesGame game, short categoryId) {
        CharadesCategory c = new CharadesCategory();
        c.setId(categoryId);
        
        return of(game, c);
    }
}
