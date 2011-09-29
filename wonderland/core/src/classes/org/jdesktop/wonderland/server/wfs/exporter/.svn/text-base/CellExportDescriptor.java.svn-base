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
package org.jdesktop.wonderland.server.wfs.exporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.wfs.CellDescriptor;
import org.jdesktop.wonderland.common.wfs.CellPath;
import org.jdesktop.wonderland.common.wfs.WorldRoot;

/**
 * Cell descriptor used during exporting.  This stores the actual server
 * state, which it encodes lazily.  This ensures that the encoding happens
 * in the service, and not in a Darkstar transaction
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class CellExportDescriptor extends CellDescriptor {
    private CellServerState serverState;

    /** Default constructor */
    public CellExportDescriptor() {
    }

    /** Constructor, takes all of the class attributes */
    public CellExportDescriptor(WorldRoot worldRoot, CellPath cellParent,
                                String cellID, String cellName,
                                CellServerState serverState)
    {
        super (worldRoot, cellParent, cellID, cellName, null);

        this.serverState = serverState;
    }

    /**
     * Generate setup information lazily
     */
    @Override
    public String getSetupInfo() {
        String setupInfo = super.getSetupInfo();
        if (setupInfo == null) {
            try {
                // Write the setup information as an XML string. If we have trouble
                // writing this, then punt.
                StringWriter sw = new StringWriter();
                serverState.encode(sw);
                setupInfo = sw.toString();

                super.setSetupInfo(setupInfo);
            } catch (JAXBException ex) {
                throw new IllegalStateException("Unable to write " +
                                                serverState, ex);
            }
        }

        return setupInfo;
    }
}
