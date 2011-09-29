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

package org.jdesktop.wonderland.client.cell;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.InteractionComponentClientState;

/**
 * Component describing how a cell interacts with the world
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class InteractionComponent extends CellComponent {
    private final Collection<InteractionComponentListener> listeners =
            new CopyOnWriteArraySet<InteractionComponentListener>();

    private boolean collidable = true;
    private boolean selectable = true;

    public InteractionComponent(Cell cell) {
        super (cell);
    }

    public boolean isCollidable() {
        return collidable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setClientState(CellComponentClientState state) {
        collidable = ((InteractionComponentClientState) state).isCollidable();
        selectable = ((InteractionComponentClientState) state).isSelectable();

        super.setClientState(state);

        // notify listeners
        fireCollidableChanged(collidable);
        fireSelectableChanged(selectable);
    }

    /**
     * Add a listener that will be notified of changes to this component.
     * @param listener the listener to add
     */
    public void addInteractionComponentListener(InteractionComponentListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a change listener
     * @param listener the listener to remove
     */
    public void removeInteractionComponentListener(InteractionComponentListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Fire an event notifying listeners that collidable has changed
     * @param collidable the new collidable value
     */
    protected void fireCollidableChanged(boolean collidable) {
        for (InteractionComponentListener listener : listeners) {
            listener.collidableChanged(collidable);
        }
    }
    
    /**
     * Fire an event notifying listeners that selectable has changed
     * @param selectable the new selectable value
     */
    protected void fireSelectableChanged(boolean selectable) {
        for (InteractionComponentListener listener : listeners) {
            listener.selectableChanged(selectable);
        }
    }

    /**
     * Listener for receiving notification of changes to the interaction
     * component
     */
    public interface InteractionComponentListener {
        /**
         * Notification that the collidable property of the cell has
         * changed.
         * @param collidable whether or not the cell should respond to
         * collisions
         */
        public void collidableChanged(boolean collidable);

        /**
         * Notification that the selectable property of the cell has
         * changed.
         * @param selectable whether or not the cell should respond to
         * selection
         */
        public void selectableChanged(boolean selectable);
    }
}
