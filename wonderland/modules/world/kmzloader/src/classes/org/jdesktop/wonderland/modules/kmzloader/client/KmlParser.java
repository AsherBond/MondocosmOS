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
package org.jdesktop.wonderland.modules.kmzloader.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author paulby
 */
public class KmlParser {

    private final static Logger logger = Logger.getLogger(KmlParser.class.getName());
    private ArrayList<KmlModel> models = new ArrayList();

    public KmlParser() {
    }

    public void decodeKML(InputStream is) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.parse(is);

        NodeList folders = d.getElementsByTagName("Folder");
        for (int i = 0; i < folders.getLength(); i++) {
            processFolder((Element) folders.item(i));
        }
    }

    public void processFolder(Element folder) {
        String name = getFirstTagValue("name", folder);
        String desc = getFirstTagValue("description", folder);

        logger.fine("Found folder: " + name + " : " + desc);

        NodeList placemarks = folder.getElementsByTagName("Placemark");
        for (int i = 0; i < placemarks.getLength(); i++) {
            processPlacemark((Element) placemarks.item(i));
        }
    }

    public void processPlacemark(Element placemark) {
        String name = getFirstTagValue("name", placemark);

        logger.fine("Found placemark: " + name);

        NodeList models = placemark.getElementsByTagName("Model");
        for (int i = 0; i < models.getLength(); i++) {
            processModel((Element) models.item(i));
        }
    }

    public void processModel(Element model) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        NodeList locations = model.getElementsByTagName("Location");
        if (locations.getLength() == 1) {
            Element location = (Element) locations.item(0);
            x = Double.parseDouble(getFirstTagValue("longitude", location));
            y = Double.parseDouble(getFirstTagValue("latitude", location));
            z = Double.parseDouble(getFirstTagValue("altitude", location));
        }

        double sX = 1.0;
        double sY = 1.0;
        double sZ = 1.0;

        NodeList scales = model.getElementsByTagName("Scale");
        if (scales.getLength() == 1) {
            Element scale = (Element) scales.item(0);
            sX = Double.parseDouble(getFirstTagValue("x", scale));
            sY = Double.parseDouble(getFirstTagValue("y", scale));
            sZ = Double.parseDouble(getFirstTagValue("z", scale));
        }

        String href = null;

        NodeList links = model.getElementsByTagName("Link");
        if (links.getLength() == 1) {
            Element link = (Element) links.item(0);
            href = getFirstTagValue("href", link);
        }


        logger.fine("Model href: " + href +
                           " Location: (" + x + "," + y + "," + z + ")" +
                           " Scale: (" + sX + "," + sY + "," + sZ + ")");
        models.add(new KmlModel(href));
    }

    public List<KmlModel> getModels() {
        return models;
    }

    private static String getFirstTagValue(String sTag, Element eElement) {

        NodeList tagList = eElement.getElementsByTagName(sTag);
        if (tagList.getLength() == 0) {
            return null;
        }

        NodeList nlList = tagList.item(0).getChildNodes();
        if (nlList.getLength() == 0) {
            return null;
        }

        return nlList.item(0).getNodeValue();
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        try {
////            File kmz = new File(args[0]);
//            File kmz = new File("/Users/paulby/Documents/stl/plane2.2.kmz");
//
//            KmlParser main = new KmlParser(kmz);
//            InputStream is = main.findKML();
//            main.decodeKML(is);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public class KmlModel {
        private String href;

        KmlModel(String href) {
            this.href = href;
        }

        public String getHref() {
            return href;
        }
    }

}
