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
package org.jdesktop.wonderland.modules.placemarks.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSessionImpl;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionCreator;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLogin;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLogin.DarkstarServerListener;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLoginFactory;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkList;
import org.jdesktop.wonderland.modules.placemarks.web.PlacemarkWebConfigConnection;

/**
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PlacemarksServlet extends HttpServlet implements ServletContextListener, DarkstarServerListener {

    private static final Logger logger = Logger.getLogger(PlacemarksServlet.class.getName());
    private AdminRegistration ar = null;
    private ServletContext context = null;

    /** the key to identify the connection in the servlet context */
    public static final String PLACEMARKS_CONN_ATTR = "__placemarksConfigConnection";

    /** the key to identify the darkstar session in the servlet context */
    public static final String SESSION_ATTR = "__pacemarksConfigSession";

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
        // See if the request comes with an "action" (e.g. Delete). If so,
        // handle it and fall through to below to re-load the page
        try {
            String action = request.getParameter("action");
            if (action != null && action.equalsIgnoreCase("delete") == true) {
                handleDelete(request, response);
            }
            else if (action != null && action.equalsIgnoreCase("add") == true) {
                handleAdd(request, response);
            }

            // Otherwise, display the items
            handleBrowse(request, response);
        } catch (java.lang.Exception cre) {
            throw new ServletException(cre);
        }
    }

    protected void error(HttpServletRequest request,
            HttpServletResponse response, String message)
        throws ServletException, IOException
    {
        request.setAttribute("message", message);
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/error.jsp");
        rd.forward(request, response);
    }

    /**
     * Deletes an entry from the X11 Apps.
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, ContentRepositoryException, JAXBException
    {
        // Fetch the name of the placemark to be deleted.
        String name = request.getParameter("name");

        // Fetch the PlacemarkList from the /system/placemarks/placemarks.xml
        // file.
        PlacemarkList placemarkList = getPlacemarkList();
        Placemark placemark = placemarkList.getPlacemark(name);

        // Remove the placemark given the name. It should exist. If not, then
        // the HTML list will get reloaded anyway. Write back to the placemarks.xml
        // file
        placemarkList.removePlacemark(name);
        setPlacemarkList(placemarkList);

        // Tell the config connection that we have removed a Placemark, if the
        // config connection exists (it should)
        Object obj = getServletContext().getAttribute(PLACEMARKS_CONN_ATTR);
        if (obj != null) {
            PlacemarkWebConfigConnection connection = (PlacemarkWebConfigConnection)obj;
            connection.removePlacemark(placemark);
        }
    }

    /**
     * Adds an entry to the system-wite Placemarks.
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, ContentRepositoryException,
            JAXBException
    {
        // Fetch all of the attributes for the new placemark
        String name = request.getParameter("name");
        String url = request.getParameter("url");
        float x = parseFloatString(request.getParameter("x"));
        float y = parseFloatString(request.getParameter("y"));
        float z = parseFloatString(request.getParameter("z"));
        float angle = parseFloatString(request.getParameter("angle"));

        // Fetch the PlacemarkList from the /system/placemarks/placemarks.xml
        // file.
        PlacemarkList placemarkList = getPlacemarkList();

        // Check to see if a name has been entered, flag an error if not
        if (name == null || name.equals("") == true) {
            String msg = "No name was entered for the Placemark. Cancelling.";
            error(request, response, msg);
            return;
        }
        
        // Check to see if the placemark already exists, flag an error if so.
        if (placemarkList.getPlacemark(name) != null) {
            String msg = "A Placemark named " + name + " already exists.";
            error(request, response, msg);
            return;
        }

        // Add the new placemark to the list of placemarks. Write the data
        // back out to the file.
        Placemark placemark = new Placemark(name, url, x, y, z, angle);
        placemarkList.addPlacemark(placemark);
        setPlacemarkList(placemarkList);

        // Tell the config connection that we have added a new Placemark, if
        // the config connection exists (it should)
        Object obj = getServletContext().getAttribute(PLACEMARKS_CONN_ATTR);
        if (obj != null) {
            PlacemarkWebConfigConnection connection = (PlacemarkWebConfigConnection)obj;
            connection.addPlacemark(placemark);
        }
    }

    /**
     * Attempts to parse a String as a floating point value. If valid, returns
     * the floating point value, if not, returns 0.0. Properly handles null and
     * empty strings too.
     */
    private float parseFloatString(String floatStr) {
        // If the given String is null or an empty return, return 0.0 right away
        if (floatStr == null || floatStr.equals("") == true) {
            return 0.0f;
        }

        // Otherwise, try to parse it and return it
        try {
            return Float.parseFloat(floatStr);
        } catch (NumberFormatException excp) {
            return 0.0f;
        }
    }

    /**
     * Handles the default "browse" action to display the Placemark entries.
     */
    private void handleBrowse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ContentRepositoryException,
            JAXBException
    {
        // Read in and parse the system-wide Placemarks configuration file.
        // Iterate through each child found and add an entry to spit out to
        // the HTML page.
        Collection<PlacemarkEntry> entries = new ArrayList();
        PlacemarkList placemarkList = getPlacemarkList();
        Set<String> nameSet = placemarkList.getPlacemarkNames();
        for (String name : nameSet) {
            // Create a new entry for the HTML page to list
            Placemark placemark = placemarkList.getPlacemark(name);
            PlacemarkEntry entry = new PlacemarkEntry();
            entry.name = placemark.getName();
            entry.url = placemark.getUrl();
            entry.x = placemark.getX();
            entry.y = placemark.getY();
            entry.z = placemark.getZ();
            entry.angle = placemark.getAngle();

            // Add an action to "Delete" the entry
            String url = "delete&name=" + name;
            entry.addAction(new PlacemarkAction("delete", url));
            entries.add(entry);
        }

        // Send along the list of entries and redirect to the proper display
        // page.
        request.setAttribute("entries", entries);
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/browse.jsp");
        rd.forward(request, response);
    }

    /**
     * Reads the placemarks.xml file and returns a PlacemarkList object
     */
    private PlacemarkList getPlacemarkList() throws ContentRepositoryException, JAXBException {
        ContentResource resource = getPlacemarksResource();
        Reader r = new InputStreamReader(resource.getInputStream());
        return PlacemarkList.decode(r);
    }

    /**
     * Writes the PlacemarkList object to the placemarks.xml file.
     */
    private void setPlacemarkList(PlacemarkList placemarkList) throws ContentRepositoryException, JAXBException {
        ContentResource resource = getPlacemarksResource();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(os);
        placemarkList.encode(w);
        resource.put(os.toByteArray());
    }

    /**
     * Returns the ContentResource that represents the placemarks.xml file.
     */
    private ContentResource getPlacemarksResource() throws ContentRepositoryException, JAXBException {
        // Get the repository
        ServletContext sc = getServletContext();
        WebContentRepositoryRegistry reg = WebContentRepositoryRegistry.getInstance();
        WebContentRepository wcr = reg.getRepository(sc);
        if (wcr == null) {
            return null;
        }

        // Fetch the content node for the "placemarks/" directory under "system".
        // If "placemarks/" isn't there, then create it.
        ContentCollection sysRoot = wcr.getSystemRoot();
        ContentCollection c = (ContentCollection)sysRoot.getChild("placemarks");
        if (c == null) {
            c = (ContentCollection)sysRoot.createChild("placemarks", Type.COLLECTION);
        }

        // Try to find the "placemarks.xml" file. If it does not exist, then
        // create it with an empty PlacemarksList.
        ContentResource r = (ContentResource)c.getChild("placemarks.xml");
        if (r == null) {
            r = (ContentResource)c.createChild("placemarks.xml", Type.RESOURCE);
            PlacemarkList placemarkList = new PlacemarkList();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(os);
            placemarkList.encode(w);
            r.put(os.toByteArray());
        }
        return r;
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
        ar = new AdminRegistration("Edit Placemarks",
                                   "/placemarks/wonderland-placemarks/browse");
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
            PlacemarkSession session = mgr.createSession(new SessionCreator<PlacemarkSession>() {

                public PlacemarkSession createSession(ServerSessionManager session,
                                                      WonderlandServerInfo info,
                                                      ClassLoader loader)
                {
                    // issue #891: use the webapp classloader to resolve
                    // placemark messages
                    return new PlacemarkSession(session, info,
                                                getClass().getClassLoader());
                }
            });
            context.setAttribute(SESSION_ATTR, session);
            context.setAttribute(PLACEMARKS_CONN_ATTR, session.getConnection());
        } catch (LoginFailureException ex) {
            logger.log(Level.WARNING, "Login failed", ex);
        }
    }

    public void serverStopped(DarkstarRunner arg0) {
        // When the darkstar server stops, remove the keys from the servlet
        // context
        context.removeAttribute(SESSION_ATTR);
        context.removeAttribute(PLACEMARKS_CONN_ATTR);
    }

    // issue #891: use a custom session class, to make sure that messages
    // are resolved using the correct classloader (in this case, the 
    // webapp classloader must be used, since it has the Placemark messages
    // defined).
    private static class PlacemarkSession extends WonderlandSessionImpl {
        private PlacemarkWebConfigConnection conn;

        public PlacemarkSession(ServerSessionManager session,
                                WonderlandServerInfo info, ClassLoader cl)
        {
            super (session, info, cl);
        }

        public PlacemarkWebConfigConnection getConnection() {
            return conn;
        }

        @Override
        public void login(LoginParameters loginParams)
                throws LoginFailureException
        {
            super.login(loginParams);

            try {
                conn = new PlacemarkWebConfigConnection();
                connect(conn);
            } catch (ConnectionFailureException ex) {
                throw new LoginFailureException(ex);
            }
        }
    }

    /**
     * Represents a single placemark to be displayed in the HTML Web UI.
     */
    public static class PlacemarkEntry {

        public String name = null;
        public String url = null;
        public float x = 0.0f;
        public float y = 0.0f;
        public float z = 0.0f;
        public float angle = 0.0f;
        private List<PlacemarkAction> actions = null;

        public PlacemarkEntry() {
            this.actions = new ArrayList<PlacemarkAction>();
        }

        public void addAction(PlacemarkAction action) {
            actions.add(action);
        }

        public List<PlacemarkAction> getActions() {
            return actions;
        }

        public float getAngle() {
            return angle;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }
    }

    /**
     * A class that represents an "action" for one placemark that appears in the
     * Web UI. (e.g. "Delete").
     */
    public static class PlacemarkAction {
        private String name;
        private String url;

        public PlacemarkAction(String name, String url) {
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
