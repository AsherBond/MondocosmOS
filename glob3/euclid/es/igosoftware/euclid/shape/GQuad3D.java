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


package es.igosoftware.euclid.shape;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorFunction;


public final class GQuad3D
         extends
            GQuad<IVector3, GSegment3D, GAxisAlignedBox>
         implements
            ISimplePolygon3D {

   private static final long serialVersionUID = 1L;


   //   private final GPlane      _plane;
   //   private GQuad2D           _quad2d;


   public GQuad3D(final IVector3 pV0,
                  final IVector3 pV1,
                  final IVector3 pV2,
                  final IVector3 pV3) {
      super(pV0, pV1, pV2, pV3);

      //      _plane = initializePlane();
   }


   //   private GPlane initializePlane() {
   //      final List<IVector3> points = new ArrayList<IVector3>(getPoints());
   //      try {
   //         final GPlane plane = GPlane.getBestFitPlane(points);
   //
   //         for (final IVector3 point : points) {
   //            if (!plane.contains(point)) {
   //               throw new IllegalArgumentException("Points are not coplanar");
   //            }
   //         }
   //
   //         return plane;
   //      }
   //      catch (final GColinearException e) {
   //         throw new IllegalArgumentException(e);
   //      }
   //      catch (final GInsufficientPointsException e) {
   //         throw new IllegalArgumentException(e);
   //      }
   //   }


   @Override
   public GAxisAlignedBox getBounds() {
      final IVector3 lower = GVectorUtils.min(_v0, _v1, _v2, _v3);
      final IVector3 upper = GVectorUtils.max(_v0, _v1, _v2, _v3);
      return new GAxisAlignedBox(lower, upper);
   }


   //   private GQuad2D getPolygon2D() {
   //      if (_quad2d == null) {
   //         _quad2d = initializeQuad2D();
   //      }
   //      return _quad2d;
   //   }


   //   private GQuad2D initializeQuad2D() {
   //      final List<IVector2> points2d;
   //
   //      final List<IVector3> points = getPoints();
   //      if (_plane.isCloseToPlaneXY()) {
   //         points2d = GCollections.collect(points, new IFunction<IVector3, IVector2>() {
   //            @Override
   //            public IVector2 apply(final IVector3 element) {
   //               return new GVector2D(element.x(), element.y());
   //            }
   //         });
   //      }
   //      else if (_plane.isCloseToPlaneXZ()) {
   //         points2d = GCollections.collect(points, new IFunction<IVector3, IVector2>() {
   //            @Override
   //            public IVector2 apply(final IVector3 element) {
   //               return new GVector2D(element.x(), element.z());
   //            }
   //         });
   //      }
   //      else /*if (_plane.isCloseToPlaneYZ())*/{
   //         points2d = GCollections.collect(points, new IFunction<IVector3, IVector2>() {
   //            @Override
   //            public IVector2 apply(final IVector3 element) {
   //               return new GVector2D(element.y(), element.z());
   //            }
   //         });
   //      }
   //
   //      return new GQuad2D(points2d.get(0), points2d.get(1), points2d.get(2), points2d.get(3));
   //   }


   @Override
   public List<GTriangle3D> triangulate() {
      //      final GQuad2D pol2d = getPolygon2D();
      //      final GTriangulate.IndexedTriangle[] iTriangles = GTriangulate.triangulate(pol2d._v0, pol2d._v1, pol2d._v2, pol2d._v3);
      //
      //      final List<GTriangle3D> result = new ArrayList<GTriangle3D>(iTriangles.length);
      //      for (final GTriangulate.IndexedTriangle iTriangle : iTriangles) {
      //         result.add(new GTriangle3D(getPoint(iTriangle._v0), getPoint(iTriangle._v1), getPoint(iTriangle._v2)));
      //      }
      //      return result;

      if (isConvex()) {
         return Collections.unmodifiableList(Arrays.asList(new GTriangle3D(_v0, _v1, _v2), new GTriangle3D(_v2, _v3, _v0)));
      }

      throw new RuntimeException("Triangulation of non convex quads is not yet supported");
   }


   @Override
   public boolean contains(final IVector3 point) {
      if (!getBounds().contains(point)) {
         return false;
      }

      for (final GTriangle3D triangle : triangulate()) {
         if (triangle.contains(point)) {
            return true;
         }
      }

      return false;
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


   @Override
   protected List<GSegment3D> initializeEdges() {
      final List<IVector3> points = getPoints();
      final int pointsCount = points.size();

      final GSegment3D[] edges = new GSegment3D[pointsCount];

      int j = pointsCount - 1;
      for (int i = 0; i < pointsCount; j = i++) {
         edges[j] = new GSegment3D(points.get(j), points.get(i));
      }

      return Arrays.asList(edges);
   }


   @Override
   public boolean isConvex() {
      return GShapeUtils.isConvexQuad(_v0, _v1, _v2, _v3);
   }


   @Override
   public GQuad3D transform(final IVectorFunction<IVector3> transformer) {
      if (transformer == null) {
         return this;
      }
      return new GQuad3D(transformer.apply(_v0), transformer.apply(_v1), transformer.apply(_v2), transformer.apply(_v3));
   }


   @Override
   public double area() {
      return _v2.sub(_v0).cross(_v3.sub(_v1)).length() / 2;
   }


   @Override
   public boolean isCounterClockWise() {
      return GShapeUtils.isCounterClockWise(_v0, _v1, _v2, _v3);
   }


   @Override
   public boolean isClockWise() {
      return GShapeUtils.isClockWise(_v0, _v1, _v2, _v3);
   }


}
