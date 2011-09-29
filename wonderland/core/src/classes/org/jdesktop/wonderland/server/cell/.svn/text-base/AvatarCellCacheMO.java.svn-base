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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.util.Properties;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.server.cell.view.ViewCellMO;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.ObjectNotFoundException;
import com.sun.sgs.app.PeriodicTaskHandle;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.TaskManager;
import com.sun.sgs.app.util.ScalableList;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellHierarchyMessage;
import org.jdesktop.wonderland.common.cell.messages.CellHierarchyUnloadMessage;
import org.jdesktop.wonderland.common.messages.MessageList;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.spatial.UniverseManagerFactory;

/**
 * Container for the cell cache for an avatar.
 *
 * Calculates the set of cells that the client needs to load and sends the
 * information to the client.
 *
 * This is a nieve implementation that does not contain View Frustum culling,
 * culling is performed only on relationship to users position.
 *
 * @author paulby
 * @author Bernard Horan
 */
@InternalAPI
public class AvatarCellCacheMO extends ViewCellCacheMO implements ManagedObject, Serializable {
    
    private final static Logger logger = Logger.getLogger(AvatarCellCacheMO.class.getName());
    
    protected WonderlandClientSender sender;
    protected WonderlandClientID clientID;
    
    protected WonderlandIdentity identity;

    protected ClientCapabilities capabilities = null;
         
    private PeriodicTaskHandle task = null;
    
    // handle revalidates
    protected RevalidateScheduler scheduler;
    
    // whether or not to aggregate messages
    private static final boolean AGGREGATE_MESSAGES = true;
            
    private HashSet<ViewCellCacheRevalidationListener> revalidationsListeners = new HashSet();

    private Properties connectionProperties = new Properties();
    private static final String INITIAL_POSITION_PROP_PREFIX = "view.initial.";

    /**
     * Creates a new instance of AvatarCellCacheMO
     */
    public AvatarCellCacheMO(AvatarCellMO view) {
        super(view);
        logger.info("Creating AvatarCellCache");
        
        identity = view.getUser().getIdentity();

//        dm.setBinding(identity.getUsername() + "_CELL_CACHE", this);
    }
    
    /**
     * Notify CellCache that user has logged in
     */
    public void login(WonderlandClientSender sender, WonderlandClientID clientID) {
        this.sender = sender;
        this.clientID = clientID;

        ViewCellMO view = getViewCell();

        // see if there is an initial position specified
        CellTransform xform = getInitialPosition();
        if (xform != null) {
            view.setLocalTransform(xform);
        }

        if (!view.isLive()) {
            try {
                WonderlandContext.getCellManager().insertCellInWorld(view);
            } catch (MultipleParentException ex) {
                Logger.getLogger(ViewCellCacheMO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        UniverseManagerFactory.getUniverseManager().viewLogin(view);

        // issue 963: session could be null if client is in the process of
        // logging out
        String name = (clientID.getSession() == null)?"null":clientID.getSession().getName();
        logger.info("AvatarCellCacheMO.login() CELL CACHE LOGIN FOR USER "
                    + name + " AS " + identity.getUsername());

        // set up the revalidate scheduler
        scheduler = new SharedListRevalidateScheduler(sender, 5);
    }
    
    /**
     * Notify CellCache that user has logged out
     */
    protected void logout(WonderlandClientID clientID) {
        logger.warning("DEBUG - logout");
        ViewCellMO view = getViewCell();
        UniverseManagerFactory.getUniverseManager().viewLogout(view);
        WonderlandContext.getCellManager().removeCellFromWorld(view);
    }
    
    /**
     * Get the initial position for this view based on the connection
     * properties
     * @return the initial transform, or null if there is no initial 
     * transform
     */
    protected CellTransform getInitialPosition() {
        float transX = 0f;
        float transY = 0f;
        float transZ = 0f;

        String transXStr = connectionProperties.getProperty(INITIAL_POSITION_PROP_PREFIX + "x");
        if (transXStr != null) {
            transX = Float.parseFloat(transXStr);
        }

        String transYStr = connectionProperties.getProperty(INITIAL_POSITION_PROP_PREFIX + "y");
        if (transYStr != null) {
            transY = Float.parseFloat(transYStr);
        }

        String transZStr = connectionProperties.getProperty(INITIAL_POSITION_PROP_PREFIX + "z");
        if (transZStr != null) {
            transZ = Float.parseFloat(transZStr);
        }

        Vector3f translation = new Vector3f(transX, transY, transZ);

        float rotX = 0f;
        float rotY = 0f;
        float rotZ = 0f;

        String rotXStr = connectionProperties.getProperty(INITIAL_POSITION_PROP_PREFIX + "rotx");
        if (rotXStr != null) {
            rotX = Float.parseFloat(rotXStr);
        }

        String rotYStr = connectionProperties.getProperty(INITIAL_POSITION_PROP_PREFIX + "roty");
        if (rotYStr != null) {
            rotY = Float.parseFloat(rotYStr);
        }

        String rotZStr = connectionProperties.getProperty(INITIAL_POSITION_PROP_PREFIX + "rotz");
        if (rotZStr != null) {
            rotZ = Float.parseFloat(rotZStr);
        }

        Quaternion rotate = new Quaternion();
        rotate.fromAngles(rotX, rotY, rotZ);

        return new CellTransform(rotate, translation);
    }

    

    protected void sendLoadMessages(Collection<CellDescription> cells) {
        if (logger.isLoggable(Level.FINE)) {
            StringBuffer logBuf = new StringBuffer(getViewCell().getCellID() +
                                                   " send load messages: ");
            for (CellDescription desc : cells) {
                logBuf.append(desc.getCellID() + " ");
            }
            logger.fine(logBuf.toString());
        }

        ManagedReference<AvatarCellCacheMO> viewCellCacheRef =
                AppContext.getDataManager().createReference(this);

        scheduler.startRevalidate();
        for(CellDescription cellDescription : cells) {
            // if we haven't already loaded the cell, send a message
            if (setLoaded(cellDescription.getCellID())) {
               
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("Entering cell " + cellDescription.getCellID() +
                                 " cellcache for user " + identity.getUsername());
                }

                CellLoadOp op = new CellLoadOp(cellDescription, clientID,
                                               viewCellCacheRef, capabilities);
                scheduler.schedule(op);
            }
        }
        scheduler.endRevalidate();
    }


    protected void sendUnloadMessages(Collection<CellDescription> removeCells) {
        if (logger.isLoggable(Level.FINE)) {
            StringBuffer logBuf = new StringBuffer(getViewCell().getCellID() +
                                                   " send unload messages: ");
            for (CellDescription desc : removeCells) {
                logBuf.append(desc.getCellID() + " ");
            }
            logger.fine(logBuf.toString());
        }

        ManagedReference<AvatarCellCacheMO> viewCellCacheRef =
                AppContext.getDataManager().createReference(this);


        scheduler.startRevalidate();
        // oldCells contains the set of cells to be removed from client memory
        for(CellDescription ref : removeCells) {
            if (setUnloaded(ref.getCellID())) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.fine("Leaving cell " + ref.getCellID() +
                                " cellcache for user "+identity.getUsername());
                }

                // schedule the remove operation
                CellUnloadOp op = new CellUnloadOp(ref, clientID,
                                                   viewCellCacheRef,
                                                   capabilities);
                scheduler.schedule(op);
            }
        }
        scheduler.endRevalidate();
    }

    void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    /**
     * Return the property from the connection handler for this cache.
     * The properties are set when the client creates the connection
     * 
     * @param key
     * @return
     */
    public String getConnectionProperty(String key) {
        if (connectionProperties==null)
            return null;

        return connectionProperties.getProperty(key);
    }


    void addRevalidationListener(ViewCellCacheRevalidationListener listener) {
        // Called from the scheduler via a reference so does not need synchronization
        revalidationsListeners.add(listener);
    }
    
    void removeRevalidationListener(ViewCellCacheRevalidationListener listener) {
        // Called from the scheduler via a reference so does not need synchronization
        revalidationsListeners.remove(listener);
    }
    
    /**
     * Utility to get the session
     */
    protected ClientSession getSession() {
        try {
            return clientID.getSession();
        } catch(ObjectNotFoundException e) {
            return null;
        }
    }

       /**
      * Getter for the WonderlandClientID property. Needed this mapping in a
      * proximity listener when all I had was the viewCellID and needed
      * to know their ClientId.
      *
      * @return The WonderlandClientID associated with this ViewCell.
      */
     public WonderlandClientID getClientID() {
         return this.clientID;
     }
            

    private static class OpsList extends ScalableList<CellOp> {
        // whether or not there is already a worker task in progress
        private boolean taskRunning = false;

        public boolean isTaskRunning() {
            return taskRunning;
        }

        public void setTaskRunning(boolean taskRunning) {
            this.taskRunning = taskRunning;

            AppContext.getDataManager().markForUpdate(this);
        }
    }

    /**
     * Superclass of operations to modify the list of cached cells.  Operations
     * include adding, removing or updating the list of cells.
     */
    protected static abstract class CellOp
            implements Serializable, Runnable 
    {
        protected CellDescription desc;
        protected WonderlandClientID clientID;
        protected ManagedReference<? extends AvatarCellCacheMO> viewCellCacheRef;
        protected ClientCapabilities capabilities;
        
        // optional message list.  If the list is not null, messages will
        // be added to the list instead of sent immediately.  This is
        // set by the RevalidateScheduler prior to calling run()
        private MessageList messageList;
        
        // optional sender.  If the sender is not null, messages will be
        // sent immediately.  This is set by the RevalidateScheduler
        // prior to calling run.
        private WonderlandClientSender sender;
    
        public CellOp(CellDescription desc,
                      WonderlandClientID clientID,
                      ManagedReference<? extends AvatarCellCacheMO> viewCellCacheRef,
                      ClientCapabilities capabilities) 
        {
            this.desc = desc;
            this.clientID = clientID;
            this.viewCellCacheRef = viewCellCacheRef;
            this.capabilities = capabilities;
        }
        
        public void setMessageList(MessageList messageList) {
            this.messageList = messageList;
        }
        
        public void setClientSender(WonderlandClientSender sender) {
            this.sender = sender;
        }
        
        protected void sendMessage(CellHierarchyMessage message) {
            if (messageList != null) {
                // if there is a message list, use it to aggregate messages
                messageList.addMessage(message);
            } else {
                // no list, send immediately
                sender.send(clientID, message);
            }
        }
    }
    
    /**
     * Operation to add a cell to the set of cached cells
     */
    public static class CellLoadOp extends CellOp {
        public CellLoadOp(CellDescription desc,
                         WonderlandClientID clientID,
                         ManagedReference<? extends AvatarCellCacheMO> viewCellCacheRef,
                         ClientCapabilities capabilities) {
            super (desc, clientID, viewCellCacheRef, capabilities);
        }
        
        public void run() {
            // the cell is new -- add it and send a message
            CellMO cell = CellManagerMO.getCell(desc.getCellID());

            // issue #950 -- make sure the cell is live before adding any
            // clients
            if (!cell.isLive()) {
                return;
            }

            //System.out.println("SENDING "+msg.getActionType()+" "+msg.getBytes().length);
            CellSessionProperties prop = cell.addClient(clientID, capabilities);
            
            ViewCellCacheRevalidationListener listener = prop.getViewCellCacheRevalidationListener();
            if (listener != null) {
                viewCellCacheRef.getForUpdate().addRevalidationListener(listener);
            }
//            cellRef.setCellSessionProperties(prop);
                    
            logger.fine("Sending NEW CELL to Client: " + cell.getCellID().toString()+"  "+cell.getClass().getName());
            sendMessage(newCreateCellMessage(cell, prop));
        }
    }
    
    /**
     * Operation to remove a cell from the list of cached cells
     */
    protected static class CellUnloadOp extends CellOp {
        public CellUnloadOp(CellDescription desc,
                         WonderlandClientID clientID,
                         ManagedReference<? extends AvatarCellCacheMO> viewCellCacheRef,
                         ClientCapabilities capabilities) {
            super (desc, clientID, viewCellCacheRef, capabilities);
        }
        
        public void run() {
            CellHierarchyMessage msg;
                    
            // the cell may be inactive or removed.  Try to get the cell,
            // and catch the exception if it no longer exists.
            try {
                CellMO cell = CellManagerMO.getCellManager().getCell(desc.getCellID());

                cell.removeSession(clientID);

                ViewCellCacheRevalidationListener listener = cell.getViewCellCacheRevalidationListener();
                if (listener!=null) {
                    viewCellCacheRef.getForUpdate().removeRevalidationListener(listener);
                }
            
                // get suceeded, so cell is just inactive
                msg = newUnloadCellMessage(cell);
                cell.removeSession(clientID);
            } catch (ObjectNotFoundException onfe) {
                // get failed, cell is deleted
                msg = newDeleteCellMessage(desc.getCellID());
            }

            sendMessage(msg);
            //System.out.println("SENDING "+msg.getClass().getName()+" "+msg.getBytes().length);

        }
    }
    
    /**
     * A revalidate scheduler defines how the various revalidate operations
     * are managed.  Some schedulers will perform the operations immediately,
     * while others will try to batch them up in a single task.
     */
    public interface RevalidateScheduler {
        public void startRevalidate();
        public void schedule(CellOp op);
        public void endRevalidate();
    }
    
    /**
     * Do nothing.  This will break the system, but is good for testing by
     * ignoring the updates.
     */
    private class NoopRevalidateScheduler
            implements RevalidateScheduler, Serializable
    {
        public void startRevalidate() {}
        public void schedule(CellOp op) {}
        public void endRevalidate() {}
    }
    
    /**
     * Perform all revalidate operations immediately in this task.
     */
    public class ImmediateRevalidateScheduler
            implements RevalidateScheduler, Serializable 
    {
        // the sender to send to
        private WonderlandClientSender sender;
        
        // the client to send to
        private WonderlandClientID clientID;

        // the message list
        private MessageList messageList;
        
        
        public ImmediateRevalidateScheduler(WonderlandClientSender sender,
                                            WonderlandClientID clientID)
        {
            this.sender = sender;
            this.clientID = clientID;
        }
        
        public void startRevalidate() {
            if (AGGREGATE_MESSAGES) {
                messageList = new MessageList();
            }
        }
        
        public void schedule(CellOp op) {
            if (AGGREGATE_MESSAGES) {
                op.setMessageList(messageList);
            } else {
                op.setClientSender(sender);
            }
            
            op.run();
        }
        
        public void endRevalidate() {
            if (AGGREGATE_MESSAGES) {                
                sender.send(clientID, messageList);
            }
        }
    }
    
    /**
     * Write revalidate requests to a shared list of operations to run.
     * Schedule a task to read the list and perform some number of operations.
     * The count variable in the constructor controls how many operations
     * each task should consume before scheduling another task to complete
     * the remaining operations.
     */
    private class SharedListRevalidateScheduler 
            implements RevalidateScheduler, Serializable 
    {
        // the sender to send to
        private final WonderlandClientSender sender;
        
        // the number of tasks to consume during each run
        private final int count;
        
        // a reference to the shared list of operations
        private final ManagedReference<OpsList> opsRef;
        
        public SharedListRevalidateScheduler(WonderlandClientSender sender,
                                             int count)
        {
            this.sender = sender;
            this.count = count;
            
            // create managed references
            DataManager dm = AppContext.getDataManager();
            opsRef = dm.createReference(new OpsList());
        }
        
        public void startRevalidate() {    
        }
        
        public void schedule(CellOp op) {
            opsRef.getForUpdate().add(op);
        }

        public void endRevalidate() {            
            logger.fine("Schedule " + opsRef.get().size() + " tasks " +
                        " with count " + count + " running: " +
                        opsRef.get().isTaskRunning());

            // schedule tasks to handle up to count operations
            if (!(opsRef.get().isTaskRunning())) {
                opsRef.get().setTaskRunning(true);

                TaskManager tm = AppContext.getTaskManager();
                tm.scheduleTask(new SharedListRevalidateTask(sender, clientID,
                                                             count, opsRef));
            }
        }
    }
    
    /**
     * A task to dequeue the next operations from the shared list and
     * execute them.
     */
    private static class SharedListRevalidateTask
            implements Task, Serializable
    {
        private final WonderlandClientSender sender;
        private final WonderlandClientID clientID;
        private final ManagedReference<OpsList> opsRef;
        private final int count;
        private MessageList messageList;
        
        public SharedListRevalidateTask(WonderlandClientSender sender,
                                        WonderlandClientID clientID,
                                        int count, 
                                        ManagedReference<OpsList> opsRef)
        {
            this.sender = sender;
            this.clientID = clientID;
            this.count = count;
            this.opsRef = opsRef;
        }

        public void run() throws Exception {
            List<CellOp> ops = opsRef.get();
            
            if (AGGREGATE_MESSAGES) {
                messageList = new MessageList();
            }

            int size = ops.size();
            int num = Math.min(size, count);
            for (int i = 0; i < num; i++) {
                CellOp op = ops.remove(0);
                
                if (AGGREGATE_MESSAGES) {
                    op.setMessageList(messageList);
                } else {
                    op.setClientSender(sender);
                }
                
                op.run();
            }
            
            // send all messages
            if (AGGREGATE_MESSAGES) {
                sender.send(clientID, messageList);
            }
            
            // schedule a task to handle more
            if (size - num > 0) {
                logger.fine("Continue " + (size - num) + " remaining tasks " +
                            " with count " + count);

                TaskManager tm = AppContext.getTaskManager();
                tm.scheduleTask(new SharedListRevalidateTask(sender, clientID,
                                                             count, opsRef));
            } else {
                logger.fine("Done running task");
                opsRef.get().setTaskRunning(false);
            }
        }
    }

    

    /**
     * Return a new Create cell message
     */
    public static CellHierarchyMessage newCreateCellMessage(CellMO cell, CellSessionProperties properties) {
        CellID parent=null;
        
        CellMO p = cell.getParent();
        if (p!=null) {
            parent = p.getCellID();
        }
        
        return new CellHierarchyMessage(CellHierarchyMessage.ActionType.LOAD_CELL,
            properties.getClientCellClassName(),
            cell.getLocalBounds(),
            cell.getCellID(),
            parent,
            cell.getLocalTransform(null),
            properties.getClientCellSetup(),
            cell.getName()
            
            
            );
    }
    
    /**
     * Return a new LoadLocalAvatar cell message
     */
    public static CellHierarchyMessage newLoadLocalAvatarMessage(CellMO cell, CellSessionProperties properties) {
        CellID parent=null;
        
        CellMO p = cell.getParent();
        if (p!=null) {
            parent = p.getCellID();
        }
        
        return new CellHierarchyMessage(CellHierarchyMessage.ActionType.LOAD_CLIENT_AVATAR,
            properties.getClientCellClassName(),
            cell.getLocalBounds(),
            cell.getCellID(),
            parent,
            cell.getLocalTransform(null),
            properties.getClientCellSetup(),
            cell.getName()
            
            
            );
    }
    
    /**
     * Return a new Cell inactive message
     */
    public static CellHierarchyUnloadMessage newUnloadCellMessage(CellMO cell) {
        return new CellHierarchyUnloadMessage(cell.getCellID());
    }
    
    /**
     * Return a new Delete cell message
     */
    public static CellHierarchyMessage newDeleteCellMessage(CellID cellID) {
        return new CellHierarchyMessage(CellHierarchyMessage.ActionType.DELETE_CELL,
            null,
            null,
            cellID,
            null,
            null,
            null,
            null
            );
    }
    
    /**
     * Return a new Delete cell message
     */
//    public static CellHierarchyMessage newChangeParentCellMessage(CellMO childCell, CellMO parentCell) {
//        return new CellHierarchyMessage(CellHierarchyMessage.ActionType.CHANGE_PARENT,
//            null,
//            null,
//            childCell.getCellID(),
//            parentCell.getCellID(),
//            null,
//            null,
//            null
//            
//            );
//    }
    
    /**
     * Return a new cell update message. Indicates that the content of the cell
     * has changed.
     */
    public static CellHierarchyMessage newConfigureCellMessage(CellMO cellMO, ClientCapabilities capabilities) {
        CellID parentID = null;
        if (cellMO.getParent() != null) {
            parentID = cellMO.getParent().getCellID();
        }
        
        /* Return a new CellHiearchyMessage class, with populated data fields */
        return new CellHierarchyMessage(CellHierarchyMessage.ActionType.CONFIGURE_CELL,
            cellMO.getClientCellClassName(null,capabilities),
            cellMO.getLocalBounds(),
            cellMO.getCellID(),
            parentID,
            cellMO.getLocalTransform(null),
            cellMO.getClientState(null, null, capabilities),
            cellMO.getName()
            
            );
    }
}

