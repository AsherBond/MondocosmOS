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
package org.jdesktop.wonderland.common.timingframework;

import com.jme.math.Vector3f;
import com.sun.scenario.animation.Composer;

/**
 *
 * @author paulby
 */
public class ComposerVector3f extends Composer<Vector3f> {
     static {
//         Composer.register(Vector3f.class, ComposerVector3f.class);
     }

    public ComposerVector3f() {
          super(3);
      }
      public double[] decompose(Vector3f o, double[] v) {
          v[0] = o.getX();
          v[1] = o.getY();
          v[2] = o.getZ();
          return v;
      }
      public Vector3f compose(double[] v) {
          return new Vector3f((float)v[0], (float)v[1], (float)v[2]);
      }
}
