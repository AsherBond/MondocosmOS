

package es.igosoftware.euclid.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import es.igosoftware.euclid.IEdgedGeometry;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GQuad2D;
import es.igosoftware.euclid.shape.GQuad3D;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.GSimplePolygon3D;
import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.shape.GTriangle3D;
import es.igosoftware.euclid.shape.IComplexPolygon;
import es.igosoftware.euclid.shape.IPolygon;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;


public class GEdgedGeometryTest
         extends
            TestCase {

   public void testGetEdgesPolygonalChain2D() throws Exception {
      final GVector2D p1 = new GVector2D(0, 0);
      final GVector2D p2 = new GVector2D(2, 0);
      final GVector2D p3 = new GVector2D(2, 5);
      final IPolygonalChain2D geom = new GLinesStrip2D(false, p1, p2, p3);
      checkEdges(geom, false);
   }


   public void testGetEdgesSegment2D() throws Exception {
      final GVector2D p1 = new GVector2D(2, 0);
      final GVector2D p2 = new GVector2D(2, 5);
      final GSegment2D geom = new GSegment2D(p1, p2);
      checkEdges(geom, false);
   }


   public void testGetEdgesSegment3D() throws Exception {
      final GVector3D p1 = new GVector3D(2, 0, 0);
      final GVector3D p2 = new GVector3D(2, 5, 2);
      final GSegment3D geom = new GSegment3D(p1, p2);
      checkEdges(geom, false);
   }


   public void testGetEdgesSimplePolygon2D() throws Exception {
      final GVector2D p1 = new GVector2D(0, 0);
      final GVector2D p2 = new GVector2D(2, 0);
      final GVector2D p3 = new GVector2D(2, 2);
      final GSimplePolygon2D geom = new GSimplePolygon2D(false, p1, p2, p3);
      checkEdges(geom, true);
   }


   public void testGetEdgesSimplePolygon3D() throws Exception {
      final GVector3D p1 = new GVector3D(0, 0, 1);
      final GVector3D p2 = new GVector3D(2, 0, 2);
      final GVector3D p3 = new GVector3D(2, 2, 3);
      final GSimplePolygon3D geom = new GSimplePolygon3D(false, p1, p2, p3);
      checkEdges(geom, true);
   }


   public void testGetEdgesTriangle2D() throws Exception {
      final GVector2D p1 = new GVector2D(0, 0);
      final GVector2D p2 = new GVector2D(2, 0);
      final GVector2D p3 = new GVector2D(2, 2);
      final GTriangle2D geom = new GTriangle2D(p1, p2, p3);
      checkEdges(geom, true);
   }


   public void testGetEdgesTriangle3D() throws Exception {
      final GVector3D p1 = new GVector3D(0, 0, 1);
      final GVector3D p2 = new GVector3D(2, 0, 2);
      final GVector3D p3 = new GVector3D(2, 2, 3);
      final GTriangle3D geom = new GTriangle3D(p1, p2, p3);
      checkEdges(geom, true);
   }


   public void testGetEdgesQuad2D() throws Exception {
      final GVector2D p1 = new GVector2D(0, 0);
      final GVector2D p2 = new GVector2D(2, 0);
      final GVector2D p3 = new GVector2D(2, 2);
      final GVector2D p4 = new GVector2D(0, 2);
      final GQuad2D geom = new GQuad2D(p1, p2, p3, p4);
      checkEdges(geom, true);
   }


   public void testGetEdgesQuad3D() throws Exception {
      final GVector3D p1 = new GVector3D(0, 0, 1);
      final GVector3D p2 = new GVector3D(2, 0, 2);
      final GVector3D p3 = new GVector3D(2, 2, 3);
      final GVector3D p4 = new GVector3D(2, 1, 2.5);
      final GQuad3D geom = new GQuad3D(p1, p2, p3, p4);
      checkEdges(geom, true);
   }


   public void testGetEdgesComplexPolygon2D() throws Exception {
      GVector2D p1 = new GVector2D(0, 0);
      GVector2D p2 = new GVector2D(10, 0);
      GVector2D p3 = new GVector2D(10, 10);
      GVector2D p4 = new GVector2D(0, 10);
      final GSimplePolygon2D ring = new GSimplePolygon2D(false, p1, p2, p3, p4);
      p1 = new GVector2D(1, 1);
      p2 = new GVector2D(9, 1);
      p3 = new GVector2D(9, 9);
      p4 = new GVector2D(1, 9);
      final GSimplePolygon2D hole = new GSimplePolygon2D(false, p1, p2, p3, p4);
      final GComplexPolygon2D geom = new GComplexPolygon2D(ring, Collections.singletonList(hole));
      checkEdges(geom, true);
      //      fail(); 
      checkEdgesComplexGeometry(geom);
   }


   private <

   VectorT extends IVector<VectorT, ?>,

   SegmentT extends GSegment<VectorT, SegmentT, ?>,

   BoundsT extends IBounds<VectorT, BoundsT>

   > void checkEdgesComplexGeometry(final IComplexPolygon<VectorT, SegmentT, BoundsT> geom) {
      checkEdges(geom.getHull(), true);
      final List<? extends IPolygon<VectorT, SegmentT, BoundsT>> holes = geom.getHoles();
      for (final IPolygon<VectorT, SegmentT, BoundsT> hole : holes) {
         checkEdges(hole, true);
      }
   }


   private <

   VectorT extends IVector<VectorT, ?>,

   SegmentT extends GSegment<VectorT, SegmentT, ?>,

   BoundsT extends IBounds<VectorT, BoundsT>

   > void checkEdges(final IEdgedGeometry<VectorT, SegmentT, BoundsT> geom,
                     final boolean loop) {
      final List<SegmentT> edges = geom.getEdges();
      final List<VectorT> points = geom.getPoints();

      /*
       * Number of expected edges are pointCount - 1 in lines, and pointCount in closed rings
       */
      final int expectedEdges = loop ? points.size() : points.size() - 1;
      assertTrue(edges.size() == expectedEdges);

      /*
       * Check that all points except leading and trailing ones are referenced two times by the edges 
       */
      final List<Integer> timesUsed = new ArrayList<Integer>();
      for (int i = 0; i < points.size(); i++) {
         timesUsed.add(2);
      }

      if (!loop) {
         timesUsed.set(0, 1);
         timesUsed.set(timesUsed.size() - 1, 1);
      }

      for (final SegmentT edge : edges) {
         final int fromIndex = points.indexOf(edge._from);
         final int toIndex = points.indexOf(edge._to);

         final int difference = Math.abs(fromIndex - toIndex);
         //Check they are consecutive
         assertTrue((difference == 1) || (difference == points.size() - 1));
         timesUsed.set(fromIndex, timesUsed.get(fromIndex) - 1);
         timesUsed.set(toIndex, timesUsed.get(toIndex) - 1);
      }

      for (final Integer timesUsedPoint : timesUsed) {
         assertTrue(timesUsedPoint == 0);
      }
   }

}
