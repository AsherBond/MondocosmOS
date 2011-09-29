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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.service.ModuleManager;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * A servlet that accepts HTTP POST requests to upload and install modules.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleUploadServlet extends HttpServlet {

    // The error logger
    private static final Logger LOGGER =
            Logger.getLogger(ModuleUploadServlet.class.getName());

    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        throw new ServletException("Upload servlet only handles post");
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Create a factory for disk-base file items to handle the request. Also
         * place the file in add/.
         */
        String redirect = "/installFailed.jsp";
        ModuleManager manager = ModuleManager.getModuleManager();
        
        /* Check that we have a file upload request */
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart == false) {
            LOGGER.warning("Failed to upload module, isMultipart=false");
            String msg = "Unable to recognize upload request. Please try again.";
            request.setAttribute("errorMessage", msg);
            RequestDispatcher rd = request.getRequestDispatcher(redirect);
            rd.forward(request, response);
            return;
        }
 
        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();

        // Parse the request
        try {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext() == true) {
                FileItemStream item = iter.next();
                InputStream stream = item.openStream();
                if (item.isFormField() == false) {
                    /*
                     * The name given should have a .jar extension. Check this here. If
                     * not, return an error. If so, parse out just the module name.
                     */
                    String moduleJar = item.getName();
                    if (moduleJar.endsWith(".jar") == false) {
                        /* Log an error to the log and write an error message back */
                        LOGGER.warning("Upload is not a jar file " + moduleJar);
                        String msg = "The file " + moduleJar + " needs to be" +
                                " a jar file. Please try again.";
                        request.setAttribute("errorMessage", msg);
                        RequestDispatcher rd = request.getRequestDispatcher(redirect);
                        rd.forward(request, response);
                        return;
                    }
                    String moduleName = moduleJar.substring(0, moduleJar.length() - 4);

                    LOGGER.info("Upload Install module " + moduleName +
                            " with file name " + moduleJar);
                    
                    /*
                     * Write the file a temporary file
                     */
                    File tmpFile = null;
                    try {
                        tmpFile = File.createTempFile(moduleName+"_tmp", ".jar");
                        tmpFile.deleteOnExit();
                        RunUtil.writeToFile(stream, tmpFile);
                    } catch (java.lang.Exception excp) {
                        /* Log an error to the log and write an error message back */
                        LOGGER.log(Level.WARNING, "Failed to save file", excp);
                        String msg = "Internal error installing the module.";
                        request.setAttribute("errorMessage", msg);
                        RequestDispatcher rd = request.getRequestDispatcher(redirect);
                        rd.forward(request, response);
                        return;
                    }

                    /* Add the new module */
                    Collection<File> moduleFiles = new LinkedList<File>();
                    moduleFiles.add(tmpFile);
                    Collection<Module> result = manager.addToInstall(moduleFiles);
                    if (result.isEmpty() == true) {
                        /* Log an error to the log and write an error message back */
                        LOGGER.warning("Failed to install module " + moduleName);
                        String msg = "Internal error installing the module.";
                        request.setAttribute("errorMessage", msg);
                        RequestDispatcher rd = request.getRequestDispatcher(redirect);
                        rd.forward(request, response);
                        return;
                    }
                }
            }
        } catch (FileUploadException excp) {
            /* Log an error to the log and write an error message back */
            LOGGER.log(Level.WARNING, "File upload failed", excp);
            String msg = "Failed to upload the file. Please try again.";
            request.setAttribute("errorMessage", msg);
            RequestDispatcher rd = request.getRequestDispatcher(redirect);
            rd.forward(request, response);
            return;
        }
 
        /* Install all of the modules that are possible */
        manager.installAll();
        
        /* If we have reached here, then post a simple message */
        LOGGER.info("Added module successfully");
        RequestDispatcher rd = request.getRequestDispatcher("/installSuccess.jsp");
        rd.forward(request, response);
    }
    
    /** 
    * Returns a short description of the servlet.
    */
    @Override
    public String getServletInfo() {
        return "Module Upload Servlet";
    }
}
