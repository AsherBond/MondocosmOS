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

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A version of a protocol defines what other versions of the same protcol
 * are compatible.  Note this relationship is not necessarily 
 * bi-directional: a server may support a newer, backwards compatible version
 * of a protocol, bu a client may not support backwards compatibility.
 * @author jkaplan
 */
@ExperimentalAPI
public interface ProtocolVersion {
    /**
     * Determine if this version is compatible with the given protocol version
     * @param version the version to compare to
     * @return true if the versions are compatible, and false if not.
     */
    public boolean isCompatible(ProtocolVersion version);
}
