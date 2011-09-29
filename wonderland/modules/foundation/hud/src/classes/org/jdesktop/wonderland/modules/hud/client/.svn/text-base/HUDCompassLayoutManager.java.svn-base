/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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

import com.jme.math.Vector2f;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDView;

/**
 * A layout manager which lays out HUD components according to compass point
 * positions.
 *
 * @author nsimpson
 */
public class HUDCompassLayoutManager extends HUDAbsoluteLayoutManager {

    private static final Logger logger = Logger.getLogger(HUDCompassLayoutManager.class.getName());
    private static final int MIN_LEFT_MARGIN = 10;
    private static final int MIN_RIGHT_MARGIN = 10;
    private static final int MIN_TOP_MARGIN = 20;
    private static final int MIN_BOTTOM_MARGIN = 5;
    protected Map<HUDComponent, Vector2f> positionMap;

    public HUDCompassLayoutManager(HUD hud) {
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
        positionMap.remove(component);
    }

    private boolean overlaps(HUDComponent a, HUDComponent b) {
        boolean overlaps = false;
        if ((a != null) && (b != null)) {
            overlaps = a.isVisible() && b.isVisible() &&
                    !a.equals(b) &&
                    a.getBounds().intersects(b.getBounds());
        }
        return overlaps;
    }

    private SortedSet getOverlappers(HUDComponent a) {
        SortedSet overlappers = null;
        Iterator<HUDComponent> iter = hudViewMap.keySet().iterator();
        while (iter.hasNext()) {
            HUDComponent candidate = iter.next();
            if (overlaps(a, candidate)) {
                if (overlappers == null) {
                    overlappers = Collections.synchronizedSortedSet(new TreeSet(new HUDComponentComparator()));
                }
                overlappers.add(candidate);
            }
        }
        return overlappers;
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
            int hudX = hud.getX();
            int hudY = hud.getY();
            int hudWidth = hud.getWidth();
            int hudHeight = hud.getHeight();

            // find the center of the HUD
            float hudCenterX = hudX + hudWidth / 2f;
            float hudCenterY = hudY + hudHeight / 2f;

            if ((component.getPreferredLocation() != Layout.NONE) &&
                    (component.getX() == 0) && (component.getY() == 0)) {
                // just for initial placement of objects using compass layout
                switch (component.getPreferredLocation()) {
                    case NORTH:
                        location.set(hudCenterX - compWidth / 2f, hudHeight - MIN_TOP_MARGIN - compHeight);
                        break;
                    case SOUTH:
                        location.set(hudCenterX - compWidth / 2f, MIN_BOTTOM_MARGIN);
                        break;
                    case WEST:
                        location.set(MIN_LEFT_MARGIN, hudCenterY - compHeight / 2f);
                        break;
                    case EAST:
                        location.set(hudWidth - MIN_RIGHT_MARGIN - compWidth, hudCenterY - compHeight / 2f);
                        break;
                    case CENTER:
                        location.set(hudCenterX - compWidth / 2f, hudCenterY - compHeight / 2f);
                        break;
                    case NORTHWEST:
                        location.set(MIN_LEFT_MARGIN, hudHeight - MIN_TOP_MARGIN - compHeight);
                        break;
                    case NORTHEAST:
                        location.set(hudWidth - MIN_RIGHT_MARGIN - compWidth, hudHeight - MIN_TOP_MARGIN - compHeight);
                        break;
                    case SOUTHWEST:
                        location.set(MIN_LEFT_MARGIN, MIN_BOTTOM_MARGIN);
                        break;
                    case SOUTHEAST:
                        location.set(hudWidth - MIN_RIGHT_MARGIN - compWidth, MIN_BOTTOM_MARGIN);
                        break;
                    default:
                        logger.warning("unhandled layout type: " + component.getPreferredLocation());
                        break;
                }
                // offset from the HUD origin
                location.set(location.x + hudX, location.y + hudY);
            } else {
                // just use the component's current location, but constrain the
                // position of the component to fit the bounds of the HUD
                int x = component.getX();
                int y = component.getY();

                if (component.getDecoratable()) {
                    if (x + compWidth < hudX + MIN_LEFT_MARGIN*4) {
                        // allow component to move off left edge, with at least
                        // MIN_LEFT_MARGIN visible (close button visible)
                        x = (int) (hudX + MIN_LEFT_MARGIN*4 - compWidth);
                    } else if (x > hudX + hudWidth - MIN_RIGHT_MARGIN) {
                        // allow component to move off right edge, with at least
                        // MIN_RIGHT_MARGIN visible
                        x = hudX + hudWidth - MIN_RIGHT_MARGIN;
                    }

                    if (y + compHeight < hud.getY() + MIN_BOTTOM_MARGIN) {
                        // allow component to move off bottom edge, with at least
                        // MIN_BOTTOM_MARGIN visible (header visible)
                        y = (int) (hudY + MIN_BOTTOM_MARGIN - compHeight);
                    } else if (y + compHeight > hudY + hudHeight - MIN_TOP_MARGIN) {
                        // do not allow component to move off top of HUD
                        y = (int) (hudY + hudHeight - MIN_TOP_MARGIN - compHeight);
                    }
                } else {
                    // make sure a non-decoratable component is always
                    // completely visible
                    if (x < hudX + MIN_LEFT_MARGIN) {
                        x = hudX + MIN_LEFT_MARGIN;
                    } else if (x + compWidth > hudX + hudWidth - MIN_RIGHT_MARGIN) {
                        x = (int) (hudX + hudWidth - MIN_RIGHT_MARGIN - compWidth);
                    }

                    if (y < hudY + MIN_BOTTOM_MARGIN) {
                        y = hudY + MIN_BOTTOM_MARGIN;
                    } else if (y + compHeight > hudY + hudHeight - MIN_TOP_MARGIN) {
                        y = (int) (hudY + hudHeight - MIN_TOP_MARGIN - compHeight);
                    }
                }
                location.set(x, y);
            }

            if (location.y + compHeight > hudY + hudHeight) {
                // make sure frame header isn't off top of HUD
                location.set(location.x, hudY + hudHeight - MIN_TOP_MARGIN - compHeight);
            }

            Vector2f currentPosition = positionMap.get(component);
            Vector2f newPosition = new Vector2f((location.x - hudX) / hudWidth, (location.y - hudY) / hudHeight);

            if ((currentPosition == null) || (Math.abs(currentPosition.x - newPosition.x) > 0.03) || (Math.abs(currentPosition.y - newPosition.y) > 0.03)) {
                positionMap.put(component, newPosition);
            }
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
        if (positionPercent == null) {
            logger.warning("no position for component: " + component2D);
            return;
        }
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
