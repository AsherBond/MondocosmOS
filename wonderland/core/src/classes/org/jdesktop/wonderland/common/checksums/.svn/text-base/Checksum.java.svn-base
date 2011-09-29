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
package org.jdesktop.wonderland.common.checksums;

import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Checksum class represents an individual checksum for a resource. A checksum
 * consists of a hex-encoded checksum string, the date the file on disk was
 * last modified (milliseconds since the epoch), and the relative path of the
 * asset.
 * <p>
 * This class can also be encoded/decoded to/from XML via JAXB, suitable for
 * storing on disk or serializing across a network.
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class Checksum {
    /* The hex-encoded checksum string */
    @XmlElement(name="checksum-hex-encoded")
    private String checksum = null;
    
    /* The time the resource was last modified on disk, millisecs since epoch */
    @XmlElement(name="last-modified")
    private long lastModified = 0;
    
    /* The relative name of the resource in the repository */
    @XmlElement(name="resource-path")
    private String pathName = null;
    
    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(Checksum.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(Checksum.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /** Default constructor */
    public Checksum() {
    }

    /**
     * Constructor, takes the string checksum
     */
    public Checksum (String checksum) {
        this.checksum = checksum;
    }
    
    /**
     * Returns true if the given checksum is equal to this checksum, false if
     * not.
     * 
     * @param checksum Compare this checksum to the given checksum
     * @return True if the two checkums are equal, false if not
     */
    public boolean equals(Checksum checksum) {
        /*
         * If the two checksum string are not exactly equal, return false
         */
        return this.checksum.equals(checksum.getChecksum());
    }
    
    /**
     * Sets the hex-encoded checksum string.
     * 
     * @param checksum The hex-encoded checksum string
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    /**
     * Returns the hex-encoded checksum string.
     * 
     * @return The hex-encoded checksum string
     */
    @XmlTransient
    public String getChecksum() {
        return this.checksum;
    }
    
    /**
     * Sets the time the resource was last modified, in milliseconds since the
     * epoch.
     * 
     * @param lastModified The time the resource was last modified
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    
    /**
     * Returns the time the resource was last modified, in milliseconds since
     * the epoch.
     * 
     * @return The time the resource was last modified
     */
    @XmlTransient
    public long getLastModified() {
        return this.lastModified;
    }
    
    /**
     * Sets the resource path name, relative to the repository.
     * 
     * @param pathName The relative resource path name
     */
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
    
    /**
     * Returns the resource path name, relative to the repository.
     * 
     * @return The relative resource path name
     */
    @XmlTransient
    public String getPathName() {
        return this.pathName;
    }

    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the Checksum class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to Checksum
     * @throw JAXBException Upon error reading the XML file
     */
    public static Checksum decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Checksum)unmarshaller.unmarshal(r);
    }
    
    /**
     * Writes the Checksum class to an output writer.
     * <p>
     * @param os The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }
    
    /**
     * Converts the checksum given as an array of bytes into a hex-encoded
     * string.
     * 
     * @param bytes The checksum as an array of bytes
     * @return The checksum as a hex-encoded string
     */
    public static String toHexString(byte bytes[]) {
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            ret.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1));
        }
        return ret.toString();
    }
}
