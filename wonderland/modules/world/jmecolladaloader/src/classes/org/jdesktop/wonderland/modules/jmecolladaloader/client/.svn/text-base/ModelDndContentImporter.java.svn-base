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
package org.jdesktop.wonderland.modules.jmecolladaloader.client;

import com.jme.scene.Node;
import org.jdesktop.wonderland.client.jme.artimport.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.content.AbstractContentImporter;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.FileUtils;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A content importer handler for Model Loaders. This will deploy and upload
 * the file to WebDav if necessary and create a Cell to display the model.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModelDndContentImporter extends AbstractContentImporter {

    // The error logger
    private static final Logger LOGGER =
            Logger.getLogger(ModelDndContentImporter.class.getName());

    private ServerSessionManager loginInfo = null;
    private String[] extensions;

    /** Constructor, takes the login information */
    public ModelDndContentImporter(ServerSessionManager loginInfo, String[] extensions) {
        this.loginInfo = loginInfo;
        this.extensions = extensions;
    }

    /**
     * @inheritDoc()
     */
    public String[] getExtensions() {
        return extensions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String isContentExists(File file) {
        // Check to see if the model already exists on the server. It should
        // be under a file named: <filename>/<filename>.dep. If so, then return
        // the ContentResource that points to the file, otherwise, return null.

        // The model loader may use a different filename, so check the directory exists rather
        // than the existance of the .dep file.
        String fileName = "art/" + file.getName();
        ContentCollection userRoot = getUserRoot();
        try {
            ContentNode node = userRoot.getChild(fileName);
            if (node != null && node instanceof ContentCollection) {
                // Search for the dep file, and return it's url
                List<ContentNode> children = ((ContentCollection)node).getChildren();
                for(ContentNode c : children) {
                    if (c instanceof ContentResource && ((ContentResource)c).getName().endsWith(".dep")) {
                        return getModelURI(file, c.getName().substring(c.getName().lastIndexOf('/')+1));
                    }
                }
            }
            return null;
        } catch (ContentRepositoryException excp) {
            LOGGER.log(Level.WARNING, "Error while try to find " + fileName +
                    " in content repository", excp);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String uploadContent(File file) throws IOException {
        URL url = file.toURI().toURL();
        ModelLoader loader = LoaderManager.getLoaderManager().getLoader(url);
        ImportSettings importSettings = new ImportSettings(url);
        ImportedModel importedModel = loader.importModel(importSettings);

        File tmpDir = File.createTempFile("dndart", null);
        if (tmpDir.isDirectory()) {
            FileUtils.deleteDirContents(tmpDir);
        } else {
            tmpDir.delete();
        }
        tmpDir = new File(tmpDir, file.getName());
        tmpDir.mkdirs();

        // Create a fake entity, which will be used to calculate the model offset
        // from the cell
        Node cellRoot = new Node();
        cellRoot.attachChild(importedModel.getModelBG());
        cellRoot.updateGeometricState(0, true);
        Entity entity = new Entity("Fake");
        RenderComponent rc = ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(cellRoot);
        entity.addComponent(RenderComponent.class, rc);
        importedModel.setEntity(entity);
        importedModel.setDeploymentBaseURL("wlcontent://users/" + loginInfo.getUsername() + "/art/");
        String filename = file.getAbsolutePath();
        filename = filename.substring(
                filename.lastIndexOf(File.separatorChar) + 1);
        filename = filename.substring(0, filename.lastIndexOf('.'));
        importedModel.setWonderlandName(filename);

        // Deploy the model to a module as an intermediate step. Write to a
        // directory.
        DeployedModel deployedModel = loader.deployToModule(tmpDir, importedModel);

        // Now copy the temporary files into webdav

        // Create the directory to hold the contents of the model. We place it
        // in a directory named after the kmz file. If the directory already
        // exists, then just use it.
        ContentCollection modelRoot = getUserRoot();

        try {
            // Copy from the art directory
            File artDir = FileUtils.findDir(tmpDir, "art");
            copyFiles(artDir, modelRoot);
            System.err.println("RET "+ getModelURI(file, deployedModel.getModelURL()));
            return getModelURI(file, importedModel.getDeployedModelURL());
        } catch (ContentRepositoryException excp) {
            String msg = "Failed to upload model file " + file.getName();
            LOGGER.log(Level.WARNING, msg, excp);
            throw new IOException(msg);
        }
    }

    /**
     * Copies all files recursively from a local File to a remote content
     * collection, creating all of the necessary files and directories.
     */
    private void copyFiles(File f, ContentCollection n)
            throws ContentRepositoryException, IOException {

        // If the given File is a directory, then attempt to create it if it
        // does not exist and the recursively copy all of its contents
        String fName = f.getName();
        if (f.isDirectory() == true) {
            // We need to create the child directory if it does not yet exist.
            // If it does exist, but is not a collection, then we need to delete
            // the existing resource and create the new collection
            ContentNode node = n.getChild(fName);
            if (node == null) {
                node = n.createChild(fName, Type.COLLECTION);
            }
            else if (!(node instanceof ContentCollection)) {
                node.getParent().removeChild(node.getName());
                node = n.createChild(fName, Type.COLLECTION);
            }
            ContentCollection dir = (ContentCollection)node;

            // Recursively descend the children and copy them over too.
            File[] subdirs = f.listFiles();
            if (subdirs != null) {
                for (File child : subdirs) {
                    copyFiles(child, dir);
                }
            }
        } else {
            // For a file, create the file if it does not yet exist. If it does
            // exist, but is not a resource, then delete the existing node and
            // create a new resource
            ContentNode node = n.getChild(fName);
            if (node == null) {
                node = n.createChild(fName, Type.RESOURCE);
            }
            else if (!(node instanceof ContentResource)) {
                node.getParent().removeChild(node.getName());
                node = n.createChild(fName, Type.RESOURCE);
            }
            ContentResource resource = (ContentResource)node;
            resource.put(f);
        }
    }

    /**
     * Returns the content repository root for the current user, or null upon
     * error.
     */
    private ContentCollection getUserRoot() {
        ContentRepositoryRegistry reg = ContentRepositoryRegistry.getInstance();
        ContentRepository repo = reg.getRepository(loginInfo);
        try {
            return repo.getUserRoot();
        } catch (ContentRepositoryException excp) {
            LOGGER.log(Level.WARNING, "Unable to find repository root", excp);
            return null;
        }
    }

    /**
     * Returns the String URI of the deployed model (.dep) for the given the
     * original art file name and the name of the dep file. The original art
     * filename is used to form the directory name.
     */
    private String getModelURI(File originalFile, String deployedFile) {
        String deployedFilename = deployedFile.substring(deployedFile.lastIndexOf('/')+1);
        return "wlcontent://users/" + loginInfo.getUsername() + "/art/" +
                originalFile.getName() + "/" + deployedFilename;
    }}
