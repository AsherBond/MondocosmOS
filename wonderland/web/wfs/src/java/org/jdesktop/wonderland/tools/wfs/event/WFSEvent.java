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
package org.jdesktop.wonderland.tools.wfs.event;

import org.jdesktop.wonderland.tools.wfs.WFSCell;

/**
 * The WFSCellEvent class represents an update to a cell: its attributes have
 * been updated, children have been added, or children have been removed.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class WFSEvent {
    /* The cell associated with this event */
    private WFSCell cell = null;
    
    /** Default constructor, takes the WFSCell as an argument */
    public WFSEvent(WFSCell cell) {
        this.cell = cell;
    }
    
    /**
     * Returns the cell associated with the event.
     * 
     * @return The cell associated with the event
     */
    public WFSCell getWFSCell() {
        return this.cell;
    }
}
