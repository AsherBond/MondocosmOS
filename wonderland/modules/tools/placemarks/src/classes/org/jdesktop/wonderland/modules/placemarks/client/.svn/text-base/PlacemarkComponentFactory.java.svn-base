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
package org.jdesktop.wonderland.modules.placemarks.client;

import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellComponentFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellComponentFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkComponentServerState;

/**
 * The cell component factory for the placemark cell component.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@CellComponentFactory
public class PlacemarkComponentFactory implements CellComponentFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/placemarks/client/resources/Bundle");

    public String getDisplayName() {
        return BUNDLE.getString("Placemark");
    }

    public <T extends CellComponentServerState> T getDefaultCellComponentServerState() {
        PlacemarkComponentServerState state = new PlacemarkComponentServerState();
        return (T) state;
    }

    public String getDescription() {
        return BUNDLE.getString("Placemark_Description");
    }
}
