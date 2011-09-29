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
package org.jdesktop.wonderland.web.wfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.modules.spi.ModuleDeployerSPI;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * Deploys wfs files
 * @author jkaplan
 */
public class WFSDeployer implements ModuleDeployerSPI {
    private static final Logger logger =
            Logger.getLogger(WFSDeployer.class.getName());
    
    private static final FilenameFilter WFS_FILES = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith("-wfs");
        }
    };

    public String getName() {
        return "WFS";
    }

    public String[] getTypes() {
        return new String[] { "wfs" };
    }

    public boolean isDeployable(String type, Module module, ModulePart part) {
        // always deployable
        return true;
    }

    public boolean isUndeployable(String type, Module module, ModulePart part) {
        // always undeployabe
        return true;
    }

    public void deploy(String type, Module module, ModulePart part) {
        File dir = part.getFile();
        for (File wfsDir : dir.listFiles(WFS_FILES)) {
            // create the top level directory
            File targetDir = new File(getDeployDir(), wfsDir.getName());
            targetDir.mkdirs();

            try {
                copyWFSDir(wfsDir, targetDir);
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error deploying module " +
                        module.getName() + " part " + part.getName(), ioe);
            }
        }
    }

    public void undeploy(String type, Module module, ModulePart part) {
        File dir = part.getFile();
        for (File wfsDir : dir.listFiles(WFS_FILES)) {
            // get the top level directory
            File targetDir = new File(getDeployDir(), wfsDir.getName());
            if (targetDir.exists()) {
                deleteWFSDir(wfsDir, targetDir);

                // remove the directory if it is empty
                if (targetDir.listFiles().length == 0) {
                    targetDir.delete();
                }
            }
        }
    }

    private void copyWFSDir(File srcDir, File targetDir) throws IOException {
        for (File srcFile : srcDir.listFiles()) {
            File targetFile = new File(targetDir, srcFile.getName());

            // copy directory
            if (srcFile.isDirectory()) {
                targetFile.mkdir();
                copyWFSDir(srcFile, targetFile);
            } else {
                // copy file
                FileInputStream in = new FileInputStream(srcFile);
                FileOutputStream out = new FileOutputStream(targetFile);
                RunUtil.copyFile(in.getChannel(), out.getChannel());
            }
        }
    }

    private void deleteWFSDir(File srcDir, File targetDir) {
        for (File srcFile : srcDir.listFiles()) {
            File targetFile = new File(targetDir, srcFile.getName());
            if (!targetFile.exists()) {
                continue;
            }

            if (srcFile.isDirectory() && targetFile.isDirectory()) {
                // delete directory contents
                deleteWFSDir(srcFile, targetFile);

                // delete the directory if it is empty
                if (targetFile.listFiles().length == 0) {
                    targetFile.delete();
                }
            } else {
                targetFile.delete();
            }
        }
    }

    private File getDeployDir() {
        return new File(WFSManager.getBaseWFSDirectory(),
                        WFSRoot.WORLDS_DIR);
    }
}
