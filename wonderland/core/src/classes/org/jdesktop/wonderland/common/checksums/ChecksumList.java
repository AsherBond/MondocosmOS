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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
 * The ChecksumList class represents a collection of checkums.
 * <p>
 * This class uses JAXB to encode/decode the class to/from XML, either on disk
 * or over the network
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="checksum-list")
public class ChecksumList {
    /* The error logger */
    private static final Logger logger = Logger.getLogger(ChecksumList.class.getName());

    /* The SHA-1 checksum algorithm */
    public final static String SHA1_CHECKSUM_ALGORITHM = "SHA-1";
    
    /* A list of checksum entries */
    @XmlElements({
        @XmlElement(name="checksum")
    })
    public LinkedList<Checksum> checksumList = new LinkedList<Checksum>();

    /*
     * The internal representation of the checksums as a hashed map. The HashMap
     * class is not supported by JAXB so we must convert it to a list for
     * serialization
     */
    @XmlTransient
    public Map<String, Checksum> internalChecksums = new HashMap<String, Checksum>();
    
    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ChecksumList.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ChecksumList.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /** Default constructor */
    public ChecksumList() {
    }
    
    /**
     * Sets the array of individual checksums.
     * 
     * @param checksumMap A map of Checksum objects
     */
    public synchronized void setChecksums(Map<String, Checksum> checksumMap) {
       internalChecksums.clear();
       internalChecksums.putAll(checksumMap);
       for (String assetPath : checksumMap.keySet()) {
           checksumList.add(checksumMap.get(assetPath));
       }
    }
    
    /**
     * Returns a copy of the map of checksums
     * 
     * @return A Map of asset path and checksums
     */
    @XmlTransient
    public synchronized Map<String, Checksum> getChecksumMap() {
        return new HashMap(internalChecksums);
    }

    /**
     * Returns the checksum for the given asset path, null if none exists.
     *
     * @param asestPath The path to the asset
     * @return The Checksum for the asset
     */
    public synchronized Checksum getChecksum(String assetPath) {
        return internalChecksums.get(assetPath);
    }

    /**
     * Puts a single checksum into the list
     *
     * @param checksum The Checksum to place into the list
     */
    public synchronized void putChecksum(Checksum checksum) {
        internalChecksums.put(checksum.getChecksum(), checksum);
        checksumList.add(checksum);
    }

    /**
     * Takes a map of checksums and puts it in this map, overwriting any existing
     * entries.
     * 
     * @param checksumMap A map of Checksums objects to add
     */
    public synchronized void putChecksums(Map<String, Checksum> checksumMap) {
        internalChecksums.putAll(checksumMap);
        for (String assetPath : checksumMap.keySet()) {
           checksumList.add(checksumMap.get(assetPath));
       }
    }
    
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ChecksumList class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to ChecksumList
     * @throw JAXBException Upon error reading the XML file
     */
    public static ChecksumList decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ChecksumList rc = (ChecksumList)unmarshaller.unmarshal(r);
        
        // Populate the internal map of checksums from the linked list.
        for (Checksum checksum : rc.checksumList) {
            rc.internalChecksums.put(checksum.getPathName(), checksum);
        }
        return rc;
    }
    
    /**
     * Writes the ChecksumList class to an output writer.
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
