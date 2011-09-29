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
package org.jdesktop.wonderland.modules.artimport.client;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;

/**
 * A Cell Factory that loads deployed model (.dep) files. This does not appear
 * in the Cell Palette, but supports creation via DnD or the Content Browser.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@CellFactory
public class DeployedModelCellFactory implements CellFactorySPI {

    // The error logger
    private static final Logger LOGGER =
            Logger.getLogger(DeployedModelCellFactory.class.getName());

    /**
     * {@inheritDoc}
     */
    public String[] getExtensions() {
        return new String[] { "dep" };
    }

    /**
     * {@inheritDoc}
     */
    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {
        // Fetch the URI of the model to load via the "content-uri" property.
        // If not present return null. Convert into a URL
        URL url = null;
        if (props != null) {
            String uri = props.getProperty("content-uri");
            if (uri != null) {
                try {
                    url = AssetUtils.getAssetURL(uri);
                } catch (MalformedURLException excp) {
                    LOGGER.log(Level.WARNING, "Unable to form asset URI from " +
                            uri, excp);
                    url = null;
                }
            }
        }

        // Check to make sure the URL is not null. If so, then just return
        // null
        if (url == null) {
            LOGGER.warning("The URL is null");
            return null;
        }


//        LOGGER.warning("Loading URL " + url.toExternalForm());

        // Simply create a new ModelCell by creating a ModelCellServerState
        // with the URL passed in via the properties. First load the deployed
        // model from the given URL.
        LoaderManager lm = LoaderManager.getLoaderManager();
        DeployedModel dm = null;
        try {
            dm = lm.getLoaderFromDeployment(url);
        } catch (IOException excp) {
            LOGGER.log(Level.WARNING, "Unable to load deployed model from " +
                    url.toExternalForm(), excp);
            return null;
        }

        BoundingVolumeHint hint=null;
        PositionComponentServerState posComp = new PositionComponentServerState();

        if (dm.getModelBounds()==null) {
            // Legacy support, the DeployedModels object for new builds contains
            // the model bounds.
            // Go ahead and load the model. We need to load the model in order to
            // find out its bounds to set the hint.
            ModelLoader loader = dm.getModelLoader();
            Node node = loader.loadDeployedModel(dm, null);
            BoundingVolume bounds = node.getWorldBound();
            hint = getBoundingVolumeHint(bounds);
            posComp.setBounds(bounds);
        } else {
            hint = getBoundingVolumeHint(dm.getModelBounds());
            posComp.setBounds(dm.getModelBounds());
        }

        // Create a new server state for a Model Cell that knows how to display
        // the URL.
        ModelCellServerState state = new ModelCellServerState();
        ModelCellComponentServerState compState = new ModelCellComponentServerState();
        compState.setDeployedModelURL(url.toExternalForm());
        state.addComponentServerState(compState);
        state.setBoundingVolumeHint(hint);
        state.addComponentServerState(posComp);

        // Set the name of the Cell based upon the URL of the model
        state.setName(getFileName(url));

        return (T)state;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        // Should not appear in the Cell Palette so return null
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Image getPreviewImage() {
        // Does not appear in the Cell Palette, so no preview image
        return null;
    }

    /**
     * Returns the bounding volume hint based upon the deployed model. If the
     * model is too large, it places it on top of the avatar.
     */
    private BoundingVolumeHint getBoundingVolumeHint(BoundingVolume bounds) {
        // If the model is too large, then set the bounds to extent/radius of
        // 1, so that it essentially appears on top of the avatar. This prevents
        // it from being placed too far away. We keep the y value -- so that
        // the model is placed on the ground, and not in it.
        BoundingVolume hint = bounds;
        if (bounds instanceof BoundingBox) {
            // Handle if the bounding volume is a box. We only care about the
            // x and z extents, since we'll always want to place the model on
            // the floor, no matter how high it is.
            BoundingBox box = (BoundingBox) bounds;
            if (box.xExtent > 20 || box.zExtent > 20) {
                hint = new BoundingBox(Vector3f.ZERO, 1, box.yExtent, 1);
            }
        }
        else if (bounds instanceof BoundingSphere) {
            // Handle if the bounding volume is a sphere. This is a bit tricky.
            // If the radius is too large, we want to place the model close to
            // the avatar, but we also want the model to sit on the floor, not
            // in it. So in this case, we need to create a bounding box for the
            // hint instead of a sphere, so we can set the x and z extents to
            // be 1 and the y extent to be the radius of the sphere.
            BoundingSphere sphere = (BoundingSphere) bounds;
            if (sphere.radius > 20) {
                hint = new BoundingBox(Vector3f.ZERO, 1, sphere.radius, 1);
            }
        }
        return new BoundingVolumeHint(true, hint);
    }

    /**
     * Takes a URL and returns the file name, without the extension.
     */
    private String getFileName(URL url) {
        String fname = url.getFile();

        // Look for the final foward-slash ("/") and take the last token
        int index = fname.lastIndexOf("/");
        if (index != -1) {
            fname = fname.substring(index + 1);
        }

        // Also cut out the file extension, by looking for the last dot (".")
        index = fname.lastIndexOf(".");
        if (index != -1) {
            fname = fname.substring(0, index);
        }
        return fname;
    }
}
