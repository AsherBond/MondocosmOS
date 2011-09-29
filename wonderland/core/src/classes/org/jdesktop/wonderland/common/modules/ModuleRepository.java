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
package org.jdesktop.wonderland.common.modules;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;


/**
 * A module's repository information, as represented by this class, represents
 * the collection of URLs where assets may be found over the Internet.
 * <p>
 * This class stores the name of the master repository where the artwork
 * can be downloaded and also a list of mirror repositories. Both the master
 * and mirror repositories are optional. If no master or mirror is specified,
 * then it is assumed the artwork is made available by the Wonderland server
 * in which the module is installed (if use_server is not false).
 * <p>
 * This class deserializes information distributed from the module service.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="module-repository")
public class ModuleRepository implements Serializable {
    /*
     * The special string that denotes the Wonderland server from which the
     * module was installed should be used.
     */
    public static final String WL_SERVER = "%WLSERVER%";
    
    /* An array of module resources, as relative paths within the module */
    @XmlElements({
        @XmlElement(name="resource")
    })
    private String[] resources = null;
    
    /* The hostname of the master asset server for this repository */
    @XmlElement(name="master") private Repository master = null;
  
    /* An array of hostnames that serve as mirrors for serving the assets */
    @XmlElements({
        @XmlElement(name = "mirror")
    })
    private Repository[] mirrors = null;
    
    /* An ordered linked list of Repository classes for each of the entries */
    private LinkedList<Repository> repositoryList = null;

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ModuleRepository.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(ModuleRepository.class.getName()).log(Level.WARNING,
                    "Unable to create JAXBContext", excp);
        }
    }

    /**
     * The Repository static inner class simply stores the base URL of the
     * repository and whether it is located on the web server itself (if it is,
     * then there is no need to check the checksums). 
     */
    public static class Repository {
        /* The base URL */
        @XmlValue
        public String url = null;
        
        /* Whether it is the server */
        @XmlAttribute(name="isServer")
        public boolean isServer = false;
        
        /** Default constructor */
        public Repository() {}
        
        /** Constructor, takes an existing Repository as an argument */
        public Repository(Repository repository) {
            this.url = repository.url;
            this.isServer = repository.isServer;
        }
    }
    
    /** Default constructor */
    public ModuleRepository() {}
    
    /** Constructor that takes an existing ModuleRepository and makes a copy */
    public ModuleRepository(ModuleRepository repository) {
        /* Set the name of the master repository and add to the list (if not null */
        this.master = (repository.getMaster() != null) ? new Repository(repository.getMaster()) : null;
        
        /* Add the list of mirrors, if not null */
        this.mirrors = (repository.getMirrors() != null) ? new Repository[repository.getMirrors().length] : null;
        if (this.mirrors != null) {
            for (int i = 0; i < this.mirrors.length; i++) {
                this.mirrors[i] = mirrors[i];
            }
        }
        this.resources = (repository.getResources() != null) ? new String[repository.getResources().length] : null;
        if (this.resources != null) {
            for (int i = 0; i < this.resources.length; i++) {
                this.resources[i] = resources[i];
            }
        }
        this.updateRepositoryList();
    }
    
    /* Setters and getters */
    @XmlTransient public String[] getResources() { return this.resources; }
    public void setResources(String[] resources) { this.resources = resources; }
    
    @XmlTransient public Repository getMaster() { return this.master; }
    public void setMaster(Repository master) {
        this.master = master;
        this.updateRepositoryList();
    }
    
    @XmlTransient public Repository[] getMirrors() { return this.mirrors; }
    public void setMirrors(Repository[] mirrors) {
        this.mirrors = mirrors;
        this.updateRepositoryList();
    }

    /**
     * Returns the set of repository as an array of Repository objects
     * 
     * @return An array of Repository objects
     */
    public Repository[] getAllRepositories() {
        return this.repositoryList.toArray(new Repository[] {});
    }
    
    /**
     * Updates the internal linked list of repository objects. This is invoked
     * whenever the set of master and mirror repositories changes.
     */
    private void updateRepositoryList() {
        this.repositoryList = new LinkedList();
        
        /* Add the master if not null */
        if (this.getMaster() != null) {
            repositoryList.addFirst(new Repository(master));
        }
        
        /* Add the mirrors if not null */
        if (this.getMirrors() != null) {
            for (Repository mirror : this.getMirrors()) {
                repositoryList.addLast(new Repository(mirror));
            }
        }
    }
    
    /**
     * Returns the list of repositories encoded as a string
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Module Repository:\n");
        str.append("Master:\n  " + this.getMaster() + "\n");
        str.append("Mirrors:\n");
        if (this.mirrors != null) {
            for (Repository mirror : mirrors) {
                str.append("  " + mirror.url + "\n");
            }
        }
        if (this.resources != null) {
            str.append("Resources:\n");
            for (String resource : resources) {
                str.append("  " + resource + "\n");
            }
        }
        return str.toString();
    }
     
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ModuleRepository class
     * <p>
     * @param r The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to ModuleRepository
     * @throw JAXBException Upon error reading the XML file
     */
    public static ModuleRepository decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ModuleRepository list = (ModuleRepository)unmarshaller.unmarshal(r);
        list.updateRepositoryList();
        return list;
    }
    
    /**
     * Writes the ModuleRepository class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

    /**
     * Writes the ModuleRepository class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }
    
    /**
     * Strips the trailing '/' if it exists on the string.
     */
    private String stripTrailingSlash(String str) {
        if (str.endsWith("/") == true) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
