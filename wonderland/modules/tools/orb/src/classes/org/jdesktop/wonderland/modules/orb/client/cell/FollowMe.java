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
package org.jdesktop.wonderland.modules.orb.client.cell;

import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.client.cell.MovableComponent;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FollowMe extends Thread {

    private static final Logger logger =
            Logger.getLogger(FollowMe.class.getName());

    private static final float MASS = 1.0f;
    private static final float MAX_SPEED = 0.5f;
    private static final float DAMPING = 9.0f;
    private static final float SPRING = 5.0f;
    private static final float EPSILON = 0.01f;
    private static final float LONG_DISTANCE = 10f;
        
    private Vector3f currentPosition;
    private Vector3f targetPosition;
    
    private long oldTime;
    private Vector3f oldVelocity;
    private Vector3f oldAcceleration;
    
    private Quaternion rotation;

    private  MovableComponent movableComp;

    private FollowMeListener listener;

    private boolean animate = true;

    public FollowMe(MovableComponent movableComp, Vector3f initialPosition,
	    FollowMeListener listener) {

	this.movableComp = movableComp;
	this.listener = listener;

        currentPosition = new Vector3f(initialPosition);
        targetPosition = new Vector3f(initialPosition);
        
        oldVelocity = new Vector3f();
        oldAcceleration = new Vector3f();

	start();
    }
    
    public void setAnimate(boolean animate) {
	this.animate = animate;
    }

    public void jumpToTargetPosition(Vector3f newTarget) {
        //Add a little hop
        oldVelocity.addLocal(new Vector3f(0.0f, 3.0f, 0.0f));
        setTargetPosition(newTarget);
    }
    
    public void setTargetPosition(Vector3f targetPosition) {
        setTargetPosition(targetPosition, null);
    }

    public void setTargetPosition(Vector3f targetPosition, 
	    Quaternion rotation) {

	this.targetPosition = targetPosition;
	this.rotation = rotation;

        oldTime = System.currentTimeMillis();

	logger.finer("Set targetPosition " + targetPosition
	    + " rotation " + rotation);

        Vector3f positionError  = new Vector3f(targetPosition);
            positionError.subtractLocal(currentPosition);
	
	float length = positionError.length();

	logger.fine("current " + currentPosition + "target " 
	    + targetPosition + " error " + length);

	if (length >= LONG_DISTANCE) {
	    // Get to LONG_DISTANCE away, then animate from there

	    Vector3f moveTo = new Vector3f(positionError);

            moveTo.multLocal((length - LONG_DISTANCE) / length);
	    move(moveTo);
	    logger.fine("JUMP TO: " + currentPosition);
	} else {
	    if (animate == false) {
	        move(positionError);
	    }
	}

	synchronized (this) {
	    notifyAll();
	}
    }
    
    private boolean done;

    public void done() {
	done = true;
    }

    private void sleep(int ms) {
	try {
	    Thread.sleep(ms);
	} catch (InterruptedException e) {
	}
    }

    public void run() {
	while (!done) {
            long newTime = System.currentTimeMillis();
            float timeScale = 0.001f;
            float deltaTime = (newTime - oldTime) * timeScale;

	    logger.finest("oldTime " + oldTime + " newTime " + newTime
	    	+ " deltaTime " + deltaTime);

            oldTime = newTime;
        
            if (deltaTime <= 0.0) {
	    	sleep(40);
	    	continue;
            }
        
            //Difference between where we are and where we want to be.
            Vector3f positionError = new Vector3f(targetPosition);
            positionError.subtractLocal(currentPosition);
    
            if (positionError.length() < EPSILON) {
		logger.fine("Orb reached target position.");

		if (listener != null) {
		    listener.targetReached(currentPosition);
		}

		synchronized (this) {
		    try {
			wait();
		    } catch (InterruptedException e) {
		    }
		}
	    	continue;
            }

            Vector3f springForce = new Vector3f(positionError);
            springForce.multLocal(SPRING);

            Vector3f dampingForce = new Vector3f(oldVelocity);
            dampingForce.multLocal(DAMPING * -1.0f);

            Vector3f netAcceleration = new Vector3f(springForce);
            netAcceleration.addLocal(springForce);
            netAcceleration.addLocal(dampingForce);
            netAcceleration.multLocal(1.0f / MASS);
      
            Vector3f aIntegration = new Vector3f(netAcceleration);
            aIntegration.multLocal(0.5f * deltaTime * deltaTime);
     
            Vector3f vIntegration = new Vector3f(oldVelocity);
            vIntegration.multLocal(deltaTime);
     
            Vector3f jump = new Vector3f();
            jump.addLocal(aIntegration);
            jump.addLocal(vIntegration);
      
            //Speed limiter
            float newVelocity = jump.length() / deltaTime;

            if (newVelocity > MAX_SPEED) {
                jump.normalizeLocal();
                jump.multLocal(MAX_SPEED * deltaTime);
            } 
     
            jump.multLocal(1.0f/deltaTime);
            oldVelocity = jump;
    
	    move(jump);
	    sleep(40);
	}
    }

    long time;

    private void move(Vector3f position) {
	currentPosition.addLocal(position);

	if (logger.isLoggable(Level.FINEST)) {
	    long now = System.currentTimeMillis();

	    logger.finest("current " + currentPosition + " time delta " 
	        + (now - time));

	    time = now;
	}

        movableComp.localMoveRequest(
            new CellTransform(rotation, currentPosition));

	if (listener != null) {
	    listener.positionChanged(currentPosition);
	}
    }
    
    public void targetSwap(Vector3f oldVector, Vector3f newVector) {
        oldVector.subtractLocal(newVector);
        currentPosition.addLocal(oldVector);
        setTargetPosition(new Vector3f());
    }

}
