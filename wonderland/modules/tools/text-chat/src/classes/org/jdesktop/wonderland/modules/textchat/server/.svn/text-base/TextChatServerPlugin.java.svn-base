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
package org.jdesktop.wonderland.modules.textchat.server;

import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.textchat.server.cell.TextChatAvatarComponentMO;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.comms.CommsManager;

/**
 * Server-side plugin for the text chat feature.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class TextChatServerPlugin implements ServerPlugin {

    public void initialize() {
        // Register a handler for text chat connections
        CommsManager cm = WonderlandContext.getCommsManager();
        cm.registerClientHandler(new TextChatConnectionHandler());

        // Add a component to all avatars to enable the "Text Chat..." context
        // menu on the client
        CellManagerMO.getCellManager().registerAvatarCellComponent(TextChatAvatarComponentMO.class);    
    }
}
