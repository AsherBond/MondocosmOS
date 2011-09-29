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

import java.util.List;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GBall;
import es.igosoftware.euclid.bounding.GCapsule3D;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.bounding.IBounds3D;
import es.igosoftware.euclid.bounding.IInfiniteBounds;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public final class GPlane
         extends
            GGeometryAbstract<IVector3>
         implements
            IBounds3D<GPlane>,
            IInfiniteBounds<IVector3, GPlane> {

   private static final long serialVersionUID = 1L;


   public static GPlane getBestFitPlane(final IVector3... points) throws GColinearException, GInsufficientPointsException {
      return getBestFitPlane(GVectorUtils.getAverage3(points), points);
   }


   public static GPlane getBestFitPlane(final List<IVector3> points) throws GColinearException, GInsufficientPointsException {
      return getBestFitPlane(GVectorUtils.getAverage3(points), points);
   }


   public static GPlane getBestFitPlane(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices)
                                                                                                                        throws GColinearException,
                                                                                                                        GInsufficientPointsException {
      return getBestFitPlane(null, vertices, null);
   }


   public static GPlane getBestFitPlane(final IVector3 center,
                                        final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices)
                                                                                                                        throws GColinearException,
                                                                                                                        GInsufficientPointsException {
      return getBestFitPlane(center, vertices, null);
   }


   public static GPlane getBestFitPlane(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                        final int[] verticesIndexes) throws GColinearException, GInsufficientPointsException {
      return getBestFitPlane(null, vertices, verticesIndexes);
   }


   /**
    * Implementation note: From 3D math primer for graphics and game development (Fletcher Dunn and Ian Parberry) Page 255.
    * Listing 12.2
    * 
    * @param originalCenter
    * @param originalVertices
    * @param verticesIndexes
    * @return
    * @throws GColinearException
    * @throws GInsufficientPointsException
    */
   public static GPlane getBestFitPlane(final IVector3 originalCenter,
                                        final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> originalVertices,
                                        final int[] verticesIndexes) throws GColinearException, GInsufficientPointsException {

      IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices;
      if (verticesIndexes == null) {
         vertices = originalVertices;
      }
      else {
         vertices = originalVertices.asSubContainer(verticesIndexes);
      }

      final int pointsCount = vertices.size();

      if (pointsCount < 3) {
         throw new GInsufficientPointsException(vertices);
      }

      final IVector3 center;
      if (originalCenter != null) {
         center = originalCenter;
      }
      else {
         center = vertices.getAverage()._point;
      }

      vertices = GVectorUtils.sortClockwise(center, vertices);

      double normalX = 0;
      double normalY = 0;
      double normalZ = 0;

      double totalX = 0;
      double totalY = 0;
      double totalZ = 0;

      final IVector3 lastPoint = vertices.getPoint(pointsCount - 1);
      double previousX = lastPoint.x();
      double previousY = lastPoint.y();
      double previousZ = lastPoint.z();

      //for (final IVector3 current : points) {
      for (int i = 0; i < pointsCount; i++) {
         final IVector3 current = vertices.getPoint(i);
         final double currentX = current.x();
         final double currentY = current.y();
         final double currentZ = current.z();

         normalX += (previousZ + currentZ) * (previousY - currentY);
         normalY += (previousX + currentX) * (previousZ - currentZ);
         normalZ += (previousY + currentY) * (previousX - currentX);

         totalX += currentX;
         totalY += currentY;
         totalZ += currentZ;

         previousX = currentX;
         previousY = currentY;
         previousZ = currentZ;
      }

      if (GMath.closeToZero(normalX) && GMath.closeToZero(normalY) && GMath.closeToZero(normalZ)) {
         throw new GColinearException(vertices);
      }

      final IVector3 normal = new GVector3D(normalX, normalY, normalZ).normalized();

      final double averageX = totalX / pointsCount;
      final double averageY = totalY / pointsCount;
      final double averageZ = totalZ / pointsCount;
      final GVector3D average = new GVector3D(averageX, averageY, averageZ);

      final double d = average.dot(normal);

      return new GPlane(normal, d);
   }


   /**
    * Implementation note: From 3D math primer for graphics and game development (Fletcher Dunn and Ian Parberry) Page 255.
    * Listing 12.2
    * 
    */
   public static GPlane getBestFitPlane(final IVector3 center,
                                        final IVector3[] points) throws GColinearException, GInsufficientPointsException {
      final int pointsCount = points.length;

      if (pointsCount < 3) {
         throw new GInsufficientPointsException(points);
      }

      GVectorUtils.sortClockwise(center, points);

      double normalX = 0;
      double normalY = 0;
      double normalZ = 0;

      double totalX = 0;
      double totalY = 0;
      double totalZ = 0;

      final IVector3 lastPoint = points[pointsCount - 1];
      double previousX = lastPoint.x();
      double previousY = lastPoint.y();
      double previousZ = lastPoint.z();

      for (final IVector3 current : points) {
         final double currentX = current.x();
         final double currentY = current.y();
         final double currentZ = current.z();

         normalX += (previousZ + currentZ) * (previousY - currentY);
         normalY += (previousX + currentX) * (previousZ - currentZ);
         normalZ += (previousY + currentY) * (previousX - currentX);

         totalX += currentX;
         totalY += currentY;
         totalZ += currentZ;

         previousX = currentX;
         previousY = currentY;
         previousZ = currentZ;
      }

      if (GMath.closeToZero(normalX) && GMath.closeToZero(normalY) && GMath.closeToZero(normalZ)) {
         throw new GColinearException(points);
      }

      final IVector3 normal = new GVector3D(normalX, normalY, normalZ).normalized();

      final double averageX = totalX / pointsCount;
      final double averageY = totalY / pointsCount;
      final double averageZ = totalZ / pointsCount;
      final GVector3D average = new GVector3D(averageX, averageY, averageZ);

      final double d = average.dot(normal);

      return new GPlane(normal, d);
   }


   /**
    * Implementation note: From 3D math primer for graphics and game development (Fletcher Dunn and Ian Parberry) Page 255.
    * Listing 12.2
    * 
    */
   public static GPlane getBestFitPlane(final IVector3 center,
                                        final List<IVector3> points) throws GColinearException, GInsufficientPointsException {
      final int pointsCount = points.size();

      if (pointsCount < 3) {
         throw new GInsufficientPointsException(points);
      }

      GVectorUtils.sortClockwise(center, points);

      double normalX = 0;
      double normalY = 0;
      double normalZ = 0;

      double totalX = 0;
      double totalY = 0;
      double totalZ = 0;

      final IVector3 lastPoint = points.get(pointsCount - 1);
      double previousX = lastPoint.x();
      double previousY = lastPoint.y();
      double previousZ = lastPoint.z();

      for (final IVector3 current : points) {
         final double currentX = current.x();
         final double currentY = current.y();
         final double currentZ = current.z();

         normalX += (previousZ + currentZ) * (previousY - currentY);
         normalY += (previousX + currentX) * (previousZ - currentZ);
         normalZ += (previousY + currentY) * (previousX - currentX);

         totalX += currentX;
         totalY += currentY;
         totalZ += currentZ;

         previousX = currentX;
         previousY = currentY;
         previousZ = currentZ;
      }

      if (GMath.closeToZero(normalX) && GMath.closeToZero(normalY) && GMath.closeToZero(normalZ)) {
         throw new GColinearException(points);
      }

      final IVector3 normal = new GVector3D(normalX, normalY, normalZ).normalized();

      final double averageX = totalX / pointsCount;
      final double averageY = totalY / pointsCount;
      final double averageZ = totalZ / pointsCount;
      final GVector3D average = new GVector3D(averageX, averageY, averageZ);

      final double d = average.dot(normal);

      return new GPlane(normal, d);
   }


   public final IVector3 _normal;
   public final double   _d;


   public GPlane(final IVector3 point0,
                 final IVector3 point1,
                 final IVector3 point2) throws GColinearException {
      GAssert.notNull(point0, "p0");
      GAssert.notNull(point1, "p1");
      GAssert.notNull(point2, "p2");

      final IVector3 p1p0 = point1.sub(point0);
      final IVector3 p2p0 = point2.sub(point0);
      _normal = p1p0.cross(p2p0).normalized();
      _d = -_normal.dot(point0);

      if (_normal.closeToZero()) {
         throw new GColinearException(point0, point1, point2);
      }
   }


   public GPlane(final IVector3 normal,
                 final double d) {
      GAssert.notNull(normal, "normal");

      _normal = normal.normalized();
      _d = d;
   }


   public IVector3 getNormal() {
      return _normal;
   }


   public double getD() {
      return _d;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(_d);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((_normal == null) ? 0 : _normal.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GPlane other = (GPlane) obj;
      if (Double.doubleToLongBits(_d) != Double.doubleToLongBits(other._d)) {
         return false;
      }
      if (_normal == null) {
         if (other._normal != null) {
            return false;
         }
      }
      else if (!_normal.equals(other._normal)) {
         return false;
      }
      return true;
   }


   @Override
   public byte dimensions() {
      return _normal.dimensions();
   }


   @Override
   public final double precision() {
      return _normal.precision();
   }


   @Override
   public IVector3 closestPoint(final IVector3 point) {
      return closestPointOnBoundary(point);
   }


   @Override
   public IVector3 closestPointOnBoundary(final IVector3 point) {
      // from Real-Time Collision Detection (Christer Ericson)
      //    page 127

      final double sd = signedDistance(point);
      return point.sub(_normal.scale(sd));
   }


   /**
    * Implementation note: From 3D math primer for graphics and game development (Fletcher Dunn and Ian Parberry) Page 256, 12.5.4
    */
   public double signedDistance(final IVector3 point) {
      // from Real-Time Collision Detection (Christer Ericson)
      //    page 127

      // return (point.dot(normal) - d) / normal.dot(normal); // when normal is not normalized
      return point.dot(_normal) - _d;
   }


   @Override
   public double distance(final IVector3 point) {
      final double signedDistance = signedDistance(point);
      if (GMath.positiveOrZero(signedDistance)) {
         return 0;
      }
      return Math.abs(signedDistance);
   }


   @Override
   public double distanceToBoundary(final IVector3 point) {
      return Math.abs(signedDistance(point));
   }


   @Override
   public double squaredDistance(final IVector3 point) {
      final double distance = distance(point);
      return distance * distance;
   }


   @Override
   public double squaredDistanceToBoundary(final IVector3 point) {
      final double distance = distanceToBoundary(point);
      return distance * distance;
   }


   @Override
   public String toString() {
      return "Plane [normal=" + _normal + ", d=" + _d + "]";
   }


   @Override
   public boolean touches(final IBounds3D<?> that) {
      return that.touchesWithPlane(this);
   }


   @Override
   public boolean touchesWithBall(final GBall ball) {
      return ball.touchesWithPlane(this);
   }


   @Override
   public boolean touchesWithBox(final GAxisAlignedBox box) {
      final IVector3 boxCenter = box._center;
      final IVector3 halfExtent = box._upper.sub(boxCenter); // Compute positive extents

      // Compute the projection interval radius of b onto L(t) = b.c + t * normal
      final double r = (halfExtent.x() * Math.abs(_normal.x())) + (halfExtent.y() * Math.abs(_normal.y()))
                       + (halfExtent.z() * Math.abs(_normal.z()));

      // Compute distance of box center from plane
      //final double s = normal.dot(boxCenter) - d;
      final double signedDistance = signedDistance(boxCenter);

      // Intersection occurs when distance s falls within [-r,+r] interval
      //return Math.abs(signedDistance) <= r;
      return GMath.lessOrEquals(Math.abs(signedDistance), r);
   }


   @Override
   public boolean touchesWithPlane(final GPlane plane) {
      // from Real-Time Collision Detection (Christer Ericson)
      //   page 209

      // Compute direction of intersection line
      final IVector3 direction = _normal.cross(plane._normal);

      // If distance is zero, the planes are parallel (and separated) or coincident
      if (GMath.closeToZero(direction.dot(direction))) {
         // check for coincident
         if (GMath.closeTo(_d, plane._d)) {
            return true;
         }

         // they’re not considered intersecting
         return false;
      }

      return true;
   }


   @Override
   public GPlane getBounds() {
      return this;
   }


   @Override
   public boolean contains(final IVector3 point) {
      return GMath.positiveOrZero(signedDistance(point));
   }


   @Override
   public boolean containsOnBoundary(final IVector3 point) {
      return GMath.closeToZero(signedDistance(point));
   }


   public boolean isCloseToPlaneXY() {
      return (_normal.z() > Math.abs(_normal.x())) && (_normal.z() > Math.abs(_normal.y()));
   }


   public boolean isCloseToPlaneXZ() {
      return (Math.abs(_normal.y()) > Math.abs(_normal.x())) && (Math.abs(_normal.y()) > _normal.z());
   }


   public boolean isCloseToPlaneYZ() {
      return (Math.abs(_normal.x()) > Math.abs(_normal.y())) && (Math.abs(_normal.x()) > _normal.z());
   }


   @Override
   public boolean touchesWithCapsule3D(final GCapsule3D capsule) {
      return capsule.touchesWithPlane(this);
   }


   public IVector3 getIntersection(final GSegment3D segment) {
      // from: http://local.wasp.uwa.edu.au/~pbourke/geometry/planeline/

      final IVector3 p1 = segment._from;
      //      final double x1 = p1.x();
      //      final double y1 = p1.y();
      //      final double z1 = p1.z();

      final IVector3 p2 = segment._to;
      //      final double x2 = p2.x();
      //      final double y2 = p2.y();
      //      final double z2 = p2.z();

      //final IVector3 p0 = _normal.scale(_d);

      // plane definition: Ax + By + Cz + D = 0
      // plane normal = A, B, C
      //final double a = _normal.x();
      //final double b = _normal.y();
      //final double c = _normal.z();
      //final double d = _d; //-(a * p0.x()) - (b * p0.y()) - (c * p0.z());

      //final double denominator = a * (x1 - x2) + b * (y1 - y2) + c * (z1 - z2);
      final double denominator = _normal.dot(p1.sub(p2));

      if (denominator == 0) {
         return null;
      }

      //final double u = (a * x1 + b * y1 + c * z1 + d) / denominator;
      final double u = (_normal.dot(p1) + _d) / denominator;

      if (!GMath.between(u, 0.0, 1.0)) {
         return null;
      }

      // P = P1 + u (P2 - P1)

      final IVector3 temp = p2.sub(p1).scale(u);

      return p1.add(temp);


   }


   public double getZCoordinate(final IVector2 vectorXY) {

      return getZCoordinate(vectorXY.x(), vectorXY.y());

   }


   public double getZCoordinate(final double x,
                                final double y) {
      // from plane definition: Ax + By + Cz + D = 0
      // plane normal = A, B, C
      return -(_d + _normal.x() * x + _normal.y() * y) / _normal.z();

      // from plane definition: Ax + By + Cz = D
      // plane normal = A, B, C
      //return (_d - _normal.x() * x - _normal.y() * y) / _normal.z();

   }


   public IVector3 getPointForXYCordenates(final IVector2 vectorXY) {

      return getPointForXYCordenates(vectorXY.x(), vectorXY.y());

   }


   public IVector3 getPointForXYCordenates(final double x,
                                           final double y) {

      return new GVector3D(x, y, getZCoordinate(x, y));

   }


   @Override
   public boolean touches(final GPlane that) {
      return touchesWithPlane(that);
   }


   @Override
   public boolean touchesBounds(final IBounds<IVector3, ?> that) {
      return touches((IBounds3D<?>) that);
   }


   @Override
   public IVector3 getCentroid() {
      throw new RuntimeException("The plane has not a centroid");
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<IVector3, GPlane> that) {
      if (that instanceof GPlane) {
         final GPlane thatPlane = (GPlane) that;
         return GMath.closeTo(_d, thatPlane._d) && _normal.closeTo(thatPlane._normal);
      }
      return false;
   }


}
