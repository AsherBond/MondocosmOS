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
 * This process reporter factory creates process reporters that report to the Wonderland client logger.
 *
 * @author deronj
 */
@ExperimentalAPI
public class ProcessReporterFactoryLogger extends ProcessReporterFactory {

    /**
     * Create a new reporter of this type.
     *
     * @param processName the name of the process for which to report.
     * @return A process reporter which reports output and exit status for the given process.
     */
    public ProcessReporter create(String processName) {
        ProcessReporter reporter = reporterMap.get(processName);
        if (reporter == null) {
            reporter = new ProcessReporterLogger(processName);
            reporterMap.put(processName, reporter);
        }
        return reporter;
    }
}
