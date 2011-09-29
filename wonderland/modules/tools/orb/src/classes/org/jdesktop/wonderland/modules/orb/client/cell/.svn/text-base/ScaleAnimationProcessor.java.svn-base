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
import org.jdesktop.wonderland.client.jme.ClientContextJME;

/**
 * Scale Animation for use with Scenario Timing framework
 *
 * @author paulby
 * @author Bernard Horan
 */
public class ScaleAnimationProcessor extends AnimationProcessorComponent {
    /**
     * The WorldManager - used for adding to update list
     */
    private WorldManager worldManager = null;

    private Float start;
    private Float end;
    
    private Float scale = 0f;

    /**
     * The rotation target
     */
    private Node target = null;
    
    /**
     * The constructor
     */
    public ScaleAnimationProcessor(Entity entity, Node target, Float startScale, Float endScale) {
        super(entity);
        this.worldManager = ClientContextJME.getWorldManager();
        this.target = target;
        this.start = startScale;
        this.end = endScale;
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
        synchronized(scale) {
            target.setLocalScale(scale);
        }
        worldManager.addToUpdateList(target);
    }
    

    public void timingEvent(float fraction, long totalElapsed) {
        synchronized(scale) {
            scale = start + (end - start) * fraction;
        }
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
