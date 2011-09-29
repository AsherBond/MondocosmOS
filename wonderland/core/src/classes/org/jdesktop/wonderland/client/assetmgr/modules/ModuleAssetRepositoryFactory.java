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

package org.jdesktop.wonderland.client.assetmgr.modules;

import org.jdesktop.wonderland.client.assetmgr.AssetRepository;
import org.jdesktop.wonderland.client.assetmgr.AssetRepositoryFactory;
import org.jdesktop.wonderland.client.modules.CachedModule;
import org.jdesktop.wonderland.client.modules.ServerCache;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.ModuleURI;
import org.jdesktop.wonderland.common.checksums.Checksum;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.common.modules.ModuleRepository;
import org.jdesktop.wonderland.common.modules.ModuleRepository.Repository;

/**
 * A factory that returns an ordered list of asset repositories for an asset
 * that is contained within a module.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class ModuleAssetRepositoryFactory extends AssetRepositoryFactory {

    public ModuleAssetRepositoryFactory(AssetURI assetURI) {
        super(assetURI);
    }

    /**
     * @inheritDoc()
     */
    @Override
    public String getDesiredChecksum() {
        // Fetch the cache of the module information for the given asset URI.
        // If null, then we log an error and return an empty array.
        CachedModule cachedModule = getCachedModule();
        if (cachedModule == null) {
            logger.warning("Unable to find module information for " +
                    getAssetURI().toExternalForm());
            return null;
        }
        return getDesiredChecksum(cachedModule);
    }

    /**
     * @inheritDoc()
     */
    @Override
    public AssetRepository[] getAssetRepositories() {
        // Fetch the cache of the module information for the given asset URI.
        // If null, then we log an error and return an empty array.
        CachedModule cachedModule = getCachedModule();
        if (cachedModule == null) {
            logger.warning("Unable to find module information for " +
                    getAssetURI().toExternalForm());
            return new ModuleAssetRepository[] {};
        }

        // First fetch the desired checksum of the asset. If there is none,
        // then we will log an error and return an empty array
        String desiredChecksum = getDesiredChecksum(cachedModule);
        if (desiredChecksum == null) {
            logger.warning("Unable to find the desired checksum for " +
                    getAssetURI().toExternalForm());
            return new ModuleAssetRepository[] {};
        }

        // Next find the list of repositories for the module. For each, find
        // out the base URL and create a new ModuleAssetRepository class to
        // represent each.
        Repository repositories[] = getRepositories(cachedModule);
        if (repositories == null) {
            logger.warning("Unable to find repository list for " +
                    getAssetURI().toExternalForm());
            return new ModuleAssetRepository[] {};
        }

        AssetRepository[] assetRepositories = new AssetRepository[repositories.length];
        for (int i = 0; i < repositories.length; i++) {
            String baseURL = repositories[i].url;
            assetRepositories[i] = new ModuleAssetRepository(baseURL, desiredChecksum);
        }
        return assetRepositories;
    }

    /**
     * Returns the CachedModule object for the asset URI, or null if none
     * such exists.
     */
    private CachedModule getCachedModule() {
        // Fetch some information about the asset: the base server URL of where
        // the module definition is located, the name of the module and the
        // asset path
        ModuleURI assetURI = (ModuleURI)getAssetURI();
        String moduleName = assetURI.getModuleName();
        String serverURL = assetURI.getServerURL();

        ServerCache serverCache = ServerCache.getServerCache(serverURL);
        if (serverCache == null) {
            logger.warning("Unable to locate cache of modules for the server " + serverURL);
            return null;
        }

        CachedModule cachedModule = serverCache.getModule(moduleName);
        if (cachedModule == null) {
            logger.warning("Unable to locate module " + moduleName +
                    " on the server " + serverURL);
            return null;
        }
        return cachedModule;
    }

    /**
     * Returns the checksum we desire for this asset. We look up this information
     * based upon the context (host name and port) encoded in the asset URI
     * and its module name
     */
    private String getDesiredChecksum(CachedModule cachedModule) {
        // Fetch some information about the asset: the base server URL of where
        // the module definition is located, the name of the module and the
        // asset path
        ModuleURI assetURI = (ModuleURI)getAssetURI();
        String path = assetURI.getRelativePathInModule();

        ChecksumList moduleChecksums = cachedModule.getModuleChecksums();
        if (moduleChecksums == null) {
            logger.warning("Unable to locate checksum information for " +
                    assetURI.toExternalForm());
            return null;
        }

        Checksum checksum = moduleChecksums.getChecksumMap().get(path);
        if (checksum == null) {
            logger.warning("Unable to locate checksum for path " + path +
                    " for " + assetURI.toExternalForm());
            return null;
        }
        return checksum.getChecksum();
    }

    /**
     * Returns an array of Repository objects that represent the list of
     * repositories associated with this module, or null if none exist
     */
    private Repository[] getRepositories(CachedModule cachedModule) {
        ModuleRepository moduleRepository = cachedModule.getModuleRepositories();
        if (moduleRepository == null) {
            logger.warning("Unable to locate module repositories for " +
                    getAssetURI().toExternalForm());
            return null;
        }
        return moduleRepository.getAllRepositories();
    }
}
