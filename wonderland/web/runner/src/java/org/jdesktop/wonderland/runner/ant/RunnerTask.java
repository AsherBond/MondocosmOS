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
package org.jdesktop.wonderland.runner.ant;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerDetails;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.login.AuthenticationException;
import org.jdesktop.wonderland.common.login.AuthenticationInfo;
import org.jdesktop.wonderland.common.login.AuthenticationManager;
import org.jdesktop.wonderland.common.login.AuthenticationService;

/**
 * @author jkaplan
 */
public class RunnerTask extends Task {
    private URL serverUrl;
    private String action = "restart";
    private String service = "all";
    private String username;
    private String password;

    private Set<Service> services = new HashSet<Service>();
    
    // url for restarting the server
    private static final String SERVICES_URL =
            "wonderland-web-runner/services/runner";
 
    public void setServerUrl(URL serverUrl) {
        this.serverUrl = serverUrl;
    }
 
    public void setAction(String action) {
        this.action = action;
    }
    
    public void setService(String service) {
        this.service = service;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void add(Service service) {
        if (service.name == null || service.name.trim().length() == 0) {
            throw new BuildException("service requires a name");
        }
        services.add(service);
    }
    
    @Override
    public void execute() throws BuildException {
        if (serverUrl == null) {
            throw new BuildException("serverUrl required");
        }
        
        try {
            if (!services.isEmpty()) {
                for (Service s : services) {
                    execute(s.name, action);
                }
            } else {
                execute(service, action);
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }
    } 
    
    protected void execute(String service, String action)
        throws IOException, BuildException
    {
        URL restartUrl = new URL(serverUrl, SERVICES_URL + "/" +
                                 service + "/" + action);
        HttpURLConnection uc = (HttpURLConnection) restartUrl.openConnection();
        
        // tell the server not to redirect us to the login page
        uc.setRequestProperty("Redirect", "false");

        // if there is authentication, make sure to secure the connection
        try {
            AuthenticationService as = getAuthentication();
            if (as != null) {
                as.secureURLConnection(uc);
            }
        } catch (AuthenticationException ae) {
            throw new BuildException("Error requesting restart from " +
                                     restartUrl + ": " + ae.getMessage(), ae);
        }

        // now connect to the server
        uc.connect();
                
        int response = uc.getResponseCode();
        if (response != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error " + response + " restarting server: " + 
                                  uc.getResponseMessage());
        }
    }

    protected AuthenticationService getAuthentication()
        throws AuthenticationException, IOException
    {
        AuthenticationService out = null;

        // if the username is not null, set up authentication
        if (username != null && username.length() > 0 &&
                password != null && password.length() > 0)
        {
            ServerSessionManager ssm = LoginManager.getSessionManager(serverUrl.toString());
            ServerDetails details = ssm.getDetails();

            AuthenticationInfo info = details.getAuthInfo().clone();

            // if the type is EITHER, we choose to login with authentication,
            // since guest logins won't be allowed without authentication
            if (info.getType() == AuthenticationInfo.Type.EITHER) {
                info.setType(AuthenticationInfo.Type.WEB_SERVICE);
            }

            if (info.getType() == AuthenticationInfo.Type.WEB_SERVICE) {
                out = AuthenticationManager.login(info, username, password);
            }
        }

        return out;
    }

    public static class Service {
        private String name;
    
        public void setName(String name) {
            this.name = name;
        }
    }
}
