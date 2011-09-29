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
package org.jdesktop.wonderland.runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.runner.Runner.Status;
import org.jdesktop.wonderland.utils.AppServerMonitor;
import org.jdesktop.wonderland.utils.SystemPropertyUtil;

/**
 * Singleton manager for starting and stopping services.
 * <p>
 * When a new runner is added to the manager, the manager will attempt to
 * deploy the correct environment to the runner.  This consists of calling
 * the runner's <code>deploy()</code> method with a set of libraries.
 * The libraries are determined by reading a configuration file, in this
 * case located in the <code>RunManager</code>'s jar file, in the
 * <code>META-INF/deploy</code> directory.  The manager will attempt
 * to deploy all files listed in a file with the fully-qualified classname
 * of the runner.  So if the Runner is of class 
 * <code>org.jdesktop.wonderland.sample.SampleRunner</code>, the file
 * <code>META-INF/deploy/org.jdesktop.wonderland.sample.SampleRunner</code>
 * will be expected to contain the list of zip files to deploy.  The 
 * list of files is formatted one per line, with each line containing the
 * path to a zip file in the manager's jar file.
 * 
 * @author jkaplan
 */
public class RunManager {
    /** a logger */
    private static final Logger logger =
            Logger.getLogger(RunManager.class.getName());

    /** the properties for starting and stopping */
    private static final String START_PROP = "wonderland.runner.autostart";
    private static final String STOP_PROP  = "wonderland.runner.autostop";

    /** the singleton instance */
    private static RunManager runManager;
    
    /** the path of the deploy information in this archive */
    public static final String DEPLOY_DIR = "runner";

    /** the default location */
    private static final String DEFAULT_LOCATION = "localhost";

    /** the name of the location we are starting runners for */
    private String location = DEFAULT_LOCATION;

    /** the set of runners we manage, index by name */
    private Map<String, Runner> runners = new LinkedHashMap<String, Runner>();

    /** runner listeners */
    private Set<RunnerListener> listeners = new CopyOnWriteArraySet<RunnerListener>();

    /**
     * Get the singleton instance
     */
    public synchronized static RunManager getInstance() {
        if (runManager == null) {
            runManager = new RunManager();
        }
        
        return runManager;
    }
    
    /**
     * Constructor is private.  Use getInstance() to get the singleton
     * instance.
     */
    private RunManager() {
    }

    /**
     * Get the location this RunManager will start entries for
     * @return the location for this run manager
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location this RunManager will start entries for
     * @param location the location for this run manager
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Initialize this manager by loading all runners from the
     * default deployment plan, and starting them if they are set
     * to autostart.
     * @throws RunnerException if there is a problem starting one of the
     * runners.
     */
    public void initialize() throws RunnerException {
        // before we start everything up, we need to make sure the web
        // server startup is complete. Until then, there may be modules needed
        // by the various runners that aren't installed yet.
        // If the startup is not complete, register a listener that will be
        // notified when the server startup is complete
        
        if (AppServerMonitor.getInstance().isStartupComplete()) {
            doInit();
        } else {
            AppServerMonitor.getInstance().addListener(
                    new AppServerMonitor.AppServerStartupListener()
            {
                public void startupComplete() {
                    try {
                        doInit();
                    } catch (RunnerException re) {
                        logger.log(Level.WARNING, "Error during initialization",
                                   re);
                    }
                }
            });
        }
    }

    protected void doInit() throws RunnerException {

        logger.info("[RunManager] Starting all apps");
        if (Boolean.parseBoolean(SystemPropertyUtil.getProperty(STOP_PROP))) {
            // Add a listener that will stop all active processes when the
            // container shuts down
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdown();
                }
            });

            System.out.println("Shutdown hook registered");
        }

        boolean start =
            Boolean.parseBoolean(SystemPropertyUtil.getProperty(START_PROP));

        // create a list of runners to start after all runners have been
        // created
        List<Runner> startList = new ArrayList<Runner>();

        DeploymentPlan dp = DeploymentManager.getInstance().getPlan();
        for (DeploymentEntry de : dp.getEntries()) {
            // copy System properties to pass into this runner
            Properties props = new Properties(System.getProperties());

            // add in all the properties associated with this deployment
            // entry
            props.putAll(de.getProperties());
            props.setProperty("runner.name", de.getRunnerName());
            props.setProperty("runner.location", de.getLocation());

            try {
                Runner r = create(de.getRunnerClass(), props);
                r = add(r);

                // only start runners if they have the same location
                // specified as this manager
                if (start && r.getLocation().equals(getLocation()) &&
                        r.getStatus() == Status.NOT_RUNNING)
                {
                    startList.add(r);
                }
            } catch (IOException ioe) {
                throw new RunnerException("Error adding runner " +
                                          de.getRunnerName(), ioe);
            }
        }

        // now that all runners are created, go ahead and start the ones from
        // the start list
        for (Runner r : startList) {
            r.start(getStartProperties(r));
        }
    }

    /**
     * Stop all existing applications.
     */
    public void shutdown() {
        System.out.println("[RunManager] Stopping all apps");

        // copy the list of runners into a new list, so we don't get
        // concurrent modification issues
        Collection<Runner> all = new ArrayList(getAll());

        // stop all active applications
        for (Runner runner : all) {
            if (runner.getStatus() == Runner.Status.RUNNING ||
                    runner.getStatus() == Runner.Status.STARTING_UP)
            {
                runner.stop();
            }

            // remove runners and notify listeners.  A new call to
            // initialize() will recreate all appropriate runners.
            remove(runner.getName());
        }
    }

    /**
     * Add a new <code>Runner</code> managed by this manager.  Note that
     * the returned runner may be different than the original one you
     * passed in, for example because it is decorated. After calling this
     * method, use the returned runner and not the one you passed in.
     * <p>
     * This step additionally deploys all zip files associated with the
     * runner.  After <code>add()</code> is called, the runner is ready
     * to be started with the <code>start()</code> method.
     * 
     * @param runner the runner to manage
     * @return a decorated version of the managed runner
     * @throws IOException if there is an error adding the runner or
     * deploying zips to it.
     */
    public Runner add(Runner runner) throws IOException {
        synchronized (this) {
            runners.put(runner.getName(), runner);
        }

        // notify listeners
        for (RunnerListener rl : listeners) {
            rl.runnerAdded(runner);
        }

        return runner;
    }
    
    /**
     * Get a runner by name
     * @param name the name of the runner to get
     * @return a runner with the given name, or null if no runner exists
     * with that name
     */
    public synchronized Runner get(String name) {
        return runners.get(name);
    }
    
    /**
     * Get all runners.  The order that runners is returned should be 
     * consistent based on the order they were added.
     * @return the runners
     */
    public synchronized Collection<Runner> getAll() {
        return runners.values();
    }
    
    /**
     * Get all runners of the given type. Each runner will be tested
     * using "instanceof", and this method will return the list of
     * runners that implement the given type.
     * @param clazz the class of runner to get
     * @return the list of runners matching the given class, or an empty
     * list if no runners match
     */
    public synchronized <T extends Runner> Collection<T> getAll(Class<T> clazz) {
        Collection<T> out = new ArrayList<T>();
        for (Runner r : getAll()) {
            if (clazz.isAssignableFrom(r.getClass())) {
                out.add(clazz.cast(r));
            }
        }
        return out;
    }
        
    
    /**
     * Remove a runner by name
     * @param name the name of the runner to remove
     * @return the removed runner, or null if the runner isn't found
     */
    public Runner remove(String name) {
        Runner runner;

        synchronized (this) {
            runner = runners.remove(name);
        }

        if (runner != null) {
            // notify listeners
            for (RunnerListener rl : listeners) {
                rl.runnerRemoved(runner);
            }
        }
        
        return runner;
    }

    /**
     * Start the given runner.
     * @param runner the runner to start
     * @param wait whether or not to wait for the runner to start
     * @return the StatusWaiter that waits for this runner to start, or
     * null if wait is false
     * @throws RunnerException if there is a problem starting the runner
     */
    public StatusWaiter start(Runner runner, boolean wait)
        throws RunnerException
    {
        StatusWaiter out = null;

        if (runner.getStatus() == Runner.Status.NOT_RUNNING) {
            runner.start(getStartProperties(runner));

            if (wait) {
                out = new StatusWaiter(runner, Runner.Status.RUNNING);
            }
        }

        return out;
    }

    /**
     * Stop the given runner.
     * @param runner the runner to stop
     * @param wait whether or not to wait for the runner to stop
     * @return the StatusWaiter that waits for this runner to stop, or
     * null if wait is false
     * @throws RunnerException if there is a problem stopping the runner
     */
    public StatusWaiter stop(Runner runner, boolean wait)
        throws RunnerException
    {
        StatusWaiter out = null;

        if (runner.getStatus() == Runner.Status.RUNNING || 
                runner.getStatus() == Runner.Status.STARTING_UP)
        {
            runner.stop();

            if (wait) {
                out = new StatusWaiter(runner, Runner.Status.NOT_RUNNING);
            }
        }

        return out;
    }

    /**
     * Get the run properties for starting the given runner.  Properties are
     * determined by getting the deployment entry for the given runner.
     * If the deployment entry doesn't have any properties, the defaults
     * as specified by the runner are used.
     *
     * @param runner the runner to get properties for
     * @return the properties to use when starting that runner
     */
    public Properties getStartProperties(Runner runner) {
        // find a properties file from the current deployment plan
        DeploymentPlan dp = DeploymentManager.getInstance().getPlan();
        DeploymentEntry de = dp.getEntry(runner.getName());

        Properties props = runner.getDefaultProperties();
        if (de != null) {
            props.putAll(de.getProperties());

            // add in the name and location (since these might have changed)
            props.setProperty("runner.name", de.getRunnerName());
            props.setProperty("runner.location", de.getLocation());
        }

        return props;
    }

    /**
     * Add a listener
     * @param listener the listener to add
     */
    public void addRunnerListener(RunnerListener rl) {
        listeners.add(rl);
    }

    /**
     * Remove a listener
     * @param listener the listener to remove
     */
    public void removeRunnerListener(RunnerListener rl) {
        listeners.remove(rl);
    }

    /**
     * Create a new runner of the given type
     * @param className the runner type, a fully-qualified class name
     * @param props the properties to configure the runner with
     * @throws RunnerException if there is an error creating the runner
     */
    public Runner create(String className, Properties props)
            throws RunnerException
    {
        try {
            Class<Runner> clazz = (Class<Runner>) Class.forName(className);
            Runner r = clazz.newInstance();

            // if the location is set to anything other than the default,
            // then we are a remote runner, so we should translate the
            // class of any remote runners we find into local runners.
            if (!getLocation().equals(DEFAULT_LOCATION) &&
                    r instanceof RemoteRunner)
            {
                Class<Runner> localClass = ((RemoteRunner) r).getRunnerClass();
                r = localClass.newInstance();
            }

            r.configure(props);
            return r;
        } catch (ClassNotFoundException cnfe) {
            throw new RunnerCreationException(cnfe);
        } catch (InstantiationException ie) {
            throw new RunnerCreationException(ie);
        } catch (IllegalAccessException iae) {
            throw new RunnerCreationException(iae);
        }
    }

    /**
     * A listener that will be notified when runners are added or removed
     */
    public interface RunnerListener {
        /**
         * Called when a runner is added
         * @param runner the runner that was added
         */
        public void runnerAdded(Runner runner);

        /**
         * Called when a runner is removed
         * @param runner the runner that was removed
         */
        public void runnerRemoved(Runner runner);
    }
}
