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
package org.jdesktop.wonderland.client;

import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.PrimaryServerListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 * A default implementation of client plugin that listens for the primary
 * server.
 * <p>
 * In addition to the normal <code>intialize()</code> and
 * <code>cleanup()</code> methods, this base class provides two other methods,
 * <code>activate()</code> and <code>deactivate()</code>. These methods are
 * called when the <code>ServerSessionManager</code> associated with this
 * plugin becomes the primary session manager. This is used for federation,
 * to properly make changes to shared resources as the user moves from server
 * to server.
 * <p>
 * The <code>activate()</code> and <code>deactivate()</code> methods should be
 * used to make any changes to the core of Wonderland, such as adding menus or
 * data flavor listeners.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public abstract class BaseClientPlugin implements ClientPlugin, PrimaryServerListener {
    /** the session manager associated with this plugin */
    private ServerSessionManager sessionManager;

    /**
     * whether or not our session manager is currently the primary session
     * manager
     */
    private boolean primary = false;

    /**
     * Initialize the client plugin.  This implementation registers as
     * a <code>PrimaryServerListener</code> to track changes for the activate
     * and deactivate methods.
     * <p>
     * Subclasses overriding this method should be aware that the
     * <code>activate()</code> method will be called immediately from this
     * method.  If there is any setup needed before the <code>activate()</code>
     * method is called, the call to <code>super.intialize()</code> should
     * happen after that.
     *
     * @param sessionManager the ServerSessionManager this plugin is associated
     * with
     */
    public void initialize(ServerSessionManager sessionManager) {
        this.sessionManager = sessionManager;

        // activate ourselves as a primary server listener.  This will
        // immediately notify us with the current primary server.
        LoginManager.addPrimaryServerListener(this);
    }

    /**
     * Cleans up the registrations made in initialize
     */
    public void cleanup() {
        // deactivate as a primary server listener.
        LoginManager.removePrimaryServerListener(this);
    }

    /**
     * Notification that the primary server has changed.
     * @param sessionManager the new primary session manager
     */
    public void primaryServer(ServerSessionManager sessionManager) {
        // test whether the session associated with this plugin
        // is the new primary server. Make sure to handle null
        // session managers being passed in.
        if (getSessionManager() == sessionManager) {
            // ours is primary -- call activate if this is a change
            if (!isPrimary()) {
                setPrimary(true);
                activate();
            }
        } else {
            // ours is no longer primary -- call deactivate if this is a change
            if (isPrimary()) {
                setPrimary(false);
                deactivate();
            }
        }
    }

    /**
     * Override this method to modify any of Wonderland core when this
     * module's session becomes the primary session.  For example, this can
     * be used to add menus to the main window or to activate other types
     * of listeners with the system.
     */
    protected void activate() {
        // default does nothing
    }

    /**
     * Override this method to undo any changes made in the
     * <code>activate()</code> method.  This method is guaranteed to only
     * be called after <code>activate()</code> has been called.
     * <p>
     * Note that other listener's <code>activate()</code> method may be called
     * before deactivate, so clients should check when making changes to
     * shared resources to verify that their value is actually the current
     * one.
     */
    protected void deactivate() {
        // default does nothing
    }

    /**
     * Get the session manager associated with this plugin.
     * @return the session manager associated with this plugin
     */
    protected ServerSessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Determine if the session mananager associated with this plugin is
     * the primary session manager.
     * @return true if the session manager associated with this plugin is
     * the primary session manager
     */
    protected synchronized boolean isPrimary() {
        return primary;
    }

    /**
     * Set the fact that our session manager is now the primary.
     * @param primary whether or not the session manager is primary
     */
    private synchronized void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
