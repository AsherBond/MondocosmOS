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
package org.jdesktop.wonderland.modules.avatarbase.common.cell;

import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * Server state for avatarbase avatar configuration cell component. This class
 * is JAXB annotated so that is may be serialized to/from XML.
 *
 * @author paulby
 */
@XmlRootElement(name="avatar-config-component")
@ServerState
public class AvatarConfigComponentServerState extends CellComponentServerState {

    // The avatar configuration information
    private AvatarConfigInfo avatarConfigInfo = null;

    /** Default constructor */
    public AvatarConfigComponentServerState() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.modules.avatarbase.server.cell.AvatarConfigComponentMO";
    }

    /**
     * Returns the avatar configuration information.
     *
     * @return The avatar configuration information
     */
    public AvatarConfigInfo getAvatarConfigInfo() {
        return avatarConfigInfo;
    }

    /**
     * Sets the avatar configuration information.
     *
     * @param avatarConfigInfo The avatar config information
     */
    public void setAvatarConfigInfo(AvatarConfigInfo avatarConfigInfo) {
        this.avatarConfigInfo = avatarConfigInfo;
    }
}
