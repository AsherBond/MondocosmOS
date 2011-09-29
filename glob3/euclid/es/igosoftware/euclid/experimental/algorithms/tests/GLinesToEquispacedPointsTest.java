

package es.igosoftware.euclid.experimental.algorithms.tests;

import java.util.List;

import junit.framework.TestCase;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.experimental.algorithms.GLinesToEquispacedPoints;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.shape.IPolygonalChain;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.IPolygonalChain3D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;


public class GLinesToEquispacedPointsTest
         extends
            TestCase {


   public void test2D() throws Exception {
      final GLinesToEquispacedPoints<IVector2> alg2 = new GLinesToEquispacedPoints<IVector2>();

      final IPolygonalChain2D line2 = new GLinesStrip2D(false, new GVector2D(0, 0), new GVector2D(2, 0), new GVector2D(2, 5));
      final int distance = 1;
      final GLinesToEquispacedPoints.Result<IVector2> points2 = alg2.apply(new GLinesToEquispacedPoints.Parameters<IVector2>(
               line2, distance));

      final List<IVector2> result = points2.getResult();
      checkResult(result, line2, distance);
   }


   public void test3D() throws Exception {
      final GLinesToEquispacedPoints<IVector3> alg3 = new GLinesToEquispacedPoints<IVector3>();

      final IPolygonalChain3D line3 = new GSegment3D(new GVector3D(0, 0, 0), new GVector3D(0, 10, 5));
      final int distance = 1;
      final GLinesToEquispacedPoints.Result<IVector3> points3 = alg3.apply(new GLinesToEquispacedPoints.Parameters<IVector3>(
               line3, distance));

      final List<IVector3> result = points3.getResult();
      checkResult(result, line3, distance);
   }


   public void testDistanceGreaterThanSegmentSize() throws Exception {
      final GLinesToEquispacedPoints<IVector2> alg2 = new GLinesToEquispacedPoints<IVector2>();

      final IPolygonalChain2D line2 = new GLinesStrip2D(false, new GVector2D(0, 0), new GVector2D(2, 0), new GVector2D(4, 0),
               new GVector2D(6, 0));
      final int distance = 5;
      final GLinesToEquispacedPoints.Result<IVector2> points2 = alg2.apply(new GLinesToEquispacedPoints.Parameters<IVector2>(
               line2, distance));
      final List<IVector2> result = points2.getResult();
      checkResult(result, line2, distance);
   }


   /**
    * Check that two consecutive points have the expected distance and are in the line
    */
   private <

   VectorT extends IVector<VectorT, ?>,

   SegmentT extends GSegment<VectorT, SegmentT, ?>,

   BoundsT extends IBounds<VectorT, BoundsT>

   > void checkResult(final List<VectorT> result,
                      final IPolygonalChain<VectorT, SegmentT, BoundsT> geom,
                      final double distance) {
      for (int i = 1; i < result.size(); i++) {
         final VectorT p1 = result.get(i - 1);
         final VectorT p2 = result.get(i);

         /*
          * Find the segment the two consecutive points are in
          */
         final List<SegmentT> segments = geom.getEdges();
         int firstSegmentIndex = -1;
         int secondSegmentIndex = -1;
         for (int j = 0; j < segments.size(); j++) {
            final SegmentT segment = segments.get(j);
            if (segment.contains(p1) && (firstSegmentIndex == -1)) {
               firstSegmentIndex = j;
            }
            if (segment.contains(p2) && (secondSegmentIndex == -1)) {
               secondSegmentIndex = j;
            }
         }

         /*
          * Obtain the distance between the points following the segments in the source line
          */
         double realDistance = 0;
         VectorT lastPoint = p1;
         for (int j = firstSegmentIndex; j < secondSegmentIndex; j++) {
            realDistance += segments.get(j)._to.sub(lastPoint).length();
            lastPoint = segments.get(j)._to;
         }
         realDistance += p2.sub(lastPoint).length();

         assertTrue(GMath.closeTo(realDistance, distance));
         assertTrue(GMath.closeToZero(geom.distance(p2)));
      }
   }

}
