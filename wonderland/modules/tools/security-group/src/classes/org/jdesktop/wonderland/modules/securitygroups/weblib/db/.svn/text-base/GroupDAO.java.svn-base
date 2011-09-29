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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

/**
 * Data access object for group information.  This object abstracts access to
 * the underlying database of groups.
 * @author jkaplan
 */
public class GroupDAO {
    private EntityManagerFactory emf;

    /**
     * Create a new group data access object
     * @param emf the entity manager factory to access the group data
     */
    public GroupDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Get a group by name
     * @param groupName the name of the group to get
     * @return the group with the given name, or null if no group exists
     * with that name
     */
    public GroupEntity getGroup(String groupName) {
        EntityManager em = emf.createEntityManager();
        return em.find(GroupEntity.class, groupName);
    }

    /**
     * Determine if a given group exists
     * @param groupName the name of the group to test
     * @return true if a group with the given name exists, or false if not
     */
    public boolean hasGroup(String groupName) {
        EntityManager em = emf.createEntityManager();
        return em.contains(new GroupEntity(groupName));
    }

    /**
     * Get all the groups on this server
     * @return a list of all groups
     */
    public List<GroupEntity> getGroups() {
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("getGroups");
        return (List<GroupEntity>) q.getResultList();
    }

    /**
     * Find all groups that match the given search parameters
     * @param groupId the group to find, using a JPA LIKE query
     * @return the groups that match the given parameters, or an empty list
     * if no groups match
     */
    public List<GroupEntity> findGroups(String groupId) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("findGroups").setParameter("id", groupId);
        return (List<GroupEntity>) q.getResultList();
    }

    /**
     * Get all groups associated with the given member Id
     * @param memberId the member id to search for
     * @return the groups the given member belogs to, or an empty list if
     * the member does not belong to any groups
     */
    public List<GroupEntity> findGroupsForMember(String memberId) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("groupsForMember").setParameter("memberId", memberId);

        return (List<GroupEntity>) q.getResultList();
    }

    /**
     * Create or update a group.  This will persist the given version of the
     * group. The return value of this method is the set of all members that
     * are affected by this change.  This includes both members that are newly
     * added to the group and members that were formerly in the group but
     * are not any more.
     * @param group the updated
     * @return the set of affected users
     */
    public Set<MemberEntity> updateGroup(final GroupEntity group) {
        try {
            return runInTransaction(new EMCallable<Set<MemberEntity>>() {
                public Set<MemberEntity> call(EntityManager em) throws Exception {
                    // find the current value (if any) for this entity
                    GroupEntity cur = em.find(GroupEntity.class, group.getId());
                    if (cur == null) {
                        // this is a new entity, just add it and return
                        em.persist(group);
                        return group.getMembers();
                    }

                    // calculate the set of changed members.  Start with the
                    // list of all members in the new group.  We will then
                    // take away all unchanged members, and also add in any
                    // removed members from the original group
                    Set<MemberEntity> out = new LinkedHashSet<MemberEntity>();
                    out.addAll(group.getMembers());

                    // go through each member of the previous set of members.
                    // If a member is in both the previous and the new set,
                    // remove them from the change list.  If a member is in
                    // the old set but not the new set, add them to the change
                    // set since they were removed
                    for (MemberEntity me : cur.getMembers()) {
                        if (!out.remove(me)) {
                            // remove the member from the persistence
                            em.remove(me);

                            // add the removed member to the changed set
                            out.add(me);
                        }
                    }

                    // this is an existing group -- merge in the new data and
                    // return the updated group
                    em.merge(group);
                    return out;
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Remove an existing group.  Return the set of changed users (all the
     * users in the changed group).
     * @param groupName the name of the group to remove
     * @return the set of changed members, or an empty set if the group
     * doesn't exist
     */
    public Set<MemberEntity> removeGroup(final String groupName) {
        try {
            return runInTransaction(new EMCallable<Set<MemberEntity>>() {
                public Set<MemberEntity> call(EntityManager em) throws Exception {
                    GroupEntity ge = em.find(GroupEntity.class, groupName);
                    if (ge != null) {
                        em.remove(ge);
                        return ge.getMembers();
                    }

                    return Collections.emptySet();
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    

    protected <T> T runInTransaction(EMCallable<T> call) throws Exception {
        T out = null;
        EntityManager em = emf.createEntityManager();

        UserTransaction utx = getUtx();
        try {
            utx.begin();
            em.joinTransaction();
            out = call.call(em);
            utx.commit();
        } catch (Exception e) {
            utx.rollback();
        } finally {
            em.close();
        }

        return out;
    }

    protected interface EMCallable<T> {
        public T call(EntityManager em) throws Exception;
    }

    private static UserTransaction getUtx() {
        try {
            InitialContext ic = new InitialContext();
            return (UserTransaction) ic.lookup("java:comp/UserTransaction");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
}
