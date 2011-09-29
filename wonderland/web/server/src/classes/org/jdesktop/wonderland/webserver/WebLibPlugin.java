/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.webserver;

/**
 * Plugin to the weblib system.  The initialize method will be called when
 * the weblib is deployed.
 * <p>
 * WebLib plugins must be annotated with the @Plugin annotation to be
 * recognized by the system.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface WebLibPlugin {
    /**
     * Called when the library is initialized.
     * @param server the server this plugin is running in
     */
    public void initialize(WonderlandAppServer server);
}
