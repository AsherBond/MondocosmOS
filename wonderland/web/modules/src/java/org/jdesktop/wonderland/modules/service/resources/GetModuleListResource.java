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
package org.jdesktop.wonderland.modules.service.resources;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleList;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.service.DeployManager;
import org.jdesktop.wonderland.modules.service.ModuleManager;

/**
 * The GetModuleListResource class is a Jersey RESTful service that returns the
 * ModuleInfo objects (contained within module.xml) of all modules in a given
 * state.
 * <p>
 * The state can either be installed, pending, or uninstall
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path("/list/get/{state}")
public class GetModuleListResource {
    
    /**
     * Returns a list of modules in a given state.
     * <p>
     * /module/list/get/http://localhost:8080/wonderland-web-modules/modules/list/get/installed{state}
     * <p>
     * where {state} is the state of the module, either pending, installed, or
     * uninstall.
     * <p>
     * All spaces in the module name must be encoded to %20. Returns BAD_REQUEST
     * to the HTTP connection if the module name is invalid or if there was an
     * error encoding the module's information.
     * 
     * @param state The desired state of the module
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response getModuleList(@PathParam("state") String state) {
        ModuleManager manager = ModuleManager.getModuleManager();
        ModuleList moduleList = new ModuleList();
        
        /*
         * Check the state given, and fetch the modules. If the module state is
         * invalid, return a BAD_REQUEST error. Otherwise fetch the module list
         * according to the state and return a ModuleList object.
         */
        if (state == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else if (state.equals("installed") == true) {
            Map<String, Module> modules = manager.getInstalledModules();

            // sort in dependecy order
            List<String> ordered = DeployManager.getDeploymentOrder(modules);

            // create the list of infos in the correct order
            Collection<ModuleInfo> list = new LinkedList();
            for (String moduleName : ordered) {
                Module module = modules.get(moduleName);
                list.add(module.getInfo());
            }

            moduleList.setModuleInfos(list.toArray(new ModuleInfo[] {}));
            return Response.ok(moduleList).build();
        }
        else if (state.equals("pending") == true) {
            Map<String, Module> modules = manager.getPendingModules();
            Collection<ModuleInfo> list = new LinkedList();
            Iterator<Map.Entry<String, Module>> it = modules.entrySet().iterator();
            while (it.hasNext() == true) {
                Map.Entry<String, Module> entry = it.next();
                list.add(entry.getValue().getInfo());
            }
            moduleList.setModuleInfos(list.toArray(new ModuleInfo[] {}));
            return Response.ok(moduleList).build();
        }
        else if (state.equals("uninstall") == true) {
            Map<String, ModuleInfo> modules = manager.getUninstallModuleInfos();
            Collection<ModuleInfo> list = new LinkedList();
            Iterator<Map.Entry<String, ModuleInfo>> it = modules.entrySet().iterator();
            while (it.hasNext() == true) {
                Map.Entry<String, ModuleInfo> entry = it.next();
                list.add(entry.getValue());
            }
            moduleList.setModuleInfos(list.toArray(new ModuleInfo[] {}));
            return Response.ok(moduleList).build();
        }
        else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
