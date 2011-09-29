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

/**
 * The JARURLUtils class provides some utility methods to parse Jar URL's. Jar
 * URL's are of the format:
 * <p>
 * jar:<url>!/<path>
 * <p>
 * where <url> is a typical URL, such as "http://www.foo.com/bar.jar" and
 * <path> is the path name within the JAR file, such as "building/room".
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class JarURLUtils {
    /**
     * Given a URL of protocol type "jar" returns a URL that does not include
     * the specific resource within the JAR (that is, everything before and
     * including the "!/"). If the "!/" is missing, it appends it.
     *
     * @param url The JAR URL
     * @return The main part of the URL without anything following a "!/"
     */
    public static String getURLMainPart(String url) {
        /* Find the '!/' string and fetch the substring if it exists */
        int index = url.indexOf("!/");
        if (index == -1) {
            return url + "!/";
        }
        return url.substring(0, index + 2);
    }
    
    /**
     * Given a URL of protocol type "jar" returns the specific path within
     * the JAR (that is, everything after the "!/" and not including the "!/".
     * If there is nothing after the "!/", returns an empty string.
     * 
     * @param url The JAR URL
     * @return The file part of the URL without the leading "!/"
     */
    public static String getURLResourcePart(String url) {
        /* Find the '!/' string and fetch the substring if it exists */
        int index = url.indexOf("!/");
        if (index == -1) {
            return "";
        }
        return url.substring(index + 2);
    }
}
