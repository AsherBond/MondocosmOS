

package es.igosoftware.euclid.experimental.algorithms.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.algorithms.GSplitLinesWithPoints;
import es.igosoftware.euclid.experimental.algorithms.GSplitLinesWithPoints.CutLine;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GGlobeFeature;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;


public class GSplitLinesWithPointsTest
         extends
            TestCase {

   public void testOneLineOnePoint() throws Exception {
      final List<CutLine<IVector2, IPolygonalChain2D>> result = getOneLineOnePoint(5);
      assertTrue(result.size() == 2);
      assertTrue(result.get(0).getLine().closeTo(GShapeUtils.createLine2(false, new GVector2D(0, 0), new GVector2D(5, 0))));
      assertTrue(result.get(0).getLineId() == 0);
      assertTrue(result.get(0).getSourcePointId() == -1);
      assertTrue(result.get(0).getTargetPointId() == 0);
      assertTrue(result.get(1).getLine().closeTo(GShapeUtils.createLine2(false, new GVector2D(5, 0), new GVector2D(10, 0))));
      assertTrue(result.get(1).getLineId() == 0);
      assertTrue(result.get(1).getSourcePointId() == 0);
      assertTrue(result.get(1).getTargetPointId() == -1);
   }


   public void testComplexLine() throws Exception {
      final List<IPolygonalChain2D> lines = Collections.<IPolygonalChain2D> singletonList(GShapeUtils.createLine2(false,
               new GVector2D(0, 0), new GVector2D(2, 0), new GVector2D(4, 0), new GVector2D(7, 0), new GVector2D(10, 0)));

      final List<CutLine<IVector2, IPolygonalChain2D>> result = execute(lines,
               Collections.<IVector2> singletonList(new GVector2D(5, 1)), 1.1);

      assertTrue(result.size() == 2);
      assertTrue(result.get(0).getLine().closeTo(
               GShapeUtils.createLine2(false, new GVector2D(0, 0), new GVector2D(2, 0), new GVector2D(4, 0), new GVector2D(5, 0))));
      assertTrue(result.get(0).getLineId() == 0);
      assertTrue(result.get(0).getSourcePointId() == -1);
      assertTrue(result.get(0).getTargetPointId() == 0);
      assertTrue(result.get(1).getLine().closeTo(
               GShapeUtils.createLine2(false, new GVector2D(5, 0), new GVector2D(7, 0), new GVector2D(10, 0))));
      assertTrue(result.get(1).getLineId() == 0);
      assertTrue(result.get(1).getSourcePointId() == 0);
      assertTrue(result.get(1).getTargetPointId() == -1);
   }


   public void testNoSplit() throws Exception {
      final List<CutLine<IVector2, IPolygonalChain2D>> result = getOneLineOnePoint(0.5);
      assertTrue(result.size() == 1);
      assertTrue(result.get(0).getLine().closeTo(GShapeUtils.createLine2(false, new GVector2D(0, 0), new GVector2D(10, 0))));
      assertTrue(result.get(0).getLineId() == 0);
      assertTrue(result.get(0).getSourcePointId() == -1);
      assertTrue(result.get(0).getTargetPointId() == -1);
   }


   private List<CutLine<IVector2, IPolygonalChain2D>> getOneLineOnePoint(final double d) {
      final List<IPolygonalChain2D> lines = Collections.<IPolygonalChain2D> singletonList(GShapeUtils.createLine2(false,
               new GVector2D(0, 0), new GVector2D(10, 0)));
      final List<IVector2> points = Collections.<IVector2> singletonList(new GVector2D(5, 1));
      final List<CutLine<IVector2, IPolygonalChain2D>> result = execute(lines, points, d);
      return result;
   }


   public void testPointUsedJustOnce() throws Exception {
      final IPolygonalChain2D line1 = GShapeUtils.createLine2(false, new GVector2D(0, 0), new GVector2D(10, 0));
      final IPolygonalChain2D nonSplitted = GShapeUtils.createLine2(false, new GVector2D(0, 2), new GVector2D(10, 2));
      final ArrayList<IPolygonalChain2D> lines = new ArrayList<IPolygonalChain2D>();
      lines.add(line1);
      lines.add(nonSplitted);

      final ArrayList<IVector2> points = new ArrayList<IVector2>();
      points.add(new GVector2D(5, 1));
      final List<CutLine<IVector2, IPolygonalChain2D>> result = execute(lines, points, 5);

      assertTrue(result.size() == 3);

      assertTrue(result.get(0).getLine().closeTo(GShapeUtils.createLine2(false, new GVector2D(0, 0), new GVector2D(5, 0))));
      assertTrue(result.get(0).getLineId() == 0);
      assertTrue(result.get(0).getSourcePointId() == -1);
      assertTrue(result.get(0).getTargetPointId() == 0);

      assertTrue(result.get(1).getLine().closeTo(GShapeUtils.createLine2(false, new GVector2D(5, 0), new GVector2D(10, 0))));
      assertTrue(result.get(1).getLineId() == 0);
      assertTrue(result.get(1).getSourcePointId() == 0);
      assertTrue(result.get(1).getTargetPointId() == -1);

      assertTrue(result.get(2).getLine().closeTo(nonSplitted));
      assertTrue(result.get(2).getLineId() == 1);
      assertTrue(result.get(2).getSourcePointId() == -1);
      assertTrue(result.get(2).getTargetPointId() == -1);
   }


   public void testBreakingPointInEndPoint() throws Exception {
      final IPolygonalChain2D line = GShapeUtils.createLine2(false, new GVector2D(0, 0), new GVector2D(10, 0));
      final List<IPolygonalChain2D> lines = Collections.<IPolygonalChain2D> singletonList(line);

      final List<CutLine<IVector2, IPolygonalChain2D>> result = execute(lines,
               Collections.<IVector2> singletonList(new GVector2D(11, 0)), 1.1);

      assertTrue(result.size() == 1);
      assertTrue(result.get(0).getLine().closeTo(line));
      assertTrue(result.get(0).getLineId() == 0);
      assertTrue(result.get(0).getSourcePointId() == -1);
      assertTrue(result.get(0).getTargetPointId() == 0);
   }


   private List<CutLine<IVector2, IPolygonalChain2D>> execute(final List<IPolygonalChain2D> lines,
                                                              final List<IVector2> points,
                                                              final double tolerance) {
      final GListFeatureCollection<IVector2, IPolygonalChain2D> lineFC = new Tester<IVector2, IPolygonalChain2D>().from2DGeometryList(lines);
      final GListFeatureCollection<IVector2, IVector2> pointsFC = new Tester<IVector2, IVector2>().from2DGeometryList(points);

      final GSplitLinesWithPoints<IVector2, GSegment2D, GAxisAlignedRectangle, IPolygonalChain2D> alg = new GSplitLinesWithPoints<IVector2, GSegment2D, GAxisAlignedRectangle, IPolygonalChain2D>();
      final List<CutLine<IVector2, IPolygonalChain2D>> result = alg.apply(
               new GSplitLinesWithPoints.Parameters<IVector2, IPolygonalChain2D>(lineFC, pointsFC, tolerance)).getCutLines();
      return result;
   }

   private class Tester<

   VectorT extends IVector<VectorT, ? extends IFiniteBounds<VectorT, ?>>,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > {

      GListFeatureCollection<VectorT, GeometryT> from2DGeometryList(final List<GeometryT> lines) {
         final ArrayList<IGlobeFeature<VectorT, GeometryT>> lineFeatures = new ArrayList<IGlobeFeature<VectorT, GeometryT>>();
         for (int i = 0; i < lines.size(); i++) {
            final GeometryT line = lines.get(i);
            lineFeatures.add(new GGlobeFeature<VectorT, GeometryT>(line, Collections.<Object> singletonList(new Integer(i))));
         }
         return new GListFeatureCollection<VectorT, GeometryT>(GProjection.EPSG_23030, Collections.singletonList(new GField("id",
                  Integer.class)), lineFeatures);
      }
   }

}
