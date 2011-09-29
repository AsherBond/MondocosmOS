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

package org.jdesktop.wonderland.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.RunnerException;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * An extension of the default app server startup to handle the remote
 * runners.  This web server only runs the minimal remote runner
 * webapp in addition to the basic runner code.
 * @author jkaplan
 */
public class RunRemoteRunner extends RunAppServer {
    private static final Logger logger =
            Logger.getLogger(RunRemoteRunner.class.getName());

    /** the url suffix to find the deployment plan */
    private static final String DEPLOYMENT_PLAN_URL =
            "wonderland-web-runner/services/deploymentPlan";

    /** the url suffix to find the remote status service */
    private static final String REMOTE_SERVICE_URL =
            "wonderland-web-runner/services/remotestatus";

    /** the property for the location */
    private static final String REMOTE_LOCATION_PROP =
            "wonderland.remote.location";

    /** the default location */
    private static final String REMOTE_LOCATION_DEFAULT = "remote";

    /** a timer to use for update notifications */
    private Timer timer = new Timer();

    public RunRemoteRunner() throws IOException {
        super();

        try {
            // start the run manager, which will load from the deployment manager
            RunManager.getInstance().setLocation(System.getProperty(REMOTE_LOCATION_PROP,
                                                                    REMOTE_LOCATION_DEFAULT));
            RunManager.getInstance().initialize();

            // schedule a timer to update the remoote server periodically
            timer.schedule(new RemoteUpdateTask(), 5000, 5000);
        } catch (RunnerException ex) {
            IOException ioe = new IOException("Error initializing runners");
            ioe.initCause(ex);
            throw ioe;
        }
    }

    @Override
    protected void setupProperties() {
        super.setupProperties();

        // disable web deployment
        System.setProperty(WebDeployer.WEBDEPLOY_PARTNAME_PROP, "webremote");

        // set the remote deployment plan URL
        String deployURL = System.getProperty("wonderland.web.server.url");
        if (!deployURL.endsWith("/")) {
            deployURL += "/";
        }
        deployURL += DEPLOYMENT_PLAN_URL;

        String remoteLocation = System.getProperty(REMOTE_LOCATION_PROP,
                                                   REMOTE_LOCATION_DEFAULT);
        deployURL += "?location=" + remoteLocation;
        System.setProperty("wonderland.deployment.plan.url", deployURL);
    }

    @Override
    protected void writeDocumentRoot() throws IOException {
        File docDir = new File(RunUtil.getRunDir(), "docRoot");
        docDir.mkdirs();

        // no files
    }

    @Override
    protected String getWebappDir() {
        return "deployRemote";
    }

    /**
     * Update the status of a runner in the remote service
     * @param runner the runner to update
     */
    protected void updateRemoteStatus(Runner runner) throws IOException {
        URL baseURL = new URL(System.getProperty("wonderland.web.server.url"));
        String encname = URLEncoder.encode(runner.getName(), "UTF-8");
        URL serviceURL = new URL(baseURL, REMOTE_SERVICE_URL + "/" + encname);

        // connect
        URLConnection connection = serviceURL.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/xml");

        try {
            // Write out the status as an XML stream to the output connection
            runner.getRunnerInfo().encode(connection.getOutputStream());
            connection.getOutputStream().close();
        } catch (JAXBException je) {
            IOException ioe = new IOException("Error encoding runner");
            ioe.initCause(je);
            throw ioe;
        }

        // For some reason, we need to read in the input for the HTTP
        // POST to work
        InputStreamReader r = new InputStreamReader(connection.getInputStream());
        while (r.read() != -1) {
            // Do nothing
        }
        r.close();
    }

    // task to periodically update the status of all runners with the remote
    // server
    class RemoteUpdateTask extends TimerTask {
        @Override
        public void run() {
            RunManager rm = RunManager.getInstance();

            try {
                for (Runner r : rm.getAll()) {
                    updateRemoteStatus(r);
                }
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error updating", ioe);
            }
        }
    }
}
