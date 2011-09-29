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
package org.jdesktop.wonderland.client.content;

import org.jdesktop.wonderland.client.content.spi.ContentBrowserSPI;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Manages a collection of graphical browsers that display the resources within
 * a content repository somewhere. This manager currently only supports a
 * single content browser in the system.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class ContentBrowserManager {

    private ContentBrowserSPI defaultContentBrowser = null;

    /** Default constructor */
    private ContentBrowserManager() {
    }

    /**
     * Singleton to hold instance of DragAndDropMananger. This holder class is
     * loader on the first execution of DragAndDropManager.getDragAndDropManager().
     */
    private static class ContentBrowserManagerHolder {
        private final static ContentBrowserManager browserManager = new ContentBrowserManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ContentBrowserManager getContentBrowserManager() {
        return ContentBrowserManagerHolder.browserManager;
    }

    /**
     * Sets the default system content browser. There can only be one.
     *
     * @param browser The default content browser
     */
    public void setDefaultContentBrowser(ContentBrowserSPI browser) {
        defaultContentBrowser = browser;
    }

    /**
     * Returns the defualt system content browser, null if there is none.
     *
     * @return A ContentBrowserSPI object
     */
    public ContentBrowserSPI getDefaultContentBrowser() {
        return defaultContentBrowser;
    }
}
