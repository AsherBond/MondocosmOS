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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;

/**
 * A user input control arbiter which is a complete no-op. Used by SAS providers.
 *
 * @author deronj
 */
@ExperimentalAPI
public class ControlArbNull extends ControlArb {

    /** {@inheritDoc} */
    @Override
    public void takeControl() {}

    /** {@inheritDoc} */
    @Override
    public void releaseControl() {}

    /** {@inheritDoc} */
    @Override
    public boolean hasControl() { return false; }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String[] getControllers() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deliverEvent(Window2D window, KeyEvent event) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void deliverEvent(Window2D window, MouseEvent event) {}
}
