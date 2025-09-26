package com.jiraksilgame.common.util;

import java.security.SecureRandom;

/**
 * 게임 코드 생성 유틸
 */
public final class CodeGenerator {
    private static final char[] ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RND = new SecureRandom();
    
    private CodeGenerator() {}

    /**
     * 주어진 길이(len)만큼 랜덤 코드 생성
     * <p>SecureRandom 사용, 고유성은 호출 측에서 보장</p>
     * 
     * @param len 생성할 코드 길이
     * @return 생성된 코드(A~Z, 0~9로 구성된 랜덤 문자열)
     */
    public static String randomCode(int len) {
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) {
            buf[i] = ALPHANUM[RND.nextInt(ALPHANUM.length)];
        }
        return new String(buf);
    }
}
