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
package org.jdesktop.wonderland.common;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * Utilities for dealing with 3D Math
 * 
 * @author paulby
 */
@ExperimentalAPI
public class Math3DUtils {

//    /**
//     * Creates a bounding box with the specified center and size.
//     */
//    public static BoundingBox createBoundingBox(Vector3d center, float size) {
//        BoundingBox cellBounds = new BoundingBox(new Point3d(center.x-size/2f, center.y-size/2f, center.z-size/2f), new Point3d(center.x+size/2f, center.y+size/2f, center.z+size/2f));
//        return cellBounds;
//    }
//    
//    /**
//     * Creates a bounding box with the specified center and dimensions.
//     */
//    public static BoundingBox createBoundingBox(Vector3d center, float xDim, float yDim, float zDim) {
//        BoundingBox cellBounds = new BoundingBox(new Point3d(center.x-xDim/2f, center.y-yDim/2f, center.z-zDim/2f), new Point3d(center.x+xDim/2f, center.y+yDim/2f, center.z+zDim/2f));
//        return cellBounds;
//    }
//    
//    /**
//     * Creates a bounding sphere with the specified center and size.
//     */
//    public static BoundingSphere createBoundingSphere(Vector3d center, float radius) {
//        return new BoundingSphere(new Point3d(center), radius);
//    }
//    
//    /**
//     * Creates a Matrix4d with a translation to center
//     */
//    public static Matrix4d createOriginM4d(Vector3d center) {
//        Matrix3d rot = new Matrix3d();
//        rot.setIdentity();
//        return new Matrix4d(rot, center, 1);
//    }
//    
//    /**
//     * Create a Matrix4D with a translation to center, a rotation in the
//     * y axis and a scale
//     */
//    public static Matrix4d createOriginM4d(Vector3d center, double angle,
//        double scale) {
//        Matrix3d rot = new Matrix3d();
//        rot.rotY(angle);
//        return new Matrix4d(rot, center, scale);
//    }
      
    /**
     * Calculates the distance of a point from a line.
     * <p><code>
     *    x1----------------------------x2 <br>
     *                  |               <br>
     *                  | distance      <br>
     *                  |               <br>
     *                 point            <br>
     * </code>
     * <p>
     * The formula is <br>
     * <code>
     *      d = |(x2-x1) x (x1-p)| <br>
     *          ------------------ <br>
     *              |x2-x1|        <br>
     * </code>
     *
     * Where p=point, lineStart=x1, lineEnd=x2
     *
     */
    public static float pointLineDistance( final Vector3f lineStart, 
                                           final Vector3f lineEnd, 
                                           final Vector3f point ) {
        Vector3f a = new Vector3f(lineEnd);
        a.subtract(lineStart);
        
        Vector3f b = new Vector3f(lineStart);
        b.subtract(point);
        
        Vector3f cross = new Vector3f();
        cross.cross(a,b);
        
        return cross.length()/a.length();
    }

    /**
     * Converts the Matrix into Euler angles (roll, pitch, yaw )
     */
    public static void toEuler( Matrix3f matrix, Vector3f euler ) {
        Vector3f v3d = new Vector3f();
        
        Vector3f zAxis = new Vector3f( 0, 0, -1 );
        Vector3f yAxis = new Vector3f( 0, 1, 0 );
        Vector3f xAxis = new Vector3f( 1, 0, 0 );

        v3d.set( xAxis );
        matrix.mult( v3d, v3d );
        v3d.x = Math.abs( v3d.x );
        v3d.z = 0;
        v3d.normalize();

        euler.x = xAxis.angleBetween( v3d );

        v3d.set( yAxis );
        matrix.mult( v3d, v3d );
        v3d.z = Math.abs( v3d.z );
        v3d.x = 0;
        v3d.normalize();

        euler.y = yAxis.angleBetween( v3d );

        v3d.set( zAxis );
        matrix.mult( v3d, v3d );
        v3d.y = 0;
        v3d.normalize();

        euler.z = zAxis.angleBetween( v3d );
        if (v3d.x<0)
            euler.z = FastMath.TWO_PI-euler.z;
     }

    public static boolean epsilonEquals(float f1, float f2) {
        return epsilonEquals(f1, f2, FastMath.FLT_EPSILON);
    }

    public static boolean epsilonEquals(float f1, float f2, float epsilon) {
        float diff;

        diff = f1 - f2;
        if ((diff < 0 ? -diff : diff) > epsilon) {
            return false;
        }
        
        return true;
    }

    public static boolean epsilonEquals(Vector3f t1, Vector3f t2) {
        return epsilonEquals(t1, t2, FastMath.FLT_EPSILON);
    }

    public static boolean epsilonEquals(Vector3f t1, Vector3f t2, float epsilon) {
        if (t1 == null && t2 == null) {
            return true;
        } else if (t1 == null || t2 == null) {
            return false;
        }

        if (epsilonEquals(t1.x, t2.x, epsilon) &&
            epsilonEquals(t1.y, t2.y, epsilon) &&
            epsilonEquals(t1.z, t2.z, epsilon))
            return true;
        
        return false;
    }

    public static boolean epsilonEquals(Quaternion q1, Quaternion q2) {
        return epsilonEquals(q1, q2, FastMath.FLT_EPSILON);
    }

    public static boolean epsilonEquals(Quaternion q1, Quaternion q2, float epsilon) {
        if (q1 == null && q2 == null) {
            return true;
        } else if (q1 == null || q2 == null) {
            return false;
        }

        // convert to axis angles and compare
        float[] a1 = q1.toAngles(null);
        float[] a2 = q2.toAngles(null);

        if (epsilonEquals(a1[0], a2[0], epsilon) &&
            epsilonEquals(a1[1], a2[1], epsilon) &&
            epsilonEquals(a1[2], a2[2], epsilon))
            return true;

        return false;
    }

    public static boolean encloses(BoundingVolume parent, BoundingVolume child) {
                
        if (parent instanceof BoundingBox) {
            if (child instanceof BoundingBox) {
                return encloses((BoundingBox)parent, (BoundingBox)child);
            } else if (child instanceof BoundingSphere) {
                return encloses((BoundingBox)parent, (BoundingSphere)child);
            }
        } else if (parent instanceof BoundingSphere) {
            if (child instanceof BoundingBox) {
                 return encloses((BoundingSphere)parent, (BoundingBox)child);
            } else if (child instanceof BoundingSphere) {
                return encloses((BoundingSphere)parent, (BoundingSphere)child);
            }
        }

        throw new UnsupportedOperationException("Unsupported bounds combination "+parent.getClass().getName()+" "+child.getClass().getName());
    }

    /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingBox parent, BoundingSphere child) {
        Vector3f pCenter = parent.getCenter();
        Vector3f pExtent = parent.getExtent(null);
        Vector3f cCenter = child.getCenter();
        float radius= child.getRadius();

        if (cCenter.x+radius > pCenter.x + pExtent.x ||
            cCenter.y+radius > pCenter.y + pExtent.y ||
            cCenter.z+radius > pCenter.z + pExtent.z)
            return false;

        if (cCenter.x-radius < pCenter.x - pExtent.x ||
            cCenter.y-radius < pCenter.y - pExtent.y ||
            cCenter.z-radius < pCenter.z - pExtent.z)
            return false;

        return true;
    }

     /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingBox parent, BoundingBox child) {
        Vector3f pExtent = parent.getExtent(null);
        Vector3f cExtent = child.getExtent(null);
        Vector3f pCenter = parent.getCenter();
        Vector3f cCenter = child.getCenter();

        if (cCenter.x+cExtent.x > pCenter.x+pExtent.x ||
            cCenter.y+cExtent.y > pCenter.y+pExtent.y ||
            cCenter.z+cExtent.z > pCenter.z+pExtent.z)
                return false;

        if (cCenter.x-cExtent.x < pCenter.x-pExtent.x ||
            cCenter.y-cExtent.y < pCenter.y-pExtent.y ||
            cCenter.z-cExtent.z < pCenter.z-pExtent.z)
                return false;
        
        return true;
    }
    /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingSphere parent, BoundingBox child) {
        // Check each corner of the box is within the sphere

        Vector3f cCenter = child.getCenter();
        Vector3f cExtent = child.getExtent(null);
        Vector3f tmp = new Vector3f();
        
        tmp.x=cCenter.x + cExtent.x;
        tmp.y=cCenter.y + cExtent.y;
        tmp.z=cCenter.z + cExtent.z;
        if (!parent.contains(tmp))
            return false;
        
        tmp.x=cCenter.x - cExtent.x;
        if (!parent.contains(tmp))
            return false;
        
        tmp.z=cCenter.z - cExtent.z;
        if (!parent.contains(tmp))
            return false;
        
        tmp.x=cCenter.x + cExtent.x;
        if (!parent.contains(tmp))
            return false;

        tmp.x=cCenter.x + cExtent.x;
        tmp.y=cCenter.y - cExtent.y;
        tmp.z=cCenter.z + cExtent.z;
        if (!parent.contains(tmp))
            return false;
        
        tmp.x=cCenter.x - cExtent.x;
        if (!parent.contains(tmp))
            return false;
        
        tmp.z=cCenter.z - cExtent.z;
        if (!parent.contains(tmp))
            return false;
        
        tmp.x=cCenter.x + cExtent.x;
        if (!parent.contains(tmp))
            return false;
        

        return true;
    }

    /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingSphere parent, BoundingSphere child) {
        Vector3f childCenter = new Vector3f();
        Vector3f parentCenter = new Vector3f();
        child.getCenter(childCenter);
        parent.getCenter(parentCenter);
        float childR = child.getRadius();
        float parentR = parent.getRadius();

        if (childCenter.x+childR > parentCenter.x+parentR ||
            childCenter.y+childR > parentCenter.y+parentR ||
            childCenter.z+childR > parentCenter.z+parentR)
            return false;
        
        if (childCenter.x-childR < parentCenter.x-parentR ||
            childCenter.y-childR < parentCenter.y-parentR ||
            childCenter.z-childR < parentCenter.z-parentR)
            return false;
            
        return true;
    }
}
