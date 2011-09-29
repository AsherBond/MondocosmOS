/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.common.cell.state;

import org.jdesktop.wonderland.common.cell.ComponentLookupClass;

/**
 * Common utilities for cell components.
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class CellComponentUtils {
    /**
     * Return the class used to reference this component. Usually this will
     * return the class of the component, but in some cases, will return another
     * Class. This method uses the ComponentClassLookup annotation on the cell
     * component to determine this class; if not present just returns the Class
     * given.
     *
     * @param clazz The Class of the component
     * @return The Class used in the component lookup table
     */
    public static Class getLookupClass(Class clazz) {
        ComponentLookupClass l = (ComponentLookupClass) clazz.getAnnotation(ComponentLookupClass.class);
        if (l != null) {
            return l.value();
        }
        return clazz;
    }
}
