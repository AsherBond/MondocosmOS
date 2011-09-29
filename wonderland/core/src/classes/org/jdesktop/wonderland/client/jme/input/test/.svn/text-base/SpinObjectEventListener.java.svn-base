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

import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.processor.RotationProcessor;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import com.jme.scene.Node;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An event listener which toggles the spinning state of an object when an button press event occurs over it.
 * To use this in a scene just add this to any entities you wish to make input sensitive via
 * <code>this.addToEntity</code>.
 * <br><br>
 * Example Usage:
 * <br><br>
 * <code>
 * SpinObjectEventListener spinListener = new SpinObjectEventListener();
 * <br>
 * spinListener.addToEntity(entity);
 * </code>
 *
 * @author deronj
 */

@ExperimentalAPI
public class SpinObjectEventListener extends EventClassListener {

    /**
     * Consume only mouse button events.
     */
    @Override
    public Class[] eventClassesToConsume () {
	return new Class[] { MouseButtonEvent3D.class };
    }

    // Note: we don't override computeEvent because we don't do any computation in this listener.

    /**
     * {@inheritDoc}
     */
    @Override
    public void commitEvent (Event event) {
	if (!((MouseButtonEvent3D)event).isPressed()) {
	    return;
	}
	Entity entity = event.getEntity();
	SpinProcessor spinner = (SpinProcessor) entity.getComponent(SpinProcessor.class);
	if (spinner != null) {
	    // Stop the spinning
	    spinner.stop();
	    entity.removeComponent(SpinProcessor.class);
	} else {
	    // Start the spinning
	    try {
		entity.addComponent(SpinProcessor.class, new SpinProcessor(entity));
	    } catch (InstantiationException ex) {
		// Can't spin an unspinnable entity
	    }
	}
    }

    /** 
     * A component which animates an object to spin around the Y axis.
     */
    private static class SpinProcessor extends RotationProcessor {
        
	SpinProcessor (Entity entity) throws InstantiationException {
	    super("Spinner", ClientContextJME.getWorldManager(), getNode(entity), 10);
	}

	void stop () {
	    setArmingCondition(null);
	}

	static Node getNode (Entity entity) throws InstantiationException {
	    RenderComponent renderComp = (RenderComponent) entity.getComponent(RenderComponent.class);
	    if (renderComp == null) {
		throw new InstantiationException("Enity has no render component");
	    }
	    Node node = renderComp.getSceneRoot();
	    if (node == null) {
		throw new InstantiationException("Entity has no scene graph");
	    }
	    return node;
	}
    }
}

