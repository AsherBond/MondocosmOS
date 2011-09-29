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
package org.jdesktop.wonderland.web.checksums.content;

import org.jdesktop.wonderland.web.checksums.AssetDescriptor;

/**
 * Describes an asset that is contained within the content repository, and
 * consists of the root name (e.g. "system", "groups/<group name>",
 * "users/<user name>"), and the relative path of the asset beneath it.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ContentAssetDescriptor implements AssetDescriptor {

    private String rootName = null;
    private String assetPath = null;

    /**
     * Constructor, takes the root name and asset path
     */
    public ContentAssetDescriptor(String rootName, String assetPath) {
        this.rootName = rootName;
        this.assetPath = assetPath;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getRootName() {
        return rootName;
    }
}
