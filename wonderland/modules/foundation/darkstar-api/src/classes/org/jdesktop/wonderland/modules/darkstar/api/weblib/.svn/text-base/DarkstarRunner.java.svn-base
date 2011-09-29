/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

package org.jdesktop.wonderland.modules.darkstar.api.weblib;

import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.RunnerException;
import org.jdesktop.wonderland.web.wfs.WFSSnapshot;

/**
 * An extension of <code>Runner</code> with information specific to the Darkstar
 * server: hostname and port.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface DarkstarRunner extends Runner {
    /**
     * Get the hostname the Darkstar server is running on
     * @return the darkstar server hostname
     */
    public String getHostname();

    /**
     * Get the internal hostname the Darkstar server is running on
     * @return the darkstar server internal hostname
     */
    public String getInternalHostname();

    /**
     * Get the port the Darkstar server is running on
     * @return the Darkstar server port
     */
    public int getPort();

    /**
     * Create a snapshot of the Darkstar server.  The server must be stopped
     * to perform this operation.
     * @param name the name of the snapshot to create
     * @throws RunnerException if there is a problem creating the snapshot
     */
    public void createSnapshot(String name) throws RunnerException;

    /**
     * Get the name of the wfs snapshot currently in use
     * @return the name of the WFS snapshot in use
     */
    public String getWFSName();

    /**
     * Set the name of the WFS file to load from
     * @param name the name of the WFS file to load from
     */
    public void setWFSName(String wfsName);

    /**
     * Force a server coldstart on the next startup
     */
    public void forceColdstart();
}


