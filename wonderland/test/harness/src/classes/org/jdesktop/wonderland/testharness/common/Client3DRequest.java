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
package org.jdesktop.wonderland.testharness.common;

import com.jme.math.Vector3f;

/**
 *
 * @author paulby
 */
public class Client3DRequest extends TestRequest {
    private Vector3f[] desiredLocations;
    private float speed;
    private int desiredLoopCount;

    public enum ActionType { WALK };
    private ActionType action;
    
    private Client3DRequest(String username, ActionType action) {
        super(username);
        this.action = action;
    }
    
    public ActionType getAction() {
        return action;
    }
    
    public Vector3f[] getDesiredLocations() {
        return desiredLocations;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public int getLoopCount() {
        return desiredLoopCount;
    }
    
    /**
     * Walk to each of the desired locations, in order and then stop.
     * @param desiredLocations
     * @param speed
     * @return
     */
    public static Client3DRequest newWalkToRequest(String username, Vector3f[] desiredLocations, float speed) {
        Client3DRequest req = new Client3DRequest(username, ActionType.WALK);
        req.desiredLocations = desiredLocations;
        req.speed = speed;
        req.desiredLoopCount = 0;
        
        return req;
    }
    
    /**
     * Walk to each of the desired locations, in order, looping back to the first
     * location and continuing forever
     * 
     * @param desiredLocations
     * @param speed (in m/s)
     * @param loopCount number of iterations through the desired locations, -1 to loop forever
     * @return
     */
    public static Client3DRequest newWalkLoopRequest(String username, Vector3f[] desiredLocations, float speed, int loopCount) {
        Client3DRequest req = new Client3DRequest(username, ActionType.WALK);
        req.desiredLocations = desiredLocations;
        req.speed = speed;
        req.desiredLoopCount = loopCount;
        
        return req;
    }
}
