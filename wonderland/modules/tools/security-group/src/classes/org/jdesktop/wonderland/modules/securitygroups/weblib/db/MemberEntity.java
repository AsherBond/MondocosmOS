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
package org.jdesktop.wonderland.modules.securitygroups.weblib.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author jkaplan
 */
@Entity
@IdClass(MemberEntityId.class)
@NamedQueries({
    @NamedQuery(name="allMembers",
                query="SELECT m FROM MemberEntity m"),
    @NamedQuery(name="groupsForMember",
                query="SELECT m.group FROM MemberEntity m " +
                      "WHERE m.memberId = :memberId")})
public class MemberEntity implements Serializable {
    private String groupId;
    private String memberId;
    private GroupEntity group;
    private boolean owner;

    public MemberEntity() {
    }

    public MemberEntity(String groupId, String memberId) {
        this.groupId = groupId;
        this.memberId = memberId;
    }

    @Id
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Id
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    @JoinColumn(name="groupcolumn")
    @ManyToOne
    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MemberEntity other = (MemberEntity) obj;
        if ((this.groupId == null) ? (other.groupId != null) : !this.groupId.equals(other.groupId)) {
            return false;
        }
        if ((this.memberId == null) ? (other.memberId != null) : !this.memberId.equals(other.memberId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.groupId != null ? this.groupId.hashCode() : 0);
        hash = 59 * hash + (this.memberId != null ? this.memberId.hashCode() : 0);
        return hash;
    }
}
