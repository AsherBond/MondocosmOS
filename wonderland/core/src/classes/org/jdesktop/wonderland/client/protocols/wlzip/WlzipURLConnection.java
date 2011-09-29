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
package org.jdesktop.wonderland.client.protocols.wlzip;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * URLConnection for the wlzip protocol
 * 
 * @author paulby
 */
public class WlzipURLConnection extends URLConnection {

    public WlzipURLConnection(URL url) {
        super(url);
    }
    
    @Override
    public void connect() throws IOException {
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return WlzipManager.getWlzipManager().getInputStream(url.getHost(), url.getPath());
    }
    
    /**
     * Trim leading / from str and return str.
     * @param str
     * @return
     */
    private String trimSlash(String str) {
        int trim = 0;
        while(str.charAt(trim)=='/')
            trim++;
        if (trim!=0)
            str = str.substring(trim);
        return str;
    }

}
