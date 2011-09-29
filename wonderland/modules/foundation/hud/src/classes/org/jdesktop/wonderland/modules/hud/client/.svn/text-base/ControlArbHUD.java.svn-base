/*
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
package org.jdesktop.wonderland.modules.hud.client;

import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArbAppFocus;

/**
 * A simple control arbiter which always enables control.
 *
 * @author nsimpson
 */
public class ControlArbHUD extends ControlArbAppFocus {

    private InputManager inputManager;

    public ControlArbHUD() {
        inputManager = InputManager3D.getInputManager();
        setReleaseWithAll(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasControl() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void takeControl() {
        // Assign focus to the app
        inputManager.addKeyMouseFocus(new Entity[]{app.getFocusEntity()});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseControl() {
        // Remove focus from the app
        // Fix issue 534
        // - don't allow shift-click to release control of HUD windows
        //inputManager.removeKeyMouseFocus(new Entity[]{app.getFocusEntity()});
    }
}
