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
package org.jdesktop.wonderland.modules.sasxremwin.weblib;

import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;
import org.jdesktop.wonderland.runner.BaseRunner;
import org.jdesktop.wonderland.runner.RunnerConfigurationException;
import org.jdesktop.wonderland.utils.Constants;

/**
 * An extension of <code>BaseRunner</code> to launch the Darkstar server.
 * @author jkaplan
 */
public class SasProviderRunnerImpl extends BaseRunner {
    /** the default name if none is specified */
    private static final String DEFAULT_NAME = "Shared App Server";
    
    /** the logger */
    private static final Logger logger =
            Logger.getLogger(SasProviderRunnerImpl.class.getName());
    
    /** the URL of the base web server */
    private String webserverURL;

    /**
     * Configure this runner. 
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
        webserverURL = props.getProperty(Constants.WEBSERVER_URL_PROP);
    }
    
    /**
     * Get the set of files to deploy.
     * @return the files to deploy
     */
    @Override
    public Collection<String> getDeployFiles() {
        Collection<String> out = super.getDeployFiles();
        out.add("wonderland-client-dist.zip");
        out.add("sasxremwin-dist.zip");
        return out;
    }
    
    /**
     * Get the default properties for the sas provider.
     * @return the default properties
     */
    @Override
    public Properties getDefaultProperties() {
        Properties props = new Properties();
        props.setProperty(Constants.WEBSERVER_URL_PROP, webserverURL);
        return props;
    }
}
