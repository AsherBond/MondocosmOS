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
package org.jdesktop.wonderland.client.scenemanager.event;

import java.util.List;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.scenemanager.SceneManager;

/**
 * The base event class for all scene manager events. This base class manages
 * a list of Entities to which the scene event applies. In some cases (e.g.
 * hover event), there will be only one Entity, while there may be more than
 * one Entity for other events (e.g. selection event). The list of Entities is
 * ordered: the first Entity in the list was the first Entity selected.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class SceneEvent extends Event {

    /* The list of Entities associated with the context event */
    private List<Entity> entityList = null;

    /** Default constructor */
    public SceneEvent() {
    }

    /** Constructor, takes the list of Enitities of the context event. */
    public SceneEvent(List<Entity> entities) {
        this.entityList = entities;
    }

    /**
     * Returns an ordered list of Entities associated with the context event.
     * The first Entity in the list represents the first Entity selected.
     *
     * @return A list of selected Entities
     */
    public List<Entity> getEntityList() {
        return entityList;
    }

    /**
     * Returns the Cell corresonding to the given Entity, or null if there is
     * none.
     *
     * @param entity The entity for fetch the Cell for
     * @return The Cell
     */
    public static Cell getCellForEntity(Entity entity) {
        return SceneManager.getCellForEntity(entity);
    }

    /**
     * Returns the Entity associated with the "primary" selection. That is, if
     * multiple Entity's are selected, the "primary" Entity was the first one
     * selected. Returns null if none.
     *
     * @return The primary Entity, or null
     */
    public Entity getPrimaryEntity() {
        if (entityList != null && entityList.size() > 0) {
            return entityList.get(0);
        }
        return null;
    }

    /**
     * Returns the Cell associated with the "primary" selection. That is, if
     * multiple Entity's are selected, the "primary" Cell is the Cell associated
     * with the first Entity selected. Returns null if none.
     *
     * @return The primary Cell, or null
     */
    public Cell getPrimaryCell() {
        Entity entity = getPrimaryEntity();
        if (entity != null) {
            return getCellForEntity(entity);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone (Event event) {
        if (event == null) {
            event = new SceneEvent();
        }
        ((SceneEvent)event).entityList = entityList;
        return super.clone(event);
    }
}
