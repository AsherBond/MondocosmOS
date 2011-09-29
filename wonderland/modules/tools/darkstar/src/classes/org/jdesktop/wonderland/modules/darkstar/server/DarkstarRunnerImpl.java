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
package org.jdesktop.wonderland.modules.darkstar.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.modules.service.ModuleManager;
import org.jdesktop.wonderland.runner.BaseRunner;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.RunnerChecksum;
import org.jdesktop.wonderland.runner.RunnerChecksums;
import org.jdesktop.wonderland.runner.RunnerConfigurationException;
import org.jdesktop.wonderland.runner.RunnerException;
import org.jdesktop.wonderland.runner.RunnerInfo;
import org.jdesktop.wonderland.runner.StatusWaiter;
import org.jdesktop.wonderland.utils.Constants;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * An extension of <code>BaseRunner</code> to launch the Darkstar server.
 * @author jkaplan
 */
public class DarkstarRunnerImpl extends BaseRunner implements DarkstarRunner {
    /** the default name if none is specified */
    private static final String DEFAULT_NAME = "Darkstar Server";

    /** the default port to run on */
    static final int DEFAULT_PORT = 1139;

    /** the URL for listing available files */
    private static final String CHECKSUM_URL =
                                  "darkstar/darkstarserver/services/checksums";

    /** property names in the runner info object */
    static final String HOSTNAME_PROP = "hostname";
    static final String HOSTNAME_INTERNAL_PROP = "hostnameInternal";
    static final String PORT_PROP = "port";
    static final String WFSNAME_PROP = "wfsname";

    /** the files to save WFS URLs in */
    private static final String SELECTED_WFS_FILE = "selectedwfs";
    private static final String LAST_WFS_FILE = "lastwfs";
    private static final String COLDSTART_FILE = ".coldstart";

    /** the name of the default world, if the file doesn't exist */
    private static final String DEFAULT_WORLD_PROP = "wonderland.sgs.wfs.default";
    private static final String DEFAULT_WORLD = "worlds/gardenarches-wfs";

    /** the property to check for persistence options */
    private static final String PERSISTENCE_TYPE_PROP = "wonderland.sgs.persistence";
    private PersistenceType PERSISTENCE_TYPE_DEFAULT = PersistenceType.FALLBACK;
    protected enum PersistenceType {
        NONE, FALLBACK, ALWAYS;

        public static PersistenceType parsePersistenceType(String str) {
            for (PersistenceType pt : PersistenceType.values()) {
                if (str.trim().equalsIgnoreCase(pt.name())) {
                    return pt;
                }
            }

            throw new IllegalArgumentException("No such type: " + str);
        }
    }

    /** the property to check for a public hostname */
    private static final String PUBLIC_ADDRESS_PROP = "darkstar.host.public";

    /** the logger */
    private static final Logger logger =
            Logger.getLogger(DarkstarRunnerImpl.class.getName());

    /** the webserver URL to link back to */
    private String webserverURL;

    /** the sgs port.  Only valid when starting up or running */
    private int currentPort;

    /** managers and services found as we deployed files */
    private List<String> managers;
    private List<String> services;

    /** the name of the WFS to load from, or null to load an empty world */
    private String wfsName;

    /** the current wfs name.  Only valid when starting up */
    private String currentWFSName;

    /** the public hostname, or null to use the default hostname */
    private String publicAddress;

    /**
     * The current list of modules, implemented as a thread-local variable
     * that is only valid during a single call to start()
     */
    private static ThreadLocal<RunnerChecksums> moduleChecksums =
        new ThreadLocal<RunnerChecksums>();

    private DarkstarSnapshotRunner snapshot = null;
    /**
     * Configure this runner.  This method sets values to the default for the
     * Darkstar server.
     * 
     * @param props the properties to deploy with
     * @throws RunnerConfigurationException if there is an error configuring
     * the runner
     */
    @Override
    public void configure(Properties props) 
            throws RunnerConfigurationException 
    {
        super.configure(props);
    
        // if the name wasn't configured, do that now
        if (!props.containsKey("runner.name")) {
            setName(DEFAULT_NAME);
        }

        // record the webserver URL
        webserverURL = props.getProperty("wonderland.web.server.url");

        // attempt to restore the WFS URL or use the default name
        try {
            File wfsFile = new File(getBaseDir(), SELECTED_WFS_FILE);
            if (wfsFile.exists()) {
                wfsName = restoreWFSName(SELECTED_WFS_FILE);
            } else {
                wfsName = System.getProperty(DEFAULT_WORLD_PROP, DEFAULT_WORLD);
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error reading WFS file", ioe);
        }
    }
 
    /**
     * Add the Darkstar distribution file
     * @return a list containing the core distribution file as well
     * as the Darkstar distribution file
     */
    @Override
    public Collection<String> getDeployFiles() {
        // add all the files from the superclass
        Collection<String> files = super.getDeployFiles();

        // and the Darkstar server jar
        files.add("wonderland-server-dist.zip");

        // now add each module
        try {
            for (String module : getModuleChecksums().getChecksums().keySet()) {
                files.add(module);
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error reading module checksums", ioe);
        }

        return files;
    }

    /** Default Darkstar properties */
    @Override
    public Properties getDefaultProperties() {
        Properties props = new Properties();
        props.setProperty("sgs.port", String.valueOf(DEFAULT_PORT));
        props.setProperty("wonderland.web.server.url", webserverURL);
        return props;
    }

    @Override
    public synchronized void start(Properties props) throws RunnerException {
        if(snapshot!=null) {
            throw new IllegalStateException("Snapshot in progress");
        }
        publicAddress = props.getProperty(PUBLIC_ADDRESS_PROP);

        try {
            super.start(props);
        } finally {
            // reset the module checksums
            moduleChecksums.remove();
        }
    }
   
    /**
     * Deploy files to the Darkstar server with the given properties.
     * This method first manages deploying any modules specified by the module
     * manager, and also detects any Darkstar runners or services we need
     * to install
     * 
     * @param props the properties to run with
     * @throws IOException if there is an error deploying files
     */
    @Override
    protected void deployFiles(Properties props) throws IOException {
        ModuleManager mm = ModuleManager.getModuleManager();
        
        // first tell the module manager to remove any modules scheduled for
        // removal
        mm.uninstallAll();

        // next tell the module manager to install any pending modules
        mm.installAll();

        // then call the super class's deployFiles() method, which will
        // call the other methods in this class
        super.deployFiles(props);

        // set the relevant properties before super.start() is called
        setDarkstarProperties(props);
    }

    /**
     * Update the properties associated with this deployer before running.
     * This will search for all managers and services declared by any
     * module jar files, and also set the current port for Darkstar
     */
    protected void setDarkstarProperties(Properties props)
        throws IOException
    {
        // go through al the module jars looking for any Darkstar managers and
        // services
        managers = new ArrayList<String>();
        services = new ArrayList<String>();

        File[] moduleFiles = getModuleDir().listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        if (moduleFiles != null) {
            for (File moduleFile : moduleFiles) {
                checkForServices(moduleFile, managers, services);
            }
        }

        // turn the captured manager and service names into properties
        if (managers.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (String manager : managers) {
               sb.append(":" + manager);
            }
            props.put("sgs.managers", sb.toString());
        }

        if (services.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (String service : services) {
               sb.append(":" + service);
            }
            props.put("sgs.services", sb.toString());
        }

        // set the current port & WFS URL to the ones we are now running with.
        // This will stay valid until the runner is stopped.
        currentPort = getPort(props);
        currentWFSName = getWFSName();

        // see if we need to force a coldstart
        if (checkColdstart(props)) {
            props.put("sgs.coldstart", "true");
        }

        // set the WFS URL to load.  This will only be used in the case of
        // a cold start
        if (currentWFSName != null) {
            props.put("sgs.wfs.root", currentWFSName);
        }
    }

    /**
     * Deploy zip files as normal using the superclass.  Copy any
     * .jar files (which are assumed to be modules) into the modules
     * directory
     * @param deploy the file to deploy
     * @throws IOException
     */
    @Override
    protected void deployFile(File deploy) throws IOException {
        if (deploy.getName().endsWith(".jar")) {
            File out = new File(getModuleDir(), deploy.getName());
            RunUtil.writeToFile(new FileInputStream(deploy), out);
        } else {
            super.deployFile(deploy);
        }
    }

    @Override
    protected RunnerChecksums getServerChecksums() throws IOException {
        // get the server checksums
        RunnerChecksums serverChecksums = super.getServerChecksums();

        // now add in the checksums for the modules
        Map<String, RunnerChecksum> checksums = serverChecksums.getChecksums();
        checksums.putAll(getModuleChecksums().getChecksums());
        serverChecksums.setChecksums(checksums);
        return serverChecksums;
    }

    /**
     * Get the module checksums from the thread-local variable.  This is only
     * valid during the method calls within a single invocation of start()
     */
    protected synchronized RunnerChecksums getModuleChecksums()
        throws IOException
    {
        RunnerChecksums out = moduleChecksums.get();
        if (out == null) {
            // read in the new checksums from the server
            URL checksumURL = new URL(webserverURL + CHECKSUM_URL);
            try {
                Reader in = new InputStreamReader(checksumURL.openStream());
                out = RunnerChecksums.decode(in);

                moduleChecksums.set(out);
            } catch (JAXBException je) {
                IOException ioe = new IOException("Error reading checksums " +
                                                  "from " + checksumURL);
                ioe.initCause(je);
                throw ioe;
            }
        }

        return out;
    }

    /**
     * Force a coldstart
     */
    public void forceColdstart() {
        File coldstartFile = new File(getRunDir(), COLDSTART_FILE);
        try {
            coldstartFile.createNewFile();
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error creating " + coldstartFile, ioe);
        }
    }

    /**
     * Check whether we need to force a cold start.  Right now, this just
     * checks the global persistence type, and if the WFS URL has changed since
     * the last time we ran.
     * @param props the properties we are starting with
     * @return true if a coldstart is required, or false if a warm start is OK
     */
    protected boolean checkColdstart(Properties props) {
        if (getPersistenceType(props) == PersistenceType.NONE) {
            System.out.println("[Coldstart] Persistence type: NONE");
            // forece a coldstart
            return true;
        }

        // see if a coldstart file exists
        File coldstartFile = new File(getRunDir(), COLDSTART_FILE);
        if (coldstartFile.exists()) {
            // remove the file, since we are doing a coldstart now
            coldstartFile.delete();
            return true;
        }

        // see if the WFS URL has changed since the last time we ran
        String oldWFSName = null;
        try {
            oldWFSName = restoreWFSName(LAST_WFS_FILE);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error restoring WFS URL", ioe);
        }

        if (oldWFSName == null) {
            return (currentWFSName != null);
        } else {
            return !oldWFSName.equals(currentWFSName);
        }
    }

    /**
     * Get a runner info for this runner
     * @return a runner info, including hostname and port
     */
    @Override
    public RunnerInfo getRunnerInfo() {
        // get the runner info
        RunnerInfo info = super.getRunnerInfo();

        // add our properties
        info.setProperty(HOSTNAME_PROP, getHostname());
        info.setProperty(HOSTNAME_INTERNAL_PROP, getInternalHostname());
        info.setProperty(PORT_PROP, String.valueOf(getPort()));
        info.setProperty(WFSNAME_PROP, getWFSName());
        return info;

    }

    /**
     * Get the Darkstar server name for clients to connect to.
     * @return the external hostname of the Darkstar server
     */
    public String getHostname() {
        // first, see if the Darkstar server has a public hostname we should
        // be giving out instead of the internal one
        String hostname = publicAddress;
        if (hostname == null) {
            // fall back to the internal address, which is guaranteed to be set
            hostname = System.getProperty(Constants.WEBSERVER_HOST_PROP);
        }

        return hostname;
    }

    /**
     * Get the Darkstar internal hostname for clients to connect to.
     * @return the internal hostname of the Darkstar server
     */
    public String getInternalHostname() {
        String hostname = System.getProperty(Constants.WEBSERVER_HOST_INTERNAL_PROP);
        if (hostname == null) {
            return System.getProperty(Constants.WEBSERVER_HOST_PROP);
        }

        return hostname;
    }

    /**
     * Get the Darkstar server port for clients to connect to.  This method
     * returns the port of the currently running server.  If the server
     * is not running, it returns what the port will be the next time the
     * server starts up.
     * @return the port
     */
    public synchronized int getPort() {
        // if the server is running, us the current port variable
        if (getStatus() == Status.RUNNING ||
                getStatus() == Status.STARTING_UP)
        {
            return currentPort;
        } else {
            return getPort(RunManager.getInstance().getStartProperties(this));
        }
    }

    /**
     * Get the port to run on from a set of properties
     * @param properties the properties to look at
     * @return the port to run on
     */
    private int getPort(Properties props) {
        // determine the current port
        String portProp = props.getProperty("sgs.port");
        if (portProp != null) {
            return Integer.parseInt(portProp);
        } else {
            return DEFAULT_PORT;
        }
    }

    /**
     * Get the current WFS name
     * @return the current WFS name
     */
    public String getWFSName() {
        return wfsName;
    }

    /**
     * Set the current WFS name
     * @param name the name of the WFS to load the world from, or null to load
     * an empty world
     */
    public void setWFSName(String wfsName) {
        this.wfsName = wfsName;

        try {
            saveWFSName(wfsName, SELECTED_WFS_FILE);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error saving WFS name", ioe);
        }
    }

    /**
     * Save the current WFS name to disk
     * @param wfsName the name to save
     * @param fileName the file name to save to
     */
    protected void saveWFSName(String wfsName, String fileName) throws IOException {
        File wfsFile = new File(getBaseDir(), fileName);
        PrintWriter out = new PrintWriter(new FileWriter(wfsFile));
       
        if (wfsName != null) {
            out.println(wfsName);
        }

        out.close();
    }

    /**
     * Restore a WFS URL from disk
     * @param fileName the file name to read from
     * @return the current URL, or null if no URL is set
     */
    protected String restoreWFSName(String fileName) throws IOException {
        File wfsFile = new File(getBaseDir(), fileName);
        if (!wfsFile.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(wfsFile));
        return br.readLine();
    }

    /**
     * Create a snapshot with the given name.  The runner must be stopped
     * to call this method.
     * @param name the snapshot name to create
     * @return the snapshot that was created
     * @throws RunnerException if there is an error creating the snapshot
     */
    public void createSnapshot(String name) throws RunnerException {
        if (getStatus() != Status.NOT_RUNNING) {
            throw new IllegalStateException("Snapshots require server to " +
                                            "  be stopped");
        }
        synchronized(this) {
            if(snapshot != null) {
                throw new IllegalStateException("Snapshot in progress");
            }
            
            snapshot = new DarkstarSnapshotRunner(name);
        }        
        try {
            // run the snapshot runner using the RunManager
            RunManager.getInstance().start(snapshot, false);

            // wait for the snapshot runner to exit, which it should do
            // as soon as it finishes starting up
            StatusWaiter waiter = new StatusWaiter(snapshot, Status.NOT_RUNNING);
            waiter.waitFor();
        } catch (InterruptedException ie) {
            // not much we can do here...
        } finally {
            synchronized(this) {
                snapshot = null;
            }
        }
    }

    /**
     * Get the Darkstar server module directory
     * @return the server module directory
     */
    protected File getModuleDir() {
        File moduleDir = new File(getRunDir(), "modules");
        moduleDir.mkdirs();
        return moduleDir;
    }

    /**
     * Check a .jar file for any Darkstar managers and services.
     * @param f the file to check
     * @param managers the list of Darkstar managers to add to
     * @param services the list of Darkstar services to add to
     */
    private void checkForServices(File f, List<String> managers,
                                  List<String> services)
        throws IOException
    {
        JarFile jf = new JarFile(f);

        // look for services
        ZipEntry ze = jf.getEntry("META-INF/services/com.sun.sgs.service.Service");
        if (ze != null) {
            addAll(jf.getInputStream(ze), services);
        }

        // loog for managers
        ze = jf.getEntry("META-INF/services/com.sun.sgs.service.Manager");
        if (ze != null) {
            addAll(jf.getInputStream(ze), managers);
        }
    }

    /**
     * Add all services in a file to a list
     * @param is the InputStream containing the list of files to add
     * @param list the list to add entries to
     */
    private void addAll(InputStream is, List<String> list) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.trim().length() > 0) {
                list.add(line.trim());
            }
        }
    }

    /**
     * Get the persistence type from a property.  The given properties will
     * be checked fist, followed by the system properties.  If neither of
     * those has a value, the default value will be returned
     * @param props the properties to check first
     * @return the current persistence type
     */
    protected PersistenceType getPersistenceType(Properties props) {
        String persistStr = props.getProperty(PERSISTENCE_TYPE_PROP);
        if (persistStr == null) {
            persistStr = System.getProperty(PERSISTENCE_TYPE_PROP);
        }

        if (persistStr != null) {
            return PersistenceType.parsePersistenceType(persistStr);
        } else {
            return PERSISTENCE_TYPE_DEFAULT;
        }
    }

    /**
     * Override the setStatus() method to ignore the RUNNING status.  Instead,
     * we notify other processes that Darkstar is RUNNING when the output
     * reader gets the startup line successfully.
     * @param status the status to set
     */
    @Override
    protected void setStatus(Status status) {
        if (status == Status.RUNNING) {
            return;
        }
        
        super.setStatus(status);
    }
    
    /**
     * Override the createOutputReader method to return a 
     * DarkstarOutputReader that will notify us when Darkstar is really up
     */
    @Override
    protected DarkstarOutputReader createOutputReader(InputStream in,
                                                      Logger out)
    {
        return new DarkstarOutputReader(in, out, new DarkstarStartup() {
            public void darkstarStarted() {
                DarkstarRunnerImpl.this.darkstarStarted();
            }
        });
    }
    
    /**
     * Called when Darkstar starts up
     */
    protected void darkstarStarted() {
        // save the current WFS URL
        try {
            saveWFSName(currentWFSName, LAST_WFS_FILE);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error saving WFS URL", ioe);
        }

        super.setStatus(Status.RUNNING);
    }
    
    /**
     * Wait for Darkstar to fully startup
     */
    protected class DarkstarOutputReader extends BaseRunner.ProcessOutputReader {
        private static final String DARKSTAR_STARTUP =
                "Wonderland: application is ready";

        private DarkstarStartup runner;

        protected DarkstarOutputReader(InputStream in, Logger out,
                                       DarkstarStartup runner)
        {
            super (in, out);
            this.runner = runner;
        }
        
        @Override
        protected void handleLine(String line) {
            // see if this is a Darkstar startup message
            if (isStartupLine(line)) {
                runner.darkstarStarted();
            }
            
            super.handleLine(line);
        }

        /**
         * Determine when we have started up
         * @return true if startup is complete, or false if not
         */
        protected boolean isStartupLine(String line) {
            return line.contains(DARKSTAR_STARTUP);
        }
    }

    /**
     * Create a world snapshot
     */
    protected class DarkstarSnapshotRunner extends BaseRunner
        implements DarkstarStartup
    {
        private String snapshotName;

        protected DarkstarSnapshotRunner(String snapshotName) {
            super();

            this.snapshotName = snapshotName;
        }

        @Override
        public String getName() {
            return DarkstarRunnerImpl.this.getName() + " Snapshot";
        }

        @Override
        public Properties getDefaultProperties() {
            return RunManager.getInstance().getStartProperties(DarkstarRunnerImpl.this);
        }

        @Override
        public synchronized void start(Properties props) throws RunnerException {
            props.put("org.jdesktop.wonderland.server.wfs" +
                      ".exporter.CellExportService.export.on.startup",
                      snapshotName);

            // setup the other Darkstar properties
            try {
                setDarkstarProperties(props);
            } catch (IOException ioe) {
                throw new RunnerException(ioe);
            }
            
            // make sure coldstart is false, otherwise we'll overwrite
            // the database before taking the snapshot
            props.remove("sgs.coldstart");

            // now run
            super.start(props);
        }

        @Override
        protected void deployFiles(Properties props) throws IOException {
            // do nothing
        }

        @Override
        protected synchronized File getBaseDir() {
            return DarkstarRunnerImpl.this.getBaseDir();
        }

        @Override
        protected ProcessOutputReader createOutputReader(InputStream in,
                                                         Logger out)
        {
            return new SnapshotOutputReader(in, out, this);
        }

        public void darkstarStarted() {
            // once the startup is complete, the snapshot has been written
            // and we can exit
            this.stop();
        }

        class SnapshotOutputReader extends DarkstarOutputReader {
            private Pattern success = Pattern.compile("Exported \\d+ cells.");
            private Pattern failure = Pattern.compile("Error creating snapshot");

            protected SnapshotOutputReader(InputStream in, Logger out,
                                           DarkstarStartup runner)
            {
                super (in, out, runner);
            }

            @Override
            protected boolean isStartupLine(String line) {
                Matcher m = success.matcher(line);
                if (m.find()) {
                    return true;
                }

                m = failure.matcher(line);
                if (m.matches()) {
                    return true;
                }

                return false;
            }
        }
    }

    protected static interface DarkstarStartup {
        void darkstarStarted();
    }
}
