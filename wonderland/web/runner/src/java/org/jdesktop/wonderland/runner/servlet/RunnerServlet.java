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
package org.jdesktop.wonderland.runner.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jdesktop.wonderland.runner.DeploymentEntry;
import org.jdesktop.wonderland.runner.DeploymentManager;
import org.jdesktop.wonderland.runner.DeploymentPlan;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.RunnerException;

/**
 *
 * @author jkaplan
 */
public class RunnerServlet extends HttpServlet {
    /** a logger */
    private static final Logger logger =
            Logger.getLogger(RunnerServlet.class.getName());

    private static final String DEPLOYMENT_PLAN_SESSION_KEY =
            RunnerServlet.class.getName() + ".DeploymentPlan";

    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String action = request.getParameter("action");
        String runnerName = request.getParameter("name");
        Runner runner = null;
        
        if (action == null) {
            action = "view";
        }
        if (runnerName != null) {
            runner = RunManager.getInstance().get(runnerName);
        }
              
        if (action.equalsIgnoreCase("log")) {
            doLog(request, response, runner);
        } else if (action.equalsIgnoreCase("edit")) {
            doEdit(request, response, runner, null);
        } else if (action.equalsIgnoreCase("editForm")) {
            doEditForm(request, response, runner);
        } else if (action.equalsIgnoreCase("editRunners")) {
            doEditRunners(request, response);
        } else if (action.equalsIgnoreCase("editRunnersForm")) {
            doEditRunnersForm(request, response);
        } else if (action.equalsIgnoreCase("addRunner")) {
            doAddRunner(request, response, null);
        } else if (action.equalsIgnoreCase("addRunnerForm")) {
            doAddRunnerForm(request, response);
        } else if (action.equalsIgnoreCase("removeRunner")) {
            doRemoveRunner(request, response);
        } else {
            // default case -- show the view page
            doView(request, response);
        }
    }
    
    private void doView(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException
    {
        // forward to the view page
        RequestDispatcher rd = request.getRequestDispatcher("/view.jsp");
        rd.forward(request, response);
    }
    
    protected void doLog(HttpServletRequest request, 
                         HttpServletResponse response,
                         Runner runner)
        throws ServletException, IOException
    {
        FileReader fr = new FileReader(runner.getLogFile());
        request.setAttribute("log", new LogReader(fr));
        
        RequestDispatcher rd = request.getRequestDispatcher("/log.jsp");
        rd.forward(request, response);
    }

    protected void doEdit(HttpServletRequest request, 
                         HttpServletResponse response,
                         Runner runner, DeploymentEntry de)
        throws ServletException, IOException
    {
        if (de == null) {
            de = DeploymentManager.getInstance().getEntry(runner.getName());
        }
        
        // add in the default properties to the runner.  These will be removed
        // before they are saved
        Properties props = runner.getDefaultProperties();
        props.putAll(de.getProperties());
        de.setProperties(props);
        
        request.setAttribute("entry", de);
       
        RequestDispatcher rd = request.getRequestDispatcher("/edit.jsp");
        rd.forward(request, response);
    }
    
    protected void doEditForm(HttpServletRequest request,
                              HttpServletResponse response,
                              Runner runner)
        throws ServletException, IOException
    {
        DeploymentEntry de = getEntry(request);
        
        String button = request.getParameter("button");
        if (button.equalsIgnoreCase("Save")) {
            doEditSave(request, response, runner, de);
        } else if (button.equalsIgnoreCase("Cancel")) {
            redirectToRun(response);
        } else if (button.equalsIgnoreCase("Restore Defaults")) {
            de.setProperties(runner.getDefaultProperties());
            doEdit(request, response, runner, de);
        } else {
            doEdit(request, response, runner, de);
        }
    }
    
    protected void doEditSave(HttpServletRequest request, 
                              HttpServletResponse response,
                              Runner runner,
                              DeploymentEntry de)
        throws ServletException, IOException
    {
        DeploymentManager dm = DeploymentManager.getInstance();
        DeploymentPlan dp = dm.getPlan();
        
        // if any property is the same as the default property,
        // remove it so it will change as the default changes
        for (String propName : de.getProperties().stringPropertyNames()) {
            String runVal = de.getProperties().getProperty(propName);
            String defVal = runner.getDefaultProperties().getProperty(propName);

            if (runVal.equals(defVal)) {
                de.getProperties().remove(propName);
            }
        }

        // replace the existing entry with the new one
        dp.removeEntry(de);
        dp.addEntry(de);
        dm.savePlan();
        
        redirectToRun(response);
    }

    protected void doEditRunners(HttpServletRequest request,
                                 HttpServletResponse response)
        throws ServletException, IOException
    {
        DeploymentPlan dp = getSessionDeploymentPlan(request);
        request.setAttribute("entries", dp.getEntries());

        RequestDispatcher rd = request.getRequestDispatcher("/editRunners.jsp");
        rd.forward(request, response);
    }

    protected void doEditRunnersForm(HttpServletRequest request,
                                   HttpServletResponse response)
        throws ServletException, IOException
    {
        String button = request.getParameter("button");
        if (button.equalsIgnoreCase("Cancel")) {
            // remove the temporary session
            request.getSession().removeAttribute(DEPLOYMENT_PLAN_SESSION_KEY);
            redirectToRun(response);
        } else if (button.equalsIgnoreCase("Save")) {
            // store the new plan
            DeploymentPlan sessionPlan = getSessionDeploymentPlan(request);
            DeploymentManager.getInstance().setPlan(sessionPlan);
            DeploymentManager.getInstance().savePlan();
            
            // restart the RunManager
            restartRunManager();

            // remove the session key
            request.getSession().removeAttribute(DEPLOYMENT_PLAN_SESSION_KEY);

            // redirect
            redirectToRun(response);
        } else if (button.equalsIgnoreCase("Restore Defaults")) {
            // restore the default plan
            DeploymentManager.getInstance().removePlan();

            // restart the RunManager
            restartRunManager();

            // remove the session key
            request.getSession().removeAttribute(DEPLOYMENT_PLAN_SESSION_KEY);

            // redirect
            redirectToRun(response);
        } else {
            // otherwise there was an error -- go back to the add page
            doEditRunners(request, response);
        }
    }

    protected void restartRunManager() throws ServletException {
        RunManager.getInstance().shutdown();
        try {
            RunManager.getInstance().initialize();
        } catch (RunnerException re) {
            throw new ServletException(re);
        }
    }

    protected void doAddRunner(HttpServletRequest request,
                               HttpServletResponse response,
                               DeploymentEntry entry)
        throws ServletException, IOException
    {
        if (entry == null) {
            entry = new DeploymentEntry();
        }
        request.setAttribute("entry", entry);

        RequestDispatcher rd = request.getRequestDispatcher("/addRunner.jsp");
        rd.forward(request, response);
    }

    protected void doAddRunnerForm(HttpServletRequest request,
                                   HttpServletResponse response)
        throws ServletException, IOException
    {
        DeploymentEntry de = getEntry(request);

        String button = request.getParameter("button");
        if (button.equalsIgnoreCase("Cancel") ||
                (button.equalsIgnoreCase("OK") &&
                    addRunnerToPlan(request, de)))
        {
            // if we cancelled or save succefully, go back to the edit
            // page
            redirectToEditRunners(response);
        } else {
            // otherwise there was an error -- go back to the add page
            doAddRunner(request, response, de);
        }
    }

    protected boolean addRunnerToPlan(HttpServletRequest request,
                                      DeploymentEntry de)
        throws ServletException, IOException
    {
        // make sure we got an entry
        if (de == null || de.getRunnerName() == null ||
            de.getRunnerClass() == null)
        {
            request.setAttribute("error", "Invalid entry");
            return false;
        }

        DeploymentPlan dp = getSessionDeploymentPlan(request);

        // make sure this isn't a duplicate
        if (dp.getEntry(de.getRunnerName()) != null) {
            request.setAttribute("error", "Duplicate name");
            return false;
        }
        
        dp.addEntry(de);
        return true;
    }

    protected void doRemoveRunner(HttpServletRequest request,
                                  HttpServletResponse response)
        throws ServletException, IOException
    {
        String name = (String) request.getParameter("name");
        if (name == null) {
            return;
        }

        DeploymentPlan dp = getSessionDeploymentPlan(request);

        // find the entry and remove it
        DeploymentEntry de = dp.getEntry(name);
        if (de != null) {
            dp.removeEntry(de);
        }

        redirectToEditRunners(response);
    }

    /**
     * Get the deployment plan stored in the session, or copy the current
     * plan if there is none in the session.
     * @return the session deployment plan
     */
    protected DeploymentPlan getSessionDeploymentPlan(HttpServletRequest request) {
        HttpSession session = request.getSession();
        DeploymentPlan dp = (DeploymentPlan) session.getAttribute(DEPLOYMENT_PLAN_SESSION_KEY);
        if (dp == null) {
            DeploymentManager dm = DeploymentManager.getInstance();
            dp = dm.getPlan().clone();
            session.setAttribute(DEPLOYMENT_PLAN_SESSION_KEY, dp);
        }

        return dp;
    }
    
    protected void redirectToRun(HttpServletResponse response) 
        throws IOException
    {
        redirectTo(response, "/wonderland-web-runner/run");
    }

    protected void redirectToEditRunners(HttpServletResponse response)
        throws IOException
    {
        redirectTo(response, "/wonderland-web-runner/run?action=editRunners");
    }

    protected void redirectTo(HttpServletResponse response, String page)
            throws IOException
    {
        String url = "/wonderland-web-front/admin?pageURL=" +
                URLEncoder.encode(page, "utf-8");
        
        response.getWriter().println("<script>");
        response.getWriter().println("parent.location.replace('" + url + "');");
        response.getWriter().println("</script>");
        response.getWriter().close();
    }
    
    protected DeploymentEntry getEntry(HttpServletRequest request) 
        throws ServletException
    {
        // read name, class & location
        String name = request.getParameter("name");
        String clazz = request.getParameter("class");
        String location = request.getParameter("location");

        // read properties
        Properties props = new Properties();
        int c = 1;
        String key;
        String value;
        while (true) {
            key = request.getParameter("key-" + c);
            value = request.getParameter("value-" + c);
            
            if (key == null) {
                break;
            }
            
            if (key.trim().length() > 0) {
                props.put(key, value);
            }
            c++;
        } 
        
        // read new property
        key = request.getParameter("key-new");
        value = request.getParameter("value-new");
        if (key != null && key.trim().length() > 0) {
            props.put(key, value);
        }
        
        DeploymentEntry de = new DeploymentEntry(name, clazz);
        if (location != null) {
            de.setLocation(location);
        }
        de.setProperties(props);
        
        return de;
    }
    
    static class LogReader implements Iterator<String> {
        private BufferedReader log;
        private String line;
        
        public LogReader(Reader log) {
            this.log = new BufferedReader(log);
            readNextLine();
        }
        
        public void readNextLine() {
            try {
                line = log.readLine();
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error reading log file", ioe);
                line = null;
            }
        }

        public boolean hasNext() {
            return (line != null);
        }

        public String next() {
            String ret = line;
            readNextLine();
            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
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
}
