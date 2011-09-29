/*
 * Copyright (c) 2005-2009 Trident Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Trident Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.jdesktop.wonderland.modules.animationbase.client.toolkit;

import com.jme.math.Vector3f;
import java.awt.*;
import java.util.*;

import org.pushingpixels.trident.interpolator.PropertyInterpolator;
import org.pushingpixels.trident.interpolator.PropertyInterpolatorSource;

/**
 * Built-in interpolators for Wonderland JME
 *
 * @author Paul Byrne
 */
public class JMEPropertyInterpolators implements PropertyInterpolatorSource {
	private Set<PropertyInterpolator> interpolators;

	public JMEPropertyInterpolators() {
		this.interpolators = new HashSet<PropertyInterpolator>();
		this.interpolators.add(new Vector3fInterpolator());
//		this.interpolators.add(new PointInterpolator());
//		this.interpolators.add(new RectangleInterpolator());
	}

	@Override
	public Set<PropertyInterpolator> getPropertyInterpolators() {
		return Collections.unmodifiableSet(this.interpolators);
	}

	static class Vector3fInterpolator implements PropertyInterpolator<Vector3f> {
		@Override
		public Class getBasePropertyClass() {
                    return Vector3f.class;
		}

		@Override
		public Vector3f interpolate(Vector3f from, Vector3f to, float timelinePosition) {
                    return new Vector3f(from.x + (timelinePosition * (to.x - from.x)),
                            from.y + (timelinePosition * (to.y - from.y)),
                            from.z + (timelinePosition * (to.z - from.z)));
		}
	}

	static class PointInterpolator implements PropertyInterpolator<Point> {
		public Point interpolate(Point from, Point to, float timelinePosition) {
			int x = from.x + (int) (timelinePosition * (to.x - from.x));
			int y = from.y + (int) (timelinePosition * (to.y - from.y));
			return new Point(x, y);
		}

		@Override
		public Class getBasePropertyClass() {
			return Point.class;
		}
	}

	static class RectangleInterpolator implements
			PropertyInterpolator<Rectangle> {
		public Rectangle interpolate(Rectangle from, Rectangle to,
				float timelinePosition) {
			int x = from.x + (int) (timelinePosition * (to.x - from.x));
			int y = from.y + (int) (timelinePosition * (to.y - from.y));
			int w = from.width
					+ (int) (timelinePosition * (to.width - from.width));
			int h = from.height
					+ (int) (timelinePosition * (to.height - from.height));
			return new Rectangle(x, y, w, h);
		}

		@Override
		public Class getBasePropertyClass() {
			return Rectangle.class;
		}
	}
}
