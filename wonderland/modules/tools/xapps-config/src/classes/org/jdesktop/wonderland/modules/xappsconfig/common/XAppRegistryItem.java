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
package org.jdesktop.wonderland.modules.xappsconfig.common;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A class that stores information about how to run an X App. This information
 * is used to store in the content repository and can populate the cell
 * registry information.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="x-app-registry-item")
public class XAppRegistryItem implements Serializable {

    /* The JAXB content to (de)serialize to/from XML */
    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(XAppRegistryItem.class);
        } catch (javax.xml.bind.JAXBException excp) {
            excp.printStackTrace();
        }
    }

    @XmlElement(name="app-name") private String appName = null;
    @XmlElement(name="command") private String command = null;

    /**
     * Default constructor, needed for JAXB
     */
    public XAppRegistryItem() {
    }
    
    /**
     * Constructor, takes the app name (to display in Cell palettes) and the
     * command to launch the app.
     *
     * @param appName The name of the application to use in palettes
     * @param command The command to launch the application
     */
    public XAppRegistryItem(String appName, String command) {
        this.appName = appName;
        this.command = command;
    }

    /**
     * Returns the app name.
     * @return The name of the app
     */
    @XmlTransient
    public String getAppName() {
        return appName;
    }

    /**
     * Returns the command to launch the app.
     * @return The command to launch the app
     */
    @XmlTransient
    public String getCommand() {
        return command;
    }
    
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the XAppRegistryItem class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to XAppRegistryItem
     * @throw JAXBException Upon error reading the XML file
     */
    public static XAppRegistryItem decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (XAppRegistryItem)unmarshaller.unmarshal(r);
    }

    /**
     * Writes the XAppRegistryItemList class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XAppRegistryItem other = (XAppRegistryItem) obj;
        if ((this.appName == null) ? (other.appName != null) : !this.appName.equals(other.appName)) {
            return false;
        }
        if ((this.command == null) ? (other.command != null) : !this.command.equals(other.command)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.appName != null ? this.appName.hashCode() : 0);
        hash = 83 * hash + (this.command != null ? this.command.hashCode() : 0);
        return hash;
    }
}
