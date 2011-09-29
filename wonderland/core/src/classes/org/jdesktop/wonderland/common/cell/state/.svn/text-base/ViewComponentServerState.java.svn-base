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
package org.jdesktop.wonderland.common.cell.state;

import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A special cell component server state object that represents the view cell
 * transform (origin, rotation, scaling) that created the Cell.
 * <p>
 * There is no corresponding server or client-side component object. This state
 * is handled as a special case by the cell.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlTransient
public class ViewComponentServerState extends CellComponentServerState {

    private CellTransform cellTransform = null;

    /** Constructor, takes the Cell Transform */
    public ViewComponentServerState(CellTransform cellTransform) {
        this.cellTransform = cellTransform;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerComponentClassName() {
        return null;
    }

    public CellTransform getCellTransform() {
        return cellTransform;
    }

    public void setCellTransform(CellTransform cellTransform) {
        this.cellTransform = cellTransform;
    }

}
