

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.GTriangle;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GStringUtils;


public class GEarClippingTriangulator2D
         implements
            IAlgorithm<IVector2, GEarClippingTriangulator2D.Parameters, IVector2, GEarClippingTriangulator2D.Result> {


   public static class Parameters
            implements
               IAlgorithmParameters<IVector2> {


      private final GSimplePolygon2D _polygon;
      private final boolean          _verbose;


      public Parameters(final GSimplePolygon2D polygon,
                        final boolean verbose) {
         GAssert.notNull(polygon, "polygon");
         _polygon = polygon;
         _verbose = verbose;
      }


   }


   public static class Result
            implements
               IAlgorithmResult<IVector2> {

      private final List<GIndexedTriangle> _indexedTriangles;


      private Result(final List<GIndexedTriangle> triangles) {
         _indexedTriangles = triangles;
      }


      public List<GIndexedTriangle> getIndexedTriangles() {
         return _indexedTriangles;
      }
   }


   @Override
   public String getName() {
      return "Ear Clipping Triangulator 2D";
   }


   @Override
   public String getDescription() {
      return "See http://en.wikipedia.org/wiki/Polygon_triangulation#Ear_clipping_method";
   }


   public static List<GIndexedTriangle> triangulate(final GSimplePolygon2D polygon,
                                                    final boolean verbose) {
      // from: http://codesuppository.blogspot.com/2009/07/polygon-triangulator-ear-clipping.html

      final long start = System.currentTimeMillis();

      final int pointsCount = polygon.getPointsCount();

      final List<GIndexedTriangle> result = new ArrayList<GIndexedTriangle>(pointsCount - 2);

      if (pointsCount < 3) {
         return Collections.emptyList();
      }

      final int[] indices = new int[pointsCount];
      final boolean flipped;
      if (GShapeUtils.signedArea2(polygon.getPoints()) >= 0) {
         for (int v = 0; v < pointsCount; v++) {
            indices[v] = v;
         }
         flipped = false;
      }
      else {
         for (int v = 0; v < pointsCount; v++) {
            indices[v] = (pointsCount - 1) - v;
         }
         flipped = true;
      }

      int nv = pointsCount;
      int count = 2 * nv;
      for (int m = 0, v = nv - 1; nv > 2;) {
         if (0 >= (count--)) {
            return Collections.emptyList();
         }

         int u = v;
         if (nv <= u) {
            u = 0;
         }
         v = u + 1;
         if (nv <= v) {
            v = 0;
         }
         int w = v + 1;
         if (nv <= w) {
            w = 0;
         }

         if (clip(u, v, w, nv, indices, polygon)) {
            final int a = indices[u];
            final int b = indices[v];
            final int c = indices[w];
            int s;
            int t;

            if (flipped) {
               result.add(new GIndexedTriangle(a, b, c));
            }
            else {
               result.add(new GIndexedTriangle(c, b, a));
            }

            m++;
            for (s = v, t = v + 1; t < nv; s++, t++) {
               indices[s] = indices[t];
            }
            nv--;
            count = 2 * nv;
         }
      }

      if (verbose) {
         System.out.println("- Created " + result.size() + " triangles from " + pointsCount + " points in "
                            + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
      }

      return result;
   }

   private static final double EPSILON = 0.00000000001;


   private static boolean clip(final int u,
                               final int v,
                               final int w,
                               final int n,
                               final int[] indices,
                               final GSimplePolygon2D polygon) {

      final IVector2 a = polygon.getPoint(indices[u]);
      final IVector2 b = polygon.getPoint(indices[v]);
      final IVector2 c = polygon.getPoint(indices[w]);

      if (EPSILON > ((b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x()))) {
         return false;
      }

      for (int p = 0; p < n; p++) {
         if ((p == u) || (p == v) || (p == w)) {
            continue;
         }

         if (GTriangle.isTriangleContainsPoint(a, b, c, polygon.getPoint(indices[p]))) {
            return false;
         }
      }

      return true;
   }


   @Override
   public GEarClippingTriangulator2D.Result apply(final GEarClippingTriangulator2D.Parameters parameters) {
      final List<GIndexedTriangle> triangles = triangulate(parameters._polygon, parameters._verbose);
      return new GEarClippingTriangulator2D.Result(triangles);
   }

}
