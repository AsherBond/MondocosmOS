/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.euclid.experimental.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.utils.GGeometry2DRenderer;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GStringUtils;


public class GDelaunayTriangulator2D
         implements
            IAlgorithm<IVector2, GDelaunayTriangulator2D.Parameters, IVector2, GDelaunayTriangulator2D.Result> {


   public static class Parameters
            implements
               IAlgorithmParameters<IVector2> {

      private final IVector2[] _points;
      private final boolean    _verbose;


      public Parameters(final boolean verbose,
                        final IVector2... points) {
         _points = points;
         _verbose = verbose;
      }
   }


   public static class Result
            implements
               IAlgorithmResult<IVector2> {

      private final GIndexedTriangle[] _indexedTriangles;


      private Result(final GIndexedTriangle[] indexedTriangles) {
         super();
         _indexedTriangles = indexedTriangles;
      }


      public GIndexedTriangle[] getIndexedTriangles() {
         return _indexedTriangles;
      }
   }


   private static final class VectorAndIndex {
      private final IVector2 _vector;
      private final int      _originalIndex;


      public VectorAndIndex(final IVector2 vector,
                            final int index) {
         _vector = vector;
         _originalIndex = index;
      }
   }


   private static final class IndexedEdge {
      private int _from;
      private int _to;


      IndexedEdge() {
         _from = -1;
         _to = -1;
      }
   }


   private static final class Circle {
      private GVector2D _center;
      private double    _radius;
   }


   /*
     Return TRUE if a point is inside the circumcircle made up
     of the points (point0, point1, point2)
     The circumcircle is returned in circle
     NOTE: A point on the edge is inside the circumcircle
   */
   private static boolean isInsideCircumCircle(final IVector2 point,
                                               final IVector2 point0,
                                               final IVector2 point1,
                                               final IVector2 point2,
                                               final Circle circle) {

      final double x0 = point0.x();
      final double y0 = point0.y();

      final double x1 = point1.x();
      final double y1 = point1.y();

      final double x2 = point2.x();
      final double y2 = point2.y();


      /* Check for coincident points */
      if (GMath.closeTo(y0, y1) && GMath.closeTo(y1, y2)) {
         System.out.println("CircumCircle: Points are coincident.");
         return false;
      }

      final double xc;
      final double yc;

      if (GMath.closeTo(y1, y0)) {
         final double m2 = -(x2 - x1) / (y2 - y1);

         xc = GMath.average(x0, x1);
         yc = m2 * (xc - GMath.average(x2, x1)) + GMath.average(y2, y1);
      }
      else if (GMath.closeTo(y2, y1)) {
         final double m1 = -(x1 - x0) / (y1 - y0);

         xc = GMath.average(x1, x2);
         yc = m1 * (xc - GMath.average(x1, x0)) + GMath.average(y1, y0);
      }
      else {
         final double m1 = -(x1 - x0) / (y1 - y0);
         final double m2 = -(x2 - x1) / (y2 - y1);
         final double mx1 = GMath.average(x1, x0);
         final double my1 = GMath.average(y1, y0);

         xc = (m1 * mx1 - m2 * GMath.average(x2, x1) + GMath.average(y2, y1) - my1) / (m1 - m2);
         yc = m1 * (xc - mx1) + my1;
      }

      final double rsqr = GMath.squared(x1 - xc) + GMath.squared(y1 - yc);

      if (circle != null) {
         circle._center = new GVector2D(xc, yc);
         circle._radius = GMath.sqrt(rsqr);
      }

      final double drsqr = GMath.squared(point.x() - xc) + GMath.squared(point.y() - yc);
      return (drsqr <= rsqr);
   }


   public static GIndexedTriangle[] triangulate(final List<IVector2> points,
                                                final boolean verbose) {
      return triangulate(verbose, points.toArray(new IVector2[points.size()]));
   }


   public static GIndexedTriangle[] triangulate(final boolean verbose,
                                                final IVector2... originalPoints) {
      final long start = System.currentTimeMillis();

      final int pointsCount = originalPoints.length;


      if (pointsCount < 3) {
         throw new IllegalArgumentException("Insufficient points, 3 or more points are needed");
      }

      System.out.println("- Triangulating " + pointsCount + " points...");

      final GIndexedTriangle triangles[] = new GIndexedTriangle[pointsCount * 3];

      // save original indexes before sorting
      final VectorAndIndex[] points = new VectorAndIndex[pointsCount + 3];
      for (int i = 0; i < pointsCount; i++) {
         points[i] = new VectorAndIndex(originalPoints[i], i);
      }

      final IVector2.DefaultComparator vectorComparator = new IVector2.DefaultComparator();
      Arrays.sort(points, new Comparator<VectorAndIndex>() {
         @Override
         public int compare(final VectorAndIndex o1,
                            final VectorAndIndex o2) {
            if (o1 == null) {
               return 1;
            }
            if (o2 == null) {
               return -1;
            }
            return vectorComparator.compare(o1._vector, o2._vector);
         }
      });


      int nedge = 0;
      int emax = pointsCount;

      int ntri = 0;

      /* Allocate memory for the completeness list, flag for each triangle */
      final int trimax = 4 * pointsCount;
      final boolean[] complete = new boolean[trimax];
      for (int ic = 0; ic < trimax; ic++) {
         complete[ic] = false;
      }

      /* Allocate memory for the edge list */
      IndexedEdge[] edges = new IndexedEdge[emax];
      for (int ie = 0; ie < emax; ie++) {
         edges[ie] = new IndexedEdge();
      }

      /*
      Find the maximum and minimum vertex bounds.
      This is to allow calculation of the bounding triangle
      */
      final GAxisAlignedRectangle bounds = GAxisAlignedRectangle.minimumBoundingRectangle(originalPoints);
      final IVector2 extent = bounds.getExtent();
      final double dmax = Math.max(extent.x(), extent.y());
      final IVector2 center = bounds.getCenter();

      /*
       Set up the supertriangle
       This is a triangle which encompasses all the sample points.
       The supertriangle coordinates are added to the end of the
       vertex list. The supertriangle is the first triangle in
       the triangle list.
      */
      points[pointsCount + 0] = new VectorAndIndex(new GVector2D(center.x() - 2.0 * dmax, center.y() - dmax), pointsCount + 0);
      points[pointsCount + 1] = new VectorAndIndex(new GVector2D(center.x(), center.y() + 2.0 * dmax), pointsCount + 1);
      points[pointsCount + 2] = new VectorAndIndex(new GVector2D(center.x() + 2.0 * dmax, center.y() - dmax), pointsCount + 2);

      triangles[0] = new GIndexedTriangle(pointsCount + 0, pointsCount + 1, pointsCount + 2);
      complete[0] = false;
      ntri = 1;


      /*
              Include each point one at a time into the existing mesh
      */
      for (int i = 0; i < pointsCount; i++) {
         final IVector2 vector = points[i]._vector;

         nedge = 0;

         /*
           Set up the edge buffer.
           If the point lies inside the circumcircle then the
           three edges of that triangle are added to the edge buffer
           and that triangle is removed.
         */
         for (int j = 0; j < ntri; j++) {
            if (complete[j]) {
               continue;
            }

            final GIndexedTriangle triangleJ = triangles[j];
            final IVector2 point0 = points[triangleJ._i0]._vector;
            final IVector2 point1 = points[triangleJ._i1]._vector;
            final IVector2 point2 = points[triangleJ._i2]._vector;

            final Circle circle = new Circle();
            final boolean inside = isInsideCircumCircle(vector, point0, point1, point2, circle);
            if (circle._center._x + circle._radius < vector.x()) {
               complete[j] = true;
            }
            if (inside) {
               /* Check that we haven't exceeded the edge list size */
               if (nedge + 3 >= emax) {
                  emax += 100;
                  final IndexedEdge[] edges_n = new IndexedEdge[emax];
                  for (int ie = 0; ie < emax; ie++) {
                     edges_n[ie] = new IndexedEdge();
                  }
                  System.arraycopy(edges, 0, edges_n, 0, edges.length);
                  edges = edges_n;
               }

               edges[nedge + 0]._from = triangleJ._i0;
               edges[nedge + 0]._to = triangleJ._i1;
               edges[nedge + 1]._from = triangleJ._i1;
               edges[nedge + 1]._to = triangleJ._i2;
               edges[nedge + 2]._from = triangleJ._i2;
               edges[nedge + 2]._to = triangleJ._i0;
               nedge += 3;

               triangles[j] = new GIndexedTriangle(triangles[ntri - 1]._i0, triangles[ntri - 1]._i1, triangles[ntri - 1]._i2);
               complete[j] = complete[ntri - 1];
               ntri--;
               j--;
            }
         }

         /*
                 Tag multiple edges
                 Note: if all triangles are specified anticlockwise then all
                 interior edges are opposite pointing in direction.
         */
         for (int j = 0; j < nedge - 1; j++) {
            //if ( !(edges[j].p1 < 0 && edges[j].p2 < 0) )
            for (int k = j + 1; k < nedge; k++) {
               if ((edges[j]._from == edges[k]._to) && (edges[j]._to == edges[k]._from)) {
                  edges[j]._from = -1;
                  edges[j]._to = -1;
                  edges[k]._from = -1;
                  edges[k]._to = -1;
               }

               /* Shouldn't need the following, see note above */
               if ((edges[j]._from == edges[k]._from) && (edges[j]._to == edges[k]._to)) {
                  edges[j]._from = -1;
                  edges[j]._to = -1;
                  edges[k]._from = -1;
                  edges[k]._to = -1;
               }
            }
         }

         /*
                 Form new triangles for the current point
                 Skipping over any tagged edges.
                 All edges are arranged in clockwise order.
         */
         for (int j = 0; j < nedge; j++) {
            if ((edges[j]._from == -1) || (edges[j]._to == -1)) {
               continue;
            }
            if (ntri >= trimax) {
               return null;
            }

            triangles[ntri] = new GIndexedTriangle(edges[j]._from, edges[j]._to, i);
            complete[ntri] = false;
            ntri++;
         }
      }


      /*
              Remove triangles with supertriangle vertices
              These are triangles which have a vertex number greater than nv
      */
      for (int i = 0; i < ntri; i++) {
         if ((triangles[i]._i0 >= pointsCount) || (triangles[i]._i1 >= pointsCount) || (triangles[i]._i2 >= pointsCount)) {
            triangles[i] = triangles[ntri - 1];
            ntri--;
            i--;
         }
      }

      // convert the result to indexes of the original (before sorting) points
      final GIndexedTriangle[] result = new GIndexedTriangle[ntri];
      for (int i = 0; i < ntri; i++) {
         final int v0 = points[triangles[i]._i0]._originalIndex;
         final int v1 = points[triangles[i]._i1]._originalIndex;
         final int v2 = points[triangles[i]._i2]._originalIndex;
         result[i] = new GIndexedTriangle(v0, v1, v2);
      }

      if (verbose) {
         System.out.println("- Created " + result.length + " triangles from " + points.length + " points in "
                            + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
      }

      return result;
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GVoronoiTriangulator 0.1");
      System.out.println("------------------------\n");


      final IVector2[] points = new IVector2[200];

      final IVectorI2 imageSize = new GVector2I(1280, 1024);
      final GAxisAlignedRectangle bounds = new GAxisAlignedRectangle(GVector2D.ZERO, new GVector2D(imageSize.x(), imageSize.y()));

      for (int i = 0; i < points.length; i++) {
         points[i] = new GVector2D(imageSize.x() * Math.random(), imageSize.y() * Math.random());
      }

      //      GUtils.delay(30000);

      final GIndexedTriangle[] iTriangles = triangulate(true, points);


      final List<GTriangle2D> triangles = new ArrayList<GTriangle2D>(iTriangles.length);
      for (final GIndexedTriangle iTriangle : iTriangles) {
         triangles.add(new GTriangle2D(points[iTriangle._i0], points[iTriangle._i1], points[iTriangle._i2]));
      }

      final GGeometry2DRenderer renderer = new GGeometry2DRenderer(bounds, imageSize);
      renderer.drawGeometries(triangles, true);

      ImageIO.write(renderer.getImage(), "png", GFileName.absolute("home", "dgd", "Desktop", "triangles.png").asFile());

      System.out.println("- done!");
   }


   @Override
   public String getName() {
      return "Delaunay Triangulator 2D";
   }


   @Override
   public String getDescription() {
      return "In mathematics and computational geometry, a Delaunay triangulation for a set P of points in the plane is a "
             + "triangulation DT(P) such that no point in P is inside the circumcircle of any triangle in DT(P). Delaunay "
             + "triangulations maximize the minimum angle of all the angles of the triangles in the triangulation; they tend "
             + "to avoid skinny triangles. The triangulation was invented by Boris Delaunay in 1934. "
             + "See http://en.wikipedia.org/wiki/Delaunay_triangulation";
   }


   @Override
   public GDelaunayTriangulator2D.Result apply(final GDelaunayTriangulator2D.Parameters parameters) {
      final GIndexedTriangle[] result = triangulate(parameters._verbose, parameters._points);
      return new GDelaunayTriangulator2D.Result(result);
   }


}
