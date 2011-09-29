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

import com.jme.math.Vector2f;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

/**
 * An interpolator for vector properties
 * @author nsimpson
 */
public class Vector2fInterpolator implements PropertyInterpolator {

    public Vector2f interpolate(Object from, Object to, float timelinePosition) {
        Vector2f f = (Vector2f) from;
        Vector2f t = (Vector2f) to;
        Vector2f position = new Vector2f();

        position.x = f.x + timelinePosition * (t.x - f.x);
        position.y = f.y + timelinePosition * (t.y - f.y);

        return position;
    }

    public Class getBasePropertyClass() {
        return null;
    }
}
