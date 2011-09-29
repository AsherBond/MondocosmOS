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
package org.jdesktop.wonderland.modules.placemarks.common;

import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A list of placemarks.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="placemark-list")
public class PlacemarkList implements Serializable {

    @XmlElementRefs({
        @XmlElementRef()
    })
    private Placemark[] placemarks = new Placemark[] {};

    // A Map of (unique) placemark names and placemark objects. This is formed
    // from the array of Placemark objects found in the XML file
    private Map<String, Placemark> placemarkMap = new HashMap();

    /* The JAXB content to (de)serialize to/from XML */
    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(PlacemarkList.class);
        } catch (javax.xml.bind.JAXBException excp) {
            excp.printStackTrace();
        }
    }

    /**
     * Default constructor, needed for JAXB
     */
    public PlacemarkList() {
    }

    /**
     * Returns the Placemarks as a List.
     *
     * @return A List of Placemark objects
     */
    public List<Placemark> getPlacemarksAsList() {
        List<Placemark> list = new LinkedList();
        Set<String> nameSet = getPlacemarkNames();
        for (String name : nameSet) {
            Placemark placemark = getPlacemark(name);
            list.add(placemark);
        }
        return list;
    }

    /**
     * Returns the Placemark given its (unique) name, or null if it does not
     * exist.
     *
     * @param name The unique name of the placemark
     * @return The corresponding Placemark object
     */
    public Placemark getPlacemark(String name) {
        return placemarkMap.get(name);
    }

    /**
     * Returns a Set of the names of the Placemarks in this list.
     *
     * @return A Set<String> of Placemark names
     */
    public Set<String> getPlacemarkNames() {
        return placemarkMap.keySet();
    }

    /**
     * Adds a Placemark into the list of placemarks. If a Placemark with the
     * same name already exists in the list, this method does nothing.
     *
     * @param placemark The Placemark to add
     */
    public void addPlacemark(Placemark placemark) {
        placemarkMap.put(placemark.getName(), placemark);
    }

    /**
     * Removes a Placemark from the list of placemark, given it names. If a
     * Placemark with the given name does not exist, this method does nothing.
     *
     * @param name The name of the placemark
     */
    public void removePlacemark(String name) {
        placemarkMap.remove(name);
    }

    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the PlacemarkList class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to PlacemarkList
     * @throw JAXBException Upon error reading the XML file
     */
    public static PlacemarkList decode(Reader r) throws JAXBException {
        // Unmarshall the XML into a PlacemarkList class
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        PlacemarkList placemarkList = (PlacemarkList)unmarshaller.unmarshal(r);

        // Convert the array of Placemark objects into a Map
        for (Placemark placemark : placemarkList.placemarks) {
            placemarkList.placemarkMap.put(placemark.getName(), placemark);
        }
        return placemarkList;
    }

    /**
     * Writes the PlacemarkList class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        // Copy the contents of the Map of placemarks into the array.
        placemarks = new Placemark[placemarkMap.size()];
        int i = 0;
        for (String name : placemarkMap.keySet()) {
            Placemark placemark = placemarkMap.get(name);
            placemarks[i] = placemark;
            i++;
        }

        // Marshall the PlacemarkList class into XML
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }
}
