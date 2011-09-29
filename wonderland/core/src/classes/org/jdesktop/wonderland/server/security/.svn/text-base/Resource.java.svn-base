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
package org.jdesktop.wonderland.server.security;

import com.sun.sgs.kernel.ComponentRegistry;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.security.Action;

/**
 * A resource that may have protected access.
 * @author jkaplan
 */
public interface Resource {
    /** the result of a resource access query */
    public enum Result {
        GRANT,   // request was acceptable, immediately grant access
        DENY,    // request was unacceptable, immediately deny access
        SCHEDULE; // the request could not be resolved in this transaction,
                 // schedule outside a transaction


        /**
         * Combine 2 results.
         * @param r1
         * @param r2
         * @return
         */
        public static Result combine(Result r1, Result r2) {
            if (r1==DENY || r2==DENY)
                return DENY;
            if (r1==SCHEDULE || r2==SCHEDULE)
                return SCHEDULE;

            return GRANT;
        }
    }

    /**
     * Get the unique identifier for this resource
     * @return a unique identifier for the resource.  The identifier should
     * be unique across all resources in the server.
     */
    public String getId();

    /**
     * Determine if the given identity can access the given action for
     * this resource.  This method must be called within a transaction.
     * @param identity the identity requesting access
     * @param action the action
     * @return the result of processing, either grant, deny or schedule
     */
    public Result request(WonderlandIdentity identity, Action action);

    /**
     * Determune if the given identity can access the given action for this
     * resource.  This method is never called in a transaction.  It should
     * block until a result can be determined.
     * @param identity the identity requesting access
     * @param action the action
     * @param registry the component registry used by this service
     * @return true if access is granted, or false if not
     */
    public boolean request(WonderlandIdentity identity, Action action,
                           ComponentRegistry registry);


}
