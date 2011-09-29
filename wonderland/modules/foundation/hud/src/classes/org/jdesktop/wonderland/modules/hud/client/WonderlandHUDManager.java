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

import java.awt.Canvas;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDLayoutManager;
import org.jdesktop.wonderland.client.hud.HUDManager;

/**
 * The HUD system allows multiple HUD instances to share a client window.
 * Each HUD has a 2D position and a width and height.
 *
 * A HUDManager manages the placement and visual attributes of all the
 * HUD instances in a given client window.
 *
 * @author nsimpson
 */
public class WonderlandHUDManager extends HUDManager implements HUDEventListener {

    private static final Logger logger = Logger.getLogger(WonderlandHUDManager.class.getName());
    protected String name;
    protected Canvas display;
    protected HUDLayoutManager layout;
    protected boolean visible;

    protected enum VisualState {

        MINIMIZED, NORMAL, MAXIMIZED
    };
    protected final Map<String, HUD> huds = Collections.synchronizedMap(new HashMap());

    public WonderlandHUDManager(Canvas display) {
        this.display = display;

        display.addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent e) {
                Iterator<String> iter = huds.keySet().iterator();
                while (iter.hasNext()) {
                    HUD hud = huds.get(iter.next());
                    hud.setDisplayBounds(e.getComponent().getSize());
                }
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void addHUD(HUD hud) {
        if (hud != null) {
            huds.put(hud.getName(), hud);
            hud.addEventListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeHUD(HUD hud) {
        if ((hud != null) && huds.containsValue(hud)) {
            huds.remove(hud.getName());
            hud.removeEventListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public HUD getHUD(String name) {
        HUD hud = null;
        if (name != null) {
            hud = huds.get(name);
        }
        return hud;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<HUD> getHUDs() {
        Collection c = huds.values();
        return c.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void setLayoutManager(HUDLayoutManager layout) {
        this.layout = layout;
    }

    /**
     * {@inheritDoc}
     */
    public HUDLayoutManager getLayoutManager() {
        return layout;
    }

    /**
     * {@inheritDoc}
     */
    public void relayout() {
        Iterator<String> iter = huds.keySet().iterator();
        while (iter.hasNext()) {
            relayout(huds.get(iter.next()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void relayout(HUD hud) {
        if (layout != null) {
            layout.relayout();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(HUD hud, boolean visible) {
        if (huds.containsValue(hud)) {
            hud.setVisible(visible);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible(HUD hud) {
        boolean hudVisible = false;
        if ((hud != null) && huds.containsValue(hud)) {
            hudVisible = hud.isVisible();
        }
        return hudVisible;
    }

    /**
     * {@inheritDoc}
     */
    public void minimizeHUD(HUD hud) {
        logger.info("minimizing HUD: " + hud);

        if ((hud != null) && huds.containsValue(hud)) {
            if (layout != null) {
                // TODO: implement
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void maximizeHUD(HUD hud) {
        logger.info("maximizing HUD: " + hud);

        if ((hud != null) && huds.containsValue(hud)) {
            if (layout != null) {
                // TODO: implement
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void raiseHUD(HUD hud) {
        logger.info("raising HUD: " + hud);

        if ((hud != null) && huds.containsValue(hud)) {
            if (layout != null) {
                // TODO: implement
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void lowerHUD(HUD hud) {
        logger.info("lowering HUD: " + hud);

        if ((hud != null) && huds.containsValue(hud)) {
            if (layout != null) {
                // TODO: implement
            }
        }
    }

    private void hudVisible(HUD hud) {
        logger.info("showing HUD: " + hud);

        if ((hud != null) && huds.containsValue(hud)) {
            if (layout != null) {
                // TODO: implement
            }
        }
    }

    private void hudInvisible(HUD hud) {
        logger.info("hiding HUD: " + hud);

        if ((hud != null) && huds.containsValue(hud)) {
            if (layout != null) {
                // TODO: implement
            }
        }
    }

    private void hudResized(HUD hud) {
        logger.fine("resizing HUD: " + hud);

        if ((hud != null) && huds.containsValue(hud)) {
            relayout(hud);
        }
    }

    public void HUDObjectChanged(HUDEvent event) {
        if (event.getObject() instanceof HUD) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("HUDManager received HUD event: " + event);
            }

            HUD hud = (HUD) event.getObject();

            switch (event.getEventType()) {
                case ADDED:
                    break;
                case REMOVED:
                    break;
                case APPEARED:
                    hudVisible(hud);
                    break;
                case APPEARED_WORLD:
                    break;
                case DISAPPEARED:
                    hudInvisible(hud);
                    break;
                case DISAPPEARED_WORLD:
                    break;
                case CHANGED_MODE:
                    break;
                case MOVED:
                    break;
                case MOVED_WORLD:
                    break;
                case RESIZED:
                    hudResized(hud);
                    break;
                case MINIMIZED:
                    break;
                case MAXIMIZED:
                    break;
                case ENABLED:
                    break;
                case DISABLED:
                    break;
                case CHANGED_TRANSPARENCY:
                    break;
                case CHANGED_NAME:
                    break;
                case CHANGED_CONTROL:
                    break;
                case CLOSED:
                    break;
                default:
                    logger.info("TODO: handle HUD event type: " + event.getEventType());
                    break;
            }
        }
    }
}
