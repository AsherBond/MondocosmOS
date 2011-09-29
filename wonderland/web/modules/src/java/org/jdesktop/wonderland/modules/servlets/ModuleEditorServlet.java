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
package org.jdesktop.wonderland.modules.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdesktop.wonderland.modules.service.ModuleManager;

/**
 * Servlet that implements logic for the module management page.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleEditorServlet extends HttpServlet {

    // The error logger
    private static final Logger LOGGER =
            Logger.getLogger(ModuleEditorServlet.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // get the action
        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }

        // If we want to remove modules then we delegate to the remove() method
        if (action.equalsIgnoreCase("remove") == true) {
            remove(request, response);
            return;
        }

        // The default action is to view
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }

    /**
     * Removes the given array of module names. If confirmation is needed then
     * first redirect to a confirmation page.
     */
    private void remove(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Parse out the array of module names to remove and whether we still
        // need to confirm this remove (confirm == true).
        boolean confirm = Boolean.parseBoolean(request.getParameter("confirm"));
        String removeModuleNames[] = request.getParameterValues("remove");

        // If we need to confirm this remove, we redirect to the confirmation
        // page.
        String redirect = (confirm == true) ? "/confirmRemove.jsp" : "/index.jsp";
        
        // Go ahead and remove the modules and then redirect to the index.jsp
        // page.
        if (confirm == false) {
            ModuleManager manager = ModuleManager.getModuleManager();
            List<String> moduleNames = Arrays.asList(removeModuleNames);
            manager.addToUninstall(moduleNames);
            manager.uninstallAll();
        }
        
        RequestDispatcher rd = request.getRequestDispatcher(redirect);
        rd.forward(request, response);
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
}
