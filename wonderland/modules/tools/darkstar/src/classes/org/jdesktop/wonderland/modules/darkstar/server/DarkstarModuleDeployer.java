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
package org.jdesktop.wonderland.modules.darkstar.server;

import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.modules.spi.ModuleDeployerSPI;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.Runner;

/**
 * Deploy server and common jars to the Darkstar server. This deployer works
 * in conjunction with the Runner deployer. This deployer checks whether the
 * modules with a server/ part can be (un)deployed. It relies upon the Runner
 * deployer (in web/running) to generate checksums for the jars.
 * 
 * @author jkaplan
 */
public class DarkstarModuleDeployer implements ModuleDeployerSPI {
    /** the types of modules we deploy */
    private static final String[] TYPES = { "server", "common" };

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Darkstar Server";
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTypes() {
        return TYPES; 
    }

    /**
     * {@inheritDoc}
     */
    public void deploy(String type, Module module, ModulePart part) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void undeploy(String type, Module module, ModulePart part) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDeployable(String type, Module module, ModulePart part) {
        // get all darkstar servers, and make sure they are in the 
        // NOT_RUNNING state
        boolean running = false;
        
        for (Runner r : RunManager.getInstance().getAll(DarkstarRunnerImpl.class)) {
            Logger.getLogger(DarkstarModuleDeployer.class.getName()).warning(
                    "RUNNER " + r.getName() + " STATUS " + r.getStatus());
            if (r.getStatus() != Runner.Status.NOT_RUNNING) {
                running = true;
                break;
            }
        }
        
        return !running;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndeployable(String type, Module module, ModulePart part) {
         // get all darkstar servers, and make sure they are in the 
        // NOT_RUNNING state
        boolean running = false;

        Logger.getLogger(DarkstarModuleDeployer.class.getName()).warning(
                "Is undeployable for type " + type);
        for (Runner r : RunManager.getInstance().getAll(DarkstarRunnerImpl.class)) {
            Logger.getLogger(DarkstarModuleDeployer.class.getName()).warning(
                    "RUNNER " + r.getName() + " STATUS " + r.getStatus());
            if (r.getStatus() != Runner.Status.NOT_RUNNING) {
                running = true;
                break;
            }
        }
        
        return !running;
    }
}
