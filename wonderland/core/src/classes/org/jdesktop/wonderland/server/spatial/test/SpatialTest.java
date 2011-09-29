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
package org.jdesktop.wonderland.server.spatial.test;

import com.jme.bounding.BoundingBox;
import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.spatial.impl.SpatialCell;
import org.jdesktop.wonderland.server.spatial.impl.SpatialCell;
import org.jdesktop.wonderland.server.spatial.impl.UniverseImpl;

/**
 *
 * @author paulby
 */
public class SpatialTest {

    private UniverseImpl universe = new UniverseImpl(null, null, null);
    private long id = CellID.getFirstCellID();

//    public SpatialTest() {
//        SpatialCell c = createCell(new Vector3f(1,0,0), 10);
//        SpatialCell c2 = createCell(new Vector3f(3,0,0),4);
//        SpatialCell c3 = createCell(new Vector3f(3,0,2),3);
//        c2.addChild(c3);
//        c.addChild(c2);
//
//        universe.addRootSpatialCell(c);
//
//        universe.addRootSpatialCell(createCell(new Vector3f(100,0,0), 4));
//        universe.addRootSpatialCell(createCell(new Vector3f(130,1,1), 4));
//        universe.addRootSpatialCell(createCell(new Vector3f(150,2,2), 4));
//
//        SpatialCell v = createCell(new Vector3f(), 100, true);
//        universe.addRootSpatialCell(v);
//        ViewMover mover = new ViewMover(v, new Vector3f(0,0,0), new Vector3f(300,0,0));
//        mover.start();
//    }
//
//    private SpatialCell createCell(Vector3f translation, float size) {
//        return createCell(translation, size, false);
//    }
//
//    private SpatialCell createCell(Vector3f translation, float size, boolean view) {
//        SpatialCell ret = universe.createSpatialCell(new CellID(id++), null, null);
//        Matrix4f m4f = new Matrix4f();
//        m4f.setTranslation(translation);
//        ret.setLocalTransform(m4f);
//        ret.setLocalBounds(new BoundingBox(new Vector3f(), size, size, size));
//        return ret;
//    }
//
//    public static void main(String[] args) {
//        new SpatialTest();
//    }
//
//    class ViewMover extends Thread {
//        private SpatialCell cell;
//        private Vector3f start;
//        private Vector3f end;
//        private Vector3f pos = new Vector3f();
//        private float perc = 0f;   // percentage between 0.0 and 1.0
//        private float inc = 0.1f;
//        private Matrix4f transform;
//
//        public ViewMover(SpatialCell cell, Vector3f start, Vector3f end) {
//            this.cell = cell;
//            this.start = new Vector3f(start);
//            this.end = new Vector3f(end);
//            transform = ((SpatialCell)cell).getWorldTransform();
//        }
//
//        public void run() {
//            while(true) {
//                pos.x = start.x + (end.x - start.x)*perc;
//                pos.y = start.y + (end.y - start.y)*perc;
//                pos.z = start.z + (end.z - start.z)*perc;
//
//                transform.setTranslation(pos);
//                cell.setLocalTransform(transform);
//
//                perc += inc;
//                if (perc>1.0 || perc < 0.0) {
//                    System.exit(0);
//                    inc *= -1;
//                    perc+=inc;
//                }
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(SpatialTest.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
}
