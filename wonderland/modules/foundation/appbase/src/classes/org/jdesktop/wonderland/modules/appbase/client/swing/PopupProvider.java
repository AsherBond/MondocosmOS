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
package org.jdesktop.wonderland.modules.appbase.client.swing;

import java.awt.Component;
import javax.swing.Popup;

/**
 * A provider that handles creating popups for a particular window.
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public interface PopupProvider {
    /**
     * Create a new popup for the given content at the given location
     * @param contents the contents of the popup window
     * @param x the x coordinate to display the popup
     * @param y the y coordinate to display the popup
     * @return a popup for the given content at the given location
     */
    public Popup getPopup(Component contents, int x, int y);
}
