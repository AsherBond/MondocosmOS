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
package org.jdesktop.wonderland.runner.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.WebServiceException;
import org.jdesktop.wonderland.runner.BaseRemoteRunner;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.RunnerInfo;

/**
 * The RemoteStatusResource class is a Jersey RESTful service that can be used
 * to set the status of remote runners.  Typically, this will be called
 * periodically (every few seconds) for each remote runner.  If no message
 * comes from the remote runner after a few periods, the remote runner
 * will be listed as NOT_CONNECTED.
 * 
 * @author jkaplan
 */
@Path(value="/remotestatus/{runner}")
public class RemoteStatusResource implements ServletContextListener {
    private static final Logger logger =
            Logger.getLogger(RemoteStatusResource.class.getName());

    private static final String TIMER_ATTR = "Timer";
    private static final String TASK_ATTR = "CheckRunnersTask";


    @Context
    private ServletContext context;

    /**
     * Set the status of a runner
     */
    @POST
    @Consumes({"application/xml"})
    public Response setRunnerInfo(@PathParam(value="runner") String runner,
                                  RunnerInfo runnerInfo) throws UnsupportedEncodingException
    {
        RunManager rm = RunManager.getInstance();
        String decname;
        try {
            decname = URLDecoder.decode(runner, "UTF-8");
        } catch (IOException ioe) {
            throw new WebServiceException(ioe);
        }
        
        Runner r = rm.get(decname);
        if (r == null) {
            ResponseBuilder rb = Response.status(Status.NOT_ACCEPTABLE);
            return rb.entity("No such runner: " + runner).type("text/plain").build();
        }
        if (!(r instanceof BaseRemoteRunner)) {
            ResponseBuilder rb = Response.status(Status.NOT_ACCEPTABLE);
            return rb.entity("Only BaseRemoteRunners are valid for setting status").type("text/plain").build();
        }

        BaseRemoteRunner brr = (BaseRemoteRunner) r;
        brr.setRunnerInfo(runnerInfo);

        CheckRunnersTask crt = (CheckRunnersTask) context.getAttribute(TASK_ATTR);
        crt.statusUpdate(brr);
        
        return Response.ok().build();
    }

    public void contextInitialized(ServletContextEvent sce) {
        Timer timer = new Timer();
        sce.getServletContext().setAttribute(TIMER_ATTR, timer);

        CheckRunnersTask crt = new CheckRunnersTask();
        timer.schedule(crt, 5000, 5000);
        sce.getServletContext().setAttribute(TASK_ATTR, crt);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        Timer timer = (Timer) sce.getServletContext().getAttribute(TIMER_ATTR);
        if (timer != null) {
            timer.cancel();
            sce.getServletContext().removeAttribute(TIMER_ATTR);
            sce.getServletContext().removeAttribute(TASK_ATTR);
        }
    }

    static class CheckRunnersTask extends TimerTask {
        private final Map<BaseRemoteRunner, Long> runners =
                new HashMap<BaseRemoteRunner, Long>();

        // update the most recent time we've seen this runner
        public synchronized void statusUpdate(BaseRemoteRunner runner) {
            runners.put(runner, System.currentTimeMillis());
        }

        // check if we haven't heard from a runner in a while
        @Override
        public void run() {
            synchronized (this) {
                for (Iterator<Entry<BaseRemoteRunner, Long>> i = runners.entrySet().iterator();
                     i.hasNext();)
                {
                    Entry<BaseRemoteRunner, Long> e = i.next();
                    if ((System.currentTimeMillis() - e.getValue()) > 15000) {
                        e.getKey().setStatus(Runner.Status.NOT_CONNECTED);
                        i.remove();
                    }
                }
            }
        }
    }
}
