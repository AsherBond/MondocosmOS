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
package org.jdesktop.wonderland.webserver.launcher;

import org.jdesktop.wonderland.utils.FileListUtil;
import org.jdesktop.wonderland.utils.RunUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;
import org.jdesktop.wonderland.utils.Constants;
import org.jdesktop.wonderland.utils.SystemPropertyUtil;

/**
 * Main entry point for Wonderland embedded web server.  This class extracts
 * all the .jar files required to run the web server, and then instantiates
 * the server in a new classloader with all those jars loaded.
 * 
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class WebServerLauncher {

    // port to listen for killswitch connections
    private static final String WEBSERVER_KILLSWITCH_PROPERTY =
            "wonderland.webserver.killswitch";
    // the property to determine what class to launch
    private static final String WEBSERVER_LAUNCH_CLASS_PROPERTY =
            "wonderland.webserver.launch.class";
    private static final String WEBSERVER_LAUNCH_CLASS_DEFAULT =
            "org.jdesktop.wonderland.webserver.RunAppServer";
    private static final Logger LOGGER =
            Logger.getLogger(WebServerLauncher.class.getName());
    private static LauncherClassLoader classLoader;

    /**
     * runs the WebServerLauncher
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // before we do anything, ready the default properties
        try {
            InputStream is = WebServerLauncher.class.getResourceAsStream(
                    "/web-default.properties");
            Properties props = new Properties();
            props.load(is);

            // copy properties into System properties only if they don't already
            // exist.  The means that people can override the defaults by
            // passing an argument like "-Dmy.prop=xxxx" at the command line.
            for (Object prop : props.keySet()) {
                if (!System.getProperties().containsKey(prop)) {
                    System.setProperty((String) prop,
                            props.getProperty((String) prop));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading default properties", ex);
            System.exit(-1);
        }

        // read in the arguments, including the properties file (if any), before
        // we set up logging, so the logging takes the system properties int
        // account
        if (!parseArguments(args)) {
            usage();
            System.exit(-1);
        }

        // create the log directory
        File logDir = new File(SystemPropertyUtil.getProperty(
                "wonderland.log.dir"));
        if (!logDir.exists() && !logDir.mkdirs()) {
            System.err.println("[WebServerLauncher] error creating log " +
                    "directory " + logDir + ".  Logging may not work.");
        }
        
        // now load in the logging configuration -- first see if the user has
        // specified a custom configuration
        InputStream logConfig;
        String logConfigFileName =
                System.getProperty("wonderland.webserver.logging.config.file");
        
        try {
            if (logConfigFileName == null) {
                logConfig = WebServerLauncher.class.getResourceAsStream(
                                                     "/web-logging.properties");
            } else {
                File logConfigFile = new File(logConfigFileName);
                logConfig = new FileInputStream(logConfigFile);
                System.err.println("Using custom logging properties: " +
                                   logConfigFile.getPath());
            }

            // substitute the wonderland log directory for the token
            // %w in the FileHandler path
            Properties p = new Properties();
            p.load(logConfig);
            String filePattern = p.getProperty("java.util.logging.FileHandler.pattern");
            if (filePattern != null && filePattern.contains("%w")) {
                // make sure to use the correct path on Windows
                String quoted = logDir.getPath().replace('\\', '/');
                p.setProperty("java.util.logging.FileHandler.pattern",
                              filePattern.replaceAll("%w", quoted));
                File tmpLog = File.createTempFile("wonderlandlog", ".properties");
                p.store(new FileOutputStream(tmpLog), null);

                logConfig = new FileInputStream(tmpLog);
            }
            
            // read in the configuration
            LogManager.getLogManager().readConfiguration(logConfig);
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error setting up log config", ioe);
        }
   
        // If the web server port property has not been set at this point, then
        // set it to the default
        if (System.getProperty(Constants.WEBSERVER_PORT_PROP) == null) {
            System.setProperty(Constants.WEBSERVER_PORT_PROP, "8080");
        }

        // start the killswitch
        String killSwitchStr =
                System.getProperty(WEBSERVER_KILLSWITCH_PROPERTY);
        if (killSwitchStr != null) {
            KillSwitch ks = new KillSwitch(Integer.parseInt(killSwitchStr));
            new Thread(ks).start();
        }

        try {
            // extract to a webserver directory in the default temp dir
            File webDir = new File(RunUtil.getRunDir(), "webserver");
            webDir.mkdirs();

            // see if we are a new version
            if (!compareVersions(RunUtil.getRunDir())) {
                System.setProperty(Constants.WEBSERVER_NEWVERSION_PROP, "true");

                // if the versions are different, then extract the webserver
                // jars into the webserver directory
                extractWebserverJars(webDir);

                // now write the updated version number
                writeVersion(RunUtil.getRunDir());
            }

            // gather URLs by finding all files in the web directory
            List<URL> urls = new ArrayList<URL>();
            for (File jar : webDir.listFiles()) {
                URL u = jar.toURI().toURL();
                LOGGER.fine("Adding URL " + u);

                urls.add(u);
            }

            // create a classloader with those files and use it
            // to reflectively instantiate an instance of the 
            // launch class, and call its run method
            classLoader = new LauncherClassLoader(urls.toArray(new URL[0]));
            Thread.currentThread().setContextClassLoader(classLoader);

            String launchClass = System.getProperty(
                    WEBSERVER_LAUNCH_CLASS_PROPERTY,
                    WEBSERVER_LAUNCH_CLASS_DEFAULT);

            Class c = classLoader.loadClass(launchClass);
            c.newInstance();

            // log that everything is started up
            System.out.println("-----------------------------------------------------------");
            System.out.println("Wonderland web server started successfully.");
            System.out.println("Log files are in " +
                    SystemPropertyUtil.getProperty("wonderland.log.dir"));

            // get web server URL
            String url = SystemPropertyUtil.getProperty(
                                Constants.WEBSERVER_URL_PROP);
            System.out.println("Web server running on " + url);

            // get the internal host and print it if different
            String internalUrl = SystemPropertyUtil.getProperty(
                                Constants.WEBSERVER_URL_INTERNAL_PROP);
            if (!internalUrl.equals(url)) {
                System.out.println("Web server internal address " + internalUrl);
            }

            System.out.println("-----------------------------------------------------------");

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading web server", ex);
            System.out.println("Error " + ex + " starting web server");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * returns the class loader
     * @return the class loader
     */
    public static LauncherClassLoader getClassLoader() {
        return classLoader;
    }

    private static void usage() {
        System.err.println("Usage: WebServerLauncher [-p port] [-d directory]" +
                " [properties file]");
        System.err.println(" -p port: the port number to run the server on");
        System.err.println(" -d directory: the directory to install Wonderland in");
        System.err.println(" props: a properties file that overrides default values");
    }

    private static boolean parseArguments(String[] args) {
        String port = null;
        String directory = null;
        String propsFile = null;

        Iterator<String> i = Arrays.asList(args).iterator();
        while (i.hasNext()) {
            String s = i.next();

            if (s.equalsIgnoreCase("-p")) {
                if (!i.hasNext()) {
                    return false;
                }

                port = i.next();
            } else if (s.equalsIgnoreCase("-d")) {
                if (!i.hasNext()) {
                    return false;
                }

                directory = i.next();
            } else if (s.trim().length() == 0) {
                continue;
            } else {
                if (i.hasNext()) {
                    return false;
                }

                propsFile = s;
            }
        }

        // first load the properties file, if any
        if (propsFile != null) {
            try {
                System.getProperties().load(new FileInputStream(propsFile));
            } catch (IOException ioe) {
                LOGGER.log(Level.WARNING,
                        "Error reading props file " + propsFile, ioe);
                return false;
            }
        }

        // override the port and directory if specified
        if (port != null) {
            System.setProperty(Constants.WEBSERVER_PORT_PROP, port);
        }

        if (directory != null) {
            System.setProperty(Constants.RUN_DIR_PROP, directory);
        }

        return true;
    }

    /**
     * Compare the Wonderland.jar to the jar file in the run directory.
     * @param runDir the run directory
     * @return true if the versions are the same, or false if they are different
     * @throws IOException if there is an error reading a version file
     */
    private static boolean compareVersions(File runDir)
            throws IOException {
        // read the version from the jar file
        String jarVersion = getJarVersion();

        // read the version in the run directory
        String dirVersion = null;
        File versionFile = new File(runDir, "wonderland.version");
        if (versionFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(versionFile));
            dirVersion = br.readLine();
        }

        LOGGER.warning("Comparing versions.  Jar file: " + jarVersion +
                " Directory: " + dirVersion);

        // compare the values
        if (jarVersion == null || dirVersion == null) {
            // if either value is unset, treat them as different
            return false;
        }

        return (jarVersion.equals(dirVersion));
    }

    private static void writeVersion(File runDir)
            throws IOException {
        String currentVersion = getJarVersion();
        if (currentVersion != null) {
            File versionFile = new File(runDir, "wonderland.version");
            PrintWriter pr = new PrintWriter(new FileWriter(versionFile));
            pr.println(currentVersion);
            pr.close();
        }
    }

    private static String getJarVersion() throws IOException {
        String version = null;

        // try the classpath
        String paths[] = System.getProperty("java.class.path").split(
                System.getProperty("path.separator"));
        for (String path : paths) {
            if (path.endsWith("Wonderland.jar")) {
                File f = new File(path);
                version = String.valueOf(f.lastModified());
            }
        }

        return version;
    }

    private static void extractWebserverJars(File webDir)
            throws IOException {
        // figure out the set of files to add or remove
        List<String> addFiles = new ArrayList<String>();
        List<String> removeFiles = new ArrayList<String>();
        FileListUtil.compareDirs("META-INF/webserver", webDir,
                addFiles, removeFiles);

        // remove files from the remove list
        for (String removeFile : removeFiles) {
            LOGGER.fine("Removing " + removeFile);
            File remove = new File(webDir, removeFile);
            remove.delete();
        }

        // add files from the add list
        for (String addFile : addFiles) {
            LOGGER.fine("Adding " + addFile);
            String fullPath = "/webserver/" + addFile;
            RunUtil.extract(WebServerLauncher.class, fullPath, webDir);
        }

        // write the checksums to the webserver directory
        RunUtil.extract(WebServerLauncher.class,
                "/META-INF/webserver/files.list", webDir);
    }

    /**
     * Classloader that can dynamically add URLs.  Used to support modules
     * with weblib components.
     */
    public static class LauncherClassLoader extends ScannedClassLoader {

        /**
         * creates a new LauncherClassLoader
         * @param urls the URLs from which to load classes and resources
         */
        public LauncherClassLoader(URL[] urls) {
            super(urls);
        }

        /**
         * Add a URL.  Note that URLs cannot be replaced or removed
         * @param url the URL to add
         */
        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
    }

    // listen on a particular socket, and exit the server if that
    // socket disconnects
    static class KillSwitch implements Runnable {

        private int port;

        public KillSwitch(int port) {
            this.port = port;
        }

        public void run() {
            try {
                LOGGER.info("[Killswitch]: Initializing killswitch on port " +
                        port);
                ServerSocket server = new ServerSocket(port);
                Socket s = server.accept();
                LOGGER.info("[Killswitch]: accepted connection");
                while (s.getInputStream().read() != -1) {
                    // do nothing, just wait for the stream to close
                }
            } catch (IOException ioe) {
                // an error occured, just ignore it
                LOGGER.log(Level.WARNING, "Error in killswitch", ioe);
            } finally {
                LOGGER.warning("[Killswitch]: disconnected, server shutting " +
                        "down!");
                System.exit(0);
            }
        }
    }
}
