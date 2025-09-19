package com.jiraksilgame.wakeupmission.dto;

import lombok.Getter;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 기상 미션 생성 요청 DTO
 */
@Getter
public class CreateWakeUpMissionRequest {

    /** 플레이어 수(필수) */
    @Min(1) @Max(30)
    private int numPlayers;

    /** HH:mm (선택) */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime wakeUpTime;

    /** 연락처(선택, 쉼표로 구분) */
    @Size(max = 1000)
    private String contacts;

    /** 비밀번호(필수) */
    @NotBlank
    @Size(min = 4, max = 24)
    private String password;
}
