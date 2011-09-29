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
import org.jdesktop.wonderland.modules.sharedstate.common.SharedData;

/**
 * Request to set a value
 * @author jkaplan
 */
public class ChangeValueMessage extends CellMessage {
    public enum Action { PUT, REMOVE }

    private String mapName;
    private long version;
    private String propName;
    private Action action;
    private SharedData propVal;

    private ChangeValueMessage(String mapName, long version) {
        this.mapName = mapName;
        this.version = version;
    }

    public String getMapName() {
        return mapName;
    }

    public long getVersion() {
        return version;
    }

    public String getPropertyName() {
        return propName;
    }

    public Action getAction() {
        return action;
    }

    public SharedData getPropertyValue() {
        return propVal;
    }

    public static ChangeValueMessage put(String mapName, long version,
                                         String propName, SharedData propVal)
    {
        ChangeValueMessage out = new ChangeValueMessage(mapName, version);
        out.action = Action.PUT;
        out.propName = propName;
        out.propVal = propVal;
        return out;
    }

    public static ChangeValueMessage remove(String mapName, long version,
                                            String propName)
    {
        ChangeValueMessage out = new ChangeValueMessage(mapName, version);
        out.action = Action.REMOVE;
        out.propName = propName;
        return out;
    }
}
