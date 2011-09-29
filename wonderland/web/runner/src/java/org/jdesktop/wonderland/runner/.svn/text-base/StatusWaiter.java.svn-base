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
package org.jdesktop.wonderland.runner;

import java.util.logging.Logger;
import org.jdesktop.wonderland.runner.Runner;

/**
 * A listener that will block until the runner it is listening to reaches
 * a certain status.  The listener will always return if the runner 
 * goes into the ERROR status.
 * @author jkaplan
 */
public class StatusWaiter implements Runner.RunnerStatusListener {
    private static final Logger logger =
            Logger.getLogger(StatusWaiter.class.getName());
    
    private Runner runner;
    private Runner.Status currentStatus;
    private Runner.Status targetStatus;
    
    /**
     * Create a new WaitForStatusListener for the given runner
     * @param runner the runner to listen for status on
     */
    public StatusWaiter(Runner runner) {
        this (runner, null);
    }
    
    /**
     * Create a new StatusWaiter for the given runner and target status
     * @param runner the runner to listen for status on
     * @param targetStatus the status to wait for if <code>wait()</code> is
     * called.
     */
    public StatusWaiter(Runner runner, Runner.Status targetStatus) {
        this.runner = runner;
        this.targetStatus = targetStatus;
        
        // register for any status changes.  Ignore runners that are
        // not runnable -- their status never changes
        if (runner.isRunnable()) {
            currentStatus = runner.addStatusListener(this);
        }
    }
    
    /**
     * Notification that the status has changed
     * @param runner the runner that had the status change
     * @param currentStatus the new status
     */
    public synchronized void statusChanged(Runner runner, 
                                           Runner.Status currentStatus)
    {
        this.currentStatus = currentStatus;
        notify();
    }

    /**
     * Wait for the target status given in the constructor.  Throws
     * <code>IllegalStateException</code> if the target status was not
     * given in the constructor.
     * @return the actual status that was returned
     */
    public Runner.Status waitFor() throws InterruptedException {
        if (targetStatus == null) {
            throw new IllegalStateException("No target status specified.");
        }
        return waitFor(targetStatus);
    }
    
    
    /**
     * Wait for the runner's status to be the given status
     * @param status the status to wait for
     * @return the actual status.  This will either be the value
     * that was specified in status or ERROR if the runner had an error.
     * @throws InterruptedException if the thread is interrupted
     */
    public synchronized Runner.Status waitFor(Runner.Status status) 
            throws InterruptedException
    {
        // if the runner isn't runnable, its status will never change,
        // so don't wait for it
        if (!runner.isRunnable()) {
            return runner.getStatus();
        }

        while (currentStatus != status && 
               currentStatus != Runner.Status.ERROR)
        {
            wait();
        }
        
        return currentStatus;
    }
}
