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
package org.jdesktop.wonderland.client.login;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Details on a particular Darkstar server
 * @author jkaplan
 */
@XmlRootElement
public class DarkstarServer implements Cloneable {
    private String hostname;
    private int port;

    /**
     * Default constructor
     */
    public DarkstarServer() {
    }

    /**
     * Create a new DarkstarServer with the given hostname and port
     * @param hostname the hostname
     * @param port the port
     */
    public DarkstarServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Get the hostname to connect to
     * @return the hostname
     */
    @XmlElement
    public String getHostname() {
        return hostname;
    }

    /**
     * Set the hostname
     * @param hostname the hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Get the port to connect to
     * @return the port
     */
    @XmlElement
    public int getPort() {
        return port;
    }

    /**
     * Set the port to connect to
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public DarkstarServer clone() {
        return new DarkstarServer(getHostname(), getPort());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DarkstarServer other = (DarkstarServer) obj;
        if ((this.hostname == null) ?
            (other.hostname != null) : !this.hostname.equals(other.hostname))
        {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash +
                (this.hostname != null ? this.hostname.hashCode() : 0);
        hash = 29 * hash + this.port;
        return hash;
    }
}
