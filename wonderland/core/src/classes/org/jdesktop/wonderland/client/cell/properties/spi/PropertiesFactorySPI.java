/**
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
package org.jdesktop.wonderland.client.cell.properties.spi;

import javax.swing.JPanel;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An interface implemented by cells that allow their properties to be edited
 * by a GUI properties panel. This class must be annotated with
 * @PropertiesFactory and is displayed by a dialog container.
 * <p>
 * The GUI properties panel has a well-defined life-cycle. When a Cell is
 * selected, the open() method for all of its property sheets are invoked. After
 * users have made changes to properties, its apply() or restore() method will
 * be invoked. Finally, when another Cell is selected, its close() method is
 * invoked.
 * <p>
 * The specific actions the PropertiesFactorySPI class takes when these four
 * methods are invoked is implementation specific.
 * <p>
 * The open() method is invoked when the Cell is first selected and its
 * properties are to be displayed. It is also invoked after apply() is called
 * and the properties editor has refreshed its copy of the current Cell's
 * server state.
 *
 * It is the responsibility of the class that implements this interface to
 * properly update the state of the Cell when apply() is invoked. It can either
 * interact with the Cell interface directly, or update the state of the cell
 * via methods on CellPropertiesEditor.
 * <p>
 * A PropertiesFactorySPI class make also choose to immediately update the values
 * of the Cell when the GUI is changed, and not only when apply() is invoked.
 * <p>
 * When the restore() method is called, the PropertiesFactorySPI class should
 * revert all values in the state back to the values at the last apply(). When
 * the close() method is invoked, the PropertiesFactorySPI class should revert
 * all values and perform any necessary cleanup.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public interface PropertiesFactorySPI {

    /**
     * Returns a human-readable display name of the panel. This name will be
     * used to identify the panel amongst other panel in the edit dialog.
     *
     * @return The name of the configuration panel
     */
    public String getDisplayName();

    /**
     * Sets the cell properties editor containing this individual property
     * sheet.
     *
     * @param editor A CellProperties Editor object
     */
    public void setCellPropertiesEditor(CellPropertiesEditor editor);

    /**
     * Returns a panel to be used in the properties editing dialog.
     *
     * @return A JPanel
     */
    public JPanel getPropertiesJPanel();

    /**
     * Tells the proeprties GUI panel that is is about to be displayed and it
     * should refresh its values against the currently set values in the state
     * of the Cell. This method is typically called when a Cell is first
     * selected or after an appply(). Therefore, it may be invoked multiple
     * times without any intervening close() method invocations.
     */
    public void open();

    /**
     * Tells the properties GUI panel that it is being closed. The panel should
     * revert any intermediate changes it made to the state of the Cell after
     * the last time apply() was invoked. It should also perform any necessary
     * cleanup (e.g. remove listeners on the current Cell) before a new Cell's
     * properties are displayed.
     */
    public void close();

    /**
     * Instructs the GUI to refresh its values against the currently set values
     * in the state of the Cell or the last known "original" state.
     */
    public void restore();

    /**
     * Applies the values current set in the properties GUI panel to the state
     * of the Cell.
     */
    public void apply();
}
