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
package org.jdesktop.wonderland.modules.hud.client;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUDObject;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

/**
 * A class for running Timeline based animations.
 *
 * @author nsimpson
 */
public class HUDAnimator implements Runnable {

    private static final Logger logger = Logger.getLogger(HUDAnimator.class.getName());
    private HUDObject animatee;
    private HUDView2D animateeView;
    private Timeline timeline;
    public static final long DEFAULT_DURATION = 500; // 500 milliseconds

    public HUDAnimator(HUDObject animatee, String property, PropertyInterpolator interpolator,
            Object from, Object to) {
        this(animatee, property, interpolator, from, to, DEFAULT_DURATION);
    }

    public HUDAnimator(HUDObject animatee, String property, PropertyInterpolator interpolator,
            Object from, Object to, long duration) {
        this.animatee = animatee;
        try {
            timeline = new Timeline(animatee);
        } catch (Exception e) {
            logger.warning("failed to correctly initialize trident library");
        }
        timeline.addPropertyToInterpolate(property, from, to, interpolator);
        timeline.setDuration(duration);
    }

    public HUDAnimator(HUDView2D animatee, String property, PropertyInterpolator interpolator,
            Object from, Object to) {
        this(animatee, property, interpolator, from, to, DEFAULT_DURATION);

    }

    public HUDAnimator(HUDView2D animateeView, String property, PropertyInterpolator interpolator,
            Object from, Object to, long duration) {
        this.animateeView = animateeView;
        try {
            timeline = new Timeline(animateeView);
        } catch (Exception e) {
            logger.warning("failed to correctly initialize trident library");
        }
        timeline.addPropertyToInterpolate(property, from, to, interpolator);
        timeline.setDuration(duration);
    }

    public boolean isAnimating() {
        return ((timeline != null) && !timeline.isDone());
    }

    public void cancel() {
        if ((timeline != null) && !timeline.isDone()) {
            timeline.cancel();
        }
    }

    public void run() {
        if (timeline != null) {
            timeline.play();
        }
    }
}
