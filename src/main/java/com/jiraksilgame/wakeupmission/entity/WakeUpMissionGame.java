package com.jiraksilgame.wakeupmission.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "WAKE_UP_MISSION_GAMES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WakeUpMissionGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 관리용

    @Column(unique = true, nullable = false)
    private String code; // 외부 노출용

    @Column(name = "wake_up_time")
    private LocalDateTime wakeUpTime;

    @Column(length = 1000)
    private String contacts;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public static WakeUpMissionGame create(
        String code,
        LocalDateTime wakeUpTime,
        String contacts,
        String passwordHash)
    {
        WakeUpMissionGame g = new WakeUpMissionGame();
        g.setCode(code);
        g.setWakeUpTime(wakeUpTime);
        g.setContacts(contacts);
        g.setPasswordHash(passwordHash);
        return g;
    }
}
