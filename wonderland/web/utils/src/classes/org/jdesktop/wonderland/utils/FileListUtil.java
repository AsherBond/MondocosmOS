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
package org.jdesktop.wonderland.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kaplanj
 */
public class FileListUtil {
    private static final String DEFAULT_ALGORITHM = "SHA-1";

    /**
     * Get the set of changes needed to make the dest directory equivalent
     * to the source directory
     * @param src the name of the source directory, assumed to be in
     * the form of a resource available from the current file
     * @param dest the destination directory
     * @param addedFiles a list that will be populated with the set of
     * files to add
     * @param removedFiles a list that will be populated with the set of
     * files to remove
     * @throws IOException if there is an error reading any of the data
     */
    public static void compareDirs(String src, File dest,
                                   List<String> addedFiles,
                                   List<String> removedFiles)
        throws IOException
    {
        Map<String, String> srcFiles = null;
        Map<String, String> destFiles = null;

        srcFiles = readChecksums(src);

        File destFileList = new File(dest, "files.list");
        if (destFileList.exists()) {
            destFiles = readChecksums(new FileInputStream(destFileList));
        }
        if (destFiles == null) {
            destFiles = new HashMap<String, String>();
        }

        // calculate additions
        for (Map.Entry<String, String> e : srcFiles.entrySet()) {
            String checksum = destFiles.remove(e.getKey());
            if (checksum == null || !checksum.equals(e.getValue())) {
                addedFiles.add(e.getKey());
            }
        }

        // anything left in the destFiles map should be removed
        removedFiles.addAll(destFiles.keySet());
    }

    public static Map<String, String> readChecksums(String src)
        throws IOException
    {
        String listFile = "/" + src + "/files.list";
        InputStream is = FileListUtil.class.getResourceAsStream(listFile);
        if (is != null) {
            return readChecksums(is);
        } else {
            return new HashMap<String, String>();
        }
    }

    public static Map<String, String> readChecksums(File src)
        throws IOException
    {
        if (!src.exists()) {
            return new HashMap<String, String>();
        }

        return readChecksums(new FileInputStream(src));
    }

    public static Map<String, String> readChecksums(InputStream is)
        throws IOException
    {
        Map<String, String> out = new LinkedHashMap();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            String[] vals = line.split(" ");
            out.put(vals[0], vals[1]);
        }

        return out;
    }
    
    public static void writeChecksums(Map<String, String> checksums, 
                                      File file)
        throws IOException
    {
        PrintWriter pr = new PrintWriter(new FileWriter(file));
        for (Map.Entry<String, String> e : checksums.entrySet()) {
            pr.println(e.getKey() + " " + e.getValue());
        }
        
        pr.close();
    }

    public static  String generateChecksum(InputStream is)
            throws IOException
    {
        return generateChecksum(is, DEFAULT_ALGORITHM);
    }

    public static String generateChecksum(InputStream is,
                                          String checksumAlgorithm)
            throws IOException
    {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance(checksumAlgorithm);
        } catch (NoSuchAlgorithmException nsae) {
            IOException ioe = new IOException("No such algorithm " +
                                              checksumAlgorithm);
            ioe.initCause(nsae);
            throw ioe;
        }

        byte[] buf = new byte[1024 * 1024];
        int bytesRead = 0;
        BufferedInputStream bis = new BufferedInputStream(is);
        InputStream in = new DigestInputStream(bis, digest);

        /* Read in the entire file */
        do {
            bytesRead = in.read(buf);
        } while (bytesRead != -1);
        in.close();

        /* Compute the checksum with the digest */
        byte[] byteChecksum = digest.digest();
        digest.reset();

        return toHexString(byteChecksum);
    }

    /**
     * Converts the checksum given as an array of bytes into a hex-encoded
     * string.
     *
     * @param bytes The checksum as an array of bytes
     * @return The checksum as a hex-encoded string
     */
    private static String toHexString(byte bytes[]) {
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            ret.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1));
        }
        return ret.toString();
    }
}
