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
package org.jdesktop.wonderland.modules.avatarbase.client.registry;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;

/**
 * The avatar registry manages the set of all avatars configured in the system.
 * Each avatar is represented by a class that implements the AvatarSPI
 * interface. Each avatar must have a unique name in the system.
 * <p>
 * The registry also supports the notion of a "default" avatar, which is used
 * by default (when no other is selected) and should work across all systems
 * regardless of graphics capabilities.
 * <p>
 * This class supports a listener interface to notify when avatars have been
 * added/removed from the system.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AvatarRegistry {

    private static final Logger LOGGER =
            Logger.getLogger(AvatarRegistry.class.getName());

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle");

    // A map and set of all avatars, including the "default" avatar
    private Map<String, AvatarSPI> avatarMap = null;
    private Set<AvatarSPI> avatarSet = null;

    // The special "default" avatar, null if none
    private AvatarSPI defaultAvatar = null;

    // The set of configuration settings for the avatar system (e.g. the current
    // avatar in use).
    private AvatarConfigSettings settings = null;

    // Returns the base directory (content collection) that stores all avatar
    // related configuration information localy
    private ContentCollection avatarCollection = null;

    // A list of listeners for changes to the avatars and the current avatar
    // in use
    private Set<AvatarListener> listenerSet = new HashSet();
    private Set<AvatarInUseListener> inUseListenerSet = new HashSet();

    /** Default constructor */
    public AvatarRegistry() {
        avatarMap = new HashMap();
        avatarSet = new HashSet();

        // Fetch the local content repository to store all avatar-related
        // configuration information.
        // XXX This should really be re-done for every primary session because
        // the login name can change.
        ContentCollection localContent = ContentRepositoryRegistry.getInstance().getLocalRepository();
        try {
            avatarCollection = (ContentCollection) localContent.getChild("avatars");
            if (avatarCollection == null) {
                avatarCollection = (ContentCollection) localContent.createChild("avatars", Type.COLLECTION);
            }
        } catch (ContentRepositoryException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        // Loads the configuration settings from the user's local repository.
        // Upon error, log it and initialize an empty set of settings.
        try {
            settings = AvatarConfigUtils.loadConfigSettings(avatarCollection);
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.INFO, "Unable to load avatar settings", excp);
            settings = new AvatarConfigSettings();
        }
    }

    /**
     * Singleton to hold instance of AvatarRegistry. This holder class is loaded
     * on the first execution of AvatarRegistry.getAvatarRegistry().
     */
    private static class AvatarRegistryHolder {
        private final static AvatarRegistry registry = new AvatarRegistry();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final AvatarRegistry getAvatarRegistry() {
        return AvatarRegistryHolder.registry;
    }

    /**
     * Returns the base content collection to use for all avatar configuration
     * information in the user's local repository.
     *
     * @return A ContentCollection for all avatar configuration data.
     */
    public ContentCollection getAvatarCollection() {
        return avatarCollection;
    }

    /**
     * Returns the "default" avatar, or null if there is none.
     *
     * @return The default avatar or null
     */
    public AvatarSPI getDefaultAvatar() {
        return defaultAvatar;
    }

    /**
     * Registers a given avatar. If this should become the special "default"
     * avatar, then 'isDefault' should be true.
     *
     * @param avatar The new avatar
     * @param isDefault True if this avatar should become the default
     */
    public void registerAvatar(AvatarSPI avatar, boolean isDefault) {
        // Add the avatar to the set/map and then notify listeners
        synchronized (this) {
            avatarMap.put(avatar.getName(), avatar);
            avatarSet.add(avatar);
            if (isDefault == true) {
                defaultAvatar = avatar;
            }
            fireAvatarAddedListener(avatar);
        }
    }

    /**
     * Removes a given avatar.
     *
     * @param avatar The avatar to remove
     */
    public void unregisterAvatar(AvatarSPI avatar) {
        // Remove the avatar from the set/map and then notify listeners
        synchronized (this) {
            avatarMap.remove(avatar.getName());
            avatarSet.remove(avatar);
            fireAvatarRemovedListener(avatar);

            // If this avatar is the default avatar, then reset the default to
            // null
            if (defaultAvatar == avatar) {
                defaultAvatar = null;
            }
        }
    }

    /**
     * Returns a set of all avatars. If no avatars are registered, returns an
     * empty set.
     *
     * @return A set of registered avatars
     */
    public Set<AvatarSPI> getAllAvatars() {
        synchronized (this) {
            return new HashSet(avatarSet);
        }
    }

    /**
     * Returns an avatar by name, null if an avatar does not exist.
     *
     * @param avatarName The name of the avatar
     * @return The AvatarSPI or null
     */
    public AvatarSPI getAvatarByName(String avatarName) {
        return avatarMap.get(avatarName);
    }

    /**
     * Sets the avatar currently in use. This always sets the avatar, even if
     * it is the same one in use. The 'isLocal' flag indicates whether the
     * avatar should only be updated locally or for all clients.
     *
     * @param avatar The avatar configuration to use
     * @param isLocal True if the avatar update should only happen locally
     */
    public void setAvatarInUse(AvatarSPI avatar, boolean isLocal) {
        settings.setAvatarNameInUse(avatar.getName());

        // Write out the avatar currently in use to the configuration file.        
        try {
            AvatarConfigUtils.saveConfigSettings(avatarCollection, settings);
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING, "Unable to store avatar settings", excp);
        }

        // Inform the listeners that there is a new avatar in-use
        fireAvatarInUseListener(avatar, isLocal);
    }

    /**
     * Returns true if the given avatar name is the one in-use, false if not.
     *
     * @param avatarName The name of the avatar
     * @return True if the avatar name is currently in use, false if not
     */
    public boolean isAvatarInUse(String avatarName) {
        return avatarName.equals(settings.getAvatarNameInUse()) == true;
    }
    
    /**
     * Returns the current avatar in use, or null if there is none.
     *
     * @return The current avatar in use (or null)
     */
    public AvatarSPI getAvatarInUse() {
        // We need to fetch the AvatarSPI for the given name that is currently
        // in use.
        String name = settings.getAvatarNameInUse();
        if (name == null) {
            return null;
        }
        return avatarMap.get(name);
    }

    /**
     * Returns a unique name that is not used by any other avatar.
     *
     * @return A new, unique avatar name
     */
    public String getUniqueAvatarName() {
        // Simply loop through and find the first name that isn't take. We
        // assume this should not take a long amount of time (or loop forever).
        // We synchronize because we access the Map of avatars
        synchronized (this) {
            int i = 1;
            while (true) {
                String avatarName = BUNDLE.getString("NewAvatar");
                avatarName = MessageFormat.format(avatarName, i);
                if (avatarMap.containsKey(avatarName) == false) {
                    return avatarName;
                }
                i++;
            }
        }
    }

    /**
     * Adds a new listener for changes to the avatar registry. If this listener
     * is already present, this method does nothing.
     *
     * @param listener The new listener to add
     */
    public void addAvatarListener(AvatarListener listener) {
        synchronized (listenerSet) {
            listenerSet.add(listener);
        }
    }

    /**
     * Removes a listener for changes to the avatar registry. If this listener
     * is not present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeAvatarListener(AvatarListener listener) {
        synchronized (listenerSet) {
            listenerSet.remove(listener);
        }
    }

    /**
     * Adds a new listener for changes to the avatar in use. If this listener
     * is already present, this method does nothing.
     *
     * @param listener The new listener to add
     */
    public void addAvatarInUseListener(AvatarInUseListener listener) {
        synchronized (inUseListenerSet) {
            inUseListenerSet.add(listener);
        }
    }

    /**
     * Removes a listener for changes to the avatar in use. If this listener
     * is not present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeAvatarInUseListener(AvatarInUseListener listener) {
        synchronized (inUseListenerSet) {
            inUseListenerSet.remove(listener);
        }
    }

    /**
     * Sends an event to all registered listeners that an avatar has been
     * added.
     */
    private void fireAvatarAddedListener(AvatarSPI avatar) {
        synchronized (listenerSet) {
            for (AvatarListener listener : listenerSet) {
                listener.avatarAdded(avatar);
            }
        }
    }

    /**
     * Sends an event to all registered listeners that an avatar has been
     * removed.
     */
    private void fireAvatarRemovedListener(AvatarSPI avatar) {
        synchronized (listenerSet) {
            for (AvatarListener listener : listenerSet) {
                listener.avatarRemoved(avatar);
            }
        }
    }

    /**
     * Sends an event to all registered listeners that an avatar is in use.
     */
    private void fireAvatarInUseListener(AvatarSPI avatar, boolean isLocal) {
        synchronized (inUseListenerSet) {
            for (AvatarInUseListener listener : inUseListenerSet) {
                listener.avatarInUse(avatar, isLocal);
            }
        }
    }

    /**
     * A listener indicating that a change has happened to the set of registered
     * avatars.
     */
    public interface AvatarListener {
        /**
         * An avatar has been added.
         *
         * @param avatar The avatar added
         */
        public void avatarAdded(AvatarSPI avatar);

        /**
         * A avatar has been removed.
         *
         * @param avatar The avatar removed
         */
        public void avatarRemoved(AvatarSPI avatar);
    }

    /**
     * A listener indicates that a change in the avatar currently in use
     */
    public interface AvatarInUseListener {
        /**
         * Indicates that a new avatar is in use. The 'isLocal' parameter tells
         * whether the change should only be uplodated locally.
         *
         * @param avatar The avatar in use
         * @param isLocal True if the avatar update should only happen locally
         */
        public void avatarInUse(AvatarSPI avatar, boolean isLocal);
    }
}
