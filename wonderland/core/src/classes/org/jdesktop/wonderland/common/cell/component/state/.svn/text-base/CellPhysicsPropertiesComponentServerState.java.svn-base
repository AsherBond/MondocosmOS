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
import java.util.Map.Entry;
import java.util.Set;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;

/**
 *
 * @author paulby
 */
public class CellPhysicsPropertiesComponentServerState extends CellComponentServerState {

    private HashMap<String, PhysicsProperties> propertiesMap = new HashMap();

    public final static String DEFAULT_NAME = "default_entity";

    @Override
    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.server.cell.component.CellPhysicsPropertiesComponentMO";
    }

    /**
     * Return the physcics properties associated with the entity name
     * @param entityName
     * @return
     */
    public PhysicsProperties getPhyiscsProperties(String entityName) {
        return propertiesMap.get(entityName);
    }

    public void addPhysicsProperties(String entityName, PhysicsProperties properties) {
        System.err.println("ServerState addPhyProp "+entityName);
        propertiesMap.put(entityName, properties);
    }

    /**
     * Set clientState to the values in this server state
     * @param clientState
     */
    public void getClientState(CellComponentClientState clientState) {
        assert(clientState!=null);

        System.err.println("SET CLIENT STATE");

        Set<Entry<String, PhysicsProperties>> entries = propertiesMap.entrySet();
        for(Entry<String, PhysicsProperties> e : entries) {
            System.err.println(e.getKey());
            ((CellPhysicsPropertiesComponentClientState)clientState).addPhysicsProperties(e.getKey(), e.getValue());
        }
    }

    /**
     * Clone this object into ret and return. If ret is null a new object will
     * be created and returned.
     * @param ret
     * @return
     */
    public CellPhysicsPropertiesComponentServerState clone(CellPhysicsPropertiesComponentServerState ret) {
        if (ret==null)
            ret = new CellPhysicsPropertiesComponentServerState();

        System.err.println("CLONING");

        Set<Entry<String, PhysicsProperties>> entries = propertiesMap.entrySet();
        for(Entry<String, PhysicsProperties> e : entries) {
            System.err.println(e.getKey());
            ret.addPhysicsProperties(e.getKey(), e.getValue());
        }
        
        return ret;
    }


}
