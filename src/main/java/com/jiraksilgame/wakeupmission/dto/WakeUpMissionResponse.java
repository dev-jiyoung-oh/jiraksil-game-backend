package com.jiraksilgame.wakeupmission.dto;

import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGame;
import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGameMission;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WakeUpMissionResponse {
    private String id;
    private LocalDateTime wakeUpTime;
    private String contacts;
    private List<MissionDto> missions;

    public WakeUpMissionResponse(WakeUpMissionGame game, List<WakeUpMissionGameMission> gameMissions) {
        this.id = game.getCode();
        this.wakeUpTime = game.getWakeUpTime();
        this.contacts = game.getContacts();
        this.missions = gameMissions.stream()
                .map(m -> new MissionDto(m.getMission().getContent(), m.getAssignedPlayer()))
                .collect(Collectors.toList());
    }

    @Getter
    public static class MissionDto {
        private final String content;
        private final int assignedPlayer;

        public MissionDto(String content, int assignedPlayer) {
            this.content = content;
            this.assignedPlayer = assignedPlayer;
        }
    }
}
