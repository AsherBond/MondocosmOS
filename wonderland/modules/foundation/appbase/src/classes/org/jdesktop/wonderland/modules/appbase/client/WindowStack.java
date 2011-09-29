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
package org.jdesktop.wonderland.modules.appbase.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The stack of visible, non-coplanar windows of an app. Each window in the stack has a unique
 * zOrder determined by its position in the stack. The topmost visible window has zOrder = 0, 
 * and zOrders increase from top to bottom. The zOrder of the bottommost visible window is 
 * getNumWindows() - 1. 
 *
 * @author deronj
 */
@ExperimentalAPI
class WindowStack {

    /** 
     * The list of stacked windows. Windows in the list appear in top to bottom order.
     * The topmost visible window has zOrder = 0, and zOrders increase from top to bottom. The zOrder of the 
     * bottommost window is getNumWindows() - 1. 
     */
    protected LinkedList<Window2D> stack = new LinkedList<Window2D>();
 
    /**
     * Deallocate resources.
     */
    public synchronized void cleanup() {
        stack.clear();
    }

    /**
     * Add a new window to the top of the stack. Does nothing if the window is not visible or is coplanar.
     * @param window The window to be added.
     */
    public synchronized void add(Window2D window) {
        if (window == null || !window.isVisibleApp() || window.isCoplanar()) return;
        stack.addFirst(window);
    }

     /**
      * Remove the given window from the stack. Does nothing if the window is not visible or is coplanar.
      * @param window The window to be removed.
      */
    public synchronized void remove(Window2D window) {
        if (window == null || window.isCoplanar()) return;
        stack.remove(window);
    }

    /**
     * Returns the number of windows in the stack.
     */
    public synchronized int getNumWindows () {
        return stack.size();
    }

    /**
     * Move the given window to the top of the stack. Does nothing if the window is not visible 
     * or is coplanar.
     * @param window The window to be moved.
     */
    public synchronized void restackToTop(Window2D window) {
        if (window == null || !window.isVisibleApp() || window.isCoplanar()) return;
        stack.remove(window);
        stack.addFirst(window);
    }

    /**
     * Move the given window to the bottom of the stack. Does nothing if the window is not visible 
     * or is coplanar.
     * @param window The window to be moved.
     */
    public synchronized void restackToBottom(Window2D window) {
        if (window == null || !window.isVisibleApp() || window.isCoplanar()) return;
        stack.remove(window);
        stack.addLast(window);
    }

    /**
     * Move the given window so that it is above the given sibling window in the stack. If sibling is null,
     * this is the same as restackToTop. Does nothing if either window is not visible or is coplanar.
     * @param window The window to be moved.
     */
    public synchronized void restackAbove(Window2D window, Window2D sibling) {
        if (window == null || !window.isVisibleApp() || window.isCoplanar()) return;
        if (sibling == null) {
            restackToTop(window);
        } else {
            if (!sibling.isVisibleApp() || sibling.isCoplanar()) return;
            int idx = stack.indexOf(sibling);
            if (idx <= 0) {
                stack.addFirst(window);
            } else {
                stack.add(idx, window);
            }
        }
    }

    /**
     * Move the given window so that it is below the given sibling window in the stack. If sibling is null,
     * this is the same as restackToBottom. Does nothing if either window is not visible or is coplanar.
     * @param window The window to be moved.
     */
    public synchronized void restackBelow(Window2D window, Window2D sibling) {
        if (window == null || !window.isVisibleApp() || window.isCoplanar()) return;
        if (sibling == null) {
            restackToBottom(window);
        } else {
            if (!sibling.isVisibleApp() || sibling.isCoplanar()) return;
            int idx = stack.indexOf(sibling);
            if (idx < 0 || idx >= stack.size()) {
                stack.addLast(window);
            } else {
                stack.add(idx+1, window);
            }
        }
    }


    /** 
     * Return the top window of the stack.
     */
    public synchronized Window2D getTop() {
        try {
            return stack.getFirst();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    /** 
     * Return the bottom window of the stack.
     */
    public synchronized Window2D getBottom() {
        try {
            return stack.getLast();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    /** 
     * Returns the window above the given window in the stack. Returns null if the 
     * given window is at the top of the stack.
     */
    public synchronized Window2D getAbove(Window2D window) {
        if (window == null) return null;
        int idx = stack.indexOf(window);
        if (idx <= 0) {
            return null;
        } else {
            return stack.get(idx-1);
        }
    }

    /** 
     * Returns the window below the given window in the stack. Returns null if the 
     * given window is at the bottom of the stack.
     */
    public synchronized Window2D getBelow(Window2D window) {
        if (window == null) return null;
        int idx = stack.indexOf(window);
        if (idx < 0 || idx >= stack.size()) {
            return null;
        } else {
            return stack.get(idx+1);
        }
    }

    /**
     * Return the zOrder of the given window in the stack.
     * @param window The window whose zOrder is returned.
     * @return The zOrder of the window, or -1 if the window is not in the stack.
     */
    public synchronized int getZOrderOfWindow (Window2D window) {
        if (window == null) return -1;
        int idx = stack.indexOf(window);
        //System.err.println("getZOrder: stack idx of window " + window + " = " + idx);
        if (idx < 0 || idx >= stack.size()) {
            return -1;
        } else {
            return idx;
        }
    }

    /**
     * Return the stack position of the given window in the stack.
     * The stack position is the position of the window relative to the bottom of the stack.
     * The stack position of the bottommost window is 0 and the stack position of the topmost
     * window is getNumWindows()-1.
     * @param window The window whose stack position is returned.
     * @return The stack position of the window, or -1 if the window is not in the stack.
     */
    public synchronized int getStackPositionOfWindow (Window2D window) {
        if (window == null) return -1;
        int idx = stack.indexOf(window);
        if (idx < 0 || idx >= stack.size()) {
            return -1;
        } else {
            return stack.size()-1-idx;
        }
    }


    /**
     * Recalculate the stack positions of all windows in the stack based on the 
     * current desired Z order attributes of the windows. Used during conventional window 
     * slave synchronization.
     */
    public synchronized void restackFromDesiredZOrders () {
        try {
            Collections.sort(stack, new ComparatorImpl ());
        } catch (Exception ex) {
            RuntimeException re = new RuntimeException("Error during window desiredZOrder sort");
            re.initCause(ex);
            throw re;
        }
    }

    private static class ComparatorImpl implements Comparator<Window2D> {

        public ComparatorImpl() {
        }

        public int compare(Window2D window1, Window2D window2) {
            int zOrder1 = window1.getDesiredZOrder();
            int zOrder2 = window2.getDesiredZOrder();
            if (zOrder1 < zOrder2) {
                return -1;
            } else if (zOrder1 > zOrder2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    // For debug
    public void printStack() {
        System.err.println("Window stack: ");
        int i = 0;
        for (Window2D window : stack) {
            System.err.print(i++);
            System.err.print(": " + window + ": ");
            System.err.print(": " + window.getName());
            System.err.println("(" + window.isVisibleApp() + ")");
        }
    }
}
