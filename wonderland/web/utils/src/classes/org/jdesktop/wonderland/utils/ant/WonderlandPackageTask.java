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
package org.jdesktop.wonderland.utils.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.util.FileUtils;
import org.jdesktop.wonderland.utils.FileListUtil;

/**
 * Package the main Wonderland.jar
 * @author jkaplan
 */
public class WonderlandPackageTask extends Jar {
    private final List<ZipFileSet> checksums = new ArrayList<ZipFileSet>();
    private File checksumDir;
    private String checksumAlgorithm = "SHA-1";

    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    public void addChecksumFileset(ZipFileSet fileSet) {
        checksums.add(fileSet);
        addZipfileset(fileSet);
    }

    public void setChecksumDir(File checksumDir) {
        this.checksumDir = checksumDir;
    }

    public void setChecksumAlgorithm(String checksumAlgorithm) {
        this.checksumAlgorithm = checksumAlgorithm;
    }

    @Override
    public void execute() throws BuildException {
        try {
            if (checksumDir == null) {
                // initialize a temp directory if we don't have one
                checksumDir = File.createTempFile("wonderlandpkg", "files");
                checksumDir.delete();
                checksumDir.mkdir();
                checksumDir.deleteOnExit();
            } else {
                checksumDir.mkdirs();
            }

            Map<String, Map<String, String>> fileLists =
                    new HashMap<String, Map<String, String>>();

            // go through each fileset and generate checksums if necessary
            for (ZipFileSet files : checksums) {
                String prefix = files.getPrefix(getProject());
                File fileSetDir = new File(checksumDir, prefix);
                fileSetDir.mkdir();

                // keep track of file names
                Map<String, String> fileChecksums = fileLists.get(prefix);
                if (fileChecksums == null) {
                    fileChecksums = new LinkedHashMap<String, String>();
                    fileLists.put(prefix, fileChecksums);
                }

                Iterator<FileResource> i = (Iterator<FileResource>) files.iterator();
                while (i.hasNext()) {
                    FileResource fr = i.next();
                    File f = fr.getFile();

                    String writeName = fr.getName().replace(File.separatorChar, '-');
                    writeName += ".checksum";
                    File checksumFile = new File(fileSetDir, writeName);

                    if (!checksumFile.exists() || outOfDate(checksumFile, f)) {
                        generateChecksum(fr, checksumFile);
                    }

                    String checksum = readChecksum(checksumFile);

                    fileChecksums.put(fr.getName(), checksum);
                }
            }

            // write file lists
            for (Map.Entry<String, Map<String, String>> e : fileLists.entrySet()) {
                writeFileList(e.getKey(), e.getValue());
            }

            // add the checksums directory to the jar
            ZipFileSet zfs = new ZipFileSet();
            zfs.setDir(checksumDir);
            FilenameSelector fs = new FilenameSelector();
            fs.setName("**/files.list");
            zfs.add(fs);
            zfs.setPrefix("META-INF");
            addFileset(zfs);
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }

        super.execute();
    }

    protected boolean outOfDate(File checksums, File orig) {
        return checksums.lastModified() < orig.lastModified();
    }

    protected void generateChecksum(FileResource fr, File checksumFile)
            throws IOException
    {
        log("Generating checksum for " + fr.getName(), Project.MSG_INFO);

        String csStr = FileListUtil.generateChecksum(fr.getInputStream(),
                                                     checksumAlgorithm);
        PrintWriter pr = new PrintWriter(new FileWriter(checksumFile));
        pr.println(csStr);
        pr.close();
    }

    protected String readChecksum(File checksumFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(checksumFile));
        return br.readLine();
    }

    protected void writeFileList(String dirname,
                                 Map<String, String> fileList)
        throws IOException
    {
        File fileSetDir = new File(checksumDir, dirname);

        // compare the new list to the old list
        File fileListFile = new File(fileSetDir, "files.list");
        Map<String, String> existingFileList =
                FileListUtil.readChecksums(fileListFile);
        
        if (!existingFileList.equals(fileList)) {
            // create a file with a list of file names
            FileListUtil.writeChecksums(fileList, fileListFile);
        }
    }
}
