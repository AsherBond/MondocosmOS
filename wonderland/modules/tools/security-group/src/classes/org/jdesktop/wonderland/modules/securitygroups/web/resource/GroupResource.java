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

import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupDTO;
import org.jdesktop.wonderland.modules.securitygroups.web.GroupContextListener;
import org.jdesktop.wonderland.modules.securitygroups.web.SecurityCacheConnection;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupDAO;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.MemberEntity;


/**
 *
 * @author jkaplan
 */
public class GroupResource {
    private SecurityContext security;
    private ServletContext context;
    private GroupDAO groups;
    private UriInfo uriInfo;
    private String groupId;

    public GroupResource(GroupDAO groups, UriInfo uriInfo, String groupId, 
                         ServletContext context, SecurityContext security)
    {
        this.groups = groups;
        this.uriInfo = uriInfo;
        this.groupId = groupId;
        this.context = context;
        this.security = security;
    }

    @GET
    @Produces({"text/plain", "application/xml", "application/json"})
    public Response get() {
        GroupDTO out = GroupResourceUtil.toDTO(groups.getGroup(groupId), true);
        if (out == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // set whether or not the requesting user can edit the group
        out.setEditable(canModify());

        // return the encoded group
        return Response.ok(out).build();
    }
    
    @PUT
    @Consumes({"application/xml", "application/json"})
    public Response put(GroupDTO group) {
        if (group.getId() == null || !group.getId().equals(groupId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // only group owners are allowed to update exiting groups
        if (!canModify()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // make the update, which will return to us the set of users that
        // changed
        Set<MemberEntity> members = groups.updateGroup(GroupResourceUtil.toEntity(group));
        
        // notify the Darkstar server to invalidate the given members
        sendInvalidateMessage(members);

        // send an OK response
        return Response.ok().build();
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    public Response post(GroupDTO group) {
        return put(group);
    }

    /**
     * This is used by the prototype javascript library to handle delete
     * requests from a browser by encoding the method in an argument to
     * a post.
     * @param method the method to execute
     * @return the result of executing the method
     */
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response postForm(@FormParam("_method") String method) {
        if (method == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (method.equalsIgnoreCase("delete")) {
            return delete();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    public Response delete() {
        // only group owners are allowed to update exiting groups
        if (!canModify()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // remove the group, returning the set of users formerly in the
        // group
        Set<MemberEntity> members = groups.removeGroup(groupId);

        // notify the Darkstar server to invalidate the given members
        sendInvalidateMessage(members);

        // send an OK response
        return Response.ok().build();
    }

    private boolean canModify() {
        return GroupResourceUtil.canModify(groupId, groups, security);
    }

    private void sendInvalidateMessage(Set<MemberEntity> members) {
        // get the connection from the servlet context
        SecurityCacheConnection conn = (SecurityCacheConnection)
                context.getAttribute(GroupContextListener.SECURITY_CACHE_CONN_ATTR);
        if (conn == null) {
            return;
        }

        // convert the members into a set of usernames
        Set<String> usernames = new LinkedHashSet<String>(members.size());
        for (MemberEntity member : members) {
            usernames.add(member.getMemberId());
        }

        // send the message
        conn.invalidateGroup(groupId, usernames);
    }
}
