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

package org.jdesktop.wonderland.modules.security.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * Server state for Wonderland security component
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@XmlRootElement(name="security-component")
@ServerState
public class SecurityComponentServerState extends CellComponentServerState {
    private CellPermissions permissions = new CellPermissions();

    /** Default constructor */
    public SecurityComponentServerState() {
    }

    @Override
    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.modules.security.server.SecurityComponentMO";
    }
    
    @XmlElement
    public CellPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(CellPermissions permissions) {
        this.permissions = permissions;
    }
}
