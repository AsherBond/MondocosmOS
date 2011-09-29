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

import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * A factory object which creates <code>View2D</code>s must implement this interface.
 *
 * @author deronj
 */

@InternalAPI
public interface View2DFactory {
    public View2D createView (View2DDisplayer displayer, Window2D window);
}
