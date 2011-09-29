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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.geonames.Toponym;
import org.geonames.WebService;
import org.geonames.WikipediaArticle;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeWorldWindModel;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.MarkerAttributes;


public class GSearchGeonamesModule
         extends
            GAbstractGlobeModule {


   public static final String    SEARCH_ANNOTATION_LAYER_NAME = "SEARCH_ANNOTATION_LAYER_NAME";
   private GSearchResultMarker   lastHighlit;
   private BasicMarkerAttributes lastAttrs;


   public GSearchGeonamesModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getName() {
      return "Search GeoNames";
   }


   @Override
   public String getDescription() {
      return "Search GeoNames";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeRunningContext context) {
      final ArrayList<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      panels.add(new GPair<String, Component>("Search", new GGeonamesPanel(context, new Dimension(250, 500))));

      return panels;
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {
      addListener(context);
   }


   private void addListener(final IGlobeRunningContext context) {

      final IGlobeWorldWindModel wwModel = context.getWorldWindModel();

      wwModel.getWorldWindowGLCanvas().addSelectListener(new SelectListener() {

         @Override
         public void selected(final SelectEvent event) {

            if (event.getTopObject() instanceof GlobeAnnotation) {
               return;
            }

            if ((lastHighlit != null) && ((event.getTopObject() == null) || !event.getTopObject().equals(lastHighlit))) {
               lastHighlit.setAttributes(lastAttrs);
               lastHighlit = null;
               final Layer layer = wwModel.getLayerList().getLayerByName(SEARCH_ANNOTATION_LAYER_NAME);
               if (layer != null) {
                  wwModel.removeLayer(layer);
               }
            }

            if ((event.getTopObject() == null) || (event.getTopPickedObject().getParentLayer() == null)) {
               return;
            }

            if (!event.getEventAction().equals(SelectEvent.ROLLOVER)) {
               return;
            }

            if ((lastHighlit == null) && (event.getTopObject() instanceof GSearchResultMarker)) {
               lastHighlit = (GSearchResultMarker) event.getTopObject();
               lastAttrs = (BasicMarkerAttributes) lastHighlit.getAttributes();
               final MarkerAttributes highliteAttrs = new BasicMarkerAttributes(lastAttrs);
               highliteAttrs.setMaterial(Material.WHITE);
               highliteAttrs.setOpacity(1d);
               highliteAttrs.setMarkerPixels(lastAttrs.getMarkerPixels() * 1.4);
               highliteAttrs.setMinMarkerSize(lastAttrs.getMinMarkerSize() * 1.4);
               lastHighlit.setAttributes(highliteAttrs);

               final Toponym toponym = lastHighlit.getToponym();
               final AnnotationLayer annotationsLayer = new AnnotationLayer();
               final Position pos = new Position(Angle.fromDegrees(toponym.getLatitude()),
                        Angle.fromDegrees(toponym.getLongitude()), 0);
               String sAnnotationText = toponym.getName();
               try {
                  final List<WikipediaArticle> list = WebService.wikipediaSearchForTitle(toponym.getName(),
                           toponym.getCountryCode());
                  for (int i = 0; i < list.size(); i++) {
                     final WikipediaArticle wiki = list.get(i);
                     if (wiki.getTitle().equalsIgnoreCase(toponym.getName())) {
                        sAnnotationText = "<p>\n<b><font color=\"#664400\">" + toponym.getName()
                                          + "</font></b><br />\n<br />\n<p>" + list.get(0).getSummary() + "</p>";
                        break;
                     }
                  }

               }
               catch (final Exception e) {
                  //ignore
               }
               final GlobeAnnotation annotation = new GlobeAnnotation(sAnnotationText, pos);
               annotation.getAttributes().setSize(new Dimension(200, 0));
               annotationsLayer.addAnnotation(annotation);
               annotationsLayer.setName(SEARCH_ANNOTATION_LAYER_NAME);
               //annotationsLayer.setMaxActiveAltitude(30000d);

               wwModel.addLayer(annotationsLayer);

            }
         }
      });

   }


}
