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
package org.jdesktop.wonderland.web.checksums.content;

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
import org.jdesktop.wonderland.web.checksums.AssetDescriptor;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory;
import org.jdesktop.wonderland.web.checksums.ChecksumManager;
import org.jdesktop.wonderland.web.checksums.utils.ChecksumUtils;

/**
 * Manages checksums for assets contained within the content repository. A
 * checksum file is maintained on an individual file basis.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ContentChecksumFactory implements ChecksumFactory {

    private static Logger logger = Logger.getLogger(ContentChecksumFactory.class.getName());

    /**
     * @inheritDoc()
     */
    public ChecksumList getChecksumList(AssetDescriptor descriptor, ChecksumAction generate) {
        // Cast the descriptor to one that recognizes content. Since the
        // checksums for modules are kept in a single file per asset,
        // we parse the file from there.
        ContentAssetDescriptor cad = (ContentAssetDescriptor)descriptor;

        // First check to see whether the content file actually exists. If not,
        // then return null
        File contentFile = getContentFile(cad.getRootName(), cad.getAssetPath());
        if (contentFile.exists() == false || contentFile.canRead() == false) {
            logger.warning("Unable to access content file " + contentFile.getAbsolutePath());
            return null;
        }

        // Check to see if we always want to generate the checksum
        if (generate == ChecksumAction.FORCE_GENERATE) {
            generateChecksum(cad.getRootName(), cad.getAssetPath());
        }

        // Check to see if the checksum file does not exist. If it does not,
        // then check whether we want to generate it.
        File checksumFile = getChecksumFile(cad.getRootName(), cad.getAssetPath());
        if (checksumFile.exists() == false && generate == ChecksumAction.GENERATE) {
            generateChecksum(cad.getRootName(), cad.getAssetPath());
        }
        else if (checksumFile.exists() == false) {
            // Otherwise if the checksums do not exist and we do not wish to
            // generate it, return null now.
            logger.warning("Checksum file does not exist and we do not wish " +
                    "to generate it now for " + cad.getRootName() + " asset " +
                    cad.getAssetPath());
            return null;
        }

        // Parse the checksum directory. If it does not exist, then we generate
        // it only if 'generate' is true.
        // installation. So we ignore the 'generate' flag.
        if (checksumFile.exists() == false && generate == ChecksumAction.GENERATE) {
            logger.warning("Unable to find checkum " + checksumFile);
            return null;
        }

        // Read in the checksum file as a list and return
        FileReader reader = null;
        try {
            reader = new FileReader(checksumFile);
            ChecksumList checksumList = ChecksumList.decode(reader);
            return checksumList;
        } catch (FileNotFoundException excp) {
            logger.log(Level.WARNING, "Unable to find file " +
                    checksumFile.getAbsolutePath(), excp);
            return null;
        } catch (JAXBException excp) {
            logger.log(Level.WARNING, "Unable to parse file " +
                    checksumFile.getAbsolutePath(), excp);
            return null;
        } finally {
            RunUtil.close(reader);
        }
    }

    /**
     * Generates the checksum for a given a content root and asset path.
     */
    private void generateChecksum(String rootName, String assetPath) {
        // Look up the File of the module part with the AssetDeployer. If it
        // does not exist, log an error and return.
        File contentFile = getContentFile(rootName, assetPath);
        File contentRoot = getContentRoot(rootName);

        // Generate a checksum for the content file using its root and file
        // and an SHA1 algorithm
        ChecksumList checksumList = null;
        try {
            // Generate the checksums based upon the files present
            String sha = ChecksumUtils.SHA1_CHECKSUM_ALGORITHM;
            checksumList = ChecksumUtils.generate(contentRoot, contentFile, sha);
        } catch (NoSuchAlgorithmException excp) {
            // Log an error, although this exception should never happen
            logger.log(Level.WARNING, "Unable to generate checksums for " +
                    contentFile.getAbsolutePath());
            return;
        }

        // Generate all of the necessary directories for the checksum file
        File checksumFile = getChecksumFile(rootName, assetPath);
        File checksumParent = checksumFile.getParentFile();
        checksumParent.mkdirs();

        // Write out the newly generated checksums to the file. If we cannot
        // then log an error. Make sure the writer is closed under any event
        // however.
        FileWriter writer = null;
        try {
            // Write the checksums out to a checksums file
            writer = new FileWriter(checksumFile);
            checksumList.encode(writer);
        } catch (java.lang.Exception excp) {
            logger.log(Level.WARNING, "Unable to write checksums.xml to " +
                    checksumFile.getAbsolutePath() + " for content " +
                    contentFile.getAbsolutePath(), excp);
        } finally {
            RunUtil.close(writer);
        }
    }

    /**
     * Returns the File corresonding to the content root
     */
    private File getContentRoot(String rootName) {
        return new File(RunUtil.getContentDir(), rootName);
    }

    /**
     * Returns the File corresponding to the content file
     */
    private File getContentFile(String rootName, String assetPath) {
        String fileName = rootName + File.separator + assetPath;
        return new File(RunUtil.getContentDir(), fileName);
    }

    /**
     * Returns the checksum file for the given content root name and asset path
     */
    private File getChecksumFile(String rootName, String assetPath) {
        // Form the File from the module name and module part beneath the
        // "modules" subdirectory.
        ChecksumManager checksumManager = ChecksumManager.getChecksumManager();
        String checksumFile = "content" + File.separator + rootName +
                File.separator + assetPath + File.separator + "checksum.xml";
        return new File(checksumManager.getChecksumDir(), checksumFile);
    }
}
