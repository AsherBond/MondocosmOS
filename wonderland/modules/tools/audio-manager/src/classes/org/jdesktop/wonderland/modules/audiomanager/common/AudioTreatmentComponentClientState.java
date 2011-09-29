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
package org.jdesktop.wonderland.modules.audiomanager.common;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentServerState.PlayWhen;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentServerState.TreatmentType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

/**
 * The component client state
 * @author jprovino
 */
public class AudioTreatmentComponentClientState extends AudioParticipantComponentClientState {

    public String groupId = null;

    public TreatmentType treatmentType;

    public String[] treatments;

    public double volume;

    public PlayWhen playWhen;

    public boolean playOnce;

    public double extent;

    public boolean useCellBounds;

    public double fullVolumeAreaPercent;

    public boolean distanceAttenuated;

    public double falloff;

    public boolean showBounds = false;

    public AudioTreatmentComponentClientState() {
	super(false, false);
    }

    @XmlElement
    public String getGroupId() {
	return groupId;
    }

    @XmlElement
    public TreatmentType getTreatmentType() {
	return treatmentType;
    }

    @XmlElement
    public String[] getTreatments() {
	return treatments;
    }

    @XmlElement
    public double getVolume() {
        return volume;
    }

    @XmlElement
    public PlayWhen getPlayWhen() {
        return playWhen;
    }

    @XmlElement
    public boolean getPlayOnce() {
        return playOnce;
    }

    @XmlElement
    public double getExtent() {
        return extent;
    }

    @XmlElement
    public boolean getUseCellBounds() {
        return useCellBounds;
    }

    @XmlElement
    public double getFullVolumeAreaPercent() {
        return fullVolumeAreaPercent;
    }
    @XmlElement
    public boolean getDistanceAttenuated() {
        return distanceAttenuated;
    }

    @XmlElement
    public double getFalloff() {
	return falloff;
    }

    @XmlElement
    public boolean getShowBounds() {
	return showBounds;
    }

}
