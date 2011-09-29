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


package es.igosoftware.experimental.vectorial;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GGlobeFeature;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.utils.GJTSUtils;


public class GShapefileTools {

   private GShapefileTools() {
   }


   public static IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> readFile(final File file)
                                                                                                                              throws IOException {

      final HashMap<String, URL> connect = new HashMap<String, URL>();
      connect.put("url", file.toURI().toURL());

      final DataStore dataStore = DataStoreFinder.getDataStore(connect);

      final Query query = new Query(dataStore.getTypeNames()[0], Filter.INCLUDE);
      final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(query.getTypeName());
      final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = featureSource.getFeatures(query);
      final FeatureIterator<SimpleFeature> iterator = featureCollection.features();
      final List<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> features = new ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               featureCollection.size());

      while (iterator.hasNext()) {
         final SimpleFeature feature = iterator.next();
         final Geometry jtsGeometry = (Geometry) feature.getDefaultGeometry();
         features.add(new GGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(
                  GJTSUtils.toEuclid(jtsGeometry), feature.getAttributes()));
      }

      final SimpleFeatureType schema = featureSource.getSchema();
      final int fieldsCount = schema.getAttributeCount() - 1;
      final List<GField> fields = new ArrayList<GField>(fieldsCount);
      for (int i = 0; i < fieldsCount; i++) {
         final String fieldName = schema.getType(i + 1).getName().getLocalPart();
         final Class<?> fieldType = schema.getType(i + 1).getBinding();

         fields.add(new GField(fieldName, fieldType));
      }

      // TODO: read projection or ask user
      final GProjection projection = GProjection.EPSG_4326;

      return new GListFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(projection, fields, features);
   }

}
