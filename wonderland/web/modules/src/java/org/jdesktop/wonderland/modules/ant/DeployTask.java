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
package org.jdesktop.wonderland.modules.ant;

import org.jdesktop.wonderland.common.modules.ModuleUploader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
public class DeployTask extends Task {
    private File module;
    private URL serverUrl;
    private String username;
    private String password;

    public void setModule(File module) {
        this.module = module;
    }
    
    public void setServerUrl(URL serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public void execute() throws BuildException {
        if (module == null) {
            throw new BuildException("module required");
        }
        if (serverUrl == null) {
            throw new BuildException("serverUrl required");
        }

        this.log("Deploying " + module.getName() + " to " + serverUrl);

        try {
            // create the uploader
            ModuleUploader mu = new ModuleUploader(serverUrl);

            // get the authentication service (if any)
            AuthenticationService as = getAuthentication();
            if (as != null) {
                mu.setAuthURL(as.getAuthenticationURL());
            }

            // upload the module
            mu.upload(module);
        } catch (IOException ioe) {
            throw new BuildException("Error uploading to " + serverUrl +
                                     ": " + ioe.getMessage(), ioe);
        } catch (AuthenticationException ae) {
            throw new BuildException("Error uploading to " + serverUrl +
                                     ": " + ae.getMessage(), ae);
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
}
