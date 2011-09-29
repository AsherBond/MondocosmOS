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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import org.jdesktop.wonderland.client.input.Event;

/**
 * Event requesting that the avatar renderer collision/gravity mode changes
 *
 * @author paulby
 */
public class AvatarCollisionChangeRequestEvent extends Event {

    private boolean collisionResponseEnabled;
    private boolean gravityEnabled;

    public AvatarCollisionChangeRequestEvent(boolean collisionResponseEnabled, boolean gravityEnabled) {
        this.collisionResponseEnabled = collisionResponseEnabled;
        this.gravityEnabled = gravityEnabled;
    }

    @Override
    public Event clone(Event evt) {
        if (evt==null) {
            evt = new AvatarCollisionChangeRequestEvent(isCollisionResponseEnabled(), isGravityEnabled());
        } else {
            ((AvatarCollisionChangeRequestEvent)evt).collisionResponseEnabled = isCollisionResponseEnabled();
            ((AvatarCollisionChangeRequestEvent)evt).gravityEnabled = isGravityEnabled();
        }
        super.clone(evt);

        return evt;
    }

    /**
     * @return the collisionEnabled
     */
    public boolean isCollisionResponseEnabled() {
        return collisionResponseEnabled;
    }

    /**
     * @return the gravityEnabled
     */
    public boolean isGravityEnabled() {
        return gravityEnabled;
    }
}
