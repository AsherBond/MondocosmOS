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
package org.jdesktop.wonderland.servermanager.client.resources;

import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.servermanager.client.PingData;
import org.jdesktop.wonderland.servermanager.client.PingDataCollection;
import org.jdesktop.wonderland.servermanager.client.PingDataCollector;

/**
 * The PingDataResource class is a Jersey RESTful service that returns the
 * list of ping data we have recorded. 
 * 
 * @author jkaplan
 */
@Path(value = "/pingData")
public class PingDataResource {

    private static final Logger logger =
            Logger.getLogger(PingDataResource.class.getName());
    
    @Context
    private ServletContext context;

    /**
     * Return a list of all runners currently running.
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces({"text/plain", "application/xml", "application/json"})
    public Response getPingData(@QueryParam(value="after") String afterParam,
                                @QueryParam(value="count") String countParam) 
    {
        long after = 0;
        if (afterParam != null) {
            after = Long.parseLong(afterParam);
        }
        
        int count = Integer.MAX_VALUE;
        if (countParam != null) {
            count = Integer.parseInt(countParam);
        }
        
        PingDataCollector collector = (PingDataCollector) context.getAttribute(PingDataCollector.KEY);
        
        // get the data we want
        List<PingData> data = collector.getData(after, count);
        
        // find out how much data there is
        int dataSize = collector.getDataSize();
        
        ResponseBuilder rb = Response.ok(new PingDataCollection(dataSize, data));
        return rb.build();
    }
}
