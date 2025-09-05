package com.jiraksilgame.wakeupmission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "WAKE_UP_MISSION_MISSIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WakeUpMissionMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
