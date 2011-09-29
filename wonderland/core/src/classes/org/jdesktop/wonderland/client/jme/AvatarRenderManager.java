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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 * Provides a mechansim for Avatar renderer modules to register themselves
 * with the core system.
 *
 * @author paulby
 */
public class AvatarRenderManager {
    private final Map<ServerSessionManager, Class<? extends CellRenderer>> renderers =
            new HashMap<ServerSessionManager, Class<? extends CellRenderer>>();
    private final Map<ServerSessionManager, Class<? extends ViewControls>> controls =
            new HashMap<ServerSessionManager, Class<? extends ViewControls>>();

    /**
     * Get the avatar render manager
     * @return
     */
    public static AvatarRenderManager getAvatarRenderManager() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Singleton, use getAvatarRenderManager() instead
     */
    private AvatarRenderManager() {
    }


    public void registerRenderer(ServerSessionManager session, 
                                 Class<? extends CellRenderer> rendererClass,
                                 Class<? extends ViewControls> controlClass)
    {
        renderers.put(session, rendererClass);
        controls.put(session, controlClass);
    }

    public void unregisterRenderer(ServerSessionManager session) {
        renderers.remove(session);
        controls.remove(session);
    }

    /**
     * Instantiate and return a renderer of the specified class
     *
     * @param rendererClass
     * @return
     */
    public CellRenderer createRenderer(ServerSessionManager session, Cell cell) throws RendererUnavailable {
       Class<? extends CellRenderer> clazz = renderers.get(session);

        try {
            Constructor con = clazz.getConstructor(Cell.class);
            return (CellRenderer) con.newInstance(cell);
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Failed to create Renderer because ", e);
            throw new RendererUnavailable(clazz.getName());
        }
    }

    public ViewControls createViewControls(ServerSessionManager session) {
        Class<? extends ViewControls> clazz = controls.get(session);

        try {
            return (ViewControls) clazz.newInstance();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Failed to create Control because ", e);
            throw new RuntimeException("Failed to create control");
        }

    }

    public class RendererUnavailable extends Exception {
        public RendererUnavailable(String msg) {
            super(msg);
        }
    }

    private static final class SingletonHolder {
        private static final AvatarRenderManager INSTANCE =
                new AvatarRenderManager();
    }
}
