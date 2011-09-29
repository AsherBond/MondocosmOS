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
package org.jdesktop.wonderland.modules.securitygroups.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLogin.DarkstarServerListener;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLoginFactory;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupDAO;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.GroupEntity;
import org.jdesktop.wonderland.modules.securitygroups.weblib.db.MemberEntity;
        
/**
 * Manage the installation and removal of the PingDataListener
 * @author jkaplan
 */
public class GroupContextListener 
        implements ServletContextListener, DarkstarServerListener
{
    /** logger */
    private static final Logger logger =
            Logger.getLogger(GroupContextListener.class.getName());

    /** the key to identify the connection in the servlet context */
    public static final String SECURITY_CACHE_CONN_ATTR =
            "__securityCacheConnection";

    /** the key to identify the session in the servlet context */
    public static final String SESSION_ATTR =
            "__securityCacheSession";

    /** the group database persistence unit (injected automatically) */
    @PersistenceUnit
    private EntityManagerFactory emf;

    /** the servlet context */
    private ServletContext context;

    /** the registration with the UI */
    private AdminRegistration ar;

    /**
     * called when the context is initialized
     * @param sce the event with the context information
     */
    public void contextInitialized(ServletContextEvent sce) {
        this.context = sce.getServletContext();

        // makse sure we have at least the admin group
        createInitialGroups();

        // add ourselves as a listener for when the Darkstar server changes
        DarkstarWebLoginFactory.getInstance().addDarkstarServerListener(this);

        // register with the UI
        ar = new AdminRegistration("Manage Groups",
                                   "/security-groups/security-groups/editor");
        ar.setFilter(AdminRegistration.ADMIN_FILTER);
        AdminRegistration.register(ar, this.context);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // remove the Darkstar server listener
        DarkstarWebLoginFactory.getInstance().removeDarkstarServerListener(this);

        // unregister from the UI
        if (ar != null) {
            AdminRegistration.unregister(ar, sce.getServletContext());
        }

        // log out of any connected sessions
        WonderlandSession session = (WonderlandSession)
                context.getAttribute(SESSION_ATTR);
        if (session != null) {
            session.logout();
        }
    }

    private void createInitialGroups() {
        GroupDAO groups = new GroupDAO(emf);
        GroupEntity adminGroup = groups.getGroup("admin");
        if (adminGroup == null) {
            adminGroup = new GroupEntity("admin");

            MemberEntity adminMember = new MemberEntity("admin", "admin");
            adminMember.setOwner(true);
            adminMember.setGroup(adminGroup);
            adminGroup.getMembers().add(adminMember);

            MemberEntity darkstarMember = new MemberEntity("admin", "darkstar");
            darkstarMember.setGroup(adminGroup);
            adminGroup.getMembers().add(darkstarMember);

            MemberEntity webserverMember = new MemberEntity("admin", "webserver");
            webserverMember.setGroup(adminGroup);
            adminGroup.getMembers().add(webserverMember);

            MemberEntity sasMember = new MemberEntity("admin", "sasxprovider");
            sasMember.setGroup(adminGroup);
            adminGroup.getMembers().add(sasMember);

            groups.updateGroup(adminGroup);

            logger.warning("Created initial group " + adminGroup.getId() +
                           " with " + adminGroup.getMembers().size() +
                           " members.");
        }
    }

    public void serverStarted(DarkstarRunner runner,
                              ServerSessionManager mgr)
    {
        try {
            WonderlandSession session = mgr.createSession();
            context.setAttribute(SESSION_ATTR, session);

            SecurityCacheConnection conn = new SecurityCacheConnection();
            session.connect(conn);
            context.setAttribute(SECURITY_CACHE_CONN_ATTR, conn);
        } catch (ConnectionFailureException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (LoginFailureException ex) {
            logger.log(Level.WARNING, "Login failed");
        }
    }

    public void serverStopped(DarkstarRunner arg0) {
        context.removeAttribute(SESSION_ATTR);
        context.removeAttribute(SECURITY_CACHE_CONN_ATTR);
    }
}
