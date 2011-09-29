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
package org.jdesktop.wonderland.modules.artimport.client.jme;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.PostEventCondition;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.WorldManager;

/**
 *
 * @author paulby
 */
public class TransformProcessorComponent extends ProcessorComponent {


        private Matrix3f rotation;
        private Vector3f translation;
        private Vector3f scale = new Vector3f(1,1,1);
        private Node modelBG;
        private Node rootBG;
        private WorldManager worldManager;
        private boolean updatePending = false;
        
        public TransformProcessorComponent(WorldManager worldManager, Node modelBG, Node rootBG) {
            this.modelBG = modelBG;
            this.rootBG = rootBG;
            this.worldManager = worldManager;
        }
        
        @Override
        public void compute(ProcessorArmingCollection conditions) {
            // Nothing to do
        }

        @Override
        public void commit(ProcessorArmingCollection conditions) {
            synchronized(this) {
                if (updatePending) {
                    modelBG.setLocalRotation(rotation);
                    rootBG.setLocalTranslation(translation);
                    modelBG.setLocalScale(scale);
                    worldManager.addToUpdateList(modelBG);
                    updatePending = false;
                }  
            }
        }

        @Override
        public void initialize() {
            // TODO this should be a post condition
            setArmingCondition(new NewFrameCondition(this));
        }

        public void setTransform(Matrix3f rotation, Vector3f translation) {
            synchronized(this) {
                this.rotation = rotation;
                this.translation = translation;
                updatePending = true;
            }
        }

        public void setTransform(Matrix3f rotation, Vector3f translation, Vector3f scale) {
            synchronized(this) {
                this.rotation = rotation;
                this.translation = translation;
                this.scale = scale;
                updatePending = true;
            }
        }
    
}
