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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;

/**
 * A manager which manages a mapping of HUD components on one HUD to icons
 * on another.
 *
 * @author nsimpson
 */
public class WonderlandHUDIconManager extends WonderlandHUDComponentManager {

    private static final Logger logger = Logger.getLogger(WonderlandHUDIconManager.class.getName());
    // a mapping from HUD components on a HUD to icons on the icon HUD
    protected Map<HUDComponent, HUDImageComponent> hudIconMap;

    public WonderlandHUDIconManager(HUD iconHUD) {
        super(iconHUD);
        hudIconMap = Collections.synchronizedMap(new HashMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComponent(final HUDComponent component) {
        if (hud.hasComponent(component)) {
            // this is a component on the icon HUD
            super.addComponent(component);
            component.setVisible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeComponent(HUDComponent component) {
        if (hud.hasComponent(component)) {
            // this is an icon on the icon HUD
            super.removeComponent(component);
        } else {
            // this is a component on the window HUD
            // remove the icon from the icon HUD
            HUDComponent icon = hudIconMap.get(component);
            hud.removeComponent(icon);
        }
    }

    @Override
    protected void componentVisible(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentVisible(component);
        }
    }

    @Override
    protected void componentInvisible(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentInvisible(component);
        }
    }

    @Override
    protected void componentWorldVisible(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentWorldVisible(component);
        }
    }

    @Override
    protected void componentWorldInvisible(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentWorldInvisible(component);
        }
    }

    @Override
    protected void componentMoved(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentMoved(component);
        }
    }

    @Override
    protected void componentMovedWorld(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentMovedWorld(component);
        }
    }

    @Override
    protected void componentResized(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentResized(component);
        }
    }

    @Override
    protected void componentViewChanged(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentViewChanged(component);
        }
    }

    @Override
    protected void componentMinimized(final HUDComponent2D component) {
        HUDImageComponent icon = null;

        if (!hudIconMap.containsKey(component) && !hudStateMap.containsKey(component)) {
            // there's no icon for this component, and it's not an icon in the
            // icon HUD

            // create an icon for this component
            ImageIcon imageIcon = component.getIcon();
            if (imageIcon == null) {
                imageIcon = new ImageIcon(getClass().getResource(DEFAULT_HUD_ICON));
            }
            icon = (HUDImageComponent) hud.createImageComponent(imageIcon);
            icon.setDecoratable(false);
            icon.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    component.setMaximized();
                }
            });
            // remember this component-icon mapping
            hudIconMap.put(component, icon);

            // add the icon to the icon HUD
            hud.addComponent(icon);
        } else {
            icon = hudIconMap.get(component);
            // display the icon
            icon.setVisible(true);
        }
    }

    @Override
    protected void componentMaximized(HUDComponent2D component) {
        if (!hudStateMap.containsKey(component)) {
            // show the window component
            component.setMaximized();

            // hide the icon component
            HUDComponent icon = hudIconMap.get(component);
            if (icon != null) {
                icon.setVisible(false);
            }
        }
    }

    @Override
    protected void componentClosed(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentClosed(component);
        }
    }

    @Override
    protected void componentTransparencyChanged(HUDComponent2D component) {
        if (hudStateMap.containsKey(component)) {
            super.componentTransparencyChanged(component);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void HUDObjectChanged(HUDEvent event) {
        super.HUDObjectChanged(event);
    }
}
