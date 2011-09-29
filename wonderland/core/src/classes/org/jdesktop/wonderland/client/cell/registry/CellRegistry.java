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
package org.jdesktop.wonderland.client.cell.registry;

import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.PrimaryServerListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;

/**
 * The Cell Registry manages the collection of Cell registered with the system.
 * Each Cell is represented by a Cell Factory that implements the CellFactorySPI
 * interface. This CellFactorySPI interface tells the system how to create a
 * new instance of the Cell in the world.
 * <p>
 * Modules may register Cell Factories in this Cell Registry in one of two
 * ways:
 * <p>
 * (1) Annotate their CellFactorySPI class with the @CellFactory annotation.
 * (2) Manually register their CellFactorySPI class using registerCellFactory().
 * <p>
 * This class maintains the set of registered Cell Factories for the current
 * primary ServerSessionManager. When the primary ServerSessionManager changes,
 * the current set of Cell Factories that were registered via the @CellFactory
 * annotation are unregistered before the set of Cell Factories associated with
 * the new primary ServerSessionManager and annotated with @CellFactory are
 * added.
 * <p>
 * Modules that manually add CellFactorySPI class with registerCellFactory()
 * must manually unregister their class with unregisterCellFactory() whenever
 * the primary ServerSessionManager changes.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellRegistry implements PrimaryServerListener {

    /* A set of all cell factories */
    private Set<CellFactorySPI> cellFactorySet = null;
    
    /* A map of cell factories and the extensions the support */
    private Map<String, Set<CellFactorySPI>> cellFactoryExtensionMap = null;

    /* The set of all cell factories associated with the cuurent session */
    private final Set<CellFactorySPI> sessionFactories = new LinkedHashSet();

    /* A list of listeners for changes to the cell factory entries */
    private Set<CellRegistryListener> listeners = new HashSet();

    /** Default constructor */
    public CellRegistry() {
        cellFactoryExtensionMap = new HashMap();
        cellFactorySet = new HashSet();

        LoginManager.addPrimaryServerListener(this);
    }
    
    /**
     * Singleton to hold instance of CellRegistry. This holder class is loaded
     * on the first execution of CellRegistry.getMediaManager().
     */
    private static class CellRegistryHolder {
        private final static CellRegistry cellRegistry = new CellRegistry();
    }
    
    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final CellRegistry getCellRegistry() {
        return CellRegistryHolder.cellRegistry;
    }
    
    /**
     * Registers a CellFactory. This cell factory is used to generate cell setup
     * classes, GUI panels to configuration the cell setup information, and
     * information so that the cell type can be used in a world assembly palette
     * of cell types.
     * <p>
     * This method also generates an event to the listeners for changes in the
     * set of registered cell factories.
     * 
     * @param factory The cell factory
     */
    public synchronized void registerCellFactory(CellFactorySPI factory) {
        // Add the factory to the list and then notify listeners of the change
        addFactory(factory);
        fireCellRegistryListener();
    }

    /**
     * Removes a CellFactory registration.
     *
     * This method also generates an event to the listeners for changes in the
     * set of registered cell factories.
     *
     * @param factory The cell factory
     */
    public synchronized void unregisterCellFactory(CellFactorySPI factory) {
        // Remove the factory from the list and then notify listeners of the
        // change
        removeFactory(factory);
        fireCellRegistryListener();
    }
    
    /**
     * Returns a set of all cell factories. If no factories are registered,
     * returns an empty set.
     * 
     * @return A set of registered cell factories
     */
    public synchronized Set<CellFactorySPI> getAllCellFactories() {
        return new HashSet(cellFactorySet);
    }
    
    /**
     * Returns a set of cell factories given the extension type. If no factories
     * are present for the given extension, returns null.
     * 
     * @param extension File type extension (e.g. 'jpg', 'dae')
     * @return A set of CellFactory objects registered on the extension
     */
    public synchronized Set<CellFactorySPI> getCellFactoriesByExtension(String extension) {
        // Convert the extension to lower case so that, for example JPG is the
        // same as jpg. If the given extension is null, return null.
	if (extension != null) {
            extension = extension.toLowerCase();
            return cellFactoryExtensionMap.get(extension);
        }
        return null;
    }

    /**
     * Notification that the primary server has changed. Update our maps
     * accordingly.
     *
     * @param server the new primary server (may be null)
     */
    public void primaryServer(ServerSessionManager server) {
        // remove any existing entries
        unregisterFactories();

        // find new entries
        if (server != null) {
            registerFactories(server);
        }

        // Fire an even that the list of Cell factories has changed
        fireCellRegistryListener();
    }

    /**
     * Register all factories associated with the given session
     * manager.
     */
    protected synchronized void registerFactories(ServerSessionManager manager) {
        // search annotations
        ScannedClassLoader cl = manager.getClassloader();
        Iterator<CellFactorySPI> it = cl.getAll(CellFactory.class,
                                                CellFactorySPI.class);
        while (it.hasNext()) {
            CellFactorySPI factory = it.next();
            addFactory(factory);
            sessionFactories.add(factory);
        }
    }

    /**
     * Unregister all factories associated with the current session
     */
    protected synchronized void unregisterFactories() {
        for (CellFactorySPI factory : sessionFactories) {
            removeFactory(factory);
        }

        sessionFactories.clear();
    }

    /**
     * Actually adds the given cell factory to the necessary maps. This method
     * assumes synchronized access.
     */
    private void addFactory(CellFactorySPI factory) {
        // For now, don't check if the factory already exists. We may need to
        // create an entry in cellFactoryExtensionMap for the extension type if
        // it does not yet exist.
        String[] extensions = factory.getExtensions();
        if (extensions != null) {
            for (String extension : extensions) {
                // Convert the extension to lower case so that, for example
                // JPG is the same as jpg
                extension = extension.toLowerCase();
                Set<CellFactorySPI> factories = cellFactoryExtensionMap.get(extension);
                if (factories == null) {
                    factories = new HashSet<CellFactorySPI>();
                    cellFactoryExtensionMap.put(extension, factories);
                }
                factories.add(factory);
            }
        }

        // Add to the set containing all cell factories
        cellFactorySet.add(factory);
    }

    /**
     * Actually removes the given cell factory from the maps. This method
     * assumed synchronized access.
     */
    private void removeFactory(CellFactorySPI factory) {
        String[] extensions = factory.getExtensions();
        if (extensions != null) {
            for (String extension : extensions) {
                // Convert the extension to lower case so that, for example
                // JPG is the same as jpg
                extension = extension.toLowerCase();
                Set<CellFactorySPI> factories = cellFactoryExtensionMap.get(extension);
                if (factories != null) {
                    factories.remove(factory);

                    // If there are no more factories for the given extension,
                    // then remove it from the map.
                    if (factories.isEmpty() == true) {
                        cellFactoryExtensionMap.remove(extension);
                    }
                }
            }
        }

        // Add to the set containing all cell factories
        cellFactorySet.remove(factory);
    }

    /**
     * Adds a new listener for changes to the Cell registry. If this listener
     * is already present, this method does nothing.
     *
     * @param listener The new listener to add
     */
    public void addCellRegistryListener(CellRegistryListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener for changes to the Cell registry. If this listener is
     * not present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeCellRegistryListener(CellRegistryListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Sends an event to all registered listeners that a changes has occurred
     * in the list of Cell factories.
     */
    private void fireCellRegistryListener() {
        synchronized (listeners) {
            for (CellRegistryListener listener : listeners) {
                listener.cellRegistryChanged();
            }
        }
    }

    /**
     * A listener indicating that a change has happened to the set of registered
     * Cell factories.
     */
    public interface CellRegistryListener {
        /**
         * A change has occurred to the list of registered Cell factories.
         */
        public void cellRegistryChanged();
    }
}
