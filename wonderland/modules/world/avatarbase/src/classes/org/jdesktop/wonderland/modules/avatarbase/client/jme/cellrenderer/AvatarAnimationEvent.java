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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import imi.character.avatar.Avatar;
import org.jdesktop.wonderland.client.input.Event;

/**
 * Event which notifies listeners that an avatar animation has ended or
 * cycled.
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class AvatarAnimationEvent extends Event {
    public enum EventType {
        STARTED, STOPPED, LOOPED
    }

    private EventType type;
    private Avatar source;
    private String animationName;

    public AvatarAnimationEvent(EventType type, Avatar source,
                                String animationName)
    {
        this.type = type;
        this.source = source;
        this.animationName = animationName;
    }

    public EventType getType() {
        return type;
    }

    public Avatar getSource() {
        return source;
    }

    public String getAnimationName() {
        return animationName;
    }
    
    @Override
    public Event clone(Event evt) {
        if (evt == null) {
            evt = new AvatarAnimationEvent(getType(), getSource(),
                                           getAnimationName());
        } else {
            ((AvatarAnimationEvent) evt).type = getType();
            ((AvatarAnimationEvent) evt).source = getSource();
            ((AvatarAnimationEvent) evt).animationName = getAnimationName();
        }

        return super.clone(evt);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getType() + " " +
               getAnimationName() + " on " + getSource().getName();
    }
}
