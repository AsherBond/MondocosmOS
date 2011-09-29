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
package org.jdesktop.wonderland.modules.securitysession.web.identity;

import com.sun.jersey.api.Responses;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManager;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManagerFactory;

/**
 *
 * @author jkaplan
 */
@Path("logout")
public class LogoutResource {
    private SessionManager sm = SessionManagerFactory.getSessionManager();

    @GET
    public Response get(@QueryParam("subjectid") String subjectId) {
        return post(subjectId);
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response post(@FormParam("subjectid") String subjectId) {
        if (subjectId == null) {
            return Responses.notAcceptable().build();
        }

        sm.logout(subjectId);
        return Response.ok().build();
    }
}
