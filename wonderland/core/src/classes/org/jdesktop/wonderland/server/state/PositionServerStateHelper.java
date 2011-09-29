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
package org.jdesktop.wonderland.server.state;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState.Bounds.BoundsType;

/**
 * The BasicPositionComponentServerStateHelper class implements a collection of utility routines
 * that help convert between JMonkeyEngine (JME) types for the cell bounds and
 * tranform and the representations of these quantities using basic Java types
 * in the PositionComponentServerState class.
 * 
 * @author jkaplan
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PositionServerStateHelper {
   
    /**
     * Returns the bounds of a cell as a BoundingVolume object, given the cell's
     * setup information.
     * 
     * @param setup The cell's setup information
     * @return The bounds as a JME BoundingVolume object
     */
    public static BoundingVolume getCellBounds(PositionComponentServerState setup) {
        BoundsType type = setup.getBounds().type;
        float x = (float)setup.getBounds().x;
        
        if (type.equals(BoundsType.SPHERE) == true) {
            return new BoundingSphere(x, new Vector3f());
        }
        else if (type.equals(BoundsType.BOX) == true) {
            return new BoundingBox(new Vector3f(), x, (float)setup.getBounds().y, (float)setup.getBounds().z);
        }
        
        /* This should never happen, but in case it does... */
        throw new RuntimeException("Unsupported bounds type " + type);
    }

    /**
     * Creates and returns a new CellTransform object that representing the
     * translation (to origin), rotation, and scaling of a cell.
     * 
     * @param setup The cell's setup parameters
     * @return A CellTranform class representing the origin, rotation, scaling
     */
    public static CellTransform getCellTransform(PositionComponentServerState setup) {
        
        /* Create an return a new CellTransform class */
        return new CellTransform(setup.getRotation(), setup.getTranslation(), setup.getScaling().x);
    }
    
    /**
     * Given the JME BoundingVolume object, returns the bounds used in the cell
     * setup information.
     * 
     * @param bounds The JME bounds object
     * @return The PositionComponentServerState.Bounds object
     */
    public static PositionComponentServerState.Bounds getSetupBounds(BoundingVolume bounds) {
        PositionComponentServerState.Bounds cellBounds = new PositionComponentServerState.Bounds();
        if (bounds instanceof BoundingSphere) {
            cellBounds.type = BoundsType.SPHERE;
            cellBounds.x = ((BoundingSphere)bounds).getRadius();
            return cellBounds;
        }
        else if (bounds instanceof BoundingBox) {
            cellBounds.type = BoundsType.BOX;
            Vector3f extent = new Vector3f();
            ((BoundingBox)bounds).getExtent(extent);
            cellBounds.x = extent.x;
            cellBounds.y = extent.y;
            cellBounds.z = extent.z;
            return cellBounds;
        }
        
        /* This should never happen, but in case it does... */
        throw new RuntimeException("Unsupported bounds type " + bounds.getClass().getName());
    }

    /**
     * Given a (non-null) CellTranform class, returns the Translation class that
     * is used in the cell setup information.
     * 
     * @param transform The cell's transform
     * @return The origin used in the cell setup information
     * @deprecated
     */
    public static Vector3f getSetupOrigin(CellTransform transform) {
        return transform.getTranslation(null);
    }
    
    /**
     * Given a (non-null) CellTranform class, returns the Rotation class that
     * is used in the cell setup information.
     * 
     * @param transform The cell's transform
     * @return The rotation used in the cell setup information
     * @deprecated
     */
    public static Quaternion getSetupRotation(CellTransform transform) {
        return transform.getRotation(null);
    }
    
    /**
     * Given a (non-null) CellTranform class, returns the Scale class that
     * is used in the cell setup information.
     * 
     * @param transform The cell's transform
     * @return The scaling used in the cell setup information
     * @deprecated
     */
    public static Vector3f getSetupScaling(CellTransform transform) {
        return transform.getScaling(null);

    }
} 
