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
package org.jdesktop.wonderland.common.cell.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The CellClientState class is the base class of all state information
 * communicated between the client and Darkstar server nodes.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellClientState implements Serializable {
    /* The name of the cell */
    private String name = null;

    private Map<String, CellComponentClientState> clientComponentClasses = new HashMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the class names of all the client CellComponents which should
     * be added to the cell at config time
     * 
     * @return
     */
    public String[] getClientComponentClasses() {
        if (clientComponentClasses==null)
            return new String[0];
        return clientComponentClasses.keySet().toArray(new String[clientComponentClasses.size()]);
    }

    /**
     * Returns the client state for the given cell component class name, or null
     * if the class name is not present
     *
     * @param className The name of the client component class
     * @return The cell component client state object, or null
     */
    public CellComponentClientState getCellComponentClientState(String className) {
        if (clientComponentClasses == null) {
            return null;
        }
        return clientComponentClasses.get(className);
    }

    /**
     * Set the CellComponent class names that will be installed in the client
     * cell
     * @param cellComponenClasses the array of class names for client CellComponents
     */
    public void addClientComponentClasses(String[] cellComponenClasses) {
        if (cellComponenClasses!=null) {        
            for(String s : cellComponenClasses)
                clientComponentClasses.put(s, null);
        }
    }

    /**
     * Add a client component class to the set of components
     *
     * @param clientClass The name of the client component class
     * @param clientState The client component state
     */
    public void addClientComponentClasses(String clientClass, CellComponentClientState clientState) {
        clientComponentClasses.put(clientClass, clientState);
    }
}
