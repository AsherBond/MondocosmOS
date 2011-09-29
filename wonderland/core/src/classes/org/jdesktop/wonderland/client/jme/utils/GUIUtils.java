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
package org.jdesktop.wonderland.client.jme.utils;

import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 *
 * @author nsimpson
 */
public class GUIUtils {

    private static final Logger logger = Logger.getLogger(GUIUtils.class.getName());

    public static void initLookAndFeel() {
        try {

            boolean hasNimbus = false;

            try {
                Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                hasNimbus = true;
            } catch (ClassNotFoundException e) {
            }

            // Workaround for bug 15: Embedded Swing on Mac: SwingTest: radio button image problems
            // For now, force the cross-platform (metal) LAF to be used, or Nimbus
            // Also workaround bug 10.
            if (hasNimbus) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }

            if ("Mac OS X".equals(System.getProperty("os.name"))) {
                //to workaround popup clipping on the mac we force top-level popups
                //note: this is implemented in scenario's EmbeddedPopupFactory
                javax.swing.UIManager.put("PopupFactory.forceHeavyWeight", Boolean.TRUE);
            }
        } catch (Exception ex) {
            logger.warning("Loading of " + UIManager.getCrossPlatformLookAndFeelClassName() + " look-and-feel failed, exception = " + ex);
        }
    }
}
