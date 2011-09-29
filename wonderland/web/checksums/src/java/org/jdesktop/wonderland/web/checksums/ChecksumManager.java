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
package org.jdesktop.wonderland.web.checksums;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jdesktop.wonderland.utils.RunUtil;
import org.jdesktop.wonderland.web.checksums.content.ContentAssetDescriptor;
import org.jdesktop.wonderland.web.checksums.content.ContentChecksumFactory;
import org.jdesktop.wonderland.web.checksums.modules.ModuleAssetDescriptor;
import org.jdesktop.wonderland.web.checksums.modules.ModuleChecksumFactory;

/**
 * Manages the checksums for all assets, whether deployed from a module or
 * uploaded to the content repository.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ChecksumManager {
    /* The checksums directory beneath the content directory */
    private static final String CHECKSUM_DIR = "checksums";
    private File checksumDir = null;

    /* A map of checksum factories for each asset type (e.g. "modules", "users") */
    private Map<Class, ChecksumFactory> factoryMap = new HashMap();

    /** Default constructor */
    public ChecksumManager() {
        // Create the checksums directory if it does not yet exist
        checksumDir = new File(RunUtil.getContentDir(), CHECKSUM_DIR);
        checksumDir.mkdirs();

        // Add handlers for each of the different kinds of assets
        factoryMap.put(ModuleAssetDescriptor.class, new ModuleChecksumFactory());
        factoryMap.put(ContentAssetDescriptor.class, new ContentChecksumFactory());
    }

    /**
     * Singleton to hold instance of this class.
     */
    private static class ChecksumManagerHolder {
        private final static ChecksumManager holder = new ChecksumManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ChecksumManager getChecksumManager() {
        return ChecksumManagerHolder.holder;
    }

    /**
     * Returns the base directory in which checksums are kept.
     *
     * @return A File of the base directory for checksums
     */
    public File getChecksumDir() {
        return checksumDir;
    }

    /**
     * Returns the given checksum factory for the particular description of
     * the asset, or null if not present.
     *
     * @param descriptor Describes the asset
     * @return The factory to fetch the checksum
     */
    public ChecksumFactory getChecksumFactory(AssetDescriptor descriptor) {
        return factoryMap.get(descriptor.getClass());
    }
}
