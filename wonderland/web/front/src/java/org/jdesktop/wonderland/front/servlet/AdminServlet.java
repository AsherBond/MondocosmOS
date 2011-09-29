/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.front.servlet;

import javax.servlet.ServletContextEvent;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdesktop.wonderland.front.admin.AdminRegistration.RegistrationFilter;
import org.jdesktop.wonderland.utils.version.Version;

/**
 *
 * @author jkaplan
 */
public class AdminServlet extends HttpServlet implements ServletContextListener {
   private static final String DEFAULT_PAGE_URL = "/wonderland-web-front/default";

   /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
        String pageURL = request.getParameter("pageURL");
        if (pageURL == null) {
            String page = request.getParameter("page");
            AdminRegistration ae = mapPage(page);
            if (ae != null) {
                pageURL = ae.getUrl();
            }
        }
        
        if (pageURL == null) {
            pageURL = DEFAULT_PAGE_URL;
        }           

        // set the page URL variable
        request.setAttribute("pageURL", pageURL);

        // set the version variable
        request.setAttribute("version", new Version());

        // get the set of registered links, and apply any filter
        List<AdminRegistration> registry = getRegistry();

        // go through and apply any filters
        List<AdminRegistration> adminPages = new ArrayList<AdminRegistration>();
        for (AdminRegistration reg : registry) {
            RegistrationFilter filter = reg.getFilter();
            if (filter == null || filter.isVisible(request, response)) {
                adminPages.add(reg);
            }
        }

        // sort the pages
        Collections.sort(adminPages);
        
        // add the sorted list of registrations
        request.setAttribute("adminPages", adminPages);

        RequestDispatcher rd = request.getRequestDispatcher("/admin.jsp");
        rd.forward(request, response);
    } 

    private AdminRegistration mapPage(String page) {
        if (page == null) {
            return null;
        }
        
        for (AdminRegistration reg : getRegistry()) {
            if (page.equalsIgnoreCase(reg.getShortName())) {
                return reg;
            }
        }
        
        return null;
    }
    
    private List<AdminRegistration> getRegistry() {
        ServletContext sc = getServletContext();
        return (List<AdminRegistration>)
                sc.getAttribute(AdminRegistration.ADMIN_REGISTRY_PROP);
    }
        
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public void contextInitialized(ServletContextEvent sce) {
        // create the registry
        List<AdminRegistration> registry = new ArrayList<AdminRegistration>();

        // add the home item to the top of the list
        AdminRegistration home = new AdminRegistration("home", "Home",
                                           "/wonderland-web-front");
        home.setAbsolute(true);
        home.setPosition(0);
        registry.add(home);
        
        // add other built-in registrations
        AdminRegistration runner = new AdminRegistration("runner",
                "Manage Server", "/wonderland-web-runner");
        runner.setFilter(AdminRegistration.ADMIN_FILTER);
        runner.setPosition(1);
        registry.add(runner);

        // Register the module management page. This needs to happen here because
        // there is no forced order of installation for things in web/, and
        // front/ needs to be installed before modules/ and that currently
        // cannot be guaranteed.
        AdminRegistration modules = new AdminRegistration("modules",
                "Manage Modules", "/wonderland-web-modules/editor");
        modules.setFilter(AdminRegistration.ADMIN_FILTER);
        registry.add(modules);

        // Register the client properties management page
        AdminRegistration client = new AdminRegistration("client",
                "Client Settings", "/wonderland-web-front/config/settings");
        client.setFilter(AdminRegistration.ADMIN_FILTER);
        registry.add(client);

        // add the registry to the context
        ServletContext sc = sce.getServletContext();
        sc.setAttribute(AdminRegistration.ADMIN_REGISTRY_PROP, registry);
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        // ignore
    }
}
