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
package org.jdesktop.wonderland.web.checksums;

import org.jdesktop.wonderland.common.checksums.ChecksumList;

/**
 * A ChecksumFactory is responsible for generating and returning the checksums
 * for a particular "root" in the content repository. Examples of root include
 * "modules", "users", "system", etc. so that each of these different parts of
 * the content store can manage checksums in their own way.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface ChecksumFactory {
    /**
     * The action to take if the checksum file does not exist:
     * DO_NOT_GENERATE: Do not generate a checksum file
     * GENERATE: Generate a checksum file if it does not exist
     * FORCE_GENERATE: Always generate a checksum file, even if it exists
     */
    public enum ChecksumAction { DO_NOT_GENERATE, GENERATE, FORCE_GENERATE };

    /**
     * Returns the checksum of a given asset as described by the given asset
     * descriptor. If no checksum is present and the 'generate' parameter is
     * GENERATE, this creates the checksum on the fly, or otherwise returns null.
     * This method returns a list of checksums, to support the case where the
     * asset descriptor resolves to more than one asset (e.g. in the case where
     * the asset is null).
     *
     * @param descriptor Describes the location of the asset
     * @param generate Generate a checksum if not present on the system
     * @return A Checksum for the asset or null if not present
     */
    public ChecksumList getChecksumList(AssetDescriptor descriptor, ChecksumAction generate);
}
