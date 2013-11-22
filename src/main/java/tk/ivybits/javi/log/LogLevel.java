package tk.ivybits.javi.log;

import tk.ivybits.javi.ffmpeg.LibAVUtil;

public enum LogLevel {
    LOG_QUIET,
    LOG_PANIC,
    LOG_FATAL,
    LOG_ERROR,
    LOG_WARNING,
    LOG_INFO,
    LOG_VERBOSE,
    LOG_DEBUG;

    public static void setLogLevel(LogLevel level) {
        LibAVUtil.av_log_set_level((level.ordinal() * 8 - 8));
    }

    public static LogLevel getLogLevel() {
        return LogLevel.values()[LibAVUtil.av_log_get_level() / 8 + 1];
    }
}
