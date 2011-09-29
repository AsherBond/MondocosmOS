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
package org.jdesktop.wonderland.front.servlet;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jkaplan
 */
public class ClientPropertiesEditorServlet extends ClientPropertiesServlet {
    private static final Logger LOGGER =
            Logger.getLogger(ClientPropertiesEditorServlet.class.getName());

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("button");
        if (action == null) {
            action = "view";
        }

        Properties props = (Properties) request.getSession().getAttribute(PROPS_SESSION_KEY);
        if (props == null) {
            props = load();
            request.getSession().setAttribute(PROPS_SESSION_KEY, props);
        }

        if (action.equalsIgnoreCase("add property")) {
            doEditUpdate(props, request, response);
        } else if (action.equalsIgnoreCase("save")) {
            doEditSave(props, request, response);
        } else if (action.equalsIgnoreCase("cancel")) {
            doEditCancel(request, response);
        } else {
            doEditView(props, request, response);
        }
    }

    protected void doEditUpdate(Properties props, HttpServletRequest request,
                                HttpServletResponse response)
        throws ServletException, IOException
    {
        // update properties
        updateProperties(props, request, response);

        // forward back to editor
        doEditView(props, request, response);
    }

    protected void doEditView(Properties props, HttpServletRequest request,
                              HttpServletResponse response)
        throws ServletException, IOException
    {
        Comparator<Map.Entry<Object, Object>> comp =
                new Comparator<Map.Entry<Object, Object>>()
        {
            public int compare(Entry<Object, Object> o1,
                               Entry<Object, Object> o2)
            {
                if (o1.getKey() instanceof String &&
                    o2.getKey() instanceof String)
                {
                    return ((String) o1.getKey()).compareTo((String) o2.getKey());
                } else {
                    return 0;
                }
            }
        };

        Set<Map.Entry<Object, Object>> sorted =
                new TreeSet<Map.Entry<Object, Object>>(comp);
        sorted.addAll(props.entrySet());

        request.setAttribute("props", sorted);

        RequestDispatcher rd = getServletContext().getRequestDispatcher("/clientProps.jsp");
        rd.forward(request, response);
    }

    protected void doEditCancel(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        request.getSession().removeAttribute(PROPS_SESSION_KEY);
        
        // redirect to admin page
        String url = "/wonderland-web-front/admin";

        response.getWriter().println("<script>");
        response.getWriter().println("parent.location.replace('" + url + "');");
        response.getWriter().println("</script>");
        response.getWriter().close();
    }

    protected void doEditSave(Properties props, HttpServletRequest request,
                              HttpServletResponse response)
        throws ServletException, IOException
    {
        // update the properties list based on what was submitted
        updateProperties(props, request, response);

        // save to file
        save(props);

        // forward back to original page
        doEditCancel(request, response);
    }

    protected void updateProperties(Properties props, HttpServletRequest request,
                                    HttpServletResponse response)
        throws ServletException, IOException
    {
        props.clear();

        int count = 1;
        while (request.getParameter("key-" + count) != null) {
            String key = request.getParameter("key-" + count);
            String value = request.getParameter("value-" + count);

            if (key.trim().length() > 0) {
                props.setProperty(key.trim(), value.trim());
            }

            count++;
        }

        String nk = request.getParameter("key-new");
        String nv = request.getParameter("value-new");
        if (nk.trim().length() > 0) {
            props.setProperty(nk.trim(), nv.trim());
        }
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
