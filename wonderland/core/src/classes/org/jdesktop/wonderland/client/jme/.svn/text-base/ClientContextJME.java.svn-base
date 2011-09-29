/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

import java.util.HashMap;
import org.jdesktop.mtgame.CollisionSystem;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.PhysicsSystem;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.mtgame.processor.WorkProcessor;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.scenemanager.SceneManager;

/**
 * A subclass of ClientContext which adds JME client specific context accessors.
 * 
 * @author paulby
 */
public class ClientContextJME extends ClientContext {

    private static WorldManager worldManager;
    private static HashMap<ServerSessionManager, HashMap<String, PhysicsSystem>> physicsSystems = new HashMap();
    private static HashMap<ServerSessionManager, HashMap<String, CollisionSystem>> collisionSystems = new HashMap();

    private static WorkProcessor workProcessor;

    private static SceneWorker sceneWorker;

    private static JmeClientMain clientMain;

    static {
        worldManager = new WorldManager("Wonderland");
        sceneWorker = new SceneWorker(worldManager);
        InputManager3D.getInputManager(); // worldManager must be instantiated first
        SceneManager.getSceneManager();
        System.setProperty("BafCacheDir", "file://"+getUserDirectory().getAbsolutePath());

    }

    /**
     * Get the view manager
     * @return
     */
    public static ViewManager getViewManager() {
        return ViewManager.getViewManager();
    }
    
    /**
     * Get the mtgame world manager
     * @return
     */
    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public static AvatarRenderManager getAvatarRenderManager() {
        return AvatarRenderManager.getAvatarRenderManager();
    }

    public static InputManager getInputManager() {
        return InputManager.inputManager();
    }

    public static void addPhysicsSystem(ServerSessionManager session, String name, PhysicsSystem physicsSystem) {
//        System.err.println("SESSION addPhysics "+session);
        synchronized(physicsSystems) {
            HashMap<String, PhysicsSystem> sessionPhy = physicsSystems.get(session);
            if (sessionPhy==null) {
                sessionPhy = new HashMap();
                physicsSystems.put(session, sessionPhy);
            }
            sessionPhy.put(name, physicsSystem);
        }
    }

    /**
     * Return the PhysicsSystem for the specified session with the given name.
     * Returns null if there is no system with the given name
     * @param session
     * @param name
     * @return
     */
    public static PhysicsSystem getPhysicsSystem(ServerSessionManager session, String name) {
        synchronized(physicsSystems) {
//        System.err.println("SESSION getPhysics "+((Object)session).toString()+"  "+physicsSystems.get(session));
            HashMap<String, PhysicsSystem> sessionPhy = physicsSystems.get(session);
            if (sessionPhy==null)
                return null;

            return sessionPhy.get(name);
        }
    }

    public static void addCollisionSystem(ServerSessionManager session, String name, CollisionSystem collisionSystem) {
//        System.err.println("SESSION addColl"+session);
        synchronized(collisionSystems) {
            HashMap<String, CollisionSystem> sessionCollision = collisionSystems.get(session);
            if (sessionCollision==null) {
                sessionCollision = new HashMap();
                collisionSystems.put(session, sessionCollision);
            }
            sessionCollision.put(name, collisionSystem);
        }
    }

    /**
     * Return the PhysicsSystem for the specified session with the given name.
     * Returns null if there is no system with the given name
     * @param session
     * @param name
     * @return
     */
    public static CollisionSystem getCollisionSystem(ServerSessionManager session, String name) {
//        System.err.println("SESSION getColl "+session);
        synchronized(collisionSystems) {
            HashMap<String, CollisionSystem> sessionCollisions = collisionSystems.get(session);
            if (sessionCollisions==null)
                return null;

            return sessionCollisions.get(name);
        }
    }

    public static SceneWorker getSceneWorker() {
        return sceneWorker;
    }

    /**
     * Remove all the physics systems for this session
     * @param session
     */
    static void removeAllPhysicsSystems(ServerSessionManager session) {
        synchronized(physicsSystems) {
            physicsSystems.remove(session);
        }
    }
    
    /**
     * Remove all the collision systems for this session
     * @param session
     */
    static void removeAllCollisionSystems(ServerSessionManager session) {
        synchronized(collisionSystems) {
            collisionSystems.remove(session);
        }
    }

    /**
     * Get the JME client main object
     * @return the jme main client object
     */
    public static JmeClientMain getClientMain() {
        return clientMain;
    }

    /**
     * Set the JME client main object
     * @param clientMain the main object
     */
    static void setClientMain(JmeClientMain clientMain) {
        ClientContextJME.clientMain = clientMain;
    }
}
