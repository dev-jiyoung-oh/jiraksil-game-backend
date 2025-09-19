package com.jiraksilgame.wakeupmission.dto;

import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGame;
import com.jiraksilgame.wakeupmission.entity.WakeUpMissionGameMission;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** 기상 미션 조회 응답 DTO */
@Getter
public class WakeUpMissionResponse {
    private String code;
    private LocalDateTime wakeUpTime;
    private String contacts;
    private List<MissionDto> missions;

    public WakeUpMissionResponse(WakeUpMissionGame game, List<WakeUpMissionGameMission> gameMissions) {
        this.code = game.getCode();
        this.wakeUpTime = game.getWakeUpTime();
        this.contacts = game.getContacts();
        this.missions = gameMissions.stream()
                .map(m -> new MissionDto(m.getMission().getContent(), m.getAssignedPlayer()))
                .collect(Collectors.toList());
    }

    /** 배정된 미션 정보 DTO */
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
