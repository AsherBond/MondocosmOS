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
package org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.jme.input.test.KeyEvent3DLogger;

/**
 * FOR DEBUG.
 * <br>
 * A printer of entity trees.
 *
 * @author dj
 */
public class EntityLogger {

    private static final Logger logger = Logger.getLogger(KeyEvent3DLogger.class.getName());

    static {
	logger.setLevel(Level.INFO);
    }

    private static final String INDENT = "    ";

    static void logEntity (Entity entity) {
	
	logger.info("Entity = " + entity);
	logger.info("Entity contents = ");
	logger.info(getEntityContentsString(entity, 0));
    }

    private static String getEntityContentsString (Entity entity, int indentLevel) {
	StringBuffer sb = new StringBuffer();
	
	appendLine(sb, indentLevel, "sceneRoot = \n");
	RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
	if (rc != null) {
	    appendSceneGraph(sb, indentLevel, rc.getSceneRoot());
	}
	sb.append("\n");

	int numChildren = entity.numEntities();
	for (int i = 0; i < numChildren; i++) {
	    Entity child = entity.getEntity(i);
	    appendLine(sb, indentLevel, "==================");
	    appendLine(sb, indentLevel, "Child Entity " + i + ": " + child);
	    appendLine(sb, indentLevel, getEntityContentsString(child, indentLevel+1));
	    appendLine(sb, indentLevel, "==================");

	}

       	return sb.toString();
    }

    private static void appendSceneGraph (StringBuffer sb, int indentLevel, Node node) {
	if (node == null) return;
	appendLine(sb, indentLevel, node.toString());
	
	List<Spatial> childSpatials = node.getChildren();
	if (childSpatials == null) return;
	for (Spatial child : childSpatials) {
	    appendLine(sb, indentLevel, "------------------");
	    if (child instanceof Node) {
		appendLine(sb, indentLevel, "Child Node:  " + child.toString());
		appendSceneGraph(sb, indentLevel+1, (Node)child);
	    } else {
		appendLine(sb, indentLevel, "Child Leaf:  " + child.toString());
	    }
	    appendLine(sb, indentLevel, "------------------");
	}
    }

    private static void appendLine (StringBuffer sb, int indentLevel, String line) {
	for (int i = 0; i < indentLevel; i++) {
	    sb.append(INDENT);
	}
	sb.append(line + "\n");
    }
}
