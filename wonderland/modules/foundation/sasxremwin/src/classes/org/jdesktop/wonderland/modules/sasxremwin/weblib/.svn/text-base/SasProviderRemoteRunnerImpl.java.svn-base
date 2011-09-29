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

import java.util.Properties;
import org.jdesktop.wonderland.runner.BaseRemoteRunner;
import org.jdesktop.wonderland.runner.RunnerConfigurationException;
import org.jdesktop.wonderland.utils.Constants;

/**
 * Remote implementation of the sas provider runner
 * @author jkaplan
 */
public class SasProviderRemoteRunnerImpl extends BaseRemoteRunner {
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

        // record the webserver URL
        webserverURL = props.getProperty(Constants.WEBSERVER_URL_PROP);
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

    public Class getRunnerClass() {
        return SasProviderRunnerImpl.class;
    }
}
