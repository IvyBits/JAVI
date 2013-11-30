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

package tk.ivybits.javi.exc;

/**
 * Signals that an error has occured while streaming.
 *
 * @since 1.0
 */
public class StreamException extends RuntimeException {
    /**
     * Constructs an StreamException with no detail message.
     * A detail message is a String that describes this particular
     * exception.
     */
    public StreamException() {
        super();
    }

    /**
     * Constructs an StreamException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param err the String that contains a detailed message
     */
    public StreamException(String err) {
        super(err);
    }
}
