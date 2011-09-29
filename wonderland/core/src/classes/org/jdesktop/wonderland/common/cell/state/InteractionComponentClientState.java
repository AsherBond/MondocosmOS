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

/**
 * The server state for the cell interaction component, which controls how users
 * can interact with a cell.
 *
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class InteractionComponentClientState extends CellComponentClientState {
    private boolean collidable;
    private boolean selectable;

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean pickable) {
        this.selectable = pickable;
    }
}
