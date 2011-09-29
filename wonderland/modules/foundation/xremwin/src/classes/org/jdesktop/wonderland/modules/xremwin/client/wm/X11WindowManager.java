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
/*
    This file is derived from escher-0.2.2/gnu/app/puppet/Puppet.java
    of Escher 0.2.2.  Here is the copyright notice of the original file:

Copyright (c) 2000-2004, Stephen Tse
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the organization nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOTa
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jdesktop.wonderland.modules.xremwin.client.wm;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import gnu.x11.Application;
import gnu.x11.Atom;
import gnu.x11.Depth;
import gnu.x11.Display;
import gnu.x11.Error;
import gnu.x11.Screen;
import gnu.x11.Visual;
import gnu.x11.Window;
import gnu.x11.event.ClientMessage;
import gnu.x11.event.ConfigureNotify;
import gnu.x11.event.ConfigureRequest;
import gnu.x11.event.CreateNotify;
import gnu.x11.event.DestroyNotify;
import gnu.x11.event.EnterNotify;
import gnu.x11.event.Event;
import gnu.x11.event.MapNotify;
import gnu.x11.event.MapRequest;
import gnu.x11.event.MappingNotify;
import gnu.x11.event.PropertyNotify;
import gnu.x11.event.ReparentNotify;
import gnu.x11.event.UnmapNotify;
//TODO: import java.awt.Event;
import java.util.LinkedList;
import org.jdesktop.wonderland.common.StableAPI;
//TODO: import org.jdesktop.wonderland.modules.appbase.client.Window;
//TODO: import sun.awt.X11.Depth;
//TODO: import sun.awt.X11.Screen;
//TODO: import sun.awt.X11.Visual;

@StableAPI
final public class X11WindowManager extends Application 
    implements Runnable, Display.DisconnectListener
{
    private static final Logger logger = Logger.getLogger("lg.x11");
    
    /**
     * Provides a way for the window manager to notify other
     * Wonderland software components that a master window's
     * title has changed.
     */
    public interface WindowTitleListener {

	/** 
	 * Window title of Master window has changed.
	 * @param wid The X window ID of the window.
	 * @param String The new window title.
	 */
	public void setWindowTitle(int wid, String windowTitle);
    }

    // useful atoms
    private final Atom atomWmState;
    private final Atom atomWmChangeState;
    private final Atom atomWmProtocols;
    private final Atom atomWmDeleteWindow;
    private final Atom atomWmTakeFocus;
    private final Atom atomWmColormapWindows;
    private final Atom atomWmTransientFor;
    private final Atom atomCompoundText;
    private final Atom atomServerShutdown;
    private final Atom[] atomsWmSelection;
    
    //X11WindowManagerHints x11WMHints = null;
    private Window rootWin = null;
    private Window rootWindows[];
    private HashMap<Integer,Depth> screenDepth = null;
    private HashMap<Integer,Visual> screenVisual = null;    
    private int defaultDepth;
    
    private WindowTitleListener wtl;

    // True while the window manager thread is running
    private boolean active;

    // Who to notify when the window manager exits
    private LinkedList<ExitListener> exitListeners =
	new LinkedList<ExitListener>();

    public interface ExitListener {
	public void windowManagerExitted();
    }

    public X11WindowManager(String dpy) {
	this(dpy, null);
    }

    public X11WindowManager(String dpy, WindowTitleListener wtl) {
	super(new String[] {"--display", dpy});
        this.wtl = wtl;

        logger.config("Starting X Window Manager against display " + dpy);
        
	// TODO: windows sometimes appear and disappear so fast that 
	// LG ends up sending some requests after a window has been 
	// destroyed. As a simple work around the LG Display Server
	// is simply going to tell Escher to ignore troublesome requests.
	// But we should eventually see if there is a better way to work around.
	display.ignore_error(gnu.x11.Error.BAD_WINDOW);
	display.ignore_error(gnu.x11.Error.BAD_DRAWABLE);
	display.ignore_error(gnu.x11.Error.BAD_MATCH);
	display.ignore_error(gnu.x11.Error.BAD_ATOM);
	X11WindowManagerHints.initX11WindowManagerHint(display);
	
	//display.debug_mode = true;
        rootWin = display.default_root;
	Window.NONE.display = display; // for move pointer
	
	display.setDisconnectListener(this);

        screenDepth = new HashMap<Integer,Depth>();
        screenVisual = new HashMap<Integer,Visual>();
	
	defaultDepth = display.default_depth;

        atomsWmSelection = new Atom[display.screens.length];
        Window wmSelectionWin = new Window(rootWin, 0,0, 1, 1, 0, new Window.Attributes());

	for (int i = 0; i< display.screens.length; i++) {
	    Screen screen = display.screens[i];
	    gnu.x11.Enum scrdepths = screen.depths();
	    while (scrdepths.more()) {
	        Depth depth = (Depth) scrdepths.next(); 
	        screenDepth.put(depth.depth(), depth);
		
		gnu.x11.Enum enm = depth.visuals();        
		while (enm.more()) {
		    Visual visual = (Visual) enm.next();              
		    screenVisual.put(visual.id(), visual);
		}
	    }

	    // AWT in Java6u1 requires that the WM acquire these selections.
	    // Note: even though the WM conventions recommend that CurrentTime
	    // not be used we can use it here because the LG WM is the only one
	    // acquiring these selections.

	    atomsWmSelection[i] = (Atom)Atom.intern(display, "WM_S" + i);
	    wmSelectionWin.set_selection_owner(atomsWmSelection[i], Display.CURRENT_TIME);
	    display.check_error();
	}

	atomWmState = (Atom)Atom.intern(display, "WM_STATE");
	atomWmChangeState = (Atom)Atom.intern(display, "WM_CHANGE_STATE");
	atomWmProtocols = (Atom)Atom.intern(display, "WM_PROTOCOLS");
	atomWmDeleteWindow = (Atom)Atom.intern(display, "WM_DELETE_WINDOW");
	atomWmTakeFocus = (Atom)Atom.intern(display, "WM_TAKE_FOCUS");
	atomWmColormapWindows=(Atom)Atom.intern(display,"WM_COLORMAP_WINDOWS");
	atomWmTransientFor = (Atom)Atom.intern(display, "WM_TRANSIENT_FOR");
	atomCompoundText = (Atom)Atom.intern(display, "COMPOUND_TEXT");
	atomServerShutdown = (Atom)Atom.intern(display, "SERVER_SHUTDOWN");

	try {
            selectInput(rootWin);
	} catch (Error err) {
	    if (err.code == Error.BAD_ACCESS && err.bad == rootWin.id) {
                logger.severe("Failed to access root window. Another WM is running?");
		throw new RuntimeException ("Failed to access root window\n"
		    + "Another WM is running?"); // FIXME
	    } else {
		throw err;
	    }
	}
        
        // Remote Windows: Automatically composite redirect all root top-level windows
	compositeTopLevelWindows();

	// Init WmNET support for screen 0
	// TODO: multiscreen
	Window[] rootWins = new Window[1];
	Window[] checkWins = new Window[1];
	rootWins[0] = rootWin;
        checkWins[0] = new Window(rootWin, 0,0, 1, 1, 0, new Window.Attributes());
	X11WindowManagerHints.initWmNETSupport(display, rootWins, checkWins);

	// prepare for the event dispatch thread
	Thread eventThread = new Thread( this, "X11WindowManager" );
	// eventThread.setPriority(Thread.NORM_PRIORITY + 2); // FIXME
	eventThread.start();
    
	logger.info(
		"X Window Manager initialization completed against display "
		+ dpy);
    }

    public void disconnect () {
	if (display != null) {
	display.setDisconnectListener(null);
	display.close();
	display = null;
    }
    }

    // Display disconnected
    public void disconnected () {
	active = false;
	disconnect();
	logger.severe("X11WindowManager exitted.");

        // We must clone the list because the exit listener may remove
        // itself as a listener
        LinkedList<ExitListener> listenersToNotify = (LinkedList<ExitListener>) exitListeners.clone();

	for (ExitListener exitListener : listenersToNotify) {
	    exitListener.windowManagerExitted();
	}
    }

    private void compositeTopLevelWindows () {
	X11CompositeExtension compExt = null;
	try {
	    compExt = new X11CompositeExtension(display);
	} catch (Exception ex) {
	    throw new RuntimeException("Failed to access composite extension");
	}

	compExt.redirectSubwindows(display.default_root, X11CompositeExtension.MANUAL);
	display.check_error(); // Xsync 
    }

    private void selectInput(Window win) {
        win.select_input(
            // unmap, destroy notify
            Event.SUBSTRUCTURE_NOTIFY_MASK
            // map, configure, circulate request
            | Event.SUBSTRUCTURE_REDIRECT_MASK
            // ICCCM properties (wm name, hints, normal hints)
            | Event.PROPERTY_CHANGE_MASK);
        
	display.check_error();
    }
    
    private void selectInputForPRW (Window win) {
	/*
        win.select_input(
            // unmap, destroy notify
            Event.SUBSTRUCTURE_NOTIFY_MASK
            // map, configure, circulate request
            | Event.SUBSTRUCTURE_REDIRECT_MASK
            // ICCCM properties (wm name, hints, normal hints)
            | Event.PROPERTY_CHANGE_MASK
            // Enter
	    | Event.ENTER_WINDOW_MASK);	    
        
	display.check_error();
	*/
    }

    public void run() {
	active = true;
	while (!exit_now) {
	    try {
		readAndDispatchEvent();
	    } catch (Throwable t) {
		// FIXME -- how to deal with this situation?
		logger.log(Level.WARNING, 
                    "Error in X event dispatching: " + t, t);
		System.err.println("error in x event dispatching. Exit");
		exit_now = true;
	    }
	}

	System.err.println("X display disconnected.");
	disconnected();
    }

    public void addExitListener (ExitListener listener) {
	exitListeners.add(listener);
    }

    public void removeExitListener (ExitListener listener) {
	exitListeners.remove(listener);
    }

    public boolean isActive () {
	return active;
    }

    private void alertUser(String message) {
	logger.warning(message);
	display.bell(-50);
    }

    private void readAndDispatchEvent() {
	Event firstEvent = null;
	try {
	    firstEvent = display.next_event();
	} catch (Exception e) {
	    // We may get an exception at this point if the XS server goes
	    // away. Just ignore it and exit the window manager.
	    System.err.println("readAndDispatchEvent: exception. Exit");
	    exit_now = true;
	}
	if (firstEvent == null) {
	    exit_now = true;
	}
	dispatch(firstEvent);    
        if (display != null) {
            display.flush();
        }
    }

    private void dispatch(Event event) {
	logger.fine(event.toString());
        
	switch(event.code()) {
	    case ClientMessage.CODE: // un-avoidable
		clientMessage((ClientMessage)event);
		break;

	    case ConfigureRequest.CODE: // Event.SUBSTRUCTURE_NOTIFY
		configureRequest((ConfigureRequest)event);
		break;

	    case ConfigureNotify.CODE: // Event.SUBSTRUCTURE_NOTIFY
		configureNotify((ConfigureNotify)event);
		break;

	    case DestroyNotify.CODE: // Event.SUBSTRUCTURE_NOTIFY
		destroyNotify((DestroyNotify)event);
		break;
		
	    case PropertyNotify.CODE: // Event.PROPERTY_CHANGE
		propertyNotify((PropertyNotify)event);
		break;

	    case MapRequest.CODE: // Event.SUBSTRUCTURE_REDIRECT
		mapRequest((MapRequest)event);
		break;
		
	    case MapNotify.CODE: // Event.SUBSTRUCTURE_NOTIFY
		mapNotify((MapNotify)event);
		break;
      
	    case UnmapNotify.CODE: // Event.SUBSTRUCTURE_NOTIFY
		unmapNotify((UnmapNotify)event);
		break;

	    case CreateNotify.CODE: // Event.SUBSTRUCTURE_NOTIFY, ignored
		createNotify((CreateNotify)event);
		break;

	    case MappingNotify.CODE: // un-avoidable, ignored
		break;

	    case ReparentNotify.CODE: // nothing to do, ignored
		break;
		
	    case EnterNotify.CODE:
	        enterNotify((EnterNotify) event);
	        break;
	        
	    default:
		alertUser("Unhandled event: " + event);
	}
    }

    private void manage(X11Client client) {
	// ready for move and resize
	client.geometry();
	// ready for next focus and preference
	client.classHint = client.wm_class_hint();
	// ready for minimize
	client.sizeHints = client.wm_normal_hints();
	// ready for info
	client.setName(client.wm_name());
	client.change_save_set(false);
    }

    private void unmanage(X11Client client) {
	client.unintern();
    }

    private void enterNotify(EnterNotify event) {
        X11Client client 
	    = (X11Client)X11Client.intern(this, event.read4(12));
	if (!checkUnmapDestroyEvent(display, client)) {
	    return;
	}
	client.set_input_focus();
	display.check_error();
    }

    private void clientMessage(ClientMessage event) {
	if (event.window_id() == 0) {
	    Atom type = event.type();
	    if (type.name.equals("SERVER_SHUTDOWN")) {
		System.err.println("Xremwin server shutdown detected");
		exit_now = true;
		return;
	    }
	}
	    
	X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());
	Atom type = event.type();
	// client asks to change window state from normal to iconic 
	if (event.format() == 32 /*atom*/) {
	    if (type.name.equals("WM_CHANGE_STATE")
		&& event.wm_data () == Window.WMHints.ICONIC)
	    {
		hide(client);
	    } else {
	        alertUser("1- Unhandled client message: " + type);
	    }
	} else {
	    alertUser("2- Unhandled client message: " + type);
	}
    }

    private void configureRequest(ConfigureRequest event) {
	// client asks to change window configuration
	// @see icccm/sec-4.html#s-4.1.5
	X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());
	if (!checkUnmapDestroyEvent(display, client)) {
	    return;
	}
	/* Should I send a synthetic ConfigureNotify instead of actually
	 * do a configure request on the window? We do not re-parent, and thus,
	 * according to icccm, a ConfigureNotify will be fine. But xterm
	 * relies on a window manager to honour its ConfigureRequest to
	 * configure a window, or it falls back to width = height = 1. A mere
	 * ConfigureNotify seems not sufficient. (Other clients does not
	 * have this problems?)
	 */
        //System.err.println("Enter configureRequest: wid = " + client.getWID());
	client.configure(event.changes());		
	client.set_geometry_cache(event.rectangle());
	client.moveAndSizeWindow();
	display.check_error();
    }

    /**
     * Called by the Xserver (via event dispatch loop) to notify us that
     * some aspect of the window has changed. Could be size, location,...
     */
    private void configureNotify(ConfigureNotify event) {
	X11Client client = (X11Client) X11Client.intern(this, event
		.window_id());
	ConfigureNotify eventFixed = new ConfigureNotifyBugFixed(display,
		event.data);
	int aboveSiblingId = eventFixed.above_sibling_id();
	client.set_geometry_cache(eventFixed.rectangle());
	if (client.attributes == null) {
            client.attributes = client.attributes ();
	}
	if (client.attributes.override_redirect()) {
	    // for override_redirect window (popup window) we don't get ConfigureRequest
	    // so that we should handle ConfigureNotify here also. emacs configure its window
	    // after mapping them. therefor we displayed its popup menus in wrong place.
	    // see Issue 457
	    client.moveAndSizeWindow();	    
	} else {
	    if (aboveSiblingId > 0) {
		X11Client aboveSibling = (X11Client) X11Client.intern(this,
			aboveSiblingId);
		client.restackWindow(aboveSibling, Window.Changes.ABOVE);
	    }
	}
    }

    private void propertyNotify(PropertyNotify event) {
	Atom atom = event.atom(display);
	X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());
	//if (atom == atomWmColormapWindows || atom == atomWmProtocols) {
	//    logger.warning("Unhandled property notify: " + atom);
	//}
	// TODO: The handling of these atoms needs to be implemented.
	// Refer to lg3d-x11 programs/twm/add_window.c for an example.

	switch (atom.id) {  
	    case Atom.WM_HINTS_ID: // TODO any action?
		client.wm_hints();
		break;

	    case Atom.WM_NORMAL_HINTS_ID: // TODO any action?
		client.sizeHints = client.wm_normal_hints();
		break;

	    case Atom.WM_NAME_ID:	
		// Appshare: tell the remote application directly
		// so it can update the title of its GUI windows
		//System.err.println("Window name changed for window " + event.window_id());
		//System.err.println("client.wm_name = " + client.wm_name());
		if (wtl != null) {
		    wtl.setWindowTitle(event.window_id(), client.wm_name());
		}
		break;

	    case Atom.WM_ICON_NAME_ID: // fall through
	    case Atom.WM_TRANSIENT_FOR_ID:
		// ignore (normal window manager should handle these)
		//logger.warning("unhandled atom: " + atom);
		// TODO: implement these
		break;
	}
    }

    private void createNotify(CreateNotify event) {
	X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());
	client.createNotify();
    }

    public void deleteWindow (int wid) {
	X11Client client = (X11Client) X11Client.intern(this, wid);
	if (client != null) {
	    client.delete();
	    // TODO: LG bug 44: for some reason we need to do this twice to destroy
	    // the window
	    client.delete();
	}
    }

    private void mapRequest(MapRequest event) {
	// client asks to change window state from withdrawn to normal/iconic,
	// or from iconic to normal

	X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());
	
        // just ignore MapRequest if we already process one before.
	// Note: this condition will return false if we unmap this window.
	if(client.isMappedBefore()) {
	    return;
	}	
        
	/* THIS LG CODE HAS BEEN DELETED FROM WONDERLAND.
	   IT IS NOT NEEDED.
        // Initialize pseudoRootWindow lazily so that FoundationWinSys
        // gets initialized completely.
        if (pseudoRootWindow == null) {
            initializePseudoRootWindow();
        }
	*/

        // just ignore MapRequest if we already process one before.
	// Note: this condition will return false if we unmap this window.
	if(client.isMappedBefore()) {
	    return;
	}
	
	Atom type = X11WindowManagerHints.getNetWindowType(display, client);
	if (type != null) {
	    X11WindowManagerHints.setNetWindowType(display, client, type);
	    // check if window is supported. we don't map it if not supported.
	    if (!X11WindowManagerHints.isSupportedWinType(display, client)) {
	    	return;
	    }
	    X11WindowManagerHints.setNetWmState(display, client);
	    X11WindowManagerHints.setNetAllowedActions(display, client);
	    X11WindowManagerHints.setWindowFeatures(display, client);
	}

        
	client.geometry();
	
	// Get override_redirect and map_state.
	if (client.attributes == null) {
            client.attributes = client.attributes ();
	}
        
	Window.WMHints wmHints = client.wm_hints();
	// listen to PropertyNotify and EnterNotify
	
        // Check the input member of the WMHints property to see if the
        // WM should manage focus for the window.
	//
	// TODO: According to the version of the ICCCM in Schiefler and Gettys
	// "The X11 Window System", 3rd ed. the input member is not valid
	// unless INPUT_HINT_MASK is set in the flags. However, not all 
	// applications set this flag. For example, xterm and emacs set the
	// flag but gnome-terminal and mozilla do not! I checked how twm deals
	// with this and twm completely ignores the flags word and just looks
	// at the input word. Following the twm approach appears to work but
	// it is disturbing that this doesn't match the ICCCM spec and manifest
	// app behavior. We should dig deeper into this if time permits.
	if ((wmHints != null) && (wmHints.input())) {
	    client.select_input(
                // ICCCM properties (wm name, hints, normal hints)
	        Event.PROPERTY_CHANGE_MASK
	        | Event.ENTER_WINDOW_MASK);	    
	} else {
	    // no input focus will be tracked.
	    client.select_input(
	         // ICCCM properties (wm name, hints, normal hints)
		 Event.PROPERTY_CHANGE_MASK);
	}
	    
	// assume NORMAL if initial_state not specified
	if (wmHints == null
	    || (wmHints.flags () & Window.WMHints.STATE_HINT_MASK) == 0
	    || wmHints.initial_state () == Window.WMHints.NORMAL) {

	    /* Do not do any visible operations on the window such as focusing
	     * and warping pointer, until a window is actually map, ie. 
	     * MapNotify.
	     *
	     * @see #when_map_notify(MapNotify)
	     */

	    client.map();
	    // now mark this window as mapped (only used to ignore other MapRequests
	    // we receive, if application send more than MapWindow requests, before we
	    // reparent it).
	    client.setMappedBefore(true);

	} else { 
	    // must be iconic. client can try to map the window, when Window Manager has
	    // already iconified it. therefore ignore its request.
	    client.state = X11Client.HIDDEN;
	    client.set_wm_state(Window.WMState.ICONIC);
	}
        
        // assume that client work in passive input focus mode.
	// TODO: need to check WM_TAKE_FOCUS atom to know in which mode
	// client work.
        //
        // DJ: removed to fix a problem mentioned in bugid 371.
	// We don't want x windows to capture the focus as soon as they
	// are mapped. This is incompatible with the focus-follows-mouse
	// policy that the DS normally follows.
        // client.set_input_focus();
        
// TODO: this was an attempt at fixing part of 210. The intent was to 
// express interest in ConfigureNotify and CirculateNotify events.
// But Amir pointed out that the way I was doing this is wrong.
// This particular code wipes out any interest expressed in EnterNotify.
// We need to integrate this code into the above code which expresses
// interest in EnterNotify
//	if (!client.attributes.override_redirect ()) {	        
//	    client.select_input(Event.STRUCTURE_NOTIFY_MASK);
//	}
        
	display.check_error();
    }

    private void mapNotify(MapNotify event) {
	final X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());
	//System.err.println("MapNotify: " + client);
	if (!checkUnmapDestroyEvent(display, client)) {
	    return;
	}
	
	// DS can map the window again - resore window from taskbar - so we didn't get
	// mapRequest and only get mapNotify event, therefore we should mark this window
	// as mapped.
	if (!client.isMappedBefore()) {
	    client.setMappedBefore(true);
	}
	
	// Get override_redirect and map_state.
	if (client.attributes == null) {
            client.attributes = client.attributes ();
	}
        
        // now manage the window
        manage(client);
        
        client.raise();
	/* Now and only now sets the window state to NORMAL (except during
	 * initialization). Setting this earlier gives false impression that
	 * the window is mapped, but it does not happen until MapNotify.
	 * Note window.raise () and window.map () do not guarantee the
	 * visibility of a window (due to map request and configure request
	 * redirection of wm). Hence, any operations that depends on visibility
	 * (warp pointer and set input focus) should check window.state.
	 */
	client.state = X11Client.NORMAL;
	client.set_wm_state(Window.WMState.NORMAL);
        display.check_error();
        
        Visual visual = screenVisual.get(client.getVisualID());
        if (client.getWinClass() == X11Client.INPUT_ONLY) {
	    if (client.attributes.override_redirect()) {
                client.initWindow3DRepresentation(false, true, defaultDepth, visual);
            } else {
		// use non-decoration for this also. will be change in feature
		// TODO: implement this feature
		logger.warning("non-override redirect InputOnly not implemented yet");
            }
	} else {  
            if (visual == null) {
                logger.warning("visual for ID " + client.getVisualID() + " not find" );
                return;
            }
	    /* Obsolete for Wonderland 
            if (visual.klass() != Visual.TRUE_COLOR) {
                logger.warning("Other than TrueColor Visual is not yet supported" );
                return;
            }
	    */
            client.initWindow3DRepresentation(
                !client.attributes.override_redirect(), false, defaultDepth, visual);
        }
        
        //
        client.geometry();
        
	client.mapNotify();

        display.check_error();
    }

    private void unmapNotify(UnmapNotify event) {
	/* Unmapped != unmanaged, since it can be iconify-ing (or hiding 
	 * in our case). We unmanage a window when it is destroyed.
	 *
	 * @see #when_destroy_notify(DestroyNotify)
	 */
	final X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());
	//System.err.println("UnmapNotify: " + client);
	// ignore syntatic one here.
	if (event.synthetic) {
	    return;
	}
	client.unmapNotify();

	display.check_error();
        if (display.checkEventTypeWindow(DestroyNotify.CODE, client.id)) {
            return;
        }

	// they withdraw it
	if (client.state != X11Client.HIDDEN) {
	    /* From icccm 4.1.4: For compatibility with obsolete clients, 
	     * window managers should trigger the transition to the Withdrawn 
	     * state on the real UnmapNotify rather than waiting for the 
	     * synthetic one. They should also trigger the transition if they 
	     * receive a synthetic UnmapNotify on a window for which they have 
	     * not yet received a real UnmapNotify.
	     *
	     * Then, what's the use of synthetic UnmapNotify event?
	     */
	    client.state = X11Client.UNMANAGED;
	    client.set_wm_state(Window.WMState.WITHDRAWN);
	    client.change_save_set(true);
	}
	display.check_error();
    }

    private void destroyNotify(DestroyNotify event) {
	final X11Client client 
	    = (X11Client)X11Client.intern(this, event.window_id());	
	
	unmanage(client);
	client.destroyNotify();
        client.state = X11Client.DESTROYED;
    }

    private boolean checkUnmapDestroyEvent(Display display, X11Client client) {
        display.check_error();
        if (display.checkEventTypeWindow(DestroyNotify.CODE, client.id)
            || display.checkEventTypeWindow(UnmapNotify.CODE, client.id)) {
            return false;
        }
        return true;
    }

    public void hide(X11Client client) {
	if (client.state == X11Client.HIDDEN) {
	    return;
	}
	/* Set this state to give hint to {@link
	 * #when_unmap_notify(UnmapNotify)}.
	 *
	 * <p>Do it before <code>client.unmap ()</code>. 
	 */
	client.state = X11Client.HIDDEN;
	client.set_wm_state(Window.WMState.ICONIC);
	client.unmap();
    }

    public void unhide(X11Client client) {
	if (client.state != X11Client.HIDDEN) {
	    return;
	}
	/* Do not set client.state here. Do it right in
	 * {@link #when_map_notify(MapNotify)}.
	 */
	client.map();
    }

    /****
  public void unhide_same_class (Client client) {
    for (Iterator it=clients.iterator (); it.hasNext ();) {
      Client c = (Client) it.next ();

      if (c.state == X11Client.HIDDEN
        && c.class_hint != null
	&& c.class_hint.class_equals (client.class_hint))

	unhide (c);
    }
  }

  public void hide_others (Client client) {
    for (Iterator it=clients.iterator (); it.hasNext ();) {
      Client c = (Client) it.next ();

      if (c.state == NORMAL && c != client) hide (c);
    }
  }


  public void hide_same_class (Client client) {
    for (Iterator it=clients.iterator (); it.hasNext ();) {
      Client c = (Client) it.next ();

      if (c.state == NORMAL
        && c.class_hint != null
	&& c.class_hint.class_equals (client.class_hint))

	hide (c);
    }
  }

  public void grant_all_focus () {
    for (Iterator it=clients.iterator (); it.hasNext ();) {
      grant_focus ((Client) it.next ());
    }
  }


  public void grant_focus (Client client) {
    if (client.state == NO_FOCUS) client.state = NORMAL;
  }

  public void key_dump_info () {
    System.out.println ("input focus: " + focus);
    System.out.println ("mouse at: " + root.pointer ().root_position ());

    if (!argument_present) return; // `dump-basic-info'

    if (argument_negative) {    // `dump-hidden-windows'"
      System.out.println ("all hidden clients: ");

      for (Iterator it=clients.iterator (); it.hasNext ();) {
        Client c = (Client) it.next ();
        
        if (c.state == X11Client.HIDDEN)
          System.out.println (c);
      }

    } else                      // `dump-all-windows'
      System.out.println ("all clients: " + clients);
  }
    ****/
    
    private static final int windowWidthMaxMargin = 0;
    private static final int windowHeightMaxMargin = 16; // FIXME
    
    public Display getDisplay() {
        return display;
    }
    
    public int widTransientFor (int wid) {
	final X11Client client = (X11Client)X11Client.intern(this, wid);	
	Window.PropertyReply reply = client.property(false, atomWmTransientFor,
						     Atom.ANY_PROPERTY_TYPE, 0, 32);
	if (reply.format() == 0) {
	    return 0;
	} 
	    
	gnu.x11.Enum e = reply.items();
	while (e.more()) {
	    // This loop does only one iteration
	    return (int) e.next4();              
	}

	return 0;
    }
}
