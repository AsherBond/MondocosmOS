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
package org.jdesktop.wonderland.modules.darkstar.web.resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.RunnerException;

/**
 * The DarkstarRunnerActionResource class is a Jersey RESTful service that
 * allows Darkstar-specific actions (like snapshot) from a web service.
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@Path(value="/{runner}/{action}")
public class DarkstarRunnerActionResource {
    private static final Logger logger =
            Logger.getLogger(DarkstarRunnerActionResource.class.getName());
    
    /**
     * Return a list of all runners currently running.
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces({"text/plain", "application/xml", "application/json"})
    public Response get(@PathParam(value="runner") String runner,
                        @PathParam(value="action") String action,
                        @QueryParam(value="name")  String nameParam)
    {
        RunManager rm = RunManager.getInstance();

        try {
            Runner r = rm.get(runner);
            if (r == null) {
                throw new RunnerException("Request for unknown runner: " + 
                                          runner);
            }
        
            if (!(r instanceof DarkstarRunner)) {
                throw new RunnerException("Only Darkstar runner supported");
            }

            DarkstarRunner dr = (DarkstarRunner) r;

            if (action.equalsIgnoreCase("snapshot")) {
                if (nameParam == null) {
                    throw new RunnerException("Name is required");
                }
                dr.createSnapshot(nameParam);
            } else if (action.equalsIgnoreCase("setwfsname")) {
                if (nameParam == null) {
                    throw new RunnerException("Name is required");
                }
                dr.setWFSName(nameParam);
            } else if (action.equalsIgnoreCase("coldstart")) {
                dr.forceColdstart();
            } else {
                throw new RunnerException("Unkown action " + action);      
            } 
            
            ResponseBuilder rb = Response.ok();
            return rb.build();
        } catch (RunnerException re) {
            logger.log(Level.WARNING, re.getMessage(), re);
            ResponseBuilder rb = Response.status(Status.BAD_REQUEST);
            return rb.build();
        }
    }
}
