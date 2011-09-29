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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GSimplePolytope<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GPolytopeAbstract<VectorT, SegmentT, BoundsT> {


   private static final long  serialVersionUID = 1L;


   public final List<VectorT> _points;


   public GSimplePolytope(final boolean validate,
                          final VectorT... points) {
      GAssert.notEmpty(points, "points");
      GAssert.notNullElements(points, "points");

      _points = new ArrayList<VectorT>(points.length);
      for (final VectorT point : points) {
         _points.add(point);
      }
      if (validate) {
         validate();
      }
   }


   public GSimplePolytope(final boolean validate,
                          final List<VectorT> points) {
      GAssert.notEmpty(points, "points");
      GAssert.notNullElements(points, "points");

      _points = new ArrayList<VectorT>(points);
      if (validate) {
         validate();
      }
   }


   @Override
   public final byte dimensions() {
      return _points.get(0).dimensions();
   }


   @Override
   public final double precision() {
      return _points.get(0).precision();
   }


   protected void validate() {
      if (_points.size() < 3) {
         throw new IllegalArgumentException("A Polygon must have at least 3 points");
      }


      for (int i = 0; i < _points.size(); i++) {
         final VectorT current = _points.get(i);

         final int nextI = (i + 1) % _points.size();
         final VectorT next = _points.get(nextI);

         if (current.closeTo(next)) {
            //         if (current.equals(next)) {
            throw new IllegalArgumentException("Two consecutive points (#" + i + "/#" + nextI + ") can't be the same");
         }
      }


      if (isSelfIntersected()) {
         //throw new IllegalArgumentException("Can't create a self-intersected polygon " + this);
         throw new IllegalArgumentException("Can't create a self-intersected polygon");
      }

   }


   @Override
   public final List<VectorT> getPoints() {
      return Collections.unmodifiableList(_points);
   }


   @Override
   public final VectorT getPoint(final int index) {
      return _points.get(index);
   }


   @Override
   public final int getPointsCount() {
      return _points.size();
   }


   @Override
   public final Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   @Override
   public final int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_points == null) ? 0 : _points.hashCode());
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
      final GSimplePolytope<?, ?, ?> other = (GSimplePolytope<?, ?, ?>) obj;
      if (_points == null) {
         if (other._points != null) {
            return false;
         }
      }
      else if (!_points.equals(other._points)) {
         return false;
      }
      return true;
   }


   @Override
   public final String toString() {
      return getStringName() + " (" + _points.size() + " points) " + _points;
   }


   protected abstract String getStringName();


   //   @Override
   //   public VectorT getCentroid() {
   //      return GVectorUtils.getAverage(_points);
   //   }

   //   @Override
   //   public VectorT getCentroid() {
   //
   //   }


   @Override
   public boolean closeTo(final IBoundedGeometry<VectorT, BoundsT> that) {
      if (that instanceof ISimplePolytope) {
         @SuppressWarnings("unchecked")
         final ISimplePolytope<VectorT, ?, BoundsT> thatPolytope = (ISimplePolytope<VectorT, ?, BoundsT>) that;

         if (_points.size() != thatPolytope.getPointsCount()) {
            return false;
         }

         for (int i = 0; i < _points.size(); i++) {
            if (!_points.get(i).closeTo(thatPolytope.getPoint(i))) {
               return false;
            }
         }

         return true;
      }
      return false;
   }

}
