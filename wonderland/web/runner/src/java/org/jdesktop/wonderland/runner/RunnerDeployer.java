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

import org.jdesktop.wonderland.web.checksums.deployer.ChecksumDeployer;

/**
 * Deploys runner .zips and server/ module parts. Note that this deployer is
 * responsible for generarting checksums for the server/ module parts. It works
 * in concert with the DarkstarModuleDeployer (in the darkstar module) that
 * checks whether the module can be (un)deployed based upon whether or not the
 * Darkstar server is running.
 * 
 * @author jkaplan
 */
public class RunnerDeployer extends ChecksumDeployer {
    @Override
    public String getName() {
        return "Runner";
    }

    @Override
    public String[] getTypes() {
        return new String[] { "server", "runner" };
    }
}
