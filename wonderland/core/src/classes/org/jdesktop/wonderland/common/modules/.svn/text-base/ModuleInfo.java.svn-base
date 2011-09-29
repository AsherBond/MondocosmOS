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
package org.jdesktop.wonderland.common.modules;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The ModuleInfo class represents the basic information about a module: its
 * unique name, its major.minor version, and a string description.
 * <p>
 * This class is annotation with JAXB XML elements and supports encoding and
 * decoding to/from XML via the encode() and decode() methods, respectively.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wonderland-module")
public class ModuleInfo implements Serializable {
    /* Flag indicating that a version component is unset */
    public static final int VERSION_UNSET = -1;
    
    /* The unique module name */
    @XmlElement(name="name", required=true)
    private String name = null;
    
    /* The version numbers */
    @XmlElement(name="version")
    private Version version = new ModuleInfo.Version();
    
    /* A textual description of the module */
    @XmlElement(name="description")
    private String description = null;
    
    /* A table of key-value parameters for the module */
    @XmlElements({
        @XmlElement(name="attribute")
    })
    private Attribute[] attributes = new Attribute[] {};
    
    /* The internal table of attributes */
    @XmlTransient
    private Map<String, String> attributeMap = new HashMap();
    
    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ModuleInfo.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ModuleInfo.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /**
     * The Version static inner class simply stores the major, minor, and mini
     * version numbers
     */
    public static class Version {
        /* The major, minor, and mini version numbers */
        @XmlElement(name="major")
        public int major = ModuleInfo.VERSION_UNSET;
        
        @XmlElement(name="minor")
        public int minor = ModuleInfo.VERSION_UNSET;

        @XmlElement(name="mini")
        public int mini = ModuleInfo.VERSION_UNSET;

        /** Default constructor */
        public Version() {}

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Version other = (Version) obj;
            if (this.major != other.major) {
                return false;
            }
            if (this.minor != other.minor) {
                return false;
            }
            if (this.mini != other.mini) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.major;
            hash = 71 * hash + this.minor;
            hash = 71 * hash + this.mini;
            return hash;
        }
    }
    
    /**
     * The Attribute inner class stores a string key-value pair
     */
    @XmlAccessorOrder(value=XmlAccessOrder.ALPHABETICAL)
    @XmlRootElement(name="attribute")
    public static class Attribute {
        /* The key and value strings */
        @XmlAttribute(name="key")
        public String key = null;
        
        @XmlAttribute(name="value")
        public String value = null;
                
        /** Default constructor */
        public Attribute() {}
        
        /** Constructor, takes key and value */
        public Attribute(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
    
    /** Default constructor */
    public ModuleInfo() {}
    
    /** Constructor which takes major/minor version number and description */
    public ModuleInfo(String name, int major, int minor, int mini, String description) {
        this(name, major, minor, mini);
        this.description = description;
    }
    
    /** Constructor which takes major/minor version number */
    public ModuleInfo(String name, int major, int minor, int mini) {
        /* Populate the basic elements of the module info */
        this.name          = name;
        this.version.major = major;
        this.version.minor = minor;
        this.version.mini  = mini;
    }
    
    /* Java Bean Setter/Getter methods */
    @XmlTransient public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    @XmlTransient public int getMajor() { return this.version.major; }
    public void setMajor(int major) { this.version.major = major; }
    @XmlTransient public int getMinor() { return this.version.minor; }
    public void setMinor(int minor) { this.version.minor = minor; }
    @XmlTransient public int getMini() { return this.version.mini; }
    public void setMini(int mini) { this.version.mini = mini; }
    @XmlTransient public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Returns a copy of the map of key-value pairs of attributes
     */
    public Map<String, String> getAttributes() {
        return new HashMap(this.attributeMap);
    }
    
    /**
     * Adds a key-value attribute pair to the attribute map
     */
    public void putAttribute(String key, String value) {
        this.attributeMap.put(key, value);
    }
    
    /**
     * Adds a map of key-value attribute pairs to the attribute map.
     * @param attributes
     */
    public void putAttibutes(Map<String, String> attributes) {
        this.attributeMap.putAll(attributes);
    }
    
    /**
     * Returns an attribute givens it key, or null if it does not exist
     */
    public String getAttribute(String key) {
        return this.attributeMap.get(key);
    }
    
    /**
     * Removes an attribute given its key. Does nothing if the attribute does
     * not exist
     */
    public void removeAttribute(String key) {
        this.attributeMap.remove(key);
    }
    
    /**
     * Returns the version as a string: <major>.<minor>
     */
    @Override
    public String toString() {
        return this.getName() + "(v" +
                Integer.toString(this.getMajor()) + "." +
                Integer.toString(this.getMinor()) + "." +
                Integer.toString(this.getMini()) + ")";
    }
    
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ModuleInfo class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to ModuleInfo
     * @throw JAXBException Upon error reading the XML file
     */
    public static ModuleInfo decode(Reader r) throws JAXBException {
        /* Read in from stream */
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ModuleInfo info = (ModuleInfo)unmarshaller.unmarshal(r);
        
        /* Convert array into hash map */
        info.attributeMap.clear();
        for (Attribute attribute : info.attributes) {
            info.attributeMap.put(attribute.key, attribute.value);
        }       
        return info;
    }
    
    /**
     * Writes the ModuleInfo class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        /* Convert the internal map into an array */
        List<Attribute> list = new LinkedList<Attribute>();
        Iterator<Map.Entry<String, String>> it = this.attributeMap.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, String> entry = it.next();
            list.add(new Attribute(entry.getKey(), entry.getValue()));
        }
        this.attributes = list.toArray(new Attribute[] {});
            
        /* Write out to the stream */
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

    /**
     * Writes the ModuleInfo class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        /* Convert the internal map into an array */
        List<Attribute> list = new LinkedList<Attribute>();
        Iterator<Map.Entry<String, String>> it = this.attributeMap.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, String> entry = it.next();
            list.add(new Attribute(entry.getKey(), entry.getValue()));
        }
        this.attributes = list.toArray(new Attribute[] {});
        
        /* Write out to the stream */
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }
    
    /**
     * Returns true if both the module name and version matches, false if not
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ModuleInfo)) {
            return false;
        }
        ModuleInfo info = (ModuleInfo)object;
        if (this.getName().equals(info.getName()) == false) {
            return false;
        }
        if (this.version.equals(info.version) == false) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 13 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }
}
