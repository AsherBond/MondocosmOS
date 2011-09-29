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
package org.jdesktop.wonderland.modules.portal.client;

import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellComponentFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellComponentFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentServerState;

/**
 * The cell component factory for the portal cell component.
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@CellComponentFactory
public class PortalComponentFactory implements CellComponentFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/portal/client/resources/Bundle");

    public String getDisplayName() {
        return BUNDLE.getString("Portal");
    }

    public <T extends CellComponentServerState> T getDefaultCellComponentServerState() {
        PortalComponentServerState state = new PortalComponentServerState();
        return (T) state;
    }

    public String getDescription() {
        return BUNDLE.getString("Portal_Description");
    }
}
