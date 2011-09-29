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
package org.jdesktop.wonderland.modules.security.server.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.security.common.Principal;

/**
 * A factory class for resolving a username into a set of principals
 * @author jkaplan
 */
public class UserPrincipals {
    private static final String RESOLVER_CLASS_PROP =
            UserPrincipals.class.getName() + ".UserPrincipalResolver";
    private static final String DEFAULT_RESOLVER_CLASS =
            "org.jdesktop.wonderland.modules.securitygroups.server.WebServiceUserPrincipalResolver";

    /**
     * Get all principals (user and group) for the given user name. The blocking
     * boolean controls whether this method is allowed to block.  If blocking
     * is true, this method will block until principals are available for the
     * given user.  If blocking is set to false, the method must return
     * immediately, but may return null if no principals are available for
     * the given user.
     * @param username the username to resolve
     * @param blocking true if the method may block, or false if not
     * @return the principals associated with the given user.  The minimal
     * set is a single user principal with this user's username.  If blocking
     * is set to false, null indicates that the values can't be read.
     */
    public static Set<Principal> getUserPrincipals(String username, boolean blocking) {
        return SingletonHolder.INSTANCE.getPrincipals(username, blocking);
    }

    /**
     * Create a resolver by class name
     * @return the created resolver
     */
    private static UserPrincipalResolver createResolver() {
        String clazz = System.getProperty(RESOLVER_CLASS_PROP,
                                          DEFAULT_RESOLVER_CLASS);
        try {
            Class c = Class.forName(clazz);
            Method getInstance = c.getMethod("getInstance");
            return (UserPrincipalResolver) getInstance.invoke(null);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Error invoking getInstance() on " +
                                            clazz, ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Error invoking getInstance() on " +
                                            clazz, ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("Error invoking getInstance() on " +
                                            clazz, ex);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("No such method getInstance() on " +
                                            clazz, ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException("Security error invoking " +
                                            "getInstance() on " + clazz, ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Class not found " + clazz, ex);
        }
    }

    private static final class SingletonHolder {
        private static final UserPrincipalResolver INSTANCE = createResolver();
    }
}
