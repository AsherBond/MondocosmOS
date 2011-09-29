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

import java.awt.Point;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

/**
 * An interpolator for position properties
 * @author nsimpson
 */
public class PointInterpolator implements PropertyInterpolator {

    public Point interpolate(Object from, Object to, float timelinePosition) {
        Point f = (Point) from;
        Point t = (Point) to;
        Point current = new Point();
        current.x = (int)(f.x + timelinePosition * (t.x - f.x));
        current.y = (int)(f.y + timelinePosition * (t.y - f.y));

        return current;
    }

    public Class getBasePropertyClass() {
        return null;
    }
}
