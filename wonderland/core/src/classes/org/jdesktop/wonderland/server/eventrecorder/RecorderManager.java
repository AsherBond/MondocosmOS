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
package org.jdesktop.wonderland.server.eventrecorder;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;



/**
 * Responsible for recording events in Wonderland.
 * Manages a singleton instance of itself.
 * @author Bernard Horan
 */
public class RecorderManager {
    private static final Logger logger = Logger.getLogger(RecorderManager.class.getName());
    private static RecorderManager DEFAULT_MANAGER;

    /***
     *Mapping table from name of event recorder to its instance. Can't use a Set due to DarkStar
     * managing persistence. (I.e. no object identity).
     */

    private Map<String, EventRecorder> recorders = new HashMap<String, EventRecorder>();

    /**
     * Return the singleton instance of the RecorderManager
     */
    public static RecorderManager getDefaultManager() {
        if (DEFAULT_MANAGER == null) {
            DEFAULT_MANAGER = new RecorderManager();
        }
        return DEFAULT_MANAGER;
    }

    /** Creates a new instance of RecorderManager */
    RecorderManager() {
    }

    /**
     * Record the message from the sender
     * @param sender the sender of the message
     * @param clientID the id of the client sending the message
     * @param message
     */
    public void recordMessage(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
        for (EventRecorder recorder : recorders.values()) {
            recorder.recordMessage(sender, clientID, message);
        }
    }

    public boolean isRecording() {
        for (EventRecorder recorder : recorders.values()) {
            if (recorder.isRecording()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Record the metadata corresponding to the message
     * @param message a message that's been received and recorded
     * @param metadata the string metadata that acts as an annotation to the message
     */
    public void recordMetadata(CellMessage message, String metadata) {
        for (EventRecorder recorder : recorders.values()) {
            recorder.recordMetadata(message, metadata);
        }
    }

    /**
     * Register an event recorder with me.
     * @param recorder the recorder to be registered
     */
    public void register(EventRecorder recorder) {
        recorders.put(recorder.getName(), recorder);
    }

    /**
     * Unregister an event recorder
     * @param recorder the event recorder to be unregistered
     */
    public void unregister(EventRecorder recorder) {
        EventRecorder registeredRecorder = recorders.get(recorder.getName());
        if (registeredRecorder == null) {
            throw new RuntimeException("Trying to remove unregistered recorder: " + recorder.getName());
        }
        recorders.remove(registeredRecorder.getName());
    }

}
