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
package org.jdesktop.wonderland.client.content.spi;

import java.io.File;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Handler for various flavors of content to import. Classes implement this
 * interface and register themselves with the ContentImportManager and are
 * called when content is being imported into the world.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public interface ContentImporterSPI {

    /**
     * Returns an array of string extensions supported by the content importer.
     * Return an empty array if none.
     *
     * @return An array of String file extension
     */
    public String[] getExtensions();

    /**
     * Imports a given File into the system for the extension type and creates
     * a corresponding cell. Returns a uri that represents the imported file.
     * <p>
     * Calling this method is equivalent to calling
     * <code>importFile(file, extension, true)</code>
     *
     * @param file The File to import
     * @param extension The extension of the file
     * @return A URI that represents the importer file
     */
    public String importFile(File file, String extension);

    /**
     * Imports a given File into the system for the extension type. Returns a
     * uri that represents the imported file.
     *
     * @param file The File to import
     * @param extension The extension of the file
     * @param createCell if true, create a cell to handle the file
     * @return A URI that represents the importer file
     */
    public String importFile(File file, String extension, boolean createCell);
}
