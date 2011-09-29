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
package org.jdesktop.wonderland.modules.audiomanager.client;

import org.jdesktop.wonderland.client.softphone.AudioQuality;

/**
 * Listener for Audio Menu items
 * 
 * @author  jprovino
 */
public interface AudioMenuListener {

    public void setMute(boolean isMuted);

    public void showSoftphone();

    public void setAudioQuality(AudioQuality quality);

    public void testAudio();

    public void testUDPPort();

    public void reconnectSoftphone();

    public void transferCall();

    public void logAudioProblem();

    public void personalPhone();

    public void voiceChat();

    public void audioVolume();

}
