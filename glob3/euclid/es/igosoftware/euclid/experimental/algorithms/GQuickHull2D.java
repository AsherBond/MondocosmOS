

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.vector.IVector2;


public class GQuickHull2D
         implements
            IAlgorithm<IVector2, GQuickHull2D.Parameters, IVector2, GQuickHull2D.Result> {


   public static class Parameters
            implements
               IAlgorithmParameters<IVector2> {

      private final GSimplePolygon2D _simplePolygon;


      public Parameters(final GSimplePolygon2D simplePolygon) {
         _simplePolygon = simplePolygon;
      }

   }


   public static class Result
            implements
               IAlgorithmResult<IVector2> {
      private final GSimplePolygon2D _simplePolygon;


      private Result(final GSimplePolygon2D simplePolygon) {
         _simplePolygon = simplePolygon;
      }


      public GSimplePolygon2D getSimplePolygon() {
         return _simplePolygon;
      }
   }


   @Override
   public String getName() {
      return "QuickHull 2D";
   }


   @Override
   public String getDescription() {
      return "Calculates the convex hull of a SimplePolygon using the QuickHull algorithm. "
             + "See: http://en.wikipedia.org/wiki/Convex_hull_algorithms";
   }


   @Override
   public GQuickHull2D.Result apply(final GQuickHull2D.Parameters parameters) {
      return new GQuickHull2D.Result(quickHull(parameters._simplePolygon));
   }


   public static GSimplePolygon2D quickHull(final GSimplePolygon2D simplePolygon) {
      final List<IVector2> result = quickHull(new ArrayList<IVector2>(simplePolygon.getPoints()));
      return GSimplePolygon2D.createConvexHull(true, result);
   }


   public static List<IVector2> quickHull(final List<IVector2> points) {
      if (points.size() <= 3) {
         return new ArrayList<IVector2>(points);
      }

      final List<IVector2> hull = new ArrayList<IVector2>();

      // find extremals
      IVector2 minPoint = null;
      IVector2 maxPoint = null;
      double minX = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      for (final IVector2 point : points) {
         if (point.x() < minX) {
            minX = point.x();
            minPoint = point;
         }
         if (point.x() > maxX) {
            maxX = point.x();
            maxPoint = point;
         }
      }

      hull.add(minPoint);
      points.remove(minPoint);
      hull.add(maxPoint);
      points.remove(maxPoint);

      final List<IVector2> leftSet = new ArrayList<IVector2>(points.size() / 2);
      final List<IVector2> rightSet = new ArrayList<IVector2>(points.size() / 2);

      for (final IVector2 point : points) {
         if (pointLocation(minPoint, maxPoint, point) == -1) {
            leftSet.add(point);
         }
         else {
            rightSet.add(point);
         }
      }

      hullSet(minPoint, maxPoint, rightSet, hull);
      hullSet(maxPoint, minPoint, leftSet, hull);

      return hull;
   }


   private static int pointLocation(final IVector2 a,
                                    final IVector2 b,
                                    final IVector2 p) {
      final double cp1 = (b.x() - a.x()) * (p.y() - a.y()) - (b.y() - a.y()) * (p.x() - a.x());
      return (cp1 > 0) ? 1 : -1;
   }


   private static void hullSet(final IVector2 a,
                               final IVector2 b,
                               final List<IVector2> set,
                               final List<IVector2> hull) {

      if (set.isEmpty()) {
         return;
      }

      final int insertPosition = hull.indexOf(b);

      if (set.size() == 1) {
         final IVector2 p = set.get(0);
         set.remove(p);
         hull.add(insertPosition, p);
         return;
      }

      double dist = Double.NEGATIVE_INFINITY;
      int furthestIVector2 = 0;
      for (int i = 0; i < set.size(); i++) {
         final IVector2 point = set.get(i);
         final double distance = distance(a, b, point);
         if (distance > dist) {
            dist = distance;
            furthestIVector2 = i;
         }
      }
      final IVector2 point = set.get(furthestIVector2);
      set.remove(furthestIVector2);
      hull.add(insertPosition, point);

      // Determine who's to the left of AP
      final List<IVector2> leftSetAP = new ArrayList<IVector2>();
      for (final IVector2 m : set) {
         if (pointLocation(a, point, m) == 1) {
            // set.remove(M);
            leftSetAP.add(m);
         }
      }

      // Determine who's to the left of PB
      final List<IVector2> leftSetPB = new ArrayList<IVector2>();
      for (final IVector2 m : set) {
         if (pointLocation(point, b, m) == 1) {
            // set.remove(M);
            leftSetPB.add(m);
         }
      }

      hullSet(a, point, leftSetAP, hull);
      hullSet(point, b, leftSetPB, hull);
   }


   private static double distance(final IVector2 a,
                                  final IVector2 b,
                                  final IVector2 c) {
      final double abx = b.x() - a.x();
      final double aby = b.y() - a.y();

      final double num = abx * (a.y() - c.y()) - aby * (a.x() - c.x());

      return (num < 0) ? -num : num;
   }


}
