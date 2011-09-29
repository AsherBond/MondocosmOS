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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * A HUD component that displays an image.
 *
 * @author nsimpson
 */
public class HUDImageComponent extends HUDComponent2D {

    private static final Logger logger = Logger.getLogger(HUDImageComponent.class.getName());
    private ImageIcon imageIcon;
    private List<ActionListener> actionListeners;

    public HUDImageComponent() {
        super();
        setDecoratable(false);
    }

    public HUDImageComponent(ImageIcon imageIcon) {
        this();
        setImage(imageIcon);
    }

    /**
     * Sets the image to be displayed by this component
     * @param imageIcon the image to display
     */
    public void setImage(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        if (component == null) {
            component = new JButton(imageIcon);
            ((JButton) component).setBorderPainted(false);
            ((JButton) component).setBorder(null);
            ((JButton) component).addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    logger.info("action performed");
                    notifyActionListeners(new ActionEvent(HUDImageComponent.this, e.getID(), "pressed"));
                }
            });
        } else {
            ((JButton) component).setIcon(imageIcon);
        }
        setBounds(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());
    }

    /**
     * Gets the image to be displayed by this component
     * @return the image
     */
    public ImageIcon getImage() {
        return imageIcon;
    }

    public void addActionListener(ActionListener listener) {
        if (actionListeners == null) {
            actionListeners = Collections.synchronizedList(new ArrayList());
        }
        actionListeners.add(listener);
    }

    public void notifyActionListeners(ActionEvent e) {
        if (actionListeners != null) {
            ListIterator<ActionListener> iter = actionListeners.listIterator();
            while (iter.hasNext()) {
                ActionListener listener = iter.next();
                listener.actionPerformed(e);
            }
            iter = null;
        }
    }
}
