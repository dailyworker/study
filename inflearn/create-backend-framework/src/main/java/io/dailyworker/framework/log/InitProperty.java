package io.dailyworker.framework.log;

import java.util.ResourceBundle;

public class InitProperty {
    private static String logDirPattern = null;
    private static String logFilePattern = null;
    private static String traceConfigUri = null;
    private static boolean isWriteLog = false;

    private InitProperty() {
        throw new IllegalStateException("유틸리티 성 클래스 입니다.");
    }

    static {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("init");

            traceConfigUri = resourceBundle.getString("TRACE_CONFIG_URL");
            logDirPattern = resourceBundle.getString("LOG_DIR_PATTERN");
            logFilePattern= resourceBundle.getString("LOG_FILE_PATTERN");
            isWriteLog = Boolean.parseBoolean(resourceBundle.getString("IS_WRITE_LOG"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String traceConfigUri() {
        return traceConfigUri;
    }

    public static String logFilePattern() {
        return logFilePattern;
    }

    public static String logDirPattern() {
        return logDirPattern;
    }

    public static boolean isWriteLog() {
        return isWriteLog;
    }

}
