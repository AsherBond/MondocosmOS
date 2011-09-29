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
package org.jdesktop.wonderland.modules.portal.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.login.LoginManager;

/**
 * Cache audio files
 * 
 * @author Joe Provino <jprovino@dev.java.net>
 */
public class AudioCacheHandler {
    private static final Logger logger = Logger.getLogger(AudioCacheHandler.class.getName());

    // The I18N resource bundle
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/portal/client/resources/Bundle");
    
    private File audioCacheDir;

    private static boolean resourcesCached;

    public AudioCacheHandler() {
    }

    public void initialize() throws AudioCacheHandlerException {
	audioCacheDir = new File(ClientContext.getUserDirectory().getAbsolutePath()
	   + File.separator + "cache" + File.separator + "audio");

	logger.warning("Audio cache dir " + audioCacheDir.getAbsolutePath());

	if (audioCacheDir.isDirectory() == false) {
	    if (audioCacheDir.exists()) {
		throw new AudioCacheHandlerException("Invalid audio cache directory " 
		    + audioCacheDir.getAbsolutePath());
	    } else {
		if (audioCacheDir.mkdir() == false) {
		    throw new AudioCacheHandlerException(
			"Unable to create audio cache directory");
		}
	    }
	}
    }

    public String getAudioCacheDir() {
	return audioCacheDir.getAbsolutePath();
    }

    public File uploadFileAudioSource(String audioSource) throws AudioCacheHandlerException {
        // make sure specified file exists, create an
        // entry in the content repository and upload the file.
	String pattern = "file://";

	String s = audioSource;

	int ix = audioSource.indexOf(pattern);

	if (ix >= 0) {
	    s = s.substring(ix + pattern.length());
	}

        File file = new File(s);

        if (file.exists() == false) {
	    throw new AudioCacheHandlerException("Nonexistent file to upload " + s);
	}

	logger.warning("Upload File: " + s);

        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();

        ContentRepository repo = registry.getRepository(LoginManager.getPrimary());
	
        ContentCollection audioCollection = null;

        try {
	    ContentCollection c = repo.getUserRoot();

	    audioCollection = (ContentCollection) c.getChild("audio");

	    if (audioCollection == null) {
		audioCollection = (ContentCollection) c.createChild("audio", Type.COLLECTION);
  	    }
        } catch (ContentRepositoryException e) {
	    throw new AudioCacheHandlerException("Content repository exception: " 
		+ e.getMessage());
        }

        try {
	    /*
	     * Remove file if it exists.
	     */
            ContentResource r = (ContentResource) audioCollection.removeChild(file.getName());
	} catch (Exception e) {
	}

        try {
            ContentResource r = (ContentResource) audioCollection.createChild(
                file.getName(), ContentNode.Type.RESOURCE);

            r.put(file);
        } catch (Exception e) {
	    throw new AudioCacheHandlerException("Failed to upload file:  " + e.getMessage());
	}

	return file;
    }

    public String cacheFile(String audioSource) throws AudioCacheHandlerException {
	File file = new File(audioSource);

	InputStream is = null;

	try {
	    is = new FileInputStream(file);
	} catch (IOException e) {
	    throw new AudioCacheHandlerException("Unable to get input stream for " 
		+ file.getAbsolutePath());
	}

	File cacheFile = createCacheFile(file.getAbsolutePath());

	if (audioSource.equals(cacheFile.getAbsolutePath()) == false) {
            copyFile(is, cacheFile);
	}

        return cacheFile.getAbsolutePath();
    }

    public String cacheContent(String serverURLText, String audioSource) 
	    throws AudioCacheHandlerException {

	if (audioSource.startsWith("wlcontent://")) {
	    URL url;

            String serverURL = serverURLText;

	    try {
		url = new URL(new URL(serverURL), "webdav/content" + audioSource);
	    } catch (MalformedURLException e) {
		throw new AudioCacheHandlerException("Bad URL: " + e.getMessage());
	    }

	    audioSource = url.toString();
	}

        int ix = audioSource.lastIndexOf(File.separator);

        if (ix >= 0) {
            audioSource = audioSource.substring(ix + 1);
        }

	audioSource = audioSource.replaceAll(" ", "%20");

	URL url;

	try {
	    url = new URL(new URL(serverURLText), "webdav/content/users/" 
                + LoginManager.getPrimary().getUsername() + "/audio/" 
		+ audioSource);
	} catch (MalformedURLException e) {
	    throw new AudioCacheHandlerException("Bad URL: " + e.getMessage());
	}

	logger.warning("Cache content: " + url);
	return cacheURL(url);
    }

    public String cacheURL(URL url) throws AudioCacheHandlerException {
	// replace "/" with "_" in url.  Then create the cache file
	// get an input stream to the url and write to the file.
	// return the path to the local file.
	logger.warning("Cache URL: " + url);
	File file = createCacheFile(url.toString());

	copyFile(url, file);
	return file.getAbsolutePath();
    }

    private File createCacheFile(String resource) throws AudioCacheHandlerException {
        logger.warning("resource: " + resource);
	int ix = resource.lastIndexOf('/');//Not File.separator as we're dealing with URLs

	String s = resource;

	if (ix >= 0) {
	    s = s.substring(ix + 1);
	}

	File file = new File(audioCacheDir, s);
        logger.warning("file: " + file);

	if (file.getAbsolutePath().equals(resource)) {
	    return file;
	}

	logger.warning("Create cache file for: " + file.getAbsolutePath());

	file.delete();

	try {
	    file.createNewFile();
	} catch (IOException e) {
	    throw new AudioCacheHandlerException("Unable to create cache file: " 
		+ e.getMessage());
	}

	return file;
    }

    private void copyFile(URL source, File destination) throws AudioCacheHandlerException {
	InputStream is;

	try {
	    is = source.openStream();
	} catch (IOException e) {
	    throw new AudioCacheHandlerException("Unable to open stream: " + e.getMessage());
	}

	copyFile(is, destination);
    }

    private void copyFile(InputStream source, File destination) throws AudioCacheHandlerException {
	FileOutputStream fos;

	try {
	    fos = new FileOutputStream(destination);
	} catch (FileNotFoundException e) {
	    throw new AudioCacheHandlerException("Unable to read file: " + e.getMessage());
	}

	byte[] b = new byte[10000];

	int n;
	int total = 0;

	try {
	    while ((n = source.read(b)) != -1) {
	        fos.write(b, 0, n);
		total += n;
	    }

	    fos.close();
	} catch (IOException e) {
	    throw new AudioCacheHandlerException("Unable to read/write cache file: " 
		+ e.getMessage());
	}
    }

}
