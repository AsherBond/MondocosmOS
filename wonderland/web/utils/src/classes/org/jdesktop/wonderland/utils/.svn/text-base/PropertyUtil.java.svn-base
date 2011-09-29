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

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common utilities for reading in property values, including simple
 * substitution based on a configurable set of handlers.
 * @author jkaplan
 */
public class PropertyUtil {
    /** pattern for substitution */
    private static final Pattern subst = Pattern.compile("\\$(?:PATH)?\\{.+?\\}");
            
    /**
     * Read a property with null as the default value.  
     * @param prop the property to read
     * @param resolver the property resolver to look it up in
     * @see getProperty(String, ServletContext, String)
     */
    public static String getProperty(final String prop,
                                     final PropertyResolver resolver)
    {
        return getProperty(prop, resolver, null);
    }
    
    /**
     * Read a property with a given default value.  
     * @param prop the property to read
     * @param resolver the property resolver to look it up in
     * @param default the default value
     * @see getProperty(String, ServletContext, String)
     */
    public static String getProperty(final String prop,
                                     final PropertyResolver resolver,
                                     final String defVal)
    {
        return getProperty(prop, Collections.singletonList(resolver), null);
    }
    
    /**
     * Read a property from a list of resolvers.  The resolvers are tried in
     * the order they are given, until one does not return null.  If all
     * resolvers return null, the default value will be used below.
     * <p>
     * Further, for any non-null return value, substitution will be 
     * peformed as follows:
     * <p>
     * For an occurence of ${property}, the value of property will be
     * substituted using a call to this method, using the same rules
     * as above.
     * 
     * @param prop the name of the property
     * @param context the context to check for the property in
     * @param defVal the default value of the property
     * @return the value of the given property, or the supplied default if no
     * value can be found for the given property
     */
    public static String getProperty(final String prop,
                                     final List<PropertyResolver> resolvers,
                                     final String defVal) 
    {
        return getProperty(prop, resolvers, defVal, true);
    }
    
    /**
     * Read a property from a list of resolvers.  The resolvers are tried in
     * the order they are given, until one does not return null.  If all
     * resolvers return null, the default value will be used below.
     * <p>
     * Further, for any non-null return value, substitution will be 
     * peformed as follows if the substitute argument is true:
     * <p>
     * For an occurence of ${property}, the value of property will be
     * substituted using a call to this method, using the same rules
     * as above.
     * 
     * @param prop the name of the property
     * @param context the context to check for the property in
     * @param defVal the default value of the property
     * @param substitute if true, perform substitution
     * @return the value of the given property, or the supplied default if no
     * value can be found for the given property
     */
    public static String getProperty(final String prop,
                                     final List<PropertyResolver> resolvers,
                                     final String defVal,
                                     final boolean substitute) 
    {
        // try the resolvers in order
        String out = null;
        for (PropertyResolver resolver : resolvers) {
            out = resolver.getValue(prop);
            if (out != null) {
                break;
            }
        }
        
        // if no resolver found our value, use the default
        if (out == null) {
            out = defVal;
        }
        
        // substitute
        if (out != null && substitute) {
            out = substitute(out, resolvers);
        }
        
        // return what we found
        return out;
    }

    /**
     * Substitute patterns.  Searches for the pattern "${value}" in the text,
     * and replaces "..." with the value of a call to 
     * <code>getProperty(value)</code>.  If the value is not found, the 
     * original pattern is left unmodified.
     * <p>
     * Substitutions can also specify a default value, in the form 
     * "${value:default}".  When a default is specified, if value is not
     * found the default value will be returned instead of the original
     * pattern.
     * <p>
     * In Wonderland, all paths should be specified using Unix-style
     * "/"'s.  Since some existing variables (like user.home) use the
     * system's default separator, these separators must be converted.
     * Defining a pattern in the form "$PATH{value:default}" will cause
     * the system to automatically replace any system "\"'s with
     * Wonderland "/"'s after substitution.
     * <p>
     * @param str the string to subsitute in
     * @param resolvers the property resolvers to use
     * <code>getProperty</code>
     * @return the string after substitution
     */
    public static String substitute(String str, 
                                    List<PropertyResolver> resolvers) 
    {
        Matcher m = subst.matcher(str);
        StringBuffer buf = new StringBuffer();
        
        while (m.find()) {
            // get the matched text
            String match = str.substring(m.start(), m.end());
            int exprStart = "${".length();

            // figure out of this is a PATH
            boolean isPath = match.startsWith("$PATH");
            if (isPath) {
                exprStart = "$PATH{".length();
            }

            String expr = match.substring(m.start() + exprStart, m.end() - 1);
            
            // see if there is a default value
            String defVal = null;
            if (expr.contains(":")) {
                String[] vals = expr.split(":");
                if (vals.length != 2) {
                    throw new IllegalArgumentException("Format must be " +
                                "${value:default} found " + expr); 
                }
                
                expr = vals[0];
                defVal = vals[1];
            }
            
            // resolve the result as a system property
            String res = getProperty(expr, resolvers, defVal, true);
            if (res == null && defVal != null) {
                // use the default
                res = defVal;
            } else if (res == null) {
                // add back the original text
                res = "${" + expr + "}";
            }

            // fix paths
            if (isPath && File.separatorChar != '/') {
                res = res.replace(File.separatorChar, '/');
            }

            // replace '\' with '\\' and '$' with '\$' to fix substitution 
            // problems
            res = res.replace("\\", "\\\\");
            res = res.replace("$", "\\$");
            
            m.appendReplacement(buf, res);
        }
        m.appendTail(buf);
        
        return buf.toString();
    }
    
    /** Something that resolves key/value queries */
    public static interface PropertyResolver {
        /**
         * Given a key, return a value or null
         * @param key the key to look up
         * @return the value, if one matching key is found, or null
         */
        public String getValue(String key);
    }
}
