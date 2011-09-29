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
package org.jdesktop.wonderland.modules.xappsconfig.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionLifecycleListener;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.xappsconfig.client.XAppsClientConfigConnection.XAppsConfigListener;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppRegistryItem;

/**
 * Client-size plugin for registering items in the Cell Registry that come from
 * the configured list of X Apps.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class XAppRegistryPlugin extends BaseClientPlugin 
        implements SessionLifecycleListener, SessionStatusListener {

    private static Logger logger = Logger.getLogger(XAppRegistryPlugin.class.getName());
    private XAppsClientConfigConnection xappsConfigConnection = null;
    private X11AppConfigListener listener = null;

    // The set of Cell factories that this plugin has currently registered
    private Set<CellFactorySPI> factorySet = new HashSet();

    /**
     * @inheritDoc()
     */
    @Override
    public void initialize(ServerSessionManager session) {
        // Create a new base connection to use for X11 Config updates and
        // registry for notifications in changes to the current session.
        xappsConfigConnection = new XAppsClientConfigConnection();
        session.addLifecycleListener(this);

        super.initialize(session);
    }

    @Override
    public void cleanup() {
        super.getSessionManager().removeLifecycleListener(this);
        super.cleanup();
    }

    /**
     * @inheritDoc()
     */
    @Override
    protected void activate() {
        CellRegistry registry = CellRegistry.getCellRegistry();

        // Fetch the list of X Apps registered for the system and register them
        // with the Cell Registry. We synchronize over the set of factories
        // in case one is added/removed via the configuration channel at the
        // same time.
        synchronized (factorySet) {
            List<XAppRegistryItem> systemItems =
                    XAppRegistryItemUtils.getSystemXAppRegistryItemList();
            for (XAppRegistryItem item : systemItems) {
                String appName = item.getAppName();
                String command = item.getCommand();
                XAppCellFactory factory = new XAppCellFactory(appName, command);
                registry.registerCellFactory(factory);
                factorySet.add(factory);
            }
        }
    }

    /**
     * @inheritDoc()
     */
    @Override
    protected void deactivate() {
        // Remove the set of cell factories that this plugin has registered
        // on the Cell Registry. We synchronize over the set of factories
        // in case one is added/removed via the configuration channel at the
        // same time.
        synchronized (factorySet) {
            CellRegistry registry = CellRegistry.getCellRegistry();
            for (CellFactorySPI factory : factorySet) {
                registry.unregisterCellFactory(factory);
            }
            factorySet.clear();
        }
    }

    /**
     * @inheritDoc()
     */
    public void sessionCreated(WonderlandSession session) {
        // Do nothing.
    }

    /**
     * @inheritDoc()
     */
    public void primarySession(WonderlandSession session) {
        if (session != null) {
            session.addSessionStatusListener(this);
            if (session.getStatus() == WonderlandSession.Status.CONNECTED) {
                connectClient(session);
            }
        }
    }

    /**
     * @inheritDoc()
     */
    public void sessionStatusChanged(WonderlandSession session, Status status) {
        switch (status) {
            case CONNECTED:
                connectClient(session);
                return;

            case DISCONNECTED:
                disconnectClient();
                return;
        }
    }

    /**
     * Connect the client.
     */
    private void connectClient(WonderlandSession session) {
        try {
            listener = new X11AppConfigListener();
            xappsConfigConnection.addX11AppConfigListener(listener);
            xappsConfigConnection.connect(session);
        } catch (ConnectionFailureException e) {
            logger.log(Level.WARNING, "Connect client error", e);
        }
    }

    /**
     * Disconnect the client
     */
    private void disconnectClient() {
        xappsConfigConnection.disconnect();
        xappsConfigConnection.removeX11AppConfigListener(listener);
        listener = null;
    }
    
    /**
     * Listens for when X11 Apps are added or removed.
     */
    private class X11AppConfigListener implements XAppsConfigListener {

        public void xappAdded(String appName, String command) {
            // Add the new factory to the registry.  We synchronize over the
            // set of factories in case the primary connection changes.
            synchronized (factorySet) {
                CellRegistry registry = CellRegistry.getCellRegistry();
                XAppCellFactory factory = new XAppCellFactory(appName, command);
                registry.registerCellFactory(factory);
                factorySet.add(factory);
            }
        }

        public void xappRemoved(String appName) {
            // Remove the X11 App from the Cell registry. We only need the app
            // name to create a suitable XAppCellFactory to remove. We
            // synchronize over the set of factories in case the primary
            // connection changes.
            synchronized (factorySet) {
                CellRegistry registry = CellRegistry.getCellRegistry();
                XAppCellFactory factory = new XAppCellFactory(appName, null);
                registry.unregisterCellFactory(factory);
                factorySet.remove(factory);
            }
        }
    }
}
