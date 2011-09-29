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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.CullState;
import com.jme.scene.state.RenderState;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.collada.ThreadSafeColladaImporter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.JAXBException;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.util.GraphOptimizer;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.ImportSettings;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderListener;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.modules.jmecolladaloader.common.cell.state.JmeColladaCellComponentServerState;
import org.jdesktop.wonderland.modules.jmecolladaloader.common.cell.state.LoaderData;

/**
 *
 * Loader for Collada files using JME loader
 * 
 * @author paulby
 */
@InternalAPI
public class JmeColladaLoader implements ModelLoader {

    private static final Logger logger = Logger.getLogger(JmeColladaLoader.class.getName());
        
    /**
     * Load a Collada file and return the graph root
     * @param file
     * @return
     */
    public ImportedModel importModel(ImportSettings settings) throws IOException {
        Node modelNode = null;
        URL origFile = settings.getModelURL();

        HashMap<URL, String> textureFilesMapping = new HashMap();
        ImportedModel importedModel = new ImportedModel(origFile, textureFilesMapping);
        SimpleResourceLocator resourceLocator=null;
        try {
            URL baseDir = new URL(origFile.toExternalForm().substring(0, origFile.toExternalForm().lastIndexOf('/')+1));
            resourceLocator = new RecordingResourceLocator(baseDir, textureFilesMapping);
            ResourceLocatorTool.addThreadResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    resourceLocator);
        } catch (URISyntaxException ex) {
            Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        logger.info("Loading MODEL " + origFile.toExternalForm());
        BufferedInputStream in = new BufferedInputStream(origFile.openStream());

        LoaderErrorListener errorListener = new LoaderErrorListener(importedModel, LoaderManager.getLoaderManager().getLoaderListeners());

        modelNode = loadModel(in, getFilename(origFile), true, errorListener);
        in.close();

        ResourceLocatorTool.removeThreadResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, resourceLocator);

        importedModel.setModelBG(modelNode);
        importedModel.setModelLoader(this);
        importedModel.setImportSettings(settings);

        return importedModel;
    }

    private Node loadModel(InputStream in,
                            String name,
                            boolean applyColladaAxisAndScale,
                            ThreadSafeColladaImporter.LoaderErrorListener listener) {
        Node modelNode;
        ThreadSafeColladaImporter importer = new ThreadSafeColladaImporter(name);
        importer.setErrorListener(listener);
        importer.load(in);
        modelNode = importer.getModel();

        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        CullState culls = (CullState) rm.createRendererState(RenderState.StateType.Cull);
        culls.setCullFace(CullState.Face.Back);
        modelNode.setRenderState(culls);

        if (applyColladaAxisAndScale) {
            // Adjust the scene transform to match the scale and axis specified in
            // the collada file
            float unitMeter = importer.getInstance().getUnitMeter();
            modelNode.setLocalScale(unitMeter);

            String upAxis = importer.getInstance().getUpAxis();
            if ("Z_UP".equals(upAxis)) {
                modelNode.setLocalRotation(new Quaternion(new float[] {-(float)Math.PI/2, 0f, 0f}));
            } else if ("X_UP".equals(upAxis)) {
                modelNode.setLocalRotation(new Quaternion(new float[] {0f, 0f, (float)Math.PI/2}));
            } // Y_UP is the Wonderland default
        }

        importer.cleanUp();

        setupBounds(modelNode);

//        TreeScan.findNode(modelNode, new ProcessNodeInterface() {
//
//            public boolean processNode(Spatial node) {
//                System.err.print(node);
//                if (node instanceof Geometry) {
//                    System.err.println("  "+((Geometry)node).getModelBound());
//                } else
//                    System.err.println();
//                return true;
//            }
//
//        });

        return modelNode;
    }

    public Node loadDeployedModel(DeployedModel deployedModel, Entity rootEntity) {
        InputStream in = null;
        try {
            LoaderData data=null;
            if (deployedModel.getLoaderDataURL()==null) {
                logger.warning("No Loader data for model "+deployedModel.getModelURL());
            } else {
                URL url = AssetUtils.getAssetURL(deployedModel.getLoaderDataURL());
                in = url.openStream();
                if (in==null) {
                    logger.severe("Unable to get loader data "+url.toExternalForm());
                } else {
                    try {
                        data = LoaderData.decode(in);
                    } catch (JAXBException ex) {
                        Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, "Error parsing loader data "+url.toExternalForm(), ex);
                    }
                    in.close();
                }
            }

            logger.info("LOADING DEPLOYED MODEL "+deployedModel.getModelURL());
            
            if (deployedModel.getModelURL().endsWith(".gz"))
                in = new GZIPInputStream(AssetUtils.getAssetURL(deployedModel.getModelURL()).openStream());
            else
                in = AssetUtils.getAssetURL(deployedModel.getModelURL()).openStream();

            String baseURL = deployedModel.getModelURL();
            baseURL = baseURL.substring(0, baseURL.lastIndexOf('/'));

            Node modelBG;
            Map<String, String> deployedTextures = null;
            if (data!=null)
                deployedTextures = data.getDeployedTextures();

            ResourceLocator resourceLocator = getDeployedResourceLocator(deployedTextures, baseURL);

            if (resourceLocator!=null) {
                ResourceLocatorTool.addThreadResourceLocator(
                        ResourceLocatorTool.TYPE_TEXTURE,
                        resourceLocator);
            }

            modelBG = loadModel(in, getFilename(deployedModel.getModelURL()), false, null);
            deployedModel.applyModelTransform(modelBG);

            if (resourceLocator!=null) {
                ResourceLocatorTool.removeThreadResourceLocator(
                        ResourceLocatorTool.TYPE_TEXTURE,
                        resourceLocator);
            }

            modelBG.updateGeometricState(0, true);

            return modelBG;
        } catch (IOException ex) {
            Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in!=null)
                    in.close();
            } catch (IOException ex) {
                Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    /**
     * Traverse the graph and set the geometric bounds on all tri mesh
     * @param node
     */
    protected void setupBounds(Node node) {
        TreeScan.findNode(node, new ProcessNodeInterface() {

            public boolean processNode(Spatial node) {
                if (node instanceof Geometry) {
                    if (node.getWorldBound()==null) {
                        node.setModelBound(new BoundingBox());
                        node.updateModelBound();
                    }
                }
                return true;
            }
        });
        node.updateGeometricState(0, true);
    }

    protected ResourceLocator getDeployedResourceLocator(Map<String, String> deployedTextures, String baseURL) {
       if (deployedTextures==null)
           return null;
       return new DeployedResourceLocator(deployedTextures, baseURL);
    }

    public DeployedModel deployToModule(File moduleRootDir, ImportedModel importedModel) throws IOException {
        try {
            String modelName = getFilename(importedModel.getOriginalURL().toURI().getPath());
            HashMap<String, String> textureDeploymentMapping = new HashMap();
            DeployedModel deployedModel = new DeployedModel(importedModel.getOriginalURL(), this);
            LoaderData data = new LoaderData();
            data.setDeployedTextures(textureDeploymentMapping);
            data.setModelLoaderClassname(this.getClass().getName());
            deployedModel.setLoaderData(data);

            // TODO replace getName with getModuleName(moduleRootDir)
            String moduleName = moduleRootDir.getName();

            String targetDirName = moduleRootDir.getAbsolutePath()+File.separator+"art"+ File.separator + modelName;
            File targetDir = new File(targetDirName);
            targetDir.mkdirs();

            // Must deploy textures before models so we have the deployment url mapping
            deployTextures(targetDir, textureDeploymentMapping, importedModel);

            ModelCellServerState cellSetup = new ModelCellServerState();
            ModelCellComponentServerState setup = new ModelCellComponentServerState();
            cellSetup.addComponentServerState(setup);
            cellSetup.setName(importedModel.getWonderlandName());
            BoundingVolume modelBounds = importedModel.getModelBG().getWorldBound();
            cellSetup.setBoundingVolumeHint(new BoundingVolumeHint(false, modelBounds));
            deployedModel.setModelBounds(modelBounds);

            Vector3f offset = importedModel.getRootBG().getLocalTranslation();
            PositionComponentServerState position = new PositionComponentServerState();
            Vector3f boundsCenter = importedModel.getRootBG().getWorldBound().getCenter();

            offset.subtractLocal(boundsCenter);
            deployedModel.setModelTranslation(offset);
            deployedModel.setModelRotation(importedModel.getModelBG().getLocalRotation());
            deployedModel.setModelScale(importedModel.getModelBG().getLocalScale());

//            System.err.println("BOUNDS CENTER "+boundsCenter);
//            System.err.println("OFfset "+offset);
//            System.err.println("Cell origin "+boundsCenter);
            position.setTranslation(boundsCenter);

            // The cell bounds already have the rotation and scale applied, so these
            // values must not go in the Cell transform. Instead they go in the
            // deployedModel so that the model is correctly oriented and thus
            // matches the bounds in the cell.

            // Center the worldBounds on the cell (ie 0,0,0)
            BoundingVolume worldBounds = importedModel.getModelBG().getWorldBound();
            worldBounds.setCenter(new Vector3f(0,0,0));
            position.setBounds(worldBounds);
            cellSetup.addComponentServerState(position);

            deployedModel.addCellServerState(cellSetup);

            deployModels(targetDir,
                         moduleName,
                         deployedModel,
                         importedModel,
                         textureDeploymentMapping, setup);

            return deployedModel;
        } catch (URISyntaxException ex) {
            Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected void deployDeploymentData(File targetDir, 
            DeployedModel deployedModel,
            String filename) {
        LoaderData data = (LoaderData) deployedModel.getLoaderData();
//        System.err.println("CREATING deploymentData "+filename);
        File deploymentDataFile = new File(targetDir, filename+".dep");
        File loaderDataFile = new File(targetDir, filename+".ldr");
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(deploymentDataFile));
            try {
                deployedModel.encode(out);
            } catch (JAXBException ex) {
                Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.close();
        } catch(IOException e) {

        }

        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(loaderDataFile));
            try {
                data.encode(out);
            } catch (JAXBException ex) {
                Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.close();
        } catch(IOException e) {

        }
    }

    /**
     * Deploy the dae files to the server, source[0] is the primary file.
     * @param moduleArtRootDir
     */
    protected void deployModels(File targetDir,
            String moduleName,
            DeployedModel deployedModel,
            ImportedModel importedModel,
            HashMap<String, String> deploymentMapping,
            ModelCellComponentServerState state) {
        try {
            URL[] source = importedModel.getAllOriginalModels();
            String filename = getFilename(importedModel.getOriginalURL().toURI().getPath());
//            System.err.println("DEPLOY filename "+filename);
            String filenameGZ = filename + ".gz";
            File targetFile = new File(targetDir, filenameGZ);
            try {
                targetFile.createNewFile();
                // compress the dae file using gzip stream
                copyAsset(source[0], targetFile, true); // TODO handle multiple dae files
                deployedModel.setModelURL(importedModel.getDeploymentBaseURL() + filename + "/" + filenameGZ);

                deployedModel.setLoaderDataURL(deployedModel.getModelURL() + ".ldr");
                deployDeploymentData(targetDir, deployedModel, filenameGZ);
                importedModel.setDeployedModelURL(deployedModel.getModelURL() + ".dep");
                state.setDeployedModelURL(importedModel.getDeployedModelURL());
                // Decided not to do this for deployment. Instead we will create and
                // manage the binary form in the client asset cache. The binary
                // files are only slightly smaller than compresses collada.
                // Fix the texture references in the graph to the deployed URL's
//            TreeScan.findNode(importedModel.getModelBG(), Geometry.class, new ProcessNodeInterface() {
//                public boolean processNode(Spatial node) {
//                    Geometry g = (Geometry)node;
//                    TextureState ts = (TextureState)g.getRenderState(StateType.Texture);
//                    if (ts!=null) {
//                        Texture texture = ts.getTexture();
////                        System.err.println("Graph Texture "+texture.getImageLocation());
//                        try {
//                            String originalURL = importedModel.getTextureFiles().get(new URL(texture.getImageLocation()));
//                            String deployedURL = "wla://"+moduleName+"/"+deploymentMapping.get(originalURL);
//                            if (deployedURL!=null)
//                                texture.setImageLocation(deployedURL);
////                            System.err.println("DeployedURL "+deployedURL);
//                        } catch (MalformedURLException ex) {
//                            Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//
//                    }
//                    return true;
//                }
//
//            }, false, false);
//
//            DeployStorage binaryModelFile = targetDir.createChildFile(filename+".wbm");
//            OutputStream binaryModelStream = binaryModelFile.getOutputStream();
//            BinaryExporter.getInstance().save(importedModel.getModelBG(), binaryModelStream);
//            binaryModelStream.close();
            } catch (IOException ex) {
                Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, "Unable to create file " + targetFile, ex);
            }

        } catch (URISyntaxException ex) {
            Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Return the filename for this url, excluding the path
     * @param url
     * @return
     */
    private String getFilename(URL url) {
        String t = url.getPath();
        return t.substring(t.lastIndexOf('/')+1);
    }

    private String getFilename(String str) {
        return str.substring(str.lastIndexOf('/')+1);
    }

    /**
     * Deploys the textures into the art directory, placing them in a directory
     * with the name of the original model.
     * @param moduleArtRootDir
     */
    protected void deployTextures(File targetDir, Map<String, String> deploymentMapping, ImportedModel loadedModel) {
        try {
            // TODO generate checksums to check for image duplication
//            String targetDirName = targetDir.getAbsolutePath();

            for (Map.Entry<URL, String> t : loadedModel.getTextureFiles().entrySet()) {
                File target=null;
                String targetFilename = t.getValue();
                String deployFilename=null;
                if (targetFilename.startsWith("/")) {
                    targetFilename = targetFilename.substring(targetFilename.lastIndexOf('/'));
                    if (targetFilename==null) {
                        targetFilename = t.getValue();
                    }
                } else if (targetFilename.startsWith("..")) {
                    // Relative path
                    deployFilename = targetFilename.substring(3);
                    target = new File(targetDir, deployFilename);
                } else if (targetFilename.startsWith("./")) {
                    deployFilename = targetFilename.substring(2);
                    target = new File(targetDir, deployFilename);
                }

                if (target==null) {
                    deployFilename = targetFilename;
                    target = new File(targetDir, targetFilename);
                }

//                logger.info("Texture file " + target.getAbsolutePath());
                target.getParentFile().mkdirs();
                target.createNewFile();
                copyAsset(t.getKey(), target, false);

                // Lookup the url that was in the collada file and store the mapping
                // between that and the deployed url
                String colladaURL = loadedModel.getTextureFiles().get(t.getKey());
                deploymentMapping.put(colladaURL, deployFilename);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Copy the asset from the source url to the target file
     * @param source the source file to copy from
     * @param target file to copy to
     */
    private void copyAsset(URL source, File targetFile, boolean compress) {
        InputStream in = null;
        OutputStream out = null;

        if (source==null) {
            logger.warning("Null asset source for targetFile "+targetFile);
            return;
        }

        try {
            in = new BufferedInputStream(source.openStream());
            if (compress)
                out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)));
            else
                out = new BufferedOutputStream(new FileOutputStream(targetFile));
            
            org.jdesktop.wonderland.common.FileUtils.copyFile(in, out);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in!=null)
                    in.close();
                if (out!=null)
                    out.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public ModelCellServerState getCellServerState(
            String deployedURL,
            Vector3f modelTranslation,
            Quaternion modelRotation,
            Vector3f modelScale,
            Map<String, Object> properties) {
        ModelCellServerState cellSetup = new ModelCellServerState();
        JmeColladaCellComponentServerState setup = new JmeColladaCellComponentServerState();
        cellSetup.addComponentServerState(setup);

        setup.setModel(deployedURL);
        setup.setModelScale(modelScale);
        setup.setModelRotation(modelRotation);
        setup.setModelTranslation(modelTranslation);
        setup.setModelLoaderClassname(this.getClass().getName());

        return cellSetup;
    }

    /**
     * Locate resource for deployed models
     */
    class DeployedResourceLocator implements ResourceLocator {
        private Map<String, String> textureUrlMapping;
        private String baseURL;

        public DeployedResourceLocator(Map<String, String> textureUrlMapping, String baseURL) {
            this.textureUrlMapping = new HashMap(textureUrlMapping);
            this.baseURL = baseURL;
       }

        public URL locateResource(String resourceName) {
            String t = textureUrlMapping.get(resourceName);

             if (t==null)
                return null;

            URL ret=null;
            try {
                ret = AssetUtils.getAssetURL(baseURL + "/" + t);
                textureUrlMapping.put(ret.getPath(), t);  // JME may ask for the texture again, using the new path
            } catch (MalformedURLException ex) {
                Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
            }

            return ret;
        }
    }

    class RecordingResourceLocator extends SimpleResourceLocator {
        private Map<URL, String> resourceSet;
        public RecordingResourceLocator(URI baseDir, Map<URL, String> resourceSet) {
            super(baseDir);
            this.resourceSet = resourceSet;
        }

        public RecordingResourceLocator(URL baseDir, Map<URL, String> resourceSet) throws URISyntaxException {
            super(baseDir);
            this.resourceSet = resourceSet;
        }

        @Override
        public URL locateResource(String resourceName) {
            URL ret = locateResourceImpl(resourceName);
            if (!resourceSet.containsKey(ret)) {
                resourceSet.put(ret, resourceName);
            }

            return ret;
        }

        // Copied directly from SimpleResourceLocator
        public URL locateResourceImpl(String resourceName) {
            // Trim off any prepended local dir.
            while (resourceName.startsWith("./") && resourceName.length() > 2) {
                resourceName = resourceName.substring(2);
            }
            while (resourceName.startsWith(".\\") && resourceName.length() > 2) {
                resourceName = resourceName.substring(2);
            }

            // Try to locate using resourceName as is.
            try {
                String spec = URLEncoder.encode( resourceName, "UTF-8" );
                //this fixes a bug in JRE1.5 (file handler does not decode "+" to spaces)
//                spec = spec.replaceAll( "\\+", "%20" );

                URL rVal = new URL( baseDir.toURL(), spec );
                // open a stream to see if this is a valid resource
                // XXX: Perhaps this is wasteful?  Also, what info will determine validity?
                rVal.openStream().close();
                return rVal;
            } catch (IOException e) {
                // URL wasn't valid in some way, so try up a path.
            } catch (IllegalArgumentException e) {
                // URL wasn't valid in some way, so try up a path.
            }

            resourceName = trimResourceName(resourceName);
            if (resourceName == null) {
                return null;
            } else {
                return locateResourceImpl(resourceName);
            }
        }
    }

    public static class LoaderErrorListener implements ThreadSafeColladaImporter.LoaderErrorListener {

        private Collection<LoaderListener> listeners;
        private ImportedModel model;

        public LoaderErrorListener(ImportedModel model, Collection<LoaderListener> listeners) {
            this.listeners = listeners;
            this.model = model;
        }

        /**
         * Called when the loader experiences an error. Once this callback
         * returns loading will continue to the best of the loaders ability
         *
         * @param level the severity of the error
         * @param msg the error message
         * @param throwable any associated exception, may be null
         */
        public void error(Level level, String msg, Throwable throwable) {
            for(LoaderListener l : listeners)
                l.modelImportErrors(model, level, msg, throwable);
        }

    }
}
