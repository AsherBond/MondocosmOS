/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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

package org.jdesktop.wonderland.client.jme.artimport;

import java.util.logging.Level;

/**
 * Interface for listening to messages from the ModelLoaders.
 * Mainly focused on notification of non fatal errors/warnings during the
 * import of a model (as opposed to loading of deployed models).
 *
 * @author paulby
 */
public interface LoaderListener {

    /**
     * Called during the import of models for any errors/warnings the loader
     * generates. This may be called multiple times for a model. Model loading
     * happens concurrently so calls for different models may be interspursed. 
     * 
     * @param model The model that has been imported
     * @param level the severity of the message
     * @param msg the warning/error message
     * @param throwable any exceptions associated with the message
     */
    public void modelImportErrors(ImportedModel model, Level level, String msg, Throwable throwable);
}
