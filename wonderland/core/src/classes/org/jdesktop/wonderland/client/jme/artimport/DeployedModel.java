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
package org.jdesktop.wonderland.client.jme.artimport;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.utils.jaxb.BoundingVolumeAdapter;
import org.jdesktop.wonderland.common.utils.jaxb.QuaternionAdapter;
import org.jdesktop.wonderland.common.utils.jaxb.Vector3fAdapter;

/**
 * This class represents a .dep file, which is a deployed model. 
 *
 * @author paulby
 */
@XmlRootElement(name="deployed-model")
public class DeployedModel {

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(DeployedModel.class);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }

    // Version number for the xml
    @XmlElement(name="version")
    private short version = 1;

    @XmlElement(name="modelURL")
    private String modelURL = null;

    @XmlElement(name="loaderDataURL")
    private String loaderDataURL = null;

    @XmlElement(name="modelBGScale", nillable=true)
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f modelBGScale = null;

    @XmlElement(name="modelBGTranslation", nillable=true)
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f modelBGTranslation = null;

    @XmlElement(name="modelBGRotation", nillable=true)
    @XmlJavaTypeAdapter(QuaternionAdapter.class)
    private Quaternion modelBGRotation = null;

    @XmlElement(name="modelBounds", nillable=true)
    @XmlJavaTypeAdapter(BoundingVolumeAdapter.class)
    private BoundingVolume modelBounds = null;

    @XmlElement(name="modelLoaderClassname")
    private String modelLoaderClassname;

    @XmlElement(name="author")
    private String author;

    @XmlTransient private CellServerState cellServerState;

    @XmlTransient private ModelLoader modelLoader;

    @XmlTransient private Object loaderData=null;

    public DeployedModel() {
    }

    public DeployedModel(URL modelURL, ModelLoader modelLoader) {
        this.modelLoaderClassname = modelLoader.getClass().getName();
        this.modelURL = modelURL.toExternalForm();
    }

    public DeployedModel(String modelLoaderClassname) {
        this.modelLoaderClassname = modelLoaderClassname;
    }

    /**
     * @return the deployedURL
     */
    @XmlTransient public String getModelURL() {
        return modelURL;
    }

    /**
     * @param deployedURL the deployedURL to set
     */
    public void setModelURL(String deployedURL) {
        this.modelURL = deployedURL;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();

        ret.append(super.toString()+"\n");
        ret.append("Deployed to "+modelURL+"\n");

        return ret.toString();
    }

    /**
     * Record the local transform of the modelBG
     * @param modelBG
     */
    public void recordModelBGTransform(Node modelBG) {
        setModelScale(modelBG.getLocalScale());
        setModelTranslation(modelBG.getLocalTranslation());
        setModelRotation(modelBG.getLocalRotation());
    }

    /**
     * Return the classname of the model loader used to load this model.
     * @return
     */
    @XmlTransient public String getModelLoaderClassname() {
        return modelLoaderClassname;
    }

    /**
     * Apply the model transform the modelBG node
     */
    public void applyModelTransform(Node modelBG) {
        if (getModelScale()!=null)
            modelBG.setLocalScale(getModelScale());
        if (getModelTranslation()!=null)
            modelBG.setLocalTranslation(getModelTranslation());
        if (getModelRotation()!=null)
            modelBG.setLocalRotation(getModelRotation());
    }

    public void addCellServerState(CellServerState cellServerState) {
        this.cellServerState = cellServerState;
    }

    @XmlTransient public CellServerState getCellServerState() {
        return cellServerState;
    }

    @XmlTransient public ModelLoader getModelLoader() {
        if (modelLoader==null) {
           modelLoader = LoaderManager.getLoaderManager().getLoader(this);
        }

        return modelLoader;
    }

    /**
     * @return the modelBGScale
     */
    @XmlTransient public Vector3f getModelScale() {
        return modelBGScale;
    }

    /**
     * @param modelBGScale the modelBGScale to set
     */
    public void setModelScale(Vector3f modelBGScale) {
        this.modelBGScale = modelBGScale;
    }

    /**
     * @return the modelBGTranslation
     */
    @XmlTransient public Vector3f getModelTranslation() {
        return modelBGTranslation;
    }

    /**
     * @param modelBGTranslation the modelBGTranslation to set
     */
    public void setModelTranslation(Vector3f modelBGTranslation) {
        this.modelBGTranslation = modelBGTranslation;
    }

    /**
     * @return the modelBGRotation
     */
    @XmlTransient public Quaternion getModelRotation() {
        return modelBGRotation;
    }

    /**
     * @param modelBGRotation the modelBGRotation to set
     */
    public void setModelRotation(Quaternion modelBGRotation) {
        this.modelBGRotation = modelBGRotation;
    }

    /**
     * @return the loaderDeploymentData
     */
    @XmlTransient public Object getLoaderData() {
        return loaderData;
    }

    /**
     * @param loaderDeploymentData the loaderDeploymentData to set
     */
    public void setLoaderData(Object loaderDeploymentData) {
        this.loaderData = loaderDeploymentData;
    }


    /**
     * @return the modelBounds
     */
    @XmlTransient public BoundingVolume getModelBounds() {
        return modelBounds;
    }

    /**
     * @param modelBounds the modelBounds to set
     */
    public void setModelBounds(BoundingVolume modelBounds) {
        this.modelBounds = modelBounds;
    }

    /**
     * @return the author
     */
    @XmlTransient public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * The URL of the .ldr file that contains loader specific data
     *
     * @return the loaderDataURL
     */
    @XmlTransient public String getLoaderDataURL() {
        return loaderDataURL;
    }

    /**
     * @param loaderDataURL the loaderDataURL to set
     */
    public void setLoaderDataURL(String loaderDataURL) {
        this.loaderDataURL = loaderDataURL;
    }

    /**
     * Writes the ModuleInfo class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        /* Write out to the stream */
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }

    public static DeployedModel decode(InputStream in) throws JAXBException {
        /* Read in from stream */
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DeployedModel info = (DeployedModel)unmarshaller.unmarshal(in);

        return info;
    }

    void setModelLoader(ModelLoader loader) {
        this.modelLoader = loader;
    }
}
