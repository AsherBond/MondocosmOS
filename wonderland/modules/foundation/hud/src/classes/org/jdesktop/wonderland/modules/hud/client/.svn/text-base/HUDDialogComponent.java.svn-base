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
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.hud.HUDDialog;

/**
 * A simple dialog for requesting a text value from the user.
 *
 * @author nsimpson
 */
public class HUDDialogComponent extends HUDComponent2D implements HUDDialog {

    private static final Logger logger = Logger.getLogger(HUDDialogComponent.class.getName());
    private HUDDialogImpl dialogImpl;

    public HUDDialogComponent() {
        super();
        initializeDialog();
    }

    public HUDDialogComponent(Cell cell) {
        this();
        setCell(cell);
    }

    public HUDDialogComponent(String text) {
        this();
        setMessage(text);
    }

    public HUDDialogComponent(String text, MESSAGE_TYPE type, BUTTONS buttons) {
        this();
        setMessage(text);
        setType(type);
        setButtons(buttons);
    }

    public HUDDialogComponent(String label, String value, Cell cell) {
        this(label);
        setCell(cell);
        setValue(value);
    }

    /**
     * Create a default dialog configured for input
     */
    private void initializeDialog() {
        if (dialogImpl == null) {
            dialogImpl = new HUDDialogImpl();
            setComponent(dialogImpl);
            dialogImpl.setMode(DIALOG_MODE.INPUT);
            dialogImpl.setType(MESSAGE_TYPE.INFO);
            dialogImpl.setButtons(BUTTONS.OK_CANCEL);
            Dimension size = dialogImpl.getPreferredSize();
            setBounds(0, 0, size.width, size.height);
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
    public void setMessage(final String text) {
        dialogImpl.setMessage(text);
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
    public void setValue(final String text) {
        dialogImpl.setValue(text);
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return dialogImpl.getValue();
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
