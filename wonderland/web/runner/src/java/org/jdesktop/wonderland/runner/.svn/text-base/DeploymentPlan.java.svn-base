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
package org.jdesktop.wonderland.runner;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Store information for starting runners
 * @author jkaplan
 */
@XmlRootElement(name="DeploymentPlan")
public class DeploymentPlan implements Cloneable {
    private static final Logger logger =
            Logger.getLogger(DeploymentPlan.class.getName());

    private Set<DeploymentEntry> entries =
            new LinkedHashSet<DeploymentEntry>();

    /* The JAXB context for later use */
    private static JAXBContext context = null;
    
    /* Create the XML marshaller and unmarshaller once for all DeploymentEntries */
    static {
        try {
            context = JAXBContext.newInstance(DeploymentPlan.class,
                                              DeploymentEntry.class);
        } catch (javax.xml.bind.JAXBException excp) {
            logger.log(Level.WARNING, "Error creating unmarshaller", excp);
        }
    }
    
    public DeploymentPlan() {
        // default no-arg constructor
    }
    
    public DeploymentPlan(DeploymentEntry[] entries) {
        this (Arrays.asList(entries));
    }
    
    public DeploymentPlan(Collection<DeploymentEntry> entries) {
        this.entries.addAll(entries);
    }

    public void addEntry(DeploymentEntry entry) {
        entries.add(entry);
    }
    
    public void removeEntry(DeploymentEntry entry) {
        entries.remove(entry);
    }
        
    public DeploymentEntry getEntry(String name) {
        for (DeploymentEntry entry : entries) {
            if (entry.getRunnerName().equals(name)) {
                return entry;
            }
        }
        
        // not found
        return null;
    }
    
    /* Setters and getters */
    @XmlElement(name="entry")
    public Set<DeploymentEntry> getEntries() {
        return this.entries;
    }
     
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the DeploymentPlan class
     * <p>
     * @param r The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to DeploymentPlan
     * @throw JAXBException Upon error reading the XML file
     */
    public static DeploymentPlan decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (DeploymentPlan) unmarshaller.unmarshal(r);
    }
    
    /**
     * Writes the DeploymentPlan class to an output writer.
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
     * Writes the DeploymentPlan class to an output stream.
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
     * Create a shallow copy of this plan.  The plan itself is new, but the
     * entry objects are shared.
     * @return the cloned plan
     */
    @Override
    public DeploymentPlan clone() {
        return new DeploymentPlan(entries);
    }

    @Override
    public String toString() {
        String out = super.toString() + " {\n";

        for (DeploymentEntry de : getEntries()) {
            out += de.toString() + "\n";
        }

        out += "}";
        return out;
    }
}
