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
package org.jdesktop.wonderland.client.jme;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.util.Properties;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 * An extension of CellClientSession to work with JME.  Primarily, this
 * uses a JmeCellCache instead of a regular cell cache
 * @author jkaplan
 */
public class JmeClientSession extends CellClientSession {
    private JmeCellCache jmeCellCache;
    private static final String INITIAL_POSITION_PREFIX = "view.initial.";

    public JmeClientSession(ServerSessionManager manager,
                            WonderlandServerInfo serverInfo,
                            ClassLoader loader)
    {
        super(manager, serverInfo, loader);
    }

    @Override
    public JmeCellCache getCellCache() {
        return jmeCellCache;
    }

    public void setInitialPosition(Vector3f initialPosition, Quaternion initialLook) {
        Properties props = getCellCacheProperties();
        
        // write the positions into cell properties
        if (initialPosition != null) {
            props.setProperty(INITIAL_POSITION_PREFIX + "x", 
                              String.valueOf(initialPosition.getX()));
            props.setProperty(INITIAL_POSITION_PREFIX + "y", 
                              String.valueOf(initialPosition.getY()));
            props.setProperty(INITIAL_POSITION_PREFIX + "z", 
                              String.valueOf(initialPosition.getZ()));
        }
        
        if (initialLook != null) {
            float[] angles = initialLook.toAngles(new float[3]);
            props.setProperty(INITIAL_POSITION_PREFIX + "rotx", 
                              String.valueOf(angles[0]));
            props.setProperty(INITIAL_POSITION_PREFIX + "roty", 
                              String.valueOf(angles[1]));
            props.setProperty(INITIAL_POSITION_PREFIX + "rotz", 
                              String.valueOf(angles[2]));
        }
    }

    // createCellCache is called in the constructor fo CellClientSession
    // so the cellCache will be set before we proceed
    @Override
    protected JmeCellCache createCellCache() {
        jmeCellCache = new JmeCellCache(this, getClassLoader());
        getCellCacheConnection().addListener(jmeCellCache);
        return jmeCellCache;
    }
}
