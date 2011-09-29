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
package org.jdesktop.wonderland.client.hud;

import java.util.Date;

/**
 * Defines events that apply to HUD objects.
 *
 * @author nsimpson
 */
public class HUDEvent {

    private HUDObject hudObject;
    private HUDEventType eventType;
    private Date eventTime;

    /**
     * Types of HUD object events
     */
    public enum HUDEventType {

        /**
         * A HUD object has been added
         */
        ADDED,
        /**
         * A HUD object has been removed
         */
        REMOVED,
        /**
         * A HUD object is visible
         */
        APPEARED,
        /**
         * A HUD object is visible in world
         */
        APPEARED_WORLD,
        /**
         * A HUD object is no longer visible
         */
        DISAPPEARED,
        /**
         * A HUD object is no longer visible in world
         */
        DISAPPEARED_WORLD,
        /**
         * A HUD object has changed display modes
         */
        CHANGED_MODE,
        /**
         * A HUD object has moved
         */
        MOVED,
        /**
         * A HUD object has moved in world
         */
        MOVED_WORLD,
        /**
         * A HUD object has resized
         */
        RESIZED,
        /**
         * A HUD object is minimized
         */
        MINIMIZED,
        /**
         * A HUD object is maximized
         */
        MAXIMIZED,
        /**
         * A HUD object is enabled
         */
        ENABLED,
        /**
         * A HUD object is disabled
         */
        DISABLED,
        /**
         * A HUD object's transparency changed
         */
        CHANGED_TRANSPARENCY,
        /**
         * A HUD object's name changed
         */
        CHANGED_NAME,
        /**
         * A HUD object's control state changed
         */
        CHANGED_CONTROL,
        /**
         * A HUD object has been closed
         */
        CLOSED
    };

    /**
     * Create a new instance of a HUD Event
     * @param hudObject the HUD object associated with this event
     */
    public HUDEvent(HUDObject hudObject) {
        this.hudObject = hudObject;
    }

    /**
     * Create a new instance of a HUD Event
     * @param hudObject the HUD object associated with this event
     * @param eventType the event type
     */
    public HUDEvent(HUDObject hudObject, HUDEventType eventType) {
        this.hudObject = hudObject;
        this.eventType = eventType;
    }

    /**
     * Create a new instance of a HUD Event
     * @param hudObject the HUD object associated with this event
     * @param eventType the event type
     * @param eventTime the time of the event
     */
    public HUDEvent(HUDObject hudObject, HUDEventType eventType, Date eventTime) {
        this.hudObject = hudObject;
        this.eventType = eventType;
        this.eventTime = eventTime;
    }

    /**
     * Clone constructor
     * @param event the event instance to clone
     */
    public HUDEvent(HUDEvent event) {
        this(event.getObject(), event.getEventType(), event.getEventTime());
    }

    
    /**
     * Sets the HUD object that triggered the event
     * @param hudObject the HUD object that triggered the event
     */
    public void setObject(HUDObject hudObject) {
        this.hudObject = hudObject;
    }

    /**
     * Gets the HUD object that triggered the event
     * @return the HUD object that triggered the event
     */
    public HUDObject getObject() {
        return hudObject;
    }

    /**
     * Sets the event type
     * @param eventType the type of the event
     */
    public void setEventType(HUDEventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the type of the event
     * @return the event type
     */
    public HUDEventType getEventType() {
        return eventType;
    }

    /**
     * Sets the time that the event occurred
     * @param eventTime the time of the event
     */
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * Gets the time the event occurred
     * @return the time of the event
     */
    public Date getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return "object: " + hudObject + ", event type: " + eventType +
                ", event time: " + eventTime;
    }
}
