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
package org.jdesktop.wonderland.modules.placemarks.common;

import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import java.util.Set;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * A message with details about all the system-wide Placemarks
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class PlacemarksMessage extends Message {
    private Set<Placemark> placemarks = null;

    public PlacemarksMessage(Set<Placemark> placemarks) {
        this.placemarks = placemarks;
    }

    public Set<Placemark> getPlacemarks() {
        return placemarks;
    }
}
