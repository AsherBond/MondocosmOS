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
package org.jdesktop.wonderland.modules.placemarks.api.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class that stores information about a placemark. A placemark consists of
 * an identifying name, the URL of the server, the (x, y, z) of the transport
 * location, and an initial "look" direction, expressed as an angle (in degrees)
 * of rotation about the +y axis, where 0 degrees faces the +x direction).
 * <p>
 * This class overrides the equals() and hashCode() method: two Placemark objects
 * are equal iff the names are equal. This implies that Placemarks must have
 * unique names.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="placemark")
public class Placemark implements Serializable {

    @XmlElement(name="name") private String name = null;
    @XmlElement(name="server-url") private String url = null;
    @XmlElement(name="x") private float x = 0.0f;
    @XmlElement(name="y") private float y = 0.0f;
    @XmlElement(name="z") private float z = 0.0f;
    @XmlElement(name="angle") private float angle = 0.0f;

    /**
     * Default constructor, needed for JAXB
     */
    public Placemark() {
    }
    
    /**
     * Constructor, takes the name, server URL, (x, y, z) and initial look angle
     * as arguments.
     *
     * @param name The identifying name of the placemark
     * @param url The URL of the server destination
     * @param x The x-coordinate of the transport destination
     * @param y The y-coordinate of the transport destination
     * @param z The z-coordinate of the transport destination
     * @param angle The initial look direction angle
     */
    public Placemark(String name, String url, float x, float y, float z, float angle) {
        this.name = name;
        this.url = url;
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
    
    public float getAngle() {
        return angle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Placemark other = (Placemark) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
