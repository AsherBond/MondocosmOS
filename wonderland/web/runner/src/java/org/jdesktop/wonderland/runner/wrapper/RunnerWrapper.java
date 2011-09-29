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
package org.jdesktop.wonderland.runner.wrapper;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.runner.Runner;

/**
 * A running service.
 * @author jkaplan
 */
@XmlRootElement(name="service")
public class RunnerWrapper {
    private String name;
    private String status;
    private String location;
    private boolean hasLog = false;
    private boolean runnable;

    /* The JAXB context for later use */
    private static JAXBContext context = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleRepositorys */
    static {
        try {
            context = JAXBContext.newInstance(RunnerWrapper.class);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }
    
    public RunnerWrapper() {
    }
   
    public RunnerWrapper(Runner runner) {
        this.name = runner.getName();
        this.status = runner.getStatus().toString();
        this.hasLog = runner.getLogFile().exists();
        this.runnable = runner.isRunnable();
        this.location = runner.getLocation();
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
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    @XmlElement
    public boolean getHasLog() {
        return hasLog;
    }
    
    public void setHasLog(boolean hasLog) {
        this.hasLog = hasLog;
    }

    @XmlElement
    public boolean isRunnable() {
        return runnable;
    }

    public void setRunnable(boolean runnable) {
        this.runnable = runnable;
    }

    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the RunnerInfo class
     * <p>
     * @param r The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to RunnerInfo
     * @throw JAXBException Upon error reading the XML file
     */
    public static RunnerWrapper decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (RunnerWrapper) unmarshaller.unmarshal(r);
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
    
    /**
     * Print name and status
     */
    @Override
    public String toString() {
        return getName() + " " + getStatus();
    }
}
