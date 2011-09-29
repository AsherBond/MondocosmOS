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
package org.jdesktop.wonderland.servermanager.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data collected from a ping of the server
 * @author jkaplan
 */
@XmlRootElement(name="pingdata")
public class PingData {
    private long sampleDate;
    private long pingTime = -1;
    private String pingNoteTitle;
    private String pingNoteText;

    public PingData() {
        this (System.currentTimeMillis());
    }
    
    public PingData(long sampleDate) {
        this.sampleDate = sampleDate;
    }
    
    @XmlElement
    public long getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(long sampleDate) {
        this.sampleDate = sampleDate;
    }
    
    @XmlElement
    public long getPingTime() {
        return pingTime;
    }

    public void setPingTime(long pingTime) {
        this.pingTime = pingTime;
    }
    
    @XmlElement
    public String getPingNoteTitle() {
        return pingNoteTitle;
    }

    public void setPingNoteTitle(String pingNoteTitle) {
        this.pingNoteTitle = pingNoteTitle;
    }
    
    @XmlElement
    public String getPingNoteText() {
        return pingNoteText;
    }

    public void setPingNoteText(String pingNoteText) {
        this.pingNoteText = pingNoteText;
    }    
}
