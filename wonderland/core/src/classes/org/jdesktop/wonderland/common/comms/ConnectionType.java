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
package org.jdesktop.wonderland.common.comms;

import java.io.Serializable;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The type of a Wonderland connection.
 * @author jkaplan
 */
@ExperimentalAPI
public class ConnectionType implements Serializable {
    private String type;
    
    /**
     * Create a new client type with the given type name
     * @param type the name of the type
     */
    public ConnectionType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        // all client types are comparable to each other
        if (!(obj instanceof ConnectionType)) {
            return false;
        }
        
        final ConnectionType other = (ConnectionType) obj;
        if (this.type != other.type && 
                (this.type == null || !this.type.equals(other.type)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return type;
    }
}
