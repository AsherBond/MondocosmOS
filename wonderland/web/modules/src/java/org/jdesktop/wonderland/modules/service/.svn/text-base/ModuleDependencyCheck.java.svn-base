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
package org.jdesktop.wonderland.modules.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.common.modules.ModuleInfo;

/**
 * The ModuleDependencyCheck class checks whether the dependencies for a module
 * has been met. A valid dependency is defined as matching the module name and
 * having a version greater than or equal to the required version. For a module,
 * its dependencies have been checked if all of its required modules are either
 * installed and not about to be removed, or waiting to be installed.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleDependencyCheck {
    /* A list of module requirements, removed as the dependencies are met. */
    private List<ModuleInfo> requirements = new LinkedList<ModuleInfo>();
    
    /** Constructor that takes module to check */
    public ModuleDependencyCheck(Module module) {
        if (module.getRequires()!=null) {
            for (ModuleInfo info : module.getRequires().getRequires()) {
                requirements.add(info);
            }
        }
    }
    
    /**
     * Returns true if all dependencies have been met, false if not.
     * 
     * @return True if all dependencies have been met
     */
    public boolean isDependenciesMet() {
        return this.requirements.isEmpty();
    }
    
    /**
     * Potentially remove a dependency from this collection of dependencies.
     * This checks whether the module info given meets a dependencies and
     * removes it if so. If not, this method does nothing. Returns true if at
     * least one requirement was satisfied, false if not.
     * 
     * @param potentialDependency Information about a module that could satify a requirement
     * @return True if the module satisfied a requirement, false if not
     */
    public boolean checkDependency(ModuleInfo potentialDependency) {
        /*
         * For all requirements, check to see if the dependency is met, if so,
         * remove it
         */
        boolean found = false;
        Iterator<ModuleInfo> it = this.requirements.iterator();
        while (it.hasNext() == true) {
            ModuleInfo requirement = it.next();
            if (this.isSatisfied(potentialDependency, requirement) == true) {
                /*
                 * We remove from the iterator so that it also removes from the
                 * underlying list is an iterator-safe fashion.
                 */
                it.remove();
                found = true;
            }
        }
        return found;
    }

    /**
     * Get the unmet dependencies declared by this module.
     * @return the unmet dependencies for this module
     */
    public List<ModuleInfo> getUnmetDependencies() {
        return new ArrayList<ModuleInfo>(requirements);
    }

    /**
     * Takes two ModuleInfo class and checks whether the first (provider) is
     * satisfied as a requirement of the second (requirer). Returns true if the
     * requirement is satisfied, false if not.
     * 
     * @param provider The module info that could satisfy the requirement
     * @param requirer The module info that specifies the requirement
     * @return True is the requirement is satisfied, false if not
     */
    private boolean isSatisfied(ModuleInfo provider, ModuleInfo requirer) {
        Logger logger = ModuleManager.getLogger();
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Checking whether provider " + provider + 
                        " satisfies the requirements of " + requirer);
        }

        /*
         * First check that both modules have the same name
         */
        if (provider.getName().compareTo(requirer.getName()) != 0) {
            return false;
        }
        
        /*
         * Next check if the requirer needs a version. If major is -1, then
         * there is no requirement and all satisfy it.
         */
        if (requirer.getMajor() == ModuleInfo.VERSION_UNSET) {
            return true;
        }
        
        /*
         * Next check to see if the required major version number is greater
         * than the provided one. If so, then return false.
         */
        if (requirer.getMajor() > provider.getMajor()) {
            return false;
        }
        
        /*
         * Next, check to see if the required major version number is less
         * than the provided one. If so return true.
         */
        if (provider.getMajor() > requirer.getMajor()) {
            return true;
        }
        
        /*
         * At this point we know that there is a required major version number
         * and that the two major version numbers are equal. Now check the minor
         * number. If the required minor number is unset, then any will do.
         */
        if (requirer.getMinor() == ModuleInfo.VERSION_UNSET) {
            return true;
        }

        /*
         * Next check to see if the required minor version number is greater
         * than the provided one. If so, then return false.
         */
        if (requirer.getMinor() > provider.getMinor()) {
            return false;
        }

        /*
         * Next, check to see if the required minor version number is less
         * than the provided one. If so return true.
         */
        if (provider.getMinor() > requirer.getMinor()) {
            return true;
        }

        /*
         * At this point we know that there is a required major and minor
         * version number and that the two major and minor version numbers are
         * equal. Now check the mini number. If the required mini number is
         * unset, then any will do.
         */
        if (requirer.getMini() == ModuleInfo.VERSION_UNSET) {
            return true;
        }

        /*
         * If the required mini number of less than or equal to what is
         * provided, then we are good, otherwise, not.
         */
        if (requirer.getMini() <= provider.getMini()) {
            return true;
        }
        return false;
    }
}
