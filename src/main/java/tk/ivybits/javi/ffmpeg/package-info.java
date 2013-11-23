/**
 * FFmpeg library bindings for JNA.
 *
 * JAVI binds <code>libavcodec</code>, <code>libavformat</code>, <code>libavutil</code>,
 * <code>libswresample</code> and <code>libswscale</code>.
 * <p/>
 * <b>Using these bindings directly is greatly discouraged,
 * as they are volatile and may change from one build to the next.</b>
 * The functions being bound will always be kept to a minimum, meaning they can spontaneously disappear at any time,
 * and parameters can be changed at any time to make internal code look better.
 * </p>
 * Consider using the public JAVI API in <code>tk.ivybits.javi.media</code> instead.
 *
 * @since 1.0
 * @version 1.0
 */
package tk.ivybits.javi.ffmpeg;