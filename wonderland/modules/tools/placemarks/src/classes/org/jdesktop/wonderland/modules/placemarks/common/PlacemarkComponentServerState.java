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

package org.jdesktop.wonderland.modules.placemarks.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * Server state for placemark cell component
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@XmlRootElement(name="placemark-component")
@ServerState
public class PlacemarkComponentServerState extends CellComponentServerState {
    /** the name of the placemark to create */
    private String placemarkName;
    private String placemarkRotation="0";

    /** Default constructor */
    public PlacemarkComponentServerState() {
    }

    public PlacemarkComponentServerState(String placemarkName) {
        this.placemarkName = placemarkName;
    }

    @Override
    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.modules.placemarks.server.PlacemarkComponentMO";
    }

    @XmlElement
    public String getPlacemarkName() {
        return placemarkName;
    }

    public void setPlacemarkName(String placemarkName) {
        this.placemarkName = placemarkName;
    }

    @XmlElement
    public String getPlacemarkRotation() {
        return placemarkRotation;
    }

    public void setPlacemarkRotation(String placemarkRotation) {
        this.placemarkRotation = placemarkRotation;
    }
}
