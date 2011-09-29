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
import java.util.List;
import javax.servlet.ServletContext;
import org.jdesktop.wonderland.utils.PropertyUtil.PropertyResolver;

/**
 * Property resolution for servlets.  These methods search for properties
 * in the following order:
 * <ul><li>The system properties
 *     <li>The servlet context
 *     <li>The supplied default
 * </ul>
 * @author jkaplan
 */
public class ServletPropertyUtil {
    /**
     * Get a property value from the system properties or the servlet
     * context.  If the property value is not found in either of these
     * places, return null.
     * <p>
     * Property resolution will happen as described in 
     * PropertyResolver.substitute();
     * 
     * @param property the property to look up
     * @param context the servlet context
     * @see PropertyResolver
     */
    public static String getProperty(final String prop,
                                     final ServletContext context)
    {
        return getProperty(prop, context, null);
    }
    
    /**
     * Get a property value from the system properties or the servlet
     * context.  If the property value is not found in either of these
     * places, return the supplied default.
     * <p>
     * Property resolution will happen as described in 
     * PropertyResolver.substitute();
     * 
     * @param property the property to look up
     * @param context the servlet context
     * @param defVal the default value
     * @see PropertyResolver
     */
    public static String getProperty(final String prop,
                                     final ServletContext context,
                                     final String defVal)
    {
        // list of resolvers to use
        List<PropertyResolver> resolvers = new ArrayList<PropertyResolver>();
        
        // first, try the system properties
        resolvers.add(new PropertyResolver() {
            public String getValue(String key) {
                return System.getProperty(prop);
            }           
        });
        
        // next try the servlet context
        resolvers.add(new PropertyResolver() {
           public String getValue(String key) {
               return context.getInitParameter(prop);
           } 
        });
        
        return PropertyUtil.getProperty(prop, resolvers, defVal);
    }
}                   
