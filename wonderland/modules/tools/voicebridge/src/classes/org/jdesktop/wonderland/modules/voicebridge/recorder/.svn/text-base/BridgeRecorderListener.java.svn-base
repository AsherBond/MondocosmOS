/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.modules.voicebridge.recorder;

import com.sun.voip.Recorder;
import com.sun.voip.RecorderListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * Represents a listener that listens when the recording is started and stopped.
 * @author Joe Provino
 * @author Bernard Horan
 */
public class BridgeRecorderListener implements RecorderListener {

    private static final Logger logger = Logger.getLogger(BridgeRecorderInitializer.class.getName());
    private static final String AUDIO_RECORDINGS_DIRECTORY = "AudioRecordings";
    private Recorder recorder;
    private BridgeRecorderInitializer brInitializer;
    private int records;
    private int bytes;

    public BridgeRecorderListener(Recorder recorder, BridgeRecorderInitializer brInitializer) {
        this.recorder = recorder;
        this.brInitializer = brInitializer;
        logger.info("adding recorder listener...");
        recorder.addRecorderListener(this);
    }

    public void recorderStarted() {
        logger.info("Start recording " + recorder.getRecordPath() + " " + recorder.getMediaInfo());
    }

    /**
     * The recording has stopped. So copy the recorded file to webdav.
     */
    public void recorderStopped() {
        try {
            logger.info("Stop recording " + recorder.getRecordPath() + " records " + records + " bytes " + bytes);
            copyFileToWebDav();
            logger.info("Copied file, now recordingDone");
            recorder.recorderDone();
        } catch (ContentRepositoryException ex) {
            logger.log(Level.SEVERE, "Failed to copy file to web dav, problem with content repository", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to copy file due to an IO exception", ex);
        }
    }

    public void recorderDone() {
    }
    
    /**
     * Mainly logging
     * @param buffer
     * @param offset
     * @param length
     */
    public void recorderData(byte[] buffer, int offset, int length) {
        records++;
        bytes += length;
    }

    private void copyFileToWebDav() throws FileNotFoundException, ContentRepositoryException, IOException {
        ContentCollection recordingRoot = getSystemRoot(brInitializer.getSessionManager());
        if (recordingRoot == null) {
            logger.severe("Failed to get recording root");
            return;
        }
        logger.info("recording root: " + recordingRoot);
        File audioFile = new File(recorder.getRecordPath());
        if (!audioFile.exists()) {
            throw new FileNotFoundException();
        }
        ContentNode node = recordingRoot.getChild(AUDIO_RECORDINGS_DIRECTORY);
        if (node == null) {
            node = recordingRoot.createChild(AUDIO_RECORDINGS_DIRECTORY, Type.COLLECTION);
        }
        ContentCollection dirNode = (ContentCollection) node;
        logger.info("directory for audio recordings: " + dirNode);
        String recordingName = audioFile.getName();
        logger.info("recording name: " + recordingName);
        node = dirNode.getChild(recordingName);
        if (node != null) {
            logger.info("recording already exists, so removing: " + recordingName);
            dirNode.removeChild(recordingName);
        }
        node = dirNode.createChild(recordingName, Type.RESOURCE);
        ContentResource resource = (ContentResource) node;
        logger.info("created a node for the audio file: " + resource);
        resource.put(audioFile);
    }

    /**
     * Returns the content repository root for the system root, or null upon
     * error.
     */
    private ContentCollection getSystemRoot(ServerSessionManager loginInfo) {
        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        ContentRepository repo = registry.getRepository(loginInfo);
        if (repo == null) {
            logger.severe("Repository is null");
            return null;
        }
        try {
            return repo.getSystemRoot();
        } catch (ContentRepositoryException excp) {
            logger.log(Level.SEVERE, "Unable to find repository root", excp);
            return null;
        }
    }
}
