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

import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;
import es.igosoftware.util.GMath;


public final class GSegment2D
         extends
            GSegment<IVector2, GSegment2D, GAxisAlignedRectangle>
         implements
            IPolygonalChain2D {

   private static final long serialVersionUID = 1L;


   public GSegment2D(final IVector2 fromPoint,
                     final IVector2 toPoint) {
      super(fromPoint, toPoint);
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return new GAxisAlignedRectangle(_from, _to);
   }


   //-- version reviewed
   public GLineIntersectionResult<IVector2> getIntersection(final GSegment2D that) {

      // ---------------------------------------------------------------------------------------------------------------------------
      // -- Algorithm comming from:
      // -- [Gems III] pp. 199-202 Faster Line Segment Intersection;  http://www.cgafaq.info/wiki/Intersecting_line_segments_%282D%29
      // -- [O’Rourke (C)] pp. 249-51;  http://osprey.unisa.ac.za/phorum/read.php?145,63441,63441
      // ---------------------------------------------------------------------------------------------------------------------------

      final double thisFromX = _from.x();
      final double thisFromY = _from.y();

      final double thisToX = _to.x();
      final double thisToY = _to.y();

      final double thatFromX = that._from.x();
      final double thatFromY = that._from.y();

      final double thatToX = that._to.x();
      final double thatToY = that._to.y();

      final double denominator = ((thatToY - thatFromY) * (thisToX - thisFromX))
                                 - ((thatToX - thatFromX) * (thisToY - thisFromY));

      final double numeratorA = ((thatToX - thatFromX) * (thisFromY - thatFromY))
                                - ((thatToY - thatFromY) * (thisFromX - thatFromX));

      final double numeratorB = ((thisToX - thisFromX) * (thisFromY - thatFromY))
                                - ((thisToY - thisFromY) * (thisFromX - thatFromX));

      //-- check for paralell or coincident segments
      if (GMath.closeToZero(denominator)) {
         if (GMath.closeToZero(numeratorA) && GMath.closeToZero(numeratorB)) {

            GSegment2D resultSegment = null;

            if (contains(that._from)) {
               if (that.contains(_from)) {
                  resultSegment = new GSegment2D(_from, that._from);
               }
               resultSegment = new GSegment2D(_to, that._from);
            }
            else if (contains(that._to)) {
               if (that.contains(_from)) {
                  resultSegment = new GSegment2D(_from, that._to);
               }
               resultSegment = new GSegment2D(_to, that._to);
            }

            if (resultSegment == null) {
               return new GLineIntersectionResult<IVector2>(GLineIntersectionResult.Type.PARALLEL, null);
            }

            if (resultSegment._from.closeTo(resultSegment._to)) { // coincident only at one of the extreme points
               return new GLineIntersectionResult<IVector2>(GLineIntersectionResult.Type.INTERSECTING,
                        (GVector2D) resultSegment._from);
            }

            return new GLineIntersectionResult<IVector2>(GLineIntersectionResult.Type.COINCIDENT, resultSegment);

         }

         return new GLineIntersectionResult<IVector2>(GLineIntersectionResult.Type.PARALLEL, null);
      }

      //final double ua = GMath.clamp(numeratorA / denominator, 0, 1);
      //final double ub = GMath.clamp(numeratorB / denominator, 0, 1);
      // --
      // -- Erroneous clamping. Asumming the parametric form of the segment ecuation (AB=A+t(B-A), t ∈ [0,1] ). We are computing  
      // -- the value of the parameter t (ua, ub), that it obtained from the ecuations below. If we clamp this values before 
      // -- comparing, then we are forcing the result to be a segment, and next comparation becomes invalid for the algorithm 
      // -- used. The algorithm based the decision on the comparation of the t value obtained on both ecuations (ua and ub): 
      // -- The segment intersects if: 0 ≤ ua ≤ 1 and 0 ≤ ub ≤ 1.
      // --
      final double ua = numeratorA / denominator;
      final double ub = numeratorB / denominator;

      final double precision = GMath.maxD(precision(), that.precision());
      if (GMath.between(ua, 0, 1, precision) && GMath.between(ub, 0, 1, precision)) {
         // Get the intersection point. 
         final double intersectionX = thisFromX + ua * (thisToX - thisFromX);
         final double intersectionY = thisFromY + ua * (thisToY - thisFromY);
         //final IVector2 intersection = new GVector2D(intersectionX, intersectionY);

         return new GLineIntersectionResult<IVector2>(GLineIntersectionResult.Type.INTERSECTING, new GVector2D(intersectionX,
                  intersectionY));
      }

      return new GLineIntersectionResult<IVector2>(GLineIntersectionResult.Type.NOT_INTERSECTING, null);
   }


   public boolean intersects(final GSegment2D that) {
      final GLineIntersectionResult.Type intersectionType = getIntersection(that).getType();

      return (intersectionType == GLineIntersectionResult.Type.COINCIDENT)
             || (intersectionType == GLineIntersectionResult.Type.INTERSECTING);


      //      final GSegment2D segment1 = this;
      //      final GSegment2D segment2 = that;
      //
      //      final IVector2 begin = segment1._from;
      //      final IVector2 end = segment1._to;
      //      final IVector2 anotherBegin = segment2._from;
      //      final IVector2 anotherEnd = segment2._to;
      //
      //      final double denominator = ((anotherEnd.y() - anotherBegin.y()) * (end.x() - begin.x()))
      //                                 - ((anotherEnd.x() - anotherBegin.x()) * (end.y() - begin.y()));
      //
      //      final double numeratorA = ((anotherEnd.x() - anotherBegin.x()) * (begin.y() - anotherBegin.y()))
      //                                - ((anotherEnd.y() - anotherBegin.y()) * (begin.x() - anotherBegin.x()));
      //
      //      final double numeratorB = ((end.x() - begin.x()) * (begin.y() - anotherBegin.y()))
      //                                - ((end.y() - begin.y()) * (begin.x() - anotherBegin.x()));
      //
      //      if (denominator == 0.0) {
      //         //         if ((numeratorA == 0.0) && (numeratorB == 0.0)) {
      //         //            return false;
      //         //         }
      //
      //         return false;
      //      }
      //
      //      final double ua = numeratorA / denominator;
      //      final double ub = numeratorB / denominator;
      //
      //      if ((ua >= 0.0) && (ua <= 1.0) && (ub >= 0.0) && (ub <= 1.0)) {
      //         return true;
      //      }
      //
      //      return false;
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


   @Override
   public List<GSegment2D> getEdges() {
      return Collections.singletonList(this);
   }


   //   public boolean neighborWithSegment(final GSegment2D that) {
   //
   //      final double epsilon = 0.0001;
   //
   //      return neighborWithSegment(that, epsilon);
   //
   //   }
   //
   //
   //   public boolean neighborWithSegment(final GSegment2D that,
   //                                      final double epsilon) {
   //
   //      final GVector2D thisMinX = new GVector2D(_from.x(), _from.y() - epsilon);
   //      final GVector2D thisMaxX = new GVector2D(_to.x(), _to.y() + epsilon);
   //      final GVector2D thisMinY = new GVector2D(_from.x() - epsilon, _from.y());
   //      final GVector2D thisMaxY = new GVector2D(_to.x() + epsilon, _to.y());
   //
   //      final GVector2D thatMinX = new GVector2D(that._from.x(), that._from.y() - epsilon);
   //      final GVector2D thatMaxX = new GVector2D(that._to.x(), that._to.y() + epsilon);
   //      final GVector2D thatMinY = new GVector2D(that._from.x() - epsilon, that._from.y());
   //      final GVector2D thatMaxY = new GVector2D(that._to.x() + epsilon, that._to.y());
   //
   //      final GLineIntersectionResult.Type intersectionType = getIntersection(that).getType();
   //      if ((intersectionType == GLineIntersectionResult.Type.COINCIDENT)
   //          || (intersectionType == GLineIntersectionResult.Type.PARALLEL)) {
   //
   //         final boolean condition1;
   //         final boolean condition2;
   //         if (GMath.closeTo(_from.y(), _to.y())) { // parallel to x axis
   //            condition1 = that._from.between(thisMinX, thisMaxX) || that._to.between(thisMinX, thisMaxX);
   //            condition2 = _from.between(thatMinX, thatMaxX) || _to.between(thatMinX, thatMaxX);
   //         }
   //         else if (GMath.closeTo(_from.x(), _to.x())) { // parallel to y axis
   //            condition1 = that._from.between(thisMinY, thisMaxY) || that._to.between(thisMinY, thisMaxY);
   //            condition2 = _from.between(thatMinY, thatMaxY) || _to.between(thatMinY, thatMaxY);
   //         }
   //         else {
   //            condition1 = (that._from.between(thisMinX, thisMaxX) && that._from.between(thisMinY, thisMaxY))
   //                         || (that._to.between(thisMinX, thisMaxX) && that._to.between(thisMinY, thisMaxY));
   //            condition2 = (_from.between(thatMinX, thatMaxX) && _from.between(thatMinY, thatMaxY))
   //                         || (_to.between(thatMinX, thatMaxX) && _to.between(thatMinY, thatMaxY));
   //         }
   //
   //         return condition1 || condition2;
   //      }
   //
   //      return false;
   //   }


   @Override
   public GSegment2D clone() {
      return this;
   }


   @Override
   public GSegment2D transform(final IVectorFunction<IVector2> transformer) {
      if (transformer == null) {
         return this;
      }

      return new GSegment2D(transformer.apply(_from), transformer.apply(_to));
   }


   public GSegment2D getVerticalBisector() {
      final IVector2 center = getCentroid();
      final IVector2 from = new GVector2D(center.x(), _from.y());
      final IVector2 to = new GVector2D(center.x(), _to.y());

      return new GSegment2D(from, to);
   }


   public GSegment2D getHorizontalBisector() {
      final IVector2 center = getCentroid();
      final IVector2 from = new GVector2D(_from.x(), center.y());
      final IVector2 to = new GVector2D(_to.x(), center.y());

      return new GSegment2D(from, to);
   }


   public IVector2 getIntersectionPoint(final GSegment2D that) {

      final GLineIntersectionResult intersectionReult = getIntersection(that);

      if (intersectionReult.getType() == GLineIntersectionResult.Type.INTERSECTING) {
         return (IVector2) intersectionReult.getPoint();
      }

      return null;

      //      
      //      final GSegment2D segment1 = this;
      //      final GSegment2D segment2 = that;
      //
      //      final IVector2 begin = segment1._from;
      //      final IVector2 end = segment1._to;
      //      final IVector2 anotherBegin = segment2._from;
      //      final IVector2 anotherEnd = segment2._to;
      //
      //      final double denominator = ((anotherEnd.y() - anotherBegin.y()) * (end.x() - begin.x()))
      //                                 - ((anotherEnd.x() - anotherBegin.x()) * (end.y() - begin.y()));
      //
      //      final double numeratorA = ((anotherEnd.x() - anotherBegin.x()) * (begin.y() - anotherBegin.y()))
      //                                - ((anotherEnd.y() - anotherBegin.y()) * (begin.x() - anotherBegin.x()));
      //
      //      final double numeratorB = ((end.x() - begin.x()) * (begin.y() - anotherBegin.y()))
      //                                - ((end.y() - begin.y()) * (begin.x() - anotherBegin.x()));
      //
      //      if (denominator == 0.0) {
      //         //         if ((numeratorA == 0.0) && (numeratorB == 0.0)) {
      //         //            return null;
      //         //         }
      //
      //         return null;
      //      }
      //
      //      final double ua = numeratorA / denominator;
      //      final double ub = numeratorB / denominator;
      //
      //      if ((ua >= 0.0) && (ua <= 1.0) && (ub >= 0.0) && (ub <= 1.0)) {
      //         // Get the intersection point.
      //         final double x = begin.x() + ua * (end.x() - begin.x());
      //         final double y = begin.y() + ua * (end.y() - begin.y());
      //
      //         return new GVector2D(x, y);
      //      }
      //
      //      return null;
   }


   public GSegment2D translate(final IVector2 offset) {
      return new GSegment2D(_from.add(offset), _to.add(offset));
   }


   public static void main(final String[] args) {
      System.out.println("GSegment2D 0.1");
      System.out.println("----------------\n");


   }


}
