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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import com.jme.image.Texture;
import com.jme.util.TextureManager;
import imi.repository.CacheBehavior;
import imi.utils.MD5HashUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wonderland implementation of the avatar CacheBehavior interface
 */
public class WonderlandAvatarCache implements CacheBehavior {

    /** Logger ref **/
    private static final Logger logger = Logger.getLogger(WonderlandAvatarCache.class.getName());

    /** The folder we will be searching for and storing cache files in. **/
    private File cacheFolder = null;

    // The base URL for relative URLs
    private String baseURL;

    /**
     * Construct a new cache using the specified folder.
     * @param cacheFolder
     */
    public WonderlandAvatarCache(String baseURL, File cacheFolder) {
        this.baseURL = baseURL;
        if (cacheFolder == null) {
            throw new ExceptionInInitializerError("Cannot have a null cache folder!");
        } else {
            this.cacheFolder = cacheFolder;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean initialize(Object[] params) {
        // Nothing needs to be done currently.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean shutdown() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFileCached(URL location) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Is File Cached? URL=" + location.toExternalForm() +
                    " Protocol " + location.getProtocol());
        }

        File cacheFile = urlToCacheFile(location);
        return cacheFile.exists();
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getStreamToResource(URL location) {
        logger.info("Get Stream to Resource URL=" + location.toExternalForm() +
                " Protocol " + location.getProtocol());

        File cacheFile = urlToCacheFile(location);
        InputStream result = null;
        if (cacheFile != null && cacheFile.exists()) {
            try {
                result = new FileInputStream(cacheFile);
            } catch (FileNotFoundException excp) {
                logger.log(Level.WARNING,
                        "Error reading cache file " + cacheFile.getAbsoluteFile(),
                        excp);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getStreamForWriting(URL location) {
        logger.info("Get Stream for Writing URL=" + location.toExternalForm() +
                " Protocol " + location.getProtocol());

        File cacheFile = urlToCacheFile(location);
        OutputStream result = null;
        if (cacheFile != null) {
            try {
                result = new FileOutputStream(cacheFile);
            } catch (FileNotFoundException excp) {
                logger.log(Level.WARNING,
                        "Error reading cache file " + cacheFile.getAbsoluteFile(),
                        excp);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean clearCache() {
        for (File file : cacheFolder.listFiles()) {
            file.delete();
        }
        if (cacheFolder.listFiles().length == 0) {
            return true; // Success
        } else {
            return false;
        }
    }

    private File urlToCacheFile(URL location) {
        File localFile = null; // If a local version exists.

        // If the URL points to a local file, check the last modified time
        if (location.getProtocol().equalsIgnoreCase("file")) {
            try {
                // For the URL, replace the spaces with %20 so that the toURI() method
                // below does not fail.
                String fixedURI = encodeSpaces(location.toExternalForm());
                localFile = new File(new URI(fixedURI));
            } catch (URISyntaxException excp) {
                logger.log(Level.WARNING,
                        "Unable to form a file object from the URI " +
                        location.toExternalForm(), excp);
            }
        }

        // Use the URL as a String to determine the file name
        String urlString = location.toExternalForm();
        logger.info("URL STRING " + urlString);
        String hashFileName = MD5HashUtils.getStringFromHash(urlString.getBytes());
        File result = new File(cacheFolder, hashFileName);

        if (localFile != null) {
            // Determine which one is newer, if the cache version is older,
            // then we will delete it.
            if (localFile.lastModified() > result.lastModified()) {
                result.delete();
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Texture loadTexture(URL location) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Load Texture URL=" + location.toExternalForm() +
                    " Protocol " + location.getProtocol());
        }

        String evolver = location.getFile();
        boolean isEvolver = evolver.contains("avatars/evolver");
        if (isEvolver && location.getProtocol().equalsIgnoreCase("wlcontent")) {
            evolver = evolver.substring(evolver.indexOf("localRepo"));
            evolver = evolver.substring(evolver.indexOf('/'));
            try {
                location = new URL("wlcontent://users@" + location.getHost() + ":" + location.getPort() + "/" + evolver);
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        // XXX HACK
        // Replace for multi-mesh evolvers if an absolute path. Yuk!
        // XXX HACK
        boolean isEvolverMultimesh = evolver.contains("avatars/multimesh-evolver");
        if (isEvolverMultimesh && location.getProtocol().equalsIgnoreCase("wlcontent")) {
            int index = evolver.indexOf("localRepo");
            if (index != -1) {
                evolver = evolver.substring(evolver.indexOf("localRepo"));
                evolver = evolver.substring(evolver.indexOf('/'));
            }
            try {
                location = new URL("wlcontent://users@" + location.getHost() + ":" + location.getPort() + "/" + evolver);
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        // XXX HACK
        // If the protocl is "file", then check to see if we are taking about
        // a .bhf file. In this case, we lop off the path with some hard-coded
        // name "assets" and append the base URL. For all other URLs and "file"
        // URLs that are not .bhf files, do nothing.
        // XXX HACK
        String urlString = location.toExternalForm();
        if (!isEvolver && !isEvolverMultimesh && location.getProtocol().equalsIgnoreCase("file") == true) {
//            if (urlString.endsWith(".bhf") == true) {
                int assetsIndex = urlString.indexOf("assets/");
                if (assetsIndex != -1) {
                    urlString = urlString.substring(assetsIndex);
                }

                URL localURL = null;
                try {
                    localURL = new URL(baseURL + urlString);
                    return TextureManager.loadTexture(localURL);
                } catch (MalformedURLException excp) {
                    logger.log(Level.WARNING, "Error creating texture url " +
                            baseURL + urlString, excp);
                    return null;
                }
//            }
        }
        try {
            return TextureManager.loadTexture(location);
        } catch (Exception e) {
            logger.warning("Error loading texture "+location.toExternalForm());
            return null;
        }
    }

    public void createCachePackage(OutputStream arg0) {
        // Do nothing
    }

    public void loadCachePackage(InputStream arg0) {
        // Do nothing
    }

    /**
     * Replaces all of the spaces (' ') in a URI string with '%20'
     */
    public static String encodeSpaces(String uri) {
        StringBuilder sb = new StringBuilder(uri);
        int index = 0;
        while ((index = sb.indexOf(" ", index )) != -1) {
            // If we find a space at position 'index', then replace the space
            // and update the value of 'index'. The value of 'index' should be
            // the next character after the replaced '%20', which is index + 3
            sb.replace(index, index + 1, "%20");
            index += 3;
        }
        return sb.toString();
    }

}
