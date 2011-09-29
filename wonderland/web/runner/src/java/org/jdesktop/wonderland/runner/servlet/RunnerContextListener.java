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
package org.jdesktop.wonderland.runner.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdesktop.wonderland.runner.RunManager;

/**
 * Manager that gets notified of context create and destroy.  Use this
 * to initialize the RunManager.
 * @author jkaplan
 */
public class RunnerContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        // Add all runners in the current deployment plan
        try {
            RunManager.getInstance().initialize();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        RunManager.getInstance().shutdown();
    }
}
