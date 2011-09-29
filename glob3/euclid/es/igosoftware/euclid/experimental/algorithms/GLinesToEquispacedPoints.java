

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.shape.IPolygonalChain;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.IPolygonalChain3D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;


public class GLinesToEquispacedPoints<

VectorT extends IVector<VectorT, ?>

>
         implements
            IAlgorithm<

            VectorT, GLinesToEquispacedPoints.Parameters<VectorT>,

            VectorT, GLinesToEquispacedPoints.Result<VectorT>

            > {


   public static class Parameters<VectorT extends IVector<VectorT, ?>>
            implements
               IAlgorithmParameters<VectorT> {

      private final IPolygonalChain<VectorT, ?, ?> _geometry;
      private final double                         _distance;


      public Parameters(final IPolygonalChain<VectorT, ?, ?> geometry,
                        final double distance) {
         GAssert.notNull(geometry, "geometry");
         GAssert.isPositive(distance, "distance");

         _geometry = geometry;
         _distance = distance;
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
   public GLinesToEquispacedPoints.Result<VectorT> apply(final GLinesToEquispacedPoints.Parameters<VectorT> parameters) {
      final List<VectorT> coords = parameters._geometry.getPoints();
      if (coords.isEmpty()) {
         throw new RuntimeException("Invalid geometry: " + parameters._geometry);
      }

      final ArrayList<VectorT> output = new ArrayList<VectorT>(coords.size());

      output.add(coords.get(0));

      double remainingDistFromLastSegment = 0;
      for (int i = 0; i < coords.size() - 1; i++) {
         final VectorT current = coords.get(i);
         final VectorT next = coords.get(i + 1);

         final double distToNextPoint = next.distance(current);

         final VectorT direction = next.sub(current).normalized();

         final int pointsToAdd = (int) ((remainingDistFromLastSegment + distToNextPoint) / parameters._distance);
         VectorT addedPoint = null;
         for (int j = 0; j < pointsToAdd; j++) {
            final double dist = (parameters._distance - remainingDistFromLastSegment) + (j * parameters._distance);

            addedPoint = current.add(direction.scale(dist));
            output.add(addedPoint);
         }

         if (addedPoint != null) {
            remainingDistFromLastSegment = addedPoint.distance(next);
         }
         else {
            remainingDistFromLastSegment += distToNextPoint;
         }

      }

      output.trimToSize(); // release some memory

      return new GLinesToEquispacedPoints.Result<VectorT>(output);
   }


   public static void main(final String[] args) {

      final GLinesToEquispacedPoints<IVector2> alg2 = new GLinesToEquispacedPoints<IVector2>();

      final IPolygonalChain2D line2 = new GLinesStrip2D(false, new GVector2D(0, 0), new GVector2D(0, 10), new GVector2D(10, 10));
      final GLinesToEquispacedPoints.Result<IVector2> points2 = alg2.apply(new GLinesToEquispacedPoints.Parameters<IVector2>(
               line2, 1));

      System.out.println("Points: " + points2.getResult().size());
      for (final IVector2 point : points2.getResult()) {
         System.out.println(" " + point);
      }

      System.out.println();


      final GLinesToEquispacedPoints<IVector3> alg3 = new GLinesToEquispacedPoints<IVector3>();

      final IPolygonalChain3D line3 = new GSegment3D(new GVector3D(0, 0, 0), new GVector3D(0, 10, 0));
      final GLinesToEquispacedPoints.Result<IVector3> points3 = alg3.apply(new GLinesToEquispacedPoints.Parameters<IVector3>(
               line3, 1));

      System.out.println("Points: " + points3.getResult().size());
      for (final IVector3 point : points3.getResult()) {
         System.out.println(" " + point);
      }


   }


   @Override
   public String getName() {
      return "Lines to equispaced points";
   }


   @Override
   public String getDescription() {
      return "Obtains a sequence of points that are equispace along the input line.";
   }


}
