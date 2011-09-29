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
package org.jdesktop.wonderland.modules.xremwin.common;

import java.util.ResourceBundle;
import org.jdesktop.wonderland.common.cell.security.ModifyAction;
import org.jdesktop.wonderland.common.security.Action;

/**
 * Permission to take control of an application.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class TakeControlAction extends Action {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/xremwin/common/Bundle");
    private static final String NAME = "TakeControl";

    public TakeControlAction() {
        super (NAME, ModifyAction.class, BUNDLE.getString("Take_Control"),
                BUNDLE.getString("Take_Control_Description"));
    }
}
