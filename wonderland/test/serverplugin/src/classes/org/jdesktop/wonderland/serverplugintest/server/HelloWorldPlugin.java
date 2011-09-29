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
package org.jdesktop.wonderland.serverplugintest.server;

import java.util.logging.Logger;
import org.jdesktop.wonderland.server.ServerPlugin;

/**
 * Sample plugin that doesn't do anything
 * @author jkaplan
 */
public class HelloWorldPlugin implements ServerPlugin {
    private static final Logger logger =
            Logger.getLogger(HelloWorldPlugin.class.getName());
    
    public void initialize() {
        logger.warning("Hello world plugin");
    }
}
