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
package org.jdesktop.wonderland.modules.appbase.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * This class monitors the output and exit status of a external Process object. If a ProcessReporter is provided 
 * the monitor sends process output and exit status to this reporter for subsequent reporting to the user. Used by
 * MonitoredProcess.
 *
 * @author deronj
 */
@ExperimentalAPI
public class ProcessMonitor {

    /** We don't know the exit status because the process hasn't exitted. */
    private static int EXIT_STATUS_UNKNOWN_STILL_RUNNING = -1;

    /** The process being monitored */
    private Process process;
    /** The name of the process */
    private String processName;
    /** The reporter to use to report process info to the user */
    private ProcessReporter reporter;
    /** A thread which monitors and reports process output */
    private OutputMonitor outputMonitor;
    /** 
     * A thread which waits for the process to exit and reports
     * the exit status to the user.
     */
    private ExitMonitor exitMonitor;

    /**
     * The exit value of the app. This is positive if the app has exitted
     * and we know the value.
     */
    private int exitValue = EXIT_STATUS_UNKNOWN_STILL_RUNNING;

    /** 
     * Create a new instance of ProcessMonitor with the default reporter.
     *
     * @param process The process to monitor.
     */
    public ProcessMonitor(Process process, String processName) {
        this(process, processName, ProcessReporterFactory.getFactory().create(processName));
    }

    /** 
     * Create a new instance of ProcessMonitor with the specified reporter.
     *
     * @param process The process to monitor.
     * @param reporter The reporter with which to report to the user.
     */
    public ProcessMonitor(Process process, String processName, ProcessReporter reporter) {
        this.process = process;
        this.processName = processName;
        this.reporter = reporter;

        // Start both monitors for process
        outputMonitor = new OutputMonitor();
        exitMonitor = new ExitMonitor();
    }

    /** 
     * Force the process to be killed and clean up resources.
     */
    public void cleanup() {

        // This kills the process. Do this first.
        if (exitMonitor != null) {
            exitMonitor.cleanup();
            exitMonitor = null;
        }

        if (outputMonitor != null) {
            outputMonitor.cleanup();
            outputMonitor = null;
        }

        reporter = null;
        process = null;
    }

    /**
     * Return the process exit value.
     */
    public int getExitValue () {
        return exitValue;
    }

    /** The class which monitors process output */
    private class OutputMonitor implements Runnable {

        /** A reader of the process's stderr */
        private BufferedReader stderr;
        /** The thread loops until stopped */
        private boolean stop;
        /** The monitoring thread */
        private Thread thread;

        /** Create a new instance of OutputMonitor */
        private OutputMonitor() {
            stderr = new BufferedReader(new InputStreamReader(process.getInputStream()));
            thread = new Thread(this, "Output Monitor for process " + processName);
            thread.start();
        }

        /** Stop monitoring the process and clean up resources. */
        public void cleanup() {
            stop = true;
            try {
                thread.join();
            } catch (InterruptedException ex) {
            }
            thread = null;
        }

        /** The thread main loop */
        public void run() {
            try {
                String line = stderr.readLine();
                while (line != null && !stop) {
                    if (reporter != null) {
                        reporter.output(line);
                    }
                    line = stderr.readLine();
                }
            } catch (IOException e) {
                // IOExceptions sometimes occur for normal reasons
                // when a unix process exits. So ignore these.
            } finally {
                try {
                    stderr.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /** The class which monitors process exit status */
    private class ExitMonitor implements Runnable {

        /** The monitoring thread */
        private Thread thread;

        /** Create a new instance of ExitMonitor */
        private ExitMonitor() {
            thread = new Thread(this, "Exit Monitor for process " + processName);
            thread.start();
        }

        /** Force the process to be killed and clean up resources. */
        public void cleanup() {
            process.destroy();
            try {
                thread.join();
            } catch (InterruptedException ex) {
            }
            thread = null;
        }

        /** The thread main loop */
        public void run() {
            try {

                // Block thread until process exits
                exitValue = process.waitFor();

                if (reporter != null) {
                    reporter.exitValue(exitValue);
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
