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
package org.jdesktop.wonderland.tools.wfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.tools.wfs.archive.ArchiveWFS;
import org.jdesktop.wonderland.tools.wfs.file.FileWFS;
import org.jdesktop.wonderland.tools.wfs.memory.MemoryWFS;

/**
 * The WFSFactory class creates instances of WFS object trees. Methods in this
 * class are the only way to create these instances. Instances of WFS object
 * trees may be created that represent file systems store on disk, in a JAR
 * file, over the network at some URL, or may represent an empty WFS that is
 * created programmatically.
 * <p>
 * All methods on this class are static.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public final class WFSFactory {

    /**
     * Creates an empty WFS, not associated with any existing file system. As
     * the WFS is built, it is stored in memory and can later be serialized
     * to an output stream. Takes the name of the WFS (without the '-wfs'
     * extension).
     *
     * @param name The name of the WFS
     * @return An instance of a WFS
     */
    public static WFS create(String name) {
        return new MemoryWFS(name);
    }
    
    /**
     * Creates an empty WFS on a disk file system. This method takes the URL of
     * the WFS parent directory, and the name of the WFS (without the '-wfs'
     * extension) to create. If an existing file structure exists there, then
     * it is entirely overwritten. The URL must use the 'file:' protocol.
     * <p>
     * @param url The URL of the WFS to create
     * @throw IOException Upon some general I/O error reading the WFS
     * @throw JAXBException Upon error reading XML
     * @throw InvalidWFSException If the WFS is not properly formatted 
     */
    public static WFS create(URL url)
            throws IOException, InvalidWFSException, JAXBException {
        
        String protocol = url.getProtocol();
        
        /* If the URL points to a disk directory */
        if (protocol.equals(WFS.FILE_PROTOCOL) == true) {
            return new FileWFS(url.getPath(), true);
        }
        else {
            throw new InvalidWFSException("Invalid Protocol for WFS Given: " + url.toString());
        }
    }

    /**
     * Opens an existing WFS given its URL. This factory method supports URLs
     * with the following protocols: 'file:' and 'jar:'. If a URL with a 'file:'
     * protocol is given, this method expects to find the root directory of the
     * WFS on the disk file system. If a URL with a 'jar:' protocol is given,
     * this method interprets the contents as a JAR file that contains a WFS.
     * The jar file may be additionally located on a disk file system or over
     * the network (see the Javadoc for the ArchiveWFS class for more details
     * about the format of the URL in this case).
     *
     * @param url The URL of the WFS to open
     * @throw FileNotFoundException If the WFS does not exist
     * @throw IOException Upon some general I/O error reading the WFS
     * @throw JAXBException Upon error reading XML
     * @throw InvalidWFSException If the WFS is not properly formatted 
     */
    public static final WFS open(URL url)
            throws FileNotFoundException, IOException, JAXBException,
            InvalidWFSException {
        
        String protocol = url.getProtocol();
        
        /* If the URL points to a disk directory */
        if (protocol.equals(WFS.FILE_PROTOCOL) == true) {
            return new FileWFS(url.getPath(), false);
        }
        else if (protocol.equals(WFS.JAR_PROTOCOL) == true) {
            return new ArchiveWFS(url);
        }
        else {
            throw new InvalidWFSException("Invalid Protocol for WFS Given: " + url.toString());
        }
    }
}
