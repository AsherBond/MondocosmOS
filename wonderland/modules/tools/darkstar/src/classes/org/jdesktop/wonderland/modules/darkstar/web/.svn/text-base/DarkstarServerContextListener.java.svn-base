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
package org.jdesktop.wonderland.modules.darkstar.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdesktop.wonderland.client.login.DarkstarServer;
import org.jdesktop.wonderland.front.admin.ServerInfo;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.RunManager.RunnerListener;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.Runner.RunnerStatusListener;
import org.jdesktop.wonderland.runner.Runner.Status;

/**
 * Register information about Darkstar servers with the server manager
 * @author jkaplan
 */
public class DarkstarServerContextListener 
        implements ServletContextListener, RunnerListener, RunnerStatusListener
{
    // keep a list of all DarkstarRunners in the system
    private final List<DarkstarRunner> darkstarRunners =
            Collections.synchronizedList(new ArrayList<DarkstarRunner>());

    public void contextInitialized(ServletContextEvent sce) {
        // add a listener for runner updates
        RunManager.getInstance().addRunnerListener(this);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // unregister runner listener
        RunManager.getInstance().removeRunnerListener(this);

        // remove all status listeners
        for (DarkstarRunner dr : darkstarRunners) {
            dr.removeStatusListener(this);
        }
    }

    // called when a new runner is added.  Update our list of Darkstar runners.
    public void runnerAdded(Runner runner) {
        if (runner instanceof DarkstarRunner) {
            darkstarRunners.add((DarkstarRunner) runner);
            runner.addStatusListener(this);
            updateServerList();
        }
    }

    // update when a runner's status changes.  Use this to update the
    // server details object
    public void statusChanged(Runner runner, Status status) {
        if (darkstarRunners.contains(runner)) {
            updateServerList();
        }
    }

    // called when a runner is removed.  Update our list of Darkstar runenrs.
    public void runnerRemoved(Runner runner) {
        if (runner instanceof DarkstarRunner) {
            darkstarRunners.remove((DarkstarRunner) runner);
            runner.removeStatusListener(this);
            updateServerList();
        }
    }

    // update the list of servers based on all the active Darkstar servers
    protected void updateServerList() {
        List<DarkstarServer> external = new ArrayList<DarkstarServer>();
        List<DarkstarServer> internal = new ArrayList<DarkstarServer>();

        synchronized (darkstarRunners) {
            for (DarkstarRunner dr : darkstarRunners) {
                if (dr.getStatus() == Runner.Status.RUNNING) {
                    external.add(new DarkstarServer(dr.getHostname(), dr.getPort()));
                    internal.add(new DarkstarServer(dr.getInternalHostname(), dr.getPort()));
                }
            }
        }

        // update the list of Darkstar servers
        ServerInfo.getServerDetails().setDarkstarServers(external);
        ServerInfo.getInternalServerDetails().setDarkstarServers(internal);
    }
}
