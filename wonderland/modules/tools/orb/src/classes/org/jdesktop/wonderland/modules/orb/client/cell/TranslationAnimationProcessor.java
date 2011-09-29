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
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.client.jme.ClientContextJME;

/**
 * Translation Animation for use with Scenario Timing framework
 *
 * @author paulby
 */
public class TranslationAnimationProcessor extends AnimationProcessorComponent {
    /**
     * The WorldManager - used for adding to update list
     */
    private WorldManager worldManager = null;

    private Vector3f startV3f;
    private Vector3f endV3f;
    
    private Vector3f translation = new Vector3f();

    /**
     * The rotation target
     */
    private Node target = null;
    
    /**
     * The constructor
     */
    public TranslationAnimationProcessor(Entity entity, Node target, Vector3f startLocation, Vector3f endLocation) {
        super(entity);
        this.worldManager = ClientContextJME.getWorldManager();
        this.target = target;
        this.startV3f = startLocation;
        this.endV3f = endLocation;
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
        synchronized(translation) {
            target.setLocalTranslation(translation);
        }
        worldManager.addToUpdateList(target);
    }
    

    public void timingEvent(float fraction, long totalElapsed) {
        synchronized(translation) {
            translation.x = startV3f.x + (endV3f.x - startV3f.x)*fraction;
            translation.y = startV3f.y + (endV3f.y - startV3f.y)*fraction;
            translation.z = startV3f.z + (endV3f.z - startV3f.z)*fraction;
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
