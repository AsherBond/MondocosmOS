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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.CameraNode;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * The processor for the camera, the behavior of the camera is controlled by
 * the pluggable CameraControllers.
 * 
 * @author paulby
 */
public class CameraProcessor extends ProcessorComponent {

    protected CameraNode cameraNode;
    private CameraController cameraController;

    private CameraController pendingController = null;
    private CellTransform worldTransform = new CellTransform(new Quaternion(), new Vector3f());
    private boolean pendingViewMoved = false;

    /**
     * Create a CameraProcessor for the specified cameraNode.
     *
     * @param cameraNode the cameraNode this processor will manipulate
     */
    public CameraProcessor(CameraNode cameraNode, CameraController cameraController) {
        this.pendingController = cameraController;
        this.cameraNode = cameraNode;
    }

    public void initialize() {
        // Chained, nothing to do.
    }
    
    /**
     * The view cell has moved, update the camera position. This must be called
     * from a commit or compute thread.
     * 
     * @param worldTransform the worldTransform of the view cell
     */
    public void viewMoved(CellTransform worldTransform) {
        synchronized(this) {
            this.worldTransform = worldTransform.clone(worldTransform);
            pendingViewMoved = true;
        }
    }

    @Override
    public void commit(ProcessorArmingCollection p) {
        if (cameraController!=null) {
//            System.err.println("Calling commit");
            cameraController.commit();
        }
    }

    @Override
    public void compute(ProcessorArmingCollection p) {
        synchronized(this) {
            if (pendingController!=null) {
                if(cameraController!=null) {
                    cameraController.setEnabled(false, null);
                }

                pendingController.setEnabled(true, cameraNode);
                cameraController = pendingController;
                pendingController=null;
            }
        }

        if (cameraController!=null) {
            synchronized(this) {
                if (pendingViewMoved) {
                    cameraController.viewMoved(worldTransform);
                    pendingViewMoved = false;
                }
            }
            cameraController.compute();
        }
    }

    /**
     * Set the camera controller. Note this call will return immediately but
     * the camera controller will not be applied to the system until the
     * next frame is rendererd, the change is applied in the compute method of
     * this processor.
     * @param cameraController
     */
    public void setCameraController(CameraController cameraController) {
        synchronized(this) {
            pendingController = cameraController;
        }
    }

    /**
     * Return the camera controller. This method returns the most recently set
     * cameraController, which may not be the controller being used this frame.
     * CameraController changes are applied in the processor commit method, this
     * method will return the most recently set camera controller, the application
     * of which may still be pending.
     * @return
     */
    public CameraController getCameraController() {
        synchronized (this) {
            if (pendingController!=null)
                return pendingController;
            return cameraController;
        }
    }
}
