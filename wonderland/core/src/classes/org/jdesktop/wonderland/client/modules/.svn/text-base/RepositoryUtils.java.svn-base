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
package org.jdesktop.wonderland.client.modules;

import org.jdesktop.wonderland.common.ModuleURI;
import org.jdesktop.wonderland.common.modules.ModuleRepository.Repository;

/**
 * Utiility routines to aid dealing with repositories.
 * 
 * @author Jordan Slott <jslott@dev.java.net?
 */
public class RepositoryUtils {

    /**
     * Returns the full URL associated with the Asset to download from this
     * repository.
     * 
     * @param repository The asset repository from which to download
     * @param assetURI The URI of the asset to download
     * @return The full URL of the asset to download
     */
    public static String getAssetURL(Repository repository, ModuleURI assetURI) {
        return RepositoryUtils.stripTrailingSlash(repository.url) + "/" + assetURI.getRelativePathInModule();
    }
    
    /**
     * Returns the full URL assocaited with the Checksums for an asset
     * repository. This method is used for asset repository that are located
     * over the Internet and are not part of the web server which hosts the
     * modules.
     * 
     * @return The full URL to download the checksums
     */
    public static String getChecksumURL(Repository repository) {
        return RepositoryUtils.stripTrailingSlash(repository.url) + "/checksums.xml";
    }
    
    /**
     * Strips the trailing '/' if it exists on the string.
     */
    private static String stripTrailingSlash(String str) {
        if (str.endsWith("/") == true) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
