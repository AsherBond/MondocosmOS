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
package org.jdesktop.wonderland.modules.xremwin.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.util.ScalableHashMap;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.jdesktop.wonderland.server.UserListener;
import org.jdesktop.wonderland.server.UserMO;
import org.jdesktop.wonderland.server.UserManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * Manage shared secrets for Xrw client authentication
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class XrwSecretManager {
    private static final Logger logger =
            Logger.getLogger(XrwSecretManager.class.getName());

    /** name to of the binding in the data manager */
    private static final String BINDING_NAME = XrwSecretManager.class.getName();

    /**
     * Constructor is protected.  Use getInstance() instead.
     */
    protected XrwSecretManager() {}

    /**
     * Get an instance of the secret manager.  The manager is stateless,
     * so this just returns a new instance.
     */
    public static XrwSecretManager getInstance() {
        return new XrwSecretManager();
    }

    /**
     * Get the secret for a given client id.  This will generate a new
     * secret if the old one doesn't exist.
     * @param a clientID to get a secret for
     * @return a secret associated with that id
     */
    public SecretKey getSecret(WonderlandClientID clientID) {
        Map<WonderlandClientID, SecretKey> keyMap = getKeys();

        SecretKey out = keyMap.get(clientID);
        if (out == null) {
            out = generateSecret(clientID);
            keyMap.put(clientID, out);
        }

        return out;
    }

    /**
     * Remove the stored secret for the given ID.
     * @param clientID the clientID to remove
     */
    protected void removeSecret(WonderlandClientID clientID) {
        getKeys().remove(clientID);
    }

    /**
     * Get the map that actually stores secrets.  Create it if it doesn't
     * exist.
     * @return the secret map.
     */
    protected Map<WonderlandClientID, SecretKey> getKeys() {
        Map<WonderlandClientID, SecretKey> out;

        try {
            out = (Map<WonderlandClientID, SecretKey>)
                    AppContext.getDataManager().getBinding(BINDING_NAME);
        } catch (NameNotBoundException nnbe) {
            out = new ScalableHashMap<WonderlandClientID, SecretKey>();
            AppContext.getDataManager().setBinding(BINDING_NAME, out);
        }

        return out;
    }

    /**
     * Generate a secret key for the given user.
     * @param clientID the id of that client
     */
    protected SecretKey generateSecret(WonderlandClientID clientID) { 
        SecretKey out;

        // generate the key
        try {
            KeyGenerator generator = KeyGenerator.getInstance("HmacSHA1");
            out = generator.generateKey();
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        // add a listener that will remove the key when this client logs out
        UserMO user = UserManager.getUserManager().getUser(clientID);
        user.addUserListener(new RemoveSecretListener(clientID));
        return out;
    }

    /**
     * A listener to remove a user's secret when they log out
     */
    private static class RemoveSecretListener
            implements UserListener, Serializable
    {
        private WonderlandClientID clientID;

        public RemoveSecretListener(WonderlandClientID clientID) {
            this.clientID = clientID;
        }

        public void userLoggedIn(WonderlandClientID clientID,
                                 ManagedReference<UserMO> userRef)
        {
            // ignore
        }

        public void userLoggedOut(WonderlandClientID clientID,
                                  ManagedReference<UserMO> userRef,
                                  ManagedReference<Queue<Task>> logoutTasksRef)
        {
            if (this.clientID.equals(clientID)) {
                XrwSecretManager.getInstance().removeSecret(clientID);
            }
        }
    }
}
