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
package org.jdesktop.wonderland.common.cell;

import java.io.Serializable;
import java.util.HashSet;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Encapsulates the capabilities of a client. For example
 *  High end 3D graphics
 *  Low end 3D graphics
 *  Low speed network
 * 
 * @author paulby
 */
@ExperimentalAPI
public class ClientCapabilities implements Serializable {

    private HashSet<Capability> capabilities;
    
    /**
     * Returns true if the client supports the specified capability
     * @param capability
     * @return
     */
    public boolean supportsCapability(Capability capability) {
        if (capabilities==null)
            return false;
        
        return capabilities.contains(capability);
    }
    
}
