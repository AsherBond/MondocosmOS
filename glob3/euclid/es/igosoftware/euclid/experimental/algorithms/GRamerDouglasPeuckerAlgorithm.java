

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public class GRamerDouglasPeuckerAlgorithm<VectorT extends IVector<VectorT, ?>>
         implements
            IAlgorithm<VectorT, GRamerDouglasPeuckerAlgorithm.Parameters<VectorT>, VectorT, GRamerDouglasPeuckerAlgorithm.Result<VectorT>> {


   public static class Parameters<VectorT extends IVector<VectorT, ?>>
            implements
               IAlgorithmParameters<VectorT> {

      private final List<VectorT> _points;
      private final double        _epsilon;


      public Parameters(final List<VectorT> points,
                        final double epsilon) {
         GAssert.notNull(points, "points");
         GAssert.isPositive(epsilon, "epsilon");

         _points = points;
         _epsilon = epsilon;
      }
   }


   public static class Result<VectorT extends IVector<VectorT, ?>>
            implements
               IAlgorithmResult<VectorT> {

      private final List<VectorT> _result;


      private Result(final List<VectorT> result) {
         _result = result;
      }


      public List<VectorT> getResult() {
         return Collections.unmodifiableList(_result);
      }
   }


   @Override
   public String getName() {
      return "Ramer-Douglas-Peucker Algorithm";
   }


   @Override
   public String getDescription() {
      return "The Douglas–Peucker algorithm is an algorithm for reducing the number of points in a curve that is approximated "
             + "by a series of points. The initial form of the algorithm was independently suggested in 1972 by Urs Ramer and "
             + "1973 by David Douglas and Thomas Peucker. This algorithm is also known under the following names: the Ramer–"
             + "Douglas–Peucker algorithm, the iterative end-point fit algorithm or the split-and-merge algorithm."
             + "See: http://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm";
   }


   @Override
   public GRamerDouglasPeuckerAlgorithm.Result<VectorT> apply(final GRamerDouglasPeuckerAlgorithm.Parameters<VectorT> parameters) {
      final List<VectorT> result = simplify(parameters._points, parameters._epsilon);

      return new GRamerDouglasPeuckerAlgorithm.Result<VectorT>(result);
   }


   public static <VectorT extends IVector<VectorT, ?>> List<VectorT> simplify(final List<VectorT> points,
                                                                              final double epsilon) {


      if (points.size() <= 2) {
         return new ArrayList<VectorT>(points);
      }


      //Find the point with the maximum distance
      final int endPos = points.size() - 1;

      final VectorT first = points.get(0);
      final VectorT last = points.get(endPos);
      final GSegment<VectorT, ?, ?> segment = GSegment.create(first, last);

      double maxSquaredDistance = Double.NEGATIVE_INFINITY;
      int maxIndex = -1;
      for (int i = 1; i < endPos; i++) {
         final double currentSquaredDistance = segment.squaredDistance(points.get(i));
         if (currentSquaredDistance > maxSquaredDistance) {
            maxSquaredDistance = currentSquaredDistance;
            maxIndex = i;
         }
      }


      final List<VectorT> result = new ArrayList<VectorT>();

      final double squaredEpsilon = epsilon * epsilon;
      if (maxSquaredDistance > squaredEpsilon) {
         //If max distance is greater than epsilon, recursively simplify
         final List<VectorT> recResults1 = simplify(points.subList(0, maxIndex + 1), epsilon);
         final List<VectorT> recResults2 = simplify(points.subList(maxIndex, endPos + 1), epsilon);

         result.addAll(recResults1.subList(0, recResults1.size() - 1));
         result.addAll(recResults2);
      }
      else {
         result.add(first);
         result.add(last);
      }

      return result;
   }


}
