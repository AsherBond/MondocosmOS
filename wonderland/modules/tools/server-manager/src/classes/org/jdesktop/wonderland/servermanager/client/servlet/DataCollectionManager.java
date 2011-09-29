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
package org.jdesktop.wonderland.servermanager.client.servlet;

import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdesktop.wonderland.servermanager.client.PingDataCollector;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
        
/**
 * Manage the installation and removal of the PingDataListener
 * @author jkaplan
 */
public class DataCollectionManager implements ServletContextListener 
{
    private static final Logger logger =
            Logger.getLogger(DataCollectionManager.class.getName());
    
    private PingDataCollector pdc;
    private AdminRegistration ar;
    
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        pdc = new PingDataCollector();
        context.setAttribute(PingDataCollector.KEY, pdc);
    
        ar = new AdminRegistration("Monitor Server", 
                                   "/servermanager/servermanager-web");
        ar.setFilter(AdminRegistration.ADMIN_FILTER);
        AdminRegistration.register(ar, context);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        if (pdc != null) {
            pdc.shutdown();
        } else {
            logger.warning("Data collector not found");
        }
        pdc = null;
    
        if (ar != null) {
            AdminRegistration.unregister(ar, context);
        }
    }
}
