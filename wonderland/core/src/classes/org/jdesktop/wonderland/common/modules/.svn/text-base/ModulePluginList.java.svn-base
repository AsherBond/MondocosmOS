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
package org.jdesktop.wonderland.common.modules;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.JarURI;
import org.jdesktop.wonderland.common.JarURIAdapter;


/**
 * A list of module plugins, given by URIs. These URIs are of the format:
 * <p>
 * wlj://<module name>/<jar path>
 * <p>
 * where <module name> is the name of the module, and <jar path> is the path
 * of the jar within the module, e.g. "server/myplugin-server.jar".
 * <p>
 * This class deserializes information distributed from the module service.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="module-plugin-list")
public class ModulePluginList implements Serializable {
    
    /* An array of module plugin JAR URIs */
    @XmlElements({
        @XmlElement(name="jar-uri")
    })
    private JarURI[] jarURIs = null;
    
    /* The JAXB context that generates marshallers and unmarshallers */
    private static JAXBContext jaxbContext = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleRepositorys */
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ModulePluginList.class, JarURI.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ModulePluginList.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /** Default constructor */
    public ModulePluginList() {}
    
    /* Setters and getters */
    @XmlTransient public JarURI[] getJarURIs() { return this.jarURIs; }
    public void setJarURIs(JarURI[] jarURIs) { this.jarURIs = jarURIs; }
    
    /**
     * Returns the list of repositories encoded as a string
     */
    @Override
    public String toString() {
        return this.jarURIs.toString();
    }
     
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ModulePluginList class
     * <p>
     * @param r The input stream of the XML data
     * @param server The server name and port as <server name>:<port>
     * @throw ClassCastException If the input data does not map to ModulePluginList
     * @throw JAXBException Upon error reading the XML data
     */
    public static ModulePluginList decode(Reader r, String server) throws JAXBException {
        Unmarshaller u = jaxbContext.createUnmarshaller();
        JarURIAdapter adapter = new JarURIAdapter(server);
        u.setAdapter(adapter);
        return (ModulePluginList)u.unmarshal(r);
    }
    
    /**
     * Writes the ModuleRepository class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty("jaxb.formatted.output", true);
        m.marshal(this, w);
    }
}
