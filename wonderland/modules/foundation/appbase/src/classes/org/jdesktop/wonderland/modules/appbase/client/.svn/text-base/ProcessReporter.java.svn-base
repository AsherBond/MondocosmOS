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

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An process reporter receives process output and exit status from a ProcessMonitor and reports it to the user
 * via an implementation method chosen by the concrete subclass. A ProcessReporter is created using the create
 * method of a ProcessReporterFactory.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class ProcessReporter {

    // TODO: concrete subclasses ProcessReporterMultiOutput, ProcessReporterMultiOutputSwing
    /** The name of the process. */
    protected String processName;

    /** 
     * Create a new instance of ProcessReporter.
     *
     * @param processName The name of the process on which to report.
     */
    ProcessReporter(String processName) {
        this.processName = processName;
    }

    /**
     * Clean up resources.
     */
    public void cleanup() {
    }

    /** 
     * Report to the user text output which the process generates.
     *
     * @param str A text string output by the process.
     */
    public abstract void output(String str);

    /** 
     * Report to the user the exit value of the process when it terminates.
     *
     * @param value The exit status value.
     */
    public abstract void exitValue(int value);
}
