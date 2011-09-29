

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GPolygonSegment2DIntersections
         implements
            IAlgorithm<IVector2, GPolygonSegment2DIntersections.Parameters, IVector2, GPolygonSegment2DIntersections.Result> {


   public static class Parameters
            implements
               IAlgorithmParameters<IVector2> {
      private final IPolygon2D _polygon;
      private final GSegment2D _segment;


      public Parameters(final IPolygon2D polygon,
                        final GSegment2D segment) {
         GAssert.notNull(polygon, "polygon");
         GAssert.notNull(segment, "segment");

         _polygon = polygon;
         _segment = segment;
      }

   }

   public static class Result
            implements
               IAlgorithmResult<IVector2> {

      private final List<GSegment2D> _intersections;


      private Result(final List<GSegment2D> intersections) {
         _intersections = intersections;
      }


      public List<GSegment2D> getIntersections() {
         return _intersections;
      }

   }


   @Override
   public String getName() {
      return "Polygon-Segment 2D Intersections";
   }


   @Override
   public String getDescription() {
      return "Calculate the intersections of a given segment over a given polygon";
   }


   public static List<GSegment2D> getIntersections(final IPolygon2D polygon,
                                                   final GSegment2D segment) {


      List<IVector2> points = new LinkedList<IVector2>();
      for (final GSegment2D edge : polygon.getEdges()) {
         final IVector2 intersectionPoint = segment.getIntersectionPoint(edge);
         if (intersectionPoint != null) {
            points.add(intersectionPoint);
         }
      }

      Collections.sort(points, new Comparator<IVector2>() {
         @Override
         public int compare(final IVector2 point1,
                            final IVector2 point2) {
            return Double.compare(segment.getMu(point1), segment.getMu(point2));
         }
      });


      final boolean startInPolygon = polygon.contains(segment._from);
      if (startInPolygon) {
         points.add(0, segment._from);
      }
      final boolean endsInPolygon = polygon.contains(segment._to);
      if (endsInPolygon) {
         points.add(segment._to);
      }

      points = removeConsecutiveEqualsPoints(points);

      GAssert.isTrue(GMath.isDivisible(points.size(), 2), "points size must be even");

      final List<GSegment2D> result = new ArrayList<GSegment2D>(points.size() / 2);

      for (int i = 0; i < points.size(); i += 2) {
         result.add(new GSegment2D(points.get(i), points.get(i + 1)));
      }

      return result;
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


   @Override
   public GPolygonSegment2DIntersections.Result apply(final GPolygonSegment2DIntersections.Parameters parameters) {
      final List<GSegment2D> result = getIntersections(parameters._polygon, parameters._segment);
      return new GPolygonSegment2DIntersections.Result(result);
   }


}
