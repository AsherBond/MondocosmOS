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
package org.jdesktop.wonderland.modules.audiomanager.common;

/**
 * Convert integer volume to float.
 * 
 * @author  jprovino
 */
public class VolumeConverter {

    public static final float DEFAULT_MAX_VOLUME = 4;

    private int minInt;
    private int maxInt;
    private float maxFloat;
    private int middle;
    private float increment;
    private float plusIncrement;

    /*
     * Convert a slider integer value to a float value.
     * The minimum float value is 0.  The center value of the slider
     * represents a value of 1.  
     * Slider values below the middle are evenly divided down to 0.
     * Slider values above the middle are evently divided so as to
     * get to the maxFloat value when the slider is at maxInt.
     */
    public VolumeConverter(int maxInt) {
	this(0, maxInt, DEFAULT_MAX_VOLUME);
    }

    public VolumeConverter(int minInt, int maxInt) {
	this(minInt, maxInt, DEFAULT_MAX_VOLUME);
    }

    public VolumeConverter(int minInt, int maxInt, float maxFloat) {
	this.minInt = minInt;
	this.maxInt = maxInt;
	this.maxFloat = maxFloat;
	
	middle = (maxInt - minInt) / 2;

	increment = 1f / (maxInt - minInt);

	plusIncrement = (maxFloat - 1) / middle;
    }

    public float getVolume(int volume) {
	float v;

	if (volume <= middle) {
	    v = volume * 2 * increment;
	} else {
	    v = 1 + ((volume - middle) * plusIncrement);
	}
	
	return v;
    }

    public int getVolume(float volume) {
	if (volume == 1) {
	    return middle;
	}

	if (volume == 0) {
	    return 0;
	}

	if (volume < 1) {
	    return (int) (volume / (2 * increment));
	} 

	return (int) (middle + ((volume - 1) / plusIncrement));
    }

}
