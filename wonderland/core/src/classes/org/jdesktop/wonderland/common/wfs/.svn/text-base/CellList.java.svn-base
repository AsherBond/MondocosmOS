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
 * The CellList class simply represent an array of child names for a
 * given cell. It is used to serialize this list across a network in XML form
 * or out to disk. It also contains the date the cell was last modified, so
 * that the cell loading and reloading scheme in Wonderland can check whether
 * a cell has been updated or not.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wfs-children")
public class CellList {
    /* An array of cell children names */
    @XmlElements({
        @XmlElement(name="child")
    })
    private Cell[] children = null;

    /* The relative path of the parent of the children */
    @XmlTransient private String relativePath = null;

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(CellList.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(CellList.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /**
     * The Child inner class simply stores the name of the cell child and the
     * date it was last modified.
     */
    public static class Cell {
        /* The name of the cell */
        @XmlElement(name="name")
        public String name = null;
        
        /* The date the cell was last modified, or -1 if unset */
        @XmlElement(name="last_modified")
        public long lastModified = -1;
        
        /** Default constructor */
        public Cell() {
        }
        
        /** Constructor, takes the name and last modified date */
        public Cell(String name, long lastModified) {
            this.name = name;
            this.lastModified = lastModified;
        }
    }
    
    /** Default constructor */
    public CellList() {
    }
    
    /** Constructor, takes the relative path and names of the children */
    public CellList(String relativePath, Cell[] children) {
        this.relativePath = relativePath;
        this.children = children;
    }
    
    /**
     * Returns the array of cell child names
     * 
     * @return An array of cell child names
     */
    @XmlTransient public Cell[] getChildren() {
        return this.children;
    }
    
    /**
     * Returns the relative path of these children.
     * 
     * @return The relative path
     */
    @XmlTransient public String getRelativePath() {
        return this.relativePath;
    }
    
    /**
     * Takes the input stream of the XML and instantiates an instance of
     * the CellList class
     * <p>
     * @param is The input stream of the XML representation
     * @throw ClassCastException If the input file does not map to CellList
     * @throw JAXBException Upon error reading the XML stream
     */
    public static CellList decode(String relativePath, InputStream is) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        CellList children = (CellList)unmarshaller.unmarshal(is);
        children.relativePath = relativePath;
        return children;
    }
    
    /**
     * Writes the ModuleInfo class to an output stream.
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
