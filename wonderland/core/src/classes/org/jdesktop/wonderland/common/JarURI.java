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

import java.net.URISyntaxException;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The JarURI class uniquely identifies a plugin jar resource within a module
 * in the sytem.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
@XmlJavaTypeAdapter(JarURIAdapter.class)
public class JarURI extends ModuleURI {

    /** Default constructor */
    public JarURI() {
    }

    /**
     * Constructor which takes the string represents of the URI.
     * 
     * @param uri The string URI representation
     * @throw URISyntaxException If the URI is not well-formed
     */
    public JarURI(String uri) throws URISyntaxException {
        super(uri);
    }

    /**
     * Constructor which takes the module name, host name and host port, and
     * asset path. This host name and port is given as: <host name>:<port>
     */
    public JarURI(String moduleName, String hostNameAndPort, String assetPath) {
        super("wlj", moduleName, hostNameAndPort, assetPath);
    }
    
    @Override
    public String getRelativePathInModule() {
        return this.getAssetPath();
    }
}
