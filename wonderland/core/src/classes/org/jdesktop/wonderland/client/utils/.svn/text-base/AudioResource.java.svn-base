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
package org.jdesktop.wonderland.client.utils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Encapsulate a URL that identifies an audio resource. Only provides the ability to play.
 * TODO: consider adding stop() and refactor internal state
 * @author Bernard Horan
 */
public class AudioResource {

    private static final Logger resourceLogger = Logger.getLogger(AudioResource.class.getName());
    private URL resourceURL;
    private float volume;

    /**
     * Create a new audio resource from a URL. If the URL is inaccessible the play() method will
     * report an exception.
     * @param resourceURL the URL that identifies the audio resource.
     */
    public AudioResource(URL resourceURL) {
        this.resourceURL = resourceURL;
    }
    
    /**
     * Play the audio resource
     */
    public void play() {
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(resourceURL);
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, audioFormat);
            Clip clip = getClip(dataLineInfo);
            clip.open(audioInputStream);
            FloatControl volctrl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            volctrl.setValue(volume);
            clip.start();
        } catch (UnsupportedAudioFileException ex) {
            resourceLogger.log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            resourceLogger.warning("cannot play audio resource, due to " + ex.getLocalizedMessage());
        } catch (IOException ex) {
            resourceLogger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                audioInputStream.close();
            } catch (IOException ex) {
                resourceLogger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    private Clip getClip(DataLine.Info info) throws LineUnavailableException {
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(mi);
            try {
                return (Clip) mixer.getLine(info);
            } catch (LineUnavailableException ex) {
                resourceLogger.warning("Matching line not available: " + ex.getLocalizedMessage());
            } catch (IllegalArgumentException ex) {
                resourceLogger.warning("Mixer does not support this format. " + ex.getLocalizedMessage());
            }
        }
        throw new LineUnavailableException("Failed to get any line for info: " + info);
    }
}
