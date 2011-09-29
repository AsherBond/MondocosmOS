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
package org.jdesktop.wonderland.web.checksums.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.utils.RunUtil;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer;
import org.jdesktop.wonderland.web.checksums.AssetDescriptor;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory;
import org.jdesktop.wonderland.web.checksums.ChecksumManager;
import org.jdesktop.wonderland.web.checksums.utils.ChecksumUtils;

/**
 * Manages checksums for assets deployed via modules. This factory manages
 * checksums per the module part, so many checksums are kept in a single file.
 * The getChecksumList() method ignores whether a specific asset is asked for;
 * it returns a list of assets for the entire module part.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleChecksumFactory implements ChecksumFactory {

    private static Logger logger = Logger.getLogger(ModuleChecksumFactory.class.getName());
    private static String CHECKSUM_FILE = "checksums.xml";

    /**
     * @inheritDoc()
     */
    public ChecksumList getChecksumList(AssetDescriptor descriptor, ChecksumAction generate) {
        // Cast the descriptor to one that recognizes modules. Since the
        // checksums for modules are kept in a single file per module part,
        // we parse the file from there.
        ModuleAssetDescriptor mad = (ModuleAssetDescriptor)descriptor;
        File modulesDir = getChecksumFile(mad.getModuleName(), mad.getModulePart());

        logger.info("Fetching checksum for module " + mad.getModuleName() +
                " part " + mad.getModulePart() + " in " + modulesDir.getAbsolutePath());

        // Check to see if we always want to generate the checksum
        if (generate == ChecksumAction.FORCE_GENERATE) {
            logger.info("Forcing checksum to be regenerated " +
                    mad.getModuleName() + " part " + mad.getModulePart() +
                    " in " + modulesDir.getAbsolutePath());
            generateChecksum(mad.getModuleName(), mad.getModulePart());
        }

        // Check to see if the checksum file does not exist. If it does not,
        // then check whether we want to generate it.
        if (modulesDir.exists() == false && generate == ChecksumAction.GENERATE) {
            generateChecksum(mad.getModuleName(), mad.getModulePart());
        }
        else if (modulesDir.exists() == false) {
            // Otherwise if the checksums do not exist and we do not wish to
            // generate it, return null now.
            logger.info("Checksum file does not exist and we do not wish " +
                    "to generate it now for module " + mad.getModuleName() +
                    " part " + mad.getModulePart());
            return null;
        }

        // Read in the checksum list and return it. We do not care if a specific
        // asset is given in the asset descriptor.
        FileReader reader = null;
        try {
            reader = new FileReader(modulesDir);
            ChecksumList checksumList = ChecksumList.decode(reader);
            return checksumList;
        } catch (FileNotFoundException excp) {
            logger.log(Level.WARNING, "Unable to find file " +
                    modulesDir.getAbsolutePath(), excp);
            return null;
        } catch (JAXBException excp) {
            logger.log(Level.WARNING, "Unable to parse file " +
                    modulesDir.getAbsolutePath(), excp);
            return null;
        } finally {
            RunUtil.close(reader);
        }
    }

    /**
     * Generates the checksum for a given module name and module part. This
     * method assumes the module has been deployed by the AssetDeployer
     */
    private void generateChecksum(String moduleName, String modulePart) {
        // Look up the File of the module part with the AssetDeployer. If it
        // does not exist, log an error and return.
        File root = AssetDeployer.getFile(moduleName, modulePart);
        if (root == null) {
            logger.warning("Cannot find deployed module " + moduleName +
                    " part " + modulePart);
            return;
        }
        File parent = root.getParentFile();
        File checksumFile = getChecksumFile(moduleName, modulePart);

        // Generate a checksum for the module part and write out. Overwrite any
        // existing checksum file it is exists.
        ChecksumList checksums = new ChecksumList();
        try {
            // Generate the checksums based upon the files present
            String sha = ChecksumUtils.SHA1_CHECKSUM_ALGORITHM;
            checksums = ChecksumUtils.generate(parent, root, sha, null, null);
        } catch (NoSuchAlgorithmException excp) {
            // Log an error, although this exception should never happen
            logger.log(Level.WARNING, "Unable to generate checksums for" +
                    " module " + moduleName + " and part " + modulePart, excp);
            return;
        }

        // Generate all of the necessary directories for the checksum file
        File checksumParent = checksumFile.getParentFile();
        checksumParent.mkdirs();
        
        // Write out the newly generated checksums to the file. If we cannot
        // then log an error. Make sure the writer is closed under any event
        // however.
        FileWriter writer = null;
        try {
            // Write the checksums out to a checksums file
            writer = new FileWriter(checksumFile);
            checksums.encode(writer);
        } catch (java.lang.Exception excp) {
            logger.log(Level.WARNING, "Unable to write checksums.xml to " +
                    checksumFile.getAbsolutePath() + " for module name " +
                    moduleName + " and part " + modulePart, excp);
        } finally {
            RunUtil.close(writer);
        }
    }

    /**
     * Returns the checksum file for the given module name and module part.
     */
    private File getChecksumFile(String moduleName, String modulePart) {
        // Form the File from the module name and module part beneath the
        // "modules" subdirectory.
        ChecksumManager checksumManager = ChecksumManager.getChecksumManager();
        String checksumDir = "modules" + File.separator + moduleName +
                File.separator + modulePart + File.separator +
                CHECKSUM_FILE;
        return new File(checksumManager.getChecksumDir(), checksumDir);
    }
}
