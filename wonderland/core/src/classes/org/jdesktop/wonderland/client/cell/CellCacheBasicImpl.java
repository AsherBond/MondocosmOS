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
package org.jdesktop.wonderland.client.cell;

import com.jme.bounding.BoundingVolume;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.CellStatistics.LongCellStat;
import org.jdesktop.wonderland.client.cell.CellStatistics.TimeCellStat;
import org.jdesktop.wonderland.client.cell.TransformChangeListener.ChangeSource;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.common.cell.state.CellClientState;

/**
 * A basic implementation of core cell cache features. This is a convenience class
 * designed to be called from more complete cache implementations. 
 * 
 * @author paulby
 */
public class CellCacheBasicImpl implements CellCache, CellCacheConnection.CellCacheMessageListener {

    private Map<CellID, Cell> cells = Collections.synchronizedMap(new HashMap());
    private Set<Cell> rootCells = Collections.synchronizedSet(new HashSet());
    
    protected static Logger logger = Logger.getLogger(CellCacheBasicImpl.class.getName());
    
    private ViewCell viewCell = null;

    /** the classloader to use when instantiating classes */
    private ClassLoader classLoader;
    
    /** the session this cache is associated with */
    private WonderlandSession session;
    
    /** the connection for sending cell cache information */
    private CellCacheConnection cellCacheConnection;
    
    /** the connection for sending cell information */
    private CellChannelConnection cellChannelConnection;

    /** executor for modifying values in separate threads */
    private ExecutorService cacheExecutor = Executors.newSingleThreadExecutor();

    /** listeners */
    private final Set<CellCacheListener> listeners =
            new CopyOnWriteArraySet<CellCacheListener>();

    /** statistics */
    private final CellStatistics stats = new CellStatistics();

    /**
     * Create a new cache implementation
     * @param session the WonderlandSession the cache is associated with
     * @param cellCacheConnection the connection for sending cell cache
     * information
     * @param cellChannelConnection the connectiong for sending cell channel
     * messages
     */
    public CellCacheBasicImpl(WonderlandSession session,
                              ClassLoader classLoader,
                              CellCacheConnection cellCacheConnection,
                              CellChannelConnection cellChannelConnection) {
        this.session = session;
        this.classLoader = classLoader;
        this.cellCacheConnection = cellCacheConnection;

        this.cellChannelConnection = cellChannelConnection;
        this.cellChannelConnection.setCellCache(this);

        ClientContext.registerCellCache(this, session);
        if (this.classLoader == null) {
            this.classLoader = getClass().getClassLoader();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Cell getCell(CellID cellId) {
        return cells.get(cellId);
    }

    /**
     * {@inheritDoc}
     */
    public EnvironmentCell getEnvironmentCell() {
        return (EnvironmentCell) getCell(CellID.getEnvironmentCellID());
    }

    /**
     * Return the set of cells in this cache
     * @return
     */
    public Cell[] getCells() {
        return (Cell[]) cells.values().toArray(new Cell[cells.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public Cell loadCell(CellID cellId, 
                         String className, 
                         BoundingVolume localBounds, 
                         CellID parentCellID, 
                         CellTransform cellTransform, 
                         CellClientState setup,
                         String cellName) {

        long startTime = System.currentTimeMillis();

        try {
            if (cells.containsKey(cellId)) {
                logger.severe("Attempt to create cell that already exists "+cellId);
                return null;
            }

            logger.info("creating cell "+className+" "+cellId);
            Cell cell = instantiateCell(className, cellId);
            if (cell==null)
                return null;     // Instantiation failed, error has already been logged

            cell.setName(cellName);
            Cell parent = cells.get(parentCellID);
            if (parent != null) {
                try {
                    parent.addChild(cell);
                } catch (MultipleParentException ex) {
                    logger.log(Level.SEVERE, "Failed to load cell", ex);
                }
            } else if (parentCellID != null) {
                logger.warning("Failed to find parent " + parentCellID +
                               " for child " + cellId);
            }

            cell.setLocalBounds(localBounds);
            cell.setLocalTransform(cellTransform, TransformChangeListener.ChangeSource.SERVER_ADJUST);
    //        System.out.println("Loading Cell "+className+" "+cellTransform.getTranslation(null));

            cells.put(cellId, cell);

            // record the set of root cells
            logger.fine("LOADING CELL " + cell.getName());
            if (parent == null && !cellId.equals(CellID.getEnvironmentCellID())) {
                logger.fine("LOADING ROOT CELL " + cell.getName());
                rootCells.add(cell);
            }

            // TODO this will change, the state will applied when the cell
            // becomes ACTIVE
            if (setup!=null)
                cell.setClientState(setup);
            else
                logger.warning("Cell has null setup "+className+"  "+cell);

            // Force the cell to create the JME renderer entity
            // Current assumption is that the cell is about to be VISIBLE, so we want the renderer asap
//            createCellRenderer(cell);

            // notify listeners
            fireCellLoaded(cell);

            if (viewCell!=null) {
                // No point in makeing cells active if we don't have a view
                // The changeCellStatus actually changes the status on another thread
                // so we don't perform geometry load operations on the DS listener thread
                changeCellStatus(cell, CellStatus.VISIBLE);
            } else if (cell instanceof ViewCell || 
                       cellId.equals(CellID.getEnvironmentCellID()))
            {
                changeCellStatus(cell, CellStatus.ACTIVE);
            }

            // record loading time statistic
            long time = System.currentTimeMillis() - startTime;
            TimeCellStat loadStat =
                    new TimeCellStat("LoadTime", "Cell Load Time");
            loadStat.setValue(time);
            getStatistics().add(cell, loadStat);

            return cell;
        } catch(Exception e) {
            // notify listeners
            fireCellLoadFailed(cellId, className, parentCellID, e);
            logger.log(Level.SEVERE, "Failed to loadCell", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void configureCell(CellID cellID, CellClientState clientState, String cellName) {
        // First fetch the cell from the cache given the unique ID. If there
        // is none, post a message to the log
        Cell cell = cells.get(cellID);
        if (cell == null) {
            logger.warning("Received a CONFIGURE_CELL message to a non-" +
                    "existent cell with id " + cellID);
            return;
        }

        // If the name of the cell has changed, then change it
        if (cellName != null && cellName.equals(cell.getName()) == false) {
            cell.setName(cellName);
        }

        // If there is a non-null client state object, then set it
        if (clientState != null) {
            cell.setClientState(clientState);
        }
    }


    /**
     * Create a the cell renderer for this cache.
     * @param cell the cell to create a renderer for
     */
    protected CellRenderer createCellRenderer(Cell cell) {
        try {
            return cell.getCellRenderer(ClientContext.getRendererType());
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Failed to get Cell Renderer for cell "+cell.getClass().getName(), e);
        }
        return null;
    }

    /**
     * Unload the cell from memory, sets the Cells status to DISK
     * @param cellId
     */
    public void unloadCell(CellID cellId) {
        Cell cell = cells.remove(cellId);
        if (cell != null) {
            logger.fine("UNLOADING CELL " + cell.getName());

            // notify listeners
            fireCellUnloaded(cell);

            // OWL issue #37: make sure to update the status on a separate
            // thread to prevent deadlocks waiting for update messages and
            // races with loading the cell
            changeCellStatus(cell, CellStatus.DISK);

            if (cell.getParent() == null) {
                logger.fine("UNLOADING ROOT CELL " + cell.getName());
                rootCells.remove(cell);
            } else {
                cell.getParent().removeChild(cell);
            }
        } else {
            logger.log(Level.WARNING, "Unloading unknown cell " + cellId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteCell(CellID cellId) {
        // TODO - remove local resources from client asset cache as long
        // as they are not shared
        unloadCell(cellId);
    }

    /**
     * TODO - this is not used yet, we are reparenting by removing and adding
     * nodes as a test to ensure there are no threading issues
     *
     * {@inheritDoc}
     */
    public void changeParent(CellID cellID, CellID parentCellID, CellTransform cellTransform) {
        // Find the current parent of the Cell and remove the Cell. If the Cell
        // does not exist in the cache, then do nothing. It could perhaps be
        // created on the client at a later date.
        logger.warning("Changing the parent of Cell with ID " + cellID);
        logger.warning("The ID of the new parent " + parentCellID);
        logger.warning("The new transform is " + cellTransform.toString());

        Cell cell = cells.get(cellID);
        if (cell == null) {
            logger.warning("Unable to find Cell in Cache with ID " + cellID);
            return;
        }

        // First, remove the Cell from its parent, if the parent is not null. If
        // the parent is null, this means it is a "root" Cell and we need to
        // remove it from the list of root Cells.
        Cell parentCell = cell.getParent();
        if (parentCell != null) {
            // The removeChild() method will remove from the parent but also
            // null the parent reference in the child Cell.
            logger.warning("Removing the Cell from old parent " + parentCell.getCellID());
            parentCell.removeChild(cell);
        }
        else {
            logger.warning("Removing the Cell from the root");
            rootCells.remove(cell);
        }

        // Find the new parent Cell. If the parent Cell ID is -1 (which will
        // result in no parent Cell being found, it means we wish to add this
        // to the root.
        Cell newParentCell = cells.get(parentCellID);
        if (newParentCell != null) {
            try {
                logger.warning("Adding the Cell to new parent " + newParentCell.getCellID());
                newParentCell.addChild(cell);
            } catch (MultipleParentException excp) {
                logger.log(Level.WARNING, "Multiple parents are set for Cell" +
                        " with ID " + cellID, excp);
            }
        }
        else if (!cellID.equals(CellID.getEnvironmentCellID())) {
            logger.warning("Adding the Cell to the root");
            rootCells.add(cell);
        }

        // Update the Cell transform with the new local transform
        cell.setLocalTransform(cellTransform, ChangeSource.REMOTE);
    }


    /**
     * Set the cell status, ensuring that the cell passes through any intermediate
     * status.
     * 
     * @param cell
     * @param status
     */
    private void setCellStatus(Cell cell, CellStatus status) {
        logger.fine("Set status of cell " + cell.getCellID() +
                       " to " + status);

        synchronized(cell) {
            int currentStatus = cell.getStatus().ordinal();
            int requiredStatus = status.ordinal();

            if (currentStatus==requiredStatus)
                return;

            int dir = (requiredStatus>currentStatus ? 1 : -1);
            boolean increasing = (dir==1);

            while(currentStatus!=requiredStatus) {
                currentStatus += dir;

                CellStatus nextStatus = CellStatus.values()[currentStatus];
                long startTime = System.currentTimeMillis();
                TimeCellStat loadStat = getLoadStat(cell, nextStatus);
                try {
                    cell.setStatus(nextStatus, increasing);
                } finally {
                    long time = System.currentTimeMillis() - startTime;
                    loadStat.changeValue(time);
                }

                cell.fireCellStatusChanged(nextStatus);
            }
        }
    }

    private TimeCellStat getLoadStat(Cell cell, CellStatus status) {
        String statId = status.name() + "-time";
        TimeCellStat loadStat;

        synchronized (getStatistics()) {
            loadStat = (TimeCellStat) getStatistics().get(cell, statId);
            if (loadStat == null) {
                loadStat = new TimeCellStat(statId);
                getStatistics().add(cell, loadStat);
            }
        }

        return loadStat;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Cell> getRootCells() {
        return new LinkedList(rootCells);
    }
    
    private Cell instantiateCell(String className, CellID cellId) {
        Cell cell;
        
        try {
            Class clazz = Class.forName(className, true, classLoader);
            Constructor constructor = clazz.getConstructor(CellID.class, CellCache.class);
            cell = (Cell) constructor.newInstance(cellId, this);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Problem instantiating cell "+className, e);
            return null;
        }
        
        return cell;
    }

    /**
     * {@inheritDoc}
     */
    public void setViewCell(ViewCell viewCell) {
        this.viewCell = viewCell;
        
        // Activate all current cells
        synchronized(cells) {
            // issue #850 -- make sure to notify parents before their
            // children
            for(Cell cell : cells.values()) {
                if (cell.getParent() != null) {
                    // ignore child cells
                    continue;
                }

                // change the status of this parent and all its children
                changeCellTreeStatus(cell, CellStatus.VISIBLE);
            }
        }
    }

    /**
     * Notify a tree of cells to change their status
     * @param cell the root cell to notify
     * @param status the status to set
     */
    private void changeCellTreeStatus(Cell root, CellStatus status) {
        if (root.getStatus().ordinal() < status.ordinal()) {
            changeCellStatus(root, status);
        }

        for (Cell child : root.getChildren()) {
            changeCellTreeStatus(child, status);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ViewCell getViewCell() {
        return viewCell;
    }
    
    /**
     * {@inheritDoc}
     */
    public WonderlandSession getSession() {
        return session;
    }

    
    public CellChannelConnection getCellChannelConnection() {
        return cellChannelConnection;
    }

    /**
     * {@inheritDoc}
     */
    public void addCellCacheListener(CellCacheListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeCellCacheListener(CellCacheListener listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public CellStatistics getStatistics() {
        return stats;
    }

    /**
     * Notify listeners that a cell is loaded
     * @param cell the cell that was loaded
     */
    protected void fireCellLoaded(Cell cell) {
        for (CellCacheListener listener : listeners) {
            listener.cellLoaded(cell.getCellID(), cell);
        }
    }

    /**
     * Notify listeners that a cell laod failed
     * @param cellID the id of the cell that failed to load
     * @param className the class of the cell that failed to load
     * @param parentCellID the id of the cell's parent
     * @param cause the reason for failure
     */
    protected void fireCellLoadFailed(CellID cellID, String className,
                                      CellID parentCellID, Throwable cause)
    {
        for (CellCacheListener listener : listeners) {
            listener.cellLoadFailed(cellID, className, parentCellID, cause);
        }
    }

    /**
     * Notify listeners that a cell is unloaded
     * @param cell the cell that was unloaded
     */
    protected void fireCellUnloaded(Cell cell) {
        for (CellCacheListener listener : listeners) {
            listener.cellUnloaded(cell.getCellID(), cell);
        }
    }

    /**
     * Cell status changes can take a while so should not be performed on the
     * Darkstar listener thread that calls the cache methods. Therefore
     * schedule a task actually apply the status change to the cell.
     * @param cell
     * @param status
     */
    private void changeCellStatus(Cell cell, CellStatus status) {
        cacheExecutor.submit(new CellStatusChanger(cell, status));
    }

    private class CellStatusChanger implements Runnable {

        private Cell cell;
        private CellStatus cellStatus;

        public CellStatusChanger(Cell cell, CellStatus status) {
            this.cell = cell;
            this.cellStatus = status;
        }

        public void run() {
            try {
                setCellStatus(cell, cellStatus);
            } catch(Throwable t) {
                // Report the exception, otherwise it will get swallowed
                logger.log(Level.WARNING, "Exception thrown in Cell.setStatus "+t.getLocalizedMessage(), t);
            }
        }

    }
}
