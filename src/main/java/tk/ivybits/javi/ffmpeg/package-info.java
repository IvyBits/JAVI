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

/**
 * FFmpeg library bindings for JNA.
 *
 * JAVI binds {@code libavcodec}, {@code libavformat}, {@code libavutil},
 * {@code libswresample} and {@code libswscale}.
 * <p/>
 * <b>Using these bindings directly is greatly discouraged,
 * as they are volatile and may change from one build to the next.</b>
 * The functions being bound will always be kept to a minimum, meaning they can spontaneously disappear at any time,
 * and parameters can be changed at any time to make internal code look better.
 * </p>
 * Consider using the public JAVI API in {@code tk.ivybits.javi.media} instead.
 *
 * @since 1.0
 * @version 1.0
 */
package tk.ivybits.javi.ffmpeg;