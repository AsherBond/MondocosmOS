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
package org.jdesktop.wonderland.web.checksums.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.checksums.Checksum;
import org.jdesktop.wonderland.common.checksums.ChecksumList;

/**
 * A collection of utilities to generate checksums.
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ChecksumUtils {
    /* The error logger */
    private static final Logger logger = Logger.getLogger(ChecksumUtils.class.getName());

    /* The SHA-1 checksum algorithm */
    public final static String SHA1_CHECKSUM_ALGORITHM = "SHA-1";

    /**
     * Creates an returns a new instance of the ChecksumList object given
     * a root directory to search, the name of the checksum algorithm, and an
     * array of regular expression to match against file names to include and
     * to exclude.
     * <p>
     * If the list of regular expressions to include is either null or an empty
     * string, all files will be included. If the list of regular expressions
     * to exclude is either null or an empty string, no files are excluded.
     * 
     * @param root The root directory from which to label assets
     * @param dir The directory in which to generate checksums
     * @param algorithm The checksum algorithm
     * @param includes An array of regular expressions of files to include
     * @throw NoSuchAlgorithmException If the given checksum algorithm is invalid
     * @throw PatternSynaxException If either includes or excludes is invalid
     */
    public static ChecksumList generate(File root, File dir, String algorithm,
            String[] includes, String excludes[]) throws NoSuchAlgorithmException {
        
        /* Try creating the message digest, throws NoSuchAlgorithmException */
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        
        /* Recursively generate checksums, then convert list to an array */
        HashMap<String, Checksum> list = new HashMap<String, Checksum>();
        list.putAll(ChecksumUtils.generateChecksumForDirectory(root, dir, digest, includes, excludes));

        ChecksumList checksumList = new ChecksumList();
        checksumList.setChecksums(list);
        return checksumList;
    }

    /**
     * Generates a checksum for a given file root as the basis for the name to
     * place in the checksum.
     *
     * @param root The root directory form which to label the asset
     * @param file The file to generate a checksum for
     * @param algorithm The checksum algorithm
     * @throw NoSuchAlgorithmException If the given checksum algorithm is invalid
     */
    public static ChecksumList generate(File root, File file, String algorithm) throws NoSuchAlgorithmException {
        // Generate the checksum for the file, but put it in the list and
        // return
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            Checksum checksum = computeChecksum(root, file, digest);
            ChecksumList checksumList = new ChecksumList();
            checksumList.putChecksum(checksum);
            return checksumList;
        } catch (java.io.IOException excp) {
            logger.log(Level.WARNING, "Unable to generate checksum for " +
                    file.getAbsolutePath());
            return null;
        }
    }

    /**
     * Recursively generates checksums for the given directory. Returns a hash
     * map of all checksum objects beneath the given root directory.
     */
    private static HashMap<String, Checksum> generateChecksumForDirectory(File root,
            File dir, MessageDigest digest, String[] includes, String[] excludes) {
 
        /* Put all of the checksums we generate in this linked list */
        HashMap<String, Checksum> list = new HashMap<String, Checksum>();

        /*
         * Loop through all of the files. If it is a directory, then recursively
         * descend into subdirectories. Before computing the checksum make sure
         * the file name satisfies the includes and excludes list.
         */
        File[] files = dir.listFiles();
        if (files == null) {
            /*
             * No files or directory doesn't exist.  Just return an empty
             * map.
             */
            return list;
        }
        for (File file : files) {
            /* If a directory, then recursively descend and append */
            if (file.isDirectory() == true && file.isHidden() == false) {
                HashMap<String, Checksum> rList = ChecksumUtils.generateChecksumForDirectory(
                        root, file, digest, includes, excludes);
                list.putAll(rList);
            }
            else if (file.isFile() == true && file.isHidden() == false) {
                /*
                 * If a normal, non-hidden file, then check whether the name
                 * is included or excluded.
                 */
                if (ChecksumUtils.isAcceptable(file.getName(), includes, excludes) == true) {
                    try {
                        Checksum checksum = computeChecksum(root, file, digest);
                        list.put(checksum.getPathName(), checksum);
                    } catch (java.io.IOException excp) {
                        // Log an error, but continue
                        logger.log(Level.WARNING, "Failed to generate checksum" +
                                " for " + file.getAbsolutePath(), excp);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Generates a checksum for the given file and returns a new Checksum
     * object. Takes the file and root directory
     */
    private static Checksum computeChecksum(File root, File file, MessageDigest digest) throws IOException {
        byte[] buf = new byte[1024 * 1024];
        int bytesRead = 0;
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        InputStream in = new DigestInputStream(bis, digest);

        /* Read in the entire file */
        do {
            bytesRead = in.read(buf);
        } while (bytesRead != -1);
        in.close();

        /* Compute the checksum with the digest */
        byte[] byteChecksum = digest.digest();
        digest.reset();

        /*
         * The relative path name is the absolute path name of the
         * file, stripping off the absolute path name of the root
         */
        String name = file.getAbsolutePath().substring((int) (root.getAbsolutePath().length() + 1));

        /* Make sure the /'s are correct */
        if (File.separatorChar != '/') {
            name = name.replace(File.separatorChar, '/');
        }

        /* Create a new checksum object and add to the list */
        Checksum c = new Checksum();
        c.setLastModified(file.lastModified());
        c.setPathName(name);
        c.setChecksum(Checksum.toHexString(byteChecksum));
        return c;
    }

    /**
     * Given a file name and an array of (possible null) string regular
     * expressions of files patterns to include and exclude, return true if
     * the given file name is acceptable.
     */
    public static boolean isAcceptable(String name, String[] includes, String excludes[]) {
        /* Track whether the file name is acceptable */
        boolean acceptable = true;
        
        /*
         * Check whether the file name is expicitly included. Only do this if
         * includes is either not null and not an empty string. We need to
         * only match one includes to be included.
         */
        if (includes != null && includes.length > 0) {
            acceptable = false;
            for (String include : includes) {
                if (name.matches(include) == true) {
                    acceptable = true;
                    break;
                }
            }
        }
        
        /* If we did match any includes return false */
        if (acceptable == false) {
            return false;
        }
        
        /*
         * Check whether the file name is explicitly excluded. Only do this if
         * excludes is either not null and not an empty string. We need to
         * match only one excludes to be excluded.
         */
        if (excludes != null && excludes.length > 0) {
            for (String exclude: excludes) {
                if (name.matches(exclude) == true) {
                    return false;
                }
            }
        }
        return true;
    }
}
