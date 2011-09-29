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
package org.jdesktop.wonderland.common.cell.state;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * The server state for the cell interaction component, which controls how users
 * can interact with a cell.
 *
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
@XmlRootElement(name = "interaction-component")
@ServerState
public class InteractionComponentServerState extends CellComponentServerState
        implements Serializable
{
    private boolean collidable = true;
    private boolean selectable = true;

    /** Default constructor */
    public InteractionComponentServerState() {
    }

    @Override
    @XmlTransient
    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.server.cell.InteractionComponentMO";
    }
    
    /**
     * Determine whether or not the cell is collidable -- ie whether avatars
     * collide with the cell or not
     * @return true if the cell is collidable, or false if not
     */
    @XmlElement
    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    /**
     * Determine whether or not the cell is selectable -- ie whether it
     * responds to selection actions like right click in the UI
     * @return true if the cell is collidable, or false if not
     */
    @XmlElement
    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
}
