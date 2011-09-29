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
 * public class FooMO extends CellComponentMO {
 *
 *  @UsesCellComponentMO(ChannelComponentMO.class)
 *  private ManagedReference<ChannelComponentMO> channelCompRef;
 *
 *  public FooMO() {
 *  }
 *
 *  public void setLive(boolean isLive) {
 *      super.setStatus(isLive);
 *
 *      channelCompRef.getForUpdate().addMessageListener(.....)
 *  }
 *
 * }
 *
 * In this example the annotation @UsesCellComponentMO(ChannelComponentMO.class)
 * informs the system that the cell FooMO uses that component. The system
 * will guarantee that ChannelComponentMO is installed and that the channelCompRef
 * references points to the component before the call to setLive.
 *
 * Thus in setLive we can simply use the channel component.
 *
 * This annotation complements @DependsOnCellComponentMO, the difference is
 * that this annotation creates a reference to the component, where the @DependsOnCellComponentMO
 * just ensures the component is added to the cell. You don't need to specify @DependsOnCellComponentMO
 * for components that you specifiy with @UsesCellComponentMO, but you can if you wish.
 *
 * @author paulby
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UsesCellComponentMO {
    Class value();
}
