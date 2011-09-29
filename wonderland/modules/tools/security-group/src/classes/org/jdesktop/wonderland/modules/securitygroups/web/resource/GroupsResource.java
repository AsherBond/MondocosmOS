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
package org.jdesktop.wonderland.modules.securitygroups.web.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupDTO;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupsDTO;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupDAO;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupEntity;

/**
 *
 * @author jkaplan
 */
@Path("/groups")
public class GroupsResource {
    @Context
    private SecurityContext security;

    @Context
    private ServletContext context;

    @Context
    private UriInfo uriInfo;

    @PersistenceUnit(unitName="WonderlandGroupPU")
    private EntityManagerFactory emf;

    @Path("{groupId}")
    public GroupResource getGroup(@PathParam("groupId") String groupId) {
        return new GroupResource(new GroupDAO(emf), uriInfo, groupId, context, security);
    }

    @GET
    @Produces({"text/plain", "application/xml", "application/json"})
    public Response get(@QueryParam("pattern") String patternParam,
                        @QueryParam("members") String membersParam,
                        @QueryParam("user") String userParam)
    {
        List<GroupEntity> res;
        GroupDAO groups = new GroupDAO(emf);

        // whether or not to include members
        boolean members = true;
        if (membersParam != null) {
            members = Boolean.parseBoolean(membersParam);
        }

        // fdin the group by user, pattern or all groups
        if (userParam != null) {
            // if a user was specified, find all that user's groups
            res = groups.findGroupsForMember(userParam);
        } else if (patternParam == null) {
            // if no user or pattern was specified, get all groups
            res = groups.getGroups();
        } else {
            // if a pattern was specified, search by pattern
            try {
                patternParam = URLDecoder.decode(patternParam, "UTF-8");
                patternParam = patternParam.replace('*', '%');
                res = groups.findGroups(patternParam);
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }

        // convert to DTOs
        List<GroupDTO> out = GroupResourceUtil.toDTOs(res, members);

        // go through each group and set the editable bit
        for (GroupDTO g : out) {
            g.setEditable(GroupResourceUtil.canModify(g.getId(), groups, security));
        }

        return Response.ok(new GroupsDTO(out)).build();
    }
}
