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
/** Copyright (c) 2005 Amir Bukhari
 *
 * Permission to use, copy, modify, distribute, and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that
 * copyright notice and this permission notice appear in supporting
 * documentation, and that the name of Amir Bukhari not be used in
 * advertising or publicity pertaining to distribution of the software without
 * specific, written prior permission.  Amir Bukhari makes no
 * representations about the suitability of this software for any purpose.  It
 * is provided "as is" without express or implied warranty.
 *
 * AMIR BUKHARI DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL AMIR BUKHARI BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 * TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */
package org.jdesktop.wonderland.modules.xremwin.client.wm;

import java.util.ArrayList;
import java.util.logging.Logger;
import gnu.x11.Atom;
import gnu.x11.Display;
import gnu.x11.Window;
import gnu.x11.Enum;
import org.jdesktop.wonderland.common.StableAPI;

/**
 * @author bukhari
 */

@StableAPI
class X11WindowManagerHints {

    protected static final Logger logger = Logger.getLogger("lg.x11");

    final static int BASENR = 5;
    static Atom netSupported = null;
    static Atom netallowedAction = null;
    static Atom netWmState = null;
    static Atom netWmName = null;
    static Atom netSupportingWmCheck = null;
    static Atom windowTypes[] = new Atom[9];
    static Atom allowedActions[] = new Atom[4];
    static Atom wmStates[] = new Atom[4];    
        
    static void initX11WindowManagerHint(Display display) {
	netSupported = (Atom) Atom.intern(display, "_NET_SUPPORTED");
	netWmName = (Atom) Atom.intern(display, "_NET_WM_NAME");

	netSupportingWmCheck = (Atom) Atom.intern(display,
		"_NET_SUPPORTING_WM_CHECK");
	windowTypes[0] = (Atom) Atom.intern(display, "_NET_WM_WINDOW_TYPE");
	windowTypes[1] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_NORMAL");
	windowTypes[2] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_DIALOG");
	windowTypes[3] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_SPLASH");
	windowTypes[4] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_DESKTOP");
	windowTypes[5] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_DOCK");
	windowTypes[6] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_TOOLBAR");
	windowTypes[7] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_MENU");
	windowTypes[8] = (Atom) Atom.intern(display,
		"_NET_WM_WINDOW_TYPE_UTILITY");

	netWmState = (Atom) Atom.intern(display, "_NET_WM_STATE");
	wmStates[0] = (Atom) Atom.intern(display, "_NET_WM_STATE_ABOVE");
	wmStates[1] = (Atom) Atom.intern(display, "_NET_WM_STATE_BELOW");
	wmStates[2] = (Atom) Atom.intern(display, "_NET_WM_STATE_MODAL");
	wmStates[3] = (Atom) Atom.intern(display,
		"_NET_WM_STATE_SKIP_TASKBAR");

	netallowedAction = (Atom) Atom.intern(display,
		"_NET_WM_ALLOWED_ACTIONS");
	allowedActions[0] = (Atom) Atom.intern(display,
		"_NET_WM_ACTION_CLOSE");
	allowedActions[1] = (Atom) Atom.intern(display,
		"_NET_WM_ACTION_MINIMIZE");
	allowedActions[2] = (Atom) Atom
		.intern(display, "_NET_WM_ACTION_MOVE");
	allowedActions[3] = (Atom) Atom.intern(display,
		"_NET_WM_ACTION_RESIZE");
    }
    
    /**
     * initialize the Window Manager Hints. this initialize all supported features
     * of extended Window Manager Hints.
     * @param root
     * @param checkwin
     */
    static void initWmNETSupport(Display display, Window root[], Window checkwin[]) {
	int netsp[] = new int[BASENR + windowTypes.length + wmStates.length
		+ allowedActions.length];
	int i = 0;
	String wmname = "LG3D";
	netsp[i++] = netSupported.id;
	netsp[i++] = netSupportingWmCheck.id;

	for (int j = 0; j < windowTypes.length; j++) {
	    netsp[i++] = windowTypes[j].id;
	}
	netsp[i++] = netWmName.id;
	netsp[i++] = netWmState.id;
	for (int j = 0; j < wmStates.length; j++) {
	    netsp[i++] = wmStates[j].id;
	}

	netsp[i++] = netallowedAction.id;
	for (int j = 0; j < allowedActions.length; j++) {
	    netsp[i++] = allowedActions[j].id;
	}

	// now set the property
	for (int j = 0; j < root.length; j++) {
	    int chkWin[] = { checkwin[j].id };
	    root[j].change_property(Window.REPLACE, netsp.length, netSupported,
		    Atom.ATOM, 32, netsp, 0, 32);
	    root[j].change_property(Window.REPLACE, 1, netSupportingWmCheck,
		    Atom.WINDOW, 32, chkWin, 0, 32);
	    checkwin[j].change_property(Window.REPLACE, 1,
		    netSupportingWmCheck, Atom.WINDOW, 32, chkWin, 0, 32);
	    checkwin[j].change_property(Window.REPLACE, wmname.length(),
		    netWmName, Atom.STRING, 8, wmname.getBytes(), 0, 8);
	    checkwin[j].set_wm_class_hint("lg3d", "LG3D");
	    checkwin[j].set_wm_name("LG3D");
	}

	System.err.println("LG3D WM Hints initialized");
    }

    //////////////////////////////////////////////////////////////
    // TODO THE FELLOWING FUNCTIONS ARE NOT COMPLETLY IMPLEMENTED.
    // NEED TO BE EXTENDED. FEATURES WILL BE ADDED FROM RELEASE
    // TO RELEASE.
    /////////////////////////////////////////////////////////////
    /**
     * set the default NET_WM_STATE acording to window type. 
     * @param client
     */
    static void setNetWmState(Display display, X11Client client) {
	int temp[] = new int[5];
	int i = 0;

	for (int j = 0; j < windowTypes.length; j++) {
	    if (client.getNetWindowType() == ((Atom) Atom.intern(display,
		    "_NET_WM_WINDOW_TYPE_DIALOG")).id) {
		temp[i++] = ((Atom) Atom.intern(display,
			"_NET_WM_STATE_SKIP_TASKBAR")).id;
		break;
	    }
	    if (client.getNetWindowType() == ((Atom) Atom.intern(display,
		    "_NET_WM_WINDOW_TYPE_SPLASH")).id) {
		temp[i++] = ((Atom) Atom.intern(display,
			"_NET_WM_STATE_SKIP_TASKBAR")).id;
		break;
	    }
	    if (client.getNetWindowType() == ((Atom) Atom.intern(display,
		    "_NET_WM_WINDOW_TYPE_UTILITY")).id) {
		temp[i++] = ((Atom) Atom.intern(display,
			"_NET_WM_STATE_SKIP_TASKBAR")).id;
		break;
	    }
	}
	int data[] = new int[i];
	for (int j = 0; j < i; j++) {
	    data[j] = temp[j];
	}
	client.change_property(Window.REPLACE, i, netWmState, Atom.ATOM, 32,
		data, 0, 32);
    }

    /**
     * set _NET_WM_ALLOWED_ACTIONS properity according to window type.
     * @param client
     */
    static void setNetAllowedActions(Display display, X11Client client) {
	int temp[] = new int[10];
	int i = 0;

	for (int j = 0; j < windowTypes.length; j++) {
	    if (client.getNetWindowType() == ((Atom) Atom.intern(display,
		    "_NET_WM_WINDOW_TYPE_DIALOG")).id) {
		temp[i++] = ((Atom) Atom
			.intern(display, "_NET_WM_ACTION_CLOSE")).id;
		temp[i++] = ((Atom) Atom.intern(display, "_NET_WM_ACTION_MOVE")).id;
		break;
	    }
	}
	int data[] = new int[i];
	for (int j = 0; j < i; j++) {
	    data[j] = temp[j];
	}
	client.change_property(Window.REPLACE, i, netallowedAction, Atom.ATOM,
		32, data, 0, 32);
    }

    /**
     * save the window type.
     * @param client
     * @param type
     */
    static void setNetWindowType(Display display, X11Client client, Atom type) {
	client.setNetWindowType(type);
    }    

    /**
     * get current window state.
     * @param win
     */
    Atom[] getWmState(Display display, Window win) {
	ArrayList<Atom> res = new ArrayList<Atom>();
	Window.PropertyReply rep = win.property(false, netWmState, Atom.ATOM,
		0, 5);
	Enum enm = rep.items();
	while (enm.more()) {
	    Atom atom = (Atom) Atom.intern(display, enm.next_integer());
	    res.add(atom);
	    logger.fine("WM State: " + win + " " + atom);
	}
	if (res.size() > 0) {
	    return res.toArray(new Atom[res.size()]);
	}
	return null;
    }

    /**
     * get window type (_NET_WM_WINDOW_TYPE)
     * @param win
     * @return window type ATOM
     */
    static Atom getNetWindowType(Display display, Window win) {
	Atom res = null;
	Window.PropertyReply rep = win.property(false, windowTypes[0],
		Atom.ATOM, 0, 1);

	if (rep.length() == 1) {
	    Enum enm = rep.items();
	    res = (Atom) Atom.intern(display, enm.next_integer(), true);
	} else {
	    /** if not set by application assume NORMAL TYPE */
	    res = (Atom) Atom.intern(display, "_NET_WM_WINDOW_TYPE_NORMAL");
	}
	logger.fine("WM Type: " + win + " " + res);
	return res;
    }

    /**
     * check if Window is supported by our WM. supported windows are:
     * _NET_WM_WINDOW_TYPE_NORMAL, _NET_WM_WINDOW_TYPE_DIALOG, 
     * _NET_WM_WINDOW_TYPE_SPLASH, _NET_WM_WINDOW_TYPE_UTILITY
     * @param client
     * @return
     */
    static boolean isSupportedWinType(Display display, X11Client client) {
	boolean res = false;
	int type = client.getNetWindowType();
	if (windowTypes[1].id == type) { // _NET_WM_WINDOW_TYPE_NORMAL
	    return true;
	}
	if (windowTypes[2].id == type) { // _NET_WM_WINDOW_TYPE_DIALOG
	    return true;
	}
	if (windowTypes[3].id == type) { // _NET_WM_WINDOW_TYPE_SPLASH
	    return true;
	}
	if (windowTypes[6].id == type) { // _NET_WM_WINDOW_TYPE_TOOLBAR
	    return true;
	}
	if (windowTypes[7].id == type) { // _NET_WM_WINDOW_TYPE_MENU
	    return true;
	}
	if (windowTypes[8].id == type) { // _NET_WM_WINDOW_TYPE_UTILITY
	    return true;
	}

	return res;
    }

    /**
     * set the window features, such as closeable, maximzable. this give 
     * control of how 3D Frame is drawn by LookANDFeel class. 
     * @param client
     */
    static void setWindowFeatures(Display display, X11Client client) {
	int type = client.getNetWindowType();
	if (windowTypes[1].id == type) { // _NET_WM_WINDOW_TYPE_NORMAL
	    client.setNormalWindow(true);
	    Window.WMSizeHints sizeHints = client.wm_normal_hints();
	    
	    if (sizeHints != null &&
		(sizeHints.min_width() == sizeHints.max_width()) &&
		(sizeHints.min_height() == sizeHints.max_height())) {
		client.setResizable(false);
		client.setMaximizable(false);
	    }	    
	    return;
	}
	if (windowTypes[2].id == type) { // _NET_WM_WINDOW_TYPE_DIALOG
	    client.setNormalWindow(false);
	    client.setCloseable(true);
	    Window.WMSizeHints sizeHints = client.wm_normal_hints();
	    if ((sizeHints.min_width() == sizeHints.max_width())
		    && (sizeHints.min_height() == sizeHints.max_height())) {
		client.setResizable(false);
	    }
	    client.setMinimizable(false);
	    client.setMaximizable(false);
	    return;
	}
	if (windowTypes[3].id == type) { // _NET_WM_WINDOW_TYPE_SPLASH
	    client.setNormalWindow(false);
	    client.setDecorated(false);
	    return;
	}
	if (windowTypes[6].id == type) { // _NET_WM_WINDOW_TYPE_TOOLBAR
	    client.setNormalWindow(false);
	    client.setCloseable(true);
	    client.setMinimizable(false);
	    client.setMaximizable(false);
	    return;
	}
	if (windowTypes[7].id == type) { // _NET_WM_WINDOW_TYPE_MENU
	    client.setNormalWindow(false);
	    client.setCloseable(true);
	    client.setMinimizable(false);
	    client.setMaximizable(false);
	    return;
	}
	if (windowTypes[8].id == type) { // _NET_WM_WINDOW_TYPE_UTILITY
	    client.setNormalWindow(false);
	    client.setCloseable(true);
	    client.setMinimizable(false);
	    client.setMaximizable(false);
	    return;
	}
    }
}
