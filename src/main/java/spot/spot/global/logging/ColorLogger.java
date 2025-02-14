package spot.spot.global.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ColorLogger {
    // ANSI 색상 코드
    private static final String RED_COLOR = "\u001B[31m";   // 빨강
    private static final String GREEN_COLOR = "\u001B[32m"; // 녹색
    private static final String RESET_COLOR = "\u001B[0m";  // 색상 초기화

    // 빨강색 로그
    public static void red(String message, Object... args) {
        log.info(RED_COLOR + message + RESET_COLOR, args);
    }

    // 녹색 로그
    public static void green(String message, Object... args) {
        log.info(GREEN_COLOR + message + RESET_COLOR, args);
    }

    // 일반 로그 (Slf4j 사용)
    public static void info(String message, Object... args) {
        log.info(message, args);
    }

    public static void warn(String message, Object... args) {
        log.warn(message, args);
    }

    public static void error(String message, Object... args) {
        log.error(message, args);
    }
}
