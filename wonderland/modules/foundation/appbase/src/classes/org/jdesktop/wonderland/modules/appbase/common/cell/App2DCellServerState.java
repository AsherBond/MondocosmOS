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
package org.jdesktop.wonderland.modules.appbase.common.cell;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * The WFS server state class for App2DCellMO.
 * 
 * @author deronj
 */
public abstract class App2DCellServerState extends CellServerState 
{

    /** The X pixel scale of the app's windows. */
    @XmlElement(name = "pixelScaleX")
    public float pixelScaleX = 0.01f;
    /** The Y pixel scale of the app's windows. */
    @XmlElement(name = "pixelScaleY")
    public float pixelScaleY = 0.01f;

    /** Default constructor */
    public App2DCellServerState() {
    }

    @XmlTransient
    public float getPixelScaleX() {
        return pixelScaleX;
    }

    public void setPixelScaleX(float pixelScaleX) {
        this.pixelScaleX = pixelScaleX;
    }

    @XmlTransient
    public float getPixelScaleY() {
        return pixelScaleY;
    }

    public void setPixelScaleY(float pixelScaleY) {
        this.pixelScaleY = pixelScaleY;
    }

    /**
     * Returns a string representation of this class.
     *
     * @return The server state information as a string.
     */
    @Override
    public String toString() {
        return super.toString() + " [App2DCellServerState]: " +
                "pixelScaleX=" + pixelScaleX + "," +
                "pixelScaleY=" + pixelScaleY;
    }
}
