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

package org.jdesktop.wonderland.webserver;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.modules.spi.ModuleDeployerSPI;

/**
 * Deployer that sets system properties based on a properties file.
 * Properties set this way will not override properties that are
 * already set, so users can still specify override values for properties
 * in my.run.properties.  Properties cannot be undeployed.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class SystemPropertyDeployer implements ModuleDeployerSPI {
    private static final Logger logger =
            Logger.getLogger(SystemPropertyDeployer.class.getName());

    public String getName() {
        return "properties";
    }

    public String[] getTypes() {
        return new String[] { "properties" };
    }

    public boolean isDeployable(String type, Module module, ModulePart part) {
        return true;
    }

    public boolean isUndeployable(String type, Module module, ModulePart part) {
        return true;
    }

    public void deploy(String type, Module module, ModulePart part) {
        // list each properties file in the part directory, and load the
        // properties from that file
        File dir = part.getFile();
        File[] props = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return !pathname.isDirectory() &&
                        pathname.getName().endsWith("properties");
            }         
        });
        
        // nothing to load
        if (props == null) {
            return;
        }
        
        // load each file
        for (File prop : props) {
            loadProperties(module, part, prop);
        }
    }

    public void loadProperties(Module module, ModulePart part, File propsFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propsFile));
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error reading properties from module " +
                       module.getName() + " part " + part.getName(), ex);
        }

        // go through each property, and set it in the system properties
        // if it is not already set.
        for (String prop : props.stringPropertyNames()) {
            if (!System.getProperties().containsKey(prop)) {
                System.setProperty(prop, props.getProperty(prop));
            }
        }
    }

    public void undeploy(String type, Module module, ModulePart part) {
        // do nothing
    }
}
