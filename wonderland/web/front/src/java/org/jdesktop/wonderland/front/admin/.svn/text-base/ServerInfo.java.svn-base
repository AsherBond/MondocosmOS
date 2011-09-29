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
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.front.admin;

import java.util.ArrayList;
import org.jdesktop.wonderland.client.login.DarkstarServer;
import org.jdesktop.wonderland.client.login.ServerDetails;
import org.jdesktop.wonderland.common.login.AuthenticationInfo;
import org.jdesktop.wonderland.utils.Constants;

/**
 * Some basic information about the server
 * @author jkaplan
 */
public class ServerInfo {
    static {
       ServerDetailsHolder.INSTANCE.setServerURL(getServerURL());
       ServerDetailsHolder.INTERNAL_INSTANCE.setServerURL(getInternalServerURL());

       // default to no authentication
       AuthenticationInfo authInfo =
               new AuthenticationInfo(AuthenticationInfo.Type.NONE, null);
       ServerDetailsHolder.INSTANCE.setAuthInfo(authInfo);
       ServerDetailsHolder.INTERNAL_INSTANCE.setAuthInfo(authInfo);

       // set the starting timestamp
       ServerDetailsHolder.INSTANCE.setTimeStamp(System.currentTimeMillis());
       ServerDetailsHolder.INTERNAL_INSTANCE.setTimeStamp(System.currentTimeMillis());

       // Darkstar servers will be filled in later as they are
       // created
       ServerDetailsHolder.INSTANCE.setDarkstarServers(new ArrayList<DarkstarServer>());
       ServerDetailsHolder.INTERNAL_INSTANCE.setDarkstarServers(new ArrayList<DarkstarServer>());
    }

    /**
     * Get the base server URL
     * @return the base server URL for this server
     */
    public static String getServerURL() {
        return System.getProperty(Constants.WEBSERVER_URL_PROP);
    }

    /**
     * Get the base internal server URL
     * @return the base internal URL for this server
     */
    public static String getInternalServerURL() {
        return System.getProperty(Constants.WEBSERVER_URL_INTERNAL_PROP);
    }

    /**
     * Get the server details object that will be sent to clients.
     * Modifying this object modifies the values that are sent to all
     * clients.
     * @return the serverDetails object
     */
    public static ServerDetails getServerDetails() {
        return ServerDetailsHolder.INSTANCE;
    }

    /**
     * Get the server details object that will be sent to clients connecting
     * from an internal address.
     * Modifying this object modifies the values that are sent to all
     * clients.
     * @return the serverDetails object
     */
    public static ServerDetails getInternalServerDetails() {
        return ServerDetailsHolder.INTERNAL_INSTANCE;
    }

    private static final class ServerDetailsHolder {
        private static final ServerDetails INSTANCE = new ServerDetails();
        private static final ServerDetails INTERNAL_INSTANCE = new ServerDetails();
    }
}
