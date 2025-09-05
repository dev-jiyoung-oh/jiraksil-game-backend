package com.jiraksilgame.wakeupmission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "WAKE_UP_MISSION_GAME_MISSIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WakeUpMissionGameMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private WakeUpMissionGame game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private WakeUpMissionMission mission;

    @Column(name = "assigned_player", nullable = false)
    private Integer assignedPlayer;
}
