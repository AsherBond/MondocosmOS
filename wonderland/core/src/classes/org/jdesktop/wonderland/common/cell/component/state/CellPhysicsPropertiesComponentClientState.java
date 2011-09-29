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
package org.jdesktop.wonderland.common.cell.component.state;

import java.util.HashMap;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

/**
 *
 * @author paulby
 */
public class CellPhysicsPropertiesComponentClientState extends CellComponentClientState {

    private HashMap<String, PhysicsProperties> propertiesMap = new HashMap();

    /**
     * Return the physcics properties associated with the entity name
     * @param entityName
     * @return
     */
    public PhysicsProperties getPhyiscsProperties(String entityName) {
        System.err.println("Prop count "+propertiesMap.size());
        return propertiesMap.get(entityName);
    }

    public void addPhysicsProperties(String entityName, PhysicsProperties properties) {
        propertiesMap.put(entityName, properties);
    }


}
