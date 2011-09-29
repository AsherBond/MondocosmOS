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
package org.jdesktop.wonderland.modules.avatarbase.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.annotation.AvatarFactory;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarFactorySPI;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;

/**
 * Manages the initialization of avatars from various sources for a given
 * session. The session loader exists in three states: INIT, LOADING, and
 * READY. The INIT state indicates that object has been created, but has
 * not begun loading, the LOADING state indicates that avatars are being
 * loaded, and READY indicates that all avatars have been loaded.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AvatarSessionLoader {

    private static Logger logger = Logger.getLogger(AvatarSessionLoader.class.getName());
    
    /** Enumeration that defines the state of the loader */
    public enum State { INIT, LOADING, READY };

    // The session associated with the loader
    private ServerSessionManager manager = null;

    // The current state of the session loader
    private State currentState = State.INIT;

    // The set of Class object that represent avatars being loaded. Each class
    // that implements AvatarFactorySPI will be represented in this list when
    // loading and removed when done.
    private Set<Class<AvatarFactorySPI>> loadingSet = new HashSet();

    // A set of listeners for state change events on this loader.
    private Set<AvatarLoaderStateListener> listenerSet = new HashSet();

    /** Constructor */
    public AvatarSessionLoader(ServerSessionManager manager) {
        this.manager = manager;
    }

    /**
     * Sets the state of the avatar session loader.
     *
     * @param state The new state
     */
    public void setState(State state) {
        synchronized (currentState) {
            currentState = state;
            fireAvatarLoaderStateEvent(state);
        }
    }

    /**
     * Returns the current state of the avatar session loader.
     *
     * @return The state
     */
    public State getState() {
        synchronized (currentState) {
            return currentState;
        }
    }

    /**
     * Returns the session associated with this loader.
     *
     * @return The session
     */
    public ServerSessionManager getSession() {
        return manager;
    }

    /**
     * Removes an avatar factory from the set of loading. This updates the
     * state of the loader session to READY once this set is empty.
     *
     * @param clazz The AvatarFactorySPI Class to remove
     */
    public void loadingComplete(Class<AvatarFactorySPI> clazz) {
        synchronized (loadingSet) {
            logger.info("Loading is complete for factory " + clazz.getName());
            loadingSet.remove(clazz);
            if (loadingSet.isEmpty() == true) {
                setState(State.READY);
            }
        }
    }

    /**
     * Asynchronously loads the set of avatars from all of the various sources
     * in the system.
     */
    public void load() {
        // First place the state of the loader into LOADING only if it is in
        // INIT.
        synchronized (currentState) {
            if (currentState == State.LOADING) {
                logger.warning("Calling load() while already loading.");
                return;
            }
            else if (currentState == State.READY) {
                logger.warning("Calling load() while already ready.");
                return;
            }
            currentState = State.LOADING;
        }

        // Next find all of the classes that are annotated with @AvatarFactory.
        // and place their classes in the set of loading classes.
        Set<AvatarFactorySPI> factorySet = new HashSet();
        ScannedClassLoader scl = manager.getClassloader();
        Iterator<AvatarFactorySPI> it = scl.getAll(AvatarFactory.class, AvatarFactorySPI.class);
        while (it.hasNext() == true) {
            factorySet.add(it.next());
        }

        // Place each of the factory classes in the loading set
        for (AvatarFactorySPI factory : factorySet) {
            loadingSet.add((Class<AvatarFactorySPI>)factory.getClass());
        }

        // For each, spawn a thread and kick off the loader for each of the
        // factory. We catch exceptions just in case.
        for (AvatarFactorySPI factory : factorySet) {
            final AvatarFactorySPI f = factory;
            logger.info("Loading avatar from factory " + f.getClass().getName());
            new Thread() {
                @Override
                public void run() {
                    try {
                        f.registerAvatars(manager);
                    } catch (java.lang.Exception excp) {
                        // Catch any and all exceptions and print a message to the log
                        logger.log(Level.WARNING, "Exception from avatar factory", excp);
                    } finally {
                        // Always remove the class from the set. When this set is empty
                        // the loading state will be made READY.
                        loadingComplete((Class<AvatarFactorySPI>) f.getClass());
                    }
                }
            }.start();
        }
    }

    /**
     * Unloads all of the avatars associated with the server session.
     */
    public void unload() {
        // Find all of the classes that are annotated with @AvatarFactory and
        // tell them to unload.
        ScannedClassLoader scl = manager.getClassloader();
        Iterator<AvatarFactorySPI> it = scl.getAll(AvatarFactory.class, AvatarFactorySPI.class);
        while (it.hasNext() == true) {
            it.next().unregisterAvatars(manager);
        }

        // Finally put the state back to INIT
        synchronized (currentState) {
            currentState = State.INIT;
        }

        // XXX
        // There probably should be more to do here. We need to consider what
        // happens when an unload() happens during a load()
        // XXX
    }

    /**
     * Returns the content collection on the server that represents the base
     * collection for all avatar configuration information.
     *
     * @return A ContentCollection root for all server avatar config info
     */
    public static synchronized ContentCollection getBaseServerCollection(ServerSessionManager manager)
            throws ContentRepositoryException {

        ContentRepositoryRegistry reg = ContentRepositoryRegistry.getInstance();
        ContentRepository repository = reg.getRepository(manager);
        ContentCollection userDir = repository.getUserRoot(true);
        if (userDir == null) {
            logger.warning("Unable to find user content directory");
            throw new ContentRepositoryException("Unable to find user dir");
        }

        // Fetch the avatars/imi directory, creating each if necessary
        ContentCollection dir = (ContentCollection) userDir.getChild("avatars");
        if (dir == null) {
            dir = (ContentCollection) userDir.createChild("avatars", Type.COLLECTION);
        }
        return dir;
    }

    /**
     * Adds the given listener to the set of listeners. If the listener is
     * already present, a duplicate is not added. When a new listener is added,
     * an event is immediately sent to the listener giving the current state
     * of the loader.
     *
     * @param l The listener to add
     */
    public void addAvatarLoaderStateListener(AvatarLoaderStateListener l) {
        synchronized (listenerSet) {
            listenerSet.add(l);
            l.stateChanged(currentState);
        }
    }
    
    /**
     * Removes the given listener from the set of listeners. If the listener
     * is not present, this method does nothing.
     *
     * @param l The listener to remove
     */
    public void removeAvatarLoaderStateListener(AvatarLoaderStateListener l) {
        synchronized (listenerSet) {
            listenerSet.remove(l);
        }
    }

    /**
     * Fires an event to all listeners given the current state
     */
    private void fireAvatarLoaderStateEvent(State state) {
        synchronized (listenerSet) {
            for (AvatarLoaderStateListener l : listenerSet) {
                l.stateChanged(state);
            }
        }
    }

    /**
     * Listener interface for when the state of this loader changes
     */
    public interface AvatarLoaderStateListener {
        /**
         * Indicates that the loader has entered the given state.
         *
         * @param state The new state of the session loader
         */
        public void stateChanged(State state);
    }
}
