/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.modules.security.weblib;

import javax.security.auth.message.module.ServerAuthModule;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.security.weblib.serverauthmodule.WonderSAM;
import org.jdesktop.wonderland.webserver.DefaultSAM;
import org.jdesktop.wonderland.webserver.WebLibPlugin;
import org.jdesktop.wonderland.webserver.WonderlandAppServer;

/**
 * A plugin that registers the WonderSAM with the default SAM
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@Plugin
public class SecurityWebLibPlugin implements WebLibPlugin {
    public void initialize(WonderlandAppServer was) {
        Class<? extends ServerAuthModule> delegate =
                WonderSAM.class.asSubclass(ServerAuthModule.class);
        DefaultSAM.setDelegateClass(delegate);
    }
}
