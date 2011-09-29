

package es.igosoftware.euclid.experimental.algorithms.tests;

import junit.framework.TestCase;
import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.experimental.algorithms.GMedianCenter;
import es.igosoftware.euclid.experimental.algorithms.GMedianCenter.Result;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPair;


public class GMedianCenterTest
         extends
            TestCase {

   public void testNoGeometries() throws Exception {
      final GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      final Result<IVector2> result = alg2.apply(new GMedianCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc,
               fc));
      assertTrue(result == null);
   }


   public void testOneGeometry2D() throws Exception {
      final GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      final GVector2D p = new GVector2D(10, 10);
      fc.add(p);

      final Result<IVector2> result = alg2.apply(new GMedianCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc,
               fc));
      final IVector2 center = result.getCenter();
      assertTrue(GMath.closeToZero(center.distance(p)));
   }


   public void testOneGeometry3D() throws Exception {
      final GMedianCenter<GPair<IGeometry<IVector3>, Double>, IVector3> alg3 = new GMedianCenter<GPair<IGeometry<IVector3>, Double>, IVector3>();

      final GMyFeatureCollection<IVector3> fc = new GMyFeatureCollection<IVector3>();
      final GVector3D p = new GVector3D(10, 10, 12);
      fc.add(p);

      final Result<IVector3> result = alg3.apply(new GMedianCenter.Parameters<GPair<IGeometry<IVector3>, Double>, IVector3>(fc,
               fc));
      final IVector3 center = result.getCenter();
      assertTrue(GMath.closeToZero(center.distance(p)));
   }


   public void testSeveralGeometries() throws Exception {
      final GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      fc.add(new GVector2D(10, 0));
      fc.add(new GVector2D(0, 0));
      fc.add(new GVector2D(1, 0));
      fc.add(new GVector2D(2, 0));

      final Result<IVector2> result = alg2.apply(new GMedianCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc,
               fc));
      final IVector2 center = result.getCenter();
      assertTrue(GMath.closeToZero(center.distance(new GVector2D(1.5, 0))));
   }


   public void testSeveralWeightedGeometries() throws Exception {
      final GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2> alg2 = new GMedianCenter<GPair<IGeometry<IVector2>, Double>, IVector2>();

      final GMyFeatureCollection<IVector2> fc = new GMyFeatureCollection<IVector2>();
      fc.add(new GVector2D(10, 0), 1);
      fc.add(new GVector2D(0, 0), 2);

      final Result<IVector2> result = alg2.apply(new GMedianCenter.Parameters<GPair<IGeometry<IVector2>, Double>, IVector2>(fc,
               fc));
      final IVector2 center = result.getCenter();
      assertTrue(GMath.closeToZero(center.distance(new GVector2D(0, 0))));
   }
}
