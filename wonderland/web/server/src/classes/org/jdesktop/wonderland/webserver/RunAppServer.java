/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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

import com.sun.enterprise.util.net.NetUtils;
import org.jdesktop.wonderland.utils.AppServerMonitor;
import org.jdesktop.wonderland.utils.RunUtil;
import org.jdesktop.wonderland.webserver.launcher.WebServerLauncher;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.embedded.ContainerBuilder;
import org.glassfish.api.embedded.EmbeddedFileSystem;
import org.glassfish.api.embedded.LifecycleException;
import org.glassfish.api.embedded.Server;
import org.jdesktop.wonderland.client.jme.WonderlandURLStreamHandlerFactory;
import org.jdesktop.wonderland.common.NetworkAddress;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModuleAttributes;
import org.jdesktop.wonderland.modules.service.ModuleManager;
import org.jdesktop.wonderland.modules.service.ModuleManager.TaggedModule;
import org.jdesktop.wonderland.utils.Constants;
import org.jdesktop.wonderland.utils.FileListUtil;
import org.jdesktop.wonderland.utils.SystemPropertyUtil;

/**
 *
 * @author jkaplan
 */
public class RunAppServer {
    // logger
    private static final Logger logger =
            Logger.getLogger(RunAppServer.class.getName());
    
    // singleton instance
    private static WonderlandAppServer appServer;
    
    public RunAppServer() throws IOException {
        // set up URL handlers for Wonderland types
        URL.setURLStreamHandlerFactory(new WonderlandURLStreamHandlerFactory()); 

        // now load the properties
        setupProperties();

        // check if we need to make any changes
        if (Boolean.parseBoolean(
                System.getProperty(Constants.WEBSERVER_NEWVERSION_PROP)))
        {
            // replace old versions of modules with newer versions
            updateModules();
            
            // write the web server's document root
            writeDocumentRoot();

            // write DTDs for local deployments
            writeSchemas();

            // write the updated webapps
            writeWebApps();
        }

        // create and start the appserver -- the hostname will be set
        // as a sideeffect of the start call
        try {
            createAppServer();
            getAppServer().start();
        } catch (LifecycleException le) {
            throw new IOException(le);
        }

        // deploy built-in web apps
        deployWebApps();

        // notify the deployer that we are ready to start deploying other apps
        getAppServer().setDeployable(true);

        // redeploy any other modules, including web modules,
        // that haven't yet been deployed.  This will also
        // install all pending modules
        ModuleManager.getModuleManager().redeployAll();

        // start accepting secure connections to the web server (needed
        // by the services that start when we fire the startup complete
        // event below)
        DefaultSAM.setStarted(true);

        // now that all the modules are deployed, notify anyone waiting
        // for startup
        AppServerMonitor.getInstance().fireStartupComplete();
    }

    /**
     * Set up some important properties needed everywhere, like the hostname
     * and webserver URL
     * @param appServerHostname the hostname as detected by the application
     * server.  This will be used, unless we have specified another value.
     */
    protected void setupProperties() {

        String host = System.getProperty(Constants.WEBSERVER_HOST_PROP);
        
        logger.fine("[RunAppServer] host property: " + host);

        if (host != null) {
            // a host was specified -- resolve it
            host = resolveAddress(host);
        }

        logger.fine("[RunAppServer] resolved host: " + host);

        // if the host does not exist or does not resolve, try glassfish's
        // guess
        if (host == null) {
            try {
                host = NetUtils.getCanonicalHostName();
            } catch (UnknownHostException uhe) {
                // ignore
            }
        }

        logger.fine("[RunAppServer] glassfish host: " + host);
        
        // still no luck -- use our best guess
        if (host == null) {
            host = resolveAddress(null);
        }

        String internalHost = System.getProperty(Constants.WEBSERVER_HOST_INTERNAL_PROP);
        if (internalHost == null) {
            internalHost = host;
        }

        logger.fine("[RunAppServer] wonderland host: " + host);
        
        // set the system property
        System.setProperty(Constants.WEBSERVER_HOST_PROP, host);
        System.setProperty(Constants.WEBSERVER_HOST_INTERNAL_PROP, internalHost);

        // set the web server URL based on the hostname and port
        if (System.getProperty(Constants.WEBSERVER_URL_PROP) == null) {
            System.setProperty(Constants.WEBSERVER_URL_PROP,
                "http://" + System.getProperty(Constants.WEBSERVER_HOST_PROP).trim() +
                ":" + System.getProperty(Constants.WEBSERVER_PORT_PROP).trim() + "/");
        }

        if (System.getProperty(Constants.WEBSERVER_URL_INTERNAL_PROP) == null) {
            System.setProperty(Constants.WEBSERVER_URL_INTERNAL_PROP,
                "http://" + System.getProperty(Constants.WEBSERVER_HOST_INTERNAL_PROP).trim() +
                ":" + System.getProperty(Constants.WEBSERVER_PORT_PROP).trim() + "/");
        }

        // make sure we load all libraries in the embedded Glassfish instance
        System.setProperty("org.glassfish.embed.Server.IncludeAllLibs", "true");

        // output the derby log file to a sensible location
        System.setProperty("derby.stream.error.file",
                           new File(RunUtil.getRunDir(), "derby.log").getPath());

        // set the run directory to the subsituted value, so that it can
        // be used in domain.xml
        System.setProperty(Constants.RUN_DIR_PROP,
                           SystemPropertyUtil.getProperty(Constants.RUN_DIR_PROP));
    }

    /**
     * Resolve the host address property into a hostname.  The mechanics of
     * this are mostly encapsulated in NetworkAddress
     * @param prop the property value to base our lookup on (may be null)
     * @return the public address we found
     */
    private static String resolveAddress(String prop) {
        String hostAddress = null;

        try {
            hostAddress = NetworkAddress.getPrivateLocalAddress(prop).getHostAddress();

            if (prop == null || prop.length() == 0) {
                logger.info("Local address " + hostAddress +
                            " was chosen from the list of interfaces");
            } else {
                logger.info("Local address " + hostAddress +
                            " was determined by using " + prop);
            }

            return hostAddress;
        } catch (UnknownHostException e) {
            logger.log(Level.WARNING, "Unable to get Local address using " +
                       prop, e);

            try {
                hostAddress = NetworkAddress.getPrivateLocalAddress().getHostAddress();
                logger.info("chose private local address " + hostAddress +
                            " from the list of interfaces: " + e.getMessage());
            } catch (UnknownHostException ee) {
                logger.log(Level.WARNING, "Unable to determine private " +
                           "local address, using localhost", ee);

                hostAddress = "localhost";
            }
        }

        return hostAddress;
    }


    protected void deployWebApps() throws IOException {
        WonderlandAppServer as = getAppServer();

        // deploy all webapps
        File deployDir = new File(RunUtil.getRunDir(), getWebappDir());
        for (File war : deployDir.listFiles(WAR_FILTER)) {
            logger.info("Deploying " + war.getPath());
            
            try {
                as.deploy(war);
            } catch (Exception excp) {
                // ignore any exception and continue
                logger.log(Level.WARNING, "Error deploying " + war, excp);
            }
        }
    }

    protected void writeDocumentRoot() throws IOException {
        File docDir = new File(RunUtil.getRunDir(), "docRoot");
        docDir.mkdirs();

        // figure out the set of files to add or remove
        List<String> addFiles = new ArrayList<String>();
        List<String> removeFiles = new ArrayList<String>();
        FileListUtil.compareDirs("META-INF/docroot", docDir,
                                 addFiles, removeFiles);

        for (String removeFile : removeFiles) {
            File file = new File(docDir, removeFile);
            file.delete();
        }

        for (String addFile : addFiles) {
            String fullPath = "/docroot/" + addFile;
            InputStream fileIs =
                    WebServerLauncher.class.getResourceAsStream(fullPath);

            RunUtil.writeToFile(fileIs, new File(docDir, addFile));
            fileIs.close();
        }

        // write the updated checksum list
        RunUtil.extract(getClass(), "/META-INF/docroot/files.list", docDir);
    }

    protected void writeWebApps() throws IOException {
        // write to a subdirectory of the default temp directory
        File deployDir = new File(RunUtil.getRunDir(), getWebappDir());
        deployDir.mkdirs();

        // figure out the set of files to add or remove
        List<String> addFiles = new ArrayList<String>();
        List<String> removeFiles = new ArrayList<String>();
        FileListUtil.compareDirs("META-INF/" + getWebappDir(), deployDir,
                                 addFiles, removeFiles);

        // remove the files to remove
        for (String removeFile : removeFiles) {
            File remove = new File(deployDir, removeFile);

            // files have been extracted into directories
            if (remove.isDirectory()) {
                RunUtil.deleteDir(remove);
            }
        }

        for (String addFile : addFiles) {
            String fullPath = "/" + getWebappDir() + "/" + addFile;

            // make sure to clear the directory before we write to it
            File existingDir = new File(deployDir, addFile);
            if (existingDir.exists() && existingDir.isDirectory()) {
                RunUtil.deleteDir(existingDir);
            }

            RunUtil.extractJar(getClass(), fullPath, deployDir);
        }

        // write the updated checksum list
        RunUtil.extract(getClass(), "/META-INF/" + getWebappDir() +
                                    "/files.list", deployDir);
    }

    protected void writeSchemas() throws IOException {
        // issue #1191: for offline instances of Wonderland, make sure
        // we have a copy of all the dtd files locally, otherwise
        // glassfish will fail
        File install_dir = new File(RunUtil.getRunDir(), "web_install");
        File lib_dir = new File(install_dir, "lib");
        File dtds_dir = new File(lib_dir, "dtds");
        File schemas_dir = new File(lib_dir, "schemas");

        // clear the directory & recreate it
        if (dtds_dir.exists()) {
            RunUtil.deleteDir(dtds_dir);
        }
        if (schemas_dir.exists()) {
            RunUtil.deleteDir(schemas_dir);
        }

        // extract the dtds
        ZipInputStream zis = new ZipInputStream(
                getClass().getResourceAsStream("/webserver/schemas/schemas.zip"));
        RunUtil.extractZip(zis, lib_dir);
    }

    /**
     * Get the directory where webapps are stored and deployed
     * @return the name of the webapp directory
     */
    protected String getWebappDir() {
        return "deploy";
    }

    /**
     * Update any system-installed module to be the latest version from
     * the Wonderland.jar file.
     * @throws IOException if there is an error reading or writing modules
     */
    protected void updateModules() throws IOException {
        ModuleManager mm = ModuleManager.getModuleManager();

        // create the directory to extract modules to, if it doesn't already
        // exist
        File moduleDir = RunUtil.createTempDir("module", ".jar");

        // read the list of modules and their checksums from the jar file
        Map<String, String> checksums =
                FileListUtil.readChecksums("META-INF/modules");

        // get the list of all installed module with the "system-installed"
        // key set.  This is set on all modules installed by the system
        Map<String, Module> installed =
                mm.getInstalledModulesByKey(ModuleAttributes.SYSTEM_MODULE);

        // get the checksum of any module that has a checksum.  As a
        // side-effect, any module with a checksum is removed from the
        // list of installed modules, so that list can be used to decide
        // which modules to uninstall
        Map<String, String[]> installedChecksums = getChecksums(installed);

        // add all modules remaining in the installed list to the
        // uninstall list.  These are modules that were installed by an
        // old version of Wonderland and do not have a filename or
        // checksum attribute set.
        Collection<String> uninstall = new ArrayList<String>(installed.keySet());

        // now go through the checksums of old and new modules to determine
        // which modules need to be installed and which are unchanged
        List<TaggedModule> install = new ArrayList<TaggedModule>();
        for (Map.Entry<String, String> checksum : checksums.entrySet()) {

            // compare an existing checksum to an old checksum. If the
            // old checksum doesn't exist or is different than the new
            // checksum, install the new file.  This will overwrite the old
            // checksum in the process.
            String[] installedChecksum = installedChecksums.remove(checksum.getKey());
            if (installedChecksum == null ||
                    !installedChecksum[0].equals(checksum.getValue()))
            {
                install.add(createTaggedModule(checksum.getKey(),
                                               checksum.getValue(),
                                               moduleDir));
            }
        }

        // any modules not removed from the installedChecksums list are
        // old modules that were installed by a previous version of Wonderland
        // (not by the user) and aren't in the new module list.  We need to
        // remove these modules
        for (String[] moduleDesc : installedChecksums.values()) {
            uninstall.add(moduleDesc[1]);
        }

        // uninstall any modules on the uninstall list
        logger.warning("Uninstall: " + uninstall);
        mm.addToUninstall(uninstall);
        
        // install any modules on the install list
        String installList = "";
        for (TaggedModule tm : install) {
            installList += " " + tm.getFile().getName();
        }
        logger.warning("Install: " + installList);
        mm.addTaggedToInstall(install);
    }

    private Map<String, String[]> getChecksums(Map<String, Module> modules) {
        Map<String, String[]> out = new HashMap<String, String[]>();

        for (Iterator<Module> i = modules.values().iterator(); i.hasNext();) {
            ModuleInfo info = i.next().getInfo();
            String filename = info.getAttribute(ModuleAttributes.FILENAME);
            String checksum = info.getAttribute(ModuleAttributes.CHECKSUM);

            if (filename != null) {
                // add both the checksum and the module name
                String[] desc = new String[2];
                desc[0] = checksum;
                desc[1] = info.getName();

                // add to the list of files with checksums
                out.put(filename, desc);

                // remove from the installed list
                i.remove();
            }
        }

        return out;
    }

    private TaggedModule createTaggedModule(String file, String checksum,
                                            File moduleDir)
        throws IOException
    {
        // extract the file
        String fullPath = "/modules/" + file;
        File extracted = RunUtil.extract(getClass(), fullPath, moduleDir);

        // now generate the attributes
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(ModuleAttributes.SYSTEM_MODULE, String.valueOf(true));
        attributes.put(ModuleAttributes.FILENAME, file);
        attributes.put(ModuleAttributes.CHECKSUM, checksum);

        // create the tagged module
        return new TaggedModule(extracted, attributes);
    }

    // get the main instance
    synchronized static WonderlandAppServer getAppServer() {
        return appServer;
    }

    synchronized static void createAppServer()
        throws IOException
    {
        if (appServer != null) {
            throw new IllegalStateException("App server already created.");
        }

        File install_dir = new File(RunUtil.getRunDir(), "web_install");
        File instance_dir = new File(RunUtil.getRunDir(), "web_run");
        File module_dir = new File(install_dir, "modules");
        File docroot_dir = new File(RunUtil.getRunDir(), "docRoot");

        File logDir = new File(SystemPropertyUtil.getProperty("wonderland.log.dir"));
        File logFile = new File(logDir, "web_server.log");

        // set environment variables that will be substituted into
        // domain.xml
        System.setProperty("org.jdesktop.wonderland.docRoot",
                           docroot_dir.toString());

        // make directories
        install_dir.mkdirs();
        instance_dir.mkdirs();
        module_dir.mkdirs();

        // extract the domain.xml file
        File domainXml = RunUtil.extract(RunAppServer.class,
                                         "/domain.xml", install_dir);

        EmbeddedFileSystem.Builder efsBuilder = new EmbeddedFileSystem.Builder();
        efsBuilder.installRoot(install_dir);
        efsBuilder.instanceRoot(instance_dir);
        efsBuilder.configurationFile(domainXml);

        Server.Builder serverBuilder = new Server.Builder("test");
        serverBuilder.embeddedFileSystem(efsBuilder.build());
        //serverBuilder.setLogFile(logFile);

        Server server = serverBuilder.build();

        String portStr = System.getProperty(Constants.WEBSERVER_PORT_PROP).trim();
        int port = Integer.parseInt(portStr);
        server.createPort(port);
        server.addContainer(ContainerBuilder.Type.all);

        // setup and launch
        appServer = new WonderlandAppServer(server);
    }
    
    private static final FilenameFilter WAR_FILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".war");
        }
    };
}
