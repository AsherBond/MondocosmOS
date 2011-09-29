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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdesktop.wonderland.front.admin.ServerInfo;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 *
 * @author jkaplan
 */
public class ClientPropertiesServlet extends HttpServlet {
    private static final Logger LOGGER =
            Logger.getLogger(ClientPropertiesServlet.class.getName());

    private static final String PROPS_FILE = "client.properties";
    protected static final String PROPS_SESSION_KEY = "ClientPropsSession";

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/plain");
        Properties props = load();

        try {
            props.store(response.getWriter(), null);
        } finally {
            response.getWriter().close();
        }
    }

    protected Properties load() {
        File propsFile = new File(RunUtil.getRunDir(), PROPS_FILE);
        Properties props = new Properties();

        // a few default properties
        props.setProperty("x", "0");
        props.setProperty("y", "0");
        props.setProperty("z", "0");
        
        props.setProperty("wonderland.client.windowSize", "800x600");

        try {
            if (propsFile.exists()) {
                props.load(new FileReader(propsFile));
            }
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error reading " + propsFile, ioe);
        }

        // make sure this value is always up-to-date
        props.setProperty("sgs.server", ServerInfo.getServerURL());
        return props;
    }

    protected void save(Properties props) throws IOException {
        // save to file
        File propsFile = new File(RunUtil.getRunDir(), PROPS_FILE);
        props.store(new FileWriter(propsFile), "Created by web editor at " +
                    DateFormat.getDateTimeInstance().format(new Date()));
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
