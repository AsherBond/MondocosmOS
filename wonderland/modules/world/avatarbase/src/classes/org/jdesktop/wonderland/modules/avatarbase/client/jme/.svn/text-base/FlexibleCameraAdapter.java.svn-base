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
package org.jdesktop.wonderland.modules.avatarbase.client.jme;


import com.jme.scene.CameraNode;
import imi.camera.AbstractCameraState;
import imi.camera.CameraModel;
import imi.scene.PMatrix;
import java.awt.event.InputEvent;
import java.util.logging.Logger;
import javolution.util.FastList;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassFocusListener;
import org.jdesktop.wonderland.client.jme.CameraController;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.input.InputEvent3D;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * This class acts as an adapter from the imi.camera.FlexibleCameraProcessor
 * @author Ronald E Dahlgren
 */
public class FlexibleCameraAdapter implements CameraController {
    private static final Logger logger = Logger.getLogger(FlexibleCameraAdapter.class.getName());
    // Behavior
    private CameraModel model;
    private AbstractCameraState state;
    // jME cam node
    private CameraNode cameraNode = null;
    // Our transform
    private double deltaTime;
    private double oldTime;

    // Get input via this class
    private FCAInputListener inputListener;

    /** Collection of input events **/
    private final FastList<InputEvent> inputEvents = new FastList<InputEvent>();
    /** Our transform! **/
    private final PMatrix transform = new PMatrix();
    /** World manager **/
    private final WorldManager wm;

    public FlexibleCameraAdapter(CameraModel model, AbstractCameraState state) {
        this.model = model;
        this.state = state;
        inputListener = new FCAInputListener();
        wm = ClientContextJME.getWorldManager();
    }

    public void setEnabled(boolean enabled, CameraNode cameraNode) {
        deltaTime = oldTime = System.nanoTime() / 1000000000.0;
        inputEvents.clear();

        if (enabled) {
            ClientContextJME.getInputManager().addGlobalEventListener(inputListener);
            this.cameraNode = cameraNode;
        } else {
            ClientContextJME.getInputManager().removeGlobalEventListener(inputListener);
            this.cameraNode = null;
        }
    }

    private void putEvent(InputEvent event) {
        synchronized(this) {
            inputEvents.add(event);
        }
    }
    public void compute() {
        double newTime = System.nanoTime() / 1000000000.0;
        deltaTime = (newTime - oldTime);
        oldTime = newTime;

        if (model != null)
        {    
            synchronized(this)
            {
                model.handleInputEvents(state, inputEvents.toArray());
                inputEvents.clear();
            }
            model.update(state, (float)deltaTime);
            model.determineTransform(state, transform);
        }
    }


    public void commit() {
        if (cameraNode != null) {
            cameraNode.setLocalTranslation(transform.getTranslation());
            cameraNode.setLocalRotation(transform.getRotationJME());
            wm.addToUpdateList(cameraNode);
        }
    }

    public void viewMoved(CellTransform worldTransform) {
        // Do nothing!
    }

    public void setBehavior(CameraModel model, AbstractCameraState state) {
        if (model.isStateClassValid(state.getClass()) == false)
            throw new RuntimeException("Incompatible state");
        this.model = model;
        this.state = state;
    }
    
    public AbstractCameraState getState() {
        return state;
    }

    /**
     * Private class to pick up events
     */
    private class FCAInputListener extends EventClassFocusListener {
        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] { KeyEvent3D.class, MouseEvent3D.class };
        }

        @Override
        public void computeEvent(Event event) {
            if (event instanceof InputEvent3D) {
                InputEvent awtEvent = ((InputEvent3D)event).getAwtEvent();
                putEvent(awtEvent);
            }
        }
    }
}
