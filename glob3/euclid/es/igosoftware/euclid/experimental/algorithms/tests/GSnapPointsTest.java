

package es.igosoftware.euclid.experimental.algorithms.tests;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.algorithms.GSnapPoints;
import es.igosoftware.euclid.experimental.algorithms.GSnapPoints.Result;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GSnapPointsTest
         extends
            TestCase {

   public void testPoints() throws Exception {
      final IGlobeFeatureCollection<IVector2, IVector2> result = testPoints(3);
      assertTrue(result != null);
      @SuppressWarnings("null")
      final Iterator<IGlobeFeature<IVector2, IVector2>> iterator = result.iterator();
      final IGlobeFeature<IVector2, IVector2> firstFeature = iterator.next();
      assertTrue(GMath.closeToZero(firstFeature.getDefaultGeometry().distance(new GVector2D(12, 12))));
      assertTrue(!iterator.hasNext());
   }


   public void testNoPointInTolerance() throws Exception {
      final IGlobeFeatureCollection<IVector2, IVector2> result = testPoints(0.1);
      assertTrue(result != null);
      @SuppressWarnings("null")
      final Iterator<IGlobeFeature<IVector2, IVector2>> iterator = result.iterator();
      final IGlobeFeature<IVector2, IVector2> firstFeature = iterator.next();
      assertTrue(GMath.closeToZero(firstFeature.getDefaultGeometry().distance(new GVector2D(10, 10))));
      assertTrue(!iterator.hasNext());
   }


   public void testEmptySnappingFeatureCollection() throws Exception {
      final IGlobeFeatureCollection<IVector2, IVector2> result = testSnap(10,
               new ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>());
      assertTrue(result != null);
      @SuppressWarnings("null")
      final Iterator<IGlobeFeature<IVector2, IVector2>> iterator = result.iterator();
      final IGlobeFeature<IVector2, IVector2> firstFeature = iterator.next();
      assertTrue(GMath.closeToZero(firstFeature.getDefaultGeometry().distance(new GVector2D(10, 10))));
      assertTrue(!iterator.hasNext());
   }


   public void testSnapToSegment() throws Exception {
      final ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> snapping = new ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>();
      snapping.add(new GSegment2D(new GVector2D(0, 0), new GVector2D(40, 0)));
      final IGlobeFeatureCollection<IVector2, IVector2> result = testSnap(11, snapping);
      assertTrue(result != null);
      @SuppressWarnings("null")
      final Iterator<IGlobeFeature<IVector2, IVector2>> iterator = result.iterator();
      final IGlobeFeature<IVector2, IVector2> firstFeature = iterator.next();
      final IBoundedGeometry2D<? extends IFinite2DBounds<?>> defaultGeometry = firstFeature.getDefaultGeometry();
      assertTrue(GMath.closeToZero(defaultGeometry.distance(new GVector2D(10, 0))));
      assertTrue(!iterator.hasNext());
   }


   public void testPointOutsideTreeBounds() throws Exception {
      final ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> snapping = new ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>();
      snapping.add(new GSegment2D(new GVector2D(0, 0), new GVector2D(10, 9)));
      /*
       * The point (10, 10) is outside the index bounds (0, 0) - (10, 9)
       */
      final IGlobeFeatureCollection<IVector2, IVector2> result = testSnap(11, snapping);
      assertTrue(result != null);
      @SuppressWarnings("null")
      final Iterator<IGlobeFeature<IVector2, IVector2>> iterator = result.iterator();
      final IGlobeFeature<IVector2, IVector2> firstFeature = iterator.next();
      final IBoundedGeometry2D<? extends IFinite2DBounds<?>> defaultGeometry = firstFeature.getDefaultGeometry();
      assertTrue(GMath.closeToZero(defaultGeometry.distance(new GVector2D(10, 9))));
      assertTrue(!iterator.hasNext());
   }


   private IGlobeFeatureCollection<IVector2, IVector2> testPoints(final double tolerance) {
      final ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> snapList = new ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>();
      snapList.add(new GVector2D(5, 9));
      snapList.add(new GVector2D(12, 12));
      return testSnap(tolerance, snapList);
   }


   private IGlobeFeatureCollection<IVector2, IVector2> testSnap(final double tolerance,
                                                                final ArrayList<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> snapList) {
      final ArrayList<IVector2> sourceList = new ArrayList<IVector2>();
      sourceList.add(new GVector2D(10, 10));
      final GListFeatureCollection<IVector2, IVector2> sourceFC = GListFeatureCollection.from2DGeometryList(
               GProjection.EPSG_23030, sourceList);

      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> snapFC = GListFeatureCollection.from2DGeometryList(
               GProjection.EPSG_23030, snapList);

      final IGlobeFeatureCollection<IVector2, IVector2> result = new Tester<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>().evaluate(
               sourceFC, snapFC, tolerance);
      return result;
   }


   private static class Tester<

   VectorT extends IVector<VectorT, ? extends IFiniteBounds<VectorT, ?>>,

   FeatureGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> {

      public IGlobeFeatureCollection<VectorT, VectorT> evaluate(final IGlobeFeatureCollection<VectorT, VectorT> sourceFC,
                                                                final IGlobeFeatureCollection<VectorT, FeatureGeometryT> snapFC,
                                                                final double tolerance) {

         final GSnapPoints<VectorT, FeatureGeometryT> snapPoints = new GSnapPoints<VectorT, FeatureGeometryT>();

         final Result<VectorT> result = snapPoints.apply(new GSnapPoints.Parameters<VectorT, FeatureGeometryT>(sourceFC, snapFC,
                  tolerance));

         return result.getFeatures();
      }


   }
}
