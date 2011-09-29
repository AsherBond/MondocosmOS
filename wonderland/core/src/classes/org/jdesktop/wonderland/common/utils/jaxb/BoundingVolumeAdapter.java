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
package org.jdesktop.wonderland.common.utils.jaxb;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.jdesktop.wonderland.common.utils.jaxb.BoundingVolumeAdapter.BoundingVolumeHandler;

/**
 *
 * @author paulby
 */
public class BoundingVolumeAdapter extends XmlAdapter<BoundingVolumeHandler, BoundingVolume> {


    @Override
    public BoundingVolume unmarshal(BoundingVolumeHandler v) throws Exception {
        if (v==null)
            return null;
        return v.createBounds();
    }

    @Override
    public BoundingVolumeHandler marshal(BoundingVolume v) throws Exception {
        if (v==null) {
            return null;
        }
        return new BoundingVolumeHandler(v);
    }

    static public class BoundingVolumeHandler {

        public enum BoundsType { BOX, SPHERE };

        @XmlElement
        private BoundsType boundsType;

        @XmlElement
        private float center_x;
        @XmlElement
        private float center_y;
        @XmlElement
        private float center_z;
        @XmlElement
        private float dimension_x=0f;
        @XmlElement
        private float dimension_y=0f;
        @XmlElement
        private float dimension_z=0f;


        public BoundingVolumeHandler() {
        }

        public BoundingVolumeHandler(BoundingVolume v) {
            if (v instanceof BoundingSphere) {
                boundsType = BoundsType.SPHERE;
                Vector3f center = ((BoundingSphere)v).getCenter();
                center_x = center.x;
                center_y = center.y;
                center_z = center.z;
                dimension_x = ((BoundingSphere)v).getRadius();
            } else if (v instanceof BoundingBox) {
                boundsType = BoundsType.BOX;
                Vector3f center = ((BoundingBox)v).getCenter();
                center_x = center.x;
                center_y = center.y;
                center_z = center.z;
                Vector3f dimension = ((BoundingBox)v).getExtent(null);
                dimension_x = dimension.x;
                dimension_y = dimension.y;
                dimension_z = dimension.z;
            } else {
                throw new RuntimeException("Unsupported bounds type "+v.getClass().getName());
            }
        }

        public BoundingVolume createBounds() {
            switch(boundsType) {
                case SPHERE :
                    return new BoundingSphere(dimension_x, new Vector3f(center_x, center_y, center_z));
                case BOX :
                    return new BoundingBox(new Vector3f(center_x, center_y, center_z), dimension_x, dimension_y, dimension_z);
                default :
                    throw new RuntimeException("Unsupported bounds type "+boundsType);
            }
        }
    }
}
