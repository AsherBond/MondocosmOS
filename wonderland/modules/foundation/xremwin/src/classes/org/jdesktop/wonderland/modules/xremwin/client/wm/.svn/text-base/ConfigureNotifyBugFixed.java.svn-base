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

import gnu.x11.Display;
import gnu.x11.event.ConfigureNotify;
import org.jdesktop.wonderland.common.StableAPI;

/**
 *
 * @author deronj
 */

@StableAPI
public class ConfigureNotifyBugFixed extends ConfigureNotify 
{
    public ConfigureNotifyBugFixed (Display dpy, byte[] data) {
	super(dpy, data); 
    }

    @Override
    public int x () {
	int x = read2(16); 
	// Be sure to sign extend
	return (x<<16)>>16;
    }

    @Override
    public int y () {
	int y = read2(18); 
	// Be sure to sign extend
	return (y<<16)>>16;
    }
}
