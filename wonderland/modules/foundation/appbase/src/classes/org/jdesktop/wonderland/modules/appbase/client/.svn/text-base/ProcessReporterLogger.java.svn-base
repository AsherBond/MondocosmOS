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

import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An process reporter which reports to the Wonderland client logger.
 *
 * @author deronj
 */
@ExperimentalAPI
public class ProcessReporterLogger extends ProcessReporter {

    private static final Logger logger = Logger.getLogger(ProcessReporterLogger.class.getName());

    /** 
     * Create a new instance of ProcessReporter.
     *
     * @param processName The name of the process on which to report.
     */
    ProcessReporterLogger(String processName) {
        super(processName);
    }

    /** 
     * {@inheritDoc}
     */
    public void output(String str) {
        logger.info("Output from app " + processName + ": " + str);
    }

    /** 
     * {@inheritDoc}
     */
    public void exitValue(int value) {
        logger.info("Process " + processName + " exitted.");
        logger.info("exitValue = " + value);
    }
}
