/*
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
 * Listener interface for an event when the context menu is about to be
 * displayed. Listeners may then operate on the ContextMenuInvocationSettings
 * object stored in the ContextMenuEvent to effect temporary (this invocation
 * only) changes to the context menu. 'Permanent' changes that carry between
 * invocations should be made to the ContextMenuManager or ContextMenuComponent.
 * 
 * Threads can also use this event to perform any other special processing
 * before the menu is displayed.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 * @author mabonner
 */
public interface ContextMenuListener {

    /**
     * Indicates that the context menu is about to be displayed.
     *
     * @param event The ContextEvent that caused the menu to be displayed
     */
    public void contextMenuDisplayed(ContextMenuEvent event);
}
