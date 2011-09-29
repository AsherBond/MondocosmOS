/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.scenemanager;

import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.InteractionComponent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.jme.input.InputEvent3D.ModifierId;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEnterExitEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D.ButtonId;

/**
 * Implements a simple selection policy based upon the JME input mechanism.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class DefaultSceneManagerPolicy implements SceneManagerPolicy {

    public boolean isClearedSelection(Event event) {
        // If the event carries no Entity (happens on the background), or if
        // the Entity that is clicked is not-selectable (NOT YET IMPLEMENTED)
        // then clear the existing selection.
        if (event instanceof MouseButtonEvent3D) {
            MouseButtonEvent3D mbe = (MouseButtonEvent3D)event;
            return mbe.isPressed() == true && mbe.getEntity() == null &&
                    mbe.getButton() == ButtonId.BUTTON1;
        }
        return false;
    }

    public boolean isSingleSelection(Event event) {
        // If the event carries an Entity that is selectable (NOT YET IMPLEMENTED)
        // and it is a left-mouse button press with no shift key
        if (event instanceof MouseButtonEvent3D) {
            MouseButtonEvent3D mbe = (MouseButtonEvent3D)event;
            return mbe.isPressed() == true && mbe.getEntity() != null &&
                    mbe.getButton() == ButtonId.BUTTON1 &&
                    mbe.getAwtEvent().isShiftDown() == false &&
                    isSelectable(mbe.getEntity());
        }
        return false;
    }

    public boolean isMultiSelection(Event event) {
        // If the event carries an Entity that is selectable (NOT YET IMPLEMENTED)
        // and it is a left-mouse button press with a shift key
        if (event instanceof MouseButtonEvent3D) {
            MouseButtonEvent3D mbe = (MouseButtonEvent3D)event;
            return mbe.isPressed() == true && mbe.getEntity() != null &&
                    mbe.getButton() == ButtonId.BUTTON1 &&
                    mbe.getAwtEvent().isShiftDown() == true &&
                    isSelectable(mbe.getEntity());
        }
        return false;        
    }

    public boolean isActivation(Event event) {
        // If the event carries an Entity that can be activated (N.Y.I) and
        // is a double-click with the left-mouse button
        if (event instanceof MouseButtonEvent3D) {
            MouseButtonEvent3D mbe = (MouseButtonEvent3D)event;
            return mbe.isClicked() == true && mbe.getEntity() != null &&
                    mbe.getButton() == ButtonId.BUTTON1 &&
                    mbe.getClickCount() == 2;
        }
        return false;
    }

    public boolean isHoverInterrupt(Event event) {
        // Any mouse event will interrupt a hover
        if (event instanceof MouseEvent3D) {
            return true;
        }
        return false;        
    }

    public long getHoverDelay() {
        return 2000;
    }
    
    public boolean isContext(Event event) {
        // If the event is a press of the right-mouse button and there is an
        // Entity. Look to see if any modifiers are present on the event, and
        // if so, then ignore the event.
        if (event instanceof MouseButtonEvent3D) {
            // If the event is not pressed and not for button 3 then ignore
            MouseButtonEvent3D mbe = (MouseButtonEvent3D) event;
            ButtonId button = mbe.getButton();
            if (mbe.isPressed() == false || button != ButtonId.BUTTON3) {
                return false;
            }

            // Check to see if there are any modifiers present. If so, then
            // ignore. The only modifier that should be present is BUTTON3
            ModifierId[] ids = mbe.getModifiersEx(null);
            for (ModifierId id : ids) {
                if (id != ModifierId.BUTTON3) {
                    return false;
                }
            }

            // is the entity selectable?
            if (mbe.getEntity() != null) {
                return isSelectable(mbe.getEntity());
            }

            return true;
        }
        return false;
    }

    public boolean isEnter(Event event) {
        // If the event carries an Entity and is a mouse enter event and the
        // Entity can be selected (N.Y.I)
        if (event instanceof MouseEnterExitEvent3D) {
            MouseEnterExitEvent3D mee = (MouseEnterExitEvent3D)event;
            return mee.isEnter() == true;
        }
        return false;
    }

    public boolean isExit(Event event) {
        // If the event carries an Entity and is a mouse exit event and the
        // Entity can be selected (N.Y.I)
        if (event instanceof MouseEnterExitEvent3D) {
            MouseEnterExitEvent3D mee = (MouseEnterExitEvent3D)event;
            return mee.isEnter() == false;
        }
        return false;  
    }

    /**
     * Return true if an entity is eligible for selection, or false if not.
     * This method maps the entity to a cell. If the cell exists and contains
     * an InteractionComponent that sets selectable to false, the entity
     * is not considered for selection.
     *
     * @param e the entity to check
     * @return true if the entity is selectable, and false if not
     */
    private boolean isSelectable(Entity e) {
        Cell cell = SceneManager.getCellForEntity(e);
        if (cell == null) {
            return true;
        }

        InteractionComponent ic = cell.getComponent(InteractionComponent.class);
        if (ic == null) {
            return true;
        }

        return ic.isSelectable();
    }
}
