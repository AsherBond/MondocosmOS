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
package org.jdesktop.wonderland.client.contextmenu;

import java.awt.Image;

/**
 * A "simple" context menu item that displays a name and (optional) label as
 * a normal menu item.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class SimpleContextMenuItem extends ContextMenuItem {

    private Image image = null;
    private ContextMenuActionListener listener = null;

    public SimpleContextMenuItem(String label, Image image,
            ContextMenuActionListener listener) {

        super(label);
        this.image = image;
        this.listener = listener;
    }

    public SimpleContextMenuItem(String label, ContextMenuActionListener listener) {
        this(label, null, listener);
    }

    /**
     * Returns the image of the simple menu item.
     *
     * @return An Image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image of the simple menu item.
     *
     * @param image The new Image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Returns the listener for the menu item action.
     *
     * @return The menu item action listener
     */
    public ContextMenuActionListener getActionListener() {
        return listener;
    }
}
