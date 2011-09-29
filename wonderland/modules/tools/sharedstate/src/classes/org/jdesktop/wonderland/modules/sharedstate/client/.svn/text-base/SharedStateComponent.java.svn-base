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
package org.jdesktop.wonderland.modules.sharedstate.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent.ComponentMessageReceiver;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.ChangeValueMessage;

@ExperimentalAPI
public class SharedStateComponent extends CellComponent {
    private static final Logger logger =
            Logger.getLogger(ComponentMessageReceiver.class.getName());

    /** 
     * the channel to connect to -- not valud until the cell's status
     * is set to ACTIVE. 
     */
    @UsesCellComponent
    private ChannelComponent channel;

    /** the set of maps we know about, indexed by map name */
    private Map<String, SharedMapImpl> maps =
            new LinkedHashMap<String, SharedMapImpl>();

    /** the message receiver, or null if the receiver has not been initialized */
    private SharedStateMessageReceiver receiver = null;

    public SharedStateComponent(Cell cell) {
        super(cell);
    }

    /**
     * Get a shared map with the given name.  A shared state component
     * may contain any number of separate shared maps. This method
     * will always return a valid map -- if the map does not exist on the
     * server, a new, empty map will be created.  That map will be 
     * persisted on the server the first time a value is set on it.
     * @param name the name of the map to get
     * @return a map with the given name
     */
    public SharedMapCli get(String name) {
        return get(name, true);
    }

    /**
     * Get a shared map by component.  This uses the class name of the component
     * to get the map.
     * @param component the component to get a map for
     * @return a map for the given component
     */
    public SharedMapCli get(CellComponent component) {
        return get(component.getClass().getName());
    }

    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        switch (status) {
            case ACTIVE:
                if (increasing) {
                    receiver = new SharedStateMessageReceiver();
                    channel.addMessageReceiver(ChangeValueMessage.class, receiver);
                } else {
                    channel.removeMessageReceiver(ChangeValueMessage.class);
                    receiver = null;
                }
                break;
        }
    }

    /**
     * Get a map, and optionally initialize it.  This method will either return
     * an existing map, if we have a map with the given name, or an
     * uninitialized map if we haven't seen the map before
     * @param name the name of the map to get
     * @param initialize true to initialize the map, or false not to
     */
    private SharedMapImpl get(String name, boolean initialize) {
        SharedMapImpl out = null;

        // check if we already have the map
        synchronized (this) {
            out = maps.get(name);

            // if we don't have the map, create a new one and put it in the
            // list immediately, so anyone else who requests the same map
            // will get the one that is in the process of initializing.  Make
            // sure to call waitForInit() below to guarantee the list is usable
            // before returning it.
            if (out == null) {
                out = new SharedMapImpl(name, cell, channel);
                maps.put(name, out);
            }
        }

        // make sure the map has initialized
        if (initialize) {
            try {
                out.waitForInit();
            } catch (InterruptedException ie) {
                // uh-oh!  We were trying to get the map and something went
                // really wrong.  Throw an exception and bail out
                throw new IllegalStateException("Request for map " + name +
                                                " timed out", ie);
            }
        }

        // return the map
        return out;
    }

    class SharedStateMessageReceiver implements ComponentMessageReceiver {
        public void messageReceived(CellMessage message) {
            if (message instanceof ChangeValueMessage) {
                ChangeValueMessage cvm = (ChangeValueMessage) message;

                // get the map
                SharedMapImpl map = get(cvm.getMapName(), false);
                map.handleMessage(cvm);
            } else {
                logger.log(Level.WARNING, "Unrecognized message " +
                           message.getClass().getName() + ": " + message);
            }
        }
    }
}
