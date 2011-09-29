/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.cell;

import java.lang.reflect.InvocationTargetException;
import org.jdesktop.wonderland.common.cell.messages.CellClientStateMessage;
import com.jme.bounding.BoundingVolume;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener.ChangeType;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.cell.messages.CellClientComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentUtils;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * The client side representation of a cell. Cells are created via the 
 * CellCache and should not be instantiated directly by the user on the client.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class Cell {

    private BoundingVolume cachedVWBounds;
    private BoundingVolume computedWorldBounds;
    private BoundingVolume localBounds;
    private Cell parent;
    private final List<Cell> children = new ArrayList<Cell>();
    private CellTransform localTransform;
    private CellTransform local2VW = new CellTransform(null, null);
    private CellTransform worldTransform = new CellTransform(null, null);
    private CellID cellID;
    private String name = null;
    private CellStatus currentStatus = CellStatus.DISK;
    private final Object statusLock = new Object();
    private CellCache cellCache;
    private final Map<Class, CellComponent> components = new HashMap<Class, CellComponent>();

    private CellClientStateMessageReceiver clientStateReceiver = null;
    private CellComponentMessageReceiver componentReceiver = null;

    /**
     * An enum representing the various render types supported by Wonderland.
     * A Cell represents the state, the renderer the visual representation of
     * that state.
     * 
     */
    public enum RendererType {

        /**
         * A 3D renderer for the JME client
         */
        RENDERER_JME,
        /**
         * A 2D rendering (not yet implemented)
         */
        RENDERER_2D,
        /**
         * No Renderer
         */
        NONE,
        /**
         * Low end 3D rendering, cell phone renderer etc, TBD
         */
    };
    private final Map<RendererType, CellRenderer> cellRenderers =
            new HashMap<RendererType, CellRenderer>();
    /**
     * The logger for Cell (and possibly it's subclasses)
     */
    protected static Logger logger = Logger.getLogger(Cell.class.getName());
    
    private final Set<TransformChangeListener> transformChangeListeners =
            new CopyOnWriteArraySet<TransformChangeListener>();
    private final Set<ComponentChangeListener> componentChangeListeners =
            new CopyOnWriteArraySet<ComponentChangeListener>();
    private final Set<CellStatusChangeListener> statusChangeListeners =
            new CopyOnWriteArraySet<CellStatusChangeListener>();
    private final Set<CellParentChangeListener> parentChangeListeners =
            new CopyOnWriteArraySet<CellParentChangeListener>();
    private final Set<CellChildrenChangeListener> childChangeListeners =
            new CopyOnWriteArraySet<CellChildrenChangeListener>();

    /**
     * Instantiate a new cell
     * @param cellID the cells unique ID
     * @param cellCache the cell cache which instantiated, and owns, this cell
     */
    public Cell(CellID cellID, CellCache cellCache) {
        this.cellID = cellID;
        this.cellCache = cellCache;

        logger.fine("Cell: Creating new Cell ID=" + cellID);
    }

    /**
     * Return the unique id of this cell
     * @return the cell id
     */
    public CellID getCellID() {
        return cellID;
    }

    /**
     * Return the cells parent, or null if it have no parent
     * @return
     */
    public Cell getParent() {
        return parent;
    }

    /**
     * Return the list of children for this cell, or an empty list if there
     * are no children
     * @return
     */
    public List<Cell> getChildren() {
        synchronized (children) {
            // return a copy of the children list
            return new ArrayList<Cell>(children);
        }
    }

    /**
     * Add the child to the set of children of this cell. Throws a MultipleParentException
     * if child is already a child to another cell
     * @param child to add
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    public void addChild(Cell child) throws MultipleParentException {
        if (child.getParent() != null) {
            throw new MultipleParentException();
        }

        synchronized (children) {
            children.add(child);
            child.setParent(this);
        }

        // notify listeners
        notifyChildChangeListeners(child, true);
    }

    /**
     * Remove the specified cell from the set of children of this cell.
     * Returns silently if the supplied cell is not a child of this cell.
     * 
     * TODO Test me
     * 
     * @param child
     */
    public void removeChild(Cell child) {
        synchronized (children) {
            if (children.remove(child)) {
                child.setParent(null);
            }
        }

        // notify listeners
        notifyChildChangeListeners(child, false);
    }

    /**
     * Return this cells instance of the specified component class if defined. Otherwise
     * return null.
     * 
     * @param <T> The class of the component being queried
     * @return the cells component of the requested class (or null)
     */
    public <T extends CellComponent> T getComponent(Class<T> cellComponentClass) {
        synchronized (components) {
            return (T) components.get(cellComponentClass);
        }
    }

    /**
     * Add a component to this cell. Only a single instance of each component
     * class can be added to a cell. Adding duplicate components will result in
     * an IllegalArgumentException.
     * 
     * When a component is added component.setStatus is called automatically with
     * the current status of this cell.
     * 
     * @param component the componnet to be added
     */
    public void addComponent(CellComponent component) {
        addComponent(component,
                CellComponentUtils.getLookupClass(component.getClass()));
    }

    /**
     * Add a component to this cell, with the specified componentClass. This allows for specialized
     * subclasses to be registered with a higher level interface/class.
     * Only a single instance of each component
     * class can be added to a cell. Adding duplicate components will result in
     * an IllegalArgumentException.
     *
     * When a component is added component.setStatus is called automatically with
     * the current status of this cell.
     *
     * @param component the componnet to be added
     */
    public void addComponent(CellComponent component, Class componentClass) {
        synchronized (components) {
            CellComponent previous = components.put(componentClass, component);
            if (previous != null) {
                throw new IllegalArgumentException("Adding duplicate component of class " + component.getClass().getName());
            }
        }

        synchronized (statusLock) {
            // If the cell is current more than just being on disk, then attempt
            // to find out what components it depends upon and add them
            if (currentStatus.ordinal() > CellStatus.DISK.ordinal()) {
                resolveAutoComponentAnnotationsForComponents(component);
            }

            // Set the status of the component, making sure to pass through all
            // intermediate statues.
            component.setComponentStatus(currentStatus, true);
        }

        // Tell all listeners of a new component. Should we only do this if the
        // cell is live? XXX
        notifyComponentChangeListeners(ChangeType.ADDED, component);
    }

    /**
     * Remove the cell component of the specified class, the components
     * setStatus method will be called with CellStatus.DISK to trigger cleanup
     * of any component state.
     * 
     * TODO Test me
     *  
     * @param componentClass
     */
    public void removeComponent(Class<? extends CellComponent> componentClass) {
        CellComponent component;
        synchronized (components) {
            component = components.remove(componentClass);
        }
        if (component != null) {
            component.setComponentStatus(CellStatus.DISK, false);
            notifyComponentChangeListeners(ChangeType.REMOVED, component);
        }
    }

    /**
     * Return a collection of all the components in this cell.
     * The collection is a clone of the internal data structure, so this is a
     * snapshot of the component set.
     * 
     * @return
     */
    public Collection<CellComponent> getComponents() {
        synchronized (components) {
            return new ArrayList<CellComponent>(components.values());
        }
    }

    /**
     * Get an array of all components in this cell. If this method is not
     * used, you must hold the component lock before iterating through
     * the set of components.
     * @return an array containing all components
     */
    private CellComponent[] getComponentsArray() {
        synchronized (components) {
            return components.values().toArray(new CellComponent[components.size()]);
        }
    }

    /**
     * Set the parent of this cell, called from addChild and removeChild
     * @param parent
     */
    void setParent(Cell parent) {
        this.parent = parent;

        // notify listeners
        notifyParentChangeListeners(parent);
    }

    /**
     * Return the number of children
     * 
     * @return
     */
    public int getNumChildren() {
        synchronized (children) {
            return children.size();
        }
    }

    /**
     * Return the transform for this cell
     * @return
     */
    public CellTransform getLocalTransform() {
        if (localTransform == null) {
            return null;
        }
        return (CellTransform) localTransform.clone(null);
    }

    /**
     * Set the transform for this cell.
     * 
     * Users should not call this method directly, rather MovableComponent should
     * be used, which will keep the client and server in sync.
     * 
     * @param localTransform
     */
    void setLocalTransform(CellTransform localTransform, TransformChangeListener.ChangeSource source) {
        // Don't process the same transform twice
        if (this.localTransform != null && this.localTransform.equals(localTransform)) {
            return;
        }

        if (localTransform == null) {
            this.localTransform = null;
            // Get parent worldTransform
            Cell current = getParent();
            while (current != null) {
                CellTransform parentWorldTransform = current.getWorldTransform();
                if (parentWorldTransform != null) {
                    setWorldTransform(parentWorldTransform, source);  // this method also calls notifyTransformChangeListeners
                    current = null;
                } else {
                    current = current.getParent();
                }
            }
        } else {
            this.localTransform = (CellTransform) localTransform.clone(null);
            if (parent != null) {
                worldTransform = (CellTransform) localTransform.clone(null);
                worldTransform = parent.getWorldTransform().mul(worldTransform);
                cachedVWBounds = localBounds.clone(cachedVWBounds);
                worldTransform.transform(cachedVWBounds);

                local2VW = null;
            } else if (parent == null) { // ROOT
                worldTransform = (CellTransform) localTransform.clone(null);
                local2VW = null;

                cachedVWBounds = localBounds.clone(cachedVWBounds);
                worldTransform.transform(cachedVWBounds);
            }

            notifyTransformChangeListeners(source);
        }

        if (cachedVWBounds == null) {
            logger.warning("********** NULL cachedVWBounds " + getName() + "  " + localBounds + "  " + localTransform);
            Thread.dumpStack();
        }

        for (Cell child : getChildren()) {
            transformTreeUpdate(this, child, source);
        }

        // Notify Renderers that the cell has moved
        for (CellRenderer rend : getCellRenderers()) {
            rend.cellTransformUpdate(localTransform);
        }

    }

    /**
     * Return the local to Virtual World transform for this cell.
     * @return cells local to VWorld transform
     */
    public CellTransform getLocalToWorldTransform() {
        if (local2VW == null) {
            local2VW = worldTransform.clone(null);
            local2VW.invert();
        }
        return (CellTransform) local2VW.clone(null);
    }

    /**
     * Return the world transform of the cell.
     *
     * @return the world transform of this cell.
     */
    public CellTransform getWorldTransform() {
        return (CellTransform) worldTransform.clone(null);
    }

    /**
     * Set the localToVWorld transform for this cell
     * @param localToVWorld
     */
    void setWorldTransform(CellTransform worldTransform, TransformChangeListener.ChangeSource source) {
        // OWL issue #149: make sure to actually set the transform
        this.worldTransform = (CellTransform) worldTransform.clone(null);
        cachedVWBounds = localBounds.clone(cachedVWBounds);
        worldTransform.transform(cachedVWBounds);
        local2VW = null; // force local2VW to be recalculated

        notifyTransformChangeListeners(source);
    }

    /**
     * Compute the local to vworld of the cell, this for test purposes only
     * @param parent
     * @return
     */
//    private CellTransform computeLocal2VWorld(Cell cell) {
//        LinkedList<CellTransform> transformStack = new LinkedList<CellTransform>();
//        
//        // Get the root
//        Cell current=cell;
//        while(current.getParent()!=null) {
//            transformStack.addFirst(current.localTransform);
//            current = current.getParent();
//        }
//        CellTransform ret = new CellTransform(null, null);
//        for(CellTransform t : transformStack) {
//            if (t!=null)
//                ret.mul(t);
//        }
//        
//        return ret;
//    }
    /**
     * Update local2VWorld and bounds of child and all its children recursively 
     * to reflect changes in a parent
     * 
     * @param parent
     * @param child
     * @return the combined bounds of the child and all it's children
     */
    private BoundingVolume transformTreeUpdate(Cell parent, Cell child, TransformChangeListener.ChangeSource source) {
        CellTransform parentWorldTransform = parent.getWorldTransform();

        CellTransform childTransform = child.getLocalTransform();

        if (childTransform != null) {
            // OWL issue #149: calculate the transform the same way as 
            // setLocalTransform() above.
            childTransform = parentWorldTransform.mul(childTransform);
            child.setWorldTransform(childTransform, source);
        } else {
            child.setWorldTransform(parentWorldTransform, source);
        }

        BoundingVolume ret = child.getWorldBounds();

        Iterator<Cell> it = child.getChildren().iterator();
        while (it.hasNext()) {
            ret.mergeLocal(transformTreeUpdate(child, it.next(), source));
        }

        child.setWorldBounds(ret);

        return null;
    }

    /**
     * Returns the world bounds, this is the local bounds transformed into VW 
     * coordinates. These bounds do not include the subgraph bounds. This call 
     * is only valid for live cells.
     * 
     * @return world bounds
     */
    public BoundingVolume getWorldBounds() {
        return cachedVWBounds;
    }

    /**
     * Set the World Bounds for this cell
     * @param cachedVWBounds
     */
    private void setWorldBounds(BoundingVolume cachedVWBounds) {
        this.cachedVWBounds = cachedVWBounds;
    }

    /**
     * Return the name for this cell (defaults to cellID)
     * @return
     */
    public String getName() {
        if (name == null) {
            return cellID.toString();
        }
        return name;
    }

    /**
     * Set a name for the cell
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the local bounds for this cell. Local bounds are in the cells
     * coordinate system
     * 
     * @return local bounds for this cell
     */
    public BoundingVolume getLocalBounds() {
        return localBounds.clone(null);
    }

    /**
     * Set the local bounds for this cell
     * @param localBounds
     */
    public void setLocalBounds(BoundingVolume localBounds) {
        this.localBounds = localBounds;
    }

    /**
     * Return the cell cache which instantiated and owns this cell.
     * @return the cell cache which instantiated this cell.
     */
    public CellCache getCellCache() {
        return cellCache;
    }

    /**
     * Returns the status of this cell
     * Cell states
     *
     * DISK - Cell is on disk with no memory footprint
     * INACTIVE - Cell object is in memory with bounds initialized, NO geometry is loaded
     * INACTIVE - All cell data is in memory
     * ACTIVE - Cell is within the avatars proximity bounds
     * VISIBLE - Cell is in the view frustum
     *
     * @return returns CellStatus
     */
    public CellStatus getStatus() {
        synchronized (statusLock) {
            return currentStatus;
        }
    }

    private void setRendererStatus(CellRenderer rend, CellStatus status) {
        synchronized(statusLock) {
            int currentRendStatus = rend.getStatus().ordinal();
            int requiredRendStatus = status.ordinal();

            if (currentRendStatus == requiredRendStatus)
                return;

            boolean increasing;
            int dir = (requiredRendStatus > currentRendStatus ? 1 : -1);
            if (dir==1)
                increasing = true;
            else
                increasing = false;

            while (currentRendStatus != requiredRendStatus) {
                currentRendStatus += dir;
                rend.setStatus(CellStatus.values()[currentRendStatus], increasing);
            }
        }
    }

    /**
     * Set the status of this cell
     *
     *
     * Cell states
     *
     * DISK - Cell is on disk with no memory footprint
     * INACTIVE - Cell object is in memory with bounds initialized, NO geometry is loaded
     * INACTIVE - All cell data is in memory
     * ACTIVE - Cell is within the avatars proximity bounds
     * VISIBLE - Cell is in the view frustum
     * 
     * The system guarantees that if a change is made between non adjacent status, say from INACTIVE to VISIBLE
     * that setStatus will automatically be called for the intermediate values.
     * 
     * If you overload this method in your own class you must call super.setStatus(...) as the first operation
     * in your method.
     *
     * Note users should not call this method directly, it should only be called
     * from implementations of the cache.
     *
     * @param status the cell status
     * @param increasing indicates if the status is increasing
     */
    protected void setStatus(CellStatus status, boolean increasing) {
        synchronized(statusLock) {
            if (status == CellStatus.INACTIVE && increasing) {
                resolveAutoComponentAnnotationsForCell();
                CellComponent[] compList = getComponentsArray();
                for (CellComponent c : compList) {
                    resolveAutoComponentAnnotationsForComponents(c);
                }
            }

            currentStatus = status;

            // issue 964: make sure to grab the correct lock
            for (CellComponent component : getComponentsArray()) {
                component.setComponentStatus(status, increasing);
            }

            for (CellRenderer renderer : getCellRenderers()) {
                setRendererStatus(renderer, status);
            }

            switch (status) {
                case DISK:
                    if (!increasing) {
                        if (transformChangeListeners != null) {
                            transformChangeListeners.clear();
                        }

                        // Also, remove the message listener for updates to the
                        // cell state
                        ChannelComponent channel = getComponent(ChannelComponent.class);
                        if (channel != null) {
                            channel.removeMessageReceiver(CellClientStateMessage.class);
                            channel.removeMessageReceiver(CellClientComponentMessage.class);
                        }

                        // remove the receivers
                        clientStateReceiver = null;
                        componentReceiver = null;

                        // Now clear all components
                        synchronized (components) {
                            components.clear();
                        }

                        synchronized(cellRenderers) {
                            cellRenderers.clear();
                        }
                    }
                    break;

                case ACTIVE:
                    if (increasing && clientStateReceiver == null) {
                        // Add the message receiver for all messages meant to
                        // update the state cell on the client-side
                        clientStateReceiver = new CellClientStateMessageReceiver(this);
                        componentReceiver = new CellComponentMessageReceiver(this);

                        ChannelComponent channel = getComponent(ChannelComponent.class);
                        if (channel != null) {
                            channel.addMessageReceiver(CellClientStateMessage.class,
                                                       clientStateReceiver);
                            channel.addMessageReceiver(CellClientComponentMessage.class,
                                                       componentReceiver);
                        }
                        try {
                            createCellRendererImpl(ClientContext.getRendererType());
                        } catch(Exception e) {
                            logger.log(Level.SEVERE, "Failed to get Cell Renderer for cell "+getClass().getName(), e);
                        }
                    }
                 break;
            }
        }
    }

    /**
     * Notify listeners that the cell status has changed.  If you manually
     * call setStatus() on the cell, you must also call this method to notify
     * listeners, after the call to setStatus() completes.
     * @param status the new status
     */
    protected void fireCellStatusChanged(CellStatus status) {
        // update both local and global listeners.  This is done after the
        // lock is released, so the status may change again before the listeners
        // are called
        notifyStatusChangeListeners(status);
        CellManager.getCellManager().notifyCellStatusChange(this, status);
    }

    /**
     * Check for @UsesCellComponent annotations in the cellcomponent and
     * populate fields appropriately. Also checks the superclassses of the
     * cell component upto CellComponent.class
     * 
     * @param c
     */
    private void resolveAutoComponentAnnotationsForComponents(CellComponent c) {
        Class clazz = c.getClass();
        while (clazz != CellComponent.class) {
            resolveAnnotations(clazz, c);
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Check for @UsesCellComponent annotations in the cell and
     * populate fields appropriately. Also checks the superclassses of the
     * cell upto Cell.class
     *
     * @param c
     */
    private void resolveAutoComponentAnnotationsForCell() {
        Class clazz = this.getClass();
        while (clazz != Cell.class) {
            resolveAnnotations(clazz, this);
            clazz = clazz.getSuperclass();
        }
    }

    private void resolveAnnotations(Class clazz, Object o) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            UsesCellComponent a = f.getAnnotation(UsesCellComponent.class);
//            System.err.println("Field "+f.getName()+"  "+f.getType()+"   "+f.getAnnotations().length);
            if (a != null) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("****** GOT ANNOTATION for field " + f.getName() + "  " + f.getType());
                }

                Class componentClazz = f.getType();
                CellComponent comp = getComponent(CellComponentUtils.getLookupClass(componentClazz));
                if (comp == null) {
                    try {
                        comp = (CellComponent) componentClazz.getConstructor(Cell.class).newInstance(this);
                        addComponent(comp);
                    } catch (IllegalArgumentException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }

                try {
                    f.setAccessible(true);
                    f.set(o, comp);
                } catch (IllegalArgumentException ex) {
                    logger.log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Called when the cell is initially created and any time there is a 
     * major configuration change. The cell will already be attached to it's parent
     * before the initial call of this method
     * 
     * @param configData the configuration data for the cell
     */
    public void setClientState(CellClientState configData) {

        // Sets the name of the cell
        this.setName(configData.getName());

        logger.fine("configure cell " + getCellID() + "  " + getClass());
        // Install the CellComponents
        for (String compClassname : configData.getClientComponentClasses()) {
            try {
                // find the classloader associated with the server session
                // manager that loaded this cell.  That classloader will
                // have all the module classes
                WonderlandSession session = getCellCache().getSession();
                ClassLoader cl = session.getSessionManager().getClassloader();

                // us the classloader we found to load the component class
                Class compClazz = cl.loadClass(compClassname);

                // Find out the Class used to lookup the component in the list
                // of components
                Class lookupClazz = CellComponentUtils.getLookupClass(compClazz);

                // Attempt to fetch the component using the lookup class. If
                // it does not exist, then create and add the component.
                // Otherwise, just set the client-side of the component
                CellComponent component = getComponent(lookupClazz);
                if (component == null) {
                    // Create a new cell component based upon the class name,
                    // set its state, and add it to the list of components
                    Constructor<CellComponent> constructor = compClazz.getConstructor(Cell.class);
                    component = constructor.newInstance(this);
                    CellComponentClientState clientState = configData.getCellComponentClientState(compClassname);
                    if (clientState != null) {
                        component.setClientState(clientState);
                    }
                    addComponent(component, CellComponentUtils.getLookupClass(component.getClass()));
                } else {
                    CellComponentClientState clientState = configData.getCellComponentClientState(compClassname);
                    if (clientState != null) {
                        component.setClientState(clientState);
                    }
                }
            } catch (InstantiationException ex) {
                logger.log(Level.SEVERE, "Instantiation exception for class " + compClassname + "  in cell " + getClass().getName(), ex);
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "Can't find component class " + compClassname, ex);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * A utility routine that fetches the channel component of the cell and
     * sends a message on it. If there is no channel component (should never
     * happen), this method logs an error message.
     *
     * @param message The CellMessage
     */
    public void sendCellMessage(CellMessage message) {
        ChannelComponent channel = getComponent(ChannelComponent.class);
        if (channel == null) {
            logger.severe("Unable to find channel on cell id " + getCellID() +
                    " with name " + getName());
            return;
        }
        channel.send(message);
    }

    /**
     * A utility routine that fetches the channel component of the cell and
     * sends a message on it. This method also waits for and returns the
     * response. If there is no channel component (should never happen), this
     * method logs an error message and returns null.
     *
     * @param message The CellMessage
     */
    public ResponseMessage sendCellMessageAndWait(CellMessage message) {
        // Fetch the channel, if not present, log and error and return null
        ChannelComponent channel = getComponent(ChannelComponent.class);
        if (channel == null) {
            logger.severe("Unable to find channel on cell id " + getCellID() +
                    " with name " + getName());
            return null;
        }

        // Send the message and return the response. Upon exception, log an
        // error and return null
        try {
            return channel.sendAndWait(message);
        } catch (java.lang.InterruptedException excp) {
            logger.log(Level.WARNING, "Sending message and waiting got " +
                    "interrupted on cell id " + getCellID() + " with name " +
                    getName(), excp);
            return null;
        }
    }

    /**
     * Create the renderer for this cell
     * @param rendererType The type of renderer required
     * @return the renderer for the specified type if available, or null
     */
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        Logger.getAnonymousLogger().warning(this.getClass().getName() + " createCellRenderer returning null");
        return null;
    }

    private void createCellRendererImpl(RendererType rendererType) {
        CellRenderer created = null;

        synchronized(cellRenderers) {
            CellRenderer ret = cellRenderers.get(rendererType);
            if (ret == null) {
                ret = createCellRenderer(rendererType);
                if (ret != null) {
                    cellRenderers.put(rendererType, ret);
                    created = ret;
                }
            }
        }

        // issue 964: we don't want to call setRenderStatus() while holding
        // the cellRenderers lock.  Instead, if we created a renderer above,
        // set its status here
        if (created != null) {
            synchronized (statusLock) {
                setRendererStatus(created, getStatus());
            }
        }
    }

    /**
     * Return the renderer of the given type for this cell. If a renderer of the
     * requested type is not available, or the cell is not in at least the RENDERING state  null will be returned
     * @param rendererType the type of the render to return
     * @return the renderer, or null if no renderer of the specified type is available
     */
    public CellRenderer getCellRenderer(RendererType rendererType) {
        synchronized(cellRenderers) {
            CellRenderer ret = cellRenderers.get(rendererType);
            return ret;
        }
    }

    /**
     * Get the set of renderers as an array, for iteration.  If this method
     * is not used, then the cellRenderers lock must be held before iterating
     * over the array.
     * @return the array of all renderers
     */
    private CellRenderer[] getCellRenderers() {
        synchronized (cellRenderers) {
            return cellRenderers.values().toArray(new CellRenderer[cellRenderers.size()]);
        }
    }

    /**
     * Add a TransformChangeListener to this cell. The listener will be
     * called for any changes to the cells transform
     * 
     * @param listener to add
     */
    public void addTransformChangeListener(TransformChangeListener listener) {
        transformChangeListeners.add(listener);
    }

    /**
     * Remove the specified listener.
     * @param listener to be removed
     */
    public void removeTransformChangeListener(TransformChangeListener listener) {
        transformChangeListeners.remove(listener);
    }

    private void notifyTransformChangeListeners(TransformChangeListener.ChangeSource source) {
        for (TransformChangeListener listener : transformChangeListeners) {
            listener.transformChanged(this, source);
        }
    }

    /**
     * Add a ComponentChangeListener to this cell. The listener will be
     * called for any changes to the cell's list of components
     *
     * @param listener to add
     */
    public void addComponentChangeListener(ComponentChangeListener listener) {
        componentChangeListeners.add(listener);
    }

    /**
     * Remove the specified listener.
     * @param listener to be removed
     */
    public void removeComponentChangeListener(ComponentChangeListener listener) {
        componentChangeListeners.remove(listener);
    }

    private void notifyComponentChangeListeners(ComponentChangeListener.ChangeType source, CellComponent component) {
        for (ComponentChangeListener listener : componentChangeListeners) {
            listener.componentChanged(this, source, component);
        }
    }

    /**
     * Add a status change listener to this cell.  The listener will be called
     * for any change to this cell's status.  For changes to any cell's
     * status, use <code>CellManager</code>.
     * @param listener the listener to add
     */
    public void addStatusChangeListener(CellStatusChangeListener listener) {
        statusChangeListeners.add(listener);
    }

    /**
     * Remove a status change listener from this cell
     * @param listener the listener to remove
     */
    public void removeStatusChangeListener(CellStatusChangeListener listener) {
        statusChangeListeners.remove(listener);
    }

    private void notifyStatusChangeListeners(CellStatus status) {
        for (CellStatusChangeListener listener : statusChangeListeners) {
            listener.cellStatusChanged(this, status);
        }
    }

    /**
     * Add a parent change listener to this cell.  The listener will be called
     * for any change to this cell's parent.
     * @param listener the listener to add
     */
    public void addParentChangeListener(CellParentChangeListener listener) {
        parentChangeListeners.add(listener);
    }

    /**
     * Remove a parent change listener from this cell
     * @param listener the listener to remove
     */
    public void removeParentChangeListener(CellParentChangeListener listener) {
        parentChangeListeners.remove(listener);
    }

    private void notifyParentChangeListeners(Cell parent) {
        for (CellParentChangeListener listener : parentChangeListeners) {
            listener.parentChanged(this, parent);
        }
    }

    /**
     * Add a children change listener to this cell.  The listener will be called
     * for any change to this cell's children.
     * @param listener the listener to add
     */
    public void addChildrenChangeListener(CellChildrenChangeListener listener) {
        childChangeListeners.add(listener);
    }

    /**
     * Remove a child change listener from this cell
     * @param listener the listener to remove
     */
    public void removeChildChangeListener(CellChildrenChangeListener listener) {
        childChangeListeners.remove(listener);
    }

    private void notifyChildChangeListeners(Cell child, boolean added) {
        for (CellChildrenChangeListener listener : childChangeListeners) {
            if (added) {
                listener.childAdded(this, child);
            } else {
                listener.childRemoved(this, child);
            }
        }
    }
}
