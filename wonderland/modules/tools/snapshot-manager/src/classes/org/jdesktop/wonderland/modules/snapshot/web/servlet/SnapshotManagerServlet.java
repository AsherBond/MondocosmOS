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
package org.jdesktop.wonderland.modules.snapshot.web.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.web.wfs.WFSManager;
import org.jdesktop.wonderland.web.wfs.WFSSnapshot;
import org.jdesktop.wonderland.runner.Runner.Status;
import org.jdesktop.wonderland.runner.RunnerException;
import org.jdesktop.wonderland.runner.StatusWaiter;
import org.jdesktop.wonderland.web.wfs.WFSRoot;

/**
 *
 * @author jkaplan
 */
public class SnapshotManagerServlet extends HttpServlet
    implements ServletContextListener
{
    private static final Logger logger =
            Logger.getLogger(SnapshotManagerServlet.class.getName());

    // the empty world
    private static final EmptyWorld EMPTY_WORLD = new EmptyWorld();

    // our registration with the webadmin system
    private AdminRegistration reg;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
        throws ServletException, IOException
    {
        WFSManager m = WFSManager.getWFSManager();

        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }

        WFSRoot root = getRoot(request);
        WFSSnapshot snapshot = null;
        if (root instanceof WFSSnapshot) {
            snapshot = (WFSSnapshot) root;
        }

        SnapshotResult result = null;
        if (action.equalsIgnoreCase("update")) {
            result = doUpdate(request, response, snapshot);
        } else if (action.equalsIgnoreCase("edit")) {
            result = doEdit(request, response, snapshot);
        } else if (action.equalsIgnoreCase("remove")) {
            result = doRemove(request, response, snapshot);
        } else if (action.equalsIgnoreCase("snapshot")) {
            result = doSnapshot(request, response);
        } else if (action.equalsIgnoreCase("current")) {
            result = doCurrent(request, response, root);
        } else if (action.equalsIgnoreCase("restore")) {
            result = doRestore(request, response, root);
        }

        if (result != null) {
            // make the error visible
            request.setAttribute("error", result.getError());
            if (result.hasError()) {
                logger.warning("Error processing action " + action + ": " +
                               result.getError());
            }

            // redirect to the requested page
            if (result.hasRedirect()) {
                RequestDispatcher rd = getServletContext().getRequestDispatcher(result.getRedirect());
                rd.forward(request, response);
                return;
            }
        }

        // if we get here, we are going to display the main page

        // store the wfs roots in a variable
        List<WFSRoot> wfsRoots = m.getWFSRoots();
        wfsRoots.add(0, EMPTY_WORLD);
        request.setAttribute("roots", wfsRoots);

        // store the wfs snapshots in a variable.  Sort the snapshots by date
        List<WFSSnapshot> snapshots = m.getWFSSnapshots();
        Collections.sort(snapshots, new Comparator<WFSSnapshot>() {
            public int compare(WFSSnapshot o1, WFSSnapshot o2) {
                if (o1.getTimestamp() == null) {
                    return (o2.getTimestamp() == null)?0:1;
                } else if (o2.getTimestamp() == null) {
                    return -1;
                }
                
                return -1 * o1.getTimestamp().compareTo(o2.getTimestamp());
            }            
        });
        request.setAttribute("snapshots", snapshots);

        // find the current snapshot
        WFSRoot currentRoot = getCurrentRoot(wfsRoots, snapshots);
        request.setAttribute("currentroot", currentRoot);

        RequestDispatcher rd =
                getServletContext().getRequestDispatcher("/snapshots.jsp");
        rd.forward(request, response);
    } 

    SnapshotResult doEdit(HttpServletRequest request,
                          HttpServletResponse response,
                          WFSSnapshot snapshot)
        throws ServletException, IOException
    {
        if (snapshot == null) {
            return new SnapshotResult("No such snapshot " + 
                                      request.getParameter("root"),
                                      null);
        }
        request.setAttribute("snapshot", snapshot);

        return new SnapshotResult(null, "/edit.jsp");
    }

    SnapshotResult doUpdate(HttpServletRequest request,
                            HttpServletResponse response,
                            WFSSnapshot snapshot)
        throws ServletException, IOException
    {
        if (snapshot == null) {
            return new SnapshotResult("No such snapshot " + 
                                      request.getParameter("root"),
                                      "/edit.jsp");
        }

        logger.info("User " + getUsername(request) + " updated snapshot " +
                    snapshot.getRootPath());

        String name = request.getParameter("name");
        if (name == null) {
            name = "";
        }

        String description = request.getParameter("description");
        if (description == null) {
            description = "";
        }

        if (!name.equals(snapshot.getName())) {
            // change the name
            String error = validName(name, snapshot);
            if (error != null) {
                return new SnapshotResult(error, "/edit.jsp");
            }

            snapshot.setName(name);
        }

        if (!description.equals(snapshot.getDescription())) {
            snapshot.setDescription(description);
        }

        return null;
    }

    SnapshotResult doRemove(HttpServletRequest request,
                            HttpServletResponse response,
                            WFSSnapshot snapshot)
        throws ServletException, IOException
    {
        if (snapshot == null) {
            return new SnapshotResult("No such snapshot " + 
                                      request.getParameter("root"),
                                      null);
        }

        logger.info("User " + getUsername(request) + " removed snapshot " +
                    snapshot.getRootPath());

        WFSManager.getWFSManager().removeWFSSnapshot(snapshot.getName());
        return null;
    }

    SnapshotResult doSnapshot(HttpServletRequest request,
                              HttpServletResponse response)
        throws ServletException, IOException
    {
        // get the name from the request
        String name = request.getParameter("name");
        if (name == null) {
            // use a default name based on the current data
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.SS");
            name = df.format(new Date());
        }

        logger.info("User " + getUsername(request) + " created snapshot " +
                    name);

        DarkstarRunner runner = getRunner();

        // make sure the runner is stopped
        SnapshotResult res = requestRestart(request, runner, "snapshot");
        if (res != null) {
            return res;
        }

        try {
            runner.createSnapshot(name);
        } catch (RunnerException re) {
            throw new ServletException(re);
        }

        completeRestart(request, runner);
        return null;
    }

    SnapshotResult doCurrent(HttpServletRequest request,
                             HttpServletResponse response,
                             WFSRoot root)
        throws ServletException, IOException
    {
        logger.info("User " + getUsername(request) + " set current snapshot " +
                    "to " + root.getRootPath());

        DarkstarRunner runner = getRunner();

        // make sure the runner is stopped
        SnapshotResult res = requestRestart(request, runner, "current&root=" +
                                            getRootName(root));
        if (res != null) {
            return res;
        }

        // set the name
        runner.setWFSName(root.getRootPath());

        // finsh restarting
        completeRestart(request, runner);
        return null;
    }

    SnapshotResult doRestore(HttpServletRequest request,
                             HttpServletResponse response,
                             WFSRoot root)
        throws ServletException, IOException
    {
        logger.info("User " + getUsername(request) + " restored snapshot " +
                    root.getRootPath());

        DarkstarRunner runner = getRunner();

        // make sure the runner is stopped
        SnapshotResult res = requestRestart(request, runner, "restore&root=" +
                                            getRootName(root));
        if (res != null) {
            return res;
        }

        // set the name
        runner.setWFSName(root.getRootPath());
        runner.forceColdstart();

        // complete the restart
        completeRestart(request, runner);
        return null;
    }

    /**
     * Request that the given runner be stopped.
     * @param request the http request
     * @param runner the runner to stop
     * @param action the current action
     * @return null if the runner is stopped, or a SnapshotResult to go
     * to if the runner is not stopped
     */
    SnapshotResult requestRestart(HttpServletRequest request,
                                  DarkstarRunner runner,
                                  String action)
        throws IOException, ServletException
    {
        // make sure the runner exists
        if (runner == null) {
            return new SnapshotResult("No Darkstar servers available", null);
        }

        // find out whether we can stop the server
        boolean restart = false;
        String restartStr = request.getParameter("restart");
        if (restartStr != null) {
            restart = Boolean.parseBoolean(restartStr);
        }

        if (runner.getStatus() != Status.NOT_RUNNING) {
            if (restart) {
                try {
                    // stop the runner, and wait for it to stop
                    StatusWaiter sw = RunManager.getInstance().stop(runner, true);
                    sw.waitFor();
                } catch (RunnerException re) {
                    logger.log(Level.WARNING, "Error stopping " + runner, re);
                    return new SnapshotResult("Error stopping runner", null);
                } catch (InterruptedException ie) {
                    // just ignore?
                    logger.log(Level.WARNING, "Status wait interrupted", ie);
                }
            } else {
                // forward to the confim page
                request.setAttribute("prompt", "The Darkstar server must " +
                    "be restarted to perform this action");
                request.setAttribute("url", "?action=" + action + "&restart=true");
                return new SnapshotResult(null, "/confirm.jsp");
            }
        }

        // everything is OK
        return null;
    }

    /**
     * Complete a previous restart request
     * @param request the request
     * @param runner the runner to restart
     */
    protected void completeRestart(HttpServletRequest request,
                                   DarkstarRunner runner)
        throws IOException, ServletException
    {
        // make sure the runner exists
        if (runner == null) {
            return;
        }

        // find out whether we can stop the server
        boolean restart = false;
        String restartStr = request.getParameter("restart");
        if (restartStr != null) {
            restart = Boolean.parseBoolean(restartStr);
        }

        // start back up
        if (runner.getStatus() == Status.NOT_RUNNING) {
            if (restart) {
                // start the runner, don't wait for it though
                try {
                    RunManager.getInstance().start(runner, false);
                } catch (RunnerException re) {
                    throw new ServletException(re);
                }
            }
        }
    }

    /**
     * Get a WFS root from the request
     * @param request the request to get the root from
     */
    protected WFSRoot getRoot(HttpServletRequest request) {
        WFSRoot root = null;

        String rootName = request.getParameter("root");
        if (rootName == null) {
            return null;
        }

        // decide if it is a world or a snapshot
        if (rootName.startsWith(WFSRoot.WORLDS_DIR)) {
            String worldName = rootName.substring(WFSRoot.WORLDS_DIR.length() + 1);
            if (worldName.equals(EMPTY_WORLD.getName())) {
                return EMPTY_WORLD;
            }
            
            root = WFSManager.getWFSManager().getWFSRoot(worldName);
        } else if (rootName.startsWith(WFSSnapshot.SNAPSHOTS_DIR)) {
            String snapshotName = rootName.substring(WFSSnapshot.SNAPSHOTS_DIR.length() + 1);
            root = WFSManager.getWFSManager().getWFSSnapshot(snapshotName);
        }

        if (root == null) {
            logger.warning("Unable to find root: " + rootName);
        }

        return root;
    }

    /**
     * Get the name of a root
     * @param root the root to get the name of
     * @return the root name
     */
    protected String getRootName(WFSRoot root) {
        if (root instanceof WFSSnapshot) {
            return WFSSnapshot.SNAPSHOTS_DIR + "/" + root.getName();
        } else {
            return WFSRoot.WORLDS_DIR + "/" + root.getName();
        }
    }

    /**
     * Get the current WFS snapshot
     * @return the current snapshot
     */
    protected WFSRoot getCurrentRoot(List<WFSRoot> roots,
                                     List<WFSSnapshot> snapshots)
    {
        DarkstarRunner dr = getRunner();
        if (dr == null) {
            return null;
        }

        System.out.println("Get current root: " + dr.getWFSName());

        if (dr.getWFSName() == null) {
            return EMPTY_WORLD;
        }

        for (WFSRoot root : roots) {
            if (root.getRootPath().equals(dr.getWFSName())) {
                return root;
            }
        }

        for (WFSRoot root : snapshots) {
            if (root.getRootPath().equals(dr.getWFSName())) {
                return root;
            }
        }

        // not found
        return null;
    }

    /**
     * Get a Darkstar runner.  For now, this returns the first valid
     * runner.
     * XXX TODO: Deal with multiple runners XXX
     * @return the runner, or null if no Darkstar runner exists
     */
    protected DarkstarRunner getRunner() {
        Collection<DarkstarRunner> runners =
                RunManager.getInstance().getAll(DarkstarRunner.class);
        if (runners.isEmpty()) {
            return null;
        }

        return runners.iterator().next();
    }

    /**
     * Return any errors changing the given snapshot to the given name,
     * or null if there are no errors.
     */
    protected String validName(String name, WFSSnapshot snapshot) {
        if (name == null || name.trim().length() == 0) {
            return "Blank name";
        } else if (name.contains(" ") || name.contains("\t")) {
            return "Spaces not allowed in name";
        } else if (WFSManager.getWFSManager().getWFSSnapshot(name) != null) {
            return "Duplicate snapshot " + name;
        } else {
            return null;
        }
    }

    /**
     * Get the name of the current user, or "unknown" for an unauthenticated user
     * @return the username, or "unknown"
     */
    public String getUsername(HttpServletRequest req) {
        if (req.getUserPrincipal() == null) {
            return "unknown";
        }

        return req.getUserPrincipal().getName();
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
        // register with web admin
        reg = new AdminRegistration("Manage Worlds",
                                    "/snapshot-manager/snapshot/SnapshotManager");
        reg.setFilter(AdminRegistration.ADMIN_FILTER);
        AdminRegistration.register(reg, sce.getServletContext());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // unregister
        AdminRegistration.unregister(reg, sce.getServletContext());
    }

    class SnapshotResult {
        private String error;
        private String redirect;

        SnapshotResult(String error, String redirect) {
            this.error = error;
            this.redirect = redirect;
        }

        boolean hasError() {
            return error != null;
        }

        String getError() {
            return error;
        }

        boolean hasRedirect() {
            return redirect != null;
        }

        String getRedirect() {
            return redirect;
        }
    }

    static class EmptyWorld extends WFSRoot {
        @Override
        public String getName() {
            return "Empty World";
        }

        @Override
        public String getRootPath() {
            return "none";
        }

        @Override
        public WFS getWfs() {
            return null;
        }
    }
}
