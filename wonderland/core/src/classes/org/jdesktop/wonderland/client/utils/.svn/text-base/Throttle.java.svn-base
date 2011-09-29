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
package org.jdesktop.wonderland.client.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A throttle can be used to schedule tasks with a fixed maximum rate.  For
 * example, messages to the server can be scheduled with a 500ms delay,
 * meaning at most two would be sent per second.
 * <p>
 * When tasks are scheduled more frequently than the rate of the throtte,
 * some tasks will be dropped. In all cases, the most recently submitted task
 * will be executed, but older tasks will be dropped.
 * <p>
 * All throttles in the system share a single, underlying thread pool.  This
 * makes them efficient, but means that individual tasks should happen
 * quickly.  Scheduling too many slow tasks will slow down the thread pool
 * for the whole system.
 *
 * @author jkaplan
 */
public class Throttle {
    /** the current rate */
    private long rate;

    /** the timeunit of the current rate */
    private TimeUnit rateUnit;

    /** the last time a task was executed, in milliseconds */
    private long lastExecution;

    /** the next task to execute, or null if there is no delayed task */
    private Runnable nextTask;

    /** the currently schedule task, or null if there is no scheduled task */
    private ScheduledFuture scheduled;

    /**
     * Create a new throttle that executes at most <rate> times every
     * <timeunit>.  For example, if the rate is 2 and the TimeUnit is
     * SECONDS, this will create a throttle that executes at most 2 times
     * per second, or every 500 milliseconds.  If the rate is 2 and the
     * TimeUnit is MINUTES, this will create a throttle that executes at most
     * 2 times per minute, or every 30 seconds.
     *
     * @param rate the rate to schedule at
     * @param rateUnit the unit of the given rate
     */
    public Throttle(long rate, TimeUnit rateUnit) {
        this.rate = rate;
        this.rateUnit = rateUnit;
    }

    /**
     * Set the rate of this throttle
     * @param rate the rate to schedule at
     * @param rateUnit the unit of the given rate
     */
    public synchronized void setRate(long rate, TimeUnit rateUnit) {
        this.rate = rate;
        this.rateUnit = rateUnit;
    }

    /**
     * Schedule a task.  The task will execute immediately if no tasks
     * have executed for more than the requested period.  If a task
     * has executed within the requested period, this task will be scheduled
     * for execution once the delay has expired.
     * @param task the task to schedule
     * @return true if the task was run immediately, or false if it was
     * delayed
     */
    public synchronized boolean schedule(Runnable task) {
        // if there is already a task waiting to execute, just replace
        // it with the current task.  The current task will execute next
        // time the timer comes up.
        if (nextTask != null) {
            nextTask = task;
            return false;
        }

        // there is no waiting task.  See how long it has been since we
        // last executed
        long delay = System.currentTimeMillis() - lastExecution;
        if (delay > getMinDelayMillis()) {
            // the delay was greater than the minimum, so we are ok
            // to execute this task now
            task.run();
            lastExecution = System.currentTimeMillis();
            return true;
        }

        // if we get here, it means we need to schedule this task to run
        // later.  The value of the delay variable will tell us when
        // to run it.
        long runAt = getMinDelayMillis() - delay;
        nextTask = task;
        getExecutor().schedule(new Runnable() {
            public void run() {
                synchronized (Throttle.this) {
                    // run the task
                    nextTask.run();

                    // set the execution time
                    lastExecution = System.currentTimeMillis();

                    // remove the scheduled task
                    scheduled = null;
                    nextTask = null;
                }
            }
        }, runAt, TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * Clears any outstanding tasks, and cancels any pending sends
     */
    public synchronized void clear() {
        nextTask = null;

        if (scheduled != null) {
            scheduled.cancel(false);
            scheduled = null;
        }
    }

    /**
     * Calculate the delay between scheduling in milliseconds
     * @return the delay in milliseconds
     */
    private synchronized long getMinDelayMillis() {
        // get the length of one of these units in milliseconds
        long millis = rateUnit.toMillis(1);

        // divide by the rate
        return millis / rate;
    }

    /**
     * Get the executor
     * @return the executor
     */
    private static final ScheduledExecutorService getExecutor() {
        return SingletonHolder.EXECUTOR;
    }

    /**
     * Holder for the singleton therad pool
     */
    private static final class SingletonHolder {
        private static final ScheduledExecutorService EXECUTOR =
                Executors.newScheduledThreadPool(2);
    }
}
