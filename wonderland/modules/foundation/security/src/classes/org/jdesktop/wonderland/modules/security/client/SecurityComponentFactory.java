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
package org.jdesktop.wonderland.modules.security.client;

import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellComponentFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellComponentFactorySPI;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.security.common.ActionDTO;
import org.jdesktop.wonderland.modules.security.common.CellPermissions;
import org.jdesktop.wonderland.modules.security.common.Permission;
import org.jdesktop.wonderland.modules.security.common.Principal;
import org.jdesktop.wonderland.modules.security.common.SecurityComponentServerState;
import org.jdesktop.wonderland.common.cell.security.ModifyAction;
import org.jdesktop.wonderland.common.cell.security.ViewAction;

/**
 * The cell component factory for the sample cell component.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@CellComponentFactory
public class SecurityComponentFactory implements CellComponentFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/security/client/Bundle");

    public String getDisplayName() {
        return BUNDLE.getString("Security");
    }

    public <T extends CellComponentServerState> T
            getDefaultCellComponentServerState() {
        SecurityComponentServerState state = new SecurityComponentServerState();

        CellPermissions perms = new CellPermissions();

        // add the current user as an owner
        ServerSessionManager primarySM = LoginManager.getPrimary();
        if (primarySM != null) {
            WonderlandSession primarySession = primarySM.getPrimarySession();
            if (primarySession != null) {
                Principal owner = new Principal(
                        primarySession.getUserID().getUsername(),
                        Principal.Type.USER);
                perms.getOwners().add(owner);
            }
        }

        // add view permissions for all users
        Principal p = new Principal("users", Principal.Type.EVERYBODY);
        ActionDTO view = new ActionDTO(new ViewAction());
        perms.getPermissions().add(new Permission(
                p, view, Permission.Access.GRANT));
        ActionDTO modify = new ActionDTO(new ModifyAction());
        perms.getPermissions().add(new Permission(
                p, modify, Permission.Access.DENY));

        state.setPermissions(perms);
        return (T) state;
    }

    public String getDescription() {
        return BUNDLE.getString("Security_Description");
    }
}
