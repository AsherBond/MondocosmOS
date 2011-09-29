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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.jdesktop.wonderland.common.login.AuthenticationManager;
import org.jdesktop.wonderland.common.login.AuthenticationService;

/**
 * Upload a module to the server
 * @author jkaplan
 */
public class ModuleUploader {
    private URL baseURL;
    private URL uploadURL;
    private String authURL;

    // where to find the upload servlet relative to the base URL
    private static final String UPLOAD_SERVLET = 
            "wonderland-web-modules/ModuleUploadServlet";
    
    /**
     * Create a new module uploader with the given base URL
     * @param baseURL the base URL to upload to
     */
    public ModuleUploader(URL baseURL) throws MalformedURLException {
        this.baseURL = baseURL;
        
        uploadURL = new URL(baseURL, UPLOAD_SERVLET);
    }
    
     /**
     * Get the base URL
     * @return the base URL
     */
    protected URL getBaseURL() {
        return baseURL;
    }
    
    /**
     * Get the URL to upload to
     * @return the upload URL
     */
    protected URL getUploadURL() {
        return uploadURL;
    }

    /**
     * Set the authentication URL, if any
     * @param authURL the authentication URL
     */
    public void setAuthURL(String authURL) {
        this.authURL = authURL;
    }

    /**
     * Get the authentication URL
     * @return the authentication URL, or null if not set
     */
    public String getAuthURL() {
        return authURL;
    }

    /**
     * Upload a module
     * @param module the module to upload
     */
    public void upload(File module) throws IOException {        
        // create the connection to write data to the form
        String boundary = MultiPartFormOutputStream.createBoundary();
        URLConnection uc = MultiPartFormOutputStream.createConnection(getUploadURL());
        if (!(uc instanceof HttpURLConnection)) {
            throw new IllegalStateException("Http URL required: " + getUploadURL());
        }
        HttpURLConnection conn = (HttpURLConnection) uc;
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Content-Type",
                MultiPartFormOutputStream.getContentType(boundary));
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Cache-Control", "no-cache");

        // tell the server not to redirect us to the login page
        conn.setRequestProperty("Redirect", "false");

        // if there is an authentication manager for this server, attempt
        // to secure the request
        if (getAuthURL() != null) {
            AuthenticationService as = AuthenticationManager.get(getAuthURL());
            if (as != null) {
                as.secureURLConnection(conn);
            }
        }
        
        MultiPartFormOutputStream up =
                new MultiPartFormOutputStream(conn.getOutputStream(), boundary);

        // write name
        up.writeFile("moduleJAR", "application/java-archive", module.getName(),
                     new FileInputStream(module));
        up.close();

        // read response
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // print error in console
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer resp = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                resp.append(line + "\n");
            }
            
            throw new IOException("Bad server response: " + responseCode + 
                                  "\n" + resp.toString());
        }
    }
}
