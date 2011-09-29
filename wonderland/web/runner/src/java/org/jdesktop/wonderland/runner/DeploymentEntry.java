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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Store information for starting a single runners
 * @author jkaplan
 */
@XmlRootElement(name="DeploymentEntry")
public class DeploymentEntry {
    private String runnerName;
    private String runnerClass;
    private String location = "localhost";
    private Properties props = new Properties();
    
    public DeploymentEntry() {
        // default no-arg constructor
    }
    
    public DeploymentEntry(String runnerName, String runnerClass) {
        this.runnerName = runnerName;
        this.runnerClass = runnerClass;
    }
    
    
    @XmlTransient
    public Properties getProperties() {
        return props;
    }
    
    public void setProperties(Properties props) {
        this.props = props;
    }
    
    @XmlElement(name="property")
    public Set<Property> getPropertiesInternal() {
        PropertySet out = new PropertySet();
        for (String key : props.stringPropertyNames()) {
            out.addInternal(key, props.getProperty(key));
        }
        return out;
    }

    @XmlElement
    public String getRunnerClass() {
        return runnerClass;
    }

    public void setRunnerClass(String runnerClass) {
        this.runnerClass = runnerClass;
    }

    @XmlElement
    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    @XmlElement
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DeploymentEntry other = (DeploymentEntry) obj;
        if (this.runnerName != other.runnerName && (this.runnerName == null || !this.runnerName.equals(other.runnerName))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.runnerName != null ? this.runnerName.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return getRunnerName() + " " + getRunnerClass() + " " + getProperties().size();
    } 

    // internal representation of elements in the properties set
    protected static class Property {
        @XmlElement public String key;
        @XmlElement public String value;

        private Property() {} //Required by JAXB

        public Property(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * A set that is backed by the internal properties object that
     * this entry stores.
     */
    private class PropertySet extends LinkedHashSet<Property> {
        @Override
        public boolean add(Property p) {
            boolean res = super.add(p);
            if (res) {
                props.setProperty(p.key, p.value);
            }
            return res;
        }

        public void addInternal(String key, String value) {
            super.add(new Property(key, value));
        }
    }
}
