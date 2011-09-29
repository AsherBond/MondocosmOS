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
package org.jdesktop.wonderland.client.softphone;

import java.util.ResourceBundle;

    /**
     * Different audio qualities
     */
public enum AudioQuality {
    MINIMUM (8000, 1, 8000, 1, "Minimum (8k mono)"),
    STEREO  (8000, 2, 8000, 1, "Low (8k stereo)"),
    VPN     (16000, 2, 16000, 1, "Normal (16k stereo)"),
    BEST    (44100, 2, 44100, 1, "High (44.1k stereo)");
    
    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/resources/Bundle");

    private final int sampleRate;
    private final int channels;
    private final int transmitSampleRate;
    private final int transmitChannels;
    private final String description;
    
    AudioQuality(int sampleRate, int channels, int transmitSampleRate, 
                 int transmitChannels, String description) 
    {
        this.sampleRate         = sampleRate;
        this.channels           = channels;
        this.transmitSampleRate = transmitSampleRate;
        this.transmitChannels   = transmitChannels;
        this.description        = bundle.getString(description);
    }
    
    public int sampleRate() {
        return sampleRate;
    }
    
    public int channels() {
        return channels;
    }
    
    public int transmitSampleRate() {
        return transmitSampleRate;
    }
    
    public int transmitChannels() {
        return transmitChannels;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
