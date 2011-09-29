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
package org.jdesktop.wonderland.modules.avatarbase.client.imi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.ThreadManager;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarSessionLoader;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * Manages the various IMI avatar configurations a user may have.
 *
 * The local avatar (ie on local disk) are the ones presented to the user
 * for selection.
 *
 * Each avatar file is versioned in the filename using the _[number] postfix
 *
 * When the user connects to a server the system will ensure that the latest
 * versions of avatars are uploaded to the server. It will also download any
 * new files from the server.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ImiAvatarConfigManager {

    private static final Logger logger = Logger.getLogger(ImiAvatarConfigManager.class.getName());

    // A map of current server sessions and that the threads that manage the
    // communcations with each.
    private final Map<ServerSessionManager, ServerSyncThread> avatarConfigServers = new HashMap();

    // A map of local avatar names and the poiners to their configurations that
    // are stored locally on the user's repository.
    private final Map<String, ImiAvatar> localAvatars = new HashMap();

    // Returns the base directory (content collection) that stores all avatar
    // related configuration information on the user's local server.
    private ContentCollection imiCollection = null;

    /**
     * Default constructor
     */
    private ImiAvatarConfigManager() {
        // Fetch the IMI base content collection for configuration, which is
        // the imi/ directory beneath the avatar base.
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        ContentCollection bc = registry.getAvatarCollection();
        try {
            imiCollection = (ContentCollection) bc.getChild("imi");
            if (imiCollection == null) {
                imiCollection = (ContentCollection) bc.createChild("imi", Type.COLLECTION);
            }
            logger.info("Using local IMI avatar collection " +
                    imiCollection.getPath());
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to fetch imi/ collection", excp);
            return;
        }

        // Initialize the set of local avatars. We register them with the system
        // but a SYNC should be called before these avatars can be relied upon
        // so that we know we have the most up-to-date state. The SYNC will
        // correct any avatars that we registered with the system here.
        try {
            List<ContentNode> avatarList = imiCollection.getChildren();
            for (ContentNode node : avatarList) {
                if (node instanceof ContentResource) {
                    ImiAvatar avatar = new ImiAvatar((ContentResource) node);
                    localAvatars.put(avatar.getName(), avatar);
                }
            }
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to fetch local avatars from " +
                    "collection " + imiCollection.getPath(), excp);
            return;
        }
    }

    /**
     * Singleton to hold instance of AvatarConfigManager. This holder class is
     * loader on the first execution of AvatarConfigManager.getAvatarConfigManager().
     */
    private static class ImiAvatarConfigHolder {
        private final static ImiAvatarConfigManager manager = new ImiAvatarConfigManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ImiAvatarConfigManager getImiAvatarConfigManager() {
        return ImiAvatarConfigHolder.manager;
    }

    /**
     * Registers the known local avatars with the avatar registry.
     */
    public void registerAvatars() {
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        synchronized (localAvatars) {
            for (String avatarName : localAvatars.keySet()) {
                ImiAvatar avatar = localAvatars.get(avatarName);
                registry.registerAvatar(avatar, false);
            }
        }
    }

    /**
     * Unregisters the known local avatars with the avatar registry.
     */
    public void unregisterAvatars() {
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        synchronized (localAvatars) {
            for (String avatarName : localAvatars.keySet()) {
                ImiAvatar avatar = localAvatars.get(avatarName);
                registry.unregisterAvatar(avatar);
            }
        }
    }

    /**
     * Returns the URL representing the configuration file for the given avatar
     * on the given server.
     *
     * @param session The current server session
     * @param avatar The avatar
     */
    public URL getAvatarURL(ServerSessionManager session, ImiAvatar avatar)
            throws InterruptedException {

        // First fetch the thread for the given session and then ask it for
        // the URL.
        ServerSyncThread t = null;
        synchronized (avatarConfigServers) {
            t = avatarConfigServers.get(session);
        }
        return t.getAvatarServerURL(avatar);
    }

    /**
     * Returns true if the given server is managed and at least the initial
     * synchronization has been performed.
     *
     * @param session The server session to check
     * @return True if the server session is ready
     */
    public boolean isServerManaged(ServerSessionManager session) {
        synchronized (avatarConfigServers) {
            return avatarConfigServers.containsKey(session);
        }
    }

    /**
     * Adds a new server session for this manager to track and performs an
     * initial synchronization with the avatar configurations found on that
     * server. This method blocks until the initial synchronization is
     * complete, or has been interrupted.
     *
     * @param session The new session to add
     * @throw InterruptedException If the initialization has been interrupted
     */
    public void addServerAndSync(ServerSessionManager session) throws InterruptedException {
        // We do not wish for multiple calls to this method to happen at once
        // for the same server, but we do not want to synchronize this whole
        // method across 'avatarConfigServers' because that would block activity
        // on other servers. So we just synchronize on 'this' which means only
        // a single addServerAndSync() call can be active at once.
        synchronized (this) {
            logger.warning("Adding server " + session.getServerURL());
            
            // First check to see if the session already exists in the map. If
            // so then just return
            synchronized (avatarConfigServers) {
                if (avatarConfigServers.containsKey(session) == true) {
                    logger.info("Server " + session.getServerURL() +
                            " is already present in the manager.");
                    return;
                }
            }

            // Go ahead and synchronize the avatar's local configuration with
            // the server's. Wait for this to complete so we know we are in
            // a 'ready' state.
            ServerSyncThread t = null;
            try {
                logger.info("Starting sychronization with server " +
                        session.getServerURL());

                t = new ServerSyncThread(session);
                t.scheduleSync(true);

                logger.info("Sychronization with server " +
                        session.getServerURL() + " is done.");
            } catch (ContentRepositoryException excp) {
                logger.log(Level.WARNING, "Unable to create sync thread for " +
                        "server " + session.getServerURL(), excp);
                return;
            }

            // Finally, add this server to the list of servers indicating that
            // it is ready.
            synchronized (avatarConfigServers) {
                avatarConfigServers.put(session, t);
                logger.info("Added server " + session.getServerURL() +
                        " to map of managed sessions.");
            }
        }
    }

    /**
     * Removes the session from being managed. If it is not being managed, this
     * method does nothing.
     *
     * @param session The session to remove.
     */
    public void removeServer(ServerSessionManager session) {
        // Remove the server session. If we find a thread, then stop it and
        // remove it from the map.
        logger.warning("Removing server " + session.getServerURL());
        synchronized (avatarConfigServers) {
            ServerSyncThread t = avatarConfigServers.remove(session);
            logger.warning("Stopping thread " + t);
            if (t != null) {
                t.setConnected(false);
            }
        }
    }

    /**
     * Given the avatar, removes the avatar and from all of the server's we
     * are currently connected to.
     *
     * @param avatar The avatar to remove
     */
    public void deleteAvatar(ImiAvatar avatar) {
        // First remove the avatar from the local list, synchronized around the
        // local this so other threads don't update this list at the same time.
        // Also, iterator through all of the servers we know about and tell
        // them to remove the avatar too.
        synchronized (localAvatars) {
            localAvatars.remove(avatar.getName());

            // Remove the avatar from the system
            AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
            registry.unregisterAvatar(avatar);

            try {
                // Remove from the user's local repository.
                String fileName = avatar.getResource().getName();
                imiCollection.removeChild(fileName);

                // Schedule asynchronous jobs to remove the avatar configuration
                // file from all of the server's we are connected to.
                // XXX This is not quite correct as it will not remove older
                // versions of a file
                synchronized (avatarConfigServers) {
                    for (ServerSyncThread c : avatarConfigServers.values()) {
                        try {
                            c.scheduleDelete(avatar, false);
                        } catch (InterruptedException excp) {
                            logger.log(Level.WARNING, "Attempt to delete the" +
                                    " avatar " + avatar.getName() + " from " +
                                    "the server " + c.toString() + " was " +
                                    "interrupted.", excp);
                        }
                    }
                }
            } catch (ContentRepositoryException excp) {
                logger.log(Level.WARNING, "Unable to remove avatar", excp);
            }
        }
    }

    /**
     * Save the given avatar. This method saves the configuration to the user's
     * repository on the local machine and synchronizing with all of the
     * server's we are curently connected to. Upon success, the avatar version
     * is increment (if not new) and a pointer to its local configuration file
     * is updated.
     *
     * @param avatar The avatar to save
     * @throw ContentRepositoryException Upon error writing files
     * @throw IOException Upon error writing data
     * @throw JAXBException Upon error serializing to XML
     */
    public void saveAvatar(ImiAvatar avatar)
            throws ContentRepositoryException, IOException, JAXBException {

        logger.info("Saving avatar with name " + avatar.getName() + " to server.");

        // First check to see if an avatar already exists with the same name.
        // We need this to know whether to update an existing one or not.
        String avatarName = avatar.getName();
        ImiAvatar existingAvatar = localAvatars.get(avatarName);
        
        // Delete the existing avatar locally if there is one. We need to do
        // this before touching the 'avatar' object because they may be one
        // in the same.
        if (existingAvatar != null) {
            ContentResource resource = existingAvatar.getResource();
            ContentCollection parent = resource.getParent();
            try {
                parent.removeChild(resource.getName());
            } catch (ContentRepositoryException excp) {
                logger.log(Level.WARNING, "Unable to remove local avatar " +
                        resource.getPath(), excp);
            }
        }

        // For each server we are connected to, delete the old file from the
        // server
        synchronized (avatarConfigServers) {
            logger.info("Attempting to delete avatar to server, number=" +
                avatarConfigServers.size());

            for (ServerSyncThread t : avatarConfigServers.values()) {
                // If there is an old version, we ask the server to delete the
                // file. We wait for its completion. We need to do this first
                // to get rid of the old avatar before we upload the new one.
                if (existingAvatar != null) {
                    try {
                        logger.info("Schedule delete of existing avatar " +
                                "named " + avatarName + " from server.");
                        t.scheduleDelete(existingAvatar, true);
                    }
                    catch (InterruptedException excp) {
                        logger.log(Level.WARNING, "Attempt to delete the" +
                                " avatar " + avatar.getName() + " from " +
                                "the server " + t.toString() + " was " +
                                "interrupted.", excp);
                    }
                }
            }
        }

        if (existingAvatar != null) {
            logger.info("Already have an avatar named " + avatarName +
                    " with version " + existingAvatar.getVersion());

            // If we already have an avatar, check it's version number,
            // increment it and assign the new version number to this file.
            int version = existingAvatar.getVersion();
            avatar.setVersion(version);
            avatar.incrementVersion();
        }

        // Generate the avatar character from the avatar configuration. We will
        // use this to write out its parameters
        WonderlandCharacterParams params = avatar.getAvatarParams(false);
        WlAvatarCharacter character = ImiAvatar.getAvatarCharacter(params);

        // Create a file to hold the avatar configuration locally if it does
        // not yet exist.
        String fileName = avatar.getFilename();
        ContentResource file = (ContentResource) imiCollection.createChild(fileName, Type.RESOURCE);
        if (file == null) {
            file = (ContentResource) imiCollection.createChild(fileName, Type.RESOURCE);
        }

        logger.info("Writing avatar to resource " + file.getPath());

        // Write out the avatar configuration to a byte avatar.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        character.saveConfiguration(out);
        out.close();

        // Then write out the XML to the local repository. Update the avatar
        // to point to this local avatar resource
        file.put(out.toByteArray());
        avatar.setResource(file);

        // Update the map of local avatars with the new avatar. We always do
        // this no matter whether the avatar is new or whether we are updating
        // an existing avatar.
        synchronized (localAvatars) {
            logger.info("Put avatar named " + avatarName + " in list of avatars");
            localAvatars.put(avatarName, avatar);
        }
        
        // For each server we are connected to, upload the new file.
        synchronized (avatarConfigServers) {
            logger.info("Attempting to upload avatar to server, number=" +
                avatarConfigServers.size());

            for (ServerSyncThread t : avatarConfigServers.values()) {
                // We ask the server to upload the file. We need to wait for
                // it to complete before we tell other clients to use it.
                try {
                    logger.info("Schedule upload of avatar named " + avatarName +
                            " to server " + t.toString());
                    t.scheduleUpload(avatar, true);
                } catch (InterruptedException excp) {
                    logger.log(Level.WARNING, "Attempt to upload the avatar " +
                            avatar.getName() + " to the server " + t.toString() +
                            " was interrupted.", excp);
                }
            }
        }

        // If the avatar is new, tell the system.
        if (existingAvatar == null) {
            logger.info("Registering avater named " + avatarName + " in the system");
            AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
            registry.registerAvatar(avatar, false);
        }
    }

    /**
     * The ServerSyncThread maintains synchronization of avatar information with
     * a particular server session. It runs its own thread to serialize updates
     * with the server (sync, add, remove, etc).
     */
    private class ServerSyncThread extends Thread {

        // The server we are currently collected to
        private ServerSessionManager manager = null;

        // A queue of jobs to execute on the thread.
        private LinkedBlockingQueue<Job> jobQueue = new LinkedBlockingQueue();

        // The collection where all IMI avatar information is kept on the server
        private ContentCollection serverCollection = null;

        // A map of avatar names to pointers to their configuration files on the
        // server
        private Map<String, ImiServerAvatar> serverAvatars = new HashMap();
        
        // True if we are connected to the server, false if we have disconnected
        private boolean isConnected = true;

        public ServerSyncThread(final ServerSessionManager manager)
                throws ContentRepositoryException {

            super(ThreadManager.getThreadGroup(), "AvatarServerSyncThread");
            this.manager = manager;

            // Fetch the base directory in which all IMI avatar configuration info
            // is found on the server.
            serverCollection = getBaseServerCollection(manager);

            // Finally, start the thread off
            this.start();
        }

        /**
         * Sets whether the session is connect or not. If the session has been
         * disconnected, then this should be set to false. Once the thread has
         * been put into the not connected state, the thread is effectively dead.
         *
         * @param isConnected True if the session is connected, false if not.
         */
        public void setConnected(boolean isConnected) {
            this.isConnected = isConnected;
        }

        /**
         * Returns the URL corresponding to the configuration file on the server
         * of given avatar.
         *
         * @param avatar The local avatar
         * @return The URL of the avatar configuration file on the server
         * @throw InterruptedException If the job has been interrupted
         */
        public URL getAvatarServerURL(ImiAvatar avatar) throws InterruptedException {
            Job job = Job.newGetURLJob(avatar);
            jobQueue.add(job);
            job.waitForJob();
            return job.url;
        }

        /**
         * Schedules an asynchronous job to synchronous the IMI avatar
         * configuration between the client and server. The caller may block
         * until the job is complete, by giving isWait of true.
         *
         * @param isWait True to block until the job has completed
         * @throw InterruptedException If the job has been interrupted
         */
        public void scheduleSync(boolean isWait) throws InterruptedException {
            Job job = Job.newSyncJob();
            jobQueue.add(job);
            if (isWait == true) {
                job.waitForJob();
            }
        }

        /**
         * Schedules an asynchronous job to delete an avatar from the server.
         * The caller may block until the job is complete, by giving isWait of
         * true.
         *
         * @param isWait True to block until the job has completed
         * @throw InterruptedException If the job has been interrupted
         */
        public void scheduleDelete(ImiAvatar avatar, boolean isWait) throws InterruptedException {
            Job job = Job.newDeleteJob(avatar);
            jobQueue.add(job);
            if (isWait == true) {
                job.waitForJob();
            }
        }

        /**
         * Schedules an asynchronous job to upload an avatar to the server.
         * The caller may block until the job is complete, by giving isWait of
         * true.
         *
         * @param isWait True to block until the job has completed
         * @throw InterruptedException If the job has been interrupted
         */
        public void scheduleUpload(ImiAvatar avatar, boolean isWait) throws InterruptedException {
            Job job = Job.newUploadJob(avatar);
            jobQueue.add(job);
            if (isWait == true) {
                job.waitForJob();
            }
        }

        /**
         * Returns the collection at the root of all of the avatar configuration
         * information.
         *
         * @return A ContentCollection of avatar configuration info on the server
         * @throw ContentRepositoryException Upon error finding the collection
         */
        private ContentCollection getBaseServerCollection(ServerSessionManager session)
                throws ContentRepositoryException {

            // Fetch the avatars/imi directory, creating each if necessary
            ContentCollection dir = AvatarSessionLoader.getBaseServerCollection(session);
            ContentCollection imiDir = (ContentCollection) dir.getChild("imi");
            if (imiDir == null) {
                imiDir = (ContentCollection) dir.createChild("imi", Type.COLLECTION);
            }
            return imiDir;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            while (isConnected == true) {
                // Fetch the next job. If we are interrupted in doing so, then
                // simply log an error and continue.
                Job job = null;
                try {
                    job = jobQueue.take();
                } catch (InterruptedException excp) {
                    logger.log(Level.WARNING, "Attempt to fetch the next job" +
                            " in queue was interrupted for server " +
                            manager.getServerURL(), excp);
                    continue;
                }

                // Dispatch to the implementation based upon the job type. When
                // done indicate we are done.
                switch (job.jobType) {
                    case SYNC:
                        syncImpl();
                        break;
                    case DELETE:
                        deleteImpl(job.avatar);
                        break;
                    case UPLOAD:
                        uploadFileImpl(job.avatar);
                        break;
                    case GETURL:
                        job.url = getURLImpl(job.avatar);
                        break;
                }
                job.setJobDone();
            }
        }

        /**
         * Synchronous implementation of removing an avatar from the server
         * given the avatar to remove.
         *
         * @param avatar The avatar to remove from the server
         */
        private void deleteImpl(ImiAvatar avatar) {
            // Fetch the name of the avatar and see if an entry exists on the
            // server. If so, then remove it from the server and the map.
            String avatarName = avatar.getName();
            ImiServerAvatar serverAvatar = serverAvatars.get(avatarName);
            if (serverAvatar != null) {
                ContentResource resource = serverAvatar.resource;
                ContentCollection parent = resource.getParent();
                try {
                    parent.removeChild(resource.getName());
                } catch (ContentRepositoryException excp) {
                    logger.log(Level.WARNING, "Unable to delete avatar from" +
                            " server " + avatar, excp);
                }
                serverAvatars.remove(avatarName);
            }
        }

        /**
         * Synchronous implementation of fetching the URL of the server
         * configuration file for the given avatar. Returns the URL.
         */
        private URL getURLImpl(ImiAvatar avatar) {

            // Fetch the server version using the name of the avatar. If it
            // does not exist, log an error and return null
            String avatarName = avatar.getName();
            ImiServerAvatar serverAvatar = serverAvatars.get(avatarName);
            if (serverAvatar == null) {
                logger.severe("No record of avatar " + avatarName +
                        " on server " + manager.getServerURL());
                return null;
            }

            // Otherwise, return its URL or null upon error
            try {
                return serverAvatar.resource.getURL();
            } catch (ContentRepositoryException excp) {
                logger.log(Level.WARNING, "Unable to fetch URL for server" +
                        " avatar " + avatarName + " on server " +
                        manager.getServerURL(), excp);
                return null;
            }
        }

        /**
         * Synchronous implementation of synchronizing the client and server
         * IMI avatar configuration information.
         */
        private void syncImpl() {
            logger.info("Beginning sychronization of server " +
                    manager.getServerURL());

            // Keep arrays of configuration files that need to be uploaded to
            // the server and downloaded from the server.
            List<ImiAvatar> uploadList = new ArrayList();
            List<ImiServerAvatar> downloadList = new ArrayList();

            // Look through the list of avatars on the server. If there was
            // a previous entry in the map (perhaps from a previous SYNC), then
            // remove the old entry. The 'serverAvatars' map contains all of
            // the avatars on the server.
            try {
                List<ContentNode> avatarList = serverCollection.getChildren();
                for (ContentNode node : avatarList) {
                    if (node instanceof ContentResource) {
                        ContentResource resource = (ContentResource)node;
                        ImiServerAvatar serverAvatar = new ImiServerAvatar(resource);
                        String avatarName = serverAvatar.avatarName;

                        logger.info("Looking at server avatar named " +
                                avatarName + " with version " + serverAvatar.version);

                        // Check to see if the server avatar already exists in
                        // the known list of server avatars.
                        ImiServerAvatar previous = serverAvatars.put(avatarName, serverAvatar);
                        if (previous != null && previous.version > serverAvatar.version) {

                            logger.info("Found a previous version named " +
                                    avatarName + " with version " + previous.version);

                            // If we somehow find a more recent avatar in our
                            // list, then we remove the one we just found on
                            // the server. (The most recent one will be added
                            // back to the server later).
                            serverAvatars.put(previous.avatarName, previous);
                            String fileName = serverAvatar.getFilename();
                            serverCollection.removeChild(fileName);
                        }
                    }
                }

                // Make a copy of the map of server avatars. This will serve
                // as a list of all avatars that need to be downloaded from
                // the server.
                Map<String, ImiServerAvatar> tmpServerAvatars = new HashMap(serverAvatars);

                // Loop through all of the avatars we know about locally. See
                // if one by the same name exists on the server. If so, and
                // the server version needs to be updated, then do so. If the
                // local copy needs to be updated then do so.
                synchronized (localAvatars) {

                    logger.info("Taking a look at all of our local avatars");

                    for (ImiAvatar avatar : localAvatars.values()) {
                        String avatarName = avatar.getName();
                        ImiServerAvatar serverVersion = tmpServerAvatars.get(avatarName);

                        logger.info("Looking at local avatar named " +
                                avatarName + " server version " + serverVersion);

                        // If the local avatar is not on the server, or if the
                        // version of the server is older than locally, then
                        // mark this avatar for upload.
                        if (serverVersion == null ||
                                serverVersion.version < avatar.getVersion()) {

                            logger.info("Server version for avatar named " +
                                    avatarName + " does not exist or is older" +
                                    " than version " + avatar.getVersion());

                            uploadList.add(avatar);
                            tmpServerAvatars.remove(avatarName);
                        }
                        else if (serverVersion.version > avatar.getVersion()) {
                            logger.info("Server version for avatar named " +
                                    avatarName + " is more recent than local " +
                                    "with server version " + serverVersion.version +
                                    " and local version " + avatar.getVersion());

                            // Otherwise, if the server has a more recent copy
                            // of the avatar, then mark this avatar for
                            // download.
                            downloadList.add(serverVersion);
                            tmpServerAvatars.remove(avatarName);
                        }
                        else if (serverVersion.version == avatar.getVersion()) {
                            logger.info("Server version for avatar named " +
                                    avatarName + " is same as local version " +
                                    avatar.getVersion());
                            
                            // If the two versions are the same, we just want
                            // to do nothing with it.
                            // XXX Why do we need to re-add it to the server
                            // avatar list? It should already be there.
                            tmpServerAvatars.remove(avatarName);
                            serverAvatars.put(avatarName, serverVersion);
                        }
                    }
                }

                // Avatars left in the serverAvatars map are only on the server,
                // and not on the client (so the previous code block did not
                // see them), so add them to the download list.
                for (ImiServerAvatar serverAvatar : tmpServerAvatars.values()) {
                    logger.info("Adding Server avatar to download list " +
                            serverAvatar.avatarName + " version " +
                            serverAvatar.version);

                    downloadList.add(serverAvatar);
                }


                // For all of the avatar configuration files that we wish to
                // upload, do so synchronously.
                logger.info("Doing upload of local avatars, number to upload " +
                        uploadList.size());

                for (ImiAvatar avatar : uploadList) {
                    logger.info("Uploading Local avatar to server " +
                            avatar.getName() + " version " + avatar.getVersion());

                    uploadFileImpl(avatar);
                }

                // Keep a list around of all of the avatars we have just
                // downloaded, to be added to the set of local avatars later.
                List<ImiAvatar> newAvatarList = new ArrayList();

                // For all of the avatar configuration files that we wish to
                // download, do so synchronously.
                logger.info("Doing download of local avatars to the server," +
                        " number to download " + downloadList.size());

                for (ImiServerAvatar serverAvatar : downloadList) {
                    try {
                        logger.info("Downloading server avatar named " +
                                serverAvatar.avatarName + " to file name " +
                                serverAvatar.resource.getName());

                        // Create an entry for the configuration file locally.
                        // Write the contents of the server version to this file.
                        String fileName = serverAvatar.resource.getName();
                        ContentResource localFile = (ContentResource) imiCollection.createChild(fileName, Type.RESOURCE);
                        InputStream is = serverAvatar.resource.getURL().openStream();
                        localFile.put(new BufferedInputStream(is));

                        // Create a new entry to put on the local list. These
                        // will be added below.
                        ImiAvatar newAvatar = new ImiAvatar(localFile);
                        newAvatarList.add(newAvatar);

                        logger.info("Local avatar created in resource " +
                                localFile.getPath());

                    } catch (IOException excp) {
                        logger.log(Level.WARNING, "Error downloading server " +
                                "avater named " + serverAvatar.avatarName, excp);
                    }
                }

                // For all of the new avatars we just downloaded, upload the
                // list of local avatars. We also fire an event to indicate
                // that a new avatar has been added.
                logger.info("Adding new local avatars to system, " +
                        "number of avatars " + newAvatarList.size());

                synchronized (localAvatars) {
                    for (ImiAvatar newAvatar : newAvatarList) {
                        logger.info("Adding new local avatar to system " +
                                "named " + newAvatar.getName());

                        localAvatars.put(newAvatar.getName(), newAvatar);
                        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
                        registry.registerAvatar(newAvatar, false);
                    }
                }
            } catch (ContentRepositoryException excp) {
                logger.log(Level.WARNING, "Error synchronizing with server " +
                        manager.getServerURL(), excp);
            }
        }

        /**
         * Synchronous implementation of uploading an avatar to the server given
         * the avatar.
         *
         * @param avatar The avatar to upload to the server
         */
        private void uploadFileImpl(ImiAvatar avatar) {
            // Fetch the avatar we wish to upload and the resource that corresponds
            // to the local configuration file.
            ContentResource resource = avatar.getResource();
            String fileName = resource.getName();

            try {
                // Create a resource on the server with the same as the local resource.
                // Assume the file does not yet exist on the server. Then go ahead
                // and upload the local file to the server.
                ContentResource file = (ContentResource) serverCollection.createChild(fileName, Type.RESOURCE);
                InputStream is = resource.getURL().openStream();
                file.put(new BufferedInputStream(is));

                // Add an entry to the map of avatars on the server.
                ImiServerAvatar serverAvatar = new ImiServerAvatar(file);
                serverAvatars.put(serverAvatar.avatarName, serverAvatar);
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "Unable to upload avatar config", excp);
            }
        }

        @Override
        public String toString() {
            return manager.getServerURL();
        }
    }

    /**
     * A static inner class that represents an avatar configuration on the
     * server
     */
    private static class ImiServerAvatar {
        // The content repository resource pointing to the remote config file
        // on the server.
        public ContentResource resource = null;
        
        // The version number of the configuration file
        public int version = 0;

        // The name of the configuration file, stripped of the version number.
        public String avatarName = null;

        private static final String EXTENSION = ".xml";

        /**
         * Constructor, takes the content resource
         * @param resource
         */
        public ImiServerAvatar(ContentResource resource) {
            this.resource = resource;
            String name = resource.getName();
            version = getAvatarVersion(name);
            int i = name.lastIndexOf('_');
            if (i == -1) {
                avatarName = name;
            } else {
                avatarName = name.substring(0, i);
            }
        }

        /**
         * Return the file name corresponding to the avatar's configuration file.
         *
         * @return The configuration file name
         */
        public String getFilename() {
            return avatarName + "_" + version + EXTENSION;
        }

        /**
         * Returns the version number embedded in the configuration file name.
         */
        private int getAvatarVersion(String filename) {
            // The version number is found between the final underscore and
            // the file extension.
            int underscore = filename.lastIndexOf('_');
            int ext = filename.lastIndexOf('.');

            if (underscore == -1 || ext == -1) {
                return -1;
            }
            String verStr = filename.substring(underscore + 1, ext);
            try {
                return Integer.parseInt(verStr);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    /**
     * A static inner class that represents a 'job' submitted to the 'server
     * sync' thread. Threads may block on the completion of a thread by
     * invoking waitForJob().
     */
    private static class Job {

        public enum JobType {
            SYNC, DELETE, UPLOAD, GETURL
        };

        public JobType jobType;
        public ImiAvatar avatar = null;
        public Semaphore jobDone = null;
        public URL url = null;

        private Job(JobType jobType, ImiAvatar avatar) {
            this.jobType = jobType;
            this.avatar = avatar;
            this.jobDone = new Semaphore(0);
            this.url = null;
        }

        public static Job newSyncJob() {
            return new Job(JobType.SYNC, null);
        }

        public static Job newDeleteJob(ImiAvatar avatar) {
            return new Job(JobType.DELETE, avatar);
        }

        public static Job newUploadJob(ImiAvatar avatar) {
            return new Job(JobType.UPLOAD, avatar);
        }

        public static Job newGetURLJob(ImiAvatar avatar) {
            return new Job(JobType.GETURL, avatar);
        }

        /**
         * Waits for this job to be done.
         * @throw InterruptedException If the job has been interrupted
         */
        public void waitForJob() throws InterruptedException {
            jobDone.acquire();
        }

        /**
         * Used by jobs to indicate they have completed.
         */
        public void setJobDone() {
            jobDone.release();
        }
    }
}

