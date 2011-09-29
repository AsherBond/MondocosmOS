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
package org.jdesktop.wonderland.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.utils.Constants;
import org.jdesktop.wonderland.utils.RunUtil;
import org.jdesktop.wonderland.utils.SystemPropertyUtil;
import org.jvnet.winp.WinProcess;

/**
 * A base implementation of <code>Runner</code>.  This implements all the 
 * methods of <code>Runner</code>, so subclasses are only necessary to 
 * differentiate what files should be deployed by the <code>RunManager</code>.
 * @author jkaplan
 */
public abstract class BaseRunner implements Runner {
    /** a logger */
    private static final Logger logger =
            Logger.getLogger(BaseRunner.class.getName());
    
    /** property to set to print out verbose logs from ant */
    private static final String VERBOSE_PROP = "wonderland.runner.verbose";

    /** the URL for listing available files */
    private static final String CHECKSUM_URL =
            "wonderland-web-runner/services/checksums";

    /** the name of this runner */
    private String name = "unknown";

    /** the location of this runner */
    private String location = "localhost";

    /** the base directory (where files and checksums are stored) */
    private File baseDir;

    /** the directory to run in */
    private File runDir;

    /** the directory to store downloaded file in */
    private File downloadDir;

    /** the process we started */
    private Process proc;
    
    /** the log file */
    private File logDir;
    private File logFile;
    private Logger logWriter;
    private Handler logFileHandler;

    /** the base URL to connect to */
    private String serverURL;
    private String localURL;

    /** the current status */
    private Status status = Status.NOT_RUNNING;
    private final Object statusLock = new Object();

    /** status listeners */
    private Set<RunnerStatusListener> listeners = 
            new CopyOnWriteArraySet<RunnerStatusListener>();
   
    /** status updater thread */
    private Thread statusUpdater;
    
    /**
     * No-arg constructor for factory
     */
    protected BaseRunner() {
    }
    
    /**
     * Get the name of this runner.  Only valid after <code>cofigure()</code>
     * is called.
     * @return the name of the runner.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this runner
     * @param name the name of this runner
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Get the location of this runner.  Only valid after configure is
     * called.
     * @return the location of the runner.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of this runner
     * @param location the location of this runner
     */
    protected void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Configure the runner.  Curently parses the following properties:
     * <ul><li><code>runner.name</code> - the name to return in 
     *         <code>getName()</code>
     *     <li><code>runner.location</code> - the location to return in
     *         <code>getLocation()</code>
     * </ul>
     * @param props the properties to configure with
     * @throws RunnerConfigurationException if there is an error
     */
    public void configure(Properties props) throws RunnerConfigurationException {
        if (props.containsKey("runner.name")) {
            setName(props.getProperty("runner.name"));
        }

        if (props.containsKey("runner.location")) {
            setLocation(props.getProperty("runner.location"));
        }
        
        // see if the serverURL was set in the properties
        serverURL = props.getProperty(Constants.WEBSERVER_URL_PROP);
        if (serverURL == null) {
            // the server URL should always be set in the system properties
            serverURL = System.getProperty(Constants.WEBSERVER_URL_PROP);
        }

        // XXX find a better way to get the local URL XXX
        // in some cases, like a remote runner, the local URL may be
        // different than the serverURL.  In that case, make sure we know
        // the localURL as well
        localURL = "http://" + System.getProperty(Constants.WEBSERVER_HOST_INTERNAL_PROP).trim() +
                   ":" + System.getProperty(Constants.WEBSERVER_PORT_PROP).trim() + "/";
    }

    /**
     * Get the default properties for this runner
     * @return an empty properties list
     */
    public Properties getDefaultProperties() {
        return new Properties();
    }

    /**
     * By default, all runners are runnable. Subclasses can override this
     * to return other values
     * @return true
     */
    public boolean isRunnable() {
        return true;
    }

    /**
     * Start the app.  This assumes the default Wonderland packaging, with
     * the ant libraries in "lib/ant" at the top level.  This will create
     * an external ant process to run the top-level build.xml script.
     * @param props the properties to start with
     * @throws RunnerException if there is a problem starting up
     */
    public synchronized void start(Properties props) throws RunnerException {
        // make sure we are in the correct state
        if (getStatus() != Status.NOT_RUNNING) {
            throw new IllegalStateException("Can't start runner in " + 
                                            getStatus() + " state");
        }

        // read properties to update name and location
        if (props.containsKey("runner.name")) {
            setName(props.getProperty("runner.name"));
        }
        if (props.containsKey("runner.location")) {
            setLocation(props.getProperty("runner.location"));
        }

        // update the run directory to make sure we have the latest of
        // everything
        try {
            deployFiles(props);
        } catch (IOException ioe) {
            throw new RunnerException(ioe);
        }

        // setup the logger.  First make sure that a new log will be
        // created.
        resetLogFile();
        try {
            logWriter = Logger.getLogger("wonderland.runner." + getLogName());
            logWriter.setLevel(Level.INFO);
            logWriter.setUseParentHandlers(false);
            logWriter.addHandler(getLogFileHandler());
        } catch (IOException ioe) {
            // no log file, abort
            logger.log(Level.WARNING, "Error creating log file " +
                       getLogFile() + " in runner " + getName());
            throw new RunnerException("Error creating log file", ioe);
        }
        
        try {
            String javaHome = System.getProperty("java.home");
            String fileSep = System.getProperty("file.separator");
            String runHome = getRunDir().getCanonicalPath();
            String antHome = runHome + fileSep + "lib" + fileSep + "ant";
        
            // create the command to execute as a list of strings.  We will
            // convert this to a string array later on in order to execute it.
            // Command will be of approximately the form:
            //
            // $java -cp ${antdir}/ant-launcher.jar -Dant.home=${antdir} \
            //       org.apache.tools.ant.launch.Launcher -Dxxx=yyy \
            //       -f ${rundir}/run.xml
            //
            List<String> cmd = new ArrayList<String>();
            cmd.add(javaHome + fileSep + "bin" + fileSep + "java");
            cmd.add("-cp");
            cmd.add(antHome + fileSep + "ant-launcher.jar");
            cmd.add("-Dant.home=" + antHome);
            cmd.add("org.apache.tools.ant.launch.Launcher");
            
            // add in the properties we were given
            for (Object propName : props.keySet()) {
                cmd.add("-D" + propName + "=" + 
                        props.getProperty((String) propName));
            }

            if (Boolean.parseBoolean(props.getProperty(VERBOSE_PROP))) {
                cmd.add("-v");
            }

            cmd.add("-f");
            cmd.add(runHome + fileSep + "run.xml");
               
            // log the command
            logWriter.info("Executing: " + cmd.toString());
        
            // update status
            setStatus(Status.STARTING_UP);
        
            // execute
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            pb.directory(getRunDir());
            
            proc = pb.start();
            ProcessOutputReader reader = createOutputReader(
                                             proc.getInputStream(), logWriter);
            Thread outReader = new Thread(reader);
            outReader.setName(getName() + " output reader");
            outReader.start();
            
            // start a thread to wait for the process to die
            ProcessWaiter waiter = new ProcessWaiter(proc, outReader, logWriter);
            Thread waiterThread = new Thread(waiter);
            waiterThread.setName(getName() + " process waiter");
            waiterThread.start();
        } catch (IOException ioe) {
            logWriter.log(Level.WARNING, "Error starting process " + getName(), 
                          ioe);
            setStatus(Status.ERROR);
            throw new RunnerException(ioe);
        }    
        
        // everything started up OK, so set the status to RUNNING.  Note
        // that we hold the lock, so even if the process finishes before
        // this is called, the ProcessWaiter won't be able to the the status
        // until we are done, guaranteeing the correct ordering of 
        // RUNNING and STOPPED.
        setStatus(Status.RUNNING);
    }

    public synchronized void stop() {
        // make sure we are in the correct state
        if (getStatus() != Status.RUNNING &&
                getStatus() != Status.STARTING_UP)
        {
            throw new IllegalStateException("Can't stop runner in " + 
                                            getStatus() + " state");
        }
        
        setStatus(Status.SHUTTING_DOWN);

        // workaround for Windows issue when stopping a process. Use
        // an external library that does a better job stopping
        // processes than Process.destroy()
        if (System.getProperty("os.name").startsWith("Windows")) {
            WinProcess wp = new WinProcess(proc);
            wp.killRecursively();
        } else {
            proc.destroy();
        }
    }

    /**
     * Return the file names needed for this runner.  This method returns
     * the filename of the Wonderland core setup file.  Runners can include
     * this file if they wish, or return a different value.
     * @return the core setup file
     */
    protected Collection<String> getDeployFiles() {
        Collection<String> out = new ArrayList<String>();
        out.add("wonderland-setup-dist.zip");

        return out;
    }

    /**
     * Deploy files. This method gets the latest checksums for the runner
     * zips, and then downloads, deploys and runs them if they are out of
     * date.
     * @param props the runtime properties
     * @throws IOException if there is an error reading the files
     */
    protected void deployFiles(Properties props) throws IOException {
        // first get the checksums from the current directory
        RunnerChecksums checksums;

        File checksumsFile = new File(getBaseDir(), "checksums.txt");
        if (checksumsFile.exists()) {
            try {
                checksums = RunnerChecksums.decode(new FileReader(checksumsFile));
            } catch (JAXBException je) {
                IOException ioe = new IOException("Error reading " + checksumsFile);
                ioe.initCause(je);
                throw ioe;
            }
        } else {
            // no checksums, just create an empty one
            checksums = new RunnerChecksums();
        }

        // now figure out if there are changes
        List<RunnerChecksum> addList = new LinkedList<RunnerChecksum>();
        List<RunnerChecksum> removeList = new LinkedList<RunnerChecksum>();
        if (checkForUpdates(checksums, addList, removeList)) {
            // there are changes, apply them

            // start by removing the run dir, since we want to revert back
            // to a clean state
            clearRunDir();

            // now remove all files on the remove list
            for (RunnerChecksum remove : removeList) {
                File removeFile = getFile(remove);
                if (removeFile.exists()) {
                    removeFile.delete();
                }

                checksums.getChecksums().remove(remove.getPathName());
            }

            // then add all files on the add list
            for (RunnerChecksum add : addList) {
                File addFile = getFile(add);
                RunUtil.writeToFile(add.getUrl().openStream(), addFile);

                checksums.getChecksums().put(add.getPathName(), add);
            }

            // finally, go through and deploy all the files
            for (File deploy : getDownloadDir().listFiles()) {
                deployFile(deploy);
            }

            // write the updated checksums
            try {
                checksums.encode(new FileWriter(checksumsFile));
            } catch (JAXBException je) {
                IOException ioe = new IOException("Error writing " + checksumsFile);
                ioe.initCause(je);
                throw ioe;
            }
        }
    }

    /**
     * Deploy the given file
     * @param deploy the file to deploy
     */
    protected void deployFile(File deploy)
        throws IOException
    {
        FileInputStream in = new FileInputStream(deploy);
        RunUtil.extractZip(new ZipInputStream(in), getRunDir());
    }

    /**
     * Clear the run directory
     */
    protected void clearRunDir() {
        RunUtil.deleteDir(getRunDir());
    }

    /**
     * Determine if the checksums are up-to-date for the runners
     * @param checksums the current checksums
     * @param updateList a list of files to download and update our version of
     * @param removeList a list of files to remove
     * @return true if there are changes, or false if not
     */
    protected boolean checkForUpdates(RunnerChecksums checksums,
            List<RunnerChecksum> updateList, List<RunnerChecksum> removeList)
        throws IOException
    {
        // get the checksums from the server
        RunnerChecksums newChecksums = getServerChecksums();

        // get a copy of all the existing keys to check what has changed
        Set<String> toRemove = new HashSet(checksums.getChecksums().keySet());

        // now compare checksums
        for (String deployFile : getDeployFiles()) {
            // remove from the list of files to remove
            toRemove.remove(deployFile);

            // get the old and new versions of the checksum
            RunnerChecksum curcs = checksums.getChecksums().get(deployFile);
            RunnerChecksum newcs = newChecksums.getChecksums().get(deployFile);

            // make sure the file we want exists on the server
            if (newcs == null) {
                throw new IOException("Unable to find file " + deployFile +
                                      " in checksums file");
            }

            // compare the checksum, to decide if it has changed
            if (curcs == null || !curcs.equals(newcs)) {
                updateList.add(newcs);
            }
        }

        // now build the remove list from any files that we currently have
        // checksums for, but weren't on the list of deploy files
        for (String remove : toRemove) {
            removeList.add(checksums.getChecksums().get(remove));
        }

        // determine if there is a change
        return !updateList.isEmpty() || !removeList.isEmpty();
    }

    /**
     * Get the updated list of checksums
     */
    protected RunnerChecksums getServerChecksums()
        throws IOException
    {
        // read in the new checksums from the server
        URL checksumURL = new URL(serverURL + CHECKSUM_URL);
        try {
            Reader in = new InputStreamReader(checksumURL.openStream());
            return RunnerChecksums.decode(in);
        } catch (JAXBException je) {
            IOException ioe = new IOException("Error reading checksums from " +
                                              checksumURL);
            ioe.initCause(je);
            throw ioe;
        }
    }

    /**
     * Translate a checksum into a file
     * @param checksum the checksum file information
     * @return a file for the given checksum
     */
    protected File getFile(RunnerChecksum checksum) {
        String fixName = checksum.getPathName().replace('/', '-');
        return new File(getDownloadDir(), fixName);
    }

    /**
     * Get the directory to install files in.
     * @return the run directory
     */
    protected synchronized File getRunDir() {
        if (runDir == null) {
            runDir = new File(getBaseDir(), "run");
            runDir.mkdirs();
        }

        return runDir;
    }

    /**
     * Get the directory to download files to.
     * @return the download directory
     */
    protected synchronized File getDownloadDir() {
        if (downloadDir == null) {
            downloadDir = new File(getBaseDir(), "download");
            downloadDir.mkdirs();
        }

        return downloadDir;
    }

    /**
     * Get the directory base directory for this runner.
     * @return the run directory
     */
    protected synchronized File getBaseDir() {
        if (baseDir == null) {
            baseDir = new File(RunUtil.getRunDir(), getLogName());
            baseDir.mkdirs();
        }

        return baseDir;
    }

    /**
     * Get the current status of this listener
     */
    public Status getStatus() {
        synchronized (statusLock) {
            return status;
        }
    }

    /**
     * Set the status and notify listeners.  Notifying listeners is done
     * in a separate thread, so we don't have to worry about listeners
     * creating deadlocks.
     * @param status the new status
     */
    protected void setStatus(final Status status) {
        synchronized (statusLock) {
            this.status = status;
        
            // make sure any notification that is in progress completes.  Otherwise
            // we could get out-of-order notifcations
            while (statusUpdater != null && statusUpdater.isAlive()) {
                try {
                   statusUpdater.join();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        
            // start a new thread to do the notifications
            statusUpdater = new Thread(new Runnable() {
                public void run() {
                    for (RunnerStatusListener l : listeners) {
                        try {
                            l.statusChanged(BaseRunner.this, status);
                        } catch (Error e) {
                            // log the exception, since it seems to get
                            // swallowed otherwise
                            logger.log(Level.WARNING, "Error notifying " + l, e);
                            throw e;
                        }
                    }
                }
            });
        
            statusUpdater.setName(getName() + " status update notifier");
            statusUpdater.start();
        }
    }

    /**
     * Get status information on this runner
     * @return the status info
     */
    public RunnerInfo getRunnerInfo() {
        return new RunnerInfo(localURL, this);
    }
    
    /**
     * Get the process log file. 
     * @return the log file, created if it doesn't exist
     */
    public File getLogFile() {
        if (logFile == null) {
            if (Boolean.parseBoolean(
                    SystemPropertyUtil.getProperty("wonderland.log.preserve"))) 
            {
                // create a unqiuely named new log file
                do {
                    String useName = getLogName() + 
                                     ((int) (Math.random() * 65536)) + 
                                     ".log"; 
                    logFile = new File(getLogDir(), useName);
                } while (logFile.exists());
            } else {
                // reuse the name of the runner as the log file
                logFile = new File(getLogDir(), getLogName() + ".log");
            }
        }
        
        return logFile;
    }

    /**
     * Get the log directory
     * @return the log directory
     */
    protected synchronized File getLogDir() {
        if (logDir == null) {
            // check a property
            String logDirProp = SystemPropertyUtil.getProperty("wonderland.log.dir");
            if (logDirProp != null) {
                logDir = new File(logDirProp);
            } else {
                logDir = new File(RunUtil.getRunDir(), "log");
            }

            logDir.mkdirs();
        }

        return logDir;
    }

    /**
     * Reset the log file
     */
    protected synchronized void resetLogFile() {
        logFile = null;
        
        if (logFileHandler != null) {
            logFileHandler.close();
            logFileHandler = null;
        }
        
        if (logWriter != null) {
            logWriter = null;
        }
    }

    /**
     * Get the name of the runner, formatted for using as the name of a log
     * file.  Typically, this will do things like replace " " with "_" and
     * convert to lower case.
     * @return the log-formatted name of this runner
     */
    protected String getLogName() {
        return getName().replaceAll(" ", "_").toLowerCase();
    }
    
    /**
     * Get a log handler that should be used to write the log out to
     * the log file.
     * @return the handler to use
     */
    protected Handler getLogFileHandler() throws IOException {
        logFileHandler = new FileHandler(getLogFile().getCanonicalPath());
        logFileHandler.setLevel(Level.ALL);
        logFileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
       
        return logFileHandler;
    }
    
    /**
     * Create a new process output listener to handle the output from
     * a process.  This method is provided so subclasses can create their own
     * readers
     * @param in the data from the process
     * @param out the logger to log results to
     * @return a ProcessOutputReader that will be used to read output from
     * this process.
     */
    protected ProcessOutputReader createOutputReader(InputStream in,
                                                     Logger out) 
    {
        return new ProcessOutputReader(in, out);
    }
    
    /**
     * Create a new process waiter that waits for a process to end.  This
     * method is provided so subclasses can create their own waiters.
     * @param proc the process to wait for
     * @param outReader the output reader thread
     * @param logWriter the logger to write information to
     * @return the new ProcessWaiter
     */
    protected ProcessWaiter createWaiter(Process proc, Thread outReader,
                                         Logger logWriter) 
    {
        return new ProcessWaiter(proc, outReader, logWriter);
    }
    
    public synchronized Status addStatusListener(RunnerStatusListener listener) 
    {
        listeners.add(listener);
        return getStatus();
    }

    public void removeStatusListener(RunnerStatusListener listener) {
        listeners.remove(listener);
    }

    /**
     * Runners are identified by name.  Equals method is based on the name.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseRunner other = (BaseRunner) obj;
        if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    /**
     * Runners are identified by name.  Hashcode is based on the name.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    /** read a stream from a process and write it to the given log file */
    protected static class ProcessOutputReader implements Runnable {
        private BufferedReader in;
        private Logger out;
        
        public ProcessOutputReader(InputStream is, Logger out) {
            this.in = new BufferedReader(new InputStreamReader(is));
            this.out = out;
        }

        public void run() {
            String line;
            
            try {
                while ((line = in.readLine()) != null) {
                    handleLine(line);
                }
            } catch (IOException ioe) {
                // oh well
                out.log(Level.WARNING, "Exception in process output reader",
                        ioe);
                
                logger.log(Level.WARNING, "Exception in process output reader",
                           ioe);
            }
        }   
        
        protected void handleLine(String line) {
            out.info(line);
        }
    } 
    
    /** wait for a process to end */
    protected class ProcessWaiter implements Runnable {
        private Process proc;
        private Thread outReader;
        private Logger logWriter;
        
        public ProcessWaiter(Process proc, Thread outReader, 
                             Logger logWriter) 
        {
            this.proc = proc;
            this.outReader = outReader;
            this.logWriter = logWriter;
        }

        public void run() {
            int exitValue = -1; 
            
            // first wait for the process to end
            boolean running = true;
            while (running) {
                try {
                    exitValue = proc.waitFor();
                    running = false;
                } catch (InterruptedException ie) {
                    // ignore -- just start waiting again
                }
            }
            
            // now wait for the output
            try {
                outReader.join();
            } catch (InterruptedException ie) {
                // ignore
            }    
            
            // wrte the exit value to the log and then close the log
            logWriter.info("Process exitted, return value: " + exitValue);
            
            // everything has terminated -- update the status
            setStatus(Status.NOT_RUNNING);
        } 
    }
}
