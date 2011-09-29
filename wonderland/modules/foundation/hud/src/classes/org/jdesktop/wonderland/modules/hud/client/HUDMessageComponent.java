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

import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUDDialog.BUTTONS;
import org.jdesktop.wonderland.client.hud.HUDDialog.DIALOG_MODE;
import org.jdesktop.wonderland.client.hud.HUDDialog.MESSAGE_TYPE;
import org.jdesktop.wonderland.client.hud.HUDMessage;

/**
 * A dialog for displaying a message on the HUD.
 *
 * @author nsimpson
 */
public class HUDMessageComponent extends HUDComponent2D implements HUDMessage {

    private static final Logger logger = Logger.getLogger(HUDMessageComponent.class.getName());
    private HUDDialogImpl dialogImpl;

    public HUDMessageComponent() {
        super();
        setDecoratable(false);
        initializeDialog();
    }

    public HUDMessageComponent(String message) {
        this();
        setMessage(message);
    }

    public HUDMessageComponent(String message, MESSAGE_TYPE type, BUTTONS buttons) {
        this();
        setMessage(message);
        setType(type);
        setButtons(buttons);
    }

    /**
     * Create the dialog components
     */
    private void initializeDialog() {
        if (dialogImpl == null) {
            dialogImpl = new HUDDialogImpl();
            dialogImpl.setMode(DIALOG_MODE.MESSAGE);
            dialogImpl.setButtons(BUTTONS.NONE);
            Dimension size = dialogImpl.getPreferredSize();
            setBounds(0, 0, size.width, size.height);
            setComponent(dialogImpl);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setMode(DIALOG_MODE mode) {
        dialogImpl.setMode(mode);
    }

    /**
     * {@inheritDoc}
     */
    public DIALOG_MODE getMode() {
        return dialogImpl.getMode();
    }

    /**
     * {@inheritDoc}
     */
    public void setType(MESSAGE_TYPE type) {
        dialogImpl.setType(type);
    }

    /**
     * {@inheritDoc}
     */
    public MESSAGE_TYPE getType() {
        return dialogImpl.getType();
    }

    /**
     * {@inheritDoc}
     */
    public void setButtons(BUTTONS buttons) {
        dialogImpl.setButtons(buttons);
    }

    /**
     * {@inheritDoc}
     */
    public BUTTONS getButtons() {
        return dialogImpl.getButtons();
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(final String message) {
        dialogImpl.setMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage() {
        return dialogImpl.getMessage();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addPropertyChangeListener(final PropertyChangeListener listener) {
        dialogImpl.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
        dialogImpl.removePropertyChangeListener(listener);
    }
}
