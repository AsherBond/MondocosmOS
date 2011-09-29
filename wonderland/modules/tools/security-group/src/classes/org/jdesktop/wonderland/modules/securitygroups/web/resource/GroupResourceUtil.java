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

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.SecurityContext;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupDTO;
import org.jdesktop.wonderland.modules.securitygroups.common.MemberDTO;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupDAO;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupEntity;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.MemberEntity;

/**
 *
 * @author jkaplan
 */
public class GroupResourceUtil {
    /**
     * Convert a GroupEntity into a GroupDTO.
     * @param ge the entity to convert
     * @param members if true, include group members in the conversion
     * @return the converted GroupDTO
     */
    public static GroupDTO toDTO(GroupEntity ge, boolean members) {
        if (ge == null) {
            return null;
        }

        GroupDTO out = new GroupDTO(ge.getId());
        out.setMemberCount(ge.getMembers().size());
        
        if (members) {
            for (MemberEntity me : ge.getMembers()) {
                out.getMembers().add(new MemberDTO(me.getMemberId(),
                                                   me.isOwner()));
            }
        }

        return out;
    }

    /**
     * Convert a list of GroupEntities into a corresponding list of
     * GroupDTOs.  Calls toDTO() on each element of the list
     * @param groups the list of entities to convert
     * @param members if true, include group members in the conversion
     * @return the list of converted groups
     */
    public static List<GroupDTO> toDTOs(List<GroupEntity> groups, boolean members) {
        List<GroupDTO> out = new ArrayList<GroupDTO>(groups.size());
        for (GroupEntity ge : groups) {
            out.add(toDTO(ge, members));
        }

        return out;
    }

    /**
     * Convert a GroupDTO into an entity
     * @param dto the dto to convert
     * @return an entity corresponding to the given DTO
     */
    public static GroupEntity toEntity(GroupDTO dto) {
        GroupEntity out = new GroupEntity(dto.getId());
        for (MemberDTO m : dto.getMembers()) {
            MemberEntity me = new MemberEntity(out.getId(), m.getId());
            me.setOwner(m.isOwner());
            me.setGroup(out);
            out.getMembers().add(me);
        }

        return out;
    }
    
    /**
     * Return true if the given user is allowed to modify the given group. A
     * user can update the group if either there are no owners, or the user is
     * an owner of the group.
     *
     * @param groupId the name of the group to check
     * @return true if the user is an adminstrator, the group is null
     * (and therefore doesn't exist) or the user is an owner of the group
     */
    public static boolean canModify(String groupId, GroupDAO groups,
                             SecurityContext security)
    {
        // get the current values for the group
        GroupEntity group = groups.getGroup(groupId);

        // admin can always edit.  Anyone can create a group that doesn't
        // already exist
        if (security.isUserInRole("admin") || group == null) {
            return true;
        }

        // check if this user is an owner of the group or if the group
        // has no owners, in which case anyone can edit the group.
        boolean owners = false;
        String userId = security.getUserPrincipal().getName();

        for (MemberEntity m : group.getMembers()) {
            if (m.getMemberId().equals(userId) && m.isOwner()) {
                // user is an owner
                return true;
            }

            // check if the group has *any* owners
            if (m.isOwner()) {
                owners = true;
            }
        }

        // this member is not an owner -- return false unless there are no
        // other owners
        return !owners;
    }
}
