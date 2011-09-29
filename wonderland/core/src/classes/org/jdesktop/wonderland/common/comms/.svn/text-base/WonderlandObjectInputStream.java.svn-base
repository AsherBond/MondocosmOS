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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;

/**
 * A specialized ObjectInputStream that reduces the size of serialized core
 * wonderland objects. For known classes this stream reads an (int) id instead
 * of the large serialization class header. For unknown classes it reads
 * the class name in the stream, which again is usually much smaller than 
 * the serialization header. 
 * 
 * @author paulby
 */public class WonderlandObjectInputStream extends ObjectInputStream {

    private static HashMap<Integer, String> idToDesc = new HashMap();

    private ClassLoader classLoader;
    
    static {
        WonderlandObjectOutputStream.populateIdToDesc(idToDesc);        
    }
    
    public WonderlandObjectInputStream(InputStream in) throws IOException {
        this (in, null);
    }
    
    public WonderlandObjectInputStream(InputStream in, ClassLoader classLoader)
            throws IOException
    {
        super(in);
   
        this.classLoader = classLoader;
        if (this.classLoader == null) {
            this.classLoader = getClass().getClassLoader();
        }
    }

    /**
     * Override the default class resolution to use the provided 
     * classloader.
     * @param osc the class to resolve
     * @return the class for the given stream
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) 
            throws IOException, ClassNotFoundException 
    {
        if (classLoader == null) {
            // fall back to the default
            return super.resolveClass(osc);
        } else {
            // resolve with our classloader
            return Class.forName(osc.getName(), true, classLoader);
        }
    }

    
    
    @Override
    protected ObjectStreamClass readClassDescriptor()
            throws ClassNotFoundException, IOException 
    {
        int id = readInt();
        Class lookupClass;
        
        if (id == WonderlandObjectOutputStream.UNKNOWN_DESCRIPTOR) {
            String className = readUTF();
//            System.err.println("WonderlandInputStream reading NEW_DESC "+className);
            lookupClass = Class.forName(className, true, classLoader);
        } else {
            lookupClass = Class.forName(idToDesc.get(id), true, classLoader);
        }
        
        ObjectStreamClass ret = ObjectStreamClass.lookup(lookupClass);
        if (ret == null) {
            throw new IOException("Unknown class ID " + id);
        }
        
        return ret;
    }
}
