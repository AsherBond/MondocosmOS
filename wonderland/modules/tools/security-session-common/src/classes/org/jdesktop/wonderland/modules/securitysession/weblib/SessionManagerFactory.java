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
package org.jdesktop.wonderland.modules.securitysession.weblib;

/**
 *
 * @author jkaplan
 */
public class SessionManagerFactory {
    private static final String SESSION_MANAGER_PROP = "session.manager.class";

    protected SessionManagerFactory() {
    }

    public static SessionManager getSessionManager() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SessionManager INSTANCE = createInstance();

        private static SessionManager createInstance() {
            String className = System.getProperty(SESSION_MANAGER_PROP);
            if (className == null) {
                throw new IllegalStateException("Property " + 
                        SESSION_MANAGER_PROP + " not set.");
            }

            try {
                Class<SessionManager> clazz =
                        (Class<SessionManager>) Class.forName(className);
                return clazz.newInstance();
            } catch (InstantiationException ie) {
                throw new IllegalStateException("Unable to create class " +
                                                className, ie);
            } catch (IllegalAccessException iae) {
                throw new IllegalStateException("Unable to create class " +
                                                className, iae);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException("Unable to find class " +
                                                className, cnfe);
            }
        }
    }
}
