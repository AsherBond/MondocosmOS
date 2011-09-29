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
package org.jdesktop.wonderland.modules.securitysession.noauth.web.identity;

import com.sun.jersey.api.Responses;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionLoginException;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManager;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManagerFactory;
import org.jdesktop.wonderland.modules.securitysession.weblib.UserRecord;

/**
 *
 * @author jkaplan
 */
@Path("noauth")
public class NoAuthResource {
    private static final Logger logger =
            Logger.getLogger(NoAuthResource.class.getName());

    private final SessionManager sm = SessionManagerFactory.getSessionManager();

    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response post(@FormParam("username") String username,
                         @FormParam("fullname") String fullname,
                         @FormParam("email") String email)
    {
        if (username == null) {
            return Responses.notAcceptable().build();
        }

        // decode arguments
        try {
            username = URLDecoder.decode(username, "UTF-8");
            
            if (fullname != null) {
                fullname = URLDecoder.decode(fullname, "UTF-8");
            } else {
                fullname = username;
            }

            if (email != null) {
                email = URLDecoder.decode(email, "UTF-8");
            }
        } catch (UnsupportedEncodingException uee) {
            logger.log(Level.WARNING, "Decoding error", uee);
            throw new WebApplicationException(uee,
                                        Response.Status.INTERNAL_SERVER_ERROR);
        }


        // get or create the record
        UserRecord rec = sm.get(username);
        if (rec == null) {
            // no record exists for this user -- login in
            try {
                rec = sm.login(username, fullname, email);
            } catch (SessionLoginException sle) {
                logger.log(Level.WARNING, "Login error", sle);
                throw new WebApplicationException(sle,
                                         Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        
        // return the token
        String res = "string=" + rec.getToken();
        return Response.ok(res).build();
    }
}
