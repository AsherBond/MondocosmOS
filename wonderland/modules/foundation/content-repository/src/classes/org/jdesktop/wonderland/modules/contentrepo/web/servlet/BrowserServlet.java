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

package org.jdesktop.wonderland.modules.contentrepo.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepositoryRegistry;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 *
 * @author jkaplan
 */
public class BrowserServlet extends HttpServlet
        implements ServletContextListener
{
    private static final Logger logger =
            Logger.getLogger(BrowserServlet.class.getName());

    private AdminRegistration ar;

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
        ServletContext sc = getServletContext();

        // get the repository
        WebContentRepositoryRegistry reg = WebContentRepositoryRegistry.getInstance();
        WebContentRepository wcr = reg.getRepository(sc);
        if (wcr == null) {
            error(request, response, "No content repositories found. <br>" +
                  "Please contact your system administrator for assistance.");
            return;
        }

        try {
            ContentNode node = translatePath(wcr, request.getPathInfo());
            if (node == null) {
                error(request, response, "Path " + request.getPathInfo() +
                      " not found.");
                return;
            }

            String action = request.getParameter("action");
            if (action == null && ServletFileUpload.isMultipartContent(request)) {
                action = "upload";
            }
            
            if (action != null) {
                node = handleAction(request, response, node, action);
                if (node == null) {
                    // indicates that the action has handled forwarding on
                    // its own, for example to the error page
                    return;
                }
            }

            DirectoryEntry current = new DirectoryEntry(node);
            request.setAttribute("current", current);

            if (node instanceof ContentCollection) {
                handleCollection(request, response, (ContentCollection) node);
            } else if (node instanceof ContentResource) {
                handleResource(request, response, (ContentResource) node);
            } else {
                error(request, response, "Unknown node type " + node.getClass());
            }
        } catch (ContentRepositoryException cre) {
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

    protected ContentNode translatePath(WebContentRepository wcr, String path)
        throws ContentRepositoryException
    {
        // find the root (system or users)
        ContentCollection root = null;

        if (path == null || path.equals("/")) {
            root = wcr.getRoot();
        } else if (path.startsWith("/system")) {
            path = path.substring("/system".length());
            root = wcr.getSystemRoot();
        } else if (path.startsWith("/users/")) {
            path = path.substring("/users/".length());
            String userId = path;

            int endIdx = path.indexOf("/");
            if (endIdx != -1) {
                userId = path.substring(0, endIdx);
                path = path.substring(endIdx);
            } else {
                path = null;
            }

            root = wcr.getUserRoot(userId);
        } else {
            root = wcr.getRoot();
        }

        if (root == null) {
            return null;
        }

        if (path == null || path.length() == 0 || path.equals("/")) {
            return root;
        }

        return root.getChild(path);
    }

    protected ContentNode handleAction(HttpServletRequest request,
                                       HttpServletResponse response,
                                       ContentNode node, String action)
        throws ServletException, IOException, ContentRepositoryException
    {
        if (action.equalsIgnoreCase("mkdir")) {
            return handleMkdir(request, response, node, action);
        } else if (action.equalsIgnoreCase("delete")) {
            return handleDelete(request, response, node, action);
        } else if (action.equalsIgnoreCase("upload")) {
            return handleUpload(request, response, node, action);
        } else {
            // do nothing
            return node;
        }
    }

    protected ContentNode handleMkdir(HttpServletRequest request,
                                      HttpServletResponse response,
                                      ContentNode node, String action)
        throws ServletException, IOException, ContentRepositoryException
    {
        String dirName = request.getParameter("dirname");
        if (dirName == null || dirName.trim().length() == 0) {
            error(request, response, "Invalid name");
            return null;
        }

        if (!(node instanceof ContentCollection)) {
            error(request, response, "Not a directory");
            return null;
        }

        ((ContentCollection) node).createChild(dirName, ContentNode.Type.COLLECTION);
        return node;
    }

    protected ContentNode handleDelete(HttpServletRequest request,
                                       HttpServletResponse response,
                                       ContentNode node, String action)
        throws ServletException, IOException, ContentRepositoryException
    {
        ContentCollection parent = node.getParent();
        if (parent == null) {
            error(request, response, "Cannot delete top-level node.");
            return null;
        }

        parent.removeChild(node.getName());
        return parent;
    }
    
    protected ContentNode handleUpload(HttpServletRequest request,
                                       HttpServletResponse response,
                                       ContentNode node, String action)
        throws ServletException, IOException, ContentRepositoryException
    {
        if (!(node instanceof ContentCollection)) {
            error(request, response, "Not a directory");
            return null;
        }
        
        ContentCollection dir = (ContentCollection) node;
        
        /* Check that we have a file upload request */
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart == false) {
            logger.warning("[Runner] UPLOAD Bad request");
            String message = "Unable to recognize upload request. Please " +
                             "try again.";
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return null;
        }

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();

        // Parse the request
        try {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext() == true) {
                FileItemStream item = iter.next();
                String name = item.getName();
                InputStream stream = item.openStream();
                if (item.isFormField() == false) {
                    ContentResource child = (ContentResource)
                            dir.createChild(name, ContentNode.Type.RESOURCE);

                    File tmp = File.createTempFile("contentupload", "tmp");
                    RunUtil.writeToFile(stream, tmp);
                    child.put(tmp);
                    tmp.delete();
                }
            }

            return node;
        } catch (FileUploadException excp) {
            /* Log an error to the log and write an error message back */
            logger.log(Level.WARNING, "[Runner] UPLOAD Failed", excp);
            String message = "Unable to upload runner for some reason.";
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                               message);
            return null;
        } catch (IOException excp) {
            /* Log an error to the log and write an error message back */
            logger.log(Level.WARNING, "[Runner] UPLOAD Failed", excp);
            String message = "Unable to upload runner for some reason.";
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                               message);
            return null;
        }
    }

    protected void handleCollection(HttpServletRequest request,
                                    HttpServletResponse response,
                                    ContentCollection c)
            throws ServletException, IOException, ContentRepositoryException
    {
        // set the path of this collection
        request.setAttribute("path", c.getPath());

        Collection<DirectoryEntry> entries =
                new ArrayList<DirectoryEntry>();
        for (ContentNode child : c.getChildren()) {
            DirectoryEntry de = new DirectoryEntry(child);
            switch (de.getType()) {
                case DIRECTORY:
                    de.addAction(new DirectoryAction("delete", "delete"));
                    break;
                case FILE:
                    de.addAction(new DirectoryAction("delete", "delete"));
                    break;
            }
            entries.add(de);
        }

        request.setAttribute("entries", entries);
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/browse.jsp");
        rd.forward(request, response);
    }

    protected void handleResource(HttpServletRequest request,
                                  HttpServletResponse response,
                                  ContentResource r)
        throws ServletException, IOException, ContentRepositoryException
    {
        // set the type
        String mimeType = getServletContext().getMimeType(r.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);

        if (r.getSize() > 0) {
            response.setContentLength((int) r.getSize());
        }

        // write the data
        InputStream is = r.getInputStream();
        byte[] buffer = new byte[16 * 1024];
        int read;
        
        while ((read = is.read(buffer)) > 0) {
            response.getOutputStream().write(buffer, 0, read);
        }
        
        response.getOutputStream().close();
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
        ServletContext sc = sce.getServletContext();
        ar = new AdminRegistration("Manage Content",
                                   "/content-repository/wonderland-content-repository/browse");
        ar.setFilter(AdminRegistration.ADMIN_FILTER);
        AdminRegistration.register(ar, sc);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // register with the admininstration page
        ServletContext sc = sce.getServletContext();
        AdminRegistration.unregister(ar, sc);
    }

    public static class DirectoryEntry {
        public enum Type { FILE, DIRECTORY };

        private String name;
        private String path;
        private Type type;
        private List<DirectoryAction> actions;

        public DirectoryEntry(ContentNode node) {
            name = node.getName();

            path = node.getPath();
            if (path.equals("")) {
                path = "/";
            }

            if (node instanceof ContentCollection) {
                type = Type.FILE;
            } else {
                type = Type.DIRECTORY;
            }

            actions = new ArrayList<DirectoryAction>();
        }

        public void addAction(DirectoryAction action) {
            actions.add(action);
        }

        public List<DirectoryAction> getActions() {
            return actions;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public Type getType() {
            return type;
        }
    }

    public static class DirectoryAction {
        private String name;
        private String url;

        public DirectoryAction(String name, String url) {
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
