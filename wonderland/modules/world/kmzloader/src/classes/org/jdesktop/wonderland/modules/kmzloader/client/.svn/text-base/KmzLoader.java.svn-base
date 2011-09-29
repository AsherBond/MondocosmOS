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
package org.jdesktop.wonderland.modules.kmzloader.client;

import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.collada.ThreadSafeColladaImporter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.modules.jmecolladaloader.client.JmeColladaLoader;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.ImportSettings;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.protocols.wlzip.WlzipManager;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentServerState;

/**
 *
 * Loader for SketchUp .kmz files
 * 
 * @author paulby
 */
class KmzLoader extends JmeColladaLoader {

    private static final Logger logger = Logger.getLogger(KmzLoader.class.getName());
        
    private HashMap<URL, ZipEntry> textureFiles = new HashMap();
    
    
    private ArrayList<String> modelFiles = new ArrayList();

    /**
     * Load a SketchUP KMZ file and return the ImportedModel object
     * @param file
     * @return
     */
    @Override
    public ImportedModel importModel(ImportSettings settings) throws IOException {
        ImportedModel importedModel;
        URL modelURL = settings.getModelURL();

        if (!modelURL.getProtocol().equalsIgnoreCase("file")) {
            final String modelURLStr = modelURL.toExternalForm();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    JOptionPane.showConfirmDialog(null,
                            "Unable to load KMZ from this url "+modelURLStr+
                            "\nPlease use a local kmz file.",
                            "Deploy Error", JOptionPane.OK_OPTION);
                }
            });
            return null;
        }

        try {
            File f = null;
            try {
                // Use the URI.getPath() to decode any escaped characters ie %20
                URI uri = modelURL.toURI();
                String path = uri.getPath();
                f = new File(path);
            } catch (URISyntaxException ex) {
                Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, "Error processing url "+modelURL.toExternalForm(), ex);
                return null;
            }
            if (f==null) {
                logger.warning("Unable to get file for model "+modelURL.toExternalForm());
                JOptionPane.showMessageDialog(null, "Unable to get file for model "+modelURL.toExternalForm(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            } else if (!f.canRead()) {
                logger.warning("Can not read file "+f.getAbsolutePath());
                JOptionPane.showMessageDialog(null, "Unable to read file "+f.getAbsolutePath()+"\nPlease check file permissions", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            ZipFile zipFile = null;
            ZipEntry docKmlEntry = null;
            try {
                zipFile = new ZipFile(f);
                docKmlEntry = zipFile.getEntry("doc.kml");
            } catch(ZipException ze) {
                logger.log(Level.WARNING,"Got a ZipException trying to open file "+f.getAbsolutePath(), ze);
                return null;
            }

            KmlParser parser = new KmlParser();
            InputStream in = zipFile.getInputStream(docKmlEntry);
            try {
                parser.decodeKML(in);
            } catch (Exception ex) {
                Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            List<KmlParser.KmlModel> models = parser.getModels();

            HashMap<URL, String> textureFilesMapping = new HashMap();
            importedModel = new KmzImportedModel(modelURL, models.get(0).getHref(), textureFilesMapping);

            String zipHost = WlzipManager.getWlzipManager().addZip(zipFile);
            ZipResourceLocator zipResource = new ZipResourceLocator(zipHost, zipFile, textureFilesMapping);
            ResourceLocatorTool.addThreadResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE,
                zipResource);

          JmeColladaLoader.LoaderErrorListener errorListener = new JmeColladaLoader.LoaderErrorListener(importedModel, LoaderManager.getLoaderManager().getLoaderListeners());

          if (models.size()==1) {
                importedModel.setModelBG(load(zipFile, models.get(0), errorListener));
            } else {
                Node modelBG = new Node();
                for(KmlParser.KmlModel model : models) {
                    modelBG.attachChild(load(zipFile, model, errorListener));
                }
                importedModel.setModelBG(modelBG);
            }
            ResourceLocatorTool.removeThreadResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, zipResource);
            WlzipManager.getWlzipManager().removeZip(zipHost, zipFile);

        } catch (ZipException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new IOException("Zip Error");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw ex;
        }

        importedModel.setModelLoader(this);
        settings.setLightingEnabled(false);     // No lighting is the default for SketchUp
        importedModel.setImportSettings(settings);

        return importedModel;
    }
    
    private Node load(ZipFile zipFile, 
                        KmlParser.KmlModel model,
                        ThreadSafeColladaImporter.LoaderErrorListener errorListener) throws IOException {

        String filename = model.getHref();

        logger.info("Loading MODEL " + filename);
        modelFiles.add(filename);
        
        ZipEntry modelEntry = zipFile.getEntry(filename);
        BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(modelEntry));

        ThreadSafeColladaImporter importer = new ThreadSafeColladaImporter(filename);
        importer.setErrorListener(errorListener);
        importer.load(in);
        Node modelNode = importer.getModel();

        // Adjust the scene transform to match the scale and axis specified in
        // the collada file
        float unitMeter = importer.getInstance().getUnitMeter();
        modelNode.setLocalScale(unitMeter);

        String upAxis = importer.getInstance().getUpAxis();
        if (upAxis.equals("Z_UP")) {
            modelNode.setLocalRotation(new Quaternion(new float[] {-(float)Math.PI/2, 0f, 0f}));
        } else if (upAxis.equals("X_UP")) {
            modelNode.setLocalRotation(new Quaternion(new float[] {0f, 0f, (float)Math.PI/2}));
        } // Y_UP is the Wonderland default

        importer.cleanUp();

        setupBounds(modelNode);
        return modelNode;
    }

    @Override
    protected ResourceLocator getDeployedResourceLocator(Map<String, String> deployedTextures, String baseURL) {
        return new RelativeResourceLocator(baseURL);
    }

    /**
     * KMZ files keep all the models in the /models directory, copy all the
     * models into the module
     * @param moduleArtRootDir
     */
    private void deployZipModels(ZipFile zipFile, File targetDir) {
        
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".dae")) {
                    File target = new File(targetDir, "/"+entry.getName()+".gz");
                    target.getParentFile().mkdirs();
//                    System.err.println("Creating file "+target.getAbsolutePath());
                    target.createNewFile();
                    
                    copyAsset(zipFile, entry, target, true);
                }
            }
            
            
        } catch (ZipException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    protected void deployModels(File targetDir,
            String moduleName,
            DeployedModel deployedModel,
            ImportedModel importedModel,
            HashMap<String, String> deploymentMapping,
            ModelCellComponentServerState state) {
        URL modelURL = importedModel.getImportSettings().getModelURL();

        System.err.println("KMZ modelURL "+modelURL.toExternalForm());

        if (!modelURL.getProtocol().equalsIgnoreCase("file")) {
            final String modelURLStr = modelURL.toExternalForm();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    JOptionPane.showConfirmDialog(null,
                            "Unable to deploy KMZ from this url "+modelURLStr+
                            "\nPlease use a local kmz file.",
                            "Deploy Error", JOptionPane.OK_OPTION);
                }
            });
            return;
        }
        try {
            ZipFile zipFile = new ZipFile(new File(modelURL.toURI()));
            deployZipModels(zipFile, targetDir);
            String kmzFilename = modelURL.toURI().getPath();
            kmzFilename = kmzFilename.substring(kmzFilename.lastIndexOf('/')+1);
            deployedModel.setModelURL(importedModel.getDeploymentBaseURL()+kmzFilename+"/"+((KmzImportedModel)importedModel).getPrimaryModel()+".gz");
            deployedModel.setLoaderDataURL(importedModel.getDeploymentBaseURL()+kmzFilename+"/"+kmzFilename+".ldr");
            deployedModel.setModelBounds(importedModel.getModelBG().getWorldBound());
            deployDeploymentData(targetDir, deployedModel, kmzFilename);
            importedModel.setDeployedModelURL(importedModel.getDeploymentBaseURL()+kmzFilename+"/"+kmzFilename+".dep");
            state.setDeployedModelURL(importedModel.getDeployedModelURL());
            state.setLightingEnabled(false);
        } catch (ZipException ex) {
            Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    protected void deployTextures(File targetDir, Map<String, String> deploymentMapping, ImportedModel importedModel) {
        URL modelURL = importedModel.getImportSettings().getModelURL();

        if (!modelURL.getProtocol().equalsIgnoreCase("file")) {
            final String modelURLStr = modelURL.toExternalForm();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    JOptionPane.showConfirmDialog(null,
                            "Unable to deploy KMZ from this url "+modelURLStr+
                            "\nPlease use a local kmz file.",
                            "Deploy Error", JOptionPane.OK_OPTION);
                }
            });
            return;
        }
        try {
            ZipFile zipFile = new ZipFile(new File(modelURL.toURI()));
            deployZipTextures(zipFile, targetDir);
        } catch (ZipException ex) {
            Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Deploys the textures into the art directory, placing them in a directory
     * with the name of the original model.
     * @param moduleArtRootDir
     */
    private void deployZipTextures(ZipFile zipFile, File targetDir) {
        try {
            // TODO generate checksums to check for image duplication
//            String targetDirName = targetDir.getAbsolutePath();

            for (Map.Entry<URL, ZipEntry> t : textureFiles.entrySet()) {
                File target = new File(targetDir, "/"+t.getKey().getPath());
                target.getParentFile().mkdirs();
                target.createNewFile();
//                logger.fine("Texture file " + target.getAbsolutePath());
                copyAsset(zipFile, t.getValue(), target, false);
            }
        } catch (ZipException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Copy the asset from the zipEntry to the target file
     * @param zipFile the zipFile that contains the zipEntry
     * @param zipEntry entry to copy from
     * @param target file to copy to
     */
    private void copyAsset(ZipFile zipFile, ZipEntry zipEntry, File target, boolean compress) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            if (compress)
                out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
            else
                out = new BufferedOutputStream(new FileOutputStream(target));
            
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

    class ZipResourceLocator implements ResourceLocator {

        private String zipHost;
        private ZipFile zipFile;
        private Map<URL, String> resourceSet;
        
        public ZipResourceLocator(String zipHost, ZipFile zipFile, Map<URL, String> resourceSet) {
            this.zipHost = zipHost;
            this.zipFile = zipFile;
            this.resourceSet = resourceSet;
        }
        
        public URL locateResource(String resourceName) {
            // Texture paths seem to be relative to the model directory....
            if (resourceName.startsWith("../")) {
                resourceName = resourceName.substring(3);
            }
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }

            ZipEntry entry = zipFile.getEntry(resourceName);
            if (entry==null) {
                // Sketchup 7.1 is putting the textures in the models dir
                resourceName = "models/"+resourceName;
                entry = zipFile.getEntry(resourceName);
                if (entry==null) {
                    logger.severe("Unable to locate texture "+resourceName);

                    return null;
                }
            }
            
            try {
                URL url = new URL("wlzip", zipHost, "/"+resourceName);
                try {
                    url.openStream();
                } catch (IOException ex) {
                    Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
                textureFiles.put(url, entry);
                if (!resourceSet.containsKey(url)) {
                    resourceSet.put(url, resourceName);
                }
                return url;
            } catch (MalformedURLException ex) {
                Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    class RelativeResourceLocator implements ResourceLocator {

        private String baseURL;
        private HashMap<String, URL> processed = new HashMap();

        /**
         * Locate resources for the given file
         * @param url
         */
        public RelativeResourceLocator(String baseURL) {
            this.baseURL = baseURL;
        }

        public URL locateResource(String resource) {
            try {
                URL url = processed.get(resource);
                if (url!=null)
                    return url;
                
                String urlStr = trimUrlStr(baseURL+"/" + resource);

                url = AssetUtils.getAssetURL(urlStr);
                processed.put(url.getPath(), url);

                return url;

            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, "Unable to locateResource "+resource, ex);
                return null;
            }
        }

        /**
         * Trim ../ from url
         * @param urlStr
         */
        private String trimUrlStr(String urlStr) {
            // replace /dir/../ with /
            return urlStr.replaceAll("/[^/]*/\\.\\./", "/");
        }
    }
    
    class KmzImportedModel extends ImportedModel {
        private String primaryModel;

        /**
         *
         * @param originalFile
         * @param primaryModel  the name of the primary dae file in the kmz.
         * @param textureFilesMapping
         */
        public KmzImportedModel(URL originalFile, String primaryModel, Map<URL, String> textureFilesMapping) {
            super(originalFile, textureFilesMapping);
            this.primaryModel = primaryModel;
        }

        /**
         * @return the primaryModel
         */
        public String getPrimaryModel() {
            return primaryModel;
        }
    }
}
