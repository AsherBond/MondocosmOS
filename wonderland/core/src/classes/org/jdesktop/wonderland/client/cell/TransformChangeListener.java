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
package org.jdesktop.wonderland.client.cell;

/**
 * Listener for tracking cell transform changes
 * 
 * TODO Unify this interface with MovableComponent.CellMoveListener
 * 
 * @author paulby
 */
public interface TransformChangeListener {

    /**
     * The source, or originator of a transform change.
     */
    public enum ChangeSource {
        /**
         * The change was originated from the local client
         */
        LOCAL,

        /**
         * Change was originated from a remote client
         */
        REMOTE,

        /**
         * The change was originated by the server because a previous
         * transform was illegal
         */
        SERVER_ADJUST };

    /**
     * Called when the cells transform has changed.
     * 
     * @param cell the cells whos transform has changed
     * @param changeSource the source or originator of the change
     */
    public void transformChanged(Cell cell, ChangeSource source);
}
