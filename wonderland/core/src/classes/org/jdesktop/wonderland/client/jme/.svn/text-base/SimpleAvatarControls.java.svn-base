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

import com.jme.math.Vector3f;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import org.jdesktop.mtgame.AWTInputComponent;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.MovableAvatarComponent;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassFocusListener;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.common.cell.CellTransform;


/**
 * This is simple camera control which mimics the typical first person shooter
 * camera control
 * 
 * @author Doug Twilleager
 * @deprecated Replaced with AvatarControls
 */
public class SimpleAvatarControls extends ViewControls {
    /**
     * The arming conditions for this processor
     */
    private ProcessorArmingCollection collection = null;
    
    /**
     * First, some common variables
     */
    private int lastMouseX = -1;
    private int lastMouseY = -1;
    
    /**
     * The cumulative rotation in Y and X
     */
    private float rotY = 0.0f;
    private float rotX = 0.0f;
    
    /**
     * This scales each change in X and Y
     */
    private float scaleX = 0.7f;
    private float scaleY = 0.7f;
    private float walkInc = 0.5f;
    
    /**
     * States for movement
     */
    private static final int STOPPED = 0;
    private static final int WALKING_FORWARD = 1;
    private static final int WALKING_BACK = 2;
    private static final int STRAFE_LEFT = 3;
    private static final int STRAFE_RIGHT = 4;
    
    /**
     * Our current state
     */
    private int state = STOPPED;
    
    /**
     * Our current position
     */
    private Vector3f position = new Vector3f(0.0f, 10.0f, -30.0f);
        
    /**
     * The Y Axis
     */
    private Vector3f yDir = new Vector3f(0.0f, 1.0f, 0.0f);
    
    /**
     * Our current forward direction
     */
    private Vector3f fwdDirection = new Vector3f(0.0f, 0.0f, 1.0f);
    private Vector3f rotatedFwdDirection = new Vector3f();
    
    /**
     * Our current side direction
     */
    private Vector3f sideDirection = new Vector3f(1.0f, 0.0f, 0.0f);
    private Vector3f rotatedSideDirection = new Vector3f();
    
    /**
     * The quaternion for our rotations
     */
    private Quaternion quaternion = new Quaternion();
    
    /**
     * This is used to keep the direction rotated
     */
    private Matrix3f directionRotation = new Matrix3f();
    
    /**
     * The Node to modify
     */
    private Cell target = null;
    private MovableAvatarComponent movableComponent = null;
    
    /**
     * The WorldManager
     */
    private WorldManager worldManager = null;
    
    private boolean updateRotations = false;
    
    private AWTInputComponent listener;
    
    private final LinkedList<Event> events = new LinkedList();

    /**
     * The global event listener
     */
    private final AvatarEventListener globalListener = new AvatarEventListener();

    /**
     * The default constructor
     */
    public SimpleAvatarControls(Cell viewCell, WorldManager wm) {
        super();
        target = viewCell;
        movableComponent = (MovableAvatarComponent) viewCell.getComponent(MovableComponent.class);
        worldManager = wm;

        // Set initial position and orientation from cell transform
        CellTransform worldTransform = viewCell.getWorldTransform();
        position = worldTransform.getTranslation(position);
        float[] angles = worldTransform.getRotation(null).toAngles(null);

        rotX = (float) Math.toDegrees(angles[0]);
        rotY = (float) Math.toDegrees(angles[1]);

//        System.out.println("********** "+angles[0]+", "+angles[1]+", "+angles[2]);

        // Setup the rotated vectors
        directionRotation.fromAngleAxis(rotY*(float)Math.PI/180.0f, yDir);
        directionRotation.mult(fwdDirection, rotatedFwdDirection);
        directionRotation.mult(sideDirection, rotatedSideDirection);
        quaternion.fromAngles(rotX*(float)Math.PI/180.0f, rotY*(float)Math.PI/180.0f, 0.0f);
        
        collection = new ProcessorArmingCollection(this);
        collection.addCondition(new NewFrameCondition(this));

        setRunInRenderer(true);

//        System.out.println("[SimpleAvatarControls] creating " + this);
    }
    
    @Override
    public void initialize() {
        setArmingCondition(collection);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

//        System.out.println("[SimpleAvatarControls] set enabled " + enabled + " " + this);

        if (enabled) {
            ClientContext.getInputManager().addGlobalEventListener(globalListener);
        } else {
            ClientContext.getInputManager().removeGlobalEventListener(globalListener);
        }
    }

    @Override
    public void compute(ProcessorArmingCollection collection) {
        synchronized(events) {
            updateRotations = false;
        
            int onmask = MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK;
            for (Event e : events) {
                if (e instanceof MouseButtonEvent3D) {
                    MouseButtonEvent3D mbe = (MouseButtonEvent3D)e;
                    MouseEvent awt = (MouseEvent) mbe.getAwtEvent();
                    if (awt.getID()== MouseEvent.MOUSE_PRESSED && (awt.getModifiersEx() & onmask)==onmask ) {
                        lastMouseX = awt.getX();
                        lastMouseY = awt.getY();
                    }
                } else if (e instanceof MouseDraggedEvent3D) {
                    MouseEvent3D me = (MouseEvent3D)e;
                    MouseEvent awtMe = (MouseEvent)me.getAwtEvent();
                    if (awtMe.getID() == MouseEvent.MOUSE_DRAGGED && (awtMe.getModifiersEx() & onmask)==onmask ) {
                        processRotations(me);
                        updateRotations = true;
                    }
                } else if (e instanceof KeyEvent3D) {
                    KeyEvent3D ke = (KeyEvent3D)e;
                    processKeyEvent(ke);
                }
            }
            events.clear();
        }
        
        if (updateRotations) {
            directionRotation.fromAngleAxis(rotY*(float)Math.PI/180.0f, yDir);
            directionRotation.mult(fwdDirection, rotatedFwdDirection);
            directionRotation.mult(sideDirection, rotatedSideDirection);
//            System.out.println("Forward: " + rotatedFwdDirection);
            quaternion.fromAngles(rotX*(float)Math.PI/180.0f, rotY*(float)Math.PI/180.0f, 0.0f);
        }
        
        updatePosition();
    }
    
    private void processRotations(MouseEvent3D me) {
        int deltaX = 0;
        int deltaY = 0;
        int currentX = 0;
        int currentY = 0;
        currentX = ((MouseEvent)me.getAwtEvent()).getX();
        currentY = ((MouseEvent)me.getAwtEvent()).getY();

        if (lastMouseX == -1) {
            // First time through, just initialize
            lastMouseX = currentX;
            lastMouseY = currentY;
        } else {
            deltaX = currentX - lastMouseX;
            deltaY = currentY - lastMouseY;
            deltaX = -deltaX;

            rotY += (deltaX * scaleX);
            rotX += (deltaY * scaleY);
            if (rotX > 60.0f) {
                rotX = 60.0f;
            } else if (rotX < -60.0f) {
                rotX = -60.0f;
            }
            lastMouseX = currentX;
            lastMouseY = currentY;
        }
    }
    
    
    private void processKeyEvent(KeyEvent3D ke3D) {
        KeyEvent ke = (KeyEvent)ke3D.getAwtEvent();
        if (ke.getID() == KeyEvent.KEY_PRESSED) {
            if (ke.getKeyCode() == KeyEvent.VK_W) {
                state = WALKING_FORWARD;
            }
            if (ke.getKeyCode() == KeyEvent.VK_S) {
                state = WALKING_BACK;
            }
            if (ke.getKeyCode() == KeyEvent.VK_A) {
                state = STRAFE_LEFT;
            }
            if (ke.getKeyCode() == KeyEvent.VK_D) {
                state = STRAFE_RIGHT;
            }
        }
        if (ke.getID() == KeyEvent.KEY_RELEASED) {
            if (ke.getKeyCode() == KeyEvent.VK_W ||
                ke.getKeyCode() == KeyEvent.VK_S ||
                ke.getKeyCode() == KeyEvent.VK_A ||
                ke.getKeyCode() == KeyEvent.VK_D) {
                state = STOPPED;
            }
        }
    }
    
    private void updatePosition() {
        switch (state) {
            case WALKING_FORWARD:
                position.x += (walkInc * rotatedFwdDirection.x);
                position.y += (walkInc * rotatedFwdDirection.y);
                position.z += (walkInc * rotatedFwdDirection.z);
                break;
            case WALKING_BACK:
                position.x -= (walkInc * rotatedFwdDirection.x);
                position.y -= (walkInc * rotatedFwdDirection.y);
                position.z -= (walkInc * rotatedFwdDirection.z);
                break;
            case STRAFE_LEFT:
                position.x += (walkInc * rotatedSideDirection.x);
                position.y += (walkInc * rotatedSideDirection.y);
                position.z += (walkInc * rotatedSideDirection.z);
                break;
            case STRAFE_RIGHT:
                position.x -= (walkInc * rotatedSideDirection.x);
                position.y -= (walkInc * rotatedSideDirection.y);
                position.z -= (walkInc * rotatedSideDirection.z);
                break;  
        }
    }
    /**
     * The commit methods
     */
    @Override
    public void commit(ProcessorArmingCollection collection) {
        if (state!=STOPPED || updateRotations) {
//            System.err.println("localMoveRequest "+position+"  "+this);
            movableComponent.localMoveRequest(new CellTransform(quaternion, position));
        }
    }

    public void attach(Cell cell) {
        
    }
    
    /**
     * The Event listener for the avatar. This is attached as a global event
     * listener.
     */
    class AvatarEventListener extends EventClassFocusListener {
        @Override
        public Class[] eventClassesToConsume () {
            return new Class[] { KeyEvent3D.class, MouseEvent3D.class };
        }

        @Override
        public void commitEvent (Event event) {
//            System.out.println("evt " +event);
            synchronized(events) {
                events.add(event);
            }
        }
        
    }
}
