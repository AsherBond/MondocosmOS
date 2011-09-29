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
package org.jdesktop.wonderland.modules.security.weblib.serverauthmodule;

import java.util.Map;

/**
 *
 * @author jkaplan
 */
public interface GroupResolver {
    /**
     * Set up this group resolver with the options from the given map.
     * The map is the one passed in to the SAM during intialization.
     * @param opts the map passed in to the initialization of the SAM
     */
    public void initialize(Map opts);
    
    /**
     * Get the set of groups associated with a user id.  At the minimum, this
     * should include a default group as specified in the options.
     * @param userId a non-null user id
     * @return a set of groups that the user is a member of
     */
    public String[] getGroupsForUser(String userId);
}
