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
import java.util.Properties;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jkaplan
 */
public class ClientSettingsServlet extends ClientPropertiesServlet {
    private static final Logger LOGGER =
            Logger.getLogger(ClientSettingsServlet.class.getName());

    private static final String PROP_WINDOW = "wonderland.client.windowSize";
    private static final String ATTR_WINDOW = "window_size";
    private static final String WINDOW_DEFAULT = "800x600";

    private static final String PROP_X = "x";
    private static final String PROP_Y = "y";
    private static final String PROP_Z = "z";

    private static final String PROP_AUDIO = "AudioManagerClient.InitialState";
    private static final String ATTR_AUDIO = "audio_type";
    private static final String AUDIO_DEFAULT = "unmuted";

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

        if (action.equalsIgnoreCase("edit properties")) {
            doEditProperties(props, request, response);
        } else if (action.equalsIgnoreCase("save")) {
            doEditSave(props, request, response);
        } else if (action.equalsIgnoreCase("cancel")) {
            doEditCancel(request, response);
        } else {
            doEditView(props, request, response);
        }
    }

    protected void doEditProperties(Properties props, HttpServletRequest request,
                                    HttpServletResponse response)
        throws ServletException, IOException
    {
        // update properties
        updateProperties(props, request, response);

        // forward to properties editor, using the value of properties stored
        // in the session
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/config/edit");
        rd.forward(request, response);
    }

    protected void doEditView(Properties props, HttpServletRequest request,
                              HttpServletResponse response)
        throws ServletException, IOException
    {
        request.setAttribute(ATTR_WINDOW, props.getProperty(PROP_WINDOW, WINDOW_DEFAULT));
        request.setAttribute(PROP_X, props.getProperty(PROP_X, "0"));
        request.setAttribute(PROP_Y, props.getProperty(PROP_Y, "0"));
        request.setAttribute(PROP_Z, props.getProperty(PROP_Z, "0"));
        request.setAttribute(ATTR_AUDIO, props.getProperty(PROP_AUDIO, AUDIO_DEFAULT));

        RequestDispatcher rd = getServletContext().getRequestDispatcher("/clientSettings.jsp");
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

        // save properties to file
        save(props);
        
        // forward back to original page
        doEditCancel(request, response);
    }

    protected void updateProperties(Properties props, HttpServletRequest request,
                                    HttpServletResponse response)
        throws ServletException, IOException
    {
        setProperty(props, PROP_WINDOW, request.getParameter(ATTR_WINDOW));
        setProperty(props, PROP_X, request.getParameter(PROP_X));
        setProperty(props, PROP_Y, request.getParameter(PROP_Y));
        setProperty(props, PROP_Z, request.getParameter(PROP_Z));
        setProperty(props, PROP_AUDIO, request.getParameter(ATTR_AUDIO));
    }

    private void setProperty(Properties props, String key, String value) {
        if (value != null) {
            props.setProperty(key, value);
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
