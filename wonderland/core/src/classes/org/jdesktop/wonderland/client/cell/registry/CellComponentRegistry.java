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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellComponentFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellComponentFactorySPI;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.PrimaryServerListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;

/**
 * The cell comopnent registry manages the collection of cell component types
 * registered with the system. This is used to display them in the palette and
 * also provides the necessary information to create them in-world.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellComponentRegistry implements PrimaryServerListener {

    /* A set of all cell factories */
    private Set<CellComponentFactorySPI> componentFactorySet;

    /* A map of Class to their cell component factories */
    private Map<Class, CellComponentFactorySPI> componentFactoryMap;

    /* A map of cell component server state classes to their factories */
    private Map<Class, CellComponentFactorySPI> stateFactoryMap;

    /* The set of factories associated with the current session */
    private final Set<CellComponentFactorySPI> sessionFactories =
            new LinkedHashSet<CellComponentFactorySPI>();

    /** Default constructor */
    public CellComponentRegistry() {
        componentFactorySet = new HashSet();
        componentFactoryMap = new HashMap();
        stateFactoryMap = new HashMap();

        LoginManager.addPrimaryServerListener(this);
    }
    
    /**
     * Singleton to hold instance of CellRegistry. This holder class is loaded
     * on the first execution of CellRegistry.getMediaManager().
     */
    private static class CellComponentRegistryHolder {
        private final static CellComponentRegistry cellRegistry = new CellComponentRegistry();
    }
    
    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final CellComponentRegistry getCellComponentRegistry() {
        return CellComponentRegistryHolder.cellRegistry;
    }
    
    /**
     * Registers a CellFactory. This cell factory is used to generate cell setup
     * classes, GUI panels to configuration the cell setup information, and
     * information so that the cell type can be used in a world assembly palette
     * of cell types.
     * 
     * @param factory The cell factory
     */
    public synchronized void registerCellComponentFactory(CellComponentFactorySPI factory) {
        // Add to the set containing all cell factories and the map
        componentFactorySet.add(factory);
        componentFactoryMap.put(factory.getClass(), factory);
        stateFactoryMap.put(factory.getDefaultCellComponentServerState().getClass(), factory);
    }

    /**
     * Unregisters a CellFactory. Make sure we're not removing something that
     * someone other than this factory wrote.
     * @param factory The cell factory
     */
    public synchronized void unregisterCellComponentFactory(CellComponentFactorySPI factory) {
        // Add to the set containing all cell factories and the map
        componentFactorySet.remove(factory);

        CellComponentFactorySPI cur = componentFactoryMap.get(factory.getClass());
        if (cur == factory) {
            componentFactoryMap.remove(factory.getClass());
        }

        cur = stateFactoryMap.get(factory.getDefaultCellComponentServerState().getClass());
        if (cur == factory) {
            stateFactoryMap.remove(factory.getDefaultCellComponentServerState().getClass());
        }
    }

    /**
     * Returns a set of all cell factories. If no factories are registered,
     * returns an empty set.
     * 
     * @return A set of registered cell factories
     */
    public Set<CellComponentFactorySPI> getAllCellFactories() {
        return new HashSet(componentFactorySet);
    }
    
    /**
     * Returns a set of cell factories given the Class. If no factories
     * are present for the given Class, returns null.
     * 
     * @param clazz The Class for the cell component factory
     * @return A CellComponentFactorySPI object registered on the Class
     */
    public CellComponentFactorySPI getCellFactoryByClass(Class clazz) {
        return componentFactoryMap.get(clazz);
    }

    /**
     * Returns a set of cell factories given the Class of the server-side state
     * object. If no factories are present for the given Class, return null.
     *
     * @param clazz The Class for the cell component server state
     * @return A CellComponentFactorySPI object registered on the Class
     */
    public CellComponentFactorySPI getCellFactoryByStateClass(Class clazz) {
        return stateFactoryMap.get(clazz);
    }

   /**
     * Notification that the primary server has changed.  Update our maps
     * accordingly.
     * @param server the new primary server (may be null)
     */
    public void primaryServer(ServerSessionManager server) {
        // remove any existing entries
        unregisterFactories();

        // find new entries
        if (server != null) {
            registerFactories(server);
        }
    }

    /**
     * Register all factories associated with the given session
     * manager.
     */
    protected synchronized void registerFactories(ServerSessionManager manager) {
        // search annotations
        // now search annotations
        ScannedClassLoader cl = manager.getClassloader();
        Iterator<CellComponentFactorySPI> it = cl.getAll(
                CellComponentFactory.class, CellComponentFactorySPI.class);
        while (it.hasNext()) {
            CellComponentFactorySPI factory = it.next();
            registerCellComponentFactory(factory);
            sessionFactories.add(factory);
        }
    }

    /**
     * Unregister all factories associated with the current session
     */
    protected synchronized void unregisterFactories() {
        for (CellComponentFactorySPI factory : sessionFactories) {
            unregisterCellComponentFactory(factory);
        }

        sessionFactories.clear();
    }
}
