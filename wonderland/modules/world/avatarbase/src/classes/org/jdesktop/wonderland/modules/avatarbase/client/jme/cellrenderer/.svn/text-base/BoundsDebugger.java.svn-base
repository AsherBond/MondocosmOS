/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.view.ViewCell;

/**
 * Singleton class for maintaining bounds debugging information
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
class BoundsDebugger {
    private final Map<String, AvatarImiJME> avatarMap =
            new LinkedHashMap<String, AvatarImiJME>();

    private BoundsDebuggerFrame frame;

    private BoundsDebugger() {
        // OWL issue #139: make sure to create the frame on the AWT event
        // thread
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               createFrame();
           }
        });
    }

    public synchronized void add(AvatarImiJME avatar) {
        if (!(avatar.getCell() instanceof ViewCell)) {
            return;
        }

        String username = ((ViewCell)avatar.getCell()).getIdentity().getUsername();
        avatarMap.put(username, avatar);
        if (frame != null) {
            frame.add(username);
        }
    }

    public synchronized void remove(AvatarImiJME avatar) {
        if (!(avatar.getCell() instanceof ViewCell))
            return;

        avatarMap.remove(((ViewCell)avatar.getCell()).getIdentity().getUsername());
    }

    synchronized AvatarImiJME getAvatar(String username) {
        return avatarMap.get(username);
    }

    // OWL issue #139: make sure to initialize the debugger on the AWT thread
    private synchronized void createFrame() {
        frame = new BoundsDebuggerFrame();
        for (String username : avatarMap.keySet()) {
            frame.add(username);
        }
    }

    public static BoundsDebugger getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final BoundsDebugger INSTANCE = new BoundsDebugger();
    }

}
