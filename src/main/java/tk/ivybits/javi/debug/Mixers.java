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

package tk.ivybits.javi.debug;

import javax.sound.sampled.*;

public class Mixers {
    public static void main(String[] args) throws LineUnavailableException {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class,
                new AudioFormat(44100, 16, 2, true, false));
        for (Mixer.Info i : mixers) {
            Mixer mixer = AudioSystem.getMixer(i);
            if (mixer.isLineSupported(lineInfo)) {
                System.out.println(i.getName() + " " + i.getVersion() + " by " + i.getVendor());
                System.out.println("\t" + i.getDescription());
            }
        }
    }
}
