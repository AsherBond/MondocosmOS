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

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.shape.GPlane;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;


public class GCapsule3D
         extends
            GNCapsule<IVector3, GSegment3D, GCapsule3D>
         implements
            IBounds3D<GCapsule3D>,
            IFinite3DBounds<GCapsule3D> {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;


   public GCapsule3D(final GSegment3D segment,
                     final double radius) {
      super(segment, radius);
   }


   @Override
   public GCapsule3D expandedByDistance(final double delta) {
      return new GCapsule3D(_segment, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Capsule 3D";
   }


   @Override
   public boolean touches(final IBounds3D<?> that) {
      return that.touchesWithCapsule3D(this);
   }


   @Override
   public boolean touchesWithBall(final GBall ball) {
      //      final double squareDistanceFrom = _segment._from.squaredDistance(ball._center);
      //      final double squareDistanceTo = _segment._to.squaredDistance(ball._center);
      //      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final IVector3 closestPoint = _segment.closestPoint(ball._center);
      final double squareDistance = closestPoint.squaredDistance(ball._center);

      final double radius = _radius + ball._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


   @Override
   public boolean touchesWithBox(final GAxisAlignedBox box) {
      return box.touchesWithCapsule3D(this);
   }


   @Override
   public boolean touchesWithPlane(final GPlane plane) {
      final double distFrom = plane.distance(_segment._from);
      final double distTo = plane.distance(_segment._to);

      return GMath.lessOrEquals(distFrom, _radius) || GMath.lessOrEquals(distTo, _radius);
   }


   @Override
   public GCapsule3D getBounds() {
      return this;
   }


   @Override
   public boolean touchesWithCapsule3D(final GCapsule3D capsule) {
      final IVector3 closestFrom = _segment.closestPoint(capsule._segment._from);
      final IVector3 closestTo = _segment.closestPoint(capsule._segment._to);

      final double squareDistanceFrom = capsule._segment.squaredDistance(closestFrom);
      final double squareDistanceTo = capsule._segment.squaredDistance(closestTo);
      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final double radius = _radius + capsule._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


   @Override
   public GAxisAlignedBox asAxisAlignedOrthotope() {
      return _segment.getBounds().expandedByDistance(_radius);
   }


   @Override
   public boolean touches(final GCapsule3D that) {
      return touchesWithCapsule3D(that);
   }


   @Override
   public boolean touchesBounds(final IBounds<IVector3, ?> that) {
      return touches((IBounds3D<?>) that);
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<IVector3, GCapsule3D> that) {
      if (that instanceof GCapsule3D) {
         final GCapsule3D thatNB = (GCapsule3D) that;
         return GMath.closeTo(_radius, thatNB._radius) && _segment.closeTo(thatNB._segment);
      }
      return false;
   }


   @Override
   public double volume() {
      /*
         The volume of a capsule is the volume of an ball of _radius plus the volume of a cylinder. 

      
                 _.-|""""""""""""""""""""""""|-._
               .'   |                        |   `.
              /     |                        |     \
             |      |        segment         |      |
             |      +------------------------+      |
             |      |                        |      |
              \     |                        |     /
               `._  |                        |  _.'
                  `-|........................|-'
                    |                        |
           half ball         cylinder         half ball


       */

      // see http://en.wikipedia.org/wiki/Sphere#Volume_of_a_sphere
      final double ballVolume = Math.PI * (_radius * _radius * _radius) * 4 / 3;

      // see http://en.wikipedia.org/wiki/Cylinder_%28geometry%29#Volume
      final double cylinderVolume = Math.PI * (_radius * _radius) * _segment.length();

      return ballVolume + cylinderVolume;
   }


}
