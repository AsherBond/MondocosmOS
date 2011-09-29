/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

package org.jdesktop.wonderland.modules.hud.client;

import javax.swing.JComponent;

/**
 * Popup window in the hud
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
class HUDPopup2D extends HUDComponent2D {
    private final HUDWindow parent;
    private final int xOffset;
    private final int yOffset;

    public HUDPopup2D(JComponent component, HUDWindow parent, int xOffset, int yOffset) {
        super (component);

        this.parent = parent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public HUDWindow getParent() {
        return parent;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }
}
