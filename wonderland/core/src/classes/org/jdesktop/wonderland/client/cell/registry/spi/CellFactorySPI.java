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

import java.awt.Image;
import java.util.Properties;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * A CellFactorySPI class is responsible for generating the necessary information
 * to generate a new cell. This includes:
 * <ol>
 * <li>A default cell setup class.
 * <li>A display name and image to be used in a palette of cell types.
 * <li>A list of file extensions which can be rendered by this cell type.
 * </ol>
 * Classes that implement this interface must also annotate themselves with
 * @CellFactory.
 * <p>
 * The main purpose of implements of this class is to generate a CellServerState
 * that is used to create an instance of the Cell. The getDefaultCelServerState()
 * is used for this purpose and takes a set of properties. These properties are
 * used to initialize the values inside the CellServerState. The following key
 * values are defined:
 * <p>
 * content-uri: Used to inject a URI of content to associated with the cell
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface CellFactorySPI {

    /**
     * Returns an array of extension file types supported by this cell. The
     * cell can handle media with these extensions. The file extensions are
     * case insensitive.
     * 
     * @return An array of supported file extension (e.g. 'jpg', 'dae')
     */
    public String[] getExtensions();
    
    /**
     * Returns a default cell server state class for this cell type, given a
     * (possibly null) set of properties. The properties carries a collection
     * of key-value pairs used to initialize the state of the cell. The key
     * values are defined by a standard mapping. If this method returns null,
     * a new Cell is not created.
     *
     * @param props A set of initial properties
     * @return A cell server state class with default values
     */
    public <T extends CellServerState> T getDefaultCellServerState(Properties props);
    
    /**
     * Returns the human-readable display name of the cell type to display in
     * the palette of cell types. If the cell type should not appear in the
     * palette, this method should return null.
     *
     * @return The name of the cell type
     */
    public String getDisplayName();

    /**
     * Returns an image preview of the cell type.
     *
     * @return An image of the cell type
     */
    public Image getPreviewImage();
}
