/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.modules.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
import org.jdesktop.wonderland.common.modules.ModuleInfo;

/**
 * The ModuleInfoList class represents a collection of ModuleInfo classes
 * serialized to XML. This is useful to send across a collection of module
 * info's or store a list of disk. It is used, for example, for the list of
 * modules to remove or uninstall.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wl-moduleinfo-list")
public class ModuleInfoList {
    /* An array of cell children names */
    @XmlElements({
        @XmlElement(name="module")
    })
    private ModuleInfo[] moduleInfos = new ModuleInfo[] {};
    
    /* The JAXBContext for later use */
    private static JAXBContext context = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleInfos */
    static {
        try {
            context = JAXBContext.newInstance(ModuleInfoList.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ModuleInfoList.class.getName()).log(Level.WARNING,
                    "Unable to get JAXBContext", excp);
        }
    }
        
    /** Default constructor */
    public ModuleInfoList() {
    }
    
    /** Constructor, takes the names of the modules */
    public ModuleInfoList(ModuleInfo[] moduleInfos) {
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
    public static ModuleInfoList decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (ModuleInfoList) unmarshaller.unmarshal(r);
    }
    
    /**
     * Writes the ModuleInfoList class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }
    
    public static void main(String args[]) throws JAXBException, IOException {
        ModuleInfo info1 = new ModuleInfo("mpk20", 1, 0, 0);
        ModuleInfo info2 = new ModuleInfo("default", 5, 2, 0);
        ModuleInfoList list = new ModuleInfoList(new ModuleInfo[] { info1, info2 });
        list.encode(new FileWriter("foo.xml"));
    }
}
