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

package org.jdesktop.wonderland.modules.darkstar.server;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.runner.BaseRemoteRunner;
import org.jdesktop.wonderland.runner.RunnerException;
import org.jdesktop.wonderland.runner.RunnerConfigurationException;
import org.jdesktop.wonderland.runner.RunnerInfo;

/**
 * An extension of <code>BaseRemoteRunner</code> with Darkstar-specific
 * information such as hostname and port.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class DarkstarRemoteRunnerImpl extends BaseRemoteRunner
        implements DarkstarRunner
{
    private static final Logger logger =
            Logger.getLogger(DarkstarRemoteRunnerImpl.class.getName());

    private static final String SERVICE = "darkstar/darkstarserver-remote/services/";

    private String hostname;
    private String hostnameInternal;
    private int port;
    private String wfsName;

    private String webserverURL;

    /**
     * The local runner class is DarkstarRunnerImpl
     * @return the runner
     */
    public Class getRunnerClass() {
        return DarkstarRunnerImpl.class;
    }

    /**
     * Get the Darkstar hostname
     * @return the hostname of the darkstar server
     */
    public String getHostname() {
        if (getStatus() == Status.NOT_CONNECTED) {
            throw new IllegalStateException("Darkstar server not connected");
        }

        return hostname;
    }

    /**
     * Get the internal hostname for the darkstar server
     * @return the internal hostname of the darkstar server
     */
    public String getInternalHostname() {
        if (getStatus() == Status.NOT_CONNECTED) {
            throw new IllegalStateException("Darkstar server not connected");
        }

        return hostnameInternal;
    }


    /**
     * Get the Darkstar port
     * @return the port number of the darkstar server
     */
    public int getPort() {
        if (getStatus() == Status.NOT_CONNECTED) {
            throw new IllegalStateException("Darkstar server not connected");
        }

        return port;
    }

    @Override
    public void configure(Properties props)
            throws RunnerConfigurationException
    {
        super.configure(props);

        // record the webserver URL
        webserverURL = props.getProperty("wonderland.web.server.url");
    }

    @Override
    public Properties getDefaultProperties() {
        Properties props = new Properties();
        props.setProperty("sgs.port", String.valueOf(DarkstarRunnerImpl.DEFAULT_PORT));
        props.setProperty("wonderland.web.server.url", webserverURL);
        return props;
    }

    @Override
    public void setRunnerInfo(RunnerInfo info) {
        this.hostname = info.getProperty(DarkstarRunnerImpl.HOSTNAME_PROP);
        this.hostnameInternal = info.getProperty(DarkstarRunnerImpl.HOSTNAME_INTERNAL_PROP);
        this.port = Integer.parseInt(info.getProperty(DarkstarRunnerImpl.PORT_PROP));
        this.wfsName = info.getProperty(DarkstarRunnerImpl.WFSNAME_PROP);

        // set the basic information
        super.setRunnerInfo(info);
    }

    public void createSnapshot(String name) throws RunnerException {
        try {
            URL serviceURL = getDarkstarURL("snapshot", name);
            connectTo(serviceURL);
        } catch (IOException ex) {
            throw new RunnerException(ex);
        }
    }

    public String getWFSName() {
        return wfsName;
    }

    public void setWFSName(String wfsName) {
        try {
            URL serviceURL = getDarkstarURL("setwfsname", wfsName);
            connectTo(serviceURL);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error setting WFS name", ex);
        }
    }

    public void forceColdstart() {
        try {
            URL serviceURL = getDarkstarURL("coldstart", null);
            connectTo(serviceURL);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error setting WFS name", ex);
        }
    }

    protected URL getDarkstarURL(String action, String name) throws IOException {
        URL base = new URL(getBaseURL());
        String encname = URLEncoder.encode(getName(), "UTF-8");
        // replace + with %20 to correspond with other services
        encname = encname.replaceAll("\\+", "%20");
        String encaction = URLEncoder.encode(action, "UTF-8");

        // add on the optional name query parameter
        if (name != null) {
            encaction += "?name=" + URLEncoder.encode(name, "UTF-8");
        }

        return new URL(base, SERVICE + encname + "/" + encaction);
    }
}
