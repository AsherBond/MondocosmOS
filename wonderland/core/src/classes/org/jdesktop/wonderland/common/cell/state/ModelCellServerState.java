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

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * The ModelCell is a convenience cell that uses a ModelCellComponent to
 * manage and render a deployed model.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="model-cell")
@ServerState
public class ModelCellServerState extends CellServerState implements Serializable {

    /** Default constructor */
    public ModelCellServerState() {
    }
    

    @Override
    public String getServerClassName() {
        return "org.jdesktop.wonderland.server.cell.ModelCellMO";
    }

}
