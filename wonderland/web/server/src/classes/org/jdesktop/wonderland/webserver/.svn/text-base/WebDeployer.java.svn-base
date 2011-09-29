/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.webserver;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.embedded.LifecycleException;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.modules.spi.ModuleDeployerSPI;
import org.jdesktop.wonderland.utils.FileListUtil;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * Deploy web modules to the app server
 * @author jkaplan
 */
public class WebDeployer implements ModuleDeployerSPI {
    private static final Logger logger =
            Logger.getLogger(WebDeployer.class.getName());
    
    private static final String DEPLOY_DIR = "webdeploy";
    private static final String CHECKSUM_FILE = "webchecksums.list";

    /** list of deployed wars to avoid duplicate deploys */
    private static final List<DeployRecord> deployed =
            new ArrayList<DeployRecord>();

    /** property for disabling the deployer */
    static final String WEBDEPLOY_PARTNAME_PROP = "web.deployer.part";
    private static final String WEBDEPLOY_PARTNAME_DEFAULT = "web";

    private static String partName = System.getProperty(WEBDEPLOY_PARTNAME_PROP,
                                                        WEBDEPLOY_PARTNAME_DEFAULT);

    /**
     * Get the name of this deployer
     * @return the deployer
     */
    public String getName() {
        return "Web";
    }

    /**
     * This deployer supports the web part of modules
     * @return the types
     */
    public String[] getTypes() {
        return new String[] { partName };
    }

    /**
     * Web modules are always deployable if the server is running
     */
    public boolean isDeployable(String type, Module module, ModulePart part) {
        return RunAppServer.getAppServer().isDeployable();
    }

    /**
     * Web modules are always undeployable
     */
    public boolean isUndeployable(String arg0, Module arg1, ModulePart arg2) {
        return true;
    }
    
    /**
     * Deploy a web app
     * @param type the type of app
     * @param module the module to deploy wars from
     * @param part the web module part
     */
    public void deploy(String type, Module module, ModulePart part) {
        for (File war : getWebApps(part.getFile(), getFileSuffixes())) {
            try {
                deployFile(module.getName(), war);
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Unable to deploy " + war, ioe);
            }
        }
    }

    protected void deployFile(String moduleName, File war)
            throws IOException
    {
        File deployDir = new File(RunUtil.getRunDir(), DEPLOY_DIR);
        deployDir.mkdir();
        
        // generate a checksum for the file
        String checksum = FileListUtil.generateChecksum(new FileInputStream(war));
        
        // create a record for this file
        DeployRecord record = new DeployRecord(moduleName, war.getName(),
                                               checksum);

        // find out if we already have a record for this application
        DeployRecord existing = null;
        synchronized (deployed) {
            int idx = deployed.indexOf(record);
            if (idx != -1) {
                existing = deployed.get(idx);
            }
        }

        // if we do have a record, see if the checksums match
        if (existing != null && existing.checksumMatches(checksum)) {
            // this version is already deployed.
            return;
        } else if (existing != null) {
            // a different version of the same module is already deployed --
            // undeploy it.
            logger.warning("Duplicate deploy " + record + ", undeploying");
            undeploy(existing);
        }

        // read the checksums in the deploy directory
        File checksumFile = getChecksumFile(deployDir);
        Map<String, String> checksums = FileListUtil.readChecksums(checksumFile);

        // see if we already have written this version to disk
        String checksumKey = moduleName + "-" + war.getName();
        File extractDir = new File(deployDir, checksumKey);

        // see if the checksum matches.  If it doesn't match, get rid of what
        // is there and overwrite it with the new directory
        String fileChecksum = checksums.get(checksumKey);
        if (fileChecksum == null || !fileChecksum.equals(checksum)) {
            // make sure the directory doesn't already exist.  If it does, remove it
            if (extractDir.exists()) {
                logger.warning("Directory " + extractDir + " already exists");
                RunUtil.deleteDir(extractDir);
            }

            // extract the war file
            doExtract(war, extractDir);
        }

        // update our record with the directory
        record.setFile(extractDir);

        // do the actual deploy
        doDeploy(record);

        // record that the app is installed
        synchronized (deployed) {
            deployed.add(record);
        }

        // update the checksums, and write them out
        checksums.put(checksumKey, checksum);
        FileListUtil.writeChecksums(checksums, checksumFile);
    }

    protected void doExtract(File war, File extractDir) throws IOException {
        // extract the war into the directory
        JarInputStream jin = new JarInputStream(new FileInputStream(war));
        try {
            RunUtil.extractZip(jin, extractDir);
        } finally {
            jin.close();
        }
    }

    protected void doDeploy(DeployRecord record) throws IOException {
        // create a context root for this app.  The context root is
        // <module-name>/<war-name>, where <war-name> is the name of
        // the .war file with ".war" taken off.
        String contextRoot = record.getModuleName() + "/" +
                             record.getWarName();
        if (contextRoot.endsWith(".war")) {
            contextRoot = contextRoot.substring(0,
                                        contextRoot.length() - ".war".length());
        }

        DeployCommandParameters dcp = new DeployCommandParameters(record.getFile());
        dcp.contextroot = contextRoot;

        // finally, deploy the application to the web server
        try {
            String name = RunAppServer.getAppServer().deploy(record.getFile(),
                                                             dcp);
            record.setAppName(name);
        } catch (LifecycleException ee) {
            throw new IOException(ee);
        }
    }

    /**
     * Undeploy a web app
     * @param type the type of app
     * @param module the module to undeploy wars from
     * @param part the web module part
     */
    public void undeploy(String type, Module module, ModulePart part) {
        for (File war : getWebApps(part.getFile(), getFileSuffixes())) {
            DeployRecord record = new DeployRecord(module.getName(), war.getName());
            undeploy(record);
        }
    }
    
    /**
     * Undeploy a war file from the given module
     * @param record the module to undeploy
     */
    protected void undeploy(DeployRecord record) {
        // remove the record of this deployment
        DeployRecord remove = null;
        synchronized (deployed) {
            int deployIdx = deployed.indexOf(record);
            if (deployIdx != -1) {
                remove = deployed.get(deployIdx);
                deployed.remove(deployIdx);
            }
        }

        // make sure we found a record
        if (remove == null) {
            logger.fine("Not found on undeploy " + record);
            return;
        }

        // do the actual undeploy
        doUndeploy(remove);

        // remove the files associated with this record
        if (remove.getFile() != null) {
            RunUtil.deleteDir(remove.getFile());
        }

        // read the checksums in the deploy directory
        try {
            File deployDir = new File(RunUtil.getRunDir(), DEPLOY_DIR);
            File checksumFile = getChecksumFile(deployDir);
            Map<String, String> checksums = FileListUtil.readChecksums(checksumFile);

            // remove this checksum and write out the update
            String checksumKey = record.getModuleName() + "-" + record.getWarName();
            if (checksums.remove(checksumKey) != null) {
                FileListUtil.writeChecksums(checksums, checksumFile);
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error updating checksums for " + record,
                       ioe);
        }
    }

    protected void doUndeploy(DeployRecord remove) {
        // undeploy the app
        if (remove.getAppName() != null) {
            RunAppServer.getAppServer().getDeployer().undeploy(remove.getAppName(), null);
        }
    }

    /**
     * Get the file suffix
     * @return the file suffix
     */
    protected String[] getFileSuffixes() {
        return new String[] { ".war", ".ear", ".rar" };
    }

    /**
     * Get the list of .wars in a directory
     * @param dir the directory to search
     * @param suffixes the file suffixes to search for
     * @return a list of .war files in the directory, or an empty list
     * if there are no .wars in the directory
     */
    protected File[] getWebApps(File dir, final String[] suffixes) {
        File[] wars = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
            }         

                for (String suffix : suffixes) {
                    if (pathname.getName().endsWith(suffix)) {
                        return true;
                    }
                }

                return false;
            }         
        });
        
        if (wars == null) {
            return new File[0];
        }
        
        return wars;
    }

    protected File getChecksumFile(File deployDir) {
        return new File(deployDir, CHECKSUM_FILE);
    }
    
    protected class DeployRecord {
        private String moduleName;
        private String warName;
        private String checksum;

        // the file or on disk
        private File file;

        // the deployed app
        private String appName;

        public DeployRecord(String moduleName, String warName) {
            this (moduleName, warName, null);
        }

        public DeployRecord(String moduleName, String warName, String checksum)
        {
            this.moduleName = moduleName;
            this.warName = warName;
            this.checksum = checksum;
        }

        public String getModuleName() {
            return moduleName;
        }

        public String getWarName() {
            return warName;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }

        public boolean checksumMatches(String checksum) {
            return this.checksum.equals(checksum);
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }


        // records are equal if they refer to the same module and war.
        // to check the checksum, manually compare the two
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DeployRecord other = (DeployRecord) obj;
            if (this.moduleName != other.moduleName &&
                    (this.moduleName == null ||
                    !this.moduleName.equals(other.moduleName)))
            {
                return false;
            }
            if (this.warName != other.warName &&
                    (this.warName == null ||
                    !this.warName.equals(other.warName)))
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + (this.moduleName != null ? this.moduleName.hashCode() : 0);
            hash = 53 * hash + (this.warName != null ? this.warName.hashCode() : 0);
            return hash;
        }
        
        @Override
        public String toString() {
            return "[ module: " + moduleName + ", war: " + warName +
                   "  app: " + appName + " ]";
        }
    }
}
