/*
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
package org.jdesktop.wonderland.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation indicating this class implements a server or client plugin.
 * The type determines whether it is a client or server plugin.
 * <p>
 * ClientPlugins must implement the ClientPlugin interface, while server
 * plugins must implement the ServerPlugin interface.
 * @author jkaplan
 */
@Target(ElementType.TYPE)
public @interface Plugin {
}
