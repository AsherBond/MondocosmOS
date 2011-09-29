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
package org.jdesktop.wonderland.client.cell.registry.spi;

import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;

/**
 * A CellComponentFactorySPI class is responsible for generating the necessary
 * information to generate a new cell components. This includes:
 * <p>
 * <ol>
 * <li>A default CellComponentServerState class
 * <li>A display name and description to be used in a list of available cell
 * components.
 * </ol>
 * Classes that implement this interface must also annotate themselves with
 * @CellComponentFactory.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface CellComponentFactorySPI {

    /**
     * Returns a default cell component server state class for this cell
     * component type.
     *
     * @return A cell component server state class with default values
     */
    public <T extends CellComponentServerState> T getDefaultCellComponentServerState();
    
    /**
     * Returns the display name of the cell component.
     * 
     * @return The human-readable name of the cell component
     */
    public String getDisplayName();

    /**
     * Returns a description of the cell component.
     *
     * @return A description of the cell component
     */
    public String getDescription();
}
