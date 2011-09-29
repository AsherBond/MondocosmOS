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
import org.jdesktop.wonderland.common.cell.security.ModifyAction;
import org.jdesktop.wonderland.common.security.annotation.Actions;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedData;

/**
 * Request to set a value
 * @author jkaplan
 */
@Actions(ModifyAction.class)
public class PutRequestMessage extends CellMessage {
    private String mapName;
    private String propName;
    private SharedData propVal;

    public PutRequestMessage(String mapName, String propName, 
                             SharedData propVal)
    {
        this.mapName = mapName;
        this.propName = propName;
        this.propVal = propVal;
    }

    public String getMapName() {
        return mapName;
    }

    public String getPropertyName() {
        return propName;
    }

    public SharedData getPropertyValue() {
        return propVal;
    }
}
