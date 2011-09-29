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
package org.jdesktop.wonderland.modules.sasxremwin.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporterFactory;
import org.jdesktop.wonderland.modules.sas.provider.SasProvider;
import org.jdesktop.wonderland.modules.sas.provider.SasProviderConnection;
import org.jdesktop.wonderland.modules.sas.provider.SasProviderConnectionListener;
import org.jdesktop.wonderland.modules.sas.provider.SasProviderSession;
import org.jdesktop.wonderland.modules.xremwin.client.AppXrwMaster;
import org.jdesktop.wonderland.common.messages.MessageID;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.logging.Level;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.common.login.CredentialManager;

/**
 * The main logic for the SAS Xremwin provider client.
 *
 * @author deronj
 */
@ExperimentalAPI
public class SasXrwProviderMain 
    implements SasProviderConnectionListener, AppXrwMaster.ExitListener {

    static final Logger logger = Logger.getLogger(SasXrwProviderMain.class.getName());

    /** The property for enabling user-specified app commands */
    private static final String ENABLE_USER_COMMANDS_PROP =
            SasXrwProviderMain.class.getSimpleName() + ".enable.user.commands";
    /** The URL to map app names to user commands */
    private static final String COMMAND_URL_PROP =
            SasXrwProviderMain.class.getSimpleName() + ".command.url";
    /** The default URL for user commands */
    private static final String COMMAND_URL_DEFAULT =
            "/xapps-config/wonderland-xapps-config/browse?action=check&app=";

    /** The property to set with the username */
    private static final String USERNAME_PROP = "sas.user";
    /** The default username */
    private static final String USERNAME_DEFAULT = "sasxprovider";
    /** The property to set with the password file location */
    private static final String PASSWORD_FILE_PROP = "sas.password.file";

    /** The URL of the Wonderland server */
    private final String serverUrl;

    /** Whether user commands are allowed */
    private final boolean userCommands;

    /** The command URL to query */
    private final String commandURL;

    /** The session associated with this provider. */
    private SasProviderSession session;

    /** An entry which holds information needed to notify the SasServer of app exit. */
    private class AppInfo {
        private SasProviderConnection connection;
        private MessageID launchMessageID;
        private AppInfo (SasProviderConnection connection, MessageID launchMessageID) {
            this.connection = connection;
            this.launchMessageID = launchMessageID;
        }
    }

    /** Information about the apps which are running in this provider. */
    private final HashMap<AppXrwMaster,AppInfo> runningAppInfos = new HashMap<AppXrwMaster,AppInfo>();

    /** The xremwin-specific provider object. */
    private static SasXrwProvider provider;

    // Register the sas provider shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(new Thread("SasXrw Shutdown Hook") {
            @Override
            public void run() { 
                if (provider != null) {
                    provider.cleanup(); 
                }
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SasXrwProviderMain providerMain = new SasXrwProviderMain();
    }

    private SasXrwProviderMain () {

        checkPlatform();

        // parse username from the system property
        String username = System.getProperty(USERNAME_PROP, USERNAME_DEFAULT);

        // parse the password file from the system property.  This may be
        // null if no password file is specified (for insecure deployments)
        String passwordFileName = System.getProperty(PASSWORD_FILE_PROP);
        File passwordFile = null;
        if (passwordFileName != null && passwordFileName.trim().length() > 0) {
            passwordFile = new File(passwordFileName);
        }

        // read the server URL property
        serverUrl = System.getProperty("wonderland.web.server.url", "http://localhost:8080");
        logger.warning("Connecting to server " + serverUrl);

        // Read the userCommands property.  The default is false unless the
        // property is specified and is equal to "true"
        userCommands = Boolean.parseBoolean(System.getProperty(ENABLE_USER_COMMANDS_PROP));

        // Read the commandURL property
        commandURL = System.getProperty(COMMAND_URL_PROP, COMMAND_URL_DEFAULT);

        try {
            provider = new SasXrwProvider(username, passwordFile, serverUrl, this, this);
        } catch (Exception ex) {
            logger.severe("Exception " + ex);
            logger.severe("Cannot connect to server " + serverUrl);
            System.exit(1);
        }        
    }

    /**
     * SAS can only run on a Unix platform. Exit if this is not the case.
     */
    private void checkPlatform() {
        String osName = System.getProperty("os.name");
        if (!"Linux".equals(osName) &&
            !"SunOS".equals(osName)) {
            logger.severe("SasXrwProviderMain cannot run on platform " + osName);
            logger.severe("Program terminated.");
            System.exit(1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSession (SasProviderSession session) {
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    public String launch (String appName, String command, SasProviderConnection connection, 
                          MessageID launchMessageID, CellID cellID) {
        AppXrwMaster app = null;
        
        // resolve the command based on the app name. A command value of null
        // indicates that the given appName could not be resolved
        command = resolveCommand(appName, command);
        if (command == null) {
            return null;
        }
        logger.warning("Resolved command = " + command);

        try {
            app = new AppXrwMaster(appName, command, cellID, null,
                                   ProcessReporterFactory.getFactory().create(appName), session, true);
        } catch (InstantiationException ex) {
            return null;
        }

        app.setExitListener(this);

        synchronized (runningAppInfos) {
            runningAppInfos.put(app, new AppInfo(connection, launchMessageID));
        }

        // Now it is time to enable the master client loop
        app.getClient().enable();

        return app.getConnectionInfo().toString();
    }


    /**
     * Called when an app exits.
     */
    public void appExitted (AppXrwMaster app) {
        logger.warning("App Exitted: " + app.getName());
        synchronized (runningAppInfos) {
            AppInfo appInfo = runningAppInfos.get(app);
            if (appInfo != null) {
                runningAppInfos.remove(app);

                // Don't send the app exitted message if the app base is no longer running.
                // This happens when a SAS Provider JVM shutdown hook has been called. If 
                // a shutdown of the SAS provider has occurred it will be restarted by the 
                // web server. But if we treat this as an app exit condition then the server 
                // cells for the apps will be deleted and their apps won't get restarted on 
                // the new SAS provider.
                if (App2D.isAppBaseRunning()) {
                    appInfo.connection.appExitted(appInfo.launchMessageID, app.getExitValue());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void appStop (SasProviderConnection connection, MessageID launchMessageID) {
        synchronized (runningAppInfos) {
            for (AppXrwMaster app : runningAppInfos.keySet()) {
                AppInfo appInfo = runningAppInfos.get(app);
                if (appInfo.connection == connection && appInfo.launchMessageID.equals(launchMessageID)) {
                    runningAppInfos.remove(app);
                    app.stop();
                }
            }
        }
    }

    /**
     * Resolve an app name into a command using a web service.
     * @param appName the app name to resolve
     * @param command the command the user specified (may be null)
     * @return the command to execute for the given app, or null
     * to return an error
     */
    protected String resolveCommand(String appName, String command) {
        // if the user specified a command, check if that command is
        // allowed
        if (command != null) {
            if (userCommands) {
                return command;
            } else {
                logger.warning("User-specified command " + command + " for " +
                               appName + " when user commands are disabled. " +
                               "Set " + ENABLE_USER_COMMANDS_PROP + " to " +
                               "enable user commands.");
                return null;
            }
        }

        // if we get here, it means the user did not specify a command.  In
        // that case, use the web service to try to find the command
        try {
            // construct the service request URL by adding the server
            // URL, the command URL and the app name (URL encoded) together.
            URL url = new URL(new URL(serverUrl), commandURL +
                              URLEncoder.encode(appName, "UTF-8"));
            URLConnection uc = url.openConnection();

            // if this was an HTTP URL connection, check the return value
            if (uc instanceof HttpURLConnection) {
                HttpURLConnection huc = (HttpURLConnection) uc;

                // don't redirect to the login page if there is a login failure
                huc.setRequestProperty("Redirect", "false");

                // secure the connection with the credentials for our session
                // (we can only do this for an http connection)
                CredentialManager cm = session.getSessionManager().getCredentialManager();
                cm.secureURLConnection(huc);

                if (huc.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    logger.warning("Connection to " + url + " returns " +
                            huc.getResponseCode() + " : " +
                            huc.getResponseMessage());
                    return null;
                }
            }

            // read the command name from the web service
            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(uc.getInputStream()));
            return br.readLine();
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error resolving command for " + appName,
                       ioe);
            return null;
        }
    }
}

