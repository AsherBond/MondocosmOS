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


package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GMath;


public abstract class GNCapsule<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

GeometryT extends GNCapsule<VectorT, SegmentT, GeometryT>

>
         extends
            GGeometryAbstract<VectorT>
         implements
            IFiniteBounds<VectorT, GeometryT> {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   //   public final VectorT      _startPoint;
   //   public final VectorT      _endPoint;
   public final SegmentT     _segment;
   public final double       _radius;


   public GNCapsule(final SegmentT segment,
                    final double radius) {
      super();
      _segment = segment;
      _radius = radius;
   }


   @Override
   public final byte dimensions() {
      return _segment.dimensions();
   }


   @Override
   public final double precision() {
      return _segment.precision();
   }


   @Override
   public final String toString() {
      return getStringName() + " [Segment:" + _segment + " Radius: " + _radius + "]";
   }


   protected abstract String getStringName();


   @Override
   public final boolean contains(final VectorT point) {
      return GMath.lessOrEquals(_segment.squaredDistance(point), (_radius * _radius));

   }


   @Override
   public final boolean containsOnBoundary(final VectorT point) {
      return GMath.closeToZero(distanceToBoundary(point));
   }


   public abstract GNCapsule<VectorT, SegmentT, GeometryT> expandedByDistance(final double delta);


   @Override
   public final double squaredDistance(final VectorT point) {
      final double distance = distance(point);
      return distance * distance;
   }


   @Override
   public final double squaredDistanceToBoundary(final VectorT point) {
      final double distance = distanceToBoundary(point);
      return distance * distance;
   }


   @Override
   public final double distance(final VectorT point) {
      if (contains(point)) {
         return 0;
      }

      return distanceToBoundary(point);
   }


   @Override
   public final double distanceToBoundary(final VectorT point) {
      return _segment.closestPoint(point).distance(point) - _radius;
   }


   @Override
   public final VectorT closestPoint(final VectorT point) {
      if (contains(point)) {
         return point;
      }

      return closestPointOnBoundary(point);
   }


   @Override
   public final VectorT closestPointOnBoundary(final VectorT point) {
      final VectorT closestPoint = _segment.closestPoint(point);
      final VectorT segmentRadiusDirection = closestPoint.sub(point).normalized();

      return closestPoint.add(segmentRadiusDirection.scale(_radius));
   }


   public boolean isFullInside(final GAxisAlignedOrthotope<VectorT, ?> orthotope) {


      final VectorT lower = _segment.closestPoint(orthotope._lower).sub(_radius);
      final VectorT upper = _segment.closestPoint(orthotope._upper).add(_radius);

      return lower.greaterOrEquals(orthotope._lower) && upper.lessOrEquals(orthotope._upper);
   }


   public SegmentT getSegment() {
      return _segment;
   }


   public double getRadius() {
      return _radius;
   }


   @Override
   public VectorT getCentroid() {
      return _segment.getCentroid();
   }


}
