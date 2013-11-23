package tk.ivybits.javi.log;

import tk.ivybits.javi.ffmpeg.LibAVUtil;

/**
 * Logging level control.
 *
 * @version 1.0
 * @since 1.0
 */
public enum LogLevel {
    LOG_QUIET(-8),
    LOG_PANIC(0),
    LOG_FATAL(8),
    LOG_ERROR(16),
    LOG_WARNING(24),
    LOG_INFO(32),
    LOG_VERBOSE(40),
    LOG_DEBUG(48);

    private int internal;

    LogLevel(int internal) {
        this.internal = internal;
    }

    /**
     * Sets the FFmpeg logging level.
     *
     * @param level The level to set to.
     * @since 1.0
     */
    public static void setLogLevel(LogLevel level) {
        LibAVUtil.av_log_set_level(level.internal);
    }

    /**
     * Fetches the current logging level.
     *
     * @return The level.
     * @since 1.0
     */
    public static LogLevel getLogLevel() {
        return LogLevel.values()[LibAVUtil.av_log_get_level() / 8 + 1];
    }
}
