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
package org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault;

import com.jme.math.Vector3f;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;

/**
 * Fix for bug 230.
 *
 * This is a kludgy way to get around the fact that under normal circumstances
 * dragging on a FrameHeaderSwing doesn't work. The problem is that the 
 * local-to-world transform was being used in both the picker before 
 * event distribution and also in FrameHeaderSwing after event distribution.
 * Also FrameHeaderSwing was updating the local-to-world transform. The 
 * problem was that the different uses of the local-to-world transform in
 * different time domains coupled by the fact that it was changing caused
 * the dragging of FrameHeaderSwings to jitter.
 *
 * What is needed to fix this is to have FrameHeaderSwing perform the 
 * drag calculations using the original world coordinates of the drag event.
 * But AWT gives us no way to pass this information along with the 
 * events that flow through AWT. So we need to bypass AWT by setting up a queue
 * which contains the info that the drag code needs. We add information from
 * the original AWT event in WindowSwingEmbeddedToolkit and then we read this
 * information in FrameHeaderSwing.
 *
 * @author deronj
 */
@ExperimentalAPI
class FHSEventHook implements WindowSwing.EventHook {

    /** A list of hook infos for incoming PRESS and DRAGGED events. */
    private LinkedList<WindowSwing.EventHookInfo> hookInfos = 
        new LinkedList<WindowSwing.EventHookInfo>();

    private WindowSwing window;

    FHSEventHook (WindowSwing window) {
        this.window = window;
    }

    /** {@inheritDoc} */
    public void specifyHookInfoForEvent (MouseEvent e, WindowSwing.EventHookInfo hookInfo) {

        // Currently cannot drag a window header when it doesn't have control.
        if (!window.getApp().getControlArb().hasControl()) {
            return;
        }

        switch (e.getID()) {
        case MouseEvent.MOUSE_PRESSED:
        case MouseEvent.MOUSE_DRAGGED:
            //logger.severe("************** add hookInfo " + hookInfo);
            hookInfos.addLast(hookInfo);
            break;
        }
    }

    /** 
     * Returns the world coordinates which have been associated with the given mouse event
     * and then forgets this info. Note that calling this method is destructive--the world 
     * coordinate info for the event will no longer be available after this method is invoked.
     */
    WindowSwing.EventHookInfo getHookInfoForEvent (MouseEvent e) {
        WindowSwing.EventHookInfo hookInfo = null;
        try {
            hookInfo = hookInfos.getFirst();
        } catch (NoSuchElementException ex) {}
        //logger.severe("************** get hookInfo " + hookInfo);

        if (hookInfo != null) {
            hookInfos.remove(0);
        }
            
        return hookInfo;
    }
}

