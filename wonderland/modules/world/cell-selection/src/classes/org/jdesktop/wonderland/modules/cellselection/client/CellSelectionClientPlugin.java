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
package org.jdesktop.wonderland.modules.cellselection.client;

import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.cell.utils.CellSelectionRegistry;
import org.jdesktop.wonderland.client.cell.utils.spi.CellSelectionSPI;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 * Client-side plugin to register the mechanism to determine the proper Cell
 * to use upon creation.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class CellSelectionClientPlugin extends BaseClientPlugin {

    private CellSelectionSPI spi = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(ServerSessionManager sessionManager) {
        spi = new SimpleCellSelectionSPI();
        super.initialize(sessionManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void activate() {
        // Register the "simple" Cell selection facility that displays a dialog
        // box
        CellSelectionRegistry.register(spi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deactivate() {
        // Unregister the "simple" Cell selection facility
        CellSelectionRegistry.unregister(spi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        spi = null;
        super.cleanup();
    }
}
