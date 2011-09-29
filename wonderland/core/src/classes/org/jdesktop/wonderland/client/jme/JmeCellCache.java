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
package org.jdesktop.wonderland.client.jme;

import java.util.LinkedList;
import java.util.List;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCacheBasicImpl;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.common.cell.CellID;

/**
*
* @author paulby
*/
/**
 * Concrete implementation of CellCache for the JME Client
 */
public class JmeCellCache extends CellCacheBasicImpl {
    public JmeCellCache(CellClientSession session, ClassLoader loader) {
        super(session, loader,
              session.getCellCacheConnection(),
              session.getCellChannelConnection());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewCell(ViewCell viewCell) {
        super.setViewCell(viewCell);
        ClientContextJME.getViewManager().register(viewCell, getSession());

        // TODO this will not work for federation, need to determine primary view cell in a
        // higher level manager
        ClientContextJME.getViewManager().setPrimaryViewCell(viewCell);
    }

    /**
     * Called when the session is disconnected.  Unload all cells that
     * have been loaded.
     */
    public void unloadAll() {
        // create a list of cells, sorted with children before their
        // parents
        List<CellID> toRemove = new LinkedList<CellID>();

        // do a depth first search of all cells starting from the roots
        for (Cell root : getRootCells()) {
            findChildren(root, toRemove);
            toRemove.add(root.getCellID());
        }

        // remove the environment cell last
        toRemove.add(CellID.getEnvironmentCellID());

        // now remove all cells, starting with children
        for (CellID id : toRemove) {
            unloadCell(id);
        }
    }

    private void findChildren(Cell parent, List<CellID> cellList) {
        for (Cell child : parent.getChildren()) {
            findChildren(child, cellList);
            cellList.add(child.getCellID());
        }
    }

    /**
     * Traverse up the cell hierarchy and return the first Entity
     * @param cell
     * @return
     */
//    private Entity findParentEntity(Cell cell) {
//        if (cell==null)
//            return null;
//
//        CellRenderer rend = cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
//        if (cell!=null && rend!=null) {
//            if (rend instanceof CellRendererJME) {
////                    System.out.println("FOUND PARENT ENTITY on CELL "+cell.getName());
//                return ((CellRendererJME)rend).getEntity();
//            }
//        }
//
//        return findParentEntity(cell.getParent());
//    }

}


