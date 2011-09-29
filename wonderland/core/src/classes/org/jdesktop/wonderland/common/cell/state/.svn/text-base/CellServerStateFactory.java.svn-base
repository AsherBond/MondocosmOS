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
package org.jdesktop.wonderland.common.cell.state;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.jdesktop.wonderland.common.ModuleURI;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;
import org.jdesktop.wonderland.common.cell.state.spi.CellServerStateSPI;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;

/**
 * The CellServerStateFactory returns marshallers and unmarshallers that can encode
 * and decode XML that is bound to JAXB-annotated classes. This class uses
 * Java's service provider mechanism to fetch the list of fully-qualified class
 * names of Java objects that have JAXB annotations.
 * <p>
 * Classes that provide such a service must have an entry in the JAR file in
 * which they are contained. In META-INF/services, a file named
 * org.jdesktop.wonderland.common.cell.setup.CellServerStateSPI should contain the
 * fully-qualified class name(s) of all classes that implement the CellServerStateSPI
 * interface.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellServerStateFactory {
    /* A list of core cell setup class names, currently only for components */
    private static final List<Class> coreClasses = new ArrayList<Class>();
    
    /* The JAXB contexts used to create marshallers and unmarshallers */
    private static JAXBContext systemContext = null;

    /* The Logger for this class */
    private static final Logger logger = Logger.getLogger(CellServerStateFactory.class.getName());
     
    /* Create the XML marshaller and unmarshaller once for all setup classes */
    static {
        // start with everything in the system classloader.
        ScannedClassLoader scl = ScannedClassLoader.getSystemScannedClassLoader();
        coreClasses.addAll(Arrays.asList(getClasses(scl)));
        
        // this list includes all ServerState classes in core, since webstart
        // won't always find them
        coreClasses.add(AvatarCellServerState.class);
        coreClasses.add(InteractionComponentServerState.class);
        coreClasses.add(ModelCellComponentServerState.class);
        coreClasses.add(ModelCellServerState.class);
        coreClasses.add(PositionComponentServerState.class);
        coreClasses.add(ModuleURI.class);

        try {
            systemContext = JAXBContext.newInstance(coreClasses.toArray(new Class[0]));
        } catch (javax.xml.bind.JAXBException excp) {
            CellServerStateFactory.logger.log(Level.SEVERE,
                     "[CELL] SETUP FACTORY Failed to create JAXBContext", excp);
        }

        logger.fine("Core classes: " + coreClasses.toString());
    }

    /**
     * Returns the object that marshalls JAXB-annotated classes into XML using
     * classes available in the supplied classLoader. If classLoader is null the
     * classloader for this class will be used.
     * 
     * @return A marhsaller for JAXB-annotated classes
     */
    public static Marshaller getMarshaller(ScannedClassLoader classLoader) {
        try {
            JAXBContext context = getContext(classLoader);
            Marshaller m = context.createMarshaller();
            m.setProperty("jaxb.formatted.output", true);
            return m;

        } catch (JAXBException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
            
        return null;
        
    }
    
    /**
     * Returns the object that unmarshalls XML into JAXB-annotated classes using
     * classes available in the supplied classLoader. If classLoader is null the
     * classloader for this class will be used.
     * 
     * @return An unmarshaller for XML
     */
    public static Unmarshaller getUnmarshaller(ScannedClassLoader classLoader) {
        try {
            JAXBContext context = getContext(classLoader);
            return context.createUnmarshaller();

        } catch (JAXBException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
            
        return null;
    }

    /**
     * Get the JAXB context to use for the given classloader
     * @param the classloader to get a context for, or null to get the
     * context for the system classloader
     * @return a JAXB context
     */
    private static JAXBContext getContext(ScannedClassLoader classLoader)
        throws JAXBException
    {
        if (classLoader == null) {
            return systemContext;
        } else {
            Class[] clazz = getClasses(classLoader);
            return JAXBContext.newInstance(clazz);
        }
    }

    /**
     * Find and return all the classes from the classLoader that implement the CellServerStateSPI
     * inteface
     * 
     * @param classLoader
     * @return
     */
    private static Class[] getClasses(ScannedClassLoader classLoader) {
        Set<Class> setupClasses = new LinkedHashSet<Class>(coreClasses);

        /* Attempt to load the class names using annotations */
        Iterator<CellState> it = classLoader.getInstances(
                ServerState.class,
                CellState.class);

        while (it.hasNext()) {
            setupClasses.add(it.next().getClass());
        }

        /* Also add the deprecated CellServerStateSPI implementations, although
         * we hope these will go away eventually
         */
        Iterator<CellServerStateSPI> it2 = classLoader.getAll(
                ServerState.class,
                CellServerStateSPI.class);
        while (it2.hasNext()) {
            setupClasses.add(it2.next().getClass());
        }

        logger.fine("Setup classes: " + setupClasses.toString());
        
        return setupClasses.toArray(new Class[0]);
    }
}
