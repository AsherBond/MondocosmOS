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
package org.jdesktop.wonderland.modules.jmecolladaloader.client.cell;

import java.awt.Image;
import java.util.Properties;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellServerState;

/**
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@CellFactory
public class JmeColladaCellFactory implements CellFactorySPI {

    public String[] getExtensions() {
        return new String[] { "dae" };
    }

    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {
        ModelCellServerState state = new ModelCellServerState();

       // Look for the content-uri field and set if so
       if (props != null) {
           String uri = props.getProperty("content-uri");
           if (uri != null) {
               System.err.println("JmeColladaCellFactory.getDefaultCellServerState needs updating to new API ! "+uri);
//               state.setModel(uri);
           }
       }
        return (T)state;
    }

    public String getDisplayName() {
        // If we return null, then this factory will not appear in the palette
        return null;
    }

    public Image getPreviewImage() {
        return null;
    }
}
