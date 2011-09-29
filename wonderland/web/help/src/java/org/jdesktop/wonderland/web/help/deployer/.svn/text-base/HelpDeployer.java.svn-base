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
package org.jdesktop.wonderland.web.help.deployer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.modules.spi.ModuleDeployerSPI;

/**
 * The module deployer to support help content.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class HelpDeployer implements ModuleDeployerSPI {

    // Holds map of deployed help module name and root Files for assets
    private static Map<String, File> helpMap = new HashMap();

    /** Default constructor */
    public HelpDeployer() {
    }

    /**
     * Returns (a copy of) a map of module assets to their File roots.
     */
    public static Map<String, File> getFileMap() {
        return new HashMap(helpMap);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Help Deployer";
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTypes() {
        return new String[] { "help" };
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDeployable(String type, Module module, ModulePart part) {
        /* Help is always deployable */
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndeployable(String type, Module module, ModulePart part) {
        /* Help is always undeployable */
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void deploy(String type, Module module, ModulePart part) {
        /* Add the File to the map so we can find the html files */
        helpMap.put(module.getName(), part.getFile());
    }

    /**
     * {@inheritDoc}
     */
    public void undeploy(String type, Module module, ModulePart part) {
        /* Remove from the file map */
        helpMap.remove(module.getName());
    }
}
