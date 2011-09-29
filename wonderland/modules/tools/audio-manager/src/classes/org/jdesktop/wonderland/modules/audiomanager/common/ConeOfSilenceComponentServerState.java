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
package org.jdesktop.wonderland.modules.audiomanager.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

import com.jme.math.Vector3f;

import org.jdesktop.wonderland.common.utils.jaxb.Vector3fAdapter;

/**
 * The ConeOfSilenceComponentServerState class is the cell that renders a coneofsilence cell in
 * world.
 * 
 * @author jprovino
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@XmlRootElement(name = "cone-of-silence-component")
@ServerState
public class ConeOfSilenceComponentServerState
        extends CellComponentServerState {

    @XmlElement(name = "name")
    private String name = "ConeOfSilence";
    @XmlElement(name = "boundsType")
    private COSBoundsType boundsType = COSBoundsType.CELL_BOUNDS;
    @XmlElement(name="bounds")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    public Vector3f bounds = new Vector3f(1.5f, 1.5f, 1.5f);
    @XmlElement(name = "showBounds")
    private boolean showBounds = false;
    @XmlElement(name = "outsideAudioVolume")
    private double outsideAudioVolume = 0;

    public enum COSBoundsType {
	CELL_BOUNDS,
	BOX,
	SPHERE
    }

    /** Default constructor */
    public ConeOfSilenceComponentServerState() {
    }

    public ConeOfSilenceComponentServerState(String name) {
	this(name, COSBoundsType.CELL_BOUNDS, new Vector3f());
    }

    public ConeOfSilenceComponentServerState(String name, double fullVolumeRadius) {
	this(name, COSBoundsType.SPHERE, new Vector3f((float) fullVolumeRadius, 0f, 0f));
    }

    public ConeOfSilenceComponentServerState(String name, Vector3f bounds) {
	this(name, COSBoundsType.BOX, bounds);
    }

    public ConeOfSilenceComponentServerState(String name, COSBoundsType boundsType,
	Vector3f bounds) {

        this.name = name;
	this.boundsType = boundsType;
	this.bounds = bounds;
    }

    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.modules.audiomanager." +
                "server.ConeOfSilenceComponentMO";
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public String getName() {
        return name;
    }

    public void setBoundsType(COSBoundsType boundsType) {
	this.boundsType = boundsType;
    }

    @XmlTransient
    public COSBoundsType getBoundsType() {
	return boundsType;
    }

    public void setShowBounds(boolean showBounds) {
	this.showBounds = showBounds;
    }

    @XmlTransient
    public boolean getShowBounds() {
	return showBounds;
    }

    public void setBounds(Vector3f bounds) {
	this.bounds = bounds;
    }

    @XmlTransient
    public Vector3f getBounds() {
	return bounds;
    }

    public void setOutsideAudioVolume(double outsideAudioVolume) {
        this.outsideAudioVolume = outsideAudioVolume;
    }

    @XmlTransient
    public double getOutsideAudioVolume() {
        return outsideAudioVolume;
    }

}
