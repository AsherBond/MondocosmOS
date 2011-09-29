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
package org.jdesktop.wonderland.modules.security.server.service;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.sgs.auth.Identity;
import com.sun.sgs.impl.util.AbstractService;
import com.sun.sgs.impl.util.AbstractService.Version;
import com.sun.sgs.impl.util.TransactionContext;
import com.sun.sgs.impl.util.TransactionContextFactory;
import com.sun.sgs.impl.sharedutil.LoggerWrapper;
import com.sun.sgs.impl.sharedutil.PropertiesWrapper;
import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.kernel.KernelRunnable;
import com.sun.sgs.service.Transaction;
import com.sun.sgs.service.TransactionProxy;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.server.auth.WonderlandServerIdentity;
import org.jdesktop.wonderland.server.security.ActionMap;
import org.jdesktop.wonderland.server.security.Resource;
import org.jdesktop.wonderland.server.security.ResourceMap;
import org.jdesktop.wonderland.server.security.SecureTask;

/**
 *
 * @author jkaplan
 */
public class SecurityService extends AbstractService {
    /** The name of this class. */
    private static final String NAME = SecurityService.class.getName();

    /** The package name. */
    private static final String PKG_NAME = "org.jdesktop.wonderland.modules.security.server.service";

    /** The logger for this class. */
	private static final LoggerWrapper logger =
        new LoggerWrapper(Logger.getLogger(PKG_NAME));

    /** The name of the version key. */
    private static final String VERSION_KEY = PKG_NAME + ".service.version";

    /** The major version. */
    private static final int MAJOR_VERSION = 1;

    /** The minor version. */
    private static final int MINOR_VERSION = 0;

    /** the component registry */
    private ComponentRegistry registry;

    /** manages the context of the current transaction */
    private TransactionContextFactory<SecurityContext> ctxFactory;

    /** executor where scheduled tasks are processed */
    private ExecutorService executor = Executors.newCachedThreadPool();

    public SecurityService(Properties props,
                           ComponentRegistry registry,
                           TransactionProxy proxy)
    {
        super(props, registry, proxy, logger);

        this.registry = registry;

        logger.log(Level.CONFIG, "Creating SecurityService properties: {0}",
                   props);
        PropertiesWrapper wrappedProps = new PropertiesWrapper(props);

        // create the transaction context factory
        ctxFactory = new TransactionContextFactoryImpl(proxy);

        try {
            /*
	         * Check service version.
 	         */
            transactionScheduler.runTask(new KernelRunnable() {
                public String getBaseTaskType() {
                    return NAME + ".VersionCheckRunner";
                }

                public void run() {
                    checkServiceVersion(
                            VERSION_KEY, MAJOR_VERSION, MINOR_VERSION);
                }
            }, taskOwner);
        } catch (Exception ex) {
            logger.logThrow(Level.SEVERE, ex, "Error reloading cells");
        }
    }

    public void doSecure(ResourceMap request, SecureTask task) {
        // figure out the identity we are running as, using the client
        // auth service
        Identity curId = txnProxy.getCurrentOwner();
        if (!(curId instanceof WonderlandServerIdentity)) {
            // what should we do here?  Grant access?  Deny access?
            logger.log(Level.SEVERE, "Cannot apply security for user " + curId);
            task.run(request);
            return;
        }
        WonderlandServerIdentity id = (WonderlandServerIdentity) curId;

        // create the response object, which will be populated below with
        // any actions that are granted
        ResourceMap grant = new ResourceMap();

        // create a set of actions to schedule in a separate transaction
        ResourceMap schedule = new ResourceMap();

        // loop through each action set in the request, and generate
        // an appropriate response
        for (ActionMap set : request.values()) {
            // get the resource this set pertains to
            Resource resource = set.getResource();

            // create a response set that we will append the granted
            // permissions to
            ActionMap grantSet = new ActionMap(resource);
            grant.put(resource.getId(), grantSet);

            // create a set of requests to schedule.  We do not add the
            // set immediately -- it will be added at the end if it
            // is not empty
            ActionMap scheduleSet = new ActionMap(resource);

            // go through each action, and decider whether to grant or deny
            // access, or if necessary, schedule for later
            for (Action action : set.values()) {
                // query the resource about this permission, and handle the
                // result
                switch (resource.request(id.getIdentity(), action)) {
                    case GRANT:
                        // access is granted, add to the response list
                        grantSet.put(action.getName(), action);
                        break;
                    case DENY:
                        // access is denied -- nothing to do
                        break;
                    case SCHEDULE:
                        // add to the set to schedule
                        scheduleSet.put(action.getName(), action);
                        break;
                }
            }

            // if the schedule set is not empty, add it for scheduling
            if (!scheduleSet.isEmpty()) {
                schedule.put(resource.getId(), scheduleSet);
            }
        }

        // determine if we can respond immediately
        if (schedule.isEmpty()) {
            // nothing to schedule, handle the response directly in
            // this transaction
            task.run(grant);
        } else {
            // schedule the request in a separate thread.  First store
            // a task that can be referrred to later once the calculation
            // is complete
            RunSecureTask runTask = new RunSecureTask(task);
            ManagedReference<RunSecureTask> runTaskRef =
                    AppContext.getDataManager().createReference(runTask);

            ScheduledRequest req = new ScheduledRequest(id, runTaskRef.getId(),
                                                        grant, schedule);
            ctxFactory.joinTransaction().add(req);
        }
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void doReady() {
        logger.log(Level.CONFIG, "Security service ready");
    }

    @Override
    protected void doShutdown() {
        // nothing to do
    }

    @Override
    protected void handleServiceVersionMismatch(Version oldVersion,
                                                Version currentVersion) {
        throw new IllegalStateException(
 	            "unable to convert version:" + oldVersion +
	            " to current version:" + currentVersion);
    }

    /**
     * Transaction state
     */
    private class SecurityContext extends TransactionContext {
        private List<ScheduledRequest> requests = 
                new LinkedList<ScheduledRequest>();

        public SecurityContext(Transaction txn) {
            super (txn);
        }

        public void add(ScheduledRequest request) {
            requests.add(request);
        }

        @Override
        public void abort(boolean retryable) {
            requests.clear();
        }

        @Override
        public void commit() {
            isCommitted = true;

            for (ScheduledRequest request : requests) {
                executor.submit(request);
            }

            requests.clear();
        }
    }

    /** Private implementation of {@code TransactionContextFactory}. */
    private class TransactionContextFactoryImpl
            extends TransactionContextFactory<SecurityContext> {

        /** Creates an instance with the given proxy. */
        TransactionContextFactoryImpl(TransactionProxy proxy) {
            super(proxy, NAME);

        }

        /** {@inheritDoc} */
        protected SecurityContext createContext(Transaction txn) {
            return new SecurityContext(txn);
        }
    }

    class ScheduledRequest implements Runnable {
        private WonderlandServerIdentity identity;
        private BigInteger taskId;
        private ResourceMap grant;
        private ResourceMap schedule;

        public ScheduledRequest(WonderlandServerIdentity identity,
                                BigInteger taskId, ResourceMap grant,
                                ResourceMap schedule)
        {
            this.identity = identity;
            this.taskId = taskId;
            this.grant = grant;
            this.schedule = schedule;
        }

        public void run() {
            // go through each request in the schedule set, and make the
            // request for it
            for (ActionMap m : schedule.values()) {
                // get the resource we are referring to
                Resource resource = m.getResource();

                // get the object that will hold the result of scheduled
                // requests
                ActionMap out = grant.get(resource.getId());
                if (out == null) {
                    out = new ActionMap(resource);
                    grant.put(resource.getId(), out);
                }

                // now make the requests
                for (Action a : m.values()) {
                    if (resource.request(identity.getIdentity(), a, registry)) {
                        out.put(a.getName(), a);
                    }
                }
            }
            try {
                // response now has the proper set of permissions, so create
                // a new transaction to call back into the secure task
                transactionScheduler.runTask(new SecureTaskKernelRunner(taskId, grant),
                                             identity);
            } catch (Exception ex) {
                logger.logThrow(Level.WARNING, ex, "Unable to run secure task");
            }
        }
    }

    private class SecureTaskKernelRunner implements KernelRunnable {
        private BigInteger taskId;
        private ResourceMap response;

        public SecureTaskKernelRunner(BigInteger taskId, ResourceMap response) {
            this.taskId = taskId;
            this.response = response;
        }

        public String getBaseTaskType() {
            return NAME + ".SecureTaskRunner";
        }

        public void run() throws Exception {
            // create a managed reference from the task id we were given
            // earlier
            ManagedReference<RunSecureTask> taskRef = (ManagedReference<RunSecureTask>)
                    dataService.createReferenceForId(taskId);
            RunSecureTask task = taskRef.getForUpdate();
            
            // set the response and run the task
            task.setGranted(response);
            task.run();

            // clean up the task in the data store
            dataService.removeObject(task);
        }
    }

    static class RunSecureTask implements Task, ManagedObject, Serializable {
        private SecureTask task;
        private ResourceMap granted;

        public RunSecureTask(SecureTask task) {
            // wrap the task if it is a mananged object
            if (task instanceof ManagedObject) {
                task = new SecureTaskWrapper(task);
            }

            this.task = task;
        }

        public void setGranted(ResourceMap granted) {
            this.granted = granted;
        }

        public void run() throws Exception {
            task.run(granted);
        }
    }

    static class SecureTaskWrapper implements SecureTask {
        private ManagedReference<SecureTask> taskRef;

        public SecureTaskWrapper(SecureTask task) {
            taskRef = AppContext.getDataManager().createReference(task);
        }

        public void run(ResourceMap granted) {
            taskRef.get().run(granted);
        }
    }
}
