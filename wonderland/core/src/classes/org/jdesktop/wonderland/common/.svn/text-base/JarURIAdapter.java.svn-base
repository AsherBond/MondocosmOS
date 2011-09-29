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
package org.jdesktop.wonderland.common;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter class to read asset uri's from XML files and insert the proper
 * server name.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class JarURIAdapter extends XmlAdapter<String, JarURI> {
    /* The host name and port number as <host name>:<port> */
    private String hostNameAndPort = null;
    
    /** Default constructor */
    public JarURIAdapter() {
    }
    
    /**
     * Constructor takes the host name and port as: <host name>:<port>
     */
    public JarURIAdapter(String hostNameAndPort) {
        this.hostNameAndPort = hostNameAndPort;
    }
    
    @Override
    public JarURI unmarshal(String uri) throws Exception {
        /*
         * Take the string URI, put is into a JarURI to parse it out, then
         * add in the server
         */
        JarURI jarURI = new JarURI(uri);
        if (uri != null) {
            String moduleName = jarURI.getModuleName();
            String assetPath = jarURI.getAssetPath();
            jarURI = new JarURI(moduleName, hostNameAndPort, assetPath);
        }
        return jarURI;
    }

    @Override
    public String marshal(JarURI jarURI) throws Exception {
        return jarURI.toString();
    }
}
