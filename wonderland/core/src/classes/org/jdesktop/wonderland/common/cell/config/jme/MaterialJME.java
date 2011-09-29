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
package org.jdesktop.wonderland.common.cell.config.jme;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.MaterialState;
import java.io.Serializable;

/**
 * Wire protocol that encapsulates JME materials
 *
 * @author paulby
 */
public class MaterialJME implements Serializable {

    private ColorRGBA diffuseColor;
    private ColorRGBA ambientColor;
    private ColorRGBA specularColor;
    private ColorRGBA emissiveColor;
    private float shininess;

    public MaterialJME() {
        this(null, null, null, null, 0f);
    }

    public MaterialJME(ColorRGBA diffuseColor,
                       ColorRGBA ambientColor,
                       ColorRGBA specularColor,
                       ColorRGBA emissiveColor,
                       float shininess) {
        this.diffuseColor = diffuseColor;
        this.ambientColor = ambientColor;
        this.specularColor = specularColor;
        this.emissiveColor = emissiveColor;
        this.shininess = shininess;
    }

    public ColorRGBA getDiffuseColor() {
        return diffuseColor;
    }

    public ColorRGBA getAmbientColor() {
        return ambientColor;
    }

    public ColorRGBA getSpecularColor() {
        return specularColor;
    }

    /**
     * @return the emissiveColor
     */
    public ColorRGBA getEmissiveColor() {
        return emissiveColor;
    }

    public float getShininess() {
        return shininess;
    }

    /**
     * Apply the settings to the supplied state and return the updated object.
     *
     * @param state
     * @return
     */
    public MaterialState apply(MaterialState state) {
        assert(state!=null);

        if (diffuseColor!=null)
            state.setDiffuse(diffuseColor);
        if (ambientColor!=null)
            state.setAmbient(ambientColor);
        if (specularColor!=null)
            state.setSpecular(specularColor);
        if (emissiveColor!=null)
            state.setEmissive(emissiveColor);
        state.setShininess(shininess);

        return state;
    }
}
