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
package org.jdesktop.wonderland.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.jdesktop.wonderland.utils.PropertyUtil.PropertyResolver;

/**
 * Property resolution for system properties.
 * @author jkaplan
 */
public class SystemPropertyUtil {
    /**
     * Get a property value from the system properties.  If the property value
     * is not found, return null.
     * <p>
     * Property substitution will happen as described in 
     * PropertyResolver.substitute();
     * 
     * @param property the property to look up
     * @see PropertyResolver
     */
    public static String getProperty(final String prop) {
        return getProperty(prop, null);
    }
    
    /**
     * Get a property value from the system properties.  If the property value 
     * is not found, return the supplied default.
     * <p>
     * Property substitution will happen as described in 
     * PropertyResolver.substitute();
     * 
     * @param property the property to look up
     * @param defVal the default value
     * @see PropertyResolver
     */
    public static String getProperty(final String prop, final String defVal) {
        return getProperty(prop, 
                           Collections.singletonList(System.getProperties()), 
                           defVal);
    }
    
    /**
     * Get a property value from a list of property sources. Sources are checked
     * in the order they are given. If the property value is not found in any of
     * the supplied sources, return the supplied default.
     * <p>
     * Property substitution will happen as described in 
     * PropertyResolver.substitute();
     * 
     * @param property the property to look up
     * @param props the set of properties to search
     * @param defVal the default value
     * @see PropertyResolver
     */
    public static String getProperty(final String prop, 
                                     final List<Properties> props,
                                     final String defVal) 
    {
        // list of resolvers to use
        List<PropertyResolver> resolvers = new ArrayList<PropertyResolver>();
     
        for (Properties p : props) {
            final Properties pFinal = p;
            resolvers.add(new PropertyResolver() {
                public String getValue(String key) {
                    return pFinal.getProperty(key);
                }  
            });
        }
        
        return PropertyUtil.getProperty(prop, resolvers, defVal);
    }
}                   
