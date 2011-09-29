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

/*
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
package org.jdesktop.wonderland.modules.hud.client;

import java.util.Iterator;
import java.util.LinkedList;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;

/**
 * Creates and manages on-HUD views of HUD component windows.
 *
 * @author nsimpson
 */
public class HUDView2DDisplayer implements View2DDisplayer {

    // HUD views displayed by this displayer
    private final LinkedList<HUDView2D> views = new LinkedList<HUDView2D>();
    private final HUD hud;

    public HUDView2DDisplayer(HUD hud) {
        super();

        this.hud = hud;
    }

    /**
     * {@inheritDoc}
     */
    public HUDView2D createView(Window2D window) {
        HUDView2D view = new HUDView2D(hud, this, window);

        // invisible by default
        view.setVisibleUser(false);

        if (view != null) {
            views.add(view);
            window.addView(view);
        }

        return view;
    }

    /**
     * {@inheritDoc}
     */
    public void destroyView(View2D view) {
        if (views.remove((HUDView2D) view)) {
            Window2D window = view.getWindow();
            window.removeView(view);
            view.cleanup();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void destroyAllViews() {
        for (View2D view : views) {
            Window2D window = view.getWindow();
            window.removeView(view);
            view.cleanup();
        }
        views.clear();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<? extends View2D> getViews() {
        return views.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup() {
        views.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "HUDView2DDisplayer views: " + views;
    }
}
