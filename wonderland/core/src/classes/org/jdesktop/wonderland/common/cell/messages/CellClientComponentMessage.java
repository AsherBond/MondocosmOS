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
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

/**
 * A message class to either add or remove a component to/from the cell, given
 * either the components client-side state or the client-side cell component
 * class name.
 * <p>
 * This message can be used under two circumstances: to instruction the client
 * to add/remove a component to/from cell, or as an event message sent back
 * to the client who are listening for changes to the components on the client
 * cell.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellClientComponentMessage extends CellMessage {

    /* The message type: ADD or REMOVE */
    public enum ComponentAction { ADD, REMOVE };

    private CellComponentClientState clientState = null;
    private String clientClassName = null;
    private ComponentAction action = null;

    /**
     * Constructor that takes the client state (used to add a component)
     *
     * @param cellID The unique ID of the cell
     * @param clientState The cell component client state
     */
    public CellClientComponentMessage(CellID cellID, String className, CellComponentClientState clientState) {
        super(cellID);
        this.clientState = clientState;
        this.clientClassName = className;
    }

    /**
     * Constructor that takes the client-side component class name and leaves
     * the client state null. (used to remove a component)
     *
     * @param cellID The unique ID of the cell
     * @param className The class name of the client-side component
     */
    public CellClientComponentMessage(CellID cellID, String className) {
        super(cellID);
        this.clientClassName = className;
        this.clientState = null;
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
     * Returns the component's client state, or null if not set.
     *
     * @return The cell component client state, possibly null
     */
    public CellComponentClientState getCellComponentClientState() {
        return clientState;
    }

    /**
     * Returns the comonent's client-side class name, this can never be null.
     *
     * @return The cell component client class name
     */
    public String getCellComponentClientClassName() {
        return clientClassName;
    }

    /**
     * Creates a new ADD component message given the cell id and cell component
     * client state object.
     *
     * @param cellID The unique ID of the cell
     * @param className The cell component class name
     * @param clientState The cell component client state
     * @return A new CellClientComponentMessage class
     */
    public static CellClientComponentMessage newAddMessage(CellID cellID, String className, CellComponentClientState clientState) {
        CellClientComponentMessage msg = new CellClientComponentMessage(cellID, className, clientState);
        msg.action = ComponentAction.ADD;
        return msg;
    }

    /**
     * Creates a new REMOVE component message given the cell id and cell component
     * client state object.
     *
     * @param cellID The unique ID of the cell
     * @param className The cell component class name
     * @return A new CellClientComponentMessage class
     */
    public static CellClientComponentMessage newRemoveMessage(CellID cellID, String className) {
        CellClientComponentMessage msg = new CellClientComponentMessage(cellID, className);
        msg.action = ComponentAction.REMOVE;
        return msg;
    }
}