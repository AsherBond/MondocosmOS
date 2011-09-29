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
package org.jdesktop.wonderland.runner.resources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.jdesktop.wonderland.runner.StatusWaiter;
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
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.RunnerException;

/**
 * The RunnerActionResource class is a Jersey RESTful service that allows
 * services to be started and stopped using a REST API. The get() 
 * method handles the HTTP GET request.
 * 
 * @author jkaplan
 */
@Path(value="/runner/{runner}/{action}")
public class RunnerActionResource {
    private static final Logger logger =
            Logger.getLogger(RunnerActionResource.class.getName());
    
    /**
     * Return a list of all runners currently running.
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces({"text/plain", "application/xml", "application/json"})
    public Response get(@PathParam(value="runner") String runner,
                        @PathParam(value="action") String action,
                        @QueryParam(value="wait")  String waitParam) 
    {
        RunManager rm = RunManager.getInstance();

        try {
            Runner r = rm.get(runner);
            if (r == null) {
                throw new RunnerException("Request for unknown runner: " + 
                                          runner);
            }
        
            boolean wait = false;
            if (waitParam != null) {
                wait = Boolean.parseBoolean(waitParam);
            }
            StatusWaiter waiter = null;
            
            if (action.equalsIgnoreCase("start")) {
                waiter = rm.start(r, wait);
            } else if (action.equalsIgnoreCase("stop")) {
                waiter = rm.stop(r, wait);
            } else if (action.equalsIgnoreCase("restart")) {
                // stop the runner and wait for it to stop
                waiter = rm.stop(r, true);
                if (waiter != null) {
                    waiter.waitFor();
                }
                
                // wait for a bit so that everything gets cleaned up
                try {
                    Thread.sleep(ActionResource.getRestartDelay() * 1000);
                } catch (InterruptedException ie) {
                    // oh well
                }

                // restart the runner
                waiter = rm.start(r, wait);
            } else if (action.equalsIgnoreCase("log")) {
                // read the log file
                if (r.getLogFile() != null) {
                    BufferedReader reader = new BufferedReader(
                                                new FileReader(r.getLogFile()));
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }

                    ResponseBuilder rb = Response.ok(sb.toString());
                    return rb.build();
                }
            } else {
                throw new RunnerException("Unkown action " + action);      
            } 
            
            // if necessary, wait for the runner
            if (waiter != null) {
                waiter.waitFor();
            }
            
            ResponseBuilder rb = Response.ok();
            return rb.build();
        } catch (RunnerException re) {
            logger.log(Level.WARNING, re.getMessage(), re);
            ResponseBuilder rb = Response.status(Status.BAD_REQUEST);
            return rb.build();
        } catch (InterruptedException ie) {
            logger.log(Level.WARNING, ie.getMessage(), ie);
            ResponseBuilder rb = Response.status(Status.INTERNAL_SERVER_ERROR);
            return rb.build();
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
            ResponseBuilder rb = Response.status(Status.INTERNAL_SERVER_ERROR);
            return rb.build();
        }
    }
}
