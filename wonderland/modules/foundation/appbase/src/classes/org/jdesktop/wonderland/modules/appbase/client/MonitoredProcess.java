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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A process external to the JVM whose output is monitored via an internally created ProcessMonitor. This
 * ProcessMonitor sends output and exit values to the given ProcessReporter.
 *
 * Note: it is very important to monitor process output because on Solaris a process can hang if it's output is
 * not read by someone.
 *
 * @author deronj
 */
@ExperimentalAPI
public class MonitoredProcess {

    private static final Logger logger = Logger.getLogger(MonitoredProcess.class.getName());

    /** The name of the process */
    private String processName;
    /** The command to run and its arguments */
    private ArrayList<String> cmdAndArgs;
    /** A map of environment variable (name, value) pairs */
    private Map<String, String> env;
    /** The reporter used to report app output and exit status */
    private ProcessReporter reporter;
    /** The process */
    private Process process;
    /** The process monitor */
    private ProcessMonitor monitor;
    /** The exit value of the process. */
    private int exitValue = -2;

    /** 
     * Create a new instance of MonitoredProcess. Use the default reporter.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     */
    public MonitoredProcess(String processName, String cmdAndArgs) {
        this(processName, stringToArrayListString(cmdAndArgs), null, null);
    }

    /** 
     * Create a new instance of MonitoredProcess. Use the default reporter.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     */
    public MonitoredProcess(String processName, String[] cmdAndArgs) {
        this(processName, stringArrayToArrayListString(cmdAndArgs), null, null);
    }

    /** 
     * Create a new instance of MonitoredProcess. Use the default reporter.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     */
    public MonitoredProcess(String processName, ArrayList<String> cmdAndArgs) {
        this(processName, cmdAndArgs, null, null);
    }

    /** 
     * Create a new instance of MonitoredProcess. Use the default reporter.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param env A map of environment variable (name, value) pairs.
     */
    public MonitoredProcess(String processName, String cmdAndArgs,
            Map<String, String> env) {
        this(processName, stringToArrayListString(cmdAndArgs), env, null);
    }

    /** 
     * Create a new instance of MonitoredProcess. Use the default reporter.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param env A map of environment variable (name, value) pairs.
     */
    public MonitoredProcess(String processName, String[] cmdAndArgs,
            Map<String, String> env) {
        this(processName, stringArrayToArrayListString(cmdAndArgs), env, null);
    }

    /** 
     * Create a new instance of MonitoredProcess. Use the default reporter.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param env A map of environment variable (name, value) pairs.
     */
    public MonitoredProcess(String processName, ArrayList<String> cmdAndArgs,
            Map<String, String> env) {
        this(processName, cmdAndArgs, env, null);
    }

    /** 
     * Create a new instance of MonitoredProcess. 
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param reporter The reporter used to report app output and exit status.
     */
    public MonitoredProcess(String processName, String cmdAndArgs,
            ProcessReporter reporter) {
        this(processName, stringToArrayListString(cmdAndArgs), null,
                reporter);
    }

    /** 
     * Create a new instance of MonitoredProcess.
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param reporter The reporter used to report app output and exit status.
     */
    public MonitoredProcess(String processName, String[] cmdAndArgs,
            ProcessReporter reporter) {
        this(processName, stringArrayToArrayListString(cmdAndArgs), null,
                reporter);
    }

    /** 
     * Create a new instance of MonitoredProcess.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param reporter The reporter used to report app output and exit status.
     */
    public MonitoredProcess(String processName, ArrayList<String> cmdAndArgs,
            ProcessReporter reporter) {
        this(processName, cmdAndArgs, null, reporter);
    }

    /** 
     * Create a new instance of MonitoredProcess.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param env A map of environment variable (name, value) pairs.
     * @param reporter The reporter used to report app output and exit status.
     */
    public MonitoredProcess(String processName, String cmdAndArgs,
            Map<String, String> env, ProcessReporter reporter) {
        this(processName, stringToArrayListString(cmdAndArgs), env, reporter);
    }

    /** 
     * Create a new instance of MonitoredProcess.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param env A map of environment variable (name, value) pairs.
     * @param reporter The reporter used to report app output and exit status.
     */
    public MonitoredProcess(String processName, String[] cmdAndArgs,
            Map<String, String> env, ProcessReporter reporter) {
        this(processName, stringArrayToArrayListString(cmdAndArgs), env, reporter);
    }

    /** 
     * Create a new instance of MonitoredProcess.
     *
     * @param processName The name of the process.
     * @param cmdAndArgs The command to run and its arguments.
     * @param env A map of environment variable (name, value) pairs.
     * @param reporter The reporter used to report app output and exit status.
     */
    public MonitoredProcess(String processName, ArrayList<String> cmdAndArgs,
            Map<String, String> env, ProcessReporter reporter) {
        this.processName = processName;
        this.cmdAndArgs = cmdAndArgs;
        this.env = env;

        this.reporter = reporter;
        if (reporter == null) {
            this.reporter = ProcessReporterFactory.getFactory().create(processName);
        }
    }

    /**
     * Forcibly kill the process and clean up resources.
     */
    public synchronized void cleanup() {
        logger.warning("Shutting down process  " + processName);

        if (process != null) {
            logger.warning("Shutting down process object for process " + processName);
            process.destroy();
            process = null;
        }
        if (monitor != null) {
            exitValue = monitor.getExitValue();
            monitor.cleanup();
            monitor = null;
        }
        if (reporter != null) {
            reporter.cleanup();
            reporter = null;
        }
    }

    /**
     * Launch the process. 
     *
     * @return false if the process cannot be started.
     */
    public boolean start() {
        if (cmdAndArgs == null || cmdAndArgs.size() <= 0) {
            cleanup();
            return false;
        }

        // Create a process builder
        ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
        pb.redirectErrorStream(true);

        // Initialize environment (if necessary)
        if (env != null) {
            Map<String, String> pbEnv = pb.environment();
            for (String envName : env.keySet()) {
                String envValue = env.get(envName);
                pbEnv.put(envName, envValue);
            }
        }

        // Now start the process
        try {
            process = pb.start();
        } catch (IOException ex) {
            cleanup();
            return false;
        }
        if (process == null) {
            cleanup();
            return false;
        }

        // Finally, create the process monitor
        monitor = new ProcessMonitor(process, processName, reporter);

        return true;
    }

    /**
     * Return the process exit value.
     */
    public int getExitValue () {
        if (monitor != null) {
            return monitor.getExitValue();
        }
        return exitValue;
    }

    /**
     * Convert a string containing space-separated tokens into an ArrayList of the separate tokens.
     *
     * @param str The input string.
     */
    private static ArrayList<String> stringToArrayListString(String str) {
        ArrayList<String> als = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(str);

        int numTokens = tok.countTokens();
        if (numTokens <= 0) {
            return null;
        }

        for (int i = 0; i < numTokens; i++) {
            String s = tok.nextToken();
            als.add(s);
        }

        return als;
    }

    /**
     * Convert an array of strings into an ArrayList of the same strings.
     *
     * @param str The input string array.
     */
    private static ArrayList<String> stringArrayToArrayListString(String[] str) {
        ArrayList<String> als = new ArrayList<String>();
        for (int i = 0; i < str.length; i++) {
            als.add(str[i]);
        }

        return als;
    }
}
