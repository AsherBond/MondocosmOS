/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.webserver.launcher.WebServerLauncher;

/**
 * Deploy libraries to the web server classpath.  Note that deploying
 * new libraries can be done dynamically, but libraries cannot be updated --
 * that requires a web server restart.
 * @author jkaplan
 */
public class WebLibDeployer extends WebDeployer {
    private static final Logger logger =
            Logger.getLogger(WebLibDeployer.class.getName());

    private static final String CHECKSUM_FILE = "weblibchecksums.list";

    // the set of plugin types we have already initialized. Only one plugin
    // of any type will be active at a time.
    private final Set<Class> plugins = new HashSet<Class>();

    @Override
    public String getName() {
        return "weblib";
    }

    @Override
    public String[] getTypes() {
        return new String[] { "weblib" };
    }

    @Override
    public boolean isDeployable(String type, Module module, ModulePart part) {
        return true;
    }

    @Override
    public boolean isUndeployable(String type, Module module, ModulePart part) {
        // this isn't actually true, but we lie so that modules where the
        // web deployer hasn't changed still work
        return true;
    }

    @Override
    protected void doDeploy(DeployRecord record) throws IOException {
        // add the URL of the record's file to the global classpath
        URL u = record.getFile().toURI().toURL();
        
        logger.info("Weblib deploy: " + u);

        WebServerLauncher.getClassLoader().addURL(u);

        // update plugins
        Iterator<WebLibPlugin> i =
            WebServerLauncher.getClassLoader().getAll(Plugin.class,
                                                      WebLibPlugin.class);
        while (i.hasNext()) {
            WebLibPlugin plugin = i.next();
            if (!plugins.contains(plugin.getClass())) {
                plugin.initialize(RunAppServer.getAppServer());
                plugins.add(plugin.getClass());
            }
        }
    }

    @Override
    protected void doUndeploy(DeployRecord remove) {
        // don't do anything -- we can't remove a URL from that classloader,
        // so we need to restart the web server
        logger.warning("Undeploy web lib: please restart web server");
    }

    @Override
    protected void doExtract(File war, File extractFile) throws IOException {
        // copy the war into the extract file
        FileChannel in = new FileInputStream(war).getChannel();
        FileChannel out = new FileOutputStream(extractFile).getChannel();

        in.transferTo(0, war.length(), out);
    }

    @Override
    protected File getChecksumFile(File deployDir) {
        return new File(deployDir, CHECKSUM_FILE);
    }

    @Override
    protected String[] getFileSuffixes() {
        return new String[] { ".jar" };
    }
}
