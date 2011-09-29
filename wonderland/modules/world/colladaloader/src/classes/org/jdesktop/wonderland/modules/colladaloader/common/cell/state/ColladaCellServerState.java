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
package org.jdesktop.wonderland.modules.colladaloader.common.cell.state;

import org.jdesktop.wonderland.common.cell.state.CellServerState;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.cell.state.spi.CellServerStateSPI;

/**
 * The ColladaCellServerState class is the cell that renders a collada model cell in
 * world.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="collada-cell")
public class ColladaCellServerState extends CellServerState implements Serializable, CellServerStateSPI {
    
    /* The URI of the static model file */
    @XmlElement(name="model")
    public AssetURI model = null;
    
    /** Default constructor */
    public ColladaCellServerState() {
    }
    
    /**
     * Returns the model URI.
     * 
     * @return The model URI specification
     */
    @XmlTransient
    public AssetURI getModel() {
        return this.model;
    }
    
    /**
     * Sets the model URI. If null, then this property will not be written
     * out to the file.
     * 
     * @param model The model URI
     */
    public void setModel(AssetURI model) {
        this.model = model;
    }
    
    public String getServerClassName() {
        return "org.jdesktop.wonderland.modules.colladaloader.server.cell.ColladaCellMO";
    }
    
    /**
     * Returns a string representation of this class
     *
     * @return The setup information as a string
     */
    @Override
    public String toString() {
        return super.toString() + " [ColladCellSetup] model: " + this.model;
    }
}
