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
package org.jdesktop.wonderland.common.comms;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * A specialized ObjectInputStream that reduces the size of serialized core
 * wonderland objects. For known classes this stream stores a (int) id instead
 * of the large serialization class header. For unknown classes it stores
 * the class name in the stream, which again is usually much smaller than 
 * the serialization header. 
 * 
 * @author paulby
 */
public class WonderlandObjectOutputStream extends ObjectOutputStream {

    protected static final int UNKNOWN_DESCRIPTOR = Integer.MIN_VALUE;
    protected static int firstID = UNKNOWN_DESCRIPTOR+1;
    
    private static HashMap<String, Integer> descToId = new HashMap();
        
    // XXX replace with Strings to avoid problems with clients that
    // don't have JME.  This is prone to typos, so should be replaced
    // by a more automatic system XXX
    private static String[] coreClass = new String[] {
        // MovableMessage.class.getName(),
        "org.jdesktop.wonderland.common.cell.messages.MovableMessage",
        
        // MovableMessage.ActionType.class.getName(),
        "org.jdesktop.wonderland.common.cell.messages.MovableMessage$ActionType",
        
        // CellMessage.class.getName(),
        "org.jdesktop.wonderland.common.cell.messages.CellMessage",
        
        // MessageID.class.getName(),
        "org.jdesktop.wonderland.common.messages.MessageID",
        
        //Enum.class.getName(),
        "java.lang.Enum",
        
        // Vector3f.class.getName(),
        "com.jme.math.Vector3f",
        
        // Quaternion.class.getName(),
        "com.jme.math.Quaternion",
        
        // Message.class.getName(),
        "org.jdesktop.wonderland.common.messages.Message",
        
        // CellID.class.getName(),
        "org.jdesktop.wonderland.common.cell.CellID"
    };
  
    static {
        populateDescToId(descToId);
    }
    
    public WonderlandObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        
        // Now send the users class descriptor
        Integer idObj = descToId.get(desc.getName());
        if (idObj == null) {
//            System.err.println("First classDescriptor for "+desc.getName()+"  "+descToId.size());
            writeInt(UNKNOWN_DESCRIPTOR);
            writeUTF(desc.forClass().getName());
        } else {
            writeInt(idObj);
        }
    }
    
    static void populateDescToId(HashMap<String, Integer> map) {
        int id = firstID;
        for(String clazz : coreClass) {
            map.put(clazz, id++);
        }
    }
    
    static void populateIdToDesc(HashMap<Integer, String> map) {
        int id = firstID;
        for(String clazz : coreClass) {
            map.put(id++, clazz);
        }
        
    }
}
