package com.jiraksilgame.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 비밀번호 인증 요청 DTO
 */
@Getter
public class PasswordRequest {
    @NotBlank
    @Size(min = 4, max = 24)
    private String password;
}
