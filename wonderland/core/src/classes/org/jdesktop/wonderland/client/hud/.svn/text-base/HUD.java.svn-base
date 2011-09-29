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

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.hud.HUDDialog.BUTTONS;
import org.jdesktop.wonderland.client.hud.HUDDialog.MESSAGE_TYPE;

/**
 * A HUD is a 2D region of the Wonderland client window on which HUD components
 * can be displayed.
 * 
 * A client may have multiple HUDs. For example, a Status HUD which displays
 * status information about the user's session, and an Audio HUD for audio
 * controls.
 * 
 * A HUD contains HUD components which are visual objects such as a 2D control
 * panel or a representation of a 3D object. HUD components are laid out within
 * a HUD by a HUDLayoutManager.
 *
 * @author nsimpson
 */
public interface HUD extends HUDObject {

    /**
     * Sets the bounds of the display that the HUD will be displayed on.
     * @param displayBounds the width and height of the display
     */
    public void setDisplayBounds(Dimension displayBounds);

    /**
     * Gets the bounds of the display
     * @return the display bounds
     */
    public Dimension getDisplayBounds();

    /**
     * Sets the bounds of the HUD using percentages of the display bounds,
     * where the x and width are expressed as percentages of the display
     * width (in pixels) and y and height are expressed as percentages of
     * the display height. Percentages are in range 0.0 - 1.0.
     * @param scalableBounds the bounds of the HUD in percentages
     */
    public void setScalableBounds(Rectangle2D.Float scalableBounds);

    /**
     * Gets the scalable bounds of the HUD.
     * @return the bounds of the HUD expressed in percentages
     */
    public Rectangle2D.Float getScalableBounds();

    /**
     * Gets whether the HUD bounds are expressed using scalable units.
     * @return the scalable bounds of the HUD
     */
    public boolean hasScalableBounds();

    /**
     * Creates a new HUD component
     * @return a new HUD component
     */
    public HUDComponent createComponent();

    /**
     * Creates a new HUD component bound to a HUD displayable
     * @param displayable a displayable to display in this HUD component
     * @return a new HUD component
     */
    public HUDComponent createComponent(HUDDisplayable displayable);

    /**
     * Creates a new HUD component bound to a Swing component
     * @param component a Swing component to display in this HUD component
     * @return a new HUD component
     */
    public HUDComponent createComponent(JComponent component);

    /**
     * Creates a new HUD component bound to a Swing component and associated
     * with a Cell
     * @param component a Swing component to display in this HUD component
     * @param cell the cell associated with this HUD component
     * @return a new HUD component
     */
    public HUDComponent createComponent(JComponent component, Cell cell);

    /**
     * Creates a new HUD dialog configured as type INFO, with OK and Cancel
     * buttons
     * @param text the text to display in the dialog
     * @return a new HUD dialog component
     */
    public HUDDialog createDialog(String text);

    /**
     * Creates a new HUD dialog with the specified configuration
     * @param text the text to display in the dialog
     * @param type the dialog type: INFO, WARNING, ERROR, or QUERY
     * @param buttons which buttons to display: NONE, OK, OK_CANCEL
     * @return a new HUD dialog component
     */
    public HUDDialog createDialog(String text, MESSAGE_TYPE type, BUTTONS buttons);

    /**
     * Creates a new HUD message configured as type INFO, with no buttons
     * @param message the message to display
     * @return a new HUD message component
     */
    public HUDMessage createMessage(String message);

    /**
     * Creates a new HUD message with the specified configuration
     * @param text the message to display
     * @param type the dialog type: INFO, WARNING, ERROR, or QUERY
     * @param buttons which buttons to display: NONE, OK, OK_CANCEL
     * @return a new HUD message component
     */
    public HUDMessage createMessage(String text, MESSAGE_TYPE type, BUTTONS buttons);

    /**
     * Creates a new HUD button
     * @param label the label to display on the button
     * @return a new HUD button component
     */
    public HUDButton createButton(String label);

    /**
     * Creates a new HUD component to display an image
     * @param imageIcon the image to display
     * @return a new HUD image component
     */
    public HUDComponent createImageComponent(ImageIcon imageIcon);

    /**
     * Adds a HUD component to the HUD
     * @param component the component to add
     */
    public void addComponent(HUDComponent component);

    /**
     * Removes a component from the HUD
     * @param component the component to remove
     */
    public void removeComponent(HUDComponent component);

    /**
     * Gets an iterator that will iterate over the HUD's components
     * @return an iterator for HUD components
     */
    public Iterator<HUDComponent> getComponents();

    /**
     * Gets whether this HUD contains the specified HUD component
     * @return true if the HUD has the component, false otherwise
     */
    public boolean hasComponent(HUDComponent component);

    /**
     * Gets whether this HUD has one or more HUD components
     * @return true if the HUD has HUD components, false otherwise
     */
    public boolean hasComponents();
}
