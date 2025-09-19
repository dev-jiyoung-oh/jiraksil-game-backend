package com.jiraksilgame.wakeupmission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordRequest {
    @NotBlank
    @Size(min = 4, max = 24)
    private String password;
}
