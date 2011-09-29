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
package org.jdesktop.wonderland.modules.avatarbase.server.cell;

import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;

/**
 * Server side component for name tags. The only purpose of class at the moment
 * is to allow the avatarbase to register it's client NameTagComponent. This server
 * side class does not track or allow changes to be made to the status of the name tag.
 * It may be enhanced in the future....
 *
 * @author paulby
 */
public class NameTagComponentMO extends CellComponentMO {

    public NameTagComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagComponent";
    }

}
