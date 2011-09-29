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

import org.pushingpixels.trident.interpolator.PropertyInterpolator;

/**
 * An interpolator for float properties
 * @author nsimpson
 */
public class FloatInterpolator implements PropertyInterpolator {

    public Float interpolate(Object from, Object to, float timelinePosition) {
        float f = (Float) from;
        float t = (Float) to;
        return f + timelinePosition * (t - f);
    }

    public Class getBasePropertyClass() {
        return null;
    }
}
