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
package org.jdesktop.wonderland.client.jme.input.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A test listener for key events. Add this to an entity and it will log all key events that
 * occur over the entity.
 *
 * @author deronj
 */

@ExperimentalAPI
public class KeyEvent3DLogger extends EventClassListener {

    private static final Logger logger = Logger.getLogger(KeyEvent3DLogger.class.getName());

    static {
	logger.setLevel(Level.INFO);
    }

    private String name;

    /**
     * Create an instance of KeyEvent3DLogger.
     */
    public KeyEvent3DLogger () {
	this(null);
    }

    /**
     * Create an instance of KeyEvent3DLogger.
     * @param name The name of the logger.
     */
    public KeyEvent3DLogger (String name) {
	this.name = name;
    }

    /**
     * Consume all key events.
     */
    @Override
    public Class[] eventClassesToConsume () {
	return new Class[] { KeyEvent3D.class };
    }

    @Override
    public void commitEvent (Event event) {
	StringBuffer sb = new StringBuffer();
	if (name != null) {
	    sb.append(name + ": ");
	}
	sb.append("Received key event, event = " + event + ", entity = " + event.getEntity());
	logger.info(sb.toString());
    }
}

