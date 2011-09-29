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
package org.jdesktop.wonderland.client.jme;

import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A collection of configurable properties for the View. When a property has
 * been changed, a set of listeners are notified of the change. This class is
 * also JAXB annotated so that is may be (de)serialized to/from XML.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="view-properties")
public class ViewProperties {

    // The field-of-view angle (degrees)
    @XmlElement(name="field-of-view")
    private float fieldOfView = 45.0f;

    // The frontand black clip (meters)
    @XmlElement(name="front-clip")
    private float frontClip = 1.0f;

    @XmlElement(name="back-clip")
    private float backClip = 2000.0f;

    // The set of listeners on changes to these properties
    @XmlTransient
    private Set<ViewPropertiesListener> listenerSet = new HashSet();

    /** An enumeration of all possible type sof view properties */
    public enum ViewProperty {
        FIELD_OF_VIEW, FRONT_CLIP, BACK_CLIP
    }

    // The JAXB content to (de)serialize to/from XML
    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(ViewProperties.class);
        } catch (javax.xml.bind.JAXBException excp) {
            excp.printStackTrace();
        }
    }

    /** Default constructor */
    public ViewProperties() {
    }

    /**
     * Returns the field-of-view in degrees.
     *
     * @return The field-of-view
     */
    @XmlTransient
    public float getFieldOfView() {
        return fieldOfView;
    }

    /**
     * Sets the field-of-view in degress and notifies any registered listeners.
     *
     * @param fov The new field-of-view (degrees)
     */
    public void setFieldOfView(float fov) {
        fieldOfView = fov;
        fireViewPropertiesChanged(ViewProperty.FIELD_OF_VIEW);
    }

    /**
     * Returns the front clip (meters).
     *
     * @return The front clip
     */
    @XmlTransient
    public float getFrontClip() {
        return frontClip;
    }

    /**
     * Sets the front clip (meters) and notifies any registered listeners.
     *
     * @param clip The front clip (meters)
     */
    public void setFrontClip(float clip) {
        frontClip = clip;
        fireViewPropertiesChanged(ViewProperty.FRONT_CLIP);
    }

    /**
     * Returns the back clip (meters).
     *
     * @return The back clip
     */
    @XmlTransient
    public float getBackClip() {
        return backClip;
    }

    /**
     * Sets the back clip (meters) and notifies any registered listeners.
     *
     * @param clip The back clip (meters)
     */
    public void setBackClip(float clip) {
        backClip = clip;
        fireViewPropertiesChanged(ViewProperty.BACK_CLIP);
    }

    /**
     * Adds a new property change listener to the set. If the listener already
     * exists, this method does nothing.
     *
     * @param listener The listener to add
     */
    public void addViewPropertiesListener(ViewPropertiesListener listener) {
        synchronized (listenerSet) {
            listenerSet.add(listener);
        }
    }

    /**
     * Removes an existing property change listener from the set. If the
     * listener does not exist, the method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeViewPropertiesListener(ViewPropertiesListener listener) {
        synchronized (listenerSet) {
            listenerSet.remove(listener);
        }
    }

    /**
     * Notifies all of the view property change listeners that the given
     * property has been changed.
     */
    private void fireViewPropertiesChanged(ViewProperty property) {
        synchronized (listenerSet) {
            for (ViewPropertiesListener listener : listenerSet) {
                listener.viewPropertiesChange(property);
            }
        }
    }

    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ViewProperties class.
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to ViewProperties
     * @throw JAXBException Upon error reading the XML file
     */
    public static ViewProperties decode(Reader r) throws JAXBException {
        // Unmarshall the XML into a ViewProperties class
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (ViewProperties)unmarshaller.unmarshal(r);
    }

    /**
     * Writes the ViewProperties class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        // Marshall the ViewProperties class into XML
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

    /**
     * Listener for changes in the collection of view properties
     */
    public interface ViewPropertiesListener {
        /**
         * Indicates that the given property has changed.
         *
         * @param property The property that has changed
         */
        public void viewPropertiesChange(ViewProperty property);
    }
}
