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

import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The component client state
 * @author jprovino
 */
public class AudioParticipantComponentClientState extends CellComponentClientState {

    private boolean isSpeaking;
    private boolean isMuted;

    public AudioParticipantComponentClientState() {
    }

    public AudioParticipantComponentClientState(boolean isSpeaking, boolean isMuted) {
	this.isSpeaking = isSpeaking;
	this.isMuted = isMuted;
    }

    @XmlElement
    public boolean isSpeaking() {
	return isSpeaking;
    }

    @XmlElement
    public boolean isMuted() {
	return isMuted;
    }

}
