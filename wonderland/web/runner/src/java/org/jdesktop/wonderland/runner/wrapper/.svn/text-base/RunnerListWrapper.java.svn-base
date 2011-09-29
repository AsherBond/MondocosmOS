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
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.runner.Runner;


/**
 * A list of runners running in a server.
 * 
 * @author jkaplan
 */
@XmlRootElement(name="service-list")
public class RunnerListWrapper implements Serializable {
    
    /* An array of module plugin JAR URIs */
    @XmlElements({
        @XmlElement(name="service")
    })
    private RunnerWrapper[] runners = null;
    
    /* The JAXB context for later use */
    private static JAXBContext context = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleRepositorys */
    static {
        try {
            context = JAXBContext.newInstance(RunnerListWrapper.class);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }
    
    /** Default constructor */
    public RunnerListWrapper() {}
    
    /** Create from a list of runners */
    public RunnerListWrapper(Collection<Runner> runnerColl) {
        runners = new RunnerWrapper[runnerColl.size()];
        int i = 0;
        for (Runner r : runnerColl) {
            runners[i++] = new RunnerWrapper(r);
        }
    }
    
    /* Setters and getters */
    @XmlTransient 
    public RunnerWrapper[] getRunners() { 
        return this.runners; 
    }
     
    public void setRunners(RunnerWrapper[] runners) {
        this.runners = runners;
    }
    
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the RunnerListWrapper class
     * <p>
     * @param r The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to ModuleRepository
     * @throw JAXBException Upon error reading the XML file
     */
    public static RunnerListWrapper decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (RunnerListWrapper) unmarshaller.unmarshal(r);
    }
    
    /**
     * Writes the RunnerListWrapper class to an output writer.
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
     * Writes the RunnerListWrapper class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }
}
