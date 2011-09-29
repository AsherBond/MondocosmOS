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

import java.io.File;
import java.util.Properties;
import org.jdesktop.wonderland.runner.BaseRunner;
import org.jdesktop.wonderland.runner.Runner.Status;
import org.jdesktop.wonderland.runner.RunnerException;
import org.jdesktop.wonderland.utils.SystemPropertyUtil;

/**
 * Implementation of a runner that presents information about the web
 * server, primarily to show the logs in the server stat us window.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class WebServerRunner extends BaseRunner {
    @Override
    public String getName() {
        if (super.getName() != null) {
            return super.getName();
        }
        
        return "Web Administration Server";
    }

    @Override
    public Status getStatus() {
        return Status.RUNNING;
    }

    @Override
    public File getLogFile() {
        File logDir = new File(SystemPropertyUtil.getProperty("wonderland.log.dir"));

        // log file has a non-standard name
        File logFile = new File(logDir, "web_server.log");
        return logFile;
    }

    @Override
    public boolean isRunnable() {
        return false;
    }

    @Override
    public synchronized void start(Properties props) throws RunnerException {
        // do nothing
    }

    @Override
    public synchronized void stop() {
        // do nothing
    }
}
