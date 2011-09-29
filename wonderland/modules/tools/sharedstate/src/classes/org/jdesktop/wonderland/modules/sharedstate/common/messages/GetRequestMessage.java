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
package org.jdesktop.wonderland.modules.sharedstate.common.messages;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 * Request a value in a map
 * @author jkaplan
 */
public class GetRequestMessage extends CellMessage {
    private String mapName;
    private String propName;

    public GetRequestMessage(String mapName, String propName) {
        this.mapName = mapName;
        this.propName = propName;
    }

    public String getMapName() {
        return mapName;
    }

    public String getPropertyName() {
        return propName;
    }
}
