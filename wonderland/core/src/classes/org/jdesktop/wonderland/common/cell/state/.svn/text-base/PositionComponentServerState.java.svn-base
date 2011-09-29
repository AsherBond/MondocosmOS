/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.common.cell.state;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;
import org.jdesktop.wonderland.common.utils.jaxb.QuaternionAdapter;
import org.jdesktop.wonderland.common.utils.jaxb.Vector3fAdapter;

/**
 * A special cell component server state object that represents the cell
 * transform (origin, rotation, scaling) and bounds. There is no corresponding
 * server or client-side component object. This state is handled as a special
 * case by the cell.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="position-component")
@ServerState
public class PositionComponentServerState extends CellComponentServerState {

    /* The (x, y, z) translation of the cell */
    @XmlElement(name="translation")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    public Vector3f translation = new Vector3f();

    /* The cell bounds */
    @XmlElement(name="bounds")
    public Bounds bounds = new Bounds();

    /* The (x, y, z) components of the scaling */
    @XmlElement(name="scale")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    public Vector3f scaling = new Vector3f(1f,1f,1f);

    /* The rotation about an (x, y, z) axis and angle (radians) */
    @XmlElement(name="rotation")
    @XmlJavaTypeAdapter(QuaternionAdapter.class)
    public Quaternion rotation = new Quaternion();

    /**
     * The Bounds static inner class stores the bounds type and bounds radius.
     */
    public static class Bounds implements Serializable {
        public enum BoundsType { SPHERE, BOX };

        /* The bounds type, either SPHERE or BOX */
        @XmlElement(name="type") public BoundsType type = BoundsType.SPHERE;

        /* The x dimension or radius of the bounds */
        @XmlElement(name="x") public double x = 1.0;
        @XmlElement(name="y") public double y = 1.0;
        @XmlElement(name="z") public double z = 1.0;

        /** Default constructor */
        public Bounds() {
        }

        public Bounds(BoundingVolume bv) {
            if (bv instanceof BoundingBox) {
                type = BoundsType.BOX;
                x = ((BoundingBox)bv).xExtent;
                y = ((BoundingBox)bv).yExtent;
                z = ((BoundingBox)bv).zExtent;
            } else if (bv instanceof BoundingSphere) {
                type = BoundsType.SPHERE;
                x = ((BoundingSphere)bv).radius;
            }
        }
    }


    @Override
    public String getServerComponentClassName() {
        return null;
    }

    /**
     * Returns the cell translation.
     *
     * @return The cell translation
     */
    @XmlTransient public Vector3f getTranslation() {
        return this.translation;
    }

    /**
     * Sets the cell translation. If null, then this property will not be written
     * out to the file.
     *
     * @param translation The new cell translation
     */
    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    /**
     * Returns the cell bounds.
     *
     * @return The cell bounds
     */
    @XmlTransient public Bounds getBounds() {
        return this.bounds;
    }

    /**
     * Sets the cell bounds. If null, then this property will not be written
     * out to the file.
     *
     * @param bounds The new cell bounds
     */
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public void setBounds(BoundingVolume boundingVolume) {
        this.bounds = new Bounds(boundingVolume);
    }

    /**
     * Returns the cell scaling.
     *
     * @return The cell scaing
     */
    @XmlTransient public Vector3f getScaling() {
        return this.scaling;
    }

    /**
     * Sets the cell scaling. If null, then this property will not be written
     * out to the file.
     *
     * @param scaling The new cell scaling
     */
    public void setScaling(Vector3f scaling) {
        this.scaling = scaling;
    }

    /**
     * Returns the cell rotation.
     *
     * @return The cell rotation
     */
    @XmlTransient public Quaternion getRotation() {
        return this.rotation;
    }

    /**
     * Sets the cell rotation. If null, then this property will not be written
     * out to the file.
     *
     * @param rotation The new cell rotation
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return "[BasicCellSetup] origin=(" + this.translation.x + "," + this.translation.y +
                "," + this.translation.z + ") rotation=(" + this.rotation.x + "," +
                this.rotation.y + "," + this.rotation.z + ") @ " + this.rotation.w +
                " scaling=(" + this.scaling.x + "," + this.scaling.y + "," +
                this.scaling.z + ") bounds=" + this.bounds.type + "@" +
                this.bounds.x+", "+this.bounds.y+" "+this.bounds.z;
    }
}
