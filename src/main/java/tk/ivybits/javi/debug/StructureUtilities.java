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

import com.sun.jna.Structure;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

public class StructureUtilities {
    public static void printFields(Structure victim) {
        try {
            String name = victim.getClass().getName();
            name = name.substring(name.lastIndexOf(".") + 1);
            System.out.println(name + ":");
            System.out.println("  size: " + victim.size());
            System.out.println("  offsets:");
            Method getFieldOrder = Structure.class.getDeclaredMethod("getFieldOrder");
            getFieldOrder.setAccessible(true);
            Method fieldOffset = Structure.class.getDeclaredMethod("fieldOffset", String.class);
            fieldOffset.setAccessible(true);
            for (String field : (List<String>) getFieldOrder.invoke(victim)) {
                System.out.printf("    %-30s: %d\n", field, fieldOffset.invoke(victim, field));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void printFieldsC(Structure victim, OutputStream out) {
        try {
            String name = victim.getClass().getName();
            name = name.substring(name.lastIndexOf(".") + 1);
            System.out.println("puts(\"" + name + ":\");");
            System.out.println("printf(\"  size: %d\\n\", sizeof(" + name + "));");
            System.out.println("puts(\"  offsets:\");");
            Method getFieldOrder = Structure.class.getDeclaredMethod("getFieldOrder");
            getFieldOrder.setAccessible(true);
            for (String field : (List<String>) getFieldOrder.invoke(victim)) {
                System.out.println("printf(\"    %-30s: %d\\n\", \"" + field + "\", offsetof(" + name + ", " + field + "));");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
