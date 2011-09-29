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
package org.jdesktop.wonderland.common.cell.messages;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.security.ComponentAction;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * A message class to either add or remove a component to/from the cell, given
 * either the components server-side state or the server-side cell component
 * class name.
 * <p>
 * This message can be used under two circumstances: to instruction the server
 * to add/remove a component to/from cell, or as an event message sent back
 * to the client who are listening for changes to the components on the server
 * cell.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Actions(ComponentAction.class)
public class CellServerComponentMessage extends CellMessage {

    /* The message type: ADD or REMOVE */
    public enum ComponentAction { ADD, REMOVE };

    private CellComponentServerState serverState = null;
    private String serverClassName = null;
    private ComponentAction action = null;

    /**
     * Constructor that takes the server state (used to add a component)
     *
     * @param cellID The unique ID of the cell
     * @param serverState The cell component server state
     */
    public CellServerComponentMessage(CellID cellID, CellComponentServerState serverState) {
        super(cellID);
        this.serverState = serverState;
        this.serverClassName = serverState.getServerComponentClassName();
    }

    /**
     * Constructor that takes the server-side component class name and leaves
     * the server state null. (used to remove a component)
     *
     * @param cellID The unique ID of the cell
     * @param className The class name of the server-side component
     */
    public CellServerComponentMessage(CellID cellID, String className) {
        super(cellID);
        this.serverClassName = className;
        this.serverState = null;
    }

    /**
     * Returns the type of component action, either ADD or REMOVE
     *
     * @return Either ADD or REMOVE
     */
    public ComponentAction getComponentAction() {
        return action;
    }

    /**
     * Returns the component's server state, or null if not set.
     *
     * @return The cell component server state, possibly null
     */
    public CellComponentServerState getCellComponentServerState() {
        return serverState;
    }

    /**
     * Returns the comonent's server-side class name, this can never be null.
     *
     * @return The cell component server class name
     */
    public String getCellComponentServerClassName() {
        return serverClassName;
    }

    /**
     * Creates a new ADD component message given the cell id and cell component
     * server state object.
     *
     * @param cellID The unique ID of the cell
     * @param serverState The cell component server state
     * @return A new CellServerComponentMessage class
     */
    public static CellServerComponentMessage newAddMessage(CellID cellID, CellComponentServerState serverState) {
        CellServerComponentMessage msg = new CellServerComponentMessage(cellID, serverState);
        msg.action = ComponentAction.ADD;
        return msg;
    }

    /**
     * Creates a new ADD component message given the cell id and the cell
     * component server-side class name.
     *
     * @param cellID The unique ID of the cell
     * @param className The cell component server class name
     * @return A new CellServerComponentMessage class
     */
    public static CellServerComponentMessage newAddMessage(CellID cellID, String className) {
        CellServerComponentMessage msg = new CellServerComponentMessage(cellID, className);
        msg.action = ComponentAction.ADD;
        return msg;
    }

    /**
     * Creates a new REMOVE component message given the cell id and cell component
     * server-side class name.
     *
     * @param cellID The unique ID of the cell
     * @param className The cell component server class name
     * @return A new CellServerComponentMessage class
     */
    public static CellServerComponentMessage newRemoveMessage(CellID cellID, String className) {
        CellServerComponentMessage msg = new CellServerComponentMessage(cellID, className);
        msg.action = ComponentAction.REMOVE;
        return msg;
    }
}
