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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDView;

/**
 * A layout manager which lays out HUD components from left to right.
 *
 * @author nsimpson
 */
public class HUDFlowLayoutManager extends HUDAbsoluteLayoutManager {

    private static final Logger logger = Logger.getLogger(HUDFlowLayoutManager.class.getName());
    private static final int MIN_LEFT_MARGIN = 10;
    private static final int MIN_RIGHT_MARGIN = 10;
    private static final int MIN_TOP_MARGIN = 20;
    private static final int MIN_BOTTOM_MARGIN = 10;
    private static final int X_GAP = 10;
    private static final int Y_GAP = 10;
    protected Map<HUDComponent, Vector2f> positionMap;
    private int nextX = MIN_LEFT_MARGIN;
    private int nextY = MIN_BOTTOM_MARGIN;

    public HUDFlowLayoutManager(HUD hud) {
        super(hud);
        positionMap = Collections.synchronizedMap(new HashMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(HUDComponent component, HUDView view) {
        super.addView(component, view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeView(HUDComponent component, HUDView view) {
        super.removeView(component, view);
        if (positionMap.containsKey(component)) {
            positionMap.remove(component);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2f getLocation(HUDComponent component) {
        Vector2f location = new Vector2f();

        if ((component != null) && (hudViewMap.get(component) != null)) {
            // get HUD component's view width
            HUDView2D view2d = (HUDView2D) hudViewMap.get(component);
            float compWidth = view2d.getDisplayerLocalWidth();
            float compHeight = view2d.getDisplayerLocalHeight();

            // get the bounds of the HUD containing the component
            int hudWidth = hud.getWidth();
            int hudHeight = hud.getHeight();

            location = positionMap.get(component);
            if (location == null) {
                // component is new, place it in the next position in the linear flow
                location = new Vector2f(nextX, nextY);
                // next free position in flow layout
                nextX += compWidth + X_GAP;
            }

            int x = (int) location.x;
            int y = (int) location.y;

            if (x < hud.getX() + MIN_LEFT_MARGIN) {
                x = hud.getX() + MIN_LEFT_MARGIN;
            } else if (x + compWidth > hud.getX() + hudWidth - MIN_RIGHT_MARGIN) {
                // start a new row, above this one
                x = MIN_LEFT_MARGIN;
                y += compHeight + Y_GAP;
            }
            if (y < hud.getY() + MIN_BOTTOM_MARGIN) {
                y = hud.getY() + MIN_BOTTOM_MARGIN;
            } else if (y + compHeight > hud.getY() + hudHeight - MIN_TOP_MARGIN) {
                y = (int) (hud.getY() + hudHeight - MIN_TOP_MARGIN - compHeight);
            }

            // adjusted location to fit bounds of HUD
            location.set(x, y);

            // remember the position of the component
            positionMap.put(component, location);
        }

        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relayout() {
        Iterator<HUDComponent> iter = hudViewMap.keySet().iterator();
        while (iter.hasNext()) {
            relayout(iter.next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relayout(HUDComponent component) {
        int hudWidth = hud.getWidth();
        int hudHeight = hud.getHeight();

        HUDComponent2D component2D = (HUDComponent2D) component;
        Vector2f positionPercent = positionMap.get(component2D);
        float compX = hud.getX() + positionPercent.x * hudWidth;
        float compY = hud.getY() + positionPercent.y * hudHeight;

        HUDView2D view = (HUDView2D) hudViewMap.get(component2D);
        float viewWidth = view.getDisplayerLocalWidth();
        float viewHeight = view.getDisplayerLocalHeight();

        if (hud.getX() + hudWidth - (compX + viewWidth) < MIN_RIGHT_MARGIN) {
            // component bumped right edge of HUD, move it to be visible
            compX = hud.getX() + hudWidth - viewWidth - MIN_RIGHT_MARGIN;
        }
        if (compX < hud.getX() + MIN_LEFT_MARGIN) {
            // component bumped left edge of HUD
            compX = hud.getX() + MIN_LEFT_MARGIN;
        }
        if (compY < hud.getY() + MIN_BOTTOM_MARGIN) {
            // component bumped bottom edge of HUD
            compY = hud.getY() + MIN_BOTTOM_MARGIN;
        }
        if (hud.getY() + hudHeight - (compY + viewHeight) < MIN_TOP_MARGIN) {
            // component bumped top edge of HUD
            compY = hud.getY() + hudHeight - viewHeight - MIN_TOP_MARGIN;
        }

        component2D.setLocation((int) compX, (int) compY);
    }
}
