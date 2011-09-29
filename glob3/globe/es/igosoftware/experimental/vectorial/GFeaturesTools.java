

package es.igosoftware.experimental.vectorial;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GGlobeFeature;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GProgress;


public class GFeaturesTools {


   public GFeaturesTools() {

   }


   public static IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> processFeatureCollection(final String dataSource,
                                                                                                                              final GProjection projection,
                                                                                                                              final SimpleFeatureCollection featuresCollection) {

      final GIntHolder validCounter = new GIntHolder(0);
      final GIntHolder invalidCounter = new GIntHolder(0);

      final int featuresCount = featuresCollection.size();
      final ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> euclidFeatures = new ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               featuresCount);


      final GProgress progress = new GProgress(featuresCount) {
         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            System.out.println("Processing features from \"" + dataSource + "\" "
                               + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
         }
      };


      final FeatureIterator<SimpleFeature> iterator = featuresCollection.features();

      while (iterator.hasNext()) {

         final SimpleFeature feature = iterator.next();

         final GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();

         if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.Polygon.class) {

            final com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) geometryAttribute.getValue();

            try {
               final IPolygon2D euclidPolygon = createEuclidPolygon(projection, polygon);

               if (euclidPolygon != null) {
                  euclidFeatures.add(createFeature(euclidPolygon, feature));
                  validCounter.increment();
               }
            }
            catch (final IllegalArgumentException e) {
               //                     System.err.println(e.getMessage());
            }

         }
         //if (type.getBinding() == com.vividsolutions.jts.geom.MultiPolygon.class) {
         //if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiPolygon.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiPolygon.class) {

            final com.vividsolutions.jts.geom.MultiPolygon multipolygon = (com.vividsolutions.jts.geom.MultiPolygon) geometryAttribute.getValue();
            final int geometriesCount = multipolygon.getNumGeometries();

            final List<IPolygon2D> polygons = new ArrayList<IPolygon2D>(geometriesCount);
            for (int i = 0; i < geometriesCount; i++) {
               final com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) multipolygon.getGeometryN(i);

               try {
                  final IPolygon2D euclidPolygon = createEuclidPolygon(projection, jtsPolygon);

                  if (euclidPolygon != null) {
                     //                     euclidFeatures.add(createFeature(euclidPolygon, feature));
                     polygons.add(euclidPolygon);
                     validCounter.increment();
                  }
               }
               catch (final IllegalArgumentException e) {
                  //                     System.err.println(e.getMessage());
               }
            }

            if (!polygons.isEmpty()) {
               if (polygons.size() == 1) {
                  euclidFeatures.add(createFeature(polygons.get(0), feature));
               }
               else {
                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygon2D>(polygons), feature));
               }
            }

         }
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.LineString.class) {

            final com.vividsolutions.jts.geom.LineString line = (com.vividsolutions.jts.geom.LineString) geometryAttribute.getValue();

            try {
               final IPolygonalChain2D euclidLine = createLine(line.getCoordinates(), projection);

               euclidFeatures.add(createFeature(euclidLine, feature));
               validCounter.increment();

            }
            catch (final IllegalArgumentException e) {
               //                     System.err.println(e.getMessage());
            }

         }
         //else if (type.getBinding() == com.vividsolutions.jts.geom.MultiLineString.class) {
         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiLineString.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiLineString.class) {


            final com.vividsolutions.jts.geom.MultiLineString multiline = (com.vividsolutions.jts.geom.MultiLineString) geometryAttribute.getValue();
            final int geometriesCount = multiline.getNumGeometries();

            final List<IPolygonalChain2D> lines = new ArrayList<IPolygonalChain2D>(geometriesCount);
            for (int i = 0; i < geometriesCount; i++) {
               final com.vividsolutions.jts.geom.LineString jtsLine = (com.vividsolutions.jts.geom.LineString) multiline.getGeometryN(i);

               try {
                  final IPolygonalChain2D euclidLine = createLine(jtsLine.getCoordinates(), projection);

                  //euclidFeatures.add(createFeature(euclidLines, feature));
                  lines.add(euclidLine);
               }
               catch (final IllegalArgumentException e) {
                  //                     System.err.println(e.getMessage());
               }
            }

            if (!lines.isEmpty()) {
               if (lines.size() == 1) {
                  euclidFeatures.add(createFeature(lines.get(0), feature));
               }
               else {
                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygonalChain2D>(lines), feature));
               }
            }

            validCounter.increment();
         }
         //else if (type.getBinding() == com.vividsolutions.jts.geom.Point.class) {
         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.Point.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.Point.class) {
            final IVector2 euclidPoint = createPoint(
                     ((com.vividsolutions.jts.geom.Point) geometryAttribute.getValue()).getCoordinate(), projection);
            euclidFeatures.add(createFeature(euclidPoint, feature));

            validCounter.increment();
         }
         //else if (type.getBinding() == com.vividsolutions.jts.geom.MultiPoint.class) {
         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiPoint.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiPoint.class) {
            final IBoundedGeometry2D<? extends IFinite2DBounds<?>> euclidMultipoint = createEuclidMultiPoint(geometryAttribute,
                     projection);
            euclidFeatures.add(createFeature(euclidMultipoint, feature));

            validCounter.increment();
         }
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.GeometryCollection.class) {
            // TODO: TODO_handling_GeometryCollection;
            final com.vividsolutions.jts.geom.GeometryCollection geometryCollection = (com.vividsolutions.jts.geom.GeometryCollection) geometryAttribute.getValue();
            final int geometriesCount = geometryCollection.getNumGeometries();

            System.out.println("GEOMETRY COLLECTION size: " + geometriesCount);
            System.out.println("GEOMETRY COLLECTION type: " + geometryCollection.getGeometryType());


            for (int i = 0; i < geometriesCount; i++) {
               System.out.println("Geometry: " + geometryCollection.getGeometryN(i).getGeometryType());
               // proccess depending on the geometry type
            }

         }
         else {
            invalidCounter.increment();
            System.out.println("invalid type: " + geometryAttribute.getValue());
         }


         progress.stepDone();
      }

      //final SimpleFeatureType schema = null;
      //      if (dataStore != null) {
      //         schema = dataStore.getFeatureSource(dataSource).getSchema();
      //         //dataStore.dispose();
      //      }
      //      else {
      //         schema = featuresCollection.getSchema();
      //      }

      euclidFeatures.trimToSize();

      //System.out.println();
      System.out.println("Features: " + featuresCount);
      System.out.println("Read " + validCounter.get() + " valid geometries");

      if (invalidCounter.get() > 0) {
         System.out.println("Ignored " + invalidCounter.get() + " invalid geometries");
      }

      //System.out.println();

      //final SimpleFeatureType schema = featureSource.getSchema();
      final SimpleFeatureType schema = featuresCollection.getSchema();

      final int fieldsCount = schema.getAttributeCount();
      System.out.print("Fields (" + fieldsCount + "): ");
      final List<GField> fields = new ArrayList<GField>(fieldsCount);
      for (int i = 0; i < fieldsCount; i++) {
         final String fieldName = schema.getType(i).getName().getLocalPart();
         final Class<?> fieldType = schema.getType(i).getBinding();
         System.out.print(fieldName + ", ");

         fields.add(new GField(fieldName, fieldType));
      }
      System.out.println();
      System.out.println();


      return new GListFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(GProjection.EPSG_4326,
               fields, euclidFeatures);

   }


   private static List<IVector2> convert(final com.vividsolutions.jts.geom.Coordinate[] coordinates,
                                         final GProjection projection) {
      final List<IVector2> result = new ArrayList<IVector2>(coordinates.length);

      for (final com.vividsolutions.jts.geom.Coordinate coordinate : coordinates) {
         if (projection.isLatLong()) {
            result.add(new GVector2D(Math.toRadians(coordinate.x), Math.toRadians(coordinate.y)));
         }
         else {
            result.add(new GVector2D(coordinate.x, coordinate.y).reproject(projection, GProjection.EPSG_4326));
         }
      }

      return result;
   }


   private static List<IVector2> removeLastIfRepeated(final List<IVector2> points) {
      if (points.size() < 2) {
         return points;
      }

      final IVector2 first = points.get(0);
      final int lastIndex = points.size() - 1;
      final IVector2 last = points.get(lastIndex);
      if (first.closeTo(last)) {
         return points.subList(0, lastIndex - 1);
      }

      return points;
   }


   private static List<IVector2> removeConsecutiveEqualsPoints(final List<IVector2> points) {
      final int pointsCount = points.size();
      final ArrayList<IVector2> result = new ArrayList<IVector2>(pointsCount);

      for (int i = 0; i < pointsCount; i++) {
         final IVector2 current = points.get(i);
         final IVector2 next = points.get((i + 1) % pointsCount);
         if (!current.closeTo(next)) {
            result.add(current);
         }
      }

      result.trimToSize();
      return result;
   }


   private static IPolygon2D createEuclidPolygon(final GProjection projection,
                                                 final com.vividsolutions.jts.geom.Polygon jtsPolygon) {
      final ISimplePolygon2D outerEuclidPolygon = createPolygon(jtsPolygon.getExteriorRing().getCoordinates(), projection);

      final int holesCount = jtsPolygon.getNumInteriorRing();
      if (holesCount == 0) {
         return outerEuclidPolygon;
      }


      final List<ISimplePolygon2D> euclidHoles = new ArrayList<ISimplePolygon2D>(holesCount);
      for (int j = 0; j < holesCount; j++) {
         final com.vividsolutions.jts.geom.LineString jtsHole = jtsPolygon.getInteriorRingN(j);

         try {
            final ISimplePolygon2D euclidHole = createPolygon(jtsHole.getCoordinates(), projection);
            euclidHoles.add(euclidHole);
         }
         catch (final IllegalArgumentException e) {
            //                              System.err.println(e.getMessage());
         }
      }

      return euclidHoles.isEmpty() ? //
                                  outerEuclidPolygon : //
                                  new GComplexPolygon2D(outerEuclidPolygon, euclidHoles);

   }


   private static IBoundedGeometry2D<? extends IFinite2DBounds<?>> createEuclidMultiPoint(final GeometryAttribute geometryAttribute,
                                                                                          final GProjection projection) {
      final com.vividsolutions.jts.geom.MultiPoint multipoint = (com.vividsolutions.jts.geom.MultiPoint) geometryAttribute.getValue();

      if (multipoint.getNumGeometries() == 1) {
         final com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) multipoint.getGeometryN(0);
         return createPoint(jtsPoint.getCoordinate(), projection);
      }

      final IVector2[] euclidPoints = new IVector2[multipoint.getNumGeometries()];

      for (int i = 0; i < euclidPoints.length; i++) {
         final com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) multipoint.getGeometryN(i);
         euclidPoints[i] = createPoint(jtsPoint.getCoordinate(), projection);
      }

      return new GMultiGeometry2D<IVector2>(euclidPoints);
   }


   private static IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> createFeature(final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry,
                                                                                                          final SimpleFeature feature) {
      //      final List<Object> featureAttributes = feature.getAttributes();
      //      return new GGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(geometry, featureAttributes.subList(1,
      //               featureAttributes.size()));

      return new GGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(geometry, feature.getAttributes());
   }


   private static ISimplePolygon2D createPolygon(final com.vividsolutions.jts.geom.Coordinate[] jtsCoordinates,
                                                 final GProjection projection) {
      final List<IVector2> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShapeUtils.createPolygon2(false, points);
   }


   private static IPolygonalChain2D createLine(final com.vividsolutions.jts.geom.Coordinate[] jtsCoordinates,
                                               final GProjection projection) {
      final List<IVector2> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShapeUtils.createLine2(false, points);
   }


   private static IVector2 createPoint(final com.vividsolutions.jts.geom.Coordinate coordinate,
                                       final GProjection projection) {

      if (projection.isLatLong()) {
         return new GVector2D(Math.toRadians(coordinate.x), Math.toRadians(coordinate.y));
      }

      return new GVector2D(coordinate.x, coordinate.y).reproject(projection, GProjection.EPSG_4326);
   }


}
