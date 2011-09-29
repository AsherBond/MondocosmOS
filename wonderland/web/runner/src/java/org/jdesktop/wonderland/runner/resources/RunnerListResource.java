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

import java.util.Collection;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.wrapper.RunnerListWrapper;

/**
 * The RunnerListResource class is a Jersey RESTful service that returns the
 * list of services that are running. The getRunnerList() method
 * handles the HTTP GET request.
 * 
 * @author jkaplan
 */
@Path(value="/list")
public class RunnerListResource {
    private static final Logger logger =
            Logger.getLogger(RunnerListResource.class.getName());
    
    /**
     * Return a list of all runners currently running.
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces({"text/plain", "application/xml", "application/json"})
    public Response getRunnerList() {
        Collection<Runner> runners = RunManager.getInstance().getAll();
        RunnerListWrapper out = new RunnerListWrapper(runners);
        
        ResponseBuilder rb = Response.ok(out);
        return rb.build();
    }
}
