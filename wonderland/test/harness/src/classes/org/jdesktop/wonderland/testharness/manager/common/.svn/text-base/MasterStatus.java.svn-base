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
package org.jdesktop.wonderland.testharness.manager.common;

/**
 *
 * @author paulby
 */
public class MasterStatus extends ManagerMessage {
    private int activeSlaves;
    private int passiveSlaves;

    public MasterStatus(int activeSlaves, int passiveSlaves) {
        this.activeSlaves = activeSlaves;
        this.passiveSlaves = passiveSlaves;
    }

    /**
     * Return number of active slaves, that is slaves in use by a director
     * @return
     */
    public int getActiveSlaves() {
        return activeSlaves;
    }

    /**
     * Return the number of passive slaves, that is slaves not currently in use
     * by any directors
     * @return
     */
    public int getPassiveSlaves() {
        return passiveSlaves;
    }
}
