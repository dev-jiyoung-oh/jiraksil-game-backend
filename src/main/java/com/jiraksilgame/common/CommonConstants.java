package com.jiraksilgame.common;

/** 공통 상수 */
public final class CommonConstants {
  private CommonConstants() {}

  /** 게임 코드(외부 노출용) 길이 */
  public static final int GAME_CODE_LENGTH = 12;

  /** 게임 코드 규칙 (A~Z,0~9) */
  public static final String GAME_CODE_REGEX = "^[A-Z0-9]{" + GAME_CODE_LENGTH + "}$";
}
