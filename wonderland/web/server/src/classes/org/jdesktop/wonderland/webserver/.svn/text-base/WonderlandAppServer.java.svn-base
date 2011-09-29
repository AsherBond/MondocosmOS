/*
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

import com.sun.enterprise.deploy.shared.ArchiveFactory;
import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.ModuleDependency;
import com.sun.enterprise.module.ModuleMetadata;
import com.sun.enterprise.module.ModulesRegistry;
import com.sun.hk2.component.InhabitantsParser;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.deployment.archive.ReadableArchive;
import org.glassfish.api.embedded.EmbeddedDeployer;
import org.glassfish.api.embedded.LifecycleException;
import org.glassfish.api.embedded.Server;
import org.jdesktop.wonderland.webserver.launcher.WebServerLauncher;
import org.jvnet.hk2.component.Habitat;

/**
 *
 * @author jkaplan
 */
public class WonderlandAppServer {
    private static final Logger logger =
            Logger.getLogger(WonderlandAppServer.class.getName());

    private final Server server;
    private boolean deployable = false;

    public WonderlandAppServer(Server server) {
        this.server = server;

        // add a module to properly specify the classpath
        Habitat h = server.getHabitat();
        ModulesRegistry mr = h.getComponent(ModulesRegistry.class);
        mr.add(new ClasspathModuleDefinition(WebServerLauncher.getClassLoader()));
    }

    public Server getServer() {
        return server;
    }

    public synchronized boolean isDeployable() {
        return deployable;
    }

    public synchronized void setDeployable(boolean deployable) {
        this.deployable = deployable;
    }

    public void start() throws LifecycleException {
        getServer().start();
    }

    public void stop() throws LifecycleException  {
        getServer().stop();
        setDeployable(false);
    }

    public String deploy(File file, DeployCommandParameters params)
        throws LifecycleException
    {
        try {
            if (!file.exists()) {
                throw new IOException("File " + file.getPath() + " not found.");
            }

            return server.getDeployer().deploy(file, params);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new LifecycleException(ioe);
        }
    }

    public String deploy(File file) throws LifecycleException {
        DeployCommandParameters params = new DeployCommandParameters(file);
        return deploy(file, params);
    }

    public EmbeddedDeployer getDeployer() {
        return server.getDeployer();
    }

    static class ClasspathModuleDefinition implements ModuleDefinition {

        private static final String[] EMPTY_STRING_ARR = new String[0];
        private static final ModuleDependency[] EMPTY_DEPEND_ARR = new ModuleDependency[0];
        private final ModuleMetadata metadata = new ModuleMetadata();
        private final Manifest manifest = new Manifest();
        private URLClassLoader classLoader;

        public ClasspathModuleDefinition(URLClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        public String getName() {
            return "Wonderland web server classpath";
        }

        public String[] getPublicInterfaces() {
            return EMPTY_STRING_ARR;
        }

        public ModuleDependency[] getDependencies() {
            return EMPTY_DEPEND_ARR;
        }

        public URI[] getLocations() {
            URL[] in = classLoader.getURLs();
            Set<URI> out = new LinkedHashSet<URI>(in.length);

            for (URL u : in) {
                try {
                    out.add(u.toURI());
                } catch (URISyntaxException use) {
                    logger.log(Level.WARNING, "Error creating URI: " + u, use);
                }
            }

            return out.toArray(new URI[0]);
        }

        public String getVersion() {
            return "1.0.0";
        }

        public String getImportPolicyClassName() {
            return null;
        }

        public String getLifecyclePolicyClassName() {
            return null;
        }

        public Manifest getManifest() {
            return manifest;
        }

        public ModuleMetadata getMetadata() {
            return metadata;
        }
    }
}
