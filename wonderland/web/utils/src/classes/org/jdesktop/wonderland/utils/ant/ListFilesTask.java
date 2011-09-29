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
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

/**
 * Ant task that lists files in a subdirectory of a jar file, and puts them
 * into and output file.
 * @author jkaplan
 */
public class ListFilesTask extends Task {
    private File jar;
    private String dir;
    private File output;

    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    
    @Override
    public void execute() throws BuildException {
        if (!jar.exists()) {
            throw new BuildException("No such jar file: " + jar);
        }
        
        if (dir == null) {
            dir = "/";
        }
        
        if (output == null) {
            output = new File("listfiles.out");
        }
        
        // track if anything has changed
        boolean changed = false;
        BufferedReader check = null;
        File tmpFile = FILE_UTILS.createTempFile("listfiles", ".out", 
                                                 jar.getParentFile());
        // open the jar file and output file
        JarInputStream in = null;
        try {
            in = new JarInputStream(new FileInputStream(jar));
            PrintWriter out = new PrintWriter(new FileWriter(tmpFile));
            
            // open the existing file to see about changes
            if (output.exists()) {
                check = new BufferedReader(new FileReader(output));
            } else {
                changed = true;
            }
            
            Pattern p = Pattern.compile("^" + dir + "/(.+)");
            
            JarEntry je;
            while ((je = in.getNextJarEntry()) != null) {
                Matcher m = p.matcher(je.getName());
                if (m.matches() && m.groupCount() == 1) {
                    String line = "/" + m.group();

                    // see if this is different
                    if (!changed && !line.equals(check.readLine())) {
                        changed = true;
                        check.close();
                    }
                    
                    out.println(line);
                }
            }
            
            out.close();
            
            // overwrite only if the file changed
            if (changed) {
                FILE_UTILS.rename(tmpFile, output);
            } else {
                tmpFile.delete();
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    log("Error closing file " + jar, ioe, Project.MSG_WARN);
                }
            }
        }
        
    }

    public void setJar(File jar) {
        this.jar = jar;
    }
    
    public void setDir(String dir) {
        this.dir = dir;
    }
    
    public void setOutput(File output) {
        this.output = output;
    }

}
