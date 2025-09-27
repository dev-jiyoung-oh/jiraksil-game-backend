package com.jiraksilgame.charades.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum TeamColor {
    BLUE("#3B82F6"),
    RED("#EF4444"),
    GREEN("#10B981"),
    YELLOW("#F59E0B"),
    PURPLE("#A855F7"),
    ORANGE("#F97316"),
    PINK("#EC4899");

    private final String hex;

    // 기본 팔레트(모든 색 사용)
    public static List<TeamColor> defaultPalette() {
        return List.of(values());
    }

    // n번째 팀 색상(부족하면 순환)
    public static TeamColor nth(int index) {
        TeamColor[] all = values();
        return all[Math.floorMod(index, all.length)];
    }

    // 토큰 → enum (대소문자/공백 허용)
    public static Optional<TeamColor> fromToken(String token) {
        if (token == null) return Optional.empty();
        try {
            return Optional.of(TeamColor.valueOf(token.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // 토큰을 안전하게 HEX로 변환 (미등록이면 null)
    public static String hexOf(String token) {
        return fromToken(token).map(TeamColor::getHex).orElse(null);
    }

    // 토큰 → HEX (미등록이면 fallback 반환)
    public static String hexOrDefault(String token, String fallback) {
        return fromToken(token).map(TeamColor::getHex).orElse(fallback);
    }
}
