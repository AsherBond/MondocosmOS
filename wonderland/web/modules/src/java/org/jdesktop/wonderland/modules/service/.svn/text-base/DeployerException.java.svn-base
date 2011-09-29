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
package org.jdesktop.wonderland.modules.service;

import org.jdesktop.wonderland.modules.Module;

/**
 * The DeploymentFaileException indicates that the deployment has failed
 * somehow.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
class DeployerException extends Exception {

    private String deployerName = null;
    private Module module = null;
    
    /** Constructor, takes the deployer name and module that is responsible */
    public DeployerException(String deployerName, Module module) {
        this.deployerName = deployerName;
        this.module = module;
    }
    
    /**
     * Returns the name of the deployer that failed
     */
    public String getDeployerName() {
        return this.deployerName;
    }
    
    /**
     * Returns the module that has failed deployment
     */
    public Module getModule() {
        return this.module;
    }
}
