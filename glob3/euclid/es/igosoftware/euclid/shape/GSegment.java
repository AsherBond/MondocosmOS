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
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.GEdgedGeometryAbstract;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public abstract class GSegment<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GEdgedGeometryAbstract<VectorT, SegmentT, BoundsT>
         implements
            IPolygonalChain<VectorT, SegmentT, BoundsT> {

   private static final long serialVersionUID = 1L;


   @SuppressWarnings("unchecked")
   public static <VectorT extends IVector<VectorT, ?>> GSegment<VectorT, ?, ?> create(final VectorT from,
                                                                                      final VectorT to) {
      GAssert.notNull(from, "from");
      GAssert.notNull(to, "to");

      if (from instanceof IVector3) {
         return (GSegment<VectorT, ?, ?>) new GSegment3D((IVector3) from, (IVector3) to);
      }
      else if (from instanceof IVector2) {
         return (GSegment<VectorT, ?, ?>) new GSegment2D((IVector2) from, (IVector2) to);
      }
      else {
         throw new IllegalArgumentException("Unsupported points type (" + from.getClass() + ")");
      }
   }


   public final VectorT _from;
   public final VectorT _to;


   public GSegment(final VectorT from,
                   final VectorT to) {
      GAssert.notNull(from, "from");
      GAssert.notNull(to, "to");

      _from = from;
      _to = to;
   }


   public double length() {
      return _from.distance(_to);
   }


   @Override
   @SuppressWarnings("unchecked")
   public final List<VectorT> getPoints() {
      return Collections.unmodifiableList(Arrays.asList((VectorT[]) new IVector[] {
                        _from,
                        _to
      }));
   }


   @Override
   public final int getPointsCount() {
      return 2;
   }


   @Override
   public VectorT getPoint(final int index) {
      if (index == 0) {
         return _from;
      }
      if (index == 1) {
         return _to;
      }
      throw new IndexOutOfBoundsException();
   }


   public VectorT getMiddle() {
      return _from.add(_to).div(2);
   }


   public VectorT getFrom() {
      return _from;
   }


   public VectorT getTo() {
      return _to;
   }


   @Override
   public final Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   @Override
   public final byte dimensions() {
      return _from.dimensions();
   }


   @Override
   public final double precision() {
      return _from.precision();
   }


   @Override
   public final String toString() {
      return "Segment [" + _from + " -> " + _to + "]";
   }


   @Override
   public final int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_from == null) ? 0 : _from.hashCode());
      result = prime * result + ((_to == null) ? 0 : _to.hashCode());
      return result;
   }


   @Override
   public final boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GSegment<?, ?, ?> other = (GSegment<?, ?, ?>) obj;
      if (_from == null) {
         if (other._from != null) {
            return false;
         }
      }
      else if (!_from.equals(other._from)) {
         return false;
      }
      if (_to == null) {
         if (other._to != null) {
            return false;
         }
      }
      else if (!_to.equals(other._to)) {
         return false;
      }
      return true;
   }


   public double getLength() {
      return _from.distance(_to);
   }


   @Override
   public boolean contains(final VectorT point) {
      return GMath.closeToZero(distance(point));
   }


   @Override
   public boolean containsOnBoundary(final VectorT point) {
      return contains(point);
   }


   @Override
   public double squaredDistance(final VectorT point) {
      // from Real-Time Collision Detection - Christer Ericson 
      //   page 130

      final VectorT toFrom = _to.sub(_from);
      final VectorT pointFrom = point.sub(_from);

      final double e = pointFrom.dot(toFrom);

      // Handle cases where point projects outside toFrom
      //      if (e <= 0) {
      if (GMath.negativeOrZero(e)) {
         return pointFrom.dot(pointFrom);
      }
      final double f = toFrom.dot(toFrom);
      //if (e >= f) {
      if (GMath.greaterOrEquals(e, f)) {
         final VectorT pointTo = point.sub(_to);
         return pointTo.dot(pointTo);
      }

      // Handle cases where c projects onto ab
      return pointFrom.dot(pointFrom) - e * e / f;
   }


   @Override
   public double squaredDistanceToBoundary(final VectorT point) {
      return squaredDistance(point);
   }


   @Override
   public double distanceToBoundary(final VectorT point) {
      return distance(point);
   }


   @Override
   public VectorT closestPoint(final VectorT point) {
      return closestPointOnBoundary(point);
   }


   @Override
   public VectorT closestPointOnBoundary(final VectorT point) {
      // from Real-Time Collision Detection - Christer Ericson 
      //   page 129

      final VectorT pointMinusFrom = point.sub(_from);
      final VectorT vectorAB = _to.sub(_from);
      // Project c onto ab, but deferring divide by Dot(ab, ab)
      double t = vectorAB.dot(pointMinusFrom);

      if (GMath.negativeOrZero(t)) {
         //      if (t <= 0) {
         //t = 0;
         return _from;
      }

      final double denom = vectorAB.dot(vectorAB); // Always nonnegative since denom = ||ab||∧2

      if (GMath.greaterOrEquals(t, denom)) {
         //      if (t >= denom) {
         // c projects outside the [a,b] interval, on the b side; clamp to b
         //t = 1;
         return _to;
      }

      // c projects inside the [a,b] interval; must do deferred divide now
      t = t / denom;
      return _from.add(vectorAB.scale(t));
   }


   @Override
   public VectorT getCentroid() {
      return _from.add(_to).div(2);
   }


   @Override
   public GSegment<VectorT, SegmentT, BoundsT> clone() {
      return this;
   }


   @Override
   public final List<SegmentT> initializeEdges() {
      throw new RuntimeException("Must not happens as subclasses implements getEdges without calling initializeEdges()");
   }


   @Override
   public final boolean isClosed() {
      return false;
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<VectorT, BoundsT> that) {
      if (that instanceof IPolygonalChain) {
         @SuppressWarnings("unchecked")
         final IPolygonalChain<VectorT, ?, BoundsT> thatSegment = (IPolygonalChain<VectorT, ?, BoundsT>) that;
         if (thatSegment.getPointsCount() == 2) {
            return _from.closeTo(thatSegment.getPoint(0)) && //
                   _to.closeTo(thatSegment.getPoint(1));
         }
      }
      return false;
   }


   public double perimeter() {
      return _from.distance(_to);
   }


   public double getMu(final VectorT point) {
      // from Real-Time Collision Detection - Christer Ericson 
      //   page 129

      final VectorT pointMinusFrom = point.sub(_from);
      final VectorT vectorAB = _to.sub(_from);

      final double t = vectorAB.dot(pointMinusFrom);
      if (GMath.negativeOrZero(t)) {
         return 0;
      }

      final double denom = vectorAB.dot(vectorAB);
      if (GMath.greaterOrEquals(t, denom)) {
         return 1;
      }

      return t / denom;
   }


}
