/*
 * This file is part of JAVI.
 *
 * JAVI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * JAVI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JAVI.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

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
