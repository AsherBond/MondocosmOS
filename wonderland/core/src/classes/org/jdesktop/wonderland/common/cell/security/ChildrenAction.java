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
package org.jdesktop.wonderland.common.cell.security;

import java.util.ResourceBundle;
import org.jdesktop.wonderland.common.security.Action;

/**
 * A sub-action of modify for adding or removing cell children
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class ChildrenAction extends Action {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/common/cell/security/Bundle");
    private static final String NAME = "ChangeCellChildren";

    public ChildrenAction() {
        super(NAME, ModifyAction.class,
                BUNDLE.getString("ChangeCellChildren_DisplayName"),
                BUNDLE.getString("ChangeCellChildren_Description"));
    }
}
