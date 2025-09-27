package com.jiraksilgame.charades;

public class CharadesConstants {
    private CharadesConstants() {}

    /** 한 게임 내 최대 팀 수 (A~Z) */
    public static final int MAX_TEAMS = 26;

    /** 단어 배치 API의 최대 반환 수 */
    public static final int MAX_WORD_BATCH = 1000;

    /** 팀 코드 규칙 (A~Z 한 글자) */
    public static final String TEAM_CODE_REGEX = "^[A-Z]$";
}
