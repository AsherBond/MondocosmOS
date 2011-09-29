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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The ModuleArt class represents a piece of artwork within a module.
 * <p>
 * This class is annotation with JAXB XML elements and supports encoding and
 * decoding to/from XML via the encode() and decode() methods, respectively.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wl-module-art")
public class ModuleArt implements Serializable {
   
    /* The unique module art path */
    @XmlElement(name="path", required=true)
    private String path = null;

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ModuleArt.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ModuleArt.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }

    /** Default constructor */
    public ModuleArt() {}
    
    /** Constructor which takes relative art resource path name */
    public ModuleArt(String path) {
        this.path = path;
    }
    
    /* Java Bean Setter/Getter methods */
    @XmlTransient public String getPath() { return this.path; }
    public void setPath(String path) { this.path = path; }
 
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ModuleInfo class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to ModuleInfo
     * @throw JAXBException Upon error reading the XML file
     */
    public static ModuleArt decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (ModuleArt)unmarshaller.unmarshal(r);
    }
    
    /**
     * Writes the ModuleInfo class to an output writer.
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
