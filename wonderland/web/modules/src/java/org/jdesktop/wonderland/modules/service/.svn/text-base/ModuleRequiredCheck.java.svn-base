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
package org.jdesktop.wonderland.modules.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jdesktop.wonderland.common.modules.ModuleInfo;

/**
 * The ModuleRequiredCheck helps track whether or not a module is still
 * required by other modules currently installed (or about to be installed).
 * This class is typically used when we are preparing to remove modules from
 * the system.
 * <p>
 * This class is used as follows: for each module being checked whether it is
 * still required, an instance of this class stores the ModuleInfo classes of
 * the module's that require it. If the list of ModuleInfo classes is empty,
 * then it is no longer required.
 * <p>
 * Typically, the initial set of ModuleInfo classes that require the module is
 * set to the list of pending and installed modules, minus those modules to be
 * uninstalled (for sure) during the next restart. Some of these may be removed
 * as we discover modules that can be safely removed.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleRequiredCheck {
    /* The module information for the class we are checking */
    private ModuleInfo moduleInfo = null;
    
    /* A set of module information classes that require this module */
    private Set<ModuleInfo> requires = new HashSet<ModuleInfo>();
    
    
    /** Default constructor */
    public ModuleRequiredCheck(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    /**
     * Returns the module information of the module we are checking.
     * 
     * @return The information for the module we are checking
     */
    public ModuleInfo getCheckedModuleInfo() {
        return this.moduleInfo;
    }
    
    /**
     * Adds a new module information class that requires this module.
     * 
     * @param moduleInfo A new module that requires this module.
     */
    public void addRequiresModuleInfo(ModuleInfo moduleInfo) {
        this.requires.add(moduleInfo);
    }
    
    /**
     * Returns true if this module has no more modules that require it, false
     * if not.
     * 
     * @return True if no other modules require this module, false if not
     */
    public boolean isRequired() {
        return this.requires.isEmpty() == false;
    }
    
    /**
     * Checks whether the module is required by the given module and removes the
     * requirement if so. If not, this method does nothing.
     * 
     * @return moduleInfo Check if the module requires this module
     */
    public void checkRequired(ModuleInfo moduleInfo) {
        this.requires.remove(moduleInfo);
    }

    /**
     * Get the required.
     * @return the modules that depend on this module.
     */
    public Set<ModuleInfo> getRequires() {
        return new LinkedHashSet<ModuleInfo>(requires);
    }
}
