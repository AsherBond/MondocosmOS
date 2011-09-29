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

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GGlobeFeature;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.experimental.vectorial.symbolizer.GGlobeVectorialSymbolizer2D;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.render.markers.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.geonames.InsufficientStyleException;
import org.geonames.Toponym;


public class GSearchResultLayer
         extends
            MarkerLayer
         implements
            IGlobeVector2Layer {


   private final Sector                                                                              _extent;
   private final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> _features;


   public GSearchResultLayer(final String searchText,
                             final List<Marker> markersList) {
      super(markersList);

      final List<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> features = new ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               markersList.size());

      double minLongitude = Double.POSITIVE_INFINITY;
      double maxLongitude = Double.NEGATIVE_INFINITY;
      double minLatitude = Double.POSITIVE_INFINITY;
      double maxLatitude = Double.NEGATIVE_INFINITY;

      for (final Marker marker : markersList) {
         final Position position = marker.getPosition();
         final double longitude = position.longitude.degrees;
         final double latitude = position.latitude.degrees;

         minLatitude = Math.min(minLatitude, longitude);
         maxLatitude = Math.max(maxLatitude, longitude);

         minLongitude = Math.min(minLongitude, latitude);
         maxLongitude = Math.max(maxLongitude, latitude);

         //         final Point point = geomFactory.createPoint(new Coordinate(longitude, latitude));
         final GVector2D point = new GVector2D(latitude, longitude);

         boolean added = false;

         if (marker instanceof GSearchResultMarker) {
            final Toponym toponym = ((GSearchResultMarker) marker).getToponym();
            try {
               final List<Object> attribs = Arrays.asList(new Object[] {
                                 toponym.getName(),
                                 toponym.getPopulation()
               });
               features.add(new GGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(point, attribs));
               added = true;
            }
            catch (final InsufficientStyleException e) {
               // just ignore, the feature will be added later
            }
         }

         if (!added) {
            features.add(new GGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(point,
                     Arrays.asList(new Object[] {
                                       "",
                                       Long.valueOf(0)
                     })));
         }
      }

      _extent = Sector.fromDegrees(minLatitude, maxLatitude, minLongitude, maxLongitude);
      final List<GField> fields = Arrays.asList(new GField("Name", String.class), new GField("Population", Integer.class));
      setName("Search result: " + searchText);
      _features = new GListFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(GProjection.EPSG_4326,
               fields, features);
   }


   @Override
   public Sector getExtent() {
      return _extent;
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return null;
   }


   //   @Override
   //   public GProjection getProjection() {
   //      return _features.getProjection();
   //   }


   @Override
   public final void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> getFeaturesCollection() {
      return _features;
   }


   @Override
   public GGlobeVectorialSymbolizer2D getSymbolizer() {
      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeRunningContext context) {
      context.getCameraController().animatedZoomToSector(getExtent());
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public void clearCache() {

   }

}
