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

import java.util.Collection;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A response to a map request, telling the map size
 * @author jkaplan
 */
public class MapResponseMessage extends ResponseMessage {
    /** the version number of the map */
    private long version;

    /** the keys in the map */
    private Collection<String> keys;

    public MapResponseMessage(MessageID messageID, long version,
                              Collection<String> keys)
    {
        super (messageID);

        this.version = version;
        this.keys = keys;
    }

    public long getVersion() {
        return version;
    }

    public Collection<String> getKeys() {
        return keys;
    }
}
