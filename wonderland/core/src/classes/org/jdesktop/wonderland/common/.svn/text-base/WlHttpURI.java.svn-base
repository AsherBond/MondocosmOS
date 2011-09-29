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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A WlHttpURI is an asset uri to handle URL's through the asset manager.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class WlHttpURI extends AssetURI {

    /* The URL with the http protocol for the URI */
    private URL url = null;
    
    /**
     * Constructor which takes the string represents of the URI with the wlhttp
     * protocol.
     * 
     * @param uri The string URI representation
     * @throw URISyntaxException If the URI is not well-formed
     * @throw MalformedURLException If the URL does not translate into a valid URL
     */
    public WlHttpURI(String uri) throws URISyntaxException, MalformedURLException {
        super(uri);
        url = uriToURL(uri);
    }

    /**
     * Constructor which takes the URL with the http protocol
     *
     * @param url The URL
     * @throw URISyntaxException If the URI is not well-formed
     * @throw MalformedURLException If the URL does not translate into a valid URL
     */
    public WlHttpURI(URL url) throws URISyntaxException, MalformedURLException {
        super(urlToURI(url));
        this.url = url;
    }

    /**
     * Returns a relative path of the asset so that it exists in a unique
     * location within a cache. The path does not have a leading "/".
     * 
     * @return A unique relative path for the URI
     */
    public String getRelativeCachePath() {
        return "wlhttp" + File.separator + url.getHost() + File.separator + url.getPath();
    }

    /**
     * @inheritDoc()
     */
    public void setServerHostAndPort(String hostNameAndPort) {
        // Do nothing since the URI already has the info
    }

    /**
     * Returns a URL from the URI.
     *
     * @return A URL
     */
    public URL toURL() throws MalformedURLException {
        return url;
    }

    /**
     * Returns the "base" url, which is the protocol and the host/port name,
     * using a protocol of 'http'.
     */
    public String getBaseURL() {
        if (url.getPort() == -1) {
            return url.getProtocol() + "://" + url.getHost();
        }
        return url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
    }

    /**
     * Returns the relative path of the url.
     */
    public String getRelativePath() {
        return url.getPath();
    }
    
    /**
     * Takes a string URI that has the "wlhttp" protocol and creates a URL
     * with the "http" protocol
     */
    private static URL uriToURL(String uri) throws MalformedURLException {
        if (uri == null || uri.startsWith("wlhttp://") == false) {
            throw new MalformedURLException("Invalid wlhttp URI " + uri);
        }
        String url = uri.substring(2);
        return new URL(url);
    }

    /**
     * Takes a URL that has the "http" protocol and creates a URI with the
     * "wlhttp" protocol
     */
    private static String urlToURI(URL url) throws MalformedURLException {
        if (url == null || url.getProtocol().equals("http") == false) {
            throw new MalformedURLException("Invalid http URL " + url.toExternalForm());
        }
        String uri = "wl" + url.toExternalForm();
        return uri;
    }
}
