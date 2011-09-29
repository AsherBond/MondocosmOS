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
package org.jdesktop.wonderland.server.spatial.impl;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author paulby
 */
class SpaceManagerGridImpl implements SpaceManager {

    static final int SPACE_SIZE = 50; // Radius

    private HashMap<String, Space> spaces = new HashMap();
 
    public void initialize() {
    }
    
    /**
     * Return the space that encloses this point, if the space does not exist, create it
     * @param position
     * @return
     */
    public Iterable<Space> getEnclosingSpace(BoundingVolume volume) {
        ArrayList retList = new ArrayList();

        Vector3f point = volume.getCenter();

        float xf = ((point.x+SPACE_SIZE) / (SPACE_SIZE*2));
        float yf = ((point.y+SPACE_SIZE) / (SPACE_SIZE*2));
        float zf = ((point.z+SPACE_SIZE) / (SPACE_SIZE*2));

        int x,y,z;

        if (xf>0)
            x = (int) Math.floor(xf);
        else
            x = (int) Math.ceil(xf);
        if (yf>0)
            y = (int) Math.floor(yf);
        else
            y = (int) Math.ceil(yf);
        if (zf>0)
            z = (int) Math.floor(zf);
        else
            z = (int) Math.ceil(zf);

        // Get the space that encloses the center of the volume
        Space sp = getEnclosingSpaceImpl(x,y,z);
        
        if (sp==null) {
            sp = createSpace(x, y, z);
//            System.err.println("Created space "+point+"  "+sp.getName()+" "+sp);
//
        }

        // Debug test. TODO REMOVE
//        if (!sp.getWorldBounds().contains(point))
//            Logger.getLogger(SpaceManagerGridImpl.class.getName()).warning("BAD ENCLOSING SPACE "+sp.getWorldBounds()+"  does not contain "+point+"   name "+getSpaceBindingName(x, y, z));
        
        retList.add(sp);

        int xStep;
        int yStep;
        int zStep;

        // Now get all the other spaces within the volume
        if (volume instanceof BoundingBox) {
            xStep = 1+(int) (((BoundingBox)volume).xExtent / (SPACE_SIZE));
            yStep = 1+(int) (((BoundingBox)volume).yExtent / (SPACE_SIZE));
            zStep = 1+(int) (((BoundingBox)volume).zExtent / (SPACE_SIZE));
        } else if (volume instanceof BoundingSphere) {
            xStep = yStep = zStep = 1+(int) (((BoundingSphere)volume).getRadius() / (SPACE_SIZE));
        } else
            throw new RuntimeException("Bounds not supported "+volume.getClass().getName());

//        System.out.println("RADIUS "+radius+"  step "+step);
//        System.err.println("Bounds "+volume);
//        System.err.println("Current "+x+", "+y+", "+z);
//        System.err.println("In space "+getSpaceBindingName(x, y, z)+"   step="+xStep+", "+yStep+", "+zStep);
        // TODO this is brute force, is there a better way ?
        for(int xs=-xStep; xs<=xStep; xs++) {
            for(int ys=-yStep; ys<=yStep; ys++) {
                for(int zs=-zStep; zs<=zStep; zs++) {
                    sp = getEnclosingSpaceImpl(x+xs, y+ys, z+zs);
                    if (sp==null){
                        // Create the space
                        sp = createSpace(x+xs, y+ys, z+zs);
//                        System.out.println("Creating "+sp.getName()+"  "+sp.getWorldBounds());
                    }
//                    System.err.print("Checking "+(x+xs)+", "+(y+ys)+", "+(z+zs)+" "+sp.getWorldBounds());

//                    System.err.println(sp.getName()+"  "+sp.getWorldBounds()+"  "+sp.getWorldBounds().intersects(volume));
                    if (sp.getWorldBounds().intersects(volume)) {
//                        System.err.println("  +");
                        retList.add(sp);
                    } else {
//                        System.err.println();
                    }
                }
            }
        }

        return retList;
    }

    private Space createSpace(int x, int y, int z) {
        
        Vector3f center = new Vector3f(((x) * SPACE_SIZE*2),
                                       ((y) * SPACE_SIZE*2),
                                       ((z) * SPACE_SIZE*2));
        BoundingBox gridBounds = new BoundingBox(center, 
                                                 SPACE_SIZE, 
                                                 SPACE_SIZE, 
                                                 SPACE_SIZE);

        String bindingName = getSpaceBindingName(x, y, z);
        Space space = new Space(gridBounds, bindingName);
        synchronized(spaces) {
            spaces.put(bindingName, space);
        }
        
        return space;
    }
    
    /**
     * Return the space that encloses this point, or null if the space does not exist.
     * @param position
     * @return
     */
    private Space getEnclosingSpaceImpl(int x, int y, int z) {
        synchronized(spaces) {
            return spaces.get(getSpaceBindingName(x,y,z));
        }
    }
    
    private String getSpaceBindingName(int x, int y, int z) {
        return x+"_"+y+"_"+z;
    }
 
}
