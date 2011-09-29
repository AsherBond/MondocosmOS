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
package org.jdesktop.wonderland.client.cell.properties.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that this class implements a property sheet for either a Cell or
 * Cell Component. Classes annotated with this interface should also implement
 * the PropertiesFactorySPI interface.
 * <p>
 * This annotation has a single value: the Class of the Cell's server state
 * object or Cell Component's server state that must be provided.
 *
 * @author jkaplan
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertiesFactory {
    // The single value is the Class of the Cell's server state object
    Class value();
}
