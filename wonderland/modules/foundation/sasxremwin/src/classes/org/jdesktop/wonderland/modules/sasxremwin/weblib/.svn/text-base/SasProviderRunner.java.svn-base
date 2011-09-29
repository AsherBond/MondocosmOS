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

package org.jdesktop.wonderland.modules.sasxremwin.weblib;

import java.util.Properties;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.Runner.Status;
import org.jdesktop.wonderland.runner.RunnerException;

/**
 * Common interface implemented by the SasProviderRunner and the
 * SasProviderRemoteRunner
 * @author jkaplan
 */
interface SasProviderRunner extends Runner {
    /**
     * Set the status of this provider
     * @param status the status to set
     */
    public void setStatus(Status status);

    /**
     * Call the superclasses' start method
     * @param props the properties
     */
    public void startSuper(Properties props) throws RunnerException;

    /**
     * Call the superclasses' stop method
     */
    public void stopSuper();
}
