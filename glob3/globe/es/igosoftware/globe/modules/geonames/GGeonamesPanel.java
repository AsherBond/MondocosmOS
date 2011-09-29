/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.globe.modules.geonames;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.render.markers.MarkerAttributes;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import es.igosoftware.globe.IGlobeRunningContext;


public class GGeonamesPanel
         extends
            JPanel {
   private static final long          serialVersionUID  = 1L;


   public static final String         SEARCH_LAYER_NAME = "Search layer";

   private JButton                    jButtonSearch;
   private JTextField                 jTextField;
   private JList                      jList;
   private JScrollPane                jScrollPane;

   private final IGlobeRunningContext _context;


   public GGeonamesPanel(final IGlobeRunningContext context,
                         final Dimension d) {

      super();

      _context = context;
      this.setSize(d);
      initGUI();

   }


   private void initGUI() {

      try {
         final TableLayout thisLayout = new TableLayout(new double[][] {
                           {
                                             7.0,
                                             TableLayoutConstants.FILL,
                                             TableLayoutConstants.FILL,
                                             TableLayoutConstants.FILL,
                                             6.0
                           },
                           {
                                             6.0,
                                             TableLayoutConstants.MINIMUM,
                                             TableLayoutConstants.FILL,
                                             TableLayoutConstants.FILL,
                                             TableLayoutConstants.MINIMUM,
                                             6.0
                           }
         });
         thisLayout.setHGap(5);
         thisLayout.setVGap(5);
         setLayout(thisLayout);
         {
            jButtonSearch = new JButton();
            this.add(jButtonSearch, "3, 1");
            jButtonSearch.setText("Search");
            jButtonSearch.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(final ActionEvent evt) {
                  search();
               }
            });
         }
         {
            jTextField = new JTextField();
            this.add(jTextField, "1, 1, 2, 1");
            jTextField.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(final ActionEvent evt) {
                  search();
               }
            });
         }
         {
            final ListModel jListModel = new DefaultListModel();
            jList = new JList();
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(jList);
            this.add(jScrollPane, "1, 2, 3, 3");
            jList.setModel(jListModel);
            jList.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseClicked(final MouseEvent e) {
                  if (e.getClickCount() == 2) {
                     final int iIndex = jList.locationToIndex(e.getPoint());
                     final ListModel dlm = jList.getModel();
                     final Object item = dlm.getElementAt(iIndex);
                     jList.ensureIndexIsVisible(iIndex);
                     final Toponym toponym = ((MyToponym) item).getToponym();
                     final Sector sector = new Sector(Angle.fromDegrees(toponym.getLatitude() - .05),
                              Angle.fromDegrees(toponym.getLatitude() + .05), Angle.fromDegrees(toponym.getLongitude() - .05),
                              Angle.fromDegrees(toponym.getLongitude() + .05));
                     _context.getCameraController().animatedZoomToSector(sector);
                  }
               }

            });
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }
   }


   private void search() {

      final DefaultListModel jListModel = new DefaultListModel();
      final ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
      final String searchText = jTextField.getText();
      searchCriteria.setQ(searchText);
      searchCriteria.setStyle(Style.LONG);
      Sector totalSector = null;
      final MarkerAttributes markerAttributes = new BasicMarkerAttributes(Material.RED, BasicMarkerShape.SPHERE, 1d, 10, 5);

      try {
         final ToponymSearchResult searchResult = WebService.search(searchCriteria);
         final LayerList layers = _context.getWorldWindModel().getLayerList();
         final Layer existingLayer = layers.getLayerByName(SEARCH_LAYER_NAME);
         if (existingLayer != null) {
            layers.remove(existingLayer);
         }
         MarkerLayer layer = null;
         final ArrayList<Marker> list = new ArrayList<Marker>();
         for (final Toponym toponym : searchResult.getToponyms()) {
            if (toponym.getName().toLowerCase().indexOf(searchText.toLowerCase()) != -1) {
               jListModel.addElement(new MyToponym(toponym));
               final LatLon latlon = new LatLon(Angle.fromDegrees(toponym.getLatitude()),
                        Angle.fromDegrees(toponym.getLongitude()));
               final GSearchResultMarker marker = new GSearchResultMarker(new Position(latlon, 0d), markerAttributes, toponym);
               marker.setHeading(Angle.fromDegrees(toponym.getLatitude() * 5));
               list.add(marker);

               final Sector sector = new Sector(Angle.fromDegrees(toponym.getLatitude() - .5),
                        Angle.fromDegrees(toponym.getLatitude() + .5), Angle.fromDegrees(toponym.getLongitude() - .5),
                        Angle.fromDegrees(toponym.getLongitude() + .5));

               if (totalSector == null) {
                  totalSector = sector;
               }
               else {
                  totalSector = Sector.union(sector, totalSector);
               }
            }
         }
         if (list.size() != 0) {
            layer = new GSearchResultLayer(searchText, list);
            layer.setOverrideMarkerElevation(true);
            layer.setKeepSeparated(false);
            layer.setName(SEARCH_LAYER_NAME);
            _context.getWorldWindModel().addLayer(layer);
         }
         _context.getCameraController().animatedZoomToSector(totalSector);
      }
      catch (final Exception e) {}
      finally {
         jList.setModel(jListModel);
      }

   }

   private static class MyToponym
            extends
               Toponym {

      private final Toponym toponym;


      public MyToponym(final Toponym t) {

         toponym = t;

      }


      @Override
      public String toString() {

         return toponym.getName() + "[" + toponym.getCountryName() + "]" + "[" + toponym.getFeatureClassName() + "]";

      }


      public Toponym getToponym() {

         return toponym;

      }

   }

}
