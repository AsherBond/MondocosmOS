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
package org.jdesktop.wonderland.server.spatial;

import com.sun.sgs.app.AppContext;

/**
 * Get an instance of the UniverseManager
 *
 * @author paulby
 */
public class UniverseManagerFactory {
    /**
     * Get the UniverseManager singleton
     * @return the UniverseManager
     */
   public static UniverseManager getUniverseManager() {
        return SingletonHolder.INSTANCE;
   }

   /**
    * SingletonHolder is loaded on the first execution of
    * UniverseManagerFactory.getInstance()
    * or the first access to SingletonHolder.INSTANCE , not before.
    */
   private static class SingletonHolder {
     private final static UniverseManager INSTANCE =
             AppContext.getManager(UniverseManager.class);
   }
    
}
