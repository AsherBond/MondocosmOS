/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.audiomanager.client;

import java.util.Arrays;
import java.util.Comparator;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode;

public class SortUsers {

    public static void sort(String[] list) {
        Arrays.sort(list, new Comparator<String>() {
            public int compare(String s1, String s2) {
		if (s1.startsWith(NameTagNode.LEFT_MUTE)) {
		    s1 = s1.substring(1);
		}

		if (s2.startsWith(NameTagNode.LEFT_MUTE)) {
		    s2 = s2.substring(1);
		}
                
		return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
            }
        });
    }

}
