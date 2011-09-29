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
package org.jdesktop.wonderland.modules.appbase.client.view;

import java.util.Iterator;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;

/**
 * An object which is capable of displaying a view. For example: a cell or a HUD display.
 *
 * @author deronj
 */
@ExperimentalAPI
public interface View2DDisplayer {

    /**
     * Create a new view and associate it with this displayer. The view will be associated with 
     * the given window. Initially, the view attributes will not depend on the 
     * current window attributes but will be set to the following default values: TODO.
     * The implementation is expected to call window.addView for the newly created view.
     * @param window The window with which the view is associated.
     */
    public View2D createView (Window2D window);

    /**
     * Destroy the given view and remove its association with its window and this displayer.
     * The implementation is expected to call window.removeView.
     * @param view The view to destroy.
     */
    public void destroyView (View2D view);

    /**
     * Destroy all views of this displayer.
     */
    public void destroyAllViews ();

    /**
     * Returns an iterator over the views of this displayer.
     */
    Iterator<? extends View2D> getViews ();
}

