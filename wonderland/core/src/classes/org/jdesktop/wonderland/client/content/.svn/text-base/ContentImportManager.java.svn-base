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
package org.jdesktop.wonderland.client.content;

import java.util.HashMap;
import java.util.Map;
import org.jdesktop.wonderland.client.content.spi.ContentImporterSPI;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Manages the collection import handlers for different kinds of content. Each
 * content type is given by the file extension, as returned by the class that
 * implements the ContentImportSPI. There can also be a "default" content
 * handler for all file extensions not registered.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class ContentImportManager {

    /* A map of content extensions and their import handlers */
    private Map<String, ContentImporterSPI> contentImportMap = new HashMap();

    /* The default content handler for imports, can be null */
    private ContentImporterSPI defaultContentImporter = null;

    /** Default constructor */
    private ContentImportManager() {
    }

    /**
     * Singleton to hold instance of DragAndDropMananger. This holder class is
     * loader on the first execution of DragAndDropManager.getDragAndDropManager().
     */
    private static class ContentImportManagerHolder {
        private final static ContentImportManager dndManager = new ContentImportManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ContentImportManager getContentImportManager() {
        return ContentImportManagerHolder.dndManager;
    }

    /**
     * Sets the default content importer, overrides any previously set.
     *
     * @param importer The content import handler
     */
    public void setDefaultContentImporter(ContentImporterSPI importer) {
        defaultContentImporter = importer;
    }

    /**
     * Get the current default content importer.
     *
     * @return importer the current default content importer, or null if there
     * is no default content importer.
     */
    public ContentImporterSPI getDefaultContentImporter() {
        return defaultContentImporter;
    }

    /**
     * Registers a handler for content import. A content import handler handles
     * when an items is to be imported into the world with a specific file
     * extension. Only one import handler is permitted per file extension
     *
     * @param importer The content importer
     */
    public void registerContentImporter(ContentImporterSPI importer) {
        // For each of the extensions that are supported by the handler, add
        // then to the map. If the extension type is already registered, then
        // overwrite.
        String extensions[] = importer.getExtensions();
        if (extensions != null) {
            for (String extension : extensions) {
                if (extension == null) {
                    continue;
                }

                contentImportMap.put(extension.toLowerCase(), importer);
            }
        }
    }

    /**
     * Unregisters a handler for content import.
     * @param importer The content importer
     */
    public void unregisterContentImporter(ContentImporterSPI importer) {
        // For each of the extensions that are supported by the handler, add
        // then to the map. If the extension type is already registered, then
        // overwrite.
        String extensions[] = importer.getExtensions();
        if (extensions != null) {
            for (String extension : extensions) {
                if (extension == null) {
                    continue;
                }

                ContentImporterSPI curImporter = contentImportMap.get(extension.toLowerCase());

                // XXX the use of .equals() here is problematic -- it
                // means we can easily overwrite valid values with null
                // if unregister is called after register XXX
                if (curImporter != null && curImporter.equals(importer)) {
                    contentImportMap.remove(extension.toLowerCase());
                }
            }
        }
    }

    /**
     * Returns the content importer given the extension name, possibly returning
     * the default content importer if the extension is not found and the
     * useDefault argument is set to true. If useDefault is false and no importer
     * is found for the given extension, this method returns null.
     *
     * @param extension The file extension to search for a content import
     * @param useDefault If true and no importer is found for the extension,
     * return the default content importer if available
     * @return A ContentImportSPI object
     */
    public ContentImporterSPI getContentImporter(String extension, boolean useDefault) {
        if (extension == null) {
            return null;
        }

        ContentImporterSPI importer = contentImportMap.get(extension.toLowerCase());
        if (importer == null && useDefault == true) {
            return defaultContentImporter;
        }
        return importer;
    }
}
