package com.jiraksilgame.dto;

import lombok.Getter;
import java.time.LocalTime;

@Getter
public class CreateWakeUpMissionRequest {
    private int numPlayers;
    private LocalTime wakeUpTime; // 요청은 HH:mm 형식으로 받음
    private String contacts;
    private String password;
}
