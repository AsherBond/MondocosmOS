/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.cell;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.ChannelComponent.ComponentMessageReceiver;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.cell.messages.CellClientComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentUtils;

/**
 * A listener on a cell's channel to handle updates to its state sent from the
 * server.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellComponentMessageReceiver implements ComponentMessageReceiver {

    private static Logger logger = Logger.getLogger(CellComponentMessageReceiver.class.getName());
    private Cell cell = null;

    public CellComponentMessageReceiver(Cell cell) {
        this.cell = cell;
    }

    public void messageReceived(CellMessage message) {
        CellClientComponentMessage cccm = (CellClientComponentMessage)message;
        switch (cccm.getComponentAction()) {
            case ADD:
                addComponent(cccm);
                break;

            case REMOVE:
                removeComponent(cccm);
                break;
        }
    }

    /**
     * Handles when an "add" component message has been received by the client.
     * Checks to see if the component exists, and creates it if necessary.
     */
    private void addComponent(CellClientComponentMessage message) {
        // Fetch the client component class name and client state. If there is
        // no client-side class name, then there is no client-side component
        // class so we just return.
        CellComponentClientState clientState = message.getCellComponentClientState();
        String compClassname = message.getCellComponentClientClassName();
        if (compClassname == null) {
            return;
        }
        
        try {
            // find the classloader associated with the server session
            // manager that loaded this cell.  That classloader will
            // have all the module classes
            WonderlandSession session = cell.getCellCache().getSession();
            ServerSessionManager ssm = session.getSessionManager();
            ClassLoader cl = ssm.getClassloader();

            // us the classloader we found to load the component class
            Class compClazz = cl.loadClass(compClassname);

            // Find out the Class used to lookup the component in the list
            // of components
            Class lookupClazz = CellComponentUtils.getLookupClass(compClazz);

            // Attempt to fetch the component using the lookup class. If
            // it does not exist, then create and add the component.
            // Otherwise, just set the client-side of the component
            CellComponent component = cell.getComponent(lookupClazz);
            if (component == null) {
                // Create a new cell component based upon the class name,
                // set its state, and add it to the list of components
                Constructor<CellComponent> constructor = compClazz.getConstructor(Cell.class);
                component = constructor.newInstance(cell);
                component.setClientState(clientState);
                cell.addComponent(component, CellComponentUtils.getLookupClass(component.getClass()));
            }
            else {
                component.setClientState(clientState);
            }
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Instantiation exception for class " + compClassname + "  in cell " + getClass().getName(), ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Can't find component class " + compClassname, ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles when a "remove" component message has been received by the
     * client. Checks to see if the component class exists, and removes it
     */
    private void removeComponent(CellClientComponentMessage message) {
        // Fetch the client component class name. If there is no client-side
        // class name, then there is no client-side component class so we just
        // return. This case should never happen, but we check it anyway.
        String className = message.getCellComponentClientClassName();
        if (className == null) {
            return;
        }

        // find the classloader associated with the server session
        // manager that loaded this cell.  That classloader will
        // have all the module classes
        WonderlandSession session = cell.getCellCache().getSession();
        ServerSessionManager ssm = session.getSessionManager();
        ClassLoader cl = ssm.getClassloader();

        // Find the class associated with the client component and remove it
        try {
            // us the classloader we found to load the component class
            Class compClazz = cl.loadClass(className);
            Class clazz = CellComponentUtils.getLookupClass(compClazz);
            cell.removeComponent(clazz);
        } catch (java.lang.ClassNotFoundException excp) {
            logger.log(Level.WARNING, "Cannot remove component", excp);
        }
    }
}
