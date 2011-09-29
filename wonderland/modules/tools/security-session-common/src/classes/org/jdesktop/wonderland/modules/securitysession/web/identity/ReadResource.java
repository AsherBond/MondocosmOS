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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupDAO;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupEntity;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.MemberEntity;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManager;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManagerFactory;
import org.jdesktop.wonderland.modules.securitysession.weblib.UserRecord;

/**
 *
 * @author jkaplan
 */
@Path("read")
public class ReadResource {
private static final String PREFIX = "identitydetails.";
    private static final String NAME_PREFIX = PREFIX + "attribute.name=";
    private static final String VALUE_PREFIX = PREFIX + "attribute.value=";

    private static final Logger logger =
            Logger.getLogger(ReadResource.class.getName());


    private final SessionManager sm = SessionManagerFactory.getSessionManager();

    @PersistenceUnit(unitName="WonderlandGroupPU")
    private EntityManagerFactory emf;

    @GET
    public Response get(@QueryParam("name") String name,
                        @QueryParam("attribute_names") List<String> attrNames,
                        @QueryParam("admin") String adminId)
    {
        return post(name, attrNames, adminId);
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response post(@FormParam("name") String name,
                         @FormParam("attribute_names") List<String> attrNames,
                         @FormParam("admin") String adminId)
    {
        UserRecord admin = sm.getByToken(adminId);
        if (admin == null) {
            throw new NotFoundException("Invalid token " + adminId);
        }

        // check that the admin user is actually in the admin group
        if (!isAdmin(admin.getUserId())) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("Only administrators can read")
                           .type("text/plain").build();
        }

        // now look up the user
        UserRecord rec = sm.get(name);
        if (rec == null) {
            throw new NotFoundException("Unknown name " + name);
        }

        StringBuffer res = new StringBuffer(PREFIX + "name=" + name + "\n");
        res.append(PREFIX + "type=user\n");
        res.append(PREFIX + "attribute=\n");

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

    private boolean isAdmin(String userId) {
        GroupDAO groups = new GroupDAO(emf);

        GroupEntity adminGroup = groups.getGroup("admin");
        MemberEntity test = new MemberEntity(adminGroup.getId(), userId);

        // if the admin group is null, what should we do?  In this case, we
        // just return null
        return adminGroup == null ||
               adminGroup.getMembers().contains(test);
    }
}
