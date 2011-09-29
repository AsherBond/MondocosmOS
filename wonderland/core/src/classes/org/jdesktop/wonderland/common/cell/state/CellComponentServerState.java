/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.common.cell.state;

import java.io.Serializable;

/**
 * The CellComponentServerState class is the base class for the server state
 * information for all cell components. Individual cell components may be
 * mixed into cells to give them added functionality: subclasses of this class
 * provide the necessary configuration information.
 * <p>
 * This mechanism used JAXB to serialize and deserialize objects to/from XML.
 * Each subclass of CellSetupComponent must be annotated with @XmlRootElement
 * which gives the name that encapsulates the component-specific XML setup
 * information. Within the root element, the subclass component is free to
 * design its own XML schema for its setup information, typically using the
 * @XmlElement and @XmlAttribute annotations.
 * <p>
 * Each subclass of this abstract class must implement a default constructor.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class CellComponentServerState implements CellState, Serializable {
    
    /**
     * Returns the fully-qualified class name of the server-side component
     * class.
     * 
     * @return The server-side cell component class name
     */
    public abstract String getServerComponentClassName();
}
