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

import com.sun.jersey.api.NotFoundException;
import java.util.List;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManager;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManagerFactory;
import org.jdesktop.wonderland.modules.securitysession.weblib.UserRecord;

/**
 *
 * @author jkaplan
 */
@Path("attributes")
public class AttributesResource {
    private static final String PREFIX = "userdetails.";
    private static final String NAME_PREFIX = PREFIX + "attribute.name=";
    private static final String VALUE_PREFIX = PREFIX + "attribute.value=";

    private static final Logger logger =
            Logger.getLogger(AttributesResource.class.getName());


    private final SessionManager sm = SessionManagerFactory.getSessionManager();

    @GET
    public Response get(@QueryParam("attribute_names") List<String> attrNames,
                        @QueryParam("subjectid") String subjectId)
    {
        return post(attrNames, subjectId);
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response post(@FormParam("attribute_names") List<String> attrNames,
                         @FormParam("subjectid") String subjectId)
    {
        UserRecord rec = sm.getByToken(subjectId);
        if (rec == null) {
            throw new NotFoundException("No such token " + subjectId);
        }

        StringBuffer res = new StringBuffer(PREFIX + "token.id=" + subjectId + "\n");

        Attributes attrs = rec.getAttributes();
        
        logger.fine("Found " + attrs.size() +
                    " attributes for " + rec.getUserId());

        NamingEnumeration attrEnum = attrs.getAll();
        try {
            while (attrEnum.hasMore()) {
                Attribute attr = (Attribute) attrEnum.next();
                if (attrNames == null || attrNames.isEmpty() ||
                    attrNames.contains(attr.getID()))
                {
                    res.append(NAME_PREFIX + attr.getID() + "\n");
                    
                    NamingEnumeration valEnum = attr.getAll();
                    while (valEnum.hasMore()) {
                        res.append(VALUE_PREFIX + valEnum.next() + "\n");
                    }
                }
            }
        } catch (NamingException ne) {
            throw new WebApplicationException(ne, 
                                        Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok(res.toString()).build();
    }
}
