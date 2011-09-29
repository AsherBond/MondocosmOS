/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import com.jme.scene.Spatial;
import imi.scene.PJoint;
import imi.scene.PMatrix;
import imi.scene.PTransform;

/**
 * A joint that can be added into an IMI PScene-based graph that will update
 * the corresponding JME spatial's local geometry whenever the geometry of the
 * IMI graph changes.
 * <p>
 * Note that to maintain frame synchronization, the JME spatial must be attached
 * to the externalKids node of the IMI JScene.
 *
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class JMEUpdateJoint extends PJoint {

    private final ModifyTransform xform;

    public JMEUpdateJoint(Spatial modifyNode) {
        this.xform = new ModifyTransform(modifyNode);
    }

    @Override
    public PTransform getTransform() {
        return xform;
    }

    @Override
    public void setTransform(PTransform transform) {
        if (xform != null) {
            xform.setLocalMatrix(transform.getLocalMatrix(false));
            xform.setDirtyWorldMat(true);
        }
    }

    // an extenstion of transform that updates the given spatial
    // whenever the transform is updated
    private static class ModifyTransform extends PTransform {

        private final Spatial modifyNode;

        public ModifyTransform(Spatial modifyNode) {
            this.modifyNode = modifyNode;
        }

        @Override
        public void buildWorldMatrix(PMatrix parentWorld) {
            super.buildWorldMatrix(parentWorld);

            PMatrix xform = getWorldMatrix(false);

            modifyNode.setLocalTranslation(xform.getTranslation());
            modifyNode.setLocalRotation(xform.getRotationJME());
            modifyNode.setLocalScale(xform.getScaleVector());

            // force the geometric state to update immediately, so that
            // we aren't a frame behind the animation
            // ClientContextJME.getWorldManager().addToUpdateList(modifyNode);
            modifyNode.updateWorldVectors(true);
        }
    }
}
