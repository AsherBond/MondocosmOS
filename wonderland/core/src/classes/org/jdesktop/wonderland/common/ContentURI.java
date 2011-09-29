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
 * The ContentURI class uniquely identifies a resource within the content
 * repository.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class ContentURI extends AssetURI {

    /* Protocol: wlcontent */
    private String protocol = null;

    /* Repository root: system or user */
    private String root = null;

    /* Content path beneath root */
    private String assetPath = null;

    /* Host and port from annotation */
    private String hostName = null;
    private int hostPort = -1;

    /**
     * Constructor which takes the string represents of the URI.
     * 
     * @param uri The string URI representation
     * @throw URISyntaxException If the URI is not well-formed
     */
    public ContentURI(String uri) throws URISyntaxException {
        super(uri);
        parseURI(uri);
    }

    /**
     * Constructor, takes the components of the URI: protocol, server name and
     * port, root, and asset path
     */
    public ContentURI(String protocol, String root, String hostName, int hostPort, String assetPath) {
        super(toURI(protocol, root, hostName, hostPort, assetPath));
        this.protocol = protocol;
        this.hostName = hostName;
        this.hostPort = hostPort;
        this.root = root;
        this.assetPath = assetPath;
    }

    /**
     * Construct, takes the components of the URI: protocol, root name, host
     * name and port, and asset path. The host name and port is given as:
     * <host name>:<port>
     */
    public ContentURI(String protocol, String root, String hostNameAndPort, String assetPath) {
        super(toURI(protocol, root, hostNameAndPort, assetPath));
        this.protocol = protocol;
        this.root = root;
        this.assetPath = assetPath;
        parseHostNameAndPort(hostNameAndPort);
    }

    /**
     * Returns the root repository name of this URI.
     *
     * @return The root name
     */
    public String getRoot() {
        return root;
    }

    /**
     * Returns the raw relative path of the asset, without prepending any
     * assumed directory like "art/". It has no leading "/".
     */
    public String getAssetPath() {
        return assetPath;
    }

    /**
     * Returns the protocol of the URI
     */
    @Override
    public String getProtocol() {
        return protocol;
    }

    /**
     * Returns the host port, -1 if none is set.
     *
     * @return The host port
     */
    public int getHostPort() {
        return hostPort;
    }

    /**
     * Returns the host name, null if none is set.
     *
     * @return The host name
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Returns the relative resource path beneath some base repository URL
     */
    public String getRelativePath() {
        return getRoot() + "/" + getAssetPath();
    }
    
    /**
     * @inheritDoc()
     */
    @Override
    public String getRelativeCachePath() {
        return getProtocol() + File.separator + getRoot() + File.separator + getAssetPath();
    }

    /**
     * Must override toString() to not equal toExternalForm(). In this case,
     * toString() will return the URI with the host name and port for display
     * purposes.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(protocol + "://" + root);
        if (hostName != null) {
            sb.append("@" + hostName);
            if (hostPort != -1) {
                sb.append(":" + hostPort);
            }
        }
        sb.append("/" + assetPath);
        return sb.toString();
    }

    /**
     * Must override toExternalForm() to not include the host name and port.
     * This method is used to generate a string representation to use in the
     * asset cache. In the case of federation, even if the host name and
     * port differ, they are considered the same for asset purposes.
     */
    @Override
    public String toExternalForm() {
        return protocol + "://" + root + "/" + assetPath;
    }

    /**
     * @inheritDoc()
     */
    public void setServerHostAndPort(String hostNameAndPort) {
        parseHostNameAndPort(hostNameAndPort);
    }

    /**
     * Returns a URL from the URI.
     *
     * @return A URL
     */
    public URL toURL() throws MalformedURLException {
        return new URL(this.toString());
    }

    /**
     * Returns the URI given the components: protocol, module name, host name,
     * host port, and asset path.
     */
    private static String toURI(String protocol, String root, String hostName, int hostPort, String assetPath) {
        StringBuilder sb = new StringBuilder(protocol + "://" + root);
        if (hostName != null) {
            sb.append("@" + hostName);
            if (hostPort != -1) {
                sb.append(":" + hostPort);
            }
        }
        sb.append("/" + assetPath);
        return sb.toString();
    }

    /**
     * Returns the URI given the components: protocol, root name, host name
     * and port, and asset path.
     */
    private static String toURI(String protocol, String root, String hostNameAndPort, String assetPath) {
        StringBuilder sb = new StringBuilder(protocol + "://" + root);
        if (hostNameAndPort != null) {
            sb.append("@" + hostNameAndPort);
        }
        sb.append("/" + assetPath);
        return sb.toString();
    }

    /**
     * Parse the a server name and port as <host name>:<port> into its parts
     */
    private void parseHostNameAndPort(String hostNameAndPort) {
        /* Sanity check: see if the argument is null */
        if (hostNameAndPort == null) {
            this.hostName = null;
            this.hostPort = -1;
            return;
        }

        /* Check if there is a colon (:), if so, parse both host and port */
        int colonIndex = hostNameAndPort.indexOf(":");
        if (colonIndex != -1) {
            this.hostName = hostNameAndPort.substring(0, colonIndex);
            try {
                this.hostPort = new Integer(hostNameAndPort.substring(colonIndex + 1, hostNameAndPort.length()));
            } catch (NumberFormatException excp) {
                this.hostPort = -1;
            }
        }
        else {
            this.hostName = hostNameAndPort;
            this.hostPort = -1;
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
        final ContentURI other = (ContentURI) obj;
        if ((this.protocol == null) ? (other.protocol != null) : !this.protocol.equals(other.protocol)) {
            return false;
        }
        if ((this.root == null) ? (other.root != null) : !this.root.equals(other.root)) {
            return false;
        }
        if ((this.assetPath == null) ? (other.assetPath != null) : !this.assetPath.equals(other.assetPath)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.protocol != null ? this.protocol.hashCode() : 0);
        hash = 79 * hash + (this.root != null ? this.root.hashCode() : 0);
        hash = 79 * hash + (this.assetPath != null ? this.assetPath.hashCode() : 0);
        return hash;
    }

    /**
     * Parses the string representation of the URI into its component parts.
     */
    private void parseURI(String uri) throws URISyntaxException {
        /* If the uri is null, throw an Exception */
        if (uri == null) {
            throw new URISyntaxException(uri, "URI is NULL");
        }

        /* First check whether the uri contains a <protocol>:// */
        int protocolIndex = uri.indexOf("://");
        if (protocolIndex == -1 || protocolIndex == 0) {
            throw new URISyntaxException(uri, "URI does not contain a protocol");
        }
        protocol = uri.substring(0, protocolIndex);

        /* Advance the index to after the "://" */
        protocolIndex += 3;

        /*
         * Next parse out the root. If we find a "@" first, then there
         * is also a hostname. If we find a "/" next, then there is no host name
         */
        int atIndex = uri.indexOf("@", protocolIndex);
        int slashIndex = uri.indexOf("/", protocolIndex);
        if (atIndex != -1 && atIndex < slashIndex) {
            root = uri.substring(protocolIndex, atIndex);
        }
        else if (slashIndex != -1) {
            root = uri.substring(protocolIndex, slashIndex);
        }
        else {
            throw new URISyntaxException(uri, "Cannot find module name in URI");
        }

        /*
         * Next parse out the host name and port if there is one.
         */
        if (atIndex != -1 && atIndex < slashIndex) {
            int colonIndex = uri.indexOf(":", atIndex + 1);
            if (colonIndex != -1 && colonIndex < slashIndex) {
                hostName = uri.substring(atIndex + 1, colonIndex);
                try {
                    hostPort = new Integer(uri.substring(colonIndex + 1, slashIndex));
                } catch (NumberFormatException excp) {
                    hostPort = -1;
                    throw new URISyntaxException(uri, "Invalid Host port given in URI");
                }
            }
            else {
                hostName = uri.substring(atIndex + 1, slashIndex);
                hostPort = -1;
            }

        }

        /* Finally, take everything past the slash as the asset path */
        assetPath = uri.substring(slashIndex + 1, uri.length());
    }
}
