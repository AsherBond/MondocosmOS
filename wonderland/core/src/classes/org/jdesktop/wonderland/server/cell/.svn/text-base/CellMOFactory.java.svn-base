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

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;

/**
 * A factory that creates cell GLOs by type.  This uses the service provider
 * API to find all installed CellGLOProviders.  The providers are responsible
 * for actually instantiating the cells.
 * <p>
 * CellGLOs are identified by a type name, which may be a fully-qualified
 * class name or may be a shorthand name defined by a specific provider.
 * <p>
 * If no provider can handle the given type name, this method assumes the name
 * is a fully-qualified class name and loads that class.
 * @author jkaplan
 */
public class CellMOFactory {

    private static Logger logger = Logger.getLogger(CellMOFactory.class.getName());

    /**
     * Instantiate a cell GLO of the given type with the given arguments.
     * This will try to load from each provider in turn, returning either
     * the first response or null if no cell can be created with the given
     * type name.
     * 
     * @param typeName the name of the cell type to instantiate.
     * @param args the arguments to the constructor of the given cell type
     * @throws LoadCellMOException if there is an error loading the
     * given cell type with the given arguments
     */
    public static CellMO loadCellMO(String typeName, Object... args) 
        throws LoadCellMOException
    {       
        CellMO res = null;

        // get a list of providers by type or annotation
        ScannedClassLoader scl = ScannedClassLoader.getSystemScannedClassLoader();
        Iterator<CellMOProvider> i = scl.getAll(
                org.jdesktop.wonderland.server.cell.annotation.CellMOProvider.class,
                CellMOProvider.class);

        // check each provider
        while (i.hasNext()) {
            res = i.next().loadCellMO(typeName, args);
            if (res != null) {
                break;
            }
        }
        
        // no luck -- try instantiating as a class
        try {
            Class clazz = Class.forName(typeName);
            res = new DefaultCellMOProvider().loadCellMO(typeName, args);
        } catch (ClassNotFoundException cnfe) {
            // ignore -- it wasn't a class name after all
            logger.log(Level.WARNING, "Cell class not found", cnfe);
        }
        
        // see what we found
        return res;
    }
    
    // default provider
    static class DefaultCellMOProvider extends CellMOProvider {
        @SuppressWarnings("unchecked")
        public CellMO loadCellMO(String typeName, Object... args) 
                throws LoadCellMOException
        {
            // assume type name is a fully-qualified class name
            try {
                Class<CellMO> clazz = 
                        (Class<CellMO>) Class.forName(typeName);
                return createCell(clazz, args);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                // Explicitly catch InvocationTargetException and make sure
                // we through the suitable RuntimeException if that's the cause.
                // The reason is this: if the cell creation times-out because
                // of the classloader, then we need to retry the task.
                Throwable throwable = ite.getCause();
                if (throwable instanceof java.lang.RuntimeException) {
                    throw ((RuntimeException)throwable);
                }
                throw new LoadCellMOException("Error loading type " + typeName,
                                               ite);
            } catch (java.lang.Exception ex) {
                // Catch all exceptions (except for Runtime exceptions) and
                // re-throw as a LoadCellMOException
                logger.log(Level.WARNING, "Cannot create cell", ex);
                throw new LoadCellMOException("Error loading type " + typeName,
                                               ex);
            }
        }
    }
}
