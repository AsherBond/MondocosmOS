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
package org.jdesktop.wonderland.modules.webdav.web;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepositoryRegistry;
import org.jdesktop.wonderland.utils.Constants;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 *
 * @author jkaplan
 */
public class FileWebContextListener implements ServletContextListener {
    private static final Logger logger =
            Logger.getLogger(FileWebContextListener.class.getName());

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();

        // make sure base repository directories exist
        File contentDir = RunUtil.getContentDir();
        File systemDir = new File(contentDir, "system");
        systemDir.mkdirs();
        File usersDir = new File(contentDir, "users");
        usersDir.mkdirs();

        // XXX Do we need to get the "/content/" part from a property? -jslott
        String baseURLStr = System.getProperty(Constants.WEBSERVER_URL_PROP);
        baseURLStr += "/webdav/content/";
        URL baseURL = null;
        try {
            baseURL = new URL(baseURLStr);
        } catch (MalformedURLException mue) {
            logger.log(Level.WARNING, "Error parsing URL " + baseURLStr, mue);
        }

        WebContentRepositoryRegistry.getInstance().registerRepository(sc,
                    new FileWebContentRepository(contentDir, baseURL));
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        WebContentRepositoryRegistry.getInstance().unregisterRepository(sc);
    }
}
