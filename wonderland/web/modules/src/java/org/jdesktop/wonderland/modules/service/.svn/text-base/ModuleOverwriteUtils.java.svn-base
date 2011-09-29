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
import java.util.List;
import java.util.Map;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleRequires;

/**
 * The ModuleOverwriteUtils contains utility routines to determine whether a
 * module can be safely overwritten: this happens when a module is installed
 * with the same name as an existing installed modules. This class just deals
 * with the dependencies, and assumes whether or not it can be deployed happens
 * elsewhere.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleOverwriteUtils {

    public static OverwriteQueryResult canOverwrite(ModuleInfo pendingInfo) {
        OverwriteQueryResult res = new OverwriteQueryResult();

        /*
         * Check to see if a module by the same name is already installed. If
         * not, we are good and just return true.
         */
        ModuleManager manager = ModuleManager.getModuleManager();
        Map<String, Module> installed = manager.getInstalledModules();
        if (installed.containsKey(pendingInfo.getName()) == false) {
            res.setResult(true);
            return res;
        }
        
        /*
         * Loop through all of the installed modules and their dependencies. See
         * if a dependency is not met by the new module.
         */
        Iterator<Map.Entry<String, Module>> it = installed.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, Module> entry = it.next();
            ModuleRequires requires = entry.getValue().getRequires();
            
            /*
             * Loop through all of the requirements of the installed module. If
             * the name matches, then check to see if the versions match too.
             * If not, return false.
             */
            for (ModuleInfo requirer : requires.getRequires()) {
                if (requirer.getName().equals(pendingInfo.getName()) == true) {
                    if (ModuleOverwriteUtils.isSatisfied(pendingInfo, requirer) == false) {
                        StringBuffer reason = new StringBuffer();
                        reason.append("Module " + entry.getValue().getName());
                        reason.append(" depends on " + pendingInfo.getName());
                        reason.append(" version " +
                                      requirer.getMajor() + "." +
                                      requirer.getMinor() + "." +
                                      requirer.getMini() + ". ");
                        reason.append("Attempting to install version " +
                                      pendingInfo.getMajor() + "." +
                                      pendingInfo.getMinor() + "." +
                                      pendingInfo.getMini() + ".");
                        res.addReason(reason.toString());
                        res.setResult(false);
                        return res;
                    }
                }
            }
        }

        res.setResult(true);
        return res;
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
    private static boolean isSatisfied(ModuleInfo provider, ModuleInfo requirer) {
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

    /**
     * The result of a deployment query about whether a module can be
     * deployed or undeployed, along with the reason it can or cannot
     * be deployed.
     */
    public static class OverwriteQueryResult {
        private boolean result;
        private final List<String> reasons =
                new ArrayList<String>();

        public boolean getResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public void addReason(String reason) {
            this.reasons.add(reason);
        }

        public List<String> getReasons() {
            return reasons;
        }
    }
}
