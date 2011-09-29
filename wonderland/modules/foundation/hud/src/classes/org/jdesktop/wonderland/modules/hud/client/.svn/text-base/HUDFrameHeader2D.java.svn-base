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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.jdesktop.swingworker.SwingWorker;

/**
 * A 2D frame for a HUDComponent2D.
 *
 * @author nsimpson
 */
public class HUDFrameHeader2D extends HUDComponent2D implements ActionListener, MouseMotionListener {

    private static final Logger logger = Logger.getLogger(HUDFrameHeader2D.class.getName());
    private List<ActionListener> actionListeners;
    private List<MouseListener> mouseListeners;
    private List<MouseMotionListener> mouseMotionListeners;
    // colors for HUD components that don't have a control state
    private static final Color NO_CONTROL_START_COLOR = new Color(137, 137, 137);
    private static final Color NO_CONTROL_END_COLOR = new Color(180, 180, 180);
    // color to use when controlled
    private static final Color CONTROL_COLOR = new Color(0.0f, 0.9f, 0.0f, 1f);
    // color to use when not controlled
    private static final Color NO_CONTROL_COLOR = new Color(0.9f, 0.0f, 0.0f, 1f);
    // text color when controlled
    private static final Color CONTROL_TEXT_COLOR = new Color(0.0f, 0.0f, 0.0f, 1f);
    // text color when not controlled
    private static final Color NO_CONTROL_TEXT_COLOR = new Color(1.0f, 1.0f, 1.0f, 1f);

    public HUDFrameHeader2D(JComponent component) {
        super(component);
        setTextColor(NO_CONTROL_TEXT_COLOR);
        setFrameColor(NO_CONTROL_START_COLOR, NO_CONTROL_END_COLOR);
    }

    public void setControlled(boolean controlled) {
        if (controlled) {
            setFrameColor(CONTROL_COLOR);
            setTextColor(CONTROL_TEXT_COLOR);
        } else {
            setFrameColor(NO_CONTROL_COLOR);
            setTextColor(NO_CONTROL_TEXT_COLOR);
        }
    }

    public void setFrameColor(Color color) {
        if (component instanceof HUDFrameHeader2DImpl) {
            ((HUDFrameHeader2DImpl) component).setFrameColor(color);
        }
    }

    public void setFrameColor(Color startColor, Color endColor) {
        if (component instanceof HUDFrameHeader2DImpl) {
            ((HUDFrameHeader2DImpl) component).setFrameColor(startColor, endColor);
        }
    }

    public void setTextColor(Color color) {
        if (component instanceof HUDFrameHeader2DImpl) {
            ((HUDFrameHeader2DImpl) component).setTextColor(color);
        }
    }

    public void setTitle(String title) {
        if (component instanceof HUDFrameHeader2DImpl) {
            ((HUDFrameHeader2DImpl) component).setTitle(title);
        }
    }

    public String getTitle() {
        String title = null;
        if (component instanceof HUDFrameHeader2DImpl) {
            title = ((HUDFrameHeader2DImpl) component).getTitle();
        }
        return title;
    }

    public void showHUDButton(boolean show) {
        if (component instanceof HUDFrameHeader2DImpl) {
            ((HUDFrameHeader2DImpl) component).showHUDButton(show);
        }
    }

    public void addActionListener(ActionListener listener) {
        if (actionListeners == null) {
            actionListeners = Collections.synchronizedList(new LinkedList());
        }
        actionListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        if (actionListeners != null) {
            actionListeners.remove(listener);
        }
    }

    public void notifyActionListeners(final ActionEvent e) {
        (new SwingWorker<String, Object>() {

            @Override
            public String doInBackground() {
                if (actionListeners != null) {
                    ListIterator<ActionListener> iter = actionListeners.listIterator();
                    while (iter.hasNext()) {
                        ActionListener listener = iter.next();
                        listener.actionPerformed(e);
                    }
                    iter = null;
                }
                return null;
            }
        }).execute();
    }

    public void actionPerformed(ActionEvent e) {
        e.setSource(this);
        notifyActionListeners(e);
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        if (mouseMotionListeners == null) {
            mouseMotionListeners = Collections.synchronizedList(new LinkedList());
        }
        mouseMotionListeners.add(listener);
    }

    public void removeMouseMotionListener(MouseMotionListener listener) {
        if (mouseMotionListeners != null) {
            mouseMotionListeners.remove(listener);
        }
    }

    public void addMouseListener(MouseListener listener) {
        if (mouseListeners == null) {
            mouseListeners = Collections.synchronizedList(new LinkedList());
        }
        mouseListeners.add(listener);
    }

    public void removeMouseListener(MouseListener listener) {
        if (mouseListeners != null) {
            mouseListeners.remove(listener);
        }
    }

    public void notifyMouseMotionListeners(final MouseEvent e) {
        SwingWorker worker = new SwingWorker<String, Object>() {

            @Override
            public String doInBackground() {
                if (mouseMotionListeners != null) {
                    e.setSource(this);
                    ListIterator<MouseMotionListener> iter = mouseMotionListeners.listIterator();
                    while (iter.hasNext()) {
                        MouseMotionListener listener = iter.next();

                        switch (e.getID()) {
                            case MouseEvent.MOUSE_MOVED:
                                listener.mouseMoved(e);
                                break;
                            case MouseEvent.MOUSE_DRAGGED:
                                listener.mouseDragged(e);
                                break;
                            default:
                                break;
                        }
                    }
                    iter = null;
                }
                return null;
            }
        };
        worker.execute();
        try {
            worker.get();
        } catch (Exception ie) {
        }
    }

    public void notifyMouseListeners(final MouseEvent e) {
        (new SwingWorker<String, Object>() {

            @Override
            public String doInBackground() {
                if (mouseListeners != null) {
                    e.setSource(this);
                    ListIterator<MouseListener> iter = mouseListeners.listIterator();
                    while (iter.hasNext()) {
                        MouseListener listener = iter.next();

                        switch (e.getID()) {
                            case MouseEvent.MOUSE_ENTERED:
                                listener.mouseEntered(e);
                                break;
                            case MouseEvent.MOUSE_EXITED:
                                listener.mouseExited(e);
                                break;
                            case MouseEvent.MOUSE_PRESSED:
                                listener.mousePressed(e);
                                break;
                            case MouseEvent.MOUSE_RELEASED:
                                listener.mouseReleased(e);
                                break;
                            case MouseEvent.MOUSE_CLICKED:
                                listener.mouseReleased(e);
                                break;
                            default:
                                break;
                        }
                    }
                    iter = null;
                }
                return null;
            }
        }).execute();
    }

    public void mouseMoved(MouseEvent e) {
        notifyMouseMotionListeners(e);
    }

    public void mouseDragged(MouseEvent e) {
        notifyMouseMotionListeners(e);
    }

    public void mouseEntered(MouseEvent e) {
        notifyMouseListeners(e);
    }

    public void mouseExited(MouseEvent e) {
        notifyMouseListeners(e);
    }

    public void mousePressed(MouseEvent e) {
        notifyMouseListeners(e);
    }

    public void mouseReleased(MouseEvent e) {
        notifyMouseListeners(e);
    }

    public void mouseClicked(MouseEvent e) {
        notifyMouseListeners(e);
    }
}
