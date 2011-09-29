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

import org.jdesktop.mtgame.*;
import com.jme.scene.Node;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.client.jme.ClientContextJME;

/**
 * Rotation Processor for use with Scenario Timing framework
 *
 * @author paulby
 */
public class RotationAnimationProcessor extends AnimationProcessorComponent {
    /**
     * The WorldManager - used for adding to update list
     */
    private WorldManager worldManager = null;
    /**
     * The current degrees of rotation
     */
    private float radians = 0.0f;

    private float startRadians;
    private float endRadians;

    private Vector3f axis;

    /**
     * The rotation matrix to apply to the target
     */
    private Quaternion quaternion = new Quaternion();
    
    /**
     * The rotation target
     */
    private Node target = null;
    
    /**
     * The constructor
     */
    public RotationAnimationProcessor(Entity entity, Node target, float startDegrees, float endDegrees) {
        this(entity, target, startDegrees, endDegrees, new Vector3f(0f,1f,0f));
    }
    public RotationAnimationProcessor(Entity entity, Node target, float startDegrees, float endDegrees, Vector3f axis) {
        super(entity);
        this.worldManager = ClientContextJME.getWorldManager();
        this.target = target;
        this.startRadians = (float) Math.toRadians(startDegrees);
        this.endRadians = (float) Math.toRadians(endDegrees);
        this.axis = axis;
    }

    /**
     * The initialize method
     */
    public void initialize() {
    }
    
    /**
     * The Calculate method
     */
    public void compute(ProcessorArmingCollection collection) {
    }

    /**
     * The commit method
     */
    public void commit(ProcessorArmingCollection collection) {
//        quaternion.fromAngles(0.0f, radians, 0.0f);
        quaternion.fromAngleAxis(radians, axis);
        target.setLocalRotation(quaternion);
        worldManager.addToUpdateList(target);
    }
    

    public void timingEvent(float fraction, long totalElapsed) {
        radians = startRadians+(endRadians-startRadians)*fraction;
    }

    public void begin() {
        setArmingCondition(new NewFrameCondition(this));
    }

    public void end() {
        setArmingCondition(null);
    }

    public void pause() {
        setArmingCondition(null);
    }

    public void resume() {
        setArmingCondition(new NewFrameCondition(this));
    }
        

}
