/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.front.resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.jdesktop.wonderland.client.login.ServerDetails;
import org.jdesktop.wonderland.front.admin.ServerInfo;
import org.jdesktop.wonderland.utils.Constants;

/**
 * The ServletDetailsResource class is a Jersey RESTful service that returns
 * login details for this server.
 *
 * @author kaplanj
 */
@Path(value="/ServerDetails")
public class ServerDetailsResource {
    private static final Logger logger =
            Logger.getLogger(ServerDetailsResource.class.getName());

    @Context
    private UriInfo uriInfo;

    /**
     * Returns the details for this server
     */
    @GET
    public Response getServerDetails() {
        ServerDetails out;
        
        // if this is a request to the internal address, return the internal
        // host details
        String requestHost = uriInfo.getRequestUri().getHost();
        String externalHost = System.getProperty(Constants.WEBSERVER_HOST_PROP);
        String internalHost = System.getProperty(Constants.WEBSERVER_HOST_INTERNAL_PROP);
        
        if (!internalHost.equalsIgnoreCase(externalHost) &&
                requestHost.equalsIgnoreCase(internalHost)) 
        {
            out = ServerInfo.getInternalServerDetails().clone();
        } else {
            out = ServerInfo.getServerDetails().clone();
        }

        // replace the URL in the default with one based on the client's
        // request
        // XXX the server URL needs to be canonical and constant across
        // all clients, since it is shared in places like webdav URL. Make
        // sure this returns the canonical public URL for the server XXX
        //out.setServerURL(getServerURL());

        try {
            ResponseBuilder rb = Response.ok(out);
            return rb.build();
        } catch (Exception excp) {
            logger.log(Level.WARNING, "Error writing response", excp);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();            
        }
    }

    /**
     * Get the URL for this server
     * @return the server URL
     */
    protected String getServerURL() {
        UriBuilder b = uriInfo.getBaseUriBuilder();
        b.replacePath("");

        return b.build().toString();
    }
}
