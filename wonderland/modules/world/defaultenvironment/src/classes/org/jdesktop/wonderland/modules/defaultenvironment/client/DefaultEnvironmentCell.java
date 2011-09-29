/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.defaultenvironment.client;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.EnvironmentCell;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 * Default environment cell
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class DefaultEnvironmentCell extends EnvironmentCell {
    private static final Logger LOGGER =
            Logger.getLogger(DefaultEnvironmentCell.class.getName());

    public DefaultEnvironmentCell(CellID cellID, CellCache cellCache) {
        super (cellID, cellCache);

        logger.warning("Creating default environment cell");
    }

    @Override
    public CellRenderer createCellRenderer(RendererType rendererType) {
        if (rendererType == RendererType.RENDERER_JME) {
            return new DefaultEnvironmentRenderer(this);
        }

        return super.getCellRenderer(rendererType);
    }
}
