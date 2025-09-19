package com.jiraksilgame.wakeupmission.entity;

import jakarta.persistence.*;
import lombok.*;

/** 기상미션 - 게임-미션 매핑 엔티티 */
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

    /** 배정된 플레이어 인덱스(0 기반) */
    @Column(name = "assigned_player", nullable = false)
    private Integer assignedPlayer;
}
