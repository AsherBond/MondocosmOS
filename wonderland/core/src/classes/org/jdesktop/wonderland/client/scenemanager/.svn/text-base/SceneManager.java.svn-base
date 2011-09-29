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

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassFocusListener;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.input.EventListener;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.CellRefComponent;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.scenemanager.event.ActivatedEvent;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.client.scenemanager.event.EnterExitEvent;
import org.jdesktop.wonderland.client.scenemanager.event.HoverEvent;
import org.jdesktop.wonderland.client.scenemanager.event.SceneEvent;
import org.jdesktop.wonderland.client.scenemanager.event.SelectionEvent;

/**
 * Manages the global "selection" in Wonderland. The Selection Manager handles
 * all sorts of interactions with Entities in the world. The selection manager
 * sits above the basic input mechanism and adds interpretation in terms of
 * "actions" in the Wonderland context.
 * <ol>
 * <li>Selection: The selection of an Entity in the world. This includes
 * selecting one or more Entities in the world. A selected Entity(s) will be
 * indicated in some way. This also involves deselecting (clearing) any of
 * the Entities.
 * <li>Context: A Context action is one that depends upon the set of Entities
 * that are selected. Typically a menu pops up with further actions possible.
 * <li>Enter/Exit: An Enter/Exit action is where the pointer enters or exits an
 * Entity, typically with the mouse pointer.
 * <li>Hover: A Hover action is where the pointer hovers over an Entity (typically
 * by hovering the mouse). This includes the "start" hovering and the "stop"
 * hovering.
 * <li>Activation: The activation of an Entity in the world. For example, when
 * a user double-clicks on an Entity, it is activated.
 * </ol>
 * Threads can register listeners on the scene manager for these actions.
 * There are methods to fetch the collection of selected entities and the
 * Entity where there is a hover. Threads can also manually clear the selected
 * Entities.
 * <p>
 * The Selection Manager acts on individual Entities, so multiple Entities with
 * a Cell can be individually selected. Threads can fetch the Cell object
 * associated with an Entity via the getCellForEntity() method.
 * <p>
 * Which mouse and keyboard events correspond to the Selection Manager actions
 * are controlled via a "selection policy" as defined by a class that implements
 * the SceneManagerPolicy interface.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class SceneManager {
    /* The selection policy */
    private SceneManagerPolicy policy = new DefaultSceneManagerPolicy();
    
    /*
     * A bunch of member variables to keep track of the hover process: the
     * hoverEntity tracks the entity we are currently hovering over if there
     * is a current hover, a hoverTimer that keeps track of the timer most
     * recently launched, and a hoverStartTime that tracks when the timer is
     * launched. This last field is used to determine whether the hover was
     * cancelled during the execution of the hover timer task run() method
     */
    protected Entity hoverEntity = null;
    protected Timer hoverTimer = null;
    protected long hoverStartTime = -1;
    
    /* The Entity that is "entered" or null if none */
    private Entity enterEntity = null;
    
    /*
     * An ordered list of selected entities, in the order that they were
     * selected.
     */
    private Set<Entity> selectedEntityList;
    
    /** Default Constructor */
    public SceneManager() {
        selectedEntityList = Collections.synchronizedSet(new LinkedHashSet());
        InputManager.inputManager().addGlobalEventListener(new MouseEventListener());
        
        // Uncomment the following line to see an example listener
//        addSceneListener(new MySelectionListener());
    }
    
    /**
     * Singleton to hold instance of SceneManager. This holder class is
     * loader on the first execution of SceneManager.getSelectionManager().
     */
    private static class SelectionManagerHolder {
        private final static SceneManager manager = new SceneManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final SceneManager getSceneManager() {
        return SelectionManagerHolder.manager;
    }
    
    /**
     * Provides a raw input event to process
     */
    protected void inputEvent(Event event) {
        InputManager inputManager = InputManager.inputManager();        
        Entity entity = event.getEntity();
                        
        // Implement hover. We check if the event interrupts hover and we
        // restart the timer. If we kill a timer, there may be an executing
        // hover task run() method. This may lead to a race condition where
        // the run() method has been called at the same time a hover interrupt
        // event happens. To fix this, we keep track of when we kicked off
        // the last timer event.
        if (policy.isHoverInterrupt(event) == true) {
            synchronized(this) {
                // Cancel the current timer (although a run() method of the timer
                // may still happen as a race condition
                if (hoverTimer != null) {
                    hoverTimer.cancel();
                }
                
                // If there is a currently hovering entity, then send a stop
                // event
                MouseEvent mouseEvent = (MouseEvent) ((MouseEvent3D) event).getAwtEvent();
                if (hoverEntity != null) {
                    inputManager.postEvent(new HoverEvent(hoverEntity, false, mouseEvent));
                    hoverEntity = null;
                }
                
                // Update the hover start time. This will cause any remaining
                // timer tasks that may have been run to ignore themselves
                hoverStartTime = System.currentTimeMillis();
                
                // Launch a new timer task, but not unless we are actually over
                // a non-null entity
                if (entity != null) {
                    HoverTimerTask task = new HoverTimerTask(entity, hoverStartTime, mouseEvent);
                    hoverTimer = new Timer();
                    hoverTimer.schedule(task, policy.getHoverDelay());
                }
            }
        }
        
        if (policy.isClearedSelection(event) == true) {
            // If we wish to clear the selection, then simply clear out the
            // list and fire an event
            selectedEntityList.clear();
            inputManager.postEvent(new SelectionEvent(new LinkedList(selectedEntityList)));
            return;
        }
        else if (policy.isSingleSelection(event) == true) {
            // issue #1115: ignore the event if it is selecting an object that
            // is already selected
            if (selectedEntityList.contains(entity) == true) {
                return;
            }

            // Clear out the list, add the new Entity and fire an event
            selectedEntityList.clear();
            selectedEntityList.add(entity);
            inputManager.postEvent(new SelectionEvent(new LinkedList(selectedEntityList)));
            return;
        }
        else if (policy.isMultiSelection(event) == true) {
            // If the Entity is already selected, then remove it from the
            // selection list. If not already present, then add it
            // Reset the selection possible. If the entity is not selected,
            // then select it (only). If the entity is selected, then do
            // nothing (if it is part of a group of selected entities)
            if (selectedEntityList.contains(entity) == false) {
                selectedEntityList.add(entity);
            }
            else {
                selectedEntityList.remove(entity);
            }
            inputManager.postEvent(new SelectionEvent(new LinkedList(selectedEntityList)));
            return;
        }
        else if (policy.isEnter(event) == true) {
            enterEntity = entity;
            inputManager.postEvent(new EnterExitEvent(entity, true));
        }
        else if (policy.isExit(event) == true) {
            Entity eventEntity = enterEntity;
            enterEntity = null;
            inputManager.postEvent(new EnterExitEvent(eventEntity, false));
        }
        else if (policy.isActivation(event) == true) {
            inputManager.postEvent(new ActivatedEvent(entity));
        }
        else if (policy.isContext(event) == true) {
            // We use the context event to clear the selected entity list first
            selectedEntityList.clear();

            // If there is an entity for the mouse event, then add the entity to
            // the list and pass a Selection event too.
            if (entity != null) {
                selectedEntityList.add(entity);
                LinkedList entityList = new LinkedList(selectedEntityList);
                inputManager.postEvent(new SelectionEvent(entityList));
            }

            // Pass the mouse event for now so we know where the event was
            // fired. We sent this even if the entity is null, so the context
            // menu can be cleared.
            LinkedList entityList = new LinkedList(selectedEntityList);
            MouseEvent mouseEvent = (MouseEvent) ((MouseEvent3D) event).getAwtEvent();
            inputManager.postEvent(new ContextEvent(entityList, mouseEvent));
        }
    }

    /**
     * Posts a Scene Manager event to this system. This method can be used to
     * 'fake' such an event. This method will re-post the event to the input
     * manager for others to handle.
     *
     * @param event The scene event to post to the system
     */
    public void postEvent(SceneEvent event) {
        InputManager inputManager = InputManager.inputManager();
        Entity entity = ((SceneEvent)event).getPrimaryEntity();

        // Much of this code duplicates the inputEvent() method -- there seems
        // to be no way around this duplication for now.
        
        // Implement hover. We check if the event interrupts hover and we
        // restart the timer. If we kill a timer, there may be an executing
        // hover task run() method. This may lead to a race condition where
        // the run() method has been called at the same time a hover interrupt
        // event happens. To fix this, we keep track of when we kicked off
        // the last timer event.
        if (event instanceof HoverEvent) {
            synchronized(this) {
                // Cancel the current timer (although a run() method of the timer
                // may still happen as a race condition
                if (hoverTimer != null) {
                    hoverTimer.cancel();
                }

                // If there is a currently hovering entity, then send a stop
                // event
                MouseEvent mouseEvent = (MouseEvent) ((HoverEvent) event).getMouseEvent();
                if (hoverEntity != null) {
                    inputManager.postEvent(new HoverEvent(hoverEntity, false, mouseEvent));
                    hoverEntity = null;
                }

                // Update the hover start time. This will cause any remaining
                // timer tasks that may have been run to ignore themselves
                hoverStartTime = System.currentTimeMillis();

                // Launch a new timer task, but not unless we are actually over
                // a non-null entity
                if (entity != null) {
                    HoverTimerTask task = new HoverTimerTask(entity, hoverStartTime, mouseEvent);
                    hoverTimer = new Timer();
                    hoverTimer.schedule(task, policy.getHoverDelay());
                }
            }
        }

        // If a selection event, then set the list of entities and re-post the
        // event.
        if (event instanceof SelectionEvent) {
            // If a selection event, then set the list of entities and re-port
            // the event
            selectedEntityList.clear();
            selectedEntityList.addAll(event.getEntityList());
            inputManager.postEvent(new SelectionEvent(new LinkedList(selectedEntityList)));
            return;
        }

        // If an enter/exit event, then note the Entity we are entering or
        // exiting and repost the event.
        if (event instanceof EnterExitEvent) {
            if (((EnterExitEvent)event).isEnter() == true) {
                enterEntity = entity;
                inputManager.postEvent(new EnterExitEvent(entity, true));
            }
            else {
                Entity eventEntity = enterEntity;
                enterEntity = null;
                inputManager.postEvent(new EnterExitEvent(eventEntity, false));
            }
            return;
        }

        // If an activation event, the simply repost the event
        if (event instanceof ActivatedEvent) {
            inputManager.postEvent(new ActivatedEvent(entity));
            return;
        }

        // If a context event, then set the list of entities associated with
        // the context and re-post the event.
        if (event instanceof ContextEvent) {
            Logger.getLogger(SceneManager.class.getName()).warning("RECEIVED CONTEXT EVENT " +
                    entity.getName());
            // We use the context event to clear the selected entity list first
            selectedEntityList.clear();

            // If there is an entity for the mouse event, then add the entity to
            // the list and pass a Selection event too.
            if (entity != null) {
                selectedEntityList.add(entity);
                LinkedList entityList = new LinkedList(selectedEntityList);
                inputManager.postEvent(new SelectionEvent(entityList));
            }

            // Pass the mouse event for now so we know where the event was
            // fired. We sent this even if the entity is null, so the context
            // menu can be cleared.
            LinkedList entityList = new LinkedList(selectedEntityList);
            MouseEvent mouseEvent = (MouseEvent) ((ContextEvent) event).getMouseEvent();
            inputManager.postEvent(event);
            return;
        }
    }

    /**
     * Clears out the currently selection set of entities.
     */
    public void clearSelection() {
        selectedEntityList.clear();
        InputManager.inputManager().postEvent(new SelectionEvent(new LinkedList(selectedEntityList)));
    }
    
    /**
     * Returns the list of currently selected entitities in the order they were
     * selected, or null if no entity is currently selected.
     * 
     * @return The currently selected list of Entity objects
     */
    public List<Entity> getSelectedEntities() {
        return new LinkedList(this.selectedEntityList);
    }

    /**
     * Sets the Entity over which there is hover.
     * 
     * @param hoverEntity The hover Entity, null to reset to none
     */
    protected void setHoverEntity(Entity hoverEntity) {
        this.hoverEntity = hoverEntity;
    }
    
    /**
     * Returns the Entity over which there is a hover, null if there is no
     * hover.
     * 
     * @return The Entity object over which there is a hover 
     */
    public Entity getHoverEntity() {
        return this.hoverEntity;
    }
    
    /**
     * Convienence method that returns the cell associated with the Entity.
     * 
     * @return The Cell associated with the currently selected Entity
     */
    public static Cell getCellForEntity(Entity entity) {
       Cell ret = null;
        while(ret==null && entity!=null) {
            CellRefComponent ref = (CellRefComponent) entity.getComponent(CellRefComponent.class);
            if (ref!=null)
                ret = ((CellRefComponent)ref).getCell();
            else
                entity = entity.getParent();
        }
        return ret;
    }

    /**
     * Adds a listener for scene events.
     *
     * @param listener The scene event listener to add
     */
    public void addSceneListener(EventListener listener) {
        InputManager.inputManager().addGlobalEventListener(listener);
    }

    /**
     * Removes a listener for scene events.
     *
     * @param listener The scene event listener to remove
     */
    public void removeSceneListener(EventListener listener) {
        InputManager.inputManager().removeGlobalEventListener(listener);
    }
    
    /**
     * Timer task for hover events. This task updates the currently hovered-
     * over Entity 
     */
    class HoverTimerTask extends TimerTask {
        /* The entity over which the last event occurred */
        private Entity lastEventEntity = null;
        
        /* The time this task was started (roughly) */
        private long thisStartTime = -1;

        /* The Mouse Event that caused the hover event */
        private MouseEvent mouseEvent = null;

        /** Constructor, takes the Entity we are intereted in */
        public HoverTimerTask(Entity entity, long time, MouseEvent mouseEvent) {
            lastEventEntity = entity;
            this.thisStartTime = time;
            this.mouseEvent = mouseEvent;
        }
        
        @Override
        public void run() {
            // We have reached a timeout, so we set the Entity that is being
            // hovered over and we send an event. There is a possible race
            // condition here. This run() may be called after the event handling
            // mechanism of the Selection Manager tries to cancel any existing
            // tasks. Therefore, we synchronize on the SceneManager object
            // and check to see if the time this task was started is the same
            // time the Selection Manager thinks the hover task should be.
            synchronized(SceneManager.this) {
                if (thisStartTime == hoverStartTime) {
                    hoverEntity = lastEventEntity;
                    HoverEvent ev = new HoverEvent(lastEventEntity, true, mouseEvent);
                    InputManager.inputManager().postEvent(ev);
                    hoverTimer = null;
                }
            }
        }
    }
    
    /**
     * Global mouse listener for selection events. Reports back to the Selection
     * Manager on any updates.
     */
    class MouseEventListener extends EventClassFocusListener {
        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] { MouseEvent3D.class };
        }

        // Note: we don't override computeEvent because we don't do any computation in this listener.

        @Override
        public void commitEvent(Event event) {
            inputEvent(event);
        }
    }
    
    class MySelectionListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] {
                        ActivatedEvent.class, ContextEvent.class,
                        EnterExitEvent.class, HoverEvent.class,
                        SelectionEvent.class
            };
        }
        
        @Override
        public void commitEvent(Event event) {
            Logger logger = Logger.getLogger(MySelectionListener.class.getName());
            SceneEvent se = (SceneEvent)event;
            if (event instanceof ActivatedEvent) {
                logger.warning("SELECTION: ACTIVATED EVENT " +
                        se.getEntityList().get(0));
            }
            else if (event instanceof SelectionEvent) {
                List<Entity> selected = SceneManager.getSceneManager().getSelectedEntities();
                ListIterator<Entity> it = selected.listIterator();
                logger.warning("SELECTION: SELECTION EVENT " + selected.size());
                while (it.hasNext() == true) {
                    Entity entity = it.next();
                    logger.warning("SELECTION: SELETION EVENT " +
                            entity.getName());
                }
            }
            else if (event instanceof ContextEvent) {
                logger.warning("SELECTION: CONTEXT EVENT " +
                        se.getEntityList().size());
            }
            else if (event instanceof HoverEvent) {
                logger.warning("SELECTION: HOVER EVENT " +
                        se.getEntityList().get(0) + " " +
                        ((HoverEvent)event).isStart() + " @ " +
                        ((HoverEvent)event).getMouseEvent().getX() + " " +
                        ((HoverEvent)event).getMouseEvent().getY());
            }
            else if (event instanceof EnterExitEvent) {
                logger.warning("SELECTION: ENTER EXIT EVENT " +
                        se.getEntityList().get(0) + " " +
                        ((EnterExitEvent)event).isEnter());
            }
        }
    }
}
