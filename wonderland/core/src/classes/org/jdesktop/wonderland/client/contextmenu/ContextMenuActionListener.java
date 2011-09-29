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

/**
 * A listener for selections on the context menu.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface ContextMenuActionListener {

    /**
     * A context menu item has been selected.
     *
     * @param event The context item event
     */
    public void actionPerformed(ContextMenuItemEvent event);
}
