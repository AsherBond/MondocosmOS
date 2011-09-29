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
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;
import es.igosoftware.util.GMath;


public final class GCapsule2D
         extends
            GNCapsule<IVector2, GSegment2D, GCapsule2D>
         implements
            IBounds2D<GCapsule2D>,
            IFinite2DBounds<GCapsule2D> {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;


   public GCapsule2D(final GSegment2D segment,
                     final double radius) {
      super(segment, radius);
   }


   @Override
   public GCapsule2D expandedByDistance(final double delta) {
      return new GCapsule2D(_segment, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Capsule 2D";
   }


   @Override
   public boolean touches(final IBounds2D<?> that) {
      return that.touchesWithCapsule2D(this);
   }


   @Override
   public boolean touchesWithDisk(final GDisk disk) {
      //      final double squareDistanceFrom = _segment._from.squaredDistance(disk._center);
      //      final double squareDistanceTo = _segment._to.squaredDistance(disk._center);
      //      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final IVector2 closestPoint = _segment.closestPoint(disk._center);
      final double squareDistance = closestPoint.squaredDistance(disk._center);

      final double radius = _radius + disk._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


   @Override
   public boolean touchesWithRectangle(final GAxisAlignedRectangle rectangle) {
      return rectangle.touchesWithCapsule2D(this);
   }


   @Override
   public GCapsule2D getBounds() {
      return this;
   }


   @Override
   public boolean touchesWithCapsule2D(final GCapsule2D capsule) {
      final IVector2 closestFrom = _segment.closestPoint(capsule._segment._from);
      final IVector2 closestTo = _segment.closestPoint(capsule._segment._to);

      final double squareDistanceFrom = capsule._segment.squaredDistance(closestFrom);
      final double squareDistanceTo = capsule._segment.squaredDistance(closestTo);
      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final double radius = _radius + capsule._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


   @Override
   public GAxisAlignedRectangle asAxisAlignedOrthotope() {
      return _segment.getBounds().expandedByDistance(_radius);
   }


   @Override
   public boolean touches(final GCapsule2D that) {
      return touchesWithCapsule2D(that);
   }


   @Override
   public boolean touchesBounds(final IBounds<IVector2, ?> that) {
      return touches((IBounds2D<?>) that);
   }


   @Override
   public GCapsule2D transform(final IVectorFunction<IVector2> transformer) {
      if (transformer == null) {
         return this;
      }

      final GDisk fromDisk = new GDisk(_segment._from, _radius);
      final GDisk toDisk = new GDisk(_segment._to, _radius);

      final GDisk transformedFromDisk = fromDisk.transform(transformer);
      final GDisk transformedToDisk = toDisk.transform(transformer);

      final GSegment2D segment = new GSegment2D(transformedFromDisk._center, transformedToDisk._center);
      final double radius = (transformedFromDisk._radius + transformedToDisk._radius) / 2;
      return new GCapsule2D(segment, radius);
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<IVector2, GCapsule2D> that) {
      if (that instanceof GCapsule2D) {
         final GCapsule2D thatNB = (GCapsule2D) that;
         return GMath.closeTo(_radius, thatNB._radius) && _segment.closeTo(thatNB._segment);
      }
      return false;
   }


   @Override
   public double area() {
      /*
         The area of a capsule is the area of an disk of _radius plus the area of a rectangle. 

      
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
           half disk        rectangle         half disk


       */

      final double diskArea = Math.PI * (_radius * _radius);

      final double rectangleWidth = _radius * 2;
      final double rectangleHeight = _segment.length();
      final double rectangleArea = rectangleWidth * rectangleHeight;

      return diskArea + rectangleArea;
   }


   @Override
   public double perimeter() {
      final double diskPerimeter = Math.PI * 2 * _radius;

      return diskPerimeter + _segment.length() * 2;
   }


   @Override
   public GSegment2D getHorizontalBisector() {
      return _segment.getHorizontalBisector();
   }


   @Override
   public GSegment2D getVerticalBisector() {
      return _segment.getVerticalBisector();
   }


}
