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
package org.jdesktop.wonderland.modules.security.common.messages;

import java.util.Set;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.security.common.ActionDTO;

/**
 * Send an update to this client's permissions
 * @author jkaplan
 */
public class PermissionsResponseMessage extends ResponseMessage {
    private Set<ActionDTO> granted;

    public PermissionsResponseMessage(MessageID id, Set<ActionDTO> granted) {
        super (id);
        
        this.granted = granted;
    }

    /**
     * Get permissions for this client
     * @return the permissions for this client
     */
    public Set<ActionDTO> getGranted() {
        return granted;
    }
}
