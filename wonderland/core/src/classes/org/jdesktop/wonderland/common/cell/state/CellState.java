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
package org.jdesktop.wonderland.common.cell.state;

/**
 * A common tagging interface for all types of cell state. Objects that
 * implement CellState and are tagged with the @ServerState annotation will
 * be included in the list of classes available when any cell state is
 * marshalled to or unmarshalled from disk.
 * <p>
 * CellState is a tagging interface. It does not have any associated methods.
 *
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public interface CellState {
}
