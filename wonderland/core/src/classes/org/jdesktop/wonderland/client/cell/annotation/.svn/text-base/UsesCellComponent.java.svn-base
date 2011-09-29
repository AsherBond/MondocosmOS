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
package org.jdesktop.wonderland.client.cell.annotation;

import org.jdesktop.wonderland.server.cell.annotation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates to the system that a Cell or CellComponent
 * uses (depends upon) a CellComponent.
 *
 * An example usage would be
 *
 * public class Foo extends CellComponent {
 *
 *  @UsesCellComponent
 *  private ChannelComponent channelComp;
 *
 *  public Foo() {
 *  }
 *
 *  public void setStatus(CellStatus status) {
 *      super.setStatus(status);
 *
 *      channelComp.addMessageListener(.....)
 *  }
 *
 * }
 *
 * In this example the annotation @UsesCellComponent
 * informs the system that the cell Foo uses that component. The system
 * will guarantee that ChannelComponent is installed and that the channelComp
 * field references the component before the call to setStatus.
 *
 * Thus in setStatus we can simply use the channel component.
 *
 * @author paulby
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UsesCellComponent {
}
