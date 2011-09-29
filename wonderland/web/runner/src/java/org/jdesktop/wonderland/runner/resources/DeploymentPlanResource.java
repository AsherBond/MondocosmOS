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
package org.jdesktop.wonderland.runner.resources;

import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.runner.DeploymentEntry;
import org.jdesktop.wonderland.runner.DeploymentManager;
import org.jdesktop.wonderland.runner.DeploymentPlan;

/**
 * The DeploymentPlanResource class is a Jersey RESTful service that returns the
 * DeploymentPlan object for the current RunManager.
 * 
 * @author jkaplan
 */
@Path(value="/deploymentPlan")
public class DeploymentPlanResource {
    private static final Logger logger =
            Logger.getLogger(DeploymentPlanResource.class.getName());
    
    /**
     * Return a list of all runners currently running.
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response getDeploymentPlan(@QueryParam(value="location") String location) {
        DeploymentPlan dp = DeploymentManager.getInstance().getPlan().clone();

        // filter by location
        if (location != null) {
            for (DeploymentEntry de : dp.getEntries().toArray(new DeploymentEntry[0])) {
                if (!location.equals(de.getLocation())) {
                    dp.removeEntry(de);
                }
            }
        }

        ResponseBuilder rb = Response.ok(dp);
        return rb.build();
    }
}
