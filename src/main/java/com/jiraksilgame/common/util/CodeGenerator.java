package com.jiraksilgame.common.util;

import java.security.SecureRandom;

/** 외부 노출용 게임 코드/토큰 등 짧은 랜덤 코드 생성 */
public final class CodeGenerator {
  private static final char[] ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
  private static final SecureRandom RND = new SecureRandom();
  
  private CodeGenerator() {}

  public static String randomCode(int len) {
    char[] buf = new char[len];
    for (int i = 0; i < len; i++) {
      buf[i] = ALPHANUM[RND.nextInt(ALPHANUM.length)];
    }
    return new String(buf);
  }
}
