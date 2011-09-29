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
package org.jdesktop.wonderland.client.hud;

/**
 * Defines layout positions based on compass points.
 *
 * @author nsimpson
 */
public interface CompassLayout {

    /**
     * Defines compass positions
     */
    public enum Layout {

        /**
         * Not positioned using compass point positions
         */
        NONE,
        /**
         * Position at center
         */
        CENTER,
        /**
         * Position at north (top center)
         */
        NORTH,
        /**
         * Position at south (bottom center)
         */
        SOUTH,
        /**
         * Position at west (left center)
         */
        WEST,
        /**
         * Position at east (right center)
         */
        EAST,
        /**
         * Position at northwest (top left)
         */
        NORTHWEST,
        /**
         * Position at northeast (top right)
         */
        NORTHEAST,
        /**
         * Position at southwest (bottom left)
         */
        SOUTHWEST,
        /**
         * Position at southeast (bottom right)
         */
        SOUTHEAST
    };
}
