/*
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
package org.jdesktop.wonderland.modules.animationbase.client.interpolators;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

/**
 * An interpolator for Quaternion coordinates
 *
 * Usage:
 *   toTurn.fromAngleAxis((float) (Math.PI / 12), new Vector3f(0, 1, 0));
 *   toRoll.fromAngleAxis((float) -(Math.PI / 12), new Vector3f(1, 0, 0));
 *
 *   step = toTurn.mult(initialQuat);
 *   to = toRoll.mult(step);
 *
 *   timeline.addPropertyToInterpolate("RotQuat", initialQuat, to, new QuaternionInterpolator());
 * 
 * @author morrisford
 */
public class QuaternionInterpolator implements PropertyInterpolator {

    public Quaternion interpolate(Object from, Object to, float timelinePosition) {
        Vector3f fromAxis = new Vector3f();
        Vector3f toAxis = new Vector3f();
        Quaternion newQuat = new Quaternion();

        float fromAngle = ((Quaternion)from).toAngleAxis(fromAxis);
        float toAngle = ((Quaternion)to).toAngleAxis(toAxis);

        newQuat.fromAngleAxis(fromAngle + (toAngle - fromAngle) * timelinePosition,
                new Vector3f(fromAxis.x + (toAxis.x - fromAxis.x) * timelinePosition,
                fromAxis.y + (toAxis.y - fromAxis.y) * timelinePosition,
                fromAxis.z + (toAxis.z - fromAxis.z) * timelinePosition));
        return newQuat;
    }

    public Class getBasePropertyClass() {
        return null;
    }
}
