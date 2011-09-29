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
package org.jdesktop.wonderland.modules.placemarks.client;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.client.jme.MainFrame.PlacemarkType;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionLifecycleListener;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.placemarks.client.PlacemarkClientConfigConnection.PlacemarkConfigListener;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistry;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistry.PlacemarkListener;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistryFactory;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;

/**
 * Client-size plugin for registering items in the Cell Registry that come from
 * the configured list of X Apps.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@Plugin
public class PlacemarkPlugin extends BaseClientPlugin
        implements SessionLifecycleListener, SessionStatusListener {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/placemarks/client/resources/Bundle");
    private static final Logger LOGGER =
            Logger.getLogger(PlacemarkPlugin.class.getName());
    private WeakReference<EditPlacemarksJFrame> managePlacemarksFrameRef = null;
    private JMenuItem manageMI = null;
    private JMenuItem addMI = null;
    private JMenuItem startingLocationMI = null;
    private PlacemarkListener listener = null;
    private PlacemarkClientConfigConnection placemarksConfigConnection = null;
    private PlacemarkConfigListener configListener = null;
    // A Map of Placemarks to their menu items for system-wide and user
    // placemarks
    private Map<Placemark, JMenuItem> systemPlacemarkMenuItems = new HashMap();
    private Map<Placemark, JMenuItem> userPlacemarkMenuItems = new HashMap();

    /**
     * @inheritDoc()
     */
    @Override
    public void initialize(final ServerSessionManager sessionManager) {
        // We will listen for changes to the list of registered Placemarks and
        // edit the main menu as a result.
        listener = new PlacemarkMenuListener();

        // Create a new base connection to use for Placemark Config updates and
        // registry for notifications in changes to the current session.
        placemarksConfigConnection = new PlacemarkClientConfigConnection();
        sessionManager.addLifecycleListener(this);

        // Create the "Manage Placemarks..." menu item. The menu will be added
        // when our server intiializes.
        manageMI = new JMenuItem(BUNDLE.getString("Manage_Placemarks..."));
        manageMI.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                EditPlacemarksJFrame managePlacemarksFrame;
                if (managePlacemarksFrameRef == null ||
                        managePlacemarksFrameRef.get() == null) {
                    JFrame frame = JmeClientMain.getFrame().getFrame();
                    managePlacemarksFrame = new EditPlacemarksJFrame();
                    managePlacemarksFrame.setLocationRelativeTo(frame);
                    managePlacemarksFrameRef =
                            new WeakReference(managePlacemarksFrame);
                } else {
                    managePlacemarksFrame = managePlacemarksFrameRef.get();
                }

                if (!managePlacemarksFrame.isVisible()) {
                    managePlacemarksFrame.setVisible(true);
                }
            }
        });

        // Create the "Add Placemark..." menu item. This menu will be added when
        // our server initializes.
        addMI = new JMenuItem(BUNDLE.getString("Add_Placemark..."));
        addMI.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFrame frame = JmeClientMain.getFrame().getFrame();

                // Form a placemark with the current position about the avatar's
                // location.
                Placemark placemark = getCurrentPlacemark(sessionManager);

                // Fetch the list of known USER Placemark names
                PlacemarkRegistry registry =
                        PlacemarkRegistryFactory.getInstance();
                Set<Placemark> placemarkSet =
                        registry.getAllPlacemarks(PlacemarkType.USER);

                // Display a dialog with the values in the Placemark. And if we
                // wish to update the values, then re-add the placemark.
                // (Re-adding the placemark should have the effect of updating
                // its values.
                AddEditPlacemarkJDialog dialog = new AddEditPlacemarkJDialog(
                        frame, true, placemark, placemarkSet);
                dialog.setTitle(BUNDLE.getString("Add_Placemark"));
                dialog.setLocationRelativeTo(frame);
                dialog.pack();
                dialog.setVisible(true);

                if (dialog.getReturnStatus() ==
                        AddEditPlacemarkJDialog.RET_OK) {
                    // Create a new placemark with the new information.
                    String name = dialog.getPlacemarkName();
                    String url = dialog.getServerURL();
                    float x = dialog.getLocationX();
                    float y = dialog.getLocationY();
                    float z = dialog.getLocationZ();
                    float angle = dialog.getLookAtAngle();
                    Placemark newPlacemark = new Placemark(name, url, x, y, z, angle);

                    try {
                        PlacemarkUtils.addUserPlacemark(newPlacemark);
                    } catch (Exception excp) {
                        LOGGER.log(Level.WARNING, "Unable to add " + name +
                                " to user's placemarks", excp);
                        return;
                    }

                    // Tell the client-side registry of placemarks that a new
                    // one has been added
                    registry.registerPlacemark(newPlacemark, PlacemarkType.USER);
                }
            }
        });

        // Menu item to take avatar to starting location
        startingLocationMI = new JMenuItem(BUNDLE.getString("Starting_Location"));
        startingLocationMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector3f position = new Vector3f();
                Quaternion look = new Quaternion();
                try {
                    ClientContextJME.getClientMain().gotoLocation(null, position, look);
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Failed to go to starting " +
                            "location.", ex);
                }
            }
        });

        super.initialize(sessionManager);
    }

    /**
     * @inheritDoc()
     */
    @Override
    public void cleanup() {
        getSessionManager().removeLifecycleListener(this);
        super.cleanup();
    }

    /**
     * @inheritDoc()
     */
    @Override
    protected void activate() {

        // Listen for changes to the list of Placemarks. This needs to come
        // first before we actually add the Placemarks from WebDav
        PlacemarkRegistry registry = PlacemarkRegistryFactory.getInstance();
        registry.addPlacemarkRegistryListener(listener);

        // Fetch the list of user placemarks from WebDav and register them
        for (Placemark placemark : PlacemarkUtils.getUserPlacemarkList().getPlacemarksAsList()) {
            registry.registerPlacemark(placemark, PlacemarkType.USER);
        }

        // Add the MANAGEMENT related Placemarks to the main frame
        JmeClientMain.getFrame().addToPlacemarksMenu(startingLocationMI, 0, MainFrame.PlacemarkType.MANAGEMENT);
        JmeClientMain.getFrame().addToPlacemarksMenu(addMI, -1, MainFrame.PlacemarkType.MANAGEMENT);
        JmeClientMain.getFrame().addToPlacemarksMenu(manageMI, -1, MainFrame.PlacemarkType.MANAGEMENT);
    }

    /**
     * @inheritDoc()
     */
    @Override
    protected void deactivate() {
        // Remove the "Manage Placemarks..." from the main frame
        JmeClientMain.getFrame().removeFromPlacemarksMenu(addMI);
        JmeClientMain.getFrame().removeFromPlacemarksMenu(manageMI);
        JmeClientMain.getFrame().removeFromPlacemarksMenu(startingLocationMI);
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
            configListener = new ClientPlacemarkConfigListener();
            placemarksConfigConnection.addPlacemarkConfigListener(configListener);
            placemarksConfigConnection.connect(session);
        } catch (ConnectionFailureException e) {
            LOGGER.log(Level.WARNING, "Connect client error", e);
        }
    }

    /**
     * Disconnect the client
     */
    private void disconnectClient() {
        placemarksConfigConnection.disconnect();
        placemarksConfigConnection.removePlacemarkConfigListener(configListener);
        configListener = null;
    }

    /**
     * Returns a Placemark that represents the current position of the avatar.
     */
    private Placemark getCurrentPlacemark(ServerSessionManager sessionManager) {
        // Fetch the current translation of the avatar and the (x, y, z) of its
        // position
        ViewManager manager = ViewManager.getViewManager();
        ViewCell viewCell = manager.getPrimaryViewCell();
        CellTransform viewTransform = viewCell.getWorldTransform();
        Vector3f location = viewTransform.getTranslation(null);
        float x = location.x;
        float y = location.y;
        float z = location.z;

        // Find out what the URL to the server is
        String url = sessionManager.getServerURL();

        // Compute the current look angle, as degrees from the +x axis, ignoring
        // any look in the y-axis.
        Quaternion viewRotation = viewTransform.getRotation(null);

        Vector3f v1 = new Vector3f(0, 0, 1);
        Vector3f normal = new Vector3f(0, 1, 0);
        Vector3f v2 = viewRotation.mult(v1);
        v2.normalizeLocal();

        // Compute the signed angle between v1 and v2. We do this with the
        // following formula: angle = atan2(normal dot (v1 cross v2), v1 dot v2)
        float dotProduct = v1.dot(v2);
        Vector3f crossProduct = v1.cross(v2);
        float lookAngle = (float) Math.atan2(normal.dot(crossProduct), dotProduct);
        lookAngle = (float) Math.toDegrees(lookAngle);

        return new Placemark("", url, x, y, z, lookAngle);
    }

    /**
     * Listens for when Placemarks are added or removed.
     */
    private class ClientPlacemarkConfigListener implements PlacemarkConfigListener {

        public void placemarkAdded(Placemark placemark) {
            PlacemarkRegistry registry =
                    PlacemarkRegistryFactory.getInstance();
            registry.registerPlacemark(placemark, PlacemarkType.SYSTEM);
        }

        public void placemarkRemoved(Placemark placemark) {
            PlacemarkRegistry registry =
                    PlacemarkRegistryFactory.getInstance();
            registry.unregisterPlacemark(placemark, PlacemarkType.SYSTEM);
        }
    }

    /**
     * Action listener for Placemark menu items.
     */
    private class PlacemarkActionListener implements ActionListener {

        private Placemark placemark = null;

        /** Constructor, takes the placemark associated with the menu item */
        public PlacemarkActionListener(Placemark placemark) {
            this.placemark = placemark;
        }

        public void actionPerformed(ActionEvent e) {
            String url = placemark.getUrl();
            float x = placemark.getX();
            float y = placemark.getY();
            float z = placemark.getZ();
            float angle = placemark.getAngle();
            Vector3f location = new Vector3f(x, y, z);

            // create the rotation
            Quaternion look = new Quaternion();
            Vector3f axis = new Vector3f(Vector3f.UNIT_Y);
            look.fromAngleAxis((float) Math.toRadians(angle), axis);

            // If the URL is an empty string, convert it to null. If the URL
            // is null, then it will use the current server.
            if (url != null && url.length() == 0) {
                url = null;
            }

            try {
                ClientContextJME.getClientMain().gotoLocation(url, location, look);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Listens for changes to the list of Placemarks and updates the menu
     * system accordingly.
     */
    private class PlacemarkMenuListener implements PlacemarkListener {

        public void placemarkAdded(Placemark placemark, PlacemarkType type) {
            // First try to find any existing placemark with the same name. If
            // there is one, then remove the JMenuItem from the menu. Note that
            // we can use the given Placemark as a key in the Map, because the
            // Placemark.equals() methods only evaluates the placemark name.
            placemarkRemoved(placemark, type);

            // Now add the new JMenuItem to either the system or user position
            // in the Placemarks menu
            JMenuItem menuItem = new JMenuItem(placemark.getName());
            PlacemarkActionListener listner =
                    new PlacemarkActionListener(placemark);
            menuItem.addActionListener(listner);
            if (type == PlacemarkType.USER) {
                userPlacemarkMenuItems.put(placemark, menuItem);
                JmeClientMain.getFrame().addToPlacemarksMenu(menuItem, 2, MainFrame.PlacemarkType.USER);
            } else {
                systemPlacemarkMenuItems.put(placemark, menuItem);
                JmeClientMain.getFrame().addToPlacemarksMenu(menuItem, 1, MainFrame.PlacemarkType.SYSTEM);
            }
        }

        public void placemarkRemoved(Placemark placemark, PlacemarkType type) {
            // Try to find the existing placemark and remove it from the menu
            Map<Placemark, JMenuItem> map =
                    (type == PlacemarkType.USER) ? userPlacemarkMenuItems
                    : systemPlacemarkMenuItems;

            JMenuItem menuItem = map.get(placemark);
            if (menuItem != null) {
                map.remove(placemark);
                JmeClientMain.getFrame().removeFromPlacemarksMenu(menuItem);
            }
        }
    }
}
