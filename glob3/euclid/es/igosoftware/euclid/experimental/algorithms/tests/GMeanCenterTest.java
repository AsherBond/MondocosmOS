

package es.igosoftware.euclid.experimental.algorithms.tests;

import junit.framework.TestCase;
import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.bounding.GNBall;
import es.igosoftware.euclid.experimental.algorithms.GMeanCenter;
import es.igosoftware.euclid.experimental.algorithms.GMeanCenter.Result;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPair;


public class GMeanCenterTest
         extends
            TestCase {

   public void testNoGeometries() throws Exception {
      final GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      final Result<IVector2> result = alg2.apply(new GMeanCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc, fc));
      assertTrue(result == null);
   }


   public void testOneGeometry2D() throws Exception {
      final GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      final GVector2D p = new GVector2D(10, 10);
      fc.add(p);

      final Result<IVector2> result = alg2.apply(new GMeanCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc, fc));
      final GNBall<IVector2, ?> ball = result.getCenter();
      assertTrue(GMath.closeToZero(ball._center.distance(p)));
      assertTrue(GMath.closeToZero(ball._radius));
   }


   public void testOneGeometry3D() throws Exception {
      final GMeanCenter<GPair<IGeometry<IVector3>, Double>, IVector3> alg3 = new GMeanCenter<GPair<IGeometry<IVector3>, Double>, IVector3>();

      final GMyFeatureCollection<IVector3> fc = new GMyFeatureCollection<IVector3>();
      final GVector3D p = new GVector3D(10, 10, 12);
      fc.add(p);

      final Result<IVector3> result = alg3.apply(new GMeanCenter.Parameters<GPair<IGeometry<IVector3>, Double>, IVector3>(fc, fc));
      final GNBall<IVector3, ?> ball = result.getCenter();
      assertTrue(GMath.closeToZero(ball._center.distance(p)));
      assertTrue(GMath.closeToZero(ball._radius));
   }


   public void testSeveralGeometries() throws Exception {
      final GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      fc.add(new GVector2D(10, 0));
      fc.add(new GVector2D(0, 0));

      final Result<IVector2> result = alg2.apply(new GMeanCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc, fc));
      final GNBall<IVector2, ?> ball = result.getCenter();
      assertTrue(GMath.closeToZero(ball._center.distance(new GVector2D(5, 0))));
      assertTrue(GMath.closeTo(ball._radius, 5));
   }


   public void testSeveralWightedGeometries() throws Exception {
      final GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMeanCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      fc.add(new GVector2D(10, 0), 1);
      fc.add(new GVector2D(0, 0), 3);

      final Result<IVector2> result = alg2.apply(new GMeanCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc, fc));
      final GNBall<IVector2, ?> ball = result.getCenter();
      //      System.out.println(ball);
      assertTrue(GMath.closeToZero(ball._center.distance(new GVector2D(2.5, 0))));
      final double expectedRadius = Math.sqrt((3 * (Math.pow(2.5, 2)) + Math.pow(7.5, 2)) / 4);
      assertTrue(GMath.closeTo(ball._radius, expectedRadius));
   }
}
