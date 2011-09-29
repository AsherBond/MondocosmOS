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
package org.jdesktop.wonderland.modules.avatarbase.common.cell;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A class that stores the basic information about the avatar configuration.
 * This class is JAXB annotated so that is may be serialized in a Cell's server
 * state.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="avatar-config-info")
public class AvatarConfigInfo implements Serializable {
    // The URL of the configuration file on the server that describes the
    // avatar. If null, use the "default" avatar.
    @XmlElement(name="avatar-config-url")
    private String avatarConfigURL = null;

    // The fully-qualified class name that gives the avatar loader factory
    @XmlElement(name = "loader-factory-class-name")
    private String loaderFactoryClassName = null;

    /** Default constructor, needed for JAXB */
    public AvatarConfigInfo() {
    }
    
    /** Constructor */
    public AvatarConfigInfo(String modelConfigURL, String className) {
        this.avatarConfigURL = modelConfigURL;
        this.loaderFactoryClassName = className;
    }

    /**
     * A string URL that represents the avatar configuration.
     *
     * @return The avatar configuration URL
     */
    @XmlTransient()
    public String getAvatarConfigURL() {
        return avatarConfigURL;
    }

    /**
     * Returns the fully-qualified class name of the factory that generates the
     * loader for this kind of avatar.
     *
     * @return The loader factory FQCN
     */
    @XmlTransient()
    public String getLoaderFactoryClassName() {
        return loaderFactoryClassName;
    }
}
