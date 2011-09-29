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
package org.jdesktop.wonderland.modules.phone.client.cell;


import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D.ButtonId;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import java.net.URL;
import org.jdesktop.wonderland.client.jme.cellrenderer.ModelRenderer;

import org.jdesktop.mtgame.Entity;

import com.jme.scene.Node;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;

/**
 * @author jkaplan
 */
public class PhoneCellRenderer extends ModelRenderer {

    private MyMouseListener listener;

    public PhoneCellRenderer(Cell cell, DeployedModel deployedModel) {
        super(cell, deployedModel);
    }

    @Override
    protected Node createSceneGraph(Entity entity) {
	listener = new MyMouseListener();
	listener.addToEntity(entity);
	return super.createSceneGraph(entity);
    }

    public void removeMouseListener() {
        listener.removeFromEntity(entity);
    }

    class MyMouseListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{MouseEvent3D.class};
        }

        @Override
        public void commitEvent(Event event) {
            if (event instanceof MouseButtonEvent3D) {
                MouseButtonEvent3D buttonEvent = (MouseButtonEvent3D) event;
                if (buttonEvent.isPressed() && buttonEvent.getButton() == ButtonId.BUTTON1) {
                    ((PhoneCell) cell).phoneSelected();
                }
            }
        }
    }

}
