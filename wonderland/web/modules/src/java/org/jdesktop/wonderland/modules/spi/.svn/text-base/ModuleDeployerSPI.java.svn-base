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
package org.jdesktop.wonderland.modules.spi;

import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;

/**
 * The ModuleDeployerSPI interface represents a component that can deploy a
 * module part of a certain type. Typically the type is given by the name of
 * the directory within a module.
 * <p>
 * Implementations of this interface tell the caller if the component can be
 * deployed. For example, certain components may not be able to be deployed
 * unless the server is not running.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface ModuleDeployerSPI {
    /**
     * Returns a human-readable string that identifies the deployer by a name.
     * This name does not have to be globally unique, it is simply for reporting
     * purposes.
     * 
     * @return The name of the deployer 
     */
    public String getName();
    
    /**
     * Returns an array of the types of the module part that this deployer
     * handles.
     * 
     * @return An array of module part types this deployer supports.
     */
    public String[] getTypes();
    
    /**
     * Returns true if the given module part can be deployed right now, false
     * if not.
     * 
     * @param type The module part type
     * @param module The module associated with the module part
     * @param part The part of the module to be deployed
     * @return True if the module part can be deployed by the deployer
     */
    public boolean isDeployable(String type, Module module, ModulePart part);
    
    /**
     * Returns true if the given module part can be undeployed right now, false
     * if not.
     * 
     * @param type The module part type
     * @param module The module associated with the module part
     * @param part The part of the module to be undeployed
     * @return True if the module part can be undeployed by the deployer
     */
    public boolean isUndeployable(String type, Module module, ModulePart part);
    
    /**
     * Deploys the module part. Deployers that return true for isDeployable()
     * should not fail in this method.
     * 
     * @param type The module part type
     * @param module The module associated with the module part
     * @param part The module part to deploy
     */
    public void deploy(String type, Module module, ModulePart part);

    /**
     * Undeploys the module part. Deployers that return true for isUndeployable()
     * should not fail in this method.
     * 
     * @param type The module part type
     * @param module The module associated with the module part
     * @param part The module part to undeploy
     */
    public void undeploy(String type, Module module, ModulePart part);
}
