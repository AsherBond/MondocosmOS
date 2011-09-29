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
package org.jdesktop.wonderland.runner;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author jkaplan
 */
@XmlRootElement(name="service")
public class RunnerInfo {
    private String baseURL;
    private String name;
    private String status;
    private Properties properties = new Properties();

    /* The JAXB context for later use */
    private static JAXBContext context = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleRepositorys */
    static {
        try {
            context = JAXBContext.newInstance(RunnerInfo.class);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }
    
    public RunnerInfo() {
    }
   
    public RunnerInfo(String baseURL, Runner runner) {
        this.baseURL = baseURL;
        this.name = runner.getName();
        this.status = runner.getStatus().toString();
    }

    @XmlElement
    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElement
    @XmlJavaTypeAdapter(PropertiesAdapter.class)
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
     
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the RunnerInfo class
     * <p>
     * @param r The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to RunnerInfo
     * @throw JAXBException Upon error reading the XML file
     */
    public static RunnerInfo decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (RunnerInfo) unmarshaller.unmarshal(r);
    }
    
    /**
     * Writes the RunnerInfo class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

    /**
     * Writes the RunnerInfo class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }

    static class PropertiesAdapter extends XmlAdapter<Property[], Properties> {
        @Override
        public Properties unmarshal(Property[] v) throws Exception {
            Properties out = new Properties();
            for (Property p : v) {
                out.setProperty(p.key, p.value);
            }
            return out;
        }

        @Override
        public Property[] marshal(Properties v) throws Exception {
            Property[] out = new Property[v.size()];
            int c = 0;
            for (Enumeration e = v.propertyNames(); e.hasMoreElements();) {
                Property prop = new Property();
                prop.key = (String) e.nextElement();
                prop.value = v.getProperty(prop.key);
                out[c++] = prop;
            }
            
            return out;
        }
    }

    static class Property {
        @XmlAttribute
        String key;
        @XmlElement
        String value;
    }
}
