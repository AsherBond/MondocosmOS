/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Load libraries in the system classloader. This is useful for native libraries
 * shared between multiple modules, so the libraries can be reused by each
 * module.
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class LibraryLoader {
    private static final Logger LOGGER =
            Logger.getLogger(LibraryLoader.class.getName());
    
    /**
     * Load the library with the given name in the core classloader
     * @param library the library to load
     * @return true if the library was loaded, or false if not
     */
    public static boolean loadLibrary(String library) {
        try {
            System.loadLibrary(library);
            return true;
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, "Error loading library", t);
            return false;
        }
    }
}
