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

import es.igosoftware.euclid.shape.GPlane;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;


public final class GBall
         extends
            GNBall<IVector3, GBall>
         implements
            IBounds3D<GBall>,
            IFinite3DBounds<GBall> {

   private static final long serialVersionUID = 1L;


   public GBall(final IVector3 center,
                final double radius) {
      super(center, radius);
   }


   @Override
   public GBall expandedByDistance(final double delta) {
      return new GBall(_center, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Ball";
   }


   @Override
   public boolean touches(final IBounds3D<?> that) {
      return that.touchesWithBall(this);
   }


   @Override
   public boolean touchesWithBox(final GAxisAlignedBox box) {
      return box.touchesWithBall(this);
   }


   @Override
   public boolean touchesWithBall(final GBall ball) {
      final double radius = _radius + ball._radius;
      return GMath.lessOrEquals(_center.squaredDistance(ball._center), radius * radius);
      //      return (center.squaredDistance(ball.center) <= radiusSquared);
   }


   @Override
   public GBall getBounds() {
      return this;
   }


   @Override
   public boolean touchesWithPlane(final GPlane plane) {
      final double dist = plane.distance(_center);
      return GMath.lessOrEquals(dist, _radius);
   }


   @Override
   public boolean touchesWithCapsule3D(final GCapsule3D capsule) {
      return capsule.touchesWithBall(this);
   }


   @Override
   public GAxisAlignedBox asAxisAlignedOrthotope() {
      return new GAxisAlignedBox(_center.sub(_radius), _center.add(_radius));
   }


   @Override
   public boolean touches(final GBall that) {
      return touchesWithBall(that);
   }


   @Override
   public boolean touchesBounds(final IBounds<IVector3, ?> that) {
      return touches((IBounds3D<?>) that);
   }


   @Override
   public double volume() {
      // see http://en.wikipedia.org/wiki/Sphere#Volume_of_a_sphere
      return Math.PI * (_radius * _radius * _radius) * 4 / 3;
   }


}
