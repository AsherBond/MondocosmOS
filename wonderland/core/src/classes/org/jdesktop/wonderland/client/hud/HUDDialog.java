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
package org.jdesktop.wonderland.client.hud;

import java.beans.PropertyChangeListener;

/**
 * A generic dialog type.
 * 
 * @author nsimpson
 */
public interface HUDDialog extends HUDComponent {

    public enum DIALOG_MODE {

        MESSAGE, INPUT
    };

    public enum MESSAGE_TYPE {

        INFO, WARNING, ERROR, QUERY
    };

    public enum BUTTONS {

        NONE, OK, OK_CANCEL
    };

    /**
     * Sets the mode of the dialog
     * HUD dialogs can have two modes: MESSAGE to display an informational
     * message, or INPUT to prompt the user for (text) input
     * @param mode the desired mode: MESSAGE or INPUT
     */
    public void setMode(DIALOG_MODE mode);

    /**
     * Gets the mode of the dialog
     * @return the dialog mode: MESSAGE or INPUT
     */
    public DIALOG_MODE getMode();

    /**
     * Sets the type of the dialog
     * HUD dialogs can have 4 types: INFO, WARNING, ERROR or QUERY. The mode
     * specifies the icon to be displayed on the dialog.
     * @param type
     */
    public void setType(MESSAGE_TYPE type);

    /**
     * Gets the type of the dialog
     * @return the dialog type: INFO, WARNING, ERROR or QUERY
     */
    public MESSAGE_TYPE getType();

    /**
     * Sets which buttons to display: none, OK only, or OK and Cancel
     * @param buttons the buttons to display
     */
    public void setButtons(BUTTONS buttons);

    /**
     * Gets which buttons are displayed
     * @return the displayed buttons: NONE, OK, or OK_CANCEL
     */
    public BUTTONS getButtons();

    /**
     * Sets the string to be displayed on the text field label
     * @param text the string to display
     */
    public void setMessage(String text);

    /**
     * Gets the string displayed on the text field label
     * @return the text field label
     */
    public String getMessage();

    /**
     * Sets the string displayed in the text field
     * @param text the string to display
     */
    public void setValue(String text);

    /**
     * Gets the string entered by the user in the text field
     * @return the text field string
     */
    public String getValue();

    /**
     * Adds a bound property listener to the dialog
     * @param listener a listener for dialog events
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a bound property listener from the dialog
     * @param listener the listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
