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

import java.util.HashMap;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A process reporter factory is a singleton which creates process 
 * reporters to use for reporting process output to the user.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class ProcessReporterFactory {

    /** The ProcessReporterFactory singleton */
    protected static ProcessReporterFactory reporterFactory;
    /** A process-name-keyed map of process reporters which have been created */
    protected HashMap<String, ProcessReporter> reporterMap = new HashMap<String, ProcessReporter>();

    /** 
     * Returns the ProcessReporterFactory singleton.
     */
    public static ProcessReporterFactory getFactory() {
        if (reporterFactory == null) {
            // TODO: eventually replace with one which uses swing
            reporterFactory = new ProcessReporterFactoryLogger();
        }
        return reporterFactory;
    }

    /**
     * Create a new reporter of this type.
     * @param processName the name of the process for which to report.
     * @return A process reporter which reports output and exit status for the given process.
     */
    public abstract ProcessReporter create(String processName);
}
