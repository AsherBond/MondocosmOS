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
package org.jdesktop.wonderland.modules.jmecolladaloader.common.cell.state;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
 * Deployment data associated with a JME Collada file
 *
 * @author paulby
 */
@XmlRootElement(name="loader-data")
public class LoaderData {

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(LoaderData.class);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }
    @XmlElement(name="version")
    private short version=1;

    @XmlElement(name="modelLoaderClassname")
    private String modelLoaderClassname;

    @XmlTransient private Map<String, String> deployedTextures; // Mapping between url in loaded file and actual location of file

    /* A table of key-value parameters for the module */
    @XmlElements({
        @XmlElement(name="deployedTextures")
    })
    private Attribute[] attributes = new Attribute[] {};
    
    /**
     * @return the deployedTextures
     */
    @XmlTransient public Map<String, String> getDeployedTextures() {
        return deployedTextures;
    }

    /**
     * @param deployedTextures the deployedTextures to set
     */
    public void setDeployedTextures(Map<String, String> deployedTextures) {
        this.deployedTextures = deployedTextures;
    }

    /**
     * @return the modelLoaderClassname
     */
    @XmlTransient public String getModelLoaderClassname() {
        return modelLoaderClassname;
    }

    /**
     * @param modelLoaderClassname the modelLoaderClassname to set
     */
    public void setModelLoaderClassname(String modelLoaderClassname) {
        this.modelLoaderClassname = modelLoaderClassname;
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

        for(Map.Entry<String, String> entry : deployedTextures.entrySet()) {
            list.add(new Attribute(entry.getKey(), entry.getValue()));
        }

        this.attributes = list.toArray(new Attribute[] {});

        /* Write out to the stream */
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }

    public static LoaderData decode(InputStream in) throws JAXBException {
        /* Read in from stream */
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        LoaderData info = (LoaderData)unmarshaller.unmarshal(in);

        /* Convert array into hash map */
        info.deployedTextures = new HashMap();
        for (Attribute attribute : info.attributes) {
            info.deployedTextures.put(attribute.key, attribute.value);
        }
        return info;
    }


    /**
     * The Attribute inner class stores a string key-value pair
     */
    @XmlAccessorOrder(value=XmlAccessOrder.ALPHABETICAL)
    @XmlRootElement(name="texture")
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

}
