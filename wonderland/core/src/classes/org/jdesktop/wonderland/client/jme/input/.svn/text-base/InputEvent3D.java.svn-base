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
package org.jdesktop.wonderland.client.jme.input;

import java.awt.event.InputEvent;
import org.jdesktop.mtgame.PickInfo;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The abstract and super class for all the input events.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class InputEvent3D extends Event {

    private PickInfo pickInfo;
    /** The originating AWT input event. */
    protected InputEvent awtEvent;

    /**
     * Enum values which indicate which modifiers keys are held down for this event.
     */
    public enum ModifierId {

        ALT, ALT_GRAPH, CTRL, META, SHIFT, BUTTON1, BUTTON2, BUTTON3,
    };
    private static final ModifierConvTable[] modifierConvTable = {
        new ModifierConvTable(InputEvent.ALT_DOWN_MASK, ModifierId.ALT),
        new ModifierConvTable(InputEvent.ALT_GRAPH_DOWN_MASK, ModifierId.ALT_GRAPH),
        new ModifierConvTable(InputEvent.CTRL_DOWN_MASK, ModifierId.CTRL),
        new ModifierConvTable(InputEvent.META_DOWN_MASK, ModifierId.META),
        new ModifierConvTable(InputEvent.SHIFT_DOWN_MASK, ModifierId.SHIFT),
        new ModifierConvTable(InputEvent.BUTTON1_DOWN_MASK, ModifierId.BUTTON1),
        new ModifierConvTable(InputEvent.BUTTON2_DOWN_MASK, ModifierId.BUTTON2),
        new ModifierConvTable(InputEvent.BUTTON3_DOWN_MASK, ModifierId.BUTTON3),};

    /** Default constructor (for cloning) */
    protected InputEvent3D() {
    }

    /**
     * Create a new instance of Event3D.
     * @param awtEvent The AWT input event which generates this 3D input event.
     */
    public InputEvent3D(InputEvent awtEvent) {
        this.awtEvent = awtEvent;
    }

    /**
     * Returns the ID of the associated AWT input event.
     */
    public int getID() {
        return awtEvent.getID();
    }

    /**
     * Returns the associated AWT input event
     */
    public InputEvent getAwtEvent() {
        return awtEvent;
    }

    /** 
     * Returns the time stamp of when this event occurred
     * @return time stamp for event
     */
    public long getWhen() {
        return getAwtEvent().getWhen();
    }

    /**
     * Returns a set of the extended modifiers this event. Extended modifiers 
     * represent the state of all modal keys, such as ALT, CTRL, META, 
     * and the mouse buttons just after the event occurred.
     * 
     * @param ret  an array to fill in the resutl.  If it is too small,
     *             a new array with enough size will be allocated.
     * @return an array of ModifierId objects that represents the modifiers
     *         status.
     * @see java.awt.event.InputEvent
     */
    public ModifierId[] getModifiersEx(ModifierId[] ret) {

        int modifiers = getAwtEvent().getModifiersEx();
        int numBits = 0;
        int tmpMod = modifiers;

        while (tmpMod > 0) {
            numBits += (tmpMod & 0x1);
            tmpMod /= 2;
        }

        if (ret == null || numBits > ret.length) {
            ret = new ModifierId[numBits];
        }

        int idx = 0;
        for (ModifierConvTable entry : modifierConvTable) {
            if ((modifiers & entry.modifierAwt) > 0) {
                assert (idx < ret.length);
                ret[idx] = entry.modifier3D;
                idx++;
            }
        }
        // null-out the rest
        for (; idx < ret.length; idx++) {
            ret[idx] = null;
        }

        return ret;
    }

    private static class ModifierConvTable {

        int modifierAwt;
        ModifierId modifier3D;

        ModifierConvTable(int awtModifier, ModifierId modifier3D) {
            this.modifierAwt = awtModifier;
            this.modifier3D = modifier3D;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Event clone(Event event) {
        // Note: doesn't create an event because this class is abstract
        ((InputEvent3D) event).awtEvent = awtEvent;
        ((InputEvent3D) event).pickInfo = pickInfo;
        return event;
    }

    /**
     * INTERNAL ONLY.
     * <br>
     * Used by the input system to specify the pickInfo for this input event.
     */
    public void setPickInfo(PickInfo pickInfo) {
        this.pickInfo = pickInfo;
    }

    /**
     * INTERNAL ONLY.
     * <br>
     * Returns the pickInfo for this event. This provides full event information to Wonderland components
     * which need it, such as the World Builder.
     */
    public PickInfo getPickInfo() {
        return pickInfo;
    }
}
