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

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.util.ScalableHashMap;
import com.sun.sgs.app.util.ScalableHashSet;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.common.cell.state.CellComponentUtils;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.spatial.CellMOListener;
import org.jdesktop.wonderland.server.spatial.UniverseManager;
import org.jdesktop.wonderland.server.spatial.UniverseManagerFactory;
import org.jdesktop.wonderland.server.wfs.exporter.CellExporter;
import org.jdesktop.wonderland.server.wfs.importer.CellImporter;

/**
 *
 * @author paulby
 */
@ExperimentalAPI
public class CellManagerMO implements ManagedObject, Serializable {
    private static final Logger logger =
            Logger.getLogger(CellManagerMO.class.getName());

    // bindings
    private static final String NAME = CellManagerMO.class.getName();
    private static final String COUNTER_BINDING_NAME = NAME + ".CellCounter";
    private static final String ROOTCELLS_BINDING_NAME = NAME + ".RootCells";
    private static final String COMPONENTS_BINDING_NAME = NAME + ".Components";
    private static final String ENV_CREATOR_BINDING_NAME = NAME + ".EnvCreator";

    /**
     * Creates a new instance of CellManagerMO.  Use the singleton
     * getCellManager() method instead.
     */
    CellManagerMO() {
    }
    
    /**
     * Initialize the master cell cache. This is an implementation detail and
     * should not be called by users of this class.
     */
    @InternalAPI
    public static void initialize() {
        logger.fine("CellManagerMO Initializing");

        // add a listener that will be notified of any cell creation
        // in the system
        UniverseManager universe = AppContext.getManager(UniverseManager.class);
        universe.addCellListener(new CellCreationListener());

        // register the cell channel message listener
        CommsManager cm = WonderlandContext.getCommsManager();
        cm.registerClientHandler(new CellChannelConnectionHandler());
        
        // Register the cell cache message handler
        cm.registerClientHandler(new CellCacheConnectionHandler());
        
        // Register the cell hierarchy edit message handler
        cm.registerClientHandler(new CellEditConnectionHandler());
    }

    /**
     * Return singleton master cell cache
     * @return the master cell cache
     */
    public static CellManagerMO getCellManager() {
        // return a unique instance of cell manager, since each instance
        // is stateless.  All calls that require state will look up the
        // appropriate bindings in the Darkstar datastore to get the
        // correct values for those states.  This removed a
        // heavily-contended object in favor of a few smaller objects.
        return new CellManagerMO();
    }
    
    /**
     * Return the cell with the given ID, or null if the id is invalid
     * 
     * @param cellID the cell ID to getTranslation
     * @return the cell with the given ID
     */
    public static CellMO getCell(CellID cellID) {
        if (cellID.equals(CellID.getInvalidCellID()))
            return null;

        try {
            return (CellMO) AppContext.getDataManager().getBinding(getCellBinding(cellID));
        } catch(NameNotBoundException e) {
            return null;
        }
    }

    /**
     * Get the singleton environment cell
     * @return the environment cell
     */
    public static EnvironmentCellMO getEnvironmentCell() {
        return (EnvironmentCellMO) getCell(CellID.getEnvironmentCellID());
    }

    /**
     * Register to create the environment cell. This object will be called if
     * a WFS is loaded that does not define an environment cell.
     * @param creator the creator to register
     */
    public void registerEnvironmentCellCreator(EnvironmentCellCreator creator) {
        AppContext.getDataManager().setBinding(ENV_CREATOR_BINDING_NAME, creator);
    }

    /**
     * Create an environment cell using the default creator.
     * @param creator the cell creator
     */
    protected EnvironmentCellMO createEnvironmentCell() {
        DataManager dm = AppContext.getDataManager();
        EnvironmentCellCreator creator =
                (EnvironmentCellCreator) dm.getBinding(ENV_CREATOR_BINDING_NAME);
        EnvironmentCellMO env = creator.createEnvironmentCell();
        
        try {
            insertCellInWorld(env);
        } catch (MultipleParentException mpe) {
            // should never happen
            logger.log(Level.WARNING, "Error adding environment cell", mpe);
        }
        
        return env;
    }
    
    /**
     * Insert the cell into the world. 
     */
    public void insertCellInWorld(CellMO cell) throws MultipleParentException {
        cell.setLive(true);

        if (cell instanceof EnvironmentCellMO) {
            // don't add environment cells to the world. Instead, just update
            // registrations
            new CellCreationListener().cellAdded(cell);
            return;
        }

        // add the cell to the universe
        UniverseManagerFactory.getUniverseManager().addRootToUniverse(cell);
        getRootCellsForUpdate().add(cell.cellID);
    }

    /**
     * Remove a cell from the world
     * @param cell the cell to remove
     */
    public void removeCellFromWorld(CellMO cell) {
        cell.setLive(false);
        // Set live now handles removal of root cells from the Universe 
//        UniverseManagerFactory.getUniverseManager().removeRootFromUniverse(cell);
        getRootCellsForUpdate().remove(cell.getCellID());
    }

    /**
     * Get the list of all root cells in the world, creating it if it doesn't
     * exist
     * @return a set of root cells
     */
    public Set<CellID> getRootCells() {
        DataManager dm = AppContext.getDataManager();
        Set<CellID> out;
        try {
            out = (Set<CellID>) dm.getBinding(ROOTCELLS_BINDING_NAME);
        } catch (NameNotBoundException nnbe) {
            out = new ScalableHashSet<CellID>();
            dm.setBinding(ROOTCELLS_BINDING_NAME, out);
        }

        return out;
    }

    /**
     * Convenience method to mark the root cells for update
     * @return the root cells, marked for update
     */
    protected Set<CellID> getRootCellsForUpdate() {
        Set<CellID> out = getRootCells();
        AppContext.getDataManager().markForUpdate(out);
        return out;
    }

    /**
     * Load the initial world.  This will typically load the cells
     * from WFS
     */
    public void loadWorld() {
        new CellImporter().load();


        // make sure there is an environment cell. If there isn't, create one.
        if (getEnvironmentCell() == null) {
            createEnvironmentCell();
        }
    }

    public void saveWorld() {
        new CellExporter().export(null);
    }
    
    /**
     * Returns a unique cell id and registers the cell with the system
     * @return
     */
    CellID createCellID(CellMO cell) {
        DataManager dm = AppContext.getDataManager();
        CellID cellID;

        if (cell instanceof EnvironmentCellMO) {
            // special case: environment cell is a singleton that has a fixed id
            cellID = CellID.getEnvironmentCellID();
        } else {
            // default case: assign a new cell ID
            CellCounter counter;
            try {
                counter = (CellCounter) dm.getBindingForUpdate(COUNTER_BINDING_NAME);
            } catch (NameNotBoundException nnbe) {
                counter = new CellCounter();
                dm.setBinding(COUNTER_BINDING_NAME, counter);
            }

            cellID = new CellID(counter.nextCellID());
        }

        dm.setBinding(getCellBinding(cellID), cell);
        return cellID;
    }

    /**
     * Return a unique name for a cell, given its ID
     * @param ID the cell's ID
     */
    static String getCellBinding(CellID cellID) {
        return "CELL_" + cellID.toString();
    }

    /**
     * Register a component that will be added to avatar cells
     * 
     * @param component
     */
    public void registerAvatarCellComponent(Class<? extends CellComponentMO> componentClass) {
        registerCellComponent(AvatarCellMO.class, componentClass);
    }

    /**
     * Register a component that will be automatically added to every cell
     * at creation time. Registrations will apply to subclasses as well,
     * so if you register a component to be added to CellMO.class, an instance
     * will be added to every cell at creation time.
     * @param cellClass the class of cell to register on
     * @param componentClass the class to instantiate
     */
    public void registerCellComponent(Class<? extends CellMO> cellClass,
                                      Class<? extends CellComponentMO> componentClass)
    {
        CellComponentMap cm = getComponentMap();
        AppContext.getDataManager().markForUpdate(cm);

        // add a record of this class to the map
        Set<Class<? extends CellComponentMO>> cms = cm.get(cellClass.getName());
        if (cms == null) {
            cms = new LinkedHashSet<Class<? extends CellComponentMO>>();
            cm.put(cellClass.getName(), cms);
        }

        cms.add(componentClass);
    }

    /**
     * Unregister a component type.
     * @param cellClass the class of cell to register on
     * @param componentClass the class to instantiate
     */
    public void unregisterCellComponent(Class<? extends CellMO> cellClass,
                                        Class<? extends CellComponentMO> componentClass)
    {
        CellComponentMap cm = getComponentMap();
        AppContext.getDataManager().markForUpdate(cm);

        Set<Class<? extends CellComponentMO>> cms = cm.get(cellClass.getName());
        if (cms != null) {
            cms.remove(componentClass);
        }
    }

    static Set<Class<? extends CellComponentMO>> getCellComponents(Class<? extends CellMO> cellClass) {
        CellComponentMap cm = getComponentMap();
        Set<Class<? extends CellComponentMO>> out = cm.get(cellClass.getName());
        if (out == null) {
            out = Collections.EMPTY_SET;
        }

        return out;
    }

    private static CellComponentMap getComponentMap() {
        DataManager dm = AppContext.getDataManager();
        CellComponentMap out;
        try {
            out = (CellComponentMap) dm.getBinding(COMPONENTS_BINDING_NAME);
        } catch (NameNotBoundException nnbe) {
            logger.log(Level.WARNING, COMPONENTS_BINDING_NAME + " not bound",
                       nnbe);
            out = new CellComponentMap();
            dm.setBinding(COMPONENTS_BINDING_NAME, out);
        }

        return out;
    }

    /**
     * Interface for the environment cell creator.
     */
    public interface EnvironmentCellCreator extends ManagedObject {
        public EnvironmentCellMO createEnvironmentCell();
    }

    private static final class CellCounter
            implements ManagedObject, Serializable
    {
        private long nextID = CellID.getFirstCellID();

        public long nextCellID() {
            return nextID++;
        }
    }

    /**
     * A listener that adds the cells from the component map to 
     * the cell when the cell is created.
     */
    private static final class CellCreationListener 
            implements CellMOListener, Serializable
    {
        public void cellAdded(CellMO cell) {
            // build the set of all components to add by looking at each
            // superclass of the cell, and adding all the components
            // registered on each superclass
            Set<Class<? extends CellComponentMO>> components =
                    new LinkedHashSet<Class<? extends CellComponentMO>>();

            // get the tree and add all classes
            Set<Class<? extends CellMO>> cellTree = getClassTree(cell.getClass());
            for (Class<? extends CellMO> cellClass : cellTree) {
                components.addAll(CellManagerMO.getCellComponents(cellClass));
            }

            // instantiate each component
            for(Class<? extends CellComponentMO> c : components) {
                // OWL issue #64: make sure component isn't a duplicate
                if (cell.getComponent(CellComponentUtils.getLookupClass(c)) != null) {
                    continue;
                }

                try {
                    Constructor con = c.getConstructor(CellMO.class);
                    CellComponentMO comp = (CellComponentMO) con.newInstance(cell);
                    cell.addComponent(comp);
                } catch (NoSuchMethodException ex) {
                    logger.log(Level.WARNING, null, ex);
                } catch (SecurityException ex) {
                    logger.log(Level.WARNING, null, ex);
                } catch (InstantiationException ex) {
                    logger.log(Level.WARNING, null, ex);
                } catch (IllegalAccessException ex) {
                    logger.log(Level.WARNING, null, ex);
                } catch (IllegalArgumentException ex) {
                    logger.log(Level.WARNING, null, ex);
                } catch (InvocationTargetException ex) {
                    // bug #527 -- rethrow Runtime exceptions
                    if (ex.getCause() != null &&
                            ex.getCause() instanceof RuntimeException)
                    {
                        throw (RuntimeException) ex.getCause();
                    }

                    logger.log(Level.WARNING, null, ex);
                }
            }
        }

        public void cellRemoved(CellMO cell) {
            // ignore
        }

        /**
         * Get all the superclasses of a cell up to CellMO
        */
        private static Set<Class<? extends CellMO>> getClassTree(Class<? extends CellMO> cellClass) {
            Set<Class<? extends CellMO>> classes =
                    new LinkedHashSet<Class<? extends CellMO>>();
            do {
                classes.add(cellClass);
                cellClass = (Class<? extends CellMO>) cellClass.getSuperclass();
            } while (cellClass != null && CellMO.class.isAssignableFrom(cellClass));

            return classes;
        }
    }

    /**
     * A map from class to the set of components registered for that class.
     * Note that we can't use class for the actual key, since the
     * hashCode of classes isn't stable across restarts.
     */
    private static class CellComponentMap
            extends ScalableHashMap<String,
                                    Set<Class<? extends CellComponentMO>>>
    {}
}
