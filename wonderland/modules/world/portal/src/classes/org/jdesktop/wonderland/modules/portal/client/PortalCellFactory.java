/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentServerState;

/**
 * The cell factory for the portal cell.
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@CellFactory
public class PortalCellFactory implements CellFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/portal/client/resources/Bundle");

    public String[] getExtensions() {
        return new String[]{};
    }

    public <T extends CellServerState> T getDefaultCellServerState(
            Properties props) {

        ModelCellServerState cellState = new ModelCellServerState();
        cellState.setBoundingVolumeHint(new BoundingVolumeHint(true,
                new BoundingBox(Vector3f.ZERO, 
                                1.44526004f,
                                1.44525876f,
                                0.13593452f)));
        
        // add the model
        ModelCellComponentServerState mccss = new ModelCellComponentServerState();
        mccss.setDeployedModelURL("wla://portal/portal.kmz/portal.kmz.dep");
        mccss.setLightingEnabled(false);
        cellState.addComponentServerState(mccss);
        
        // add the portal state (by default this will send the user to the
        // origin)
        cellState.addComponentServerState(new PortalComponentServerState());
        
        // set the scaling to make the model smaller
        PositionComponentServerState pcss = new PositionComponentServerState();
        pcss.setScaling(new Vector3f(0.4f, 0.4f, 0.4f));
        cellState.addComponentServerState(pcss);
        
        return (T) cellState;
    }

    public String getDisplayName() {
        return BUNDLE.getString("Portal_Cell");
    }

    public Image getPreviewImage() {
        URL url = PortalCellFactory.class.getResource("resources/Portal2.png");
        return Toolkit.getDefaultToolkit().createImage(url);
    }
}
