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

import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.mtgame.processor.WorkProcessor;

/**
 * This utility is similar to SwingWorker, it provides addWorker methods to
 * register work to be executed by MtGame during either commit of compute phase.
 *
 * @author paulby
 */
public class SceneWorker {

    private static WorkProcessor workProcessor;

    SceneWorker(WorldManager worldManager) {
        workProcessor = new WorkProcessor("GlobalWorkProcessor", worldManager);
        Entity entity = new Entity("GlobalEntity");
        entity.addComponent(WorkProcessor.class, workProcessor);
        ClientContextJME.getWorldManager().addEntity(entity);
    }

    /**
     * Add some work to be processed during the commit phase. This is the rendering
     * thread so the work should complete as soon as possible.
     *
     * @param work
     */
    public static void addWorker(WorkProcessor.WorkCommit work) {
        workProcessor.addWorker(work);
    }

    /**
     * Add some work to be processed during the compute phase.
     * @param work
     */
    public static void addWorker(WorkProcessor.WorkCompute work) {
        workProcessor.addWorker(work);
    }
}
