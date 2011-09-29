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
package org.jdesktop.wonderland.modules.xremwin.client.wm;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.StableAPI;

@StableAPI
public final class X11IntegrationModule implements IntegrationModule {
    private static final Logger logger = Logger.getLogger("lg.appshare");
    
    private String displayName;
    private X11WindowManager wm;

    public X11IntegrationModule (String displayName) {
	this.displayName = displayName;
    }

    public void initialize () {
	try {
            logger.fine("Starting X11WindowManager on display " + displayName);
            wm = new X11WindowManager(displayName);
            logger.fine("X11 integration module successfully started");
	} catch (Throwable e) {
            logger.log(Level.SEVERE, "X Window Manager creation failed: ", e);
	    throw new RuntimeException("X Window Manager creation failed: " + e);
	}
    }

    public void initialize (X11WindowManager.WindowTitleListener wtl) {
	try {
	    //            logger.fine("Starting X11WindowManager on display " + displayName);
	    System.err.println("Starting X11WindowManager on display " + displayName);
            wm = new X11WindowManager(displayName, wtl);
            logger.fine("X11 integration module successfully started");
	} catch (Throwable e) {
            logger.log(Level.SEVERE, "X Window Manager creation failed: ", e);
	    throw new RuntimeException("X Window Manager creation failed: " + e);
	}
    }

    public X11WindowManager getWindowManager () {
	return wm;
    }
}
