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
 * A default implementation of ProtocolVersion.  Two ProtocolVersions
 * of this type are compatible if the major and minor numbers are the same,
 * and the sub-version is less than or equal to the current version.
 * @author jkaplan
 */
@ExperimentalAPI
public class DefaultProtocolVersion implements ProtocolVersion, Serializable {
    private final int majorVersion;
    private final int minorVersion;
    private final int subVersion;
    
    /**
     * Create a new DefaultProtocolVersion with the given major, minor
     * and sub version numbers
     * @param majorVersion the major version number
     * @param minorVersion the minor version number
     * @param subVersion the sub version number
     */
    public DefaultProtocolVersion(int majorVersion, int minorVersion, 
                                  int subVersion)
    {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.subVersion = subVersion;
    }
    
    /**
     * Get the major version
     * @return the major version
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Get the minor version
     * @return the minor version
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Get the sub version
     * @return the sub version
     */
    public int getSubVersion() {
        return subVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isCompatible(ProtocolVersion version) {
        if (!(version instanceof DefaultProtocolVersion)) {
            return false;
        }
        
        DefaultProtocolVersion v = (DefaultProtocolVersion) version;
        
        return (getMajorVersion() == v.getMajorVersion() &&
                getMinorVersion() == v.getMinorVersion() &&
                getSubVersion() >= v.getSubVersion());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DefaultProtocolVersion)) {
            return false;
        }
        
        DefaultProtocolVersion v = (DefaultProtocolVersion) obj;
        
        return (getMajorVersion() == v.getMajorVersion() &&
                getMinorVersion() == v.getMinorVersion() &&
                getSubVersion() == v.getSubVersion());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.majorVersion;
        hash = 53 * hash + this.minorVersion;
        hash = 53 * hash + this.subVersion;
        return hash;
    }
    
    @Override
    public String toString() {
        return getMajorVersion() + "." + getMinorVersion() + "." +
               getSubVersion();
    }
}
