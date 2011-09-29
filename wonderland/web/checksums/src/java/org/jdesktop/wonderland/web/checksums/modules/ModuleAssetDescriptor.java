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
package org.jdesktop.wonderland.web.checksums.modules;

import org.jdesktop.wonderland.web.checksums.AssetDescriptor;

/**
 * Describes an asset that is contained within a module, and consists of the
 * module name, module part, and path to the asset.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleAssetDescriptor implements AssetDescriptor {

    private String moduleName = null;
    private String modulePart = null;
    private String assetPath = null;

    /**
     * Constructor, takes the module name, module part, and asset path
     */
    public ModuleAssetDescriptor(String moduleName, String modulePart, String assetPath) {
        this.moduleName = moduleName;
        this.modulePart = modulePart;
        this.assetPath = assetPath;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModulePart() {
        return modulePart;
    }
}
