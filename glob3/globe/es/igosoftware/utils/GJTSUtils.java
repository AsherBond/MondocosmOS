

package es.igosoftware.utils;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.multigeometry.GMultiLine2D;
import es.igosoftware.euclid.multigeometry.GMultiPoint2D;
import es.igosoftware.euclid.multigeometry.GMultiPolygon2D;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.IComplexPolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


/**
 * 
 * Utility methods to convert JTS geometries from/to Euclid geometries
 * 
 * 
 * @author dgd
 */
public class GJTSUtils {

   private static final GeometryFactory JTS_GEOMETRY_FACTORY = new GeometryFactory();


   private GJTSUtils() {
      // do not instantiate, just static methods
   }


   public static IBoundedGeometry2D<? extends IFinite2DBounds<?>> toEuclid(final Geometry jtsGeometry) {
      if (jtsGeometry == null) {
         return null;
      }
      else if (jtsGeometry instanceof Point) {
         return toEuclid((Point) jtsGeometry);
      }
      else if (jtsGeometry instanceof Polygon) {
         return toEuclid((Polygon) jtsGeometry);
      }
      else if (jtsGeometry instanceof LineString) {
         return toEuclid((LineString) jtsGeometry);
      }
      else if (jtsGeometry instanceof MultiPoint) {
         return toEuclid((MultiPoint) jtsGeometry);
      }
      else if (jtsGeometry instanceof MultiLineString) {
         return toEuclid((MultiLineString) jtsGeometry);
      }
      else if (jtsGeometry instanceof MultiPolygon) {
         return toEuclid((MultiPolygon) jtsGeometry);
      }
      else {
         throw new RuntimeException("JTS Geometry not supported " + jtsGeometry.getGeometryType());
      }
   }


   public static IVector2 toEuclid(final Point jtsPoint) {
      if (jtsPoint == null) {
         return null;
      }

      return new GVector2D(jtsPoint.getX(), jtsPoint.getY());
   }


   public static IPolygon2D toEuclid(final Polygon jtsPolygon) {
      if (jtsPolygon == null) {
         return null;
      }

      final ISimplePolygon2D outerPolygon = createEuclidPolygon(jtsPolygon.getCoordinates());

      final int holesCount = jtsPolygon.getNumInteriorRing();
      if (holesCount == 0) {
         return outerPolygon;
      }


      final List<ISimplePolygon2D> jtsHoles = new ArrayList<ISimplePolygon2D>(holesCount);
      for (int j = 0; j < holesCount; j++) {
         final LineString jtsHole = jtsPolygon.getInteriorRingN(j);

         jtsHoles.add(createEuclidPolygon(jtsHole.getCoordinates()));
      }

      return new GComplexPolygon2D(outerPolygon, jtsHoles);
   }


   public static ISimplePolygon2D createEuclidPolygon(final Coordinate... coordinates) {
      return GShapeUtils.createPolygon2(false, toEuclid(coordinates));
   }


   public static IPolygonalChain2D createEuclidLine(final Coordinate... coordinates) {
      return GShapeUtils.createLine2(false, toEuclid(coordinates));
   }


   public static IVector2[] toEuclid(final Coordinate... coordinates) {
      final IVector2[] points = new IVector2[coordinates.length];
      for (int i = 0; i < coordinates.length; i++) {
         final Coordinate coordinate = coordinates[i];
         points[i] = new GVector2D(coordinate.x, coordinate.y);
      }
      return points;
   }


   public static IPolygonalChain2D toEuclid(final LineString jtsLine) {
      if (jtsLine == null) {
         return null;
      }

      return createEuclidLine(jtsLine.getCoordinates());
   }


   public static GMultiPoint2D toEuclid(final MultiPoint jtsPoints) {
      if (jtsPoints == null) {
         return null;
      }

      final int count = jtsPoints.getNumGeometries();
      final List<IVector2> points = new ArrayList<IVector2>(count);
      for (int i = 0; i < count; i++) {
         points.add(toEuclid((Point) jtsPoints.getGeometryN(i)));
      }

      return new GMultiPoint2D(points);
   }


   public static GMultiLine2D toEuclid(final MultiLineString jtsLines) {
      if (jtsLines == null) {
         return null;
      }

      final int count = jtsLines.getNumGeometries();
      final List<IPolygonalChain2D> lines = new ArrayList<IPolygonalChain2D>(count);
      for (int i = 0; i < count; i++) {
         lines.add(toEuclid((LineString) jtsLines.getGeometryN(i)));
      }

      return new GMultiLine2D(lines);
   }


   public static GMultiPolygon2D toEuclid(final MultiPolygon jtsPolygons) {
      if (jtsPolygons == null) {
         return null;
      }

      final int count = jtsPolygons.getNumGeometries();
      final List<IPolygon2D> polygons = new ArrayList<IPolygon2D>(count);
      for (int i = 0; i < count; i++) {
         polygons.add(toEuclid((Polygon) jtsPolygons.getGeometryN(i)));
      }

      return new GMultiPolygon2D(polygons);
   }


   @SuppressWarnings("unchecked")
   public static Geometry toJTS(final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry) {
      if (geometry == null) {
         return null;
      }

      if (geometry instanceof IVector2) {
         return toJTS(((IVector2) geometry));
      }
      else if (geometry instanceof IPolygonalChain2D) {
         return toJTS((IPolygonalChain2D) geometry);
      }
      else if (geometry instanceof IPolygon2D) {
         return toJTS((IPolygon2D) geometry);
      }
      else if (geometry instanceof GMultiGeometry2D<?>) {
         return toJTS((GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) geometry);
      }
      else {
         throw new RuntimeException("Euclid geometry not supported (" + geometry.getClass() + ")");
      }
   }


   private static Coordinate[] getJTSCoordinates(final IPointsContainer<IVector2> pointsContainer) {
      if (pointsContainer == null) {
         return null;
      }

      final Coordinate[] result = new Coordinate[pointsContainer.getPointsCount()];

      for (int i = 0; i < result.length; i++) {
         final IVector2 point = pointsContainer.getPoint(i);
         result[i] = new Coordinate(point.x(), point.y());
      }

      return result;
   }


   public static LineString toJTS(final IPolygonalChain2D lineal) {
      if (lineal == null) {
         return null;
      }

      return JTS_GEOMETRY_FACTORY.createLineString(getJTSCoordinates(lineal));
   }


   public static Polygon toJTS(final IPolygon2D polygon) {
      if (polygon == null) {
         return null;
      }

      if (polygon instanceof IComplexPolygon2D) {
         return toJTS((IComplexPolygon2D) polygon);
      }


      final LinearRing jtsShell = JTS_GEOMETRY_FACTORY.createLinearRing(closeLinearRing(getJTSCoordinates(polygon)));
      final LinearRing[] jtsHoles = null;
      return JTS_GEOMETRY_FACTORY.createPolygon(jtsShell, jtsHoles);
      //      else {
      //         throw new RuntimeException("Polygon type not supported (" + polygon.getClass() + ")");
      //      }
   }


   private static Coordinate[] closeLinearRing(final Coordinate[] coordinates) {

      if (coordinates[coordinates.length - 1].equals2D(coordinates[0])) {
         return coordinates;
      }

      final Coordinate[] coords = new Coordinate[coordinates.length + 1];
      System.arraycopy(coordinates, 0, coords, 0, coordinates.length);
      coords[coords.length - 1] = coords[0];
      return coords;

   }


   public static Polygon toJTS(final IComplexPolygon2D polygon) {
      if (polygon == null) {
         return null;
      }

      final LinearRing jtsShell = JTS_GEOMETRY_FACTORY.createLinearRing(closeLinearRing(getJTSCoordinates(polygon)));

      final List<ISimplePolygon2D> holes = polygon.getHoles();
      final LinearRing[] jtsHoles = new LinearRing[holes.size()];

      for (int i = 0; i < jtsHoles.length; i++) {
         jtsHoles[i] = JTS_GEOMETRY_FACTORY.createLinearRing(closeLinearRing(getJTSCoordinates(holes.get(i))));
      }

      return JTS_GEOMETRY_FACTORY.createPolygon(jtsShell, jtsHoles);
   }


   public static GeometryCollection toJTS(final GMultiGeometry2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry) {
      final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> exemplar = multigeometry.getChild(0);

      if (exemplar instanceof IPolygonalChain2D) {
         return createMultiLineString(multigeometry);
      }
      else if (exemplar instanceof IPolygon2D) {
         return createMultiPolygon(multigeometry);
      }
      else if (exemplar instanceof IVector2) {
         return createMultiPoint(multigeometry);
      }
      else {
         throw new RuntimeException("Multigeometry children's type not supported (" + exemplar.getClass() + ")");
      }
   }


   private static MultiLineString createMultiLineString(final GMultiGeometry2D<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry) {
      final LineString[] children = new LineString[multigeometry.getChildrenCount()];
      int index = 0;

      for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
         children[index++] = toJTS((IPolygonalChain2D) child);
      }

      return JTS_GEOMETRY_FACTORY.createMultiLineString(children);
   }


   private static MultiPolygon createMultiPolygon(final GMultiGeometry2D<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry) {
      final Polygon[] children = new Polygon[multigeometry.getChildrenCount()];
      int index = 0;

      for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
         children[index++] = toJTS((IPolygon2D) child);
      }

      return JTS_GEOMETRY_FACTORY.createMultiPolygon(children);
   }


   private static MultiPoint createMultiPoint(final GMultiGeometry2D<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry) {
      final Point[] children = new Point[multigeometry.getChildrenCount()];
      int index = 0;

      for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
         children[index++] = toJTS((IVector2) child);
      }

      return JTS_GEOMETRY_FACTORY.createMultiPoint(children);
   }


   public static Point[] toJTS(final List<IVector2> points) {
      if (points == null) {
         return null;
      }

      final Point[] result = new Point[points.size()];
      for (int i = 0; i < result.length; i++) {
         result[i] = toJTS(points.get(i));
      }

      return result;
   }


   public static Point[] toJTS(final IVector2... points) {
      final Point[] result = new Point[points.length];

      for (int i = 0; i < result.length; i++) {
         result[i] = toJTS(points[i]);
      }

      return result;
   }


   public static Point toJTS(final IVector2 vector) {
      if (vector == null) {
         return null;
      }

      return JTS_GEOMETRY_FACTORY.createPoint(new Coordinate(vector.x(), vector.y()));
   }


}
