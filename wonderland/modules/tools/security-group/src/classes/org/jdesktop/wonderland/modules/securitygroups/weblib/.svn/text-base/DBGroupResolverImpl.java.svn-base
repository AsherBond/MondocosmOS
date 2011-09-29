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
package org.jdesktop.wonderland.modules.securitygroups.weblib;

import org.jdesktop.wonderland.modules.securitygroups.weblib.db.*;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jdesktop.wonderland.modules.security.weblib.serverauthmodule.GroupResolver;

/**
 *
 * @author jkaplan
 */
public class DBGroupResolverImpl implements GroupResolver {
    private static final String DEFAULT_GROUP_OPT = "default.group";
    private static final String DEFAULT_GROUP_DEFAULT = "users";

    private EntityManagerFactory emf;
    private String defaultGroup;

    public DBGroupResolverImpl() {
        // create the entity manager factory for querying the database for
        // groups
        emf = Persistence.createEntityManagerFactory("WonderlandGroupPU");
    }

    public void initialize(Map opts) {
        // get the default group
        defaultGroup = (String) opts.get(DEFAULT_GROUP_OPT);
        if (defaultGroup == null) {
            defaultGroup = DEFAULT_GROUP_DEFAULT;
        }
    }

    public String[] getGroupsForUser(String userId) {
        // get the groups associated with this user
        GroupDAO dao = new GroupDAO(emf);
        List<GroupEntity> groups = dao.findGroupsForMember(userId);

        String[] out = new String[groups.size() + 1];
        out[0] = defaultGroup;
        for (int i = 0; i < groups.size(); i++) {
            out[i + 1] = groups.get(i).getId();
        }

        return out;
    }
}
