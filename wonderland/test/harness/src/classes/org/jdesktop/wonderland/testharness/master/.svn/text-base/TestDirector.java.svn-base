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
package org.jdesktop.wonderland.testharness.master;

/**
 * The class provides the high level control of a test case
 * 
 * @author paulby
 */
public interface TestDirector {

    /**
     * A slave has joined the master session. The director can choose if it want
     * to include the slave in its tests. If it does include the slave it should
     * return true. If it returns false the slave will be offered to another director
     * @param slaveController
     * @return
     */
    public boolean slaveJoined(SlaveConnection slaveController);

}
