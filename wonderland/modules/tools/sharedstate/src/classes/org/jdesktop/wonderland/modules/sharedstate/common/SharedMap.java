/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.modules.sharedstate.common;

import java.util.Map;

/**
 * A map of shared data.
 * @author jkaplan
 */
public interface SharedMap extends Map<String, SharedData> {
    /**
     * Get the name of this map. A map is unique withing a given
     * SharedStateComponent
     * @return the map's name
     */
    String getName();

    /**
     * Get shared data of the given type
     * @param key the key to get
     * @param type the type to get
     * @return the value associated with the given key, or null if no
     * value is associated with the given key
     */
    <T extends SharedData> T get(String key, Class<T> type);
    
    /**
     * Get an unwrapped String value from the map
     * @param key the key to get
     * @return the String corresponding to key, or null if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a String
     */
    public String getString(String key);
    
    /**
     * Insert a wrapped String into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putString(String key, String value);
    
    /**
     * Get an unwrapped boolean value from the map
     * @param key the key to get
     * @return the boolean corresponding to key, or false if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a boolean
     */
    public boolean getBoolean(String key);
    
    /**
     * Insert a wrapped boolean into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putBoolean(String key, boolean value);
    
    /**
     * Get an unwrapped byte value from the map
     * @param key the key to get
     * @return the byte corresponding to key, or 0 if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a byte
     */
    public byte getByte(String key);
    
    /**
     * Insert a wrapped byte into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putByte(String key, byte value);
    
    /**
     * Get an unwrapped char value from the map
     * @param key the key to get
     * @return the char corresponding to key, or 0 if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a char
     */
    public char getChar(String key);
    
    /**
     * Insert a wrapped char into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putChar(String key, char value);
    
    /**
     * Get an unwrapped short value from the map
     * @param key the key to get
     * @return the short corresponding to key, or 0 if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a short
     */
    public short getShort(String key);
    
    /**
     * Insert a wrapped short into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putShort(String key, short value);
    
    /**
     * Get an unwrapped int value from the map
     * @param key the key to get
     * @return the int corresponding to key, or 0 if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a int
     */
    public int getInt(String key);
    
    /**
     * Insert a wrapped int into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putInt(String key, int value);
    
    /**
     * Get an unwrapped long value from the map
     * @param key the key to get
     * @return the long corresponding to key, or 0 if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a long
     */
    public long getLong(String key);
    
    /**
     * Insert a wrapped long into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putLong(String key, long value);
    
    /**
     * Get an unwrapped float value from the map
     * @param key the key to get
     * @return the float corresponding to key, or 0f if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a float
     */
    public float getFloat(String key);
    
    /**
     * Insert a wrapped float into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putFloat(String key, float value);
    
    /**
     * Get an unwrapped double value from the map
     * @param key the key to get
     * @return the double corresponding to key, or 0.0 if no value
     * corresponds to key
     * @throws ClassCastException if key doesn't map to a double
     */
    public double getDouble(String key);
    
    /**
     * Insert a wrapped double into the map
     * @param key the key to insert
     * @param value the value to insert
     */
    public void putDouble(String key, double value);
    
}
