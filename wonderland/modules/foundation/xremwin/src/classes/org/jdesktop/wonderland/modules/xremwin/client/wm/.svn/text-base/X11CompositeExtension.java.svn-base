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

import gnu.x11.Data;
import gnu.x11.Display;
import gnu.x11.Request;
import gnu.x11.Window;
import gnu.x11.XProtocolInfo;
import gnu.x11.extension.Extension;
import gnu.x11.extension.NotFoundException;
import org.jdesktop.wonderland.common.StableAPI;

/**
 * @author deronj
 */

@StableAPI
final class X11CompositeExtension extends Extension {
    public static final String COMPOSITE_NAME = "Composite";
    public static final int CLIENT_MAJOR_VERSION = 0;
    public static final int CLIENT_MINOR_VERSION = 3;
    public static final String[] MINOR_OPCODE_STRINGS = {
	"X_CompositeQueryVersion",               // 0
	"X_CompositeRedirectWindow",             // 1
	"X_CompositeRedirectSubWindows",         // 2
	"X_CompositeUnredirectWindow",           // 3
	"X_CompositeUnredirectSubWindows",       // 4
	"X_CompositeCreateRegionFromBorderClip", // 5
	"X_CompositeNameWindowPixmap",           // 6
	"X_CompositeGetOverlayWindow",           // 7
    };
    public static final int AUTOMATIC = 0;
    public static final int MANUAL    = 1;

    public int server_major_version, server_minor_version;

    /**
     * Composite opcode 0 - query version
     */
    public X11CompositeExtension(Display display) throws NotFoundException { 
	super (display, COMPOSITE_NAME, MINOR_OPCODE_STRINGS); 

	// These extension requests expect replies
	XProtocolInfo.extensionRequestExpectsReply(major_opcode, 0, 32); // QueryVersion
	XProtocolInfo.extensionRequestExpectsReply(major_opcode, 7, 32); // GetOverlayWindow

	// check version before any other operations
	Request request = new Request (display, major_opcode, 0, 3);
	request.write4 (CLIENT_MAJOR_VERSION);
	request.write4 (CLIENT_MINOR_VERSION);

	Data reply = display.read_reply (request);
	server_major_version = reply.read4 (8);
	server_minor_version = reply.read4 (12);
    }
    
    /**
     * Composite opcode 1 - redirect window
     */
    public void redirectWindow(Window window, int update) {
	Request request = new Request (display, major_opcode, 1, 3);
	request.write4 (window.id);
	request.write1 (update);
	request.write1 (0);
	request.write2 (0);
	display.send_request (request);
    }
    
    /**
     * Composite opcode 2 - redirect subwindows
     */
    public void redirectSubwindows(Window window, int update) {
	Request request = new Request (display, major_opcode, 2, 3);
	request.write4 (window.id);
	request.write1 (update);
	request.write1 (0);
	request.write2 (0);
	display.send_request (request);
    }
    
    /**
     * Composite opcode 3 - unredirect window
     */
    public void unredirectWindow(Window window, int update) {
	Request request = new Request (display, major_opcode, 3, 3);
	request.write4 (window.id);
	request.write1 (update);
	request.write1 (0);
	request.write2 (0);
	display.send_request (request);
    }
    
    /**
     * Composite opcode 4 - unredirect subwindows
     */
    public void unredirectSubwindows(Window window, int update) {
	Request request = new Request (display, major_opcode, 4, 3);
	request.write4 (window.id);
	request.write1 (update);
	request.write1 (0);
	request.write2 (0);
	display.send_request (request);
    }

    /**
     * Composite opcode 7 - GetOverlayWindow
     */
    public long getOverlayWindow (Window win) { 
	Request request = new Request (display, major_opcode, 7, 2);
	request.write4 (win.id);

	Data reply = display.read_reply (request);
	if (reply == null) {
	    return -1;
	}

	return reply.read4(8);
    }
}
