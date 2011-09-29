/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.modules.phone.client.cell;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.modules.phone.common.PhoneCellServerState;
import org.jdesktop.wonderland.modules.phone.common.PhoneInfo;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;

/**
 * The cell factory for the sample cell.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@CellFactory
public class PhoneCellFactory implements CellFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/phone/client/cell/resources/" +
            "Bundle");

    public String[] getExtensions() {
        return new String[]{};
    }

    public <T extends CellServerState> T getDefaultCellServerState(
            Properties props) {
        // Create a setup with some default values
        PhoneCellServerState cellServerState = new PhoneCellServerState();

	cellServerState.setName(BUNDLE.getString("Virtual_Phone"));

        cellServerState.setPhoneInfo(new PhoneInfo(false, "100", "foo",
                "Unknown location", .2, .1, true, true));

        /*
         * Try rotating 45 degrees to see what that does.
         */
        //Vector3f axis = new Vector3f((float) 1, (float) 0, (float) 0);
        //cellServerState.setRotation(new Rotation(axis, (float) Math.PI / 4));

	BoundingBox box = new BoundingBox(
	    new Vector3f(0, 0, 0), 0.27053905F, 0.060000006F, 0.27053908F);

	BoundingVolumeHint hint = new BoundingVolumeHint(true, box);
	cellServerState.setBoundingVolumeHint(hint);
	
        Logger.getLogger(PhoneCellFactory.class.getName()).warning(
                "New Virtual Phone!!!!");
        return (T) cellServerState;
    }

    public String getDisplayName() {
        return BUNDLE.getString("Virtual_Phone");
    }

    public Image getPreviewImage() {
        URL url = PhoneCellFactory.class.getResource(
                "resources/virtualphone_preview.png");
        return Toolkit.getDefaultToolkit().createImage(url);
    }
}
