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
package org.jdesktop.wonderland.server.cell.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates to the system that a CellMO or CellComponentMO
 * uses (depends upon) a CellComponentMO.
 *
 * An example usage would be
 *
 * @DependsOnCellComponentMO(AudioTreatmentComponentMO.class, ProximityComponentMO.class)
 * public class FooMO extends CellComponentMO {
 *
 * }
 *
 * In this example the annotation DependsOnCellComponentMO(AudioTreatmentComponentMO.class, ProximityComponentMO.class)
 * informs the system that the cell FooMO depends on both the AudioTreatmentComponentMO
 * and the ProximityComponentMO. The system will automatically instantite and add
 * those components to the cell during setLive.
 *
 * @author paulby
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DependsOnCellComponentMO {
    Class[] value();
}
