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
package org.jdesktop.wonderland.common.security;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An action that a user can perform on a resource.  This is the common
 * superclass of all actions in the Wonderland system.  An action can be
 * any class that extends Action and has a public, no-argument construtor.
 * <p>
 * Actions are typically referred to by class, so are not designed to
 * be stateful or mutable.
 *
 * @author jkaplan
 */
public abstract class Action implements Serializable {
    /** logger */
    private static final Logger logger =
            Logger.getLogger(Action.class.getName());

    /** actions are uniquely identified by name */
    private String name;

    /** the class of the parent for this action */
    private Class parentClass;

    /** an instance of the parent, created lazily */
    private transient Action parent;

    /** the display name for this action */
    private String displayName;

    /** the tool-tip for this action */
    private String toolTip;

    /**
     * Create a new top-level action with the given name
     * @param name the name of the action to created
     */
    protected Action(String name) {
        this (name, null);
    }

    /**
     * Create a new action with the given name and parent
     * @param name the name of the action to create
     * @param parentClass the class of this action's parent
     */
    protected Action(String name, Class parentClass) {
        this (name, parentClass, null, null);
    }

    /**
     * Create a new action with the given name and parent
     * @param name the name of the action to create
     * @param parentClass the class of this action's parent
     */
    protected Action(String name, Class parentClass, String displayName,
                  String toolTip)
    {
        this.name        = name;
        this.parentClass = parentClass;
        this.displayName = displayName;
        this.toolTip     = toolTip;
    }

    /**
     * Get this action's name
     * @return the action's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get this actions parent
     * @return this action's parent, or null if this action is the top-level
     */
    public synchronized Action getParent() {
        if (parent != null) {
            return parent;
        }

        if (getParentClass() != null) {
            parent = getInstance(getParentClass());
        }

        return parent;
    }

    /**
     * Get the class of this action's parent.  By default, an action is assigned
     * the same value as its parent.
     * @return the class of this action's parent, or null if this action
     * is the top-level
     */
    public Class getParentClass() {
        return parentClass;
    }

    /**
     * Get the name to display for this action. 
     * @return the display name, or null if the display name is the
     * same as the name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the tool tip associated with this action
     * @return the tool tip for this action, or null if the action has no
     * tool tip
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * Two actions are the same if they have the same name
     * @param obj the object to compare to
     * @return true if the object is an Action with the same name as this one
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Action other = (Action) obj;
        if ((this.name == null) ? (other.name != null) : 
            !this.name.equals(other.name))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * Instantiate an action class
     * @param class the action class to instantiate
     * @return the instantiated action
     */
    public static <T extends Action> T getInstance(Class<T> actionClazz) {
        try {
            return (T) actionClazz.newInstance();
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Error creating action", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Error creating action", ex);
        }

        return null;
    }
}
