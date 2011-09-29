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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.runner.Runner.RunnerStatusListener;
import org.jdesktop.wonderland.runner.Runner.Status;

/**
 * Basic implementation of RemoteRunner that uses web services to get the
 * status of the remote process.
 * @author jkaplan
 */
public abstract class BaseRemoteRunner implements RemoteRunner {
    private static final Logger logger =
            Logger.getLogger(BaseRemoteRunner.class.getName());
    private static final String SERVICE = "wonderland-web-remote-runner/services/runner/";
    
    private final Set<RunnerStatusListener> statusListeners =
            new CopyOnWriteArraySet<RunnerStatusListener>();
    
    private String baseURL;
    private String name;
    private String location = "remote";

    private File logFile;
    private long lastLogUpdate;
    private static final long LOG_UPDATE_TIME = 5000;

    private Status status = Status.NOT_CONNECTED;
    
    /**
     * Get the name of this runner
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    protected void setLocation(String location) {
        this.location = location;
    }

    protected String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public void setStatus(Status status) {
        this.status = status;
        fireStatusChange(status);
    }

    public RunnerInfo getRunnerInfo() {
        return new RunnerInfo(getBaseURL(), this);
    }

    public void setRunnerInfo(RunnerInfo runnerInfo) {
        setBaseURL(runnerInfo.getBaseURL());
        setName(runnerInfo.getName());
        setStatus(Status.valueOf(runnerInfo.getStatus()));
    }

    public void configure(Properties props) throws RunnerConfigurationException {
        if (props.containsKey("runner.name")) {
            setName(props.getProperty("runner.name"));
        }

        if (props.containsKey("runner.location")) {
            setLocation(props.getProperty("runner.location"));
        }
    }

    public Properties getDefaultProperties() {
        return new Properties();
    }

    public boolean isRunnable() {
        return (status != Status.NOT_CONNECTED);
    }

    public void start(Properties props) throws RunnerException {
        if (getStatus() != Status.NOT_RUNNING) {
            throw new IllegalStateException("Can't start runner in " +
                                            getStatus() + " state");
        }

        if (props.containsKey("runner.name")) {
            setName(props.getProperty("runner.name"));
        }

        if (props.containsKey("runner.location")) {
            setLocation(props.getProperty("runner.location"));
        }

        try {
            // open an HTTP connection to the Jersey RESTful service
            connectTo(getURL("start"));

            // if we got here, we are waiting for notification that startup
            // is complete
            fireStatusChange(Status.STARTING_UP);
        } catch (IOException ioe) {
            /* Log an error and return null */
            logger.log(Level.WARNING, "[RemoteRunner] start failed", ioe);
        }
    }

    public void stop() {
        // make sure we are in the correct state
        if (getStatus() != Status.RUNNING &&
                getStatus() != Status.STARTING_UP)
        {
            throw new IllegalStateException("Can't stop runner in " +
                                            getStatus() + " state");
        }

        try {
            // open an HTTP connection to the Jersey RESTful service
            connectTo(getURL("stop"));
        } catch (IOException ioe) {
            /* Log an error and return null */
            logger.log(Level.WARNING, "[RemoteRunner] stop failed", ioe);
        }
    }

    public Status getStatus() {
        return status;
    }

    public File getLogFile() {
        try {
            if (logFile == null) {
                logFile = File.createTempFile("tmplog", "log");
            }

            // see if we need to update the log file
            if (getStatus() == Status.NOT_CONNECTED ||
                    (System.currentTimeMillis() - lastLogUpdate) < LOG_UPDATE_TIME)
            {
                return logFile;
            }

            // read the remote log into a temporary file
            lastLogUpdate = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(logFile);

            // open an HTTP connection to the Jersey RESTful service
            URL url = getURL("log");
            
            byte[] buffer = new byte[16084];
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            int read;
            while ((read = bis.read(buffer)) >= 0) {
                fos.write(buffer, 0, read);
            }
            fos.close();
        } catch (IOException ioe) {
            /* Log an error and return null */
            logger.log(Level.WARNING, "[RemoteRunner] get log failed", ioe);
        }

        return logFile;
    }

    public Status addStatusListener(RunnerStatusListener listener) {
        statusListeners.add(listener);
        return getStatus();
    }

    public void removeStatusListener(RunnerStatusListener listener) {
        statusListeners.remove(listener);
    }

    protected void fireStatusChange(Status status) {
        for (RunnerStatusListener rsl : statusListeners) {
            rsl.statusChanged(this, status);
        }
    }

    protected void connectTo(URL url) throws IOException {
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();

        logger.fine("[BaseRemoteRunner] connect to " + url + " result: " +
                    uc.getResponseCode());

        // read the input, otherwise the method doesn't trigger
        // properly
        byte[] buffer = new byte[16384];
        BufferedInputStream bis = new BufferedInputStream(uc.getInputStream());
        while (bis.read(buffer) >= 0);

        // close the connection
        bis.close();

        // if the response code was bad, throw an exception
        if (uc.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Connection error: " +
                    uc.getResponseMessage() + " (" + uc.getResponseCode() + ")");
        }
    }

    protected URL getURL(String action) throws IOException {
        URL base = new URL(getBaseURL());
        String encname = URLEncoder.encode(getName(), "UTF-8");
        // replace + with %20 to correspond with other services
        encname = encname.replaceAll("\\+", "%20");
        String encaction = URLEncoder.encode(action, "UTF-8");

        return new URL(base, SERVICE + encname + "/" + encaction);
    }
}
