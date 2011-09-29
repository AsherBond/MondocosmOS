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
package org.jdesktop.wonderland.modules.sas.common;

import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The connection type between the SAS server and the SAS provider.
 *
 * @author deronj
 */

@InternalAPI
public class SasProviderConnectionType extends ConnectionType {

    /** The client type */
    public static final ConnectionType CLIENT_TYPE = new SasProviderConnectionType("__SasProviderConnection");
    
    private SasProviderConnectionType (String type) {
        super(type);
    }
}
