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
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The ModuleList class represents a collection of ModuleInfo classes
 * serialized to XML. This is useful to send across a collection of module
 * info's or store a list of disk. It is used, for example, for the list of
 * modules to remove or uninstall.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wl-module-list")
public class ModuleList {
    /* An array of cell children names */
    @XmlElementRefs({
        @XmlElementRef()
    })
    private ModuleInfo[] moduleInfos = new ModuleInfo[] {};

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ModuleInfo.class, ModuleList.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ModuleList.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
        
    /** Default constructor */
    public ModuleList() {
    }
    
    /** Constructor, takes the names of the modules */
    public ModuleList(ModuleInfo[] moduleInfos) {
        this.moduleInfos = moduleInfos;
    }

    /**
     * Returns the array of module names.
     * 
     * @return An array of module names
     */
    @XmlTransient public ModuleInfo[] getModuleInfos() {
        return this.moduleInfos;
    }
    
    /**
     * Sets the list of module info objects
     * 
     * @param moduleInfo An array of module info objects
     */
    public void setModuleInfos(ModuleInfo[] moduleInfos) {
        this.moduleInfos = moduleInfos;
    }
    
    /**
     * 
     * Takes the input stream of the XML and instantiates an instance of
     * the ModuleInfoList class
     * <p>
     * @param r The input reader of the XML representation
     * @throw ClassCastException If the input file does not map to WFSCellList
     * @throw JAXBException Upon error reading the XML stream
     */
    public static ModuleList decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ModuleList children = (ModuleList)unmarshaller.unmarshal(r);
        return children;
    }
    
    /**
     * Writes the ModuleInfoList class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }
}
