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
 * Event requesting that the avatar renderer be changed
 *
 * @author paulby
 */
public class AvatarRendererChangeRequestEvent extends Event {

    public enum AvatarQuality { High, Medium, Low };

    private AvatarQuality quality;


    public AvatarRendererChangeRequestEvent(AvatarQuality quality) {
        this.quality = quality;
    }

    public AvatarQuality getQuality() {
        return quality;
    }

    @Override
    public Event clone(Event evt) {
        if (evt==null) {
            evt = new AvatarRendererChangeRequestEvent(quality);
        } else {
            ((AvatarRendererChangeRequestEvent)evt).quality = quality;
        }
        super.clone(evt);

        return evt;
    }
}
