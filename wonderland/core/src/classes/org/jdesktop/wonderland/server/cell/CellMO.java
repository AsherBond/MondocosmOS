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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.common.cell.messages.CellClientComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateRequestMessage;
import org.jdesktop.wonderland.common.cell.messages.CellClientStateMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateSetMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateUpdateMessage;
import org.jdesktop.wonderland.common.cell.security.ChildrenAction;
import org.jdesktop.wonderland.common.cell.security.ComponentAction;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.security.annotation.Actions;
import org.jdesktop.wonderland.server.cell.annotation.DependsOnCellComponentMO;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.common.cell.security.ModifyAction;
import org.jdesktop.wonderland.common.cell.security.MoveAction;
import org.jdesktop.wonderland.common.cell.security.ViewAction;
import org.jdesktop.wonderland.common.cell.state.CellComponentUtils;
import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.spatial.UniverseManager;
import org.jdesktop.wonderland.server.spatial.UniverseManagerFactory;
import org.jdesktop.wonderland.server.state.PositionServerStateHelper;

/**
 * Superclass for all server side representation of a cell
 * 
 * @author paulby
 */
@ExperimentalAPI
@Actions({ViewAction.class, ModifyAction.class, ComponentAction.class, ChildrenAction.class, MoveAction.class})
public abstract class CellMO implements ManagedObject, Serializable {

    private ManagedReference<CellMO> parentRef=null;
    private ArrayList<ManagedReference<CellMO>> childCellRefs = null;
    private CellTransform localTransform = null;
    protected CellID cellID;
    private BoundingVolume localBounds;
    private CellID parentCellID;
    
    // a check if there is a bounds change that has not been committed.  If
    // there are uncommitted bounds changes, certain operations (like 
    // getting the computed VW bounds) are not valid
//    private transient boolean boundsChanged = false;
    
    private String name=null;
    
    private boolean live = false;
    
    protected ManagedReference<Channel> cellChannelRef = null;
    
    protected static Logger logger = Logger.getLogger(CellMO.class.getName());
    
    private short priority;
    
    // ManagedReferences of ClientSessions
    protected HashSet<ManagedReference<ClientSession>> clientSessionRefs = null;
    
    private HashMap<Class, ManagedReference<CellComponentMO>> components = new HashMap();
    
    private HashSet<TransformChangeListenerSrv> transformChangeListeners=null;

    private final Set<ComponentChangeListenerSrv> componentChangeListeners =
            new LinkedHashSet<ComponentChangeListenerSrv>();
    private final Set<CellParentChangeListenerSrv> parentChangeListeners =
            new LinkedHashSet<CellParentChangeListenerSrv>();
    private final Set<CellChildrenChangeListenerSrv> childrenChangeListeners =
            new LinkedHashSet<CellChildrenChangeListenerSrv>();

    /** Default constructor, used when the cell is created via WFS */
    public CellMO() {
        this.localBounds = null;
        this.localTransform = null;
        this.cellID = WonderlandContext.getCellManager().createCellID(this);
    }
    
    /**
     * Create a CellMO with the specified localBounds and transform.
     * If either parameter is null an IllegalArgumentException will be thrown.
     * @param localBounds the bounds of the new cell, must not be null
     * @param transform the transform for this cell, must not be null
     */
    public CellMO(BoundingVolume localBounds, CellTransform transform) {
        this();
        if (localBounds==null)
            throw new IllegalArgumentException("localBounds must not be null");
        if (transform==null)
            throw new IllegalArgumentException("transform must not be null");
        
        this.localTransform = transform;
        setLocalBounds(localBounds);

    }
    
    /**
     * Set the bounds of the cell in cell local coordinates
     * @param bounds
     */
    public void setLocalBounds(BoundingVolume bounds) {
        localBounds = bounds.clone(null);
        if (live) {
            throw new RuntimeException("SetBounds on live cells is not implemented yet");
//            UniverseManager.getUniverseManager().setLocalBounds(bounds);
        }
    }
    
    /**
     *  Return (a clone) of the cells bounds in cell local coordinates
     * @return the bounds in local coordinates
     */
    public BoundingVolume getLocalBounds() {
        return (BoundingVolume) localBounds.clone(null);     
    }
    
    /**
     * Returns the local bounds transformed into VW coordinates. These bounds
     * do not include the subgraph bounds. This call is only valid for live
     * cells
     * 
     * @return
     */
    public BoundingVolume getWorldBounds() {
        if (!live)
            throw new IllegalStateException("Cell is not live");
        
        return UniverseManagerFactory.getUniverseManager().getWorldBounds(this, null);
    }
   
    /**
     * Get the world transform of this cells origin. This call
     * can only be made on live cells, an IllegalStateException will be thrown
     * if the cell is not live.
     * 
     * TODO - should we create our own exception type ?
     * 
     * @param result the CellTransform to populate with the result and return, 
     * can be null in which case a new CellTransform will be returned.
     * @return
     */
    public CellTransform getWorldTransform(CellTransform result) {
        if (!live)
            throw new IllegalStateException("Unsupported Operation, only valid for a live Cell "+this.getClass().getName());
        
        return UniverseManagerFactory.getUniverseManager().getWorldTransform(this, result);
    }
    
    /**
     *  Add a child cell to list of children contained within this cell.
     *  A cell can only be attached to a single parent cell at any given time,
     *  attempting to add a cell to multiple parents will result in a
     *  MultipleParentException being thrown.
     * 
     * @param child
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    public void addChild(CellMO child) throws MultipleParentException {
        if (childCellRefs==null)
            childCellRefs = new ArrayList<ManagedReference<CellMO>>();
        
        child.setParent(this);
        
        childCellRefs.add(AppContext.getDataManager().createReference(child));

        if (live) {
           child.setLive(true);     // setLive will add the child to the universe and form the parent/child relationship
        }

        // notify listeners
        fireChildChangedEvent(child, true);
    }
    
    /**
     * Remove the child from the list of children of this cell.
     * 
     * @param child to remove
     * @return true if the child was removed, false if the cell was not a child of
     * this cell.
     */
    public boolean removeChild(CellMO child) {
        ManagedReference childRef = AppContext.getDataManager().createReference(child);
        
        if (childCellRefs.remove(childRef)) {
            try {
                child.setParent(null);
                if (live) {
                    child.setLive(false);
                }

                // notify listeners
                fireChildChangedEvent(child, false);

                return true;
            } catch (MultipleParentException ex) {
                // This should never happen
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        // Not a child of this cell
        return false;
    }
    
    /**
     * Return the number of children of this cell
     * @return the number of children
     */
    public int getNumChildren() {
        if (childCellRefs==null)
            return 0;
        
        return childCellRefs.size();
    }
    
    /**
     * Return the collection of children references for this cell. 
     * If this cell has no children an empty collection is returned.
     * Users of this call should not make changes to the collection directly
     * 
     * @return a collection of references to the children of this cell.
     */
    public Collection<ManagedReference<CellMO>> getAllChildrenRefs() {
        if (childCellRefs==null)
            return new ArrayList<ManagedReference<CellMO>>();
        
        return childCellRefs;
    }
        
    /**
     *  Return the cell which is the parentRef of this cell, null if this not
     * attached to a parentRef
     */
    public CellMO getParent() {
        if (parentRef==null)
            return null;
        return parentRef.get();                
    }
    
    /**
     * Return the cellID of the parent.  This method was added for debugging
     * and is used by SpaceMO to check that the lists are ordered correctly.
     * 
     * TODO remove
     * 
     * @return
     */
    CellID getParentCellID() {
        return parentCellID;
    }
    
    /**
     * Detach this cell from its parent
     */
    public void detach() {
        CellMO parent = getParent();
        if (parent==null)
            return;
        
        parent.removeChild(this);
    }
    
    /**
     * Set the parent of this cell. Package private because the parent is
     * controlled through add and remove child.
     * 
     * @param parent the parent cell
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    void setParent(CellMO parent) throws MultipleParentException {
        if (parent!=null && parentRef!=null)
            throw new MultipleParentException();
        
        if (parent==null) {
            this.parentRef = null;
            this.parentCellID = CellID.getInvalidCellID();
        } else {
            this.parentRef = AppContext.getDataManager().createReference(parent);
            this.parentCellID = parent.getCellID();
        }

        // notify listeners
        fireParentChangeEvent(parent);
    }
    
    /**
     * Set the transform for this cell. This will define the localOrigin of
     * the cell on the client. This transform is combined with all parentRef 
     * transforms to define the location of the cell in 3 space. 
     * 
     * Changing the transform repositions the cell which is a fairly expensive
     * operation as it changes the computed bounds of this cell and potentially
     * all it's parent cells.
     * 
     * This method is usually called during cell construction or from
     * reconfigureCell. If you want a cell that moves regularly around the
     * world use MovableComponent.
     * 
     * @param transform
     */
    protected void setLocalTransform(CellTransform transform) {
        
        this.localTransform = (CellTransform) transform.clone(null);

        if (live)
            UniverseManagerFactory.getUniverseManager().setLocalTransform(this, localTransform);
    }
    

    /**
     * Return the cells transform
     * 
     * @return return a clone of the transform
     */
    public CellTransform getLocalTransform(CellTransform result) {
        return (CellTransform) localTransform.clone(result);
    }
    
    /**
     * Notify the client that the contents of the cell have changed
     *
     * REPLACED BY setServerState
     *
     */
//    public void contentChanged() {
//        logger.severe("CellMO.contentChanged NOT IMPLEMENTED");
//    }
       
    /**
     * Return the cellID for this cell
     * 
     * @return cellID
     */
    public CellID getCellID() {
        return cellID;
    }
    
    /**
     * Get the live state of this cell. live cells are connected to the
     * world root, inlive cells are not
     */
    public boolean isLive() {
        return live;
    }
    
    /**
     * Set the live state of this cell. Live cells are connected to the
     * world root and are present in the world, non-live cells are not
     * @param live
     */
    protected void setLive(boolean live) {
        if (this.live==live)
            return;

        if (live) {
            if (localBounds==null) {
                logger.severe("CELL HAS NULL BOUNDS, defaulting to unit sphere");
                localBounds = new BoundingSphere(1f, new Vector3f());
            }

            createChannelComponent();
            resolveAutoComponentAnnotationsForCell();

            addToUniverse(UniverseManagerFactory.getUniverseManager(), true);

            this.live = live;  // Needs to happen after resolveAutoComponentAnnotationsForCell

            // Add a message receiver to handle messages to dynamically add and
            // remove components, get and set the server state.
            ChannelComponentMO channel = getComponent(ChannelComponentMO.class);
            if (channel != null) {
                channel.addMessageReceiver(CellServerComponentMessage.class,
                        new ComponentMessageReceiver(this));
                channel.addMessageReceiver(CellServerStateRequestMessage.class,
                        new ComponentStateMessageReceiver(this));
                channel.addMessageReceiver(CellServerStateSetMessage.class,
                        new ComponentStateMessageReceiver(this));
                channel.addMessageReceiver(CellServerStateUpdateMessage.class,
                        new ComponentStateMessageReceiver(this));
            }

            // Issue #1108: copy components into an array to avoid concurrent
            // modification issues.  Note that the cell is live at this point,
            // so all components added in this call will be set live
            // immediately.
            ManagedReference<CellComponentMO>[] compList =
                    components.values().toArray(new ManagedReference[components.size()]);

            for(ManagedReference<CellComponentMO> c : compList) {
                resolveAutoComponentAnnotationsForComponent(c);
            }
        } else {
            // If not live then process the children first, hence this is
            // handled in the if (!live) block below
        }


        // Notify all components of new live state
        ManagedReference<CellComponentMO>[] compList =
                    components.values().toArray(new ManagedReference[components.size()]);
        for(ManagedReference<CellComponentMO> c : compList) {
            // Issue #1108: if the component was added from another component
            // above, it may already be live.  Don't set it live a second
            // time.
            if (c.get().isLive() != live) {
                c.get().setLive(live);
            }
        }
        
        for(ManagedReference<CellMO> ref : getAllChildrenRefs()) {
            CellMO child = ref.get();
            child.setLive(live);
        }

        if (!live) {
            this.live = live;
            removeFromUniverse(UniverseManagerFactory.getUniverseManager());

            // Remove the message receiver that handles messages to dynamically
            // add and remove components, get and set the server state.
            ChannelComponentMO channel = getComponent(ChannelComponentMO.class);
            if (channel != null) {
                channel.removeMessageReceiver(CellServerComponentMessage.class);
                channel.removeMessageReceiver(CellServerStateRequestMessage.class);
                channel.removeMessageReceiver(CellServerStateSetMessage.class);
                channel.removeMessageReceiver(CellServerStateUpdateMessage.class);
            }

        }
    }

    /**
     * Check for @AutoCellComponent annotations in the cellcomponent and
     * populate fields appropriately. Also checks the superclassses of the
     * cell component upto CellComponent.class
     *
     * @param c
     */
    private void resolveAutoComponentAnnotationsForComponent(ManagedReference<CellComponentMO> c) {
            Class clazz = c.get().getClass();
            while(clazz!=CellComponentMO.class) {
                resolveAnnotations(clazz, c);
                clazz = clazz.getSuperclass();
            }
    }

    /**
     * Check for @AutoCellComponent annotations in the cell and
     * populate fields appropriately. Also checks the superclassses of the
     * cell upto Cell.class
     *
     * @param c
     */
    private void resolveAutoComponentAnnotationsForCell() {
        Class clazz = this.getClass();
        ManagedReference<CellMO> c = AppContext.getDataManager().createReference(this);
        while(clazz!=CellMO.class) {
            resolveAnnotations(clazz, c);
            clazz = clazz.getSuperclass();
        }

    }
    private void resolveAnnotations(Class clazz, ManagedReference<? extends ManagedObject> o) {

        // Resolve @DependsOnCellComponentMO on class
        DependsOnCellComponentMO dependsOn = (DependsOnCellComponentMO) clazz.getAnnotation(DependsOnCellComponentMO.class);
        if (dependsOn!=null) {
            Class[] dependClasses = dependsOn.value();
            if (dependClasses!=null) {
                for(Class c : dependClasses) {
                    checkComponentFromAnnotation(c);
                }
            }
        }

        // Resolve @UsesCellComponentMO annotation on fields
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields) {
            UsesCellComponentMO a = f.getAnnotation(UsesCellComponentMO.class);
            if (a!=null) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("****** GOT ANNOTATION for field "+f.getName());

                CellComponentMO comp = checkComponentFromAnnotation(a.value());

                try {
                    f.setAccessible(true);
                    f.set(o.getForUpdate(), AppContext.getDataManager().createReference(comp));
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Check if the component is already added to the cell, if not create and add it
     * @param componentClazz class of component
     * @return the component object
     */
    private CellComponentMO checkComponentFromAnnotation(Class componentClazz) {
        CellComponentMO comp = getComponent(componentClazz);
        if (comp==null) {

            try {
                // Create the component and add it to the map. We must
                // also recursively create the component's dependencies
                // too!
                comp = (CellComponentMO) (componentClazz.getConstructor(CellMO.class).newInstance(this));
                addComponent(comp);
            } catch (InstantiationException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, "Error instantiating component "+componentClazz, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                // bug #527 -- rethrow Runtime exceptions
                if (ex.getCause() != null && ex.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ex.getCause();
                }
                
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, "Error in " + this + " invoking constructor on component "+componentClazz, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return comp;
    }

    /**
     * Create the channel component for this cell. All cells have a channel component, but by
     * default only root cells actually open a channel. Child cells of a root will use
     * the roots channel. This is hidden from the user as they always access the common
     * ChannelComponentMO api
     */
    private void createChannelComponent() {
        if (getComponent(ChannelComponentMO.class)!=null)
            return;

        addComponent(new ChannelComponentMO(this));
    }

    /**
     * Add this cell to the universe
     */
    void addToUniverse(UniverseManager universe, boolean notify) {
        universe.createCell(this, notify);

//        System.err.println("CREATING SPATIAL CELL " + getCellID().toString() + " " + this.getClass().getName());

        if (transformChangeListeners != null) {
            for (TransformChangeListenerSrv listener : transformChangeListeners) {
                universe.addTransformChangeListener(this, listener);
            }
        }

        if (parentRef != null) {
            universe.addChild(parentRef.getForUpdate(), this);
        }
    }

    /**
     * Remove this cell from the universe
     */
    void removeFromUniverse(UniverseManager universe) {
        universe.removeCell(this);
    }

    /**
     * Get the name of the cell, by default the name is the cell id.
     * @return the cell's name
     */
    public String getName() {
        if (name==null)
            return cellID.toString();
        
        return name;
    }

    /**
     * Set the name of the cell. The name is simply for developer reference.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * Add a client session with the specified capabilities to this cell. 
     * Called by the ViewCellCacheMO as part of makeing a cell active, only 
     * applicable to cells with a ChannelComponent.
     * 
     * @param clientID the ID of the client that is being added
     * @param capabilities
     * @return
     */
    protected CellSessionProperties addClient(WonderlandClientID clientID,
                                            ClientCapabilities capabilities) {
        ChannelComponentMO chan = getComponent(ChannelComponentMO.class);
        if (chan!=null) {
            chan.addUserToCellChannel(clientID);
        }
        
        return new CellSessionProperties(getViewCellCacheRevalidationListener(), 
                getClientCellClassName(clientID, capabilities),
                getClientState(null, clientID, capabilities));
    }
    
    /**
     * Called to notify the cell that some aspect of the client sessions capabilities
     * have changed. This call is made from the ViewCellCacheOperations exectue
     * method returned by addSession.
     * 
     * @param clientID
     * @param capabilities
     * @return
     */
    protected CellSessionProperties changeClient(WonderlandClientID clientID,
                                               ClientCapabilities capabilities) {
        return new CellSessionProperties(getViewCellCacheRevalidationListener(), 
                getClientCellClassName(clientID, capabilities),
                getClientState(null, clientID, capabilities));
        
    }
    
    /**
     * Remove this cell from the specified session, only applicable to cells
     * with a ChannelComponent. This modifies the ChannelComponent for this cell
     * (if it exists) but does not modify the CellMO itself.
     * 
     * @param clientID
     */
    protected void removeSession(WonderlandClientID clientID) {
        ChannelComponentMO chan = getComponent(ChannelComponentMO.class);
        if (chan!=null) {
            chan.removeUserFromCellChannel(clientID);
        }
    }

    /**
     * Returns the fully qualified name of the class that represents
     * this cell on the client
     */
    protected abstract String getClientCellClassName(WonderlandClientID clientID,
                                                     ClientCapabilities capabilities);
    
    /**
     * Returns the client-side state of the cell. If the cellClientState argument
     * is null, then the method should create an appropriate class, otherwise,
     * the method should just fill in details in the class. Returns the client-
     * side state class
     *
     * @param cellClientState If null, create a new object
     * @param clientID The unique ID of the client
     * @param capabilities The client capabilities
     */
    protected CellClientState getClientState(CellClientState cellClientState,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        // If the given cellClientState is null, create a new one
        if (cellClientState == null) {
            cellClientState = new CellClientState();
        }
        populateCellClientState(cellClientState, clientID, capabilities);

        // Set the name of the cell
        cellClientState.setName(this.getName());
        return cellClientState;
    }

    private void populateCellClientState(CellClientState config,
            WonderlandClientID clientID, ClientCapabilities capabilities) {

        Iterable<ManagedReference<CellComponentMO>> compReferences = components.values();
        for(ManagedReference<CellComponentMO> ref : compReferences) {
            CellComponentMO componentMO = ref.get();
            String clientClass = componentMO.getClientClass();
            if (clientClass != null) {
                CellComponentClientState clientState = componentMO.getClientState(null, clientID, capabilities);
                config.addClientComponentClasses(clientClass, clientState);
            }
        }
    }
    
    /**
     * Returns the ViewCacheOperation, or null
     * @return
     */
    protected ViewCellCacheRevalidationListener getViewCellCacheRevalidationListener() {
        return null;
    }
    
    
    /**
     * Sets the server-side state of the cell, given the server state properties
     * passed in.
     *
     * @param state the properties to set the state with
     */
    public void setServerState(CellServerState state) {
        // Set the name of the cell if it is not null
        if (state.getName() != null) {
            this.setName(state.getName());
        }
        
        // For all components in the setup class, create the component classes
        // and setup them up and add to the cell.
        for (Map.Entry<Class, CellComponentServerState> e : state.getComponentServerStates().entrySet()) {
            CellComponentServerState compState = e.getValue();

            // Check to see if the component server state is the special case
            // of the Position state. If so, set the values in the cell manually.
            if (compState instanceof PositionComponentServerState) {
                
                // Set up the transform (origin, rotation, scaling) and cell bounds
                PositionComponentServerState posState = (PositionComponentServerState)compState;
                setLocalTransform(PositionServerStateHelper.getCellTransform(posState));
                setLocalBounds(PositionServerStateHelper.getCellBounds(posState));
                continue;
            }

            // Otherwise, set the state of the server-side component, creating
            // it if necessary.
            String className = compState.getServerComponentClassName();
            if (className == null) {
                continue;
            }
            Class clazz=null;
            try {
                clazz = Class.forName(className);
                Class lookupClazz = CellComponentUtils.getLookupClass(clazz);
                CellComponentMO comp = this.getComponent(lookupClazz);
                if (comp == null) {
                    Constructor<CellComponentMO> constructor = clazz.getConstructor(CellMO.class);
                    comp = constructor.newInstance(this);
                    comp.setServerState(compState);
                    this.addComponent(comp);
                }
                else {
                    comp.setServerState(compState);
                }
            } catch (InstantiationException ex) {
                logger.log(Level.SEVERE, "Error instantiating "+clazz, ex);
            } catch (IllegalAccessException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                // issue #527: rethrow nested runtime exceptions
                if (ex.getCause() != null &&
                        ex.getCause() instanceof RuntimeException)
                {
                    throw (RuntimeException) ex.getCause();
                }

                logger.log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Returns the setup information currently configured on the cell. If the
     * setup argument is non-null, fill in that object and return it. If the
     * setup argument is null, create a new setup object.
     * 
     * @param setup The setup object, if null, creates one.
     * @return The current setup information
     */
    public CellServerState getServerState(CellServerState setup) {
        // In the case of CellMO, if the 'setup' parameter is null, it means
        // it was not created by the super class. In which case, this class
        // should just return null
        if (setup == null) {
            return null;
        }

        // Set the name of the cell
        setup.setName(this.getName());

        // Fill in the details about the origin, rotation, and scaling. Create
        // and add a PositionComponentServerState with all of this information
        PositionComponentServerState position = new PositionComponentServerState();
        position.setBounds(PositionServerStateHelper.getSetupBounds(localBounds));
        position.setTranslation(PositionServerStateHelper.getSetupOrigin(localTransform));
        position.setRotation(PositionServerStateHelper.getSetupRotation(localTransform));
        position.setScaling(PositionServerStateHelper.getSetupScaling(localTransform));
        setup.addComponentServerState(position);

        // add setups for each component
        for (ManagedReference<CellComponentMO> componentRef : components.values()) {
            CellComponentMO component = componentRef.get();
            CellComponentServerState compSetup = component.getServerState(null);
            if (compSetup != null) {
                setup.addComponentServerState(compSetup);
            }
        }

        return setup;
    }
    
    /**
     * Return the priorty of the cell. A cells priority dictates the order
     * in which it is loaded by a client. Priortity 0 cells are loaded first, 
     * followed by subsequent priority levels. Priority is only a hint to the 
     * client, it has no effect on the server
     * 
     * The default priority is 5
     * 
     * @return
     */
    public short getPriority() {
        return priority;
    }

    /**
     * Set the cell priority. The priority must be >=0 otherwise an 
     * IllegalArgumentException will be thrown.
     * 
     * The default priority is 5
     * 
     * @param priority
     */
    public void setPriority(short priority) {
        if (priority<0)
            throw new IllegalArgumentException("priorty must be >= 0");
        
        this.priority = priority;
    }
    
    /**
     * If this cell supports the capabilities of cellComponent then
     * return an instance of cellComponent associated with this cell. Otherwise
     * return null.
     * 
     * @see MovableCellComponent
     * @param cellComponent
     * @return
     */
    public <T extends CellComponentMO> T getComponent(Class<T> cellComponentClass) {
        assert(CellComponentMO.class.isAssignableFrom(cellComponentClass));
        ManagedReference<CellComponentMO> comp = components.get(cellComponentClass);
        if (comp==null)
            return null;
        return (T) comp.get();
    }

    /**
     * Get all cell components associated with this cell
     * @return a collection of ManagedReferences to cell components
     */
    public Collection<ManagedReference<CellComponentMO>> getAllComponentRefs() {
        return components.values();
    }

    /**
     * Add a component to this cell. Only a single instance of each component
     * class can be added to a cell. Adding duplicate components will result in
     * an IllegalArgumentException 
     * 
     * @param component
     */
    public void addComponent(CellComponentMO component) {
        addComponent(component,
                CellComponentUtils.getLookupClass(component.getClass()));
    }

    public void addComponent(CellComponentMO component, Class componentClass) {
        // Add the component to the map of components. If it already exists,
        // then throw an exception
        ManagedReference<CellComponentMO> previous = components.put(componentClass,
                AppContext.getDataManager().createReference(component));
        if (previous != null)
            throw new IllegalArgumentException("Adding duplicate component of class " + component.getClass().getName());

        // Notify listeners -- note the component is not live at this point
        fireComponentChangeEvent(ComponentChangeListenerSrv.ChangeType.ADDED,
                                 component);

        // If the cell is live, then tell the clients to create all of the
        // components.
        if (live) {
  
            // Loop through and recursively create the components that are listed
            // as dependencies. We get back a set of components that have been
            // created.
            resolveAutoComponentAnnotationsForComponent(AppContext.getDataManager().createReference(component));

            // Send a message to all clients that a new component has been added
            // to the cell. We only need to do this when the cell is live because
            // a setLive(true) will send client states then.
            CellComponentClientState clientState = component.getClientState(null, null, null);
            String className = component.getClientClass();
            sendCellMessage(null, CellClientComponentMessage.newAddMessage(cellID, className, clientState));

            // Finally set the component to the live state
            component.setLive(live);
        }
    }

    /**
     * Removes a component from this cell. If the cell component does not exist
     * on this cell, this method does nothing.
     *
     * @param component The component to remove from this cell
     */
    public void removeComponent(CellComponentMO component) {
        // First tell the component that it is no longer "alive"
        component.setLive(false);

        // Remove the component from the map of components
        Class clazz = CellComponentUtils.getLookupClass(component.getClass());
        components.remove(clazz);

        // Notify listeners
        fireComponentChangeEvent(ComponentChangeListenerSrv.ChangeType.REMOVED,
                                 component);

        // Finally, tell all of the clients to remove the component, if there
        // is a client-side class
        String clientClass = component.getClientClass();
        if (clientClass != null) {
            sendCellMessage(null, CellClientComponentMessage.newRemoveMessage(cellID, clientClass));
        }
    }

    /**
     * Add a component change listener to this cell.  This listener will be
     * notified any time a component is added or removed from this cell.
     * The listener can either be a Serializable object or and instance of
     * ManagedObject.
     * @param listener the listener to add
     */
    public void addComponentChangeListener(ComponentChangeListenerSrv listener) {
        // wrap managed objects
        if (listener instanceof ManagedObject) {
            listener = new ManagedComponentChangeListenerSrv(listener);
        }

        componentChangeListeners.add(listener);
    }

    /**
     * Remove a component change listener.
     * @param listener the listener to remove
     */
    public void removeComponentChangeListener(ComponentChangeListenerSrv listener) {
        // wrap managed objects on remove as well so the comparison in
        // equals works.
        if (listener instanceof ManagedObject) {
            listener = new ManagedComponentChangeListenerSrv(listener);
        }

        componentChangeListeners.remove(listener);
    }

    /**
     * Notification of a component change event
     * @param type the type of event (addition or removal)
     * @param component the component that was added or removed
     */
    protected void fireComponentChangeEvent(ComponentChangeListenerSrv.ChangeType type,
                                            CellComponentMO component)
    {
        for (ComponentChangeListenerSrv listener : componentChangeListeners) {
            listener.componentChanged(this, type, component);
        }
    }

    /**
     * Add a TransformChangeListener to this cell. The listener will be
     * called for any changes to the cells transform. The listener can either
     * be a Serialized object, or an instance of ManagedReference. Both types
     * are handled correctly.
     * 
     * Listeners should generally execute quickly, if they take a long time
     * it is recommended that the listener schedules a new task to service
     * the callback.
     * 
     * @param listener to add
     */
    public void addTransformChangeListener(TransformChangeListenerSrv listener) {
        if (transformChangeListeners==null)
            transformChangeListeners = new HashSet();
        transformChangeListeners.add(listener);

        if (isLive())
            UniverseManagerFactory.getUniverseManager().addTransformChangeListener(this, listener);

    }
    
    /**
     * Remove the specified listener.
     * @param listener to be removed
     */
    public void removeTransformChangeListener(TransformChangeListenerSrv listener) {
	if (transformChangeListeners == null)
	    return;
        transformChangeListeners.remove(listener);
        if (isLive())
            UniverseManagerFactory.getUniverseManager().removeTransformChangeListener(this, listener);
    }

    /**
     * Add a parent change listener to this cell.  This listener will be
     * notified any time the parent of this cell changes.
     * The listener can either be a Serializable object or and instance of
     * ManagedObject.
     * @param listener the listener to add
     */
    public void addParentChangeListener(CellParentChangeListenerSrv listener) {
        // wrap managed objects
        if (listener instanceof ManagedObject) {
            listener = new ManagedCellParentChangeListenerSrv(listener);
        }

        parentChangeListeners.add(listener);
    }

    /**
     * Remove a parent change listener.
     * @param listener the listener to remove
     */
    public void removeParentChangeListener(CellParentChangeListenerSrv listener) {
        // wrap managed objects on remove as well so the comparison in
        // equals works.
        if (listener instanceof ManagedObject) {
            listener = new ManagedCellParentChangeListenerSrv(listener);
        }

        parentChangeListeners.remove(listener);
    }

    /**
     * Notification of a parent change event
     * @param parent the new parent cell (may be null)
     */
    protected void fireParentChangeEvent(CellMO parent)
    {
        for (CellParentChangeListenerSrv listener : parentChangeListeners) {
            listener.parentChanged(this, parent);
        }
    }

     /**
     * Add a children change listener to this cell.  This listener will be
     * notified any time the children of this cell change.
     * The listener can either be a Serializable object or and instance of
     * ManagedObject.
     * @param listener the listener to add
     */
    public void addChildrenChangeListener(CellChildrenChangeListenerSrv listener) {
        // wrap managed objects
        if (listener instanceof ManagedObject) {
            listener = new ManagedCellChildrenChangeListenerSrv(listener);
        }

        childrenChangeListeners.add(listener);
    }

    /**
     * Remove a children change listener.
     * @param listener the listener to remove
     */
    public void removeChildrenChangeListener(CellChildrenChangeListenerSrv listener) {
        // wrap managed objects on remove as well so the comparison in
        // equals works.
        if (listener instanceof ManagedObject) {
            listener = new ManagedCellChildrenChangeListenerSrv(listener);
        }

        childrenChangeListeners.remove(listener);
    }

    /**
     * Notification of a children change event
     * @param child the child cell
     * @param added true if the child was added, or false if it was removed
     */
    protected void fireChildChangedEvent(CellMO child, boolean added) {
        for (CellChildrenChangeListenerSrv listener : childrenChangeListeners) {
            if (added) {
                listener.childAdded(this, child);
            } else {
                listener.childRemoved(this, child);
            }
        }
    }

    /**
     * A utility routine that fetches the channel component of the cell and
     * sends a message on it. If there is no channel component (should never
     * happen), this method logs an error message.
     *
     * @param clientID An optional client-side if the message is in response
     * @param message The CellMessage
     */
    public void sendCellMessage(WonderlandClientID clientID, CellMessage message) {
        ChannelComponentMO channel = getComponent(ChannelComponentMO.class);
        if (channel == null) {
            logger.severe("Unable to find channel on cell id " + getCellID() +
                    " with name " + getName());
            return;
        }
        channel.sendAll(clientID, message);
    }

    /**
     * Inner class to receive messages to get or set the server state of the
     * cell
     */
    private static class ComponentStateMessageReceiver extends AbstractComponentMessageReceiver {

        public ComponentStateMessageReceiver(CellMO cellMO) {
            super(cellMO);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message)
        {
            if (message instanceof CellServerStateRequestMessage) {
                handleGetStateMessage(sender, clientID, (CellServerStateRequestMessage) message);
            }

            if (message instanceof CellServerStateSetMessage) {
                handleSetStateMessage(sender, clientID, (CellServerStateSetMessage) message);
            }

            if (message instanceof CellServerStateUpdateMessage) {
                handleUpdateStateMessage(sender, clientID, (CellServerStateUpdateMessage) message);
            }
        }

        /**
         * Handles when a GET state message is received.
         */
        private void handleGetStateMessage(WonderlandClientSender sender,
                                           WonderlandClientID clientID,
                                           CellServerStateRequestMessage message)
        {
            // If we want to query the cell setup for the given cell ID, first
            // fetch the cell and ask it for its cell setup class. We also
            // want to catch any exception to make sure we send back a
            // response
            CellMO cellMO = getCell();
            CellServerState cellSetup = cellMO.getServerState(null);

            // Formulate a response message, fill in the cell setup, and return
            // to the client.
            MessageID messageID = message.getMessageID();
            CellServerStateResponseMessage response = new CellServerStateResponseMessage(messageID, cellSetup);
            sender.send(clientID, response);
        }

        /**
         * Handles when a SET state message is received.
         */
        private void handleSetStateMessage(WonderlandClientSender sender,
                                           WonderlandClientID clientID,
                                           CellServerStateSetMessage message)
        {
            // Fetch the cell, and set its server state. Catch all exceptions
            // and report.
            CellServerState state = message.getCellServerState();
            CellMO cellMO = getCell();
            cellMO.setServerState(state);

            // Notify the sender that things went OK
            sender.send(clientID, new OKMessage(message.getMessageID()));

            // Fetch a new client-state and set it. Send a message on the
            // cell channel with the new state.
            CellClientState clientState = cellMO.getClientState(null, clientID, null);
            cellMO.sendCellMessage(clientID, new CellClientStateMessage(cellMO.getCellID(), clientState));
        }

        /**
         * Handles when an UPDATE state message is received.
         */
        private void handleUpdateStateMessage(WonderlandClientSender sender,
                WonderlandClientID clientID,
                CellServerStateUpdateMessage message)
        {
            CellMO cellMO = getCell();
            CellID cellID = cellMO.getCellID();

            // Fetch the cell, and set its server state. Catch all exceptions
            // and report. This assumes that all components have been removed
            // from the server state object, since they are handled separately
            // below. The client needs to remove the component state objects
            // to save network bandwidth in the message size.
            CellServerState state = message.getCellServerState();
            if (state != null) {
                cellMO.setServerState(state);
            }

            // Fetch the set of cell component server states. For each, update
            // them individually. We need to fetch the component server state
            // from the cell first. If an existing cell component server state
            // does not already exist, then log a message and ignore.
            Set<CellComponentServerState> compSet = message.getCellComponentServerStateSet();
            if (compSet != null) {
                for (CellComponentServerState compState : compSet) {
                    CellComponentMO componentMO = null;
                    try {
                        String className = compState.getServerComponentClassName();
                        Class clazz = Class.forName(className);

                        // OWL issue #66: be sure to use the lookup class
                        Class lookupClass = CellComponentUtils.getLookupClass(clazz);
                        componentMO = cellMO.getComponent(lookupClass);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (componentMO == null) {
                        logger.warning("Unable to find CellComponentMO for " +
                                compState.getServerComponentClassName() + " on Cell " +
                                cellMO.getName() + " of type " +
                                cellMO.getClass().getName());
                        continue;
                    }

                    // Otherwise, set the state of the component
                    componentMO.setServerState(compState);
                }
            }

            // Notify the sender that things went OK
            sender.send(clientID, new OKMessage(message.getMessageID()));

            // Fetch a new client-state and set it. Send a message on the
            // cell channel with the new state.
            CellClientState clientState = cellMO.getClientState(null, clientID, null);
            CellClientStateMessage ccsm = new CellClientStateMessage(cellID, clientState);
            cellMO.sendCellMessage(clientID, ccsm);
        }
    }

    /**
     * Inner class to receive messages to dynamically add and remove components
     */
    private static class ComponentMessageReceiver extends AbstractComponentMessageReceiver {

        public ComponentMessageReceiver(CellMO cellMO) {
            super(cellMO);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            // Dispatch to either the "add" or "remove" message handler
            CellServerComponentMessage cm = (CellServerComponentMessage)message;
            switch (cm.getComponentAction()) {
                case ADD:
                    handleAddComponentMessage(sender, clientID, cm);
                    break;

                case REMOVE:
                    handleRemoveComponentMessage(sender, clientID, cm);
                    break;
            }
        }

        /**
         * Handles an "add" message by creating and adding the component.
         */
        private void handleAddComponentMessage(WonderlandClientSender sender,
                WonderlandClientID clientID, CellServerComponentMessage message) {

            // Fetch the initial component server state and the class name of
            // the component to create.
            CellComponentServerState state = message.getCellComponentServerState();
            String className = message.getCellComponentServerClassName();
            CellMO cellMO = getCell();

            try {
                // Try to create the cmponent class and add it if the component
                // does not exist. Upon success, return a message
                Class clazz = Class.forName(className);
                Class lookupClazz = CellComponentUtils.getLookupClass(clazz);
                CellComponentMO comp = cellMO.getComponent(lookupClazz);
                if (comp == null) {
                    // Create the component class, set its state, and add it
                    Constructor<CellComponentMO> constructor = clazz.getConstructor(CellMO.class);
                    comp = constructor.newInstance(cellMO);
                    comp.setServerState(state);
                    cellMO.addComponent(comp);

                    // Now ask the component for its server state.  This may
                    // be different than the one we just set, for example if
                    // the cell does some work to calculate new properties when
                    // the state is set.  If the component returns null, just
                    // use the state that was passed in to avoid errors.
                    CellComponentServerState newState = comp.getServerState(null);
                    if (newState != null) {
                        state = newState;
                    }

                    // Send a response message back to the client indicating
                    // success
                    sender.send(clientID, new CellServerComponentResponseMessage(
                                                    message.getMessageID(), state));
                } else {
                    // Otherwise, the component already exists, so send an error
                    // message back to the client.
                    sender.send(clientID, new ErrorMessage(message.getMessageID(),
                               "The Component " + className + " already exists."));
                    return;
                }
            } catch (java.lang.Exception excp) {
                // Rethrow runtime exceptions so we don't mess up Darkstar
                if (excp instanceof RuntimeException) {
                    throw (RuntimeException) excp;
                }

                // Log an error in the log and send back an error message.
                logger.log(Level.WARNING, "Unable to add component " +
                        className + " for cell " + cellMO.getName(), excp);
                sender.send(clientID, new ErrorMessage(message.getMessageID(), excp));
                return;
            }

            // If we made it here, everything worked.  Send the updated server
            // state object to all clients as an asynchronous event
            CellServerComponentMessage out = message;
            if (state != null) {
                out = new CellServerComponentMessage(message.getCellID(), state);
            }
            cellMO.sendCellMessage(clientID, out);
        }

        /**
         * Handles a "remove" message by removing the component
         */
        private void handleRemoveComponentMessage(WonderlandClientSender sender,
                WonderlandClientID clientID, CellServerComponentMessage message) {

            // Fetch the server-side component class name and remove the
            // component. Upon success, send a general "ok" message.
            try {
                // Find the component on the cell. If it is not present, then
                // send back an error message.
                CellMO cellMO = getCell();
                String className = message.getCellComponentServerClassName();
                Class clazz = CellComponentUtils.getLookupClass(Class.forName(className));
                CellComponentMO component = cellMO.getComponent(clazz);
                if (component == null) {
                    logger.warning("Cannot find component for class " + className);
                    sender.send(clientID, new ErrorMessage(message.getMessageID()));
                    return;
                }

                // Remove the component and send a success message back to the
                // client
                cellMO.removeComponent(component);
                sender.send(clientID, new OKMessage(message.getMessageID()));

                // Send the same event message to all clients as an asynchronous
                // event
                cellMO.sendCellMessage(clientID, message);
                
            } catch (java.lang.ClassNotFoundException excp) {
                // Just got an exception and ignore here
                logger.log(Level.WARNING, "Cannot find component class", excp);
                sender.send(clientID, new ErrorMessage(message.getMessageID(), excp));
            }
        }
    }

    /** Wrapper to use a managed object to notify a listener */
    private static class ManagedComponentChangeListenerSrv
        implements ComponentChangeListenerSrv
    {
        private ManagedReference<ComponentChangeListenerSrv> ref;

        public ManagedComponentChangeListenerSrv(ComponentChangeListenerSrv listener) {
            ref = AppContext.getDataManager().createReference(listener);
        }

        public void componentChanged(CellMO cell, ChangeType type,
                                     CellComponentMO component)
        {
            ref.get().componentChanged(cell, type, component);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ManagedComponentChangeListenerSrv)) {
                return false;
            }

            return ref.equals(((ManagedComponentChangeListenerSrv) o).ref);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + (this.ref != null ? this.ref.hashCode() : 0);
            return hash;
        }
    }

    /** Wrapper to use a managed object to notify a listener */
    private static class ManagedCellParentChangeListenerSrv
        implements CellParentChangeListenerSrv
    {
        private ManagedReference<CellParentChangeListenerSrv> ref;

        public ManagedCellParentChangeListenerSrv(CellParentChangeListenerSrv listener) {
            ref = AppContext.getDataManager().createReference(listener);
        }

        public void parentChanged(CellMO cell, CellMO parent) {
            ref.get().parentChanged(cell, parent);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ManagedCellParentChangeListenerSrv)) {
                return false;
            }

            return ref.equals(((ManagedCellParentChangeListenerSrv) o).ref);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + (this.ref != null ? this.ref.hashCode() : 0);
            return hash;
        }
    }

    /** Wrapper to use a managed object to notify a listener */
    private static class ManagedCellChildrenChangeListenerSrv
        implements CellChildrenChangeListenerSrv
    {
        private ManagedReference<CellChildrenChangeListenerSrv> ref;

        public ManagedCellChildrenChangeListenerSrv(CellChildrenChangeListenerSrv listener) {
            ref = AppContext.getDataManager().createReference(listener);
        }

        public void childAdded(CellMO cell, CellMO child) {
            ref.get().childAdded(cell, child);
        }

        public void childRemoved(CellMO cell, CellMO child) {
            ref.get().childRemoved(cell, child);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ManagedCellChildrenChangeListenerSrv)) {
                return false;
            }

            return ref.equals(((ManagedCellChildrenChangeListenerSrv) o).ref);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + (this.ref != null ? this.ref.hashCode() : 0);
            return hash;
        }
    }
}

