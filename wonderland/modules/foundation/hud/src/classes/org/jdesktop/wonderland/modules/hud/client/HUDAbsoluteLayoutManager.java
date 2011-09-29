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

import com.jme.math.Vector2f;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDLayoutManager;
import org.jdesktop.wonderland.client.hud.HUDView;

/**
 * A HUDAbsoluteLayoutManager lays out components in a 2D rectangular space
 * according to their specified coordinates. Essentially, this is a no-op
 * layout manager; it does no layout management.
 * 
 * @author nsimpson
 */
public class HUDAbsoluteLayoutManager implements HUDLayoutManager {

    private static final Logger logger = Logger.getLogger(HUDAbsoluteLayoutManager.class.getName());
    // a mapping between HUD components and their views
    protected Map<HUDComponent, HUDView> hudViewMap;
    protected HUD hud;

    public HUDAbsoluteLayoutManager() {
        this(null);
    }

    public HUDAbsoluteLayoutManager(HUD hud) {
        this.hud = hud;
        hudViewMap = Collections.synchronizedMap(new HashMap());
    }

    /**
     * {@inheritDoc}
     */
    public void manageComponent(HUDComponent component) {
        hudViewMap.put(component, null);
    }

    /**
     * {@inheritDoc}
     */
    public void unmanageComponent(HUDComponent component) {
        hudViewMap.remove(component);
    }

    /**
     * {@inheritDoc}
     */
    public void addView(HUDComponent component, HUDView view) {
        hudViewMap.put(component, view);
    }

    /**
     * {@inheritDoc}
     */
    public void removeView(HUDComponent component, HUDView view) {
        if (hudViewMap.containsKey(component)) {
            hudViewMap.put(component, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public HUDView getView(HUDComponent component) {
        return hudViewMap.get(component);
    }

    /**
     * {@inheritDoc}
     */
    public Vector2f getLocation(HUDComponent component) {
        Vector2f location = new Vector2f();

        if ((component != null) && (hudViewMap.containsKey(component))) {
            location.set((float) component.getLocation().getX(),
                    (float) component.getLocation().getY());
        }

        return location;
    }

    /**
     * {@inheritDoc}
     */
    public void relayout() {
        // components are positioned absolutely
    }

    /**
     * {@inheritDoc}
     */
    public void relayout(HUDComponent component) {
        // components are positioned absolutely
    }

    public static final class HUDComponentComparator implements Comparator, Serializable {

        public int compare(Object o1, Object o2) {
            int r = 0;
            if ((o1 instanceof HUDComponent) && (o2 instanceof HUDComponent)) {
                HUDComponent c1 = (HUDComponent) o1;
                HUDComponent c2 = (HUDComponent) o2;

                if (c1.getX() < c2.getX()) {
                    r = -1;
                } else if (c1.getX() == c2.getX()) {
                    r = 0;
                } else if (c1.getX() > c2.getX()) {
                    r = 1;
                }
            } else {
                throw new ClassCastException();
            }
            return r;
        }
    }
}
