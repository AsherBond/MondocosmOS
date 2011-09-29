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
package org.jdesktop.wonderland.web.asset.resources;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.common.modules.ModulePluginList;
import org.jdesktop.wonderland.common.JarURI;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer.DeployedAsset;

/**
 * TBD
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path(value="/jars/get")
public class ModuleJarListResource {
    
    /**
     * TBD
     */
    @GET
    public Response getJarList() {
        Logger logger = Logger.getLogger(ModuleJarListResource.class.getName());
        Collection<JarURI> jarURIs = new LinkedList<JarURI>();

        /*
         * Get a map of all of the File objects for each art asset. We use the
         * convention that the first element of the 'path' is the asset type
         * (e.g. art, client, etc). Look for the entry that matches.
         */
        Map<DeployedAsset, File> assetMap = AssetDeployer.getFileMap();
        Iterator<DeployedAsset> it = assetMap.keySet().iterator();
        while (it.hasNext() == true) {
            DeployedAsset asset = it.next();
            if (asset.assetType.equals("common") == true || asset.assetType.equals("client") == true) {
                String files[] = assetMap.get(asset).list();
                if (files == null) {
                    continue;
                }
                for (String file : files) {
                    if (file.endsWith(".jar") == true) {
                        try {
                            jarURIs.add(this.getPluginJarURI(asset.moduleName, file, asset.assetType));
                        } catch (URISyntaxException excp) {
                            logger.log(Level.WARNING, "[MODULES] GET PLUGINS Invalid JAR URI", excp);
                        }
                    }
                }
            }
        }
        
        /* Otherwise, return a response with the input stream */
        ModulePluginList mpl = new ModulePluginList();
        mpl.setJarURIs(jarURIs.toArray(new JarURI[] {}));

        /* Write the XML encoding to a writer and return it2 */
        StringWriter sw = new StringWriter();
        try {
            mpl.encode(sw);
            ResponseBuilder rb = Response.ok(sw.toString());
            return rb.build();
        } catch (javax.xml.bind.JAXBException excp) {
            /* Log an error and return an error response */
            logger.log(Level.WARNING, "[MODULES] GET PLUGINS Unable to encode", excp);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
    }
    
    /**
     * Takes a module name, jar name, and jar type (client or common) and
     * returns the URL to represent it.
     */
    private JarURI getPluginJarURI(String moduleName, String jarName, String type) throws URISyntaxException {
        return new JarURI("wlj://" + moduleName + "/" + type + "/" + jarName);
    }
}
