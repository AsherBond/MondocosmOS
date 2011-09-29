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
package org.jdesktop.wonderland.common.cell;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.Math3DUtils;

/**
 * The transform for a cell.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class CellTransform implements Serializable {

    private Quaternion rotation;
    private Vector3f translation;
    private float scale = 1f;
    
    /**
     * Create an identity transform
     */
    public CellTransform() {
        this(null, null, 1f);
    }

     /**
     * Constructor that takes translation, rotation, and scaling. Any/all of
     * the three arguments may be null.
     */
    public CellTransform(Quaternion rotate, Vector3f translate, float scale) {
        if (rotate==null)
            this.rotation = new Quaternion();
        else
            this.rotation = rotate.clone();

        if (translate==null)
            this.translation = new Vector3f();
        else
            this.translation = translate.clone();

        this.scale = scale;
    }

    /**
     * @deprecated Non uniform scale are not supported
     * @param rotate
     * @param translate
     * @param scale
     */
    public CellTransform(Quaternion rotate, Vector3f translate, Vector3f scale) {
        if (rotate==null)
            this.rotation = new Quaternion();
        else
            this.rotation = rotate.clone();

        if (translate==null)
            this.translation = new Vector3f();
        else
            this.translation = translate.clone();

        if (scale == null) {
            this.scale = 0.0f;
        }
        else {
            this.scale = scale.x;
            Logger.getLogger(CellTransform.class.getName()).warning("Non uniform scale is not supported, please use another CellTransform constructor");
            Thread.dumpStack();
        }
    }
    
    /**
     * Create a cell transform. Either (or both) values may be null
     * 
     * @param quat
     * @param translation
     */
    public CellTransform(Quaternion rotate, Vector3f translation) {
        this(rotate, translation, 1f);

    }

    private CellTransform(CellTransform orig) {
        this.rotation = new Quaternion(orig.rotation);
        this.translation = new Vector3f(orig.translation);
        this.scale = orig.scale;
    }
    
    public CellTransform clone(CellTransform result) {
        if (result==null)
            return new CellTransform(this);
        
        result.set(this);
        return result;
    }
    
    /**
     * set this object to have the same state as source
     * @param source
     */
    private void set(CellTransform source) {
        this.rotation.set(source.rotation);       
        this.scale = source.scale;
        this.translation.set(source.translation);
    }

    /**
     * Transform the BoundingVolume 
     * @param ret
     */
    public void transform(BoundingVolume ret) {
        assert(ret!=null);
        ret.transform(rotation,translation, new Vector3f(scale, scale, scale), ret);
    }
    
    /**
     * Transform the vector ret by this transform. ret is modified and returned.
     * @param ret
     */
    public Vector3f transform(Vector3f ret) {
        ret.multLocal(scale);
        rotation.multLocal(ret);
        ret.addLocal(translation);

        return ret;
    }

    /**
     * Return the position and current look direction
     * @param position
     * @param look
     */
    public void getLookAt(Vector3f position, Vector3f look) {
        position.set(0,0,0);
        transform(position);

        look.set(0,0,1);
        transform(look);
        look.normalizeLocal();
    }

    /**
     * Multiply this transform by t1. This transform will be update
     * and the result returned
     * 
     * @param transform
     * @return this
     */
    public CellTransform mul(CellTransform in) {
        // This does not work when scale!=1
//        this.scale *= in.scale;
//        this.translation.addLocal(rotation.mult(in.translation).multLocal(in.scale));
//        this.rotation.multLocal(in.rotation);

        // Correctly calculate the multiplication.
        Quat4d q = new Quat4d(rotation.x, rotation.y, rotation.z, rotation.w);
        Vector3d t = new Vector3d(translation.x, translation.y, translation.z);
        Matrix4d m = new Matrix4d(q,t,scale);

        Quat4d q1 = new Quat4d(in.rotation.x, in.rotation.y, in.rotation.z, in.rotation.w);
        Vector3d t1 = new Vector3d(in.translation.x, in.translation.y, in.translation.z);
        Matrix4d m1 = new Matrix4d(q1,t1,in.scale);

        m.mul(m1);

        m.get(q);
        m.get(t);
        scale = (float)m.getScale();
        rotation.set((float)q.x, (float)q.y, (float)q.z, (float)q.w);
        translation.set((float)t.x, (float)t.y, (float)t.z);

        return this;
    }

    /**
     * Multiply t1 * t2 and put the result in this. Also return this
     * @param t1
     * @param t2
     * @return
     */
    public CellTransform mul(CellTransform t1, CellTransform t2) {
        this.set(t1);
        this.mul(t2);
        return this;
    }
    
    /**
     * Subtract t1 from this transform, modifying this transform and
     * returning this transform.
     * 
     * @param t1
     * @return
     */
    public CellTransform sub(CellTransform t1) {
        rotation.subtract(t1.rotation);
        scale -= t1.scale;
        translation.subtract(t1.translation);
        return this;
    }
    
    /**
     * Populates translation with the translation of this CellTransform, if translation
     * is null, a new Vector3f will be created and returned
     * 
     * @param translation object to return (to avoid gc)
     * @return the translation for this transform
     */
    public Vector3f getTranslation(Vector3f translation) {
        if (translation==null)
            return new Vector3f(this.translation);
        
        translation.set(this.translation);
        return translation;
    }
    
    /**
     * Set the translation.
     * @param translation set the translation for this transform
     */
    public void setTranslation(Vector3f translation) {
        if (this.translation==null)
            this.translation = new Vector3f();
        else
            this.translation.set(translation);
    }

    /**
     * Get the rotation portion of this transform. Populates the rotation 
     * paramter with the current rotation and returns it, if rotation is null
     * a new Quaternion is returned.
     * 
     * @param rotation object to return (to avoid gc)
     * @return the rotation quaternion for this transform
     */
    public Quaternion getRotation(Quaternion rotation) {
        if (rotation==null)
            rotation = new Quaternion(this.rotation);
        else
            rotation.set(this.rotation);
        
        return rotation;
    }

    /**
     * Set the rotation portion of this transform
     * @param rotation set the rotation for this transform
     */
    public void setRotation(Quaternion rotation) {
        if (this.rotation==null)
            this.rotation = new Quaternion(rotation);
        else
            this.rotation.set(rotation);
    }
    
    /**
     * Returns the scaling vector as an array of doubles to scale each axis.
     * Sets the value of the scale into the argument (if given). If a null
     * argument is passed, then this method creates and returns a new Vector3f
     * object. The scale is uniform, this is a convenience function as JME
     * expects a vector
     * 
     * @param scale Populate this object with the scale if non-null
     * @return The scaling factors
     */
    public Vector3f getScaling(Vector3f scale) {
        if (scale == null) {
            scale = new Vector3f(this.scale, this.scale, this.scale);
        }
        else {
            scale.set(this.scale, this.scale, this.scale);
        }
        return scale;
    }

    /**
     * Return the uniform scale of this transform.
     * @return
     */
    public float getScaling() {
        return scale;
    }
    
    /**
     * Sets the scaling factor for this cell transform
     * 
     * @param scale The new scaling factor
     */
    public void setScaling(float scale) {
        this.scale = scale;
    }

//    /**
//     * @deprecated Non uniform scale is not supported
//     * @param scale
//     */
//    public void setScaling(Vector3f scale) {
//        Logger.getLogger(CellTransform.class.getName()).warning("Non uniform scale is not supported, please use the other setScaling method");
//        Thread.dumpStack();
//        setScaling(scale.x);
//    }

    /**
     * Invert the transform, this object is inverted and returned.
     *
     * @return return this
     */
    public CellTransform invert() {
        // This invert for jme does not function when the scale != 1
//        Matrix3f rot = new Matrix3f();
//        rot.set(rotation);
//        float temp;
//        temp=rot.m01;
//        rot.m01=rot.m10;
//        rot.m10=temp;
//        temp=rot.m02;
//        rot.m02=rot.m20;
//        rot.m20=temp;
//        temp=rot.m21;
//        rot.m21=rot.m12;
//        rot.m12=temp;
//        rot.multLocal(1/scale);
//
//        rot.multLocal(translation);
//
//        translation.multLocal(-1);
//        scale = 1/scale;
//
//        rotation.fromRotationMatrix(rot);

        // Correctly compute the inversion, use Vecmath as the matrix invert
        // in JME does not function when scale!=1
        Quat4d q = new Quat4d(rotation.x, rotation.y, rotation.z, rotation.w);
        Vector3d t = new Vector3d(translation.x, translation.y, translation.z);
        Matrix4d m = new Matrix4d(q,t,scale);
        m.invert();

        m.get(q);
        m.get(t);
        scale = (float)m.getScale();
        rotation.set((float)q.x, (float)q.y, (float)q.z, (float)q.w);
        translation.set((float)t.x, (float)t.y, (float)t.z);

        return this;
    }

    /**
     * Determine if all values from this transform are within epsilon of
     * the values from the given transform.
     */
    public boolean epsilonEquals(CellTransform o) {
        if (o == null) {
            return false;
        }

        // within 1 millimeter
        if (!Math3DUtils.epsilonEquals(translation, o.translation, 0.001f)) {
            return false;
        }

        // within one degree
        if (!Math3DUtils.epsilonEquals(rotation, o.rotation, 0.0174532925f)) {
            return false;
        }

        // close in scale
        if (!Math3DUtils.epsilonEquals(scale, scale, 0.0001f)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = true;
        
        if (o instanceof CellTransform) {
            CellTransform e = (CellTransform)o;
            
            if (e.rotation!=null && !e.rotation.equals(rotation))
                ret = false;
            else if (e.rotation==null && rotation!=null)
                ret = false;
            
            if (e.translation!=null && !e.translation.equals(translation))
                ret = false;
            else if (e.translation==null && translation!=null)
                ret = false;
            
            if (e.scale!=scale)         // TODO should use EpsilonEquals
                ret = false;
        } else {
            ret = false;
        }
        
        
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.rotation != null ? this.rotation.hashCode() : 0);
        hash = 13 * hash + (this.translation != null ? this.translation.hashCode() : 0);
        hash = 13 * hash + (int)(scale*1000);
        return hash;
    }

    @Override
    public String toString() {
        return "[tx="+translation.x+" ty="+translation.y+" tz="+translation.z +
                "] [rot="+printQuat(rotation) +
                "] [s=" + scale+"]";

    }

    private String printQuat(Quaternion q) {
        Vector3f axis = new Vector3f();
        float angle = q.toAngleAxis(axis);
        return axis+":"+Math.toDegrees(angle);
    }
}
