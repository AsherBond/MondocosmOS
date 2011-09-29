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
package org.jdesktop.wonderland.web.asset.deployer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.modules.spi.ModuleDeployerSPI;
import org.jdesktop.wonderland.front.admin.ServerInfo;

/**
 * Manages the deployment of "assets", where assets include jar files, artwork,
 * audio files, etc. Assets are not copied from their modules, rather pointers
 * are created to them.
 * <p>
 * This class implements the ModuleDeployerSPI interface and handles module
 * "parts" defined by the getTypes() method (currently, "art", "client",
 * "common", and "audio").
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AssetDeployer implements ModuleDeployerSPI {

    private static Logger logger = Logger.getLogger(AssetDeployer.class.getName());

    /* Holds map of deployed asset info and root Files for assets */
    private static Map<DeployedAsset, File> assetMap = new HashMap();

    /**
     * A DeployedAsset represents the <module name, asset type> pair and
     * uniquely identifies a collection of assets managed by this deployer. It
     * contains equals() and hashCode() so that is can be used as a key in a
     * hashtable.
     */
    public static class DeployedAsset {
        public String moduleName = null;
        public String assetType = null;
        
        /** Constructor, takes both arguments */
        public DeployedAsset(String moduleName, String assetType) {
            this.moduleName = moduleName;
            this.assetType = assetType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DeployedAsset other = (DeployedAsset) obj;
            if (this.moduleName != other.moduleName && (this.moduleName == null || !this.moduleName.equals(other.moduleName))) {
                return false;
            }
            if (this.assetType != other.assetType && (this.assetType == null || !this.assetType.equals(other.assetType))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 43 * hash + (this.moduleName != null ? this.moduleName.hashCode() : 0);
            hash = 43 * hash + (this.assetType != null ? this.assetType.hashCode() : 0);
            return hash;
        }
    }

    /**
     * @inheritDoc()
     */
    public String getName() {
       return "Asset Deployer";
    }

    /**
     * @inheritDoc()
     */
    public String[] getTypes() {
        return new String[] { "art", "client", "common", "audio" };
    }

    /**
     * @inheritDoc()
     */
    public boolean isDeployable(String type, Module module, ModulePart part) {
        /* Assets is always deployable to here */
        return true;
    }

    /**
     * @inheritDoc()
     */
    public boolean isUndeployable(String type, Module module, ModulePart part) {
        /* Assets is always undeployable to here */
        return true;
    }

    /**
     * @inheritDoc()
     */
    public void deploy(String type, Module module, ModulePart part) {
        // Add the file root and checksums to the maps of deployed assets
        DeployedAsset asset = new DeployedAsset(module.getName(), type);
        assetMap.put(asset, part.getFile());

        // if client or common code changes, update the server info
        // object in the server, forcing clients to reload
        if (part.getName().equals("client") || part.getName().equals("common")) {
            ServerInfo.getServerDetails().setTimeStamp(System.currentTimeMillis());
            ServerInfo.getInternalServerDetails().setTimeStamp(System.currentTimeMillis());
        }

        logger.info("Deploying asset for module " + module.getName() +
                " for part " + type);
    }

    /**
     * @inheritDoc()
     */
    public void undeploy(String type, Module module, ModulePart part) {
        DeployedAsset asset = new DeployedAsset(module.getName(), type);
        assetMap.remove(asset);

        // if client or common code changes, update the server info
        // object in the server, forcing clients to reload
        if (part.getName().equals("client") || part.getName().equals("common")) {
            ServerInfo.getServerDetails().setTimeStamp(System.currentTimeMillis());
            ServerInfo.getInternalServerDetails().setTimeStamp(System.currentTimeMillis());
        }
    }
    
    /**
     * Returns (a copy of) a map of module assets to their File roots.
     */
    public static Map<DeployedAsset, File> getFileMap() {
        return new HashMap(assetMap);
    }

    /**
     * Returns a map of module parts and a File to their assets for a given
     * modules. Returns an empty map if the module is not present or has no
     * module parts containing assets. Takes an optional module part name, if
     * not null, returns only the particular module part in the map. If null,
     * returns all module parts in the module.
     *
     * @param moduleName The name of the module
     * @return A Map of DeployedAssets and Files for a given module
     */
    public static Map<DeployedAsset, File> getFileMap(String moduleName, String modulePart) {
        // Loop through the entire map and grab those with a matching module
        // name.
        Map<DeployedAsset, File> resultMap = new HashMap();
        for (DeployedAsset asset : assetMap.keySet()) {
            if (moduleName.equals(asset.moduleName) == true) {
                if (modulePart == null || modulePart.equals(asset.assetType) == true) {
                    resultMap.put(asset, assetMap.get(asset));
                }
            }
        }
        return resultMap;
    }

    /**
     * Returns the file root for the module name and module part, or null if it
     * does not exist.
     * 
     * @param moduleName The module name
     * @param modulePart The name of the module part
     * @return The File directory of the module part
     */
    public static File getFile(String moduleName, String modulePart) {
        return assetMap.get(new DeployedAsset(moduleName, modulePart));
    }
}
