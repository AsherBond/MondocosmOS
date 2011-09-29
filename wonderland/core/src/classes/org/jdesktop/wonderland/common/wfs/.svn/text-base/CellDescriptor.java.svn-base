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

import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Describes a cell within a wfs, including the root path of the wfs, the path
 * of the parent, the name of the cell, and the XML setup information
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wfs-cell-descriptor")
public class CellDescriptor {
    private WorldRoot worldRoot = null;
    private CellPath cellParent = null;
    private String cellID = null;
    private String cellName = null;
    private String setupInfo = null;

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(CellDescriptor.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(CellDescriptor.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }
    
    /** Default constructor */
    public CellDescriptor() {
    }
    
    /** Constructor, takes all of the class attributes */
    public CellDescriptor(WorldRoot worldRoot, CellPath cellParent, String cellID, String cellName, String setupInfo) {
        this.worldRoot = worldRoot;
        this.cellParent = cellParent;
        this.cellID = cellID;
        this.cellName = cellName;
        this.setupInfo = setupInfo;
    }

    @XmlElement(name="cell-id")
    public String getCellID() {
        return cellID;
    }

    public void setCellID(String cellID) {
        this.cellID = cellID;
    }
    
    @XmlElement(name="cell-name")
    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    /**
     * The unique name is a combination of the cell's name and its
     * CellID.  These unique names are used to store the cell, and in
     * places like the parent path.
     */
    @XmlTransient
    public String getCellUniqueName() {
        return getCellName() + "-" + getCellID();
    }

    @XmlElementRef
    public CellPath getParentPath() {
        return cellParent;
    }

    public void setParentPath(CellPath cellParent) {
        this.cellParent = cellParent;
    }

    @XmlElementRef
    public WorldRoot getRootPath() {
        return worldRoot;
    }

    public void setRootPath(WorldRoot worldRoot) {
        this.worldRoot = worldRoot;
    }

    @XmlElement(name="xml-setup-info")
    public String getSetupInfo() {
        return setupInfo;
    }

    public void setSetupInfo(String setupInfo) {
        this.setupInfo = setupInfo;
    }
    
    /**
     * Takes a reader for the XML stream and returns an instance of this class
     * <p>
     * @param r The reader of the XML stream
     * @throw ClassCastException If the input file does not map to this class
     * @throw JAXBException Upon error reading the XML stream
     */
    public static CellDescriptor decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (CellDescriptor)unmarshaller.unmarshal(r);        
    }
    
    /**
     * Writes the XML representation of this class to a writer.
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
