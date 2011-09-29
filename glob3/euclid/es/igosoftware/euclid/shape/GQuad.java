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

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GQuad<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GPolytopeAbstract<VectorT, SegmentT, BoundsT> {

   private static final long serialVersionUID = 1L;


   public final VectorT      _v0;
   public final VectorT      _v1;
   public final VectorT      _v2;
   public final VectorT      _v3;


   public GQuad(final VectorT v0,
                final VectorT v1,
                final VectorT v2,
                final VectorT v3) {
      GAssert.notNull(v0, "v0");
      GAssert.notNull(v1, "v1");
      GAssert.notNull(v2, "v2");
      GAssert.notNull(v3, "v3");

      _v0 = v0;
      _v1 = v1;
      _v2 = v2;
      _v3 = v3;
   }


   @Override
   public final byte dimensions() {
      return _v0.dimensions();
   }


   @Override
   public final double precision() {
      return _v0.precision();
   }


   @Override
   @SuppressWarnings("unchecked")
   public final List<VectorT> getPoints() {
      return Collections.unmodifiableList(Arrays.asList((VectorT[]) new IVector[] {
                        _v0,
                        _v1,
                        _v2,
                        _v3
      }));
   }


   @Override
   public final int getPointsCount() {
      return 4;
   }


   @Override
   public VectorT getPoint(final int index) {
      switch (index) {
         case 0:
            return _v0;
         case 1:
            return _v1;
         case 2:
            return _v2;
         case 3:
            return _v3;
         default:
            throw new IndexOutOfBoundsException();
      }
   }


   @Override
   public final Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   @Override
   public final String toString() {
      return "Quad (" + _v0 + " " + _v1 + " " + _v2 + " " + _v3 + ")";
   }


   @Override
   protected abstract List<SegmentT> initializeEdges();


   @Override
   public double squaredDistance(final VectorT point) {
      if (contains(point)) {
         return 0;
      }

      double min = Double.POSITIVE_INFINITY;

      for (final SegmentT edge : getEdges()) {
         final double current = edge.squaredDistance(point);
         if (current < min) {
            min = current;
         }
      }

      return min;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_v0 == null) ? 0 : _v0.hashCode());
      result = prime * result + ((_v1 == null) ? 0 : _v1.hashCode());
      result = prime * result + ((_v2 == null) ? 0 : _v2.hashCode());
      result = prime * result + ((_v3 == null) ? 0 : _v3.hashCode());
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
      final GQuad<?, ?, ?> other = (GQuad<?, ?, ?>) obj;
      if (_v0 == null) {
         if (other._v0 != null) {
            return false;
         }
      }
      else if (!_v0.equals(other._v0)) {
         return false;
      }
      if (_v1 == null) {
         if (other._v1 != null) {
            return false;
         }
      }
      else if (!_v1.equals(other._v1)) {
         return false;
      }
      if (_v2 == null) {
         if (other._v2 != null) {
            return false;
         }
      }
      else if (!_v2.equals(other._v2)) {
         return false;
      }
      if (_v3 == null) {
         if (other._v3 != null) {
            return false;
         }
      }
      else if (!_v3.equals(other._v3)) {
         return false;
      }
      return true;
   }


   public abstract boolean isConvex();


   @SuppressWarnings("unchecked")
   @Override
   public VectorT getCentroid() {
      return GVectorUtils.getAverage(_v0, _v1, _v2, _v3);
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<VectorT, BoundsT> that) {
      if (that instanceof ISimplePolytope) {
         @SuppressWarnings("unchecked")
         final ISimplePolytope<VectorT, ?, BoundsT> thatQuad = (ISimplePolytope<VectorT, ?, BoundsT>) that;
         if (thatQuad.getPointsCount() == 4) {
            return _v0.closeTo(thatQuad.getPoint(0)) && //
                   _v1.closeTo(thatQuad.getPoint(1)) && //
                   _v2.closeTo(thatQuad.getPoint(2)) && //
                   _v3.closeTo(thatQuad.getPoint(3));
         }
      }
      return false;
   }


   public double perimeter() {
      return _v0.distance(_v1) + _v1.distance(_v2) + _v2.distance(_v3) + _v3.distance(_v0);
   }

}
