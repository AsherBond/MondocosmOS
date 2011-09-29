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
package org.jdesktop.wonderland.client.cell.properties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;

/**
 * Manages the set of propery panels configuring cells. Cells implement the
 * CellPropertiesSPI interface and register their class with the Java service
 * loader mechanism. This class lists all of these cell properties.
 * 
 * XXX This does not work with federation -- need to listen for login events!
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class PropertiesManager {

    private static Logger logger = Logger.getLogger(PropertiesManager.class.getName());

    /* A set of all cell property objects */
    private Set<PropertiesFactorySPI> cellPropertiesSet;
    
    /* A map of cell state classes and their cell properties objects */
    private Map<Class, PropertiesFactorySPI> cellPropertiesClassMap;

    /* Initialize from the list of service providers in module JARs */
    static {
        /* Attempt to load the class names using the service providers */
        // This needs to work with federation XXX
        PropertiesManager pm = PropertiesManager.getPropertiesManager();
        ServerSessionManager manager = LoginManager.getPrimary();
        ScannedClassLoader cl = manager.getClassloader();
        
        Iterator<PropertiesFactorySPI> it = cl.getAll(
                PropertiesFactory.class, PropertiesFactorySPI.class);
        while (it.hasNext() == true) {
            PropertiesFactorySPI spi = it.next();
            pm.registerPropertiesFactory(spi);
        }
    }

    /** Default constructor */
    public PropertiesManager() {
        cellPropertiesClassMap = new HashMap();
        cellPropertiesSet = new HashSet();
    }
    
    /**
     * Singleton to hold instance of CellRegistry. This holder class is loaded
     * on the first execution of CellRegistry.getMediaManager().
     */
    private static class PropertiesHolder {
        private final static PropertiesManager propertiesManager = new PropertiesManager();
    }
    
    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final PropertiesManager getPropertiesManager() {
        return PropertiesHolder.propertiesManager;
    }
    
    /**
     * Registers a PropertiesFactorySPI class. This interface is used to generate
     * a GUI to allow editing of Cell or Cell Component's properties on the
     * client-side.
     * 
     * @param properties The PropertiesFactorySPI class to register
     */
    public synchronized void registerPropertiesFactory(PropertiesFactorySPI properties) {
        // Fetch the server state class upon which this properties factory is
        // associated. We need this before adding to the map.
        Class clazz = PropertiesManager.getServerStateClass(properties);
        if (clazz == null) {
            return;
        }

        // First check to see if the fully-qualified client-side cell class
        // name already exists and print a warning message if so (but we'll
        // still add it later)        
        if (cellPropertiesClassMap.containsKey(clazz) == true) {
            logger.warning("A CellPropertiesSPI already exist for class " +
                    clazz.getName());
        }

        // Add to the set of all CellPropertiesSPI objects and the map relating
        // the client-side cell class name to the object
        cellPropertiesSet.add(properties);
        cellPropertiesClassMap.put(clazz, properties);
    }
    
    /**
     * Returns a set of all properies objects. If no properties are registered,
     * returns an empty set.
     * 
     * @return A set of registered property objects
     */
    public Set<PropertiesFactorySPI> getAllProperties() {
        return new HashSet(cellPropertiesSet);
    }
    
    /**
     * Returns a properties object given the Class of the server-side state
     * class that the properties supports. If no properties are present for the
     * given server-side state, returns null.
     * 
     * @param clazz The class of the server-side state object
     * @return A PropertiesFactorySPI object registered for the state class
     */
    public PropertiesFactorySPI getPropertiesByClass(Class clazz) {
        return cellPropertiesClassMap.get(clazz);
    }

    /**
     * Given the PropertiesFactorySPI object (which must be annoated by the
     * @PropertiesFactory annotation, return the Class of the server state
     * object, or null upon error.
     *
     * @param factory The PropertiesFactorySPI class
     * @return The Class of the server-state object for the properties object
     */
    public static Class getServerStateClass(PropertiesFactorySPI factory) {
        // Fetch the server state class upon which this properties factory is
        // associated. We need this before adding to the map.
        Class clazz = factory.getClass();
        PropertiesFactory annotation = (PropertiesFactory)
                clazz.getAnnotation(PropertiesFactory.class);
        if (annotation == null) {
            logger.warning("Unable to find PropertiesFactory annotation on " +
                    clazz.getName());
            return null;
        }
        return annotation.value();
    }
}
