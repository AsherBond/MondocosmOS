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
package org.jdesktop.wonderland.modules.coneofsilence.client.cell;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.coneofsilence.common.ConeOfSilenceCellServerState;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;

/**
 * The cell factory for the sample cell.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@CellFactory
public class ConeOfSilenceCellFactory implements CellFactorySPI {

    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/coneofsilence/client/cell/" +
            "resources/Bundle");
    private final static Logger LOGGER =
            Logger.getLogger(ConeOfSilenceCellFactory.class.getName());

    public String[] getExtensions() {
        return new String[]{};
    }

    public <T extends CellServerState> T getDefaultCellServerState(
            Properties props) {
        // Create a setup with some default values
        ConeOfSilenceCellServerState cellServerState =
                new ConeOfSilenceCellServerState();

	cellServerState.setName("ConeOfSilence");

        // Give the hint for the bounding volume for initial Cell placement
        BoundingBox box = new BoundingBox(new Vector3f(), 2f, 0f, 2f);
	    
	BoundingVolumeHint hint = new BoundingVolumeHint(true, box);
	cellServerState.setBoundingVolumeHint(hint);

        return (T) cellServerState;
    }

    public String getDisplayName() {
        return BUNDLE.getString("Cone_of_Silence");
    }

    public Image getPreviewImage() {
        URL url = ConeOfSilenceCellFactory.class.getResource(
                "resources/coneofsilence_preview.png");
        return Toolkit.getDefaultToolkit().createImage(url);
    }
}
