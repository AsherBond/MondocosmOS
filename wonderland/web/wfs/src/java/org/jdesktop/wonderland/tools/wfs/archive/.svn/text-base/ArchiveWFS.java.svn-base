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
package org.jdesktop.wonderland.tools.wfs.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.utils.ArchiveManifest;
import org.jdesktop.wonderland.utils.JarURLUtils;
import org.jdesktop.wonderland.tools.wfs.InvalidWFSException;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSRootDirectory;
import org.jdesktop.wonderland.tools.wfs.WFSVersion;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The ArchiveWFS class extends the WFS abstract class and represents a Wonderland
 * File System that resides as an archive file. The archive file, which may be
 * located over the net, is specified as a URL as follows:
 * <p>
 * jar:<url>!/
 * <p>
 * where <url> is the location of the jar file. For example:
 * <p>
 * jar:http://www.foo.com/bar.jar!/
 * <p>
 * specifies a JAR file named bar.jar located at http://www.foo.com.
 * <p>
 * The implementation for jar files do not yet supported writing.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ArchiveWFS extends WFS {

    /* The object managing the manifest and JAR file contents */
    private ArchiveManifest manifest = null;

    /* The root path within the JAR file containing the WFS */
    private String wfsPath = null;
    
    /**
     * Creates a new instance of WFS given the URL of the archive file.
     * <p>
     * @param uri The URI of the location of the file system
     * @throw SecurityException If a URI protocol type is not supported
     * @throw FileNotFoundException If the URI does not exist
     * @throw InvalidWFSException If the URI does not contain a valid WFS
     * @throw IOException If the URI cannot be opened and/or read
     * @throw JAXException Upon error read the XML from the archive
     */
    public ArchiveWFS(URL url)
            throws SecurityException, IOException, InvalidWFSException,
            JAXBException {
        
        super(WFS.stripWfsSuffix(WFS.stripWfsName(JarURLUtils.getURLMainPart(url.toExternalForm()))));
        
        /*
         * Given the URL, parse into the constituent components: the protocol
         * (which must be JAR) and the WFS URI. Open a connection to the JAR
         * file.
         */
        String urlString = url.toExternalForm();

        /* If the protocol is not JAR, then throw IOException */
        if (urlString.startsWith("jar") != true) {
            throw new IOException("Protocol of URL is not JAR: " + url.toString());
        }
        
        /*
         * Parse out both the "main" part of the URL (up to an including the
         * "!/" and the "path" part of the URL (following and not including
         * the "!/"
         */
        String mainPart = JarURLUtils.getURLMainPart(urlString);
        String pathPart = JarURLUtils.getURLResourcePart(urlString);
        
        /* Open a connection to the JAR file and parse out its entries */
        this.manifest = new ArchiveManifest(new URL(mainPart));
        
        /*
         * Find the base-level wfs directory. If there is more than one, then
         * simply take the first. If there are no file systems within the JAR
         * file, then throw an exception. We look for entries found beneath
         * the "path" in the JAR file.
         */
        LinkedList<String> fsystems = this.manifest.getEntries(pathPart);
        
        /* Look for the first entry with a '-wfs' suffix */
        Iterator<String> it = fsystems.listIterator();
        String wfsdir = null;
        while (it.hasNext() == true) {
            wfsdir = it.next();
            if (wfsdir.endsWith(WFS.WFS_DIRECTORY_SUFFIX) == true)
                break;
        }
        
        /* If we found none, throw an exception */
        if (wfsdir == null) {
            throw new InvalidWFSException("WFS URI has no valid filesystems: " + url.toString());
        }
        
        /* The path to the valid WFS directory inside of the JAR file */
        this.wfsPath = pathPart + "/" + wfsdir;
        
        /* Create the top level directory consisting of the base WFS directory */
        DirectoryDelegate delegate = new ArchiveDirectoryDelegate(this.manifest, this.wfsPath);
        this.directory = new WFSRootDirectory(this, delegate);
        
        /*
         * Read the version.xml file from disk and instantiate a WFSVersion
         * class, if it exists
         */
        String wfsversion = this.wfsPath + "/" + WFSRootDirectory.VERSION;
        InputStream vis = this.manifest.getEntryInputStream(wfsversion);
        if (vis != null) {
            this.directory.setVersion(WFSVersion.decode(new InputStreamReader(vis)));
        }
    }
    
    /**
     * Closes any resources associated with the archive
     */
    @Override
    public void close() {
        this.manifest.close();
    }
        
    /**
     * Writes the entire WFS to the underlying medium, including the meta-
     * information contains within the root directory, the cells containing
     * within the root directory, and any child directories.
     * <p>
     * @throw IOException Upon a general I/O error.
     */
    @Override
    public void write() throws IOException {
        // Writing to archive not currently suppported
        throw new UnsupportedOperationException("Not yet supported.");
    }
}
