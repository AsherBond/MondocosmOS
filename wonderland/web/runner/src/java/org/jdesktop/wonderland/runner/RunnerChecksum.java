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

package org.jdesktop.wonderland.runner;

import java.net.URL;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.checksums.Checksum;

/**
 * A checksum that contains full location information
 * @author jkaplan
 */
public class RunnerChecksum extends Checksum {
    @XmlElement
    private URL url;

    public RunnerChecksum() {
        super ();
    }

    public RunnerChecksum(Checksum checksum, String moduleName, URL url) {
        super (checksum.getChecksum());

        // get just the file name
        String path = checksum.getPathName();
        int slashidx = path.lastIndexOf("/");
        if (slashidx != -1) {
            path = path.substring(slashidx + 1);
        }

        setPathName(path);
        setLastModified(checksum.getLastModified());

        this.url = url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @XmlTransient
    public URL getUrl() {
        return url;
    }
}
