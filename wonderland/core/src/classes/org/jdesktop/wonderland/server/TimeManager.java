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
package org.jdesktop.wonderland.server;

/**
 * Provide course grain time for the server. The courseness is dependent on
 * how well the individual OS times of the server are synchronized (using ntp).
 * The getTimeDrift method must return the maximum difference between the times
 * on the server machines.
 * 
 * @author paulby
 */
public class TimeManager {
    
    private static final long TIME_DRIFT = 10;
    
    private static long nanoOffset;
    private static long timeBase;
    
    static {
        timeBase = System.currentTimeMillis();
        nanoOffset = System.nanoTime();
    }

    /**
     * Return wonderland time in ms. This call may/will return slightly different times
     * on accross a DS multinode deployment. The time difference between servers
     * is less than the value returned by getTimeDrift.
     * @return
     */
    public static long getWonderlandTime() {
        return timeBase+(System.nanoTime()-nanoOffset);
    }
    
    /**
     * Return the maximum difference between time on multiple servers
     * @return
     */
    public static long getTimeDrift() {
        return TIME_DRIFT;
    }
}
