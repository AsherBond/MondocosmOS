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
package org.jdesktop.wonderland.web.checksums.deployer;

import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory.ChecksumAction;
import org.jdesktop.wonderland.web.checksums.ChecksumManager;
import org.jdesktop.wonderland.web.checksums.modules.ModuleAssetDescriptor;

/**
 * Manages the checksums for assets that are deployed from modules. The checksums
 * for module assets are stored beneath the checksums/ directory in "run".
 * <p>
 * This class implements the ModuleDeployerSPI interface and handles module
 * "parts" defined by the getTypes() method (currently, "art", "client",
 * "common", and "audio").
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ChecksumDeployer extends AssetDeployer {

    /**
     * @inheritDoc()
     */
    @Override
    public String getName() {
       return "Checksum Deployer";
    }

    /**
     * @inheritDoc()
     */
    @Override
    public String[] getTypes() {
        return new String[] { "art", "client", "common", "audio" };
    }

    /**
     * @inheritDoc()
     */
    @Override
    public void deploy(String type, Module module, ModulePart part) {
        // First deploy using the asset deployer
        super.deploy(type, module, part);

        // For each module to deploy, we want to generate a checksums file. We
        // need to create a descriptor for the module and part and ask for the
        // checksums to generate it.
        ChecksumManager checksumManager = ChecksumManager.getChecksumManager();
        ModuleAssetDescriptor descriptor = new ModuleAssetDescriptor(module.getName(), type, null);
        ChecksumFactory factory = checksumManager.getChecksumFactory(descriptor);
        factory.getChecksumList(descriptor, ChecksumAction.FORCE_GENERATE);
    }
}
