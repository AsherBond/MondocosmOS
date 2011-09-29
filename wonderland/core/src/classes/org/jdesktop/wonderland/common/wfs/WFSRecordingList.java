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
package org.jdesktop.wonderland.common.wfs;

import java.io.InputStream;
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

/**
 * The WFSRecordingList class simply represents an array of WFS recording names. It is used
 * to serialize this list across a network in XML form or out to disk.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Bernard Horan
 */
@XmlRootElement(name="wfs-recordings")
public class WFSRecordingList {
    /* An array of WFS recording names */
    @XmlElements({
        @XmlElement(name="recording")
    })
    private String[] recordings = new String[0];

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(WFSRecordingList.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(WFSRecordingList.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /** Default constructor */
    public WFSRecordingList() {
    }
    
    /** Constructor, takes the names of the recordings, which may be null */
    public WFSRecordingList(String[] recordings) {
        this.recordings = recordings;
    }
    
    /**
     * Returns the array of WFS recording names, null if there are none.
     * 
     * @return An array of WFS recording names
     */
    @XmlTransient public String[] getRecordings() {
        return this.recordings;
    }
    
    /**
     * Takes the input stream of the XML and instantiates an instance of
     * the WFSREcordingList class
     * <p>
     * @param is The input stream of the XML representation
     * @throw ClassCastException If the input file does not map to WFSREcordingList
     * @throw JAXBException Upon error reading the XML stream
     */
    public static WFSRecordingList decode(InputStream is) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (WFSRecordingList)unmarshaller.unmarshal(is);        
    }
    
    /**
     * Writes the WFSREcordingList class to an output stream.
     * <p>
     * @param w The output write to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }
}
