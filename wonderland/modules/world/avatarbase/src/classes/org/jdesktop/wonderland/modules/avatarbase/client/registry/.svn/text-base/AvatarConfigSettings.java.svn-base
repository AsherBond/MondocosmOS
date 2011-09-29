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
package org.jdesktop.wonderland.modules.avatarbase.client.registry;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ResourceBundle;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents the avatar-wide configuration settings, for example, the name
 * of the avatar configuration currently in use. This method is JAXB annotated
 * to be (de)serialization from/to XML.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name = "avatar-config-settings")
public class AvatarConfigSettings implements Serializable {

    private static final ResourceBundle BUNDLE =
            ResourceBundle.getBundle("org/jdesktop/wonderland/modules/" +
            "avatarbase/client/resources/Bundle");

    /**
     * The name of the "default" avatar to use (in case none is set). Perhaps
     * not the best way to do this -- the name here needs to be kept in sync
     * with the default avatar names in BasicAvatarFactory.java.
     */
    public static final String DEFAULT_NAME = BUNDLE.getString("Cartoon_Male");

    // The name of the avatar configuration current in-use
    @XmlElement(name = "avatar-in-use")
    private String avatarNameInUse = DEFAULT_NAME;

    // The JAXB content to (de)serialize to/from XML
    private static JAXBContext jaxbContext = null;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(AvatarConfigSettings.class);
        } catch (javax.xml.bind.JAXBException excp) {
            excp.printStackTrace();
        }
    }

    /** Default constructor */
    public AvatarConfigSettings() {
    }

    /**
     * Returns the name of the avatar configuration currently in-use.
     *
     * @return The in-use avatar configuration name (or null)
     */
    @XmlTransient
    public String getAvatarNameInUse() {
        return avatarNameInUse;
    }

    /**
     * Sets the avatar configuration name currently in use, or null for none.
     *
     * @param avatarNameInUse A name of an avatar configuration, or null
     */
    public void setAvatarNameInUse(String avatarNameInUse) {
        this.avatarNameInUse = avatarNameInUse;
    }

    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the AvatarConfigSettings class
     * <p>
     * @param r The input reader of the version XML file
     * @return the decoded and instantiated AvatarConfigSettings
     * @throw ClassCastException If the input file does not map to
     * AvatarConfigSettings
     * @throws JAXBException Upon error reading the XML file
     */
    public static AvatarConfigSettings decode(Reader r) throws JAXBException {
        // Unmarshall the XML into a AvatarConfigSettings class
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (AvatarConfigSettings) unmarshaller.unmarshal(r);
    }

    /**
     * Writes the AvatarConfigSettings class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throws JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        // Marshall the PlacemarkList class into XML
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }
}
