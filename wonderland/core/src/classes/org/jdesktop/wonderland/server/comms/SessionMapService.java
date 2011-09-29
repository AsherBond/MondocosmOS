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
package org.jdesktop.wonderland.server.comms;

import java.util.logging.Level;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.ObjectNotFoundException;
import com.sun.sgs.impl.sharedutil.LoggerWrapper;
import com.sun.sgs.impl.util.AbstractService;
import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.service.DataService;
import com.sun.sgs.service.TransactionProxy;

import com.sun.sgs.impl.service.session.ClientSessionWrapper;

import java.io.Serializable;
import java.math.BigInteger;

import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author jprovino
 */
public class SessionMapService extends AbstractService implements SessionMapManager {
    /** The name of this class. */
    private static final String NAME = SessionMapService.class.getName();

    /** The package name. */
    private static final String PKG_NAME = "org.jdesktop.wonderland.server.comms";

    /** The logger for this class. */
    private static final LoggerWrapper logger =
        new LoggerWrapper(Logger.getLogger(PKG_NAME));

    /** The name of the version key. */
    private static final String VERSION_KEY = PKG_NAME + ".service.version";

    /** The major version. */
    private static final int MAJOR_VERSION = 1;

    /** The minor version. */
    private static final int MINOR_VERSION = 0;

    public SessionMapService(Properties props, ComponentRegistry registry, 
	   TransactionProxy txnProxy) {

        super(props, registry, txnProxy, logger);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void doReady() {
        logger.log(Level.CONFIG, "SessionMapService is ready");
    }

    @Override
    protected void doShutdown() {
        // nothing to do
    }

    @Override
    protected void handleServiceVersionMismatch(Version oldVersion,
                                                Version currentVersion) {
        throw new IllegalStateException(
                    "unable to convert version:" + oldVersion +
                    " to current version:" + currentVersion);
    }

    public WonderlandClientID getClientID(BigInteger sessionID) {
	DataService ds = txnProxy.getService(DataService.class);

	ManagedReference<ClientSession> sessionRef;

	try {
	    sessionRef = (ManagedReference<ClientSession>) 
	        ds.createReferenceForId(sessionID);
	} catch (ObjectNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
	    return null;
	}

	if (sessionRef == null) {
	    return null;
	}
      
        try {
            return new WonderlandClientID(sessionRef);
        } catch (ObjectNotFoundException onfe) {
            // no such object exists
            logger.logThrow(Level.FINE, onfe, "No client for ID " + sessionID);
            return null;
        }
    }

}
