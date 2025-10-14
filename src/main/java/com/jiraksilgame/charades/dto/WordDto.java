package com.jiraksilgame.charades.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WordDto {
    private Long id;
    private String text;
    private String description;
}
