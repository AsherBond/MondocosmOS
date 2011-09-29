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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An AssetURI is the base class of all asset uri's handle by the client-side
 * asset management system.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public abstract class AssetURI implements Serializable {

    /* String that holds the URI */
    private String uri = null;

    /* A map of protocols to the classes that handle them */
    private static Map<String, Class<? extends AssetURI>> uriMap = new HashMap();

    /* Initialize the list of known asset uri classes */
    static {
        uriMap.put("wla", ArtURI.class);
        uriMap.put("wlhttp", WlHttpURI.class);
        uriMap.put("wlj", JarURI.class);
        uriMap.put("wlcontent", ContentURI.class);
    }

    /** Default constructor */
    public AssetURI() {
    }
    
    /** Default constructor */
    public AssetURI(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the String URI
     *
     * @return The URI as a String
     */
    public String getURI() {
        return uri;
    }
    
    /**
     * Returns a relative path of the asset so that it exists in a unique
     * location within a cache. The path does not have a leading "/". This
     * method should return either forward or backward slashes depending
     * upon the platform.
     * 
     * @return A unique relative path for the URI
     */
    public abstract String getRelativeCachePath();

    /**
     * Annotates this URI with a <server name>:<port> based upon the current
     * primary server. This method is implemented by subclasses who need a
     * reference to the current server in order to resolve the asset. For URIs
     * that do not need this information, this method does nothing.
     *
     * @param hostNameAndPort The host name and port to annotate the URI with
     */
    public abstract void setServerHostAndPort(String hostNameAndPort);

    /**
     * Returns the asset uri as a URL
     *
     * @return The URL
     */
    public abstract URL toURL() throws MalformedURLException;
    
    /**
     * Returns the string representation of the URI
     * 
     * @return The string representation of the URI
     */
    @Override
    public String toString() {
        return toExternalForm();
    }

    /**
     * Returns the external representation of the URI.
     *
     * @return The external URI form
     */
    public String toExternalForm() {
        return uri;
    }

    /**
     * Returns the protocol for this uri, null if none is present
     */
    public String getProtocol() {
        // Parse of the protocol string, return null if malformed
        int index = uri.indexOf("://");
        if (index == -1) {
            return null;
        }
        return uri.substring(0, index);
    }

    /**
     * Returns an instance of the proper subclass of AssetURI for the given
     * string URI. Returns null if none exists, based upon the protocol of
     * the given URI.
     *
     * @param uri The String uri
     * @return Some subclass of AssetURI that supports the uri
     */
    public static AssetURI uriFactory(String uri) {
        // Parse of the protocol string, return null if malformed
        int index = uri.indexOf("://");
        if (index == -1) {
            return null;
        }
        String protocol = uri.substring(0, index);

        // Find the class that deals with the protocol type
        Class clazz = uriMap.get(protocol);
        if (clazz == null) {
            return null;
        }

        try {
            // Find the proper constructor that takes a String and return
            // a new instance of the class
            Constructor constructor = clazz.getConstructor(String.class);
            return (AssetURI)constructor.newInstance(uri);
        } catch (Exception ex) {
            Logger.getLogger(AssetURI.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssetURI other = (AssetURI) obj;
        if ((this.uri == null) ? (other.uri != null) : !this.uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.uri != null ? this.uri.hashCode() : 0);
        return hash;
    }
}
