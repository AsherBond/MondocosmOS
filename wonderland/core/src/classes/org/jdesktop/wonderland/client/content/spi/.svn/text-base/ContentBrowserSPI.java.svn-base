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
package org.jdesktop.wonderland.client.content.spi;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Interface to a specific implementation of a graphical content browswer.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public interface ContentBrowserSPI {

    /** Enumeration of actions that can be taken upon the content browser */
    public enum BrowserAction { OK_ACTION, CANCEL_ACTION };

    /**
     * Sets the name of the button that represents the given action
     */
    public void setActionName(BrowserAction action, String name);

    /**
     * Adds a listener to the content browser to indicate the user's action. If
     * the listener has already been added, this method does nothing. The
     * listener is valid for one event only; it is removed after it is invoked.
     *
     * @param listener The listener to add
     */
    public void addContentBrowserListener(ContentBrowserListener listener);

    /**
     * Removes a listener from the content browser. If this listener is not
     * present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeContentBrowserListener(ContentBrowserListener listener);

    /**
     * Sets whether the content browser is visible (true) or not (false).
     *
     * @param visible True to make the browser visible, false for not
     */
    public void setVisible(boolean visible);

    /**
     * Sets whether the browser should be "modal" or not.
     *
     * @param modal True to make the browser model, false for not
     */
    public void setModal(boolean modal);
    
    /**
     * The listener interface to report the result of the action taken on the
     * content browser. Generally, the content browser supports two actions,
     * OK and Cancel and returns the URI of the content asset in the case of
     * OK.
     */
    public interface ContentBrowserListener {
        /**
         * The user has selected "OK" on the content browser and has selected
         * the given uri.
         *
         * @param uri The URI selected on the content browser
         */
        public void okAction(String uri);

        /**
         * The user has selected "Cancel" on the content browser.
         */
        public void cancelAction();
    }
}
