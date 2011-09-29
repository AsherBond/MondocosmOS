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
package org.jdesktop.wonderland.modules.xappsconfig.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLogin.DarkstarServerListener;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLoginFactory;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppRegistryItem;
import org.jdesktop.wonderland.modules.xappsconfig.web.XAppsWebConfigConnection;

/**
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class XAppsServlet extends HttpServlet implements ServletContextListener, DarkstarServerListener {

    private static final Logger logger = Logger.getLogger(XAppsServlet.class.getName());
    private AdminRegistration ar = null;
    private ServletContext context = null;

    /** the key to identify the connection in the servlet context */
    public static final String XAPPS_CONN_ATTR = "__xappsConfigConnection";

    /** the key to identify the darkstar session in the servlet context */
    public static final String SESSION_ATTR = "__xappsConfigSession";

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Get the repository
        ServletContext sc = getServletContext();
        WebContentRepositoryRegistry reg = WebContentRepositoryRegistry.getInstance();
        WebContentRepository wcr = reg.getRepository(sc);
        if (wcr == null) {
            error(request, response, "No content repositories found. <br>" +
                  "Please contact your system administrator for assistance.");
            return;
        }

        // Fetch the content node for the "x-apps" directory under "system". If
        // "x-apps" isn't there, then create it.
        ContentCollection xAppsCollection = null;
        try {
            ContentCollection sysRoot = wcr.getSystemRoot();
            ContentNode xappsNode = sysRoot.getChild("x-apps");
            if (xappsNode == null) {
                xappsNode = sysRoot.createChild("x-apps", Type.COLLECTION);
            }
            xAppsCollection = (ContentCollection)xappsNode;
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to get x-apps collection", excp);
            error(request, response, "No x-apps collection found. <br>" +
                  "Please contact your system administrator for assistance.");
            return;
        }

        // See if the request comes with an "action" (e.g. Delete). If so,
        // handle it and fall through to below to re-load the page
        try {
            String action = request.getParameter("action");
            if (action != null && action.equalsIgnoreCase("delete") == true) {
                handleDelete(request, response, xAppsCollection);
            }
            else if (action != null && action.equalsIgnoreCase("add") == true) {
                handleAdd(request, response, xAppsCollection);
            }
            else if (action != null && action.equalsIgnoreCase("check") == true) {
                handleCheck(request, response, xAppsCollection);
            }
            else {
                // Otherwise, display the items
                handleBrowse(request, response, xAppsCollection);
            }
        } catch (java.lang.Exception cre) {
            throw new ServletException(cre);
        }
    }

    protected void error(HttpServletRequest request,
                         HttpServletResponse response,
                         String message)
        throws ServletException, IOException
    {
        request.setAttribute("message", message);
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/error.jsp");
        rd.forward(request, response);
    }

    /**
     * Deletes an entry from the X11 Apps.
     */
    private void handleDelete(HttpServletRequest request,
            HttpServletResponse response, ContentCollection xAppsCollection)
        throws ServletException, IOException, ContentRepositoryException
    {
        // Fetch the path of the file being deleted, which holds the info about
        // the X11 App being deleted.
        String path = request.getParameter("path");
        String appName = request.getParameter("appName");

        // Find the file in the WebDav repository and delete it
        ContentResource resource = getXAppResource(xAppsCollection, path);
        if (resource == null) {
            error(request, response, "Path " + request.getPathInfo() +
                    " not found.");
            return;
        }
        xAppsCollection.removeChild(resource.getName());

        // Tell the config connection that we have removed an X11 App, if the
        // config connection exists (it should)
        Object obj = getServletContext().getAttribute(XAPPS_CONN_ATTR);
        if (obj != null) {
            XAppsWebConfigConnection connection = (XAppsWebConfigConnection)obj;
            connection.removeX11App(appName);
        }

        // After we have deleted the entry, then redisplay the listings
        try {
            handleBrowse(request, response, xAppsCollection);
        } catch (java.lang.Exception cre) {
            throw new ServletException(cre);
        }
    }

    /**
     * Adds an entry from the X11 Apps.
     */
    private void handleAdd(HttpServletRequest request,
            HttpServletResponse response, ContentCollection xAppsCollection)
        throws ServletException, IOException, ContentRepositoryException,
            JAXBException
    {
        // Fetch the desired app name and command of the new X11 App
        String appName = request.getParameter("appName");
        String command = request.getParameter("command");

        // Check to see if the command is blank. If so, then flag an error
        if (command == null || command.equals("") == true) {
            String msg = "The command must not be null. Cancelling.";
            error(request, response, msg);
            return;
        }

        // Check to see if the app name is null or an empty string. If so, then
        // just take the first token of the command.
        if (appName == null || appName.equals("") == true) {
            // First find the first token before the first space. Since the
            // command is not null, we are guaranteed to have at least one token
            String tokens[] = command.split(" ");
            String firstToken = tokens[0];

            // Next, if there are any '/' in the name, then just take the last
            // token. Note that X11 Apps only run on Linux/UNIX, so we only
            // care about the '/' which would appear in path names of the
            // command
            String paths[] = firstToken.split("/");
            appName = paths[paths.length - 1];
        }

        // We need to check whether the selected app name has already been taken.
        // If so, log an error and return. Create a file, based upon the app
        // name for the new X11 app, creating it if necessary
        String nodeName = appName + ".xml";
        ContentNode appNode = xAppsCollection.getChild(nodeName);
        if (appNode != null) {
            String msg = "The app name " + appName + " already exists. Cancelling.";
            error(request, response, msg);
            return;
        }
        appNode = xAppsCollection.createChild(nodeName, Type.RESOURCE);

        // Write the XAppRegistryItem object as an XML stream to this new file
        ContentResource resource = (ContentResource)appNode;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(os);
        XAppRegistryItem item = new XAppRegistryItem(appName, command);
        item.encode(w);
        byte b[] = os.toByteArray();
        resource.put(b);

        // Tell the config connection that we have added a new X11 App, if
        // the config connection exists (it should)
        Object obj = getServletContext().getAttribute(XAPPS_CONN_ATTR);
        if (obj != null) {
            XAppsWebConfigConnection connection = (XAppsWebConfigConnection)obj;
            connection.addX11App(appName, command);
        }

        // After we have added the entry, then redisplay the listings
        try {
            handleBrowse(request, response, xAppsCollection);
        } catch (java.lang.Exception cre) {
            throw new ServletException(cre);
        }
    }

    /**
     * Handles the default "browse" action to display the X11 App entries.
     */
    private void handleBrowse(HttpServletRequest request,
            HttpServletResponse response, ContentCollection c)
            throws ServletException, IOException, ContentRepositoryException,
            JAXBException
    {
        // Loop through all of the entries in the content repo and spit out
        // the information to a collection of X11AppEntry objects. This will
        // be displayed by the jsp.
        Collection<X11AppEntry> entries = new ArrayList();
        for (ContentNode child : c.getChildren()) {
            if (child instanceof ContentResource) {
                // Find out the information about the content resource item
                ContentResource resource = (ContentResource)child;
                String path = resource.getPath();

                // Use JAXB to parse the item
                Reader r = new InputStreamReader(resource.getInputStream());
                XAppRegistryItem item = XAppRegistryItem.decode(r);

                String appName = item.getAppName();
                String command = item.getCommand();
                
                X11AppEntry entry = new X11AppEntry(appName, command, path);
                String url = "delete&path=" + path + "&appName=" + appName;
                entry.addAction(new X11AppAction("delete", url));
                entries.add(entry);
            }
        }

        request.setAttribute("entries", entries);
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/browse.jsp");
        rd.forward(request, response);
    }

    /**
     * Handles the "check" action to map an app name to a command
     */
    private void handleCheck(HttpServletRequest request,
            HttpServletResponse response, ContentCollection c)
            throws ServletException, IOException, ContentRepositoryException,
            JAXBException
    {
        // get the name of the application to check
        String checkApp = request.getParameter("app");
        if (checkApp == null) {
            throw new ServletException("App parameter is required.");
        } else {
            checkApp = checkApp.trim();
        }

        // Loop through all of the entries in the content repo and spit out
        // the information to a collection of X11AppEntry objects. This will
        // be displayed by the jsp.
        for (ContentNode child : c.getChildren()) {
            if (child instanceof ContentResource) {
                // Find out the information about the content resource item
                ContentResource resource = (ContentResource)child;

                // Use JAXB to parse the item
                Reader r = new InputStreamReader(resource.getInputStream());
                XAppRegistryItem item = XAppRegistryItem.decode(r);

                if (item.getAppName().equalsIgnoreCase(checkApp)) {
                    response.setContentType("text/plain");

                    PrintWriter pr = new PrintWriter(response.getWriter());
                    pr.println(item.getCommand());
                    pr.close();
                    break;
                }
            }
        }
    }

    /**
     * Translates a path from the web page into a ContentResource representing
     * the file storing the X11 App information.
     */
    private ContentResource getXAppResource(ContentCollection node, String path)
            throws ContentRepositoryException
    {
        // Make sure the root starts with the proper prefix.
        if (path == null || path.startsWith("/system/x-apps") == false) {
            return null;
        }
        path = path.substring("/system/x-apps".length());
        return (ContentResource)node.getChild(path);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public void contextInitialized(ServletContextEvent sce) {
        // register with the admininstration page
        context = sce.getServletContext();
        ar = new AdminRegistration("Manage Apps",
                                   "/xapps-config/wonderland-xapps-config/browse");
        ar.setFilter(AdminRegistration.ADMIN_FILTER);
        AdminRegistration.register(ar, context);

        // add ourselves as a listener for when the Darkstar server changes
        DarkstarWebLoginFactory.getInstance().addDarkstarServerListener(this);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // remove the Darkstar server listener
        DarkstarWebLoginFactory.getInstance().removeDarkstarServerListener(this);
        
        // register with the admininstration page
        AdminRegistration.unregister(ar, context);

        // log out of any connected sessions
        WonderlandSession session = (WonderlandSession)context.getAttribute(SESSION_ATTR);
        if (session != null) {
            session.logout();
        }
    }

    public void serverStarted(DarkstarRunner runner, ServerSessionManager mgr) {
        // When a darkstar server starts up, open a connection to it, and
        // start a specific connection that sends messages when the configuration
        // of xapps has changed.
        try {
            WonderlandSession session = mgr.createSession();
            context.setAttribute(SESSION_ATTR, session);

            XAppsWebConfigConnection conn = new XAppsWebConfigConnection();
            session.connect(conn);
            context.setAttribute(XAPPS_CONN_ATTR, conn);
        } catch (ConnectionFailureException ex) {
            logger.log(Level.SEVERE, "Connection failed", ex);
        } catch (LoginFailureException ex) {
            logger.log(Level.WARNING, "Login failed", ex);
        }
    }

    public void serverStopped(DarkstarRunner arg0) {
        // When the darkstar server stops, remove the keys from the servlet
        // context
        context.removeAttribute(SESSION_ATTR);
        context.removeAttribute(XAPPS_CONN_ATTR);
    }


    /**
     * Represents a single X11 App entry: consists of the name of the app, the
     * command to launch the app, etc.
     */
    public static class X11AppEntry {

        private String appName;
        private String command;
        private String path;
        private List<X11AppAction> actions;

        public X11AppEntry(String appName, String command, String path) {
            this.appName = appName;
            this.command = command;
            this.actions = new ArrayList<X11AppAction>();
        }

        public void addAction(X11AppAction action) {
            actions.add(action);
        }

        public List<X11AppAction> getActions() {
            return actions;
        }

        public String getAppName() {
            return appName;
        }

        public String getCommand() {
            return command;
        }

        public String getPath() {
            return path;
        }
    }

    public static class X11AppAction {
        private String name;
        private String url;

        public X11AppAction(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
