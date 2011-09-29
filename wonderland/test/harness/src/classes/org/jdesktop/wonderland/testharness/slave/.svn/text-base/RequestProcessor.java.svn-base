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
package org.jdesktop.wonderland.testharness.slave;

import java.util.Properties;
import org.jdesktop.wonderland.testharness.common.TestRequest;
import org.jdesktop.wonderland.testharness.slave.SlaveMain.ReplySender;

/**
 * Interface for processing test requests. RequestProcessors are created
 * by reflection, so they should have a default, no-argument constructor.
 * @author jkaplan
 */
public interface RequestProcessor {
    /**
     * Get the name of this processor
     * @return this processor's name
     */
    public String getName();

    /**
     * Initialize with the given properties
     * @param username the username to log in with
     * @param props the properties to initialize with
     * @throws ProcessingException if there is an error initializing,
     * such as a problem logging in
     */
    public void initialize(String username, Properties props, ReplySender replyHandler)
            throws ProcessingException;

    /**
     * Handle a request
     * @param request the request to process
     * @throws ProcessingException if there is an error processing
     */
    public void processRequest(TestRequest request)
            throws ProcessingException;

    /**
     * Destroy this processor, freeing up any resources in use
     */
    public void destroy();
}
