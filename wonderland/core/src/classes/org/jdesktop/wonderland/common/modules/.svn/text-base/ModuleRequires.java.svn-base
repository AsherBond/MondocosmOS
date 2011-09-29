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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
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

/**
 * The ModuleRequires class represents a dependency this module has on another
 * module. Dependencies are declared using the unique name of the module and an
 * optional major.minor version. The version may be unspecified, in which case
 * any version is acceptable.
 * <p>
 * This convenience method isAcceptable() takes an instance of a module and
 * returns true whether it satisfies the dependency specified by the instance
 * of the ModuleRequires class.
 * <p>
 * This class is annotation with JAXB XML elements and supports encoding and
 * decoding to/from XML via the encode() and decode() methods, respectively.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="module-requires")
public class ModuleRequires {
    
    /* An array of modules that are dependencies */
    @XmlElements({
        @XmlElement(name="requires")
    })
    private ModuleInfo[] requires = new ModuleInfo[] {};

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ModuleRequires.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ModuleRequires.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /** Default constructor */
    public ModuleRequires() {
    }
    
    /** Constructor which takes an array of dependencies */
    public ModuleRequires(ModuleInfo[] requires) {
        this.requires = requires;
    }
    
    /* Java Bean Setter/Getter methods */
    @XmlTransient public ModuleInfo[] getRequires() { return this.requires; }
    public void setRequires(ModuleInfo[] requires) { this.requires = requires; }
    
    /**
     * Returns the list of module dependencies as a string: name vX.Y
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (ModuleInfo info : requires) {
            str.append("  " + info.toString() + " ");
        }
        return str.toString();
    }
    
    /**
     * Returns true if the given modules is required by this requirement set,
     * false if not. Only the unique name is checked.
     * 
     * @param uniqueName The module name to check if it is required
     * @return True if the given module is required, false if not
     */
    public boolean isRequired(String uniqueName) {
        for (ModuleInfo info : this.requires) {
            if (uniqueName.compareTo(info.getName()) == 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Takes the input stream of the XML file and instantiates an instance of
     * the ModuleRequires class
     * <p>
     * @param is The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to ModuleRequires
     * @throw JAXBException Upon error reading the XML file
     */
    public static ModuleRequires decode(InputStream is) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (ModuleRequires)unmarshaller.unmarshal(is);        
    }

    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ModuleRequires class
     * <p>
     * @param r The input reader of the requires XML file
     * @throw ClassCastException If the input file does not map to ModuleRequires
     * @throw JAXBException Upon error reading the XML file
     */
    public static ModuleRequires decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (ModuleRequires)unmarshaller.unmarshal(r);        
    }
    
    /**
     * Writes the ModuleInfo class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }
}
