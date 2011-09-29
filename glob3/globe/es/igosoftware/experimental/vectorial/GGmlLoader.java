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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.xml.sax.SAXException;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.GCompositeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GUndeterminateProgress;


public class GGmlLoader {


   //public static Collection<IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> readGml2Features(final File file,
   public static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> readGml2Features(final File file,
                                                                                                                                final GProjection projection)
                                                                                                                                                             throws IOException,
                                                                                                                                                             SAXException,
                                                                                                                                                             ParserConfigurationException {
      if (!file.exists()) {
         throw new IOException("File not found!");
      }

      final org.geotools.xml.Configuration configuration = new org.geotools.gml2.GMLConfiguration();
      //      configuration.getProperties().add(Parser.Properties.IGNORE_SCHEMA_LOCATION);
      //      configuration.getProperties().add(Parser.Properties.PARSE_UNKNOWN_ELEMENTS);

      //the xml instance document above
      //System.out.println("File= " + file.getPath());
      final InputStream gmlStream = new FileInputStream(file);

      final org.geotools.xml.StreamingParser parser = new org.geotools.xml.StreamingParser(configuration, gmlStream,
               org.geotools.gml2.GML.featureMember);
      //    SimpleFeature.class);

      return readFeatures(file, projection, parser);
   }


   //public static Collection<IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> readGml3Features(final File file,
   public static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> readGml3Features(final File file,
                                                                                                                                final GProjection projection)
                                                                                                                                                             throws IOException,
                                                                                                                                                             SAXException,
                                                                                                                                                             ParserConfigurationException {
      if (!file.exists()) {
         throw new IOException("File not found!");
      }

      final org.geotools.xml.Configuration configuration = new org.geotools.gml3.GMLConfiguration();
      //      configuration.getProperties().add(Parser.Properties.IGNORE_SCHEMA_LOCATION);
      //      configuration.getProperties().add(Parser.Properties.PARSE_UNKNOWN_ELEMENTS);

      //the xml instance document above
      //System.out.println("File= " + file.getPath());
      final InputStream gmlStream = new FileInputStream(file);

      final org.geotools.xml.StreamingParser parser = new org.geotools.xml.StreamingParser(configuration, gmlStream,
               org.geotools.gml3.GML.featureMember);
      //    SimpleFeature.class);

      return readFeatures(file, projection, parser);
   }


   //public static Collection<IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> readFeatures(final File file,
   public static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> readFeatures(final File file,
                                                                                                                            final GProjection projection,
                                                                                                                            final org.geotools.xml.StreamingParser parser) {


      final Map<SimpleFeatureType, SimpleFeatureCollection> featuresCollectionMap = new HashMap<SimpleFeatureType, SimpleFeatureCollection>();
      //final SimpleFeatureCollection featuresCollection = FeatureCollections.newCollection();

      final GUndeterminateProgress progress = new GUndeterminateProgress() {
         @Override
         public void informProgress(final long elapsed) {
            System.out.println("Loading \"" + file.getName() + "\" " + progressString(elapsed));
         }
      };

      SimpleFeature f = null;
      int featureCounter = 0;
      //System.out.print("Loading \"" + file.getName() + "\" [");
      while ((f = (SimpleFeature) parser.parse()) != null) {
         featureCounter++;
         if (f.getDefaultGeometry() != null) {

            final SimpleFeatureType fSchema = f.getFeatureType();
            if (featuresCollectionMap.containsKey(fSchema)) {
               final SimpleFeatureCollection fc = featuresCollectionMap.get(fSchema);
               fc.add(f);
            }
            else {
               final SimpleFeatureCollection newFc = FeatureCollections.newCollection();
               newFc.add(f);
               featuresCollectionMap.put(fSchema, newFc);
            }

            progress.stepDone();
         }

      }
      progress.finish();

      //System.out.println("]");
      System.out.println("Features found: " + featureCounter);
      System.out.println("Schemas found: " + featuresCollectionMap.size());
      System.out.println();

      final List<IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> globeFeaturesCollection = new ArrayList<IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               featuresCollectionMap.size());


      for (final SimpleFeatureCollection fc : featuresCollectionMap.values()) {
         final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> globeFeatureCollection = GFeaturesTools.processFeatureCollection(
                  file.getName(), projection, fc);
         globeFeaturesCollection.add(globeFeatureCollection);
      }

      return new GCompositeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(globeFeaturesCollection);

   }


   //   public static void main(final String[] args) throws IOException {
   //      System.out.println("GShapeLoader 0.1");
   //      System.out.println("----------------\n");
   //
   //      final GFileName samplesDirectory = GFileName.absolute("home", "dgd", "Desktop", "sample-shp");
   //
   //      final GFileName fileName = GFileName.fromParentAndParts(samplesDirectory, "shp", "great_britain.shp", "roads.shp");
   //      //      final GFileName fileName = GFileName.fromParentAndParts(samplesDirectory, "cartobrutal", "world-modified", "world.shp");
   //      //      final GFileName fileName = GFileName.fromParentAndParts(samplesDirectory, "shp", "argentina.shp", "places.shp");
   //
   //      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = GKmlLoader.readFeatures(
   //               fileName, GProjection.EPSG_4326);
   //
   //
   //      System.out.println(features);
   //   }


}
