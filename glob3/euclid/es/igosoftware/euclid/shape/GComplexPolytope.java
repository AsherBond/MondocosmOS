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

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GComplexPolytope<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>,

PolytopeT extends ISimplePolytope<VectorT, SegmentT, BoundsT>

>
         extends
            GPolytopeAbstract<VectorT, SegmentT, BoundsT>
         implements
            IComplexPolytope<VectorT, SegmentT, BoundsT> {

   private static final long       serialVersionUID = 1L;


   protected final PolytopeT       _hull;
   protected final List<PolytopeT> _holes;


   public GComplexPolytope(final PolytopeT hull,
                           final List<? extends PolytopeT> holes) {
      GAssert.notNull(hull, "hull");
      GAssert.notEmpty(holes, "holes");
      GAssert.notNullElements(holes, "holes");

      _hull = hull;
      _holes = new ArrayList<PolytopeT>(holes);
      validate();
   }


   @Override
   public final byte dimensions() {
      return _hull.dimensions();
   }


   @Override
   public final double precision() {
      return _hull.precision();
   }


   protected void validate() {
      // TODO: check holes intersections
      // TODO: check holes are inside the hull
      if (_hull == null) {
         throw new IllegalArgumentException("hull can't be null");
      }

      for (final PolytopeT hole : _holes) {
         if (hole == null) {
            throw new IllegalArgumentException("hole can't be null " + _holes);
         }
      }

   }


   @Override
   public final List<VectorT> getPoints() {
      final ArrayList<VectorT> points = new ArrayList<VectorT>();
      points.addAll(_hull.getPoints());
      for (final PolytopeT hole : _holes) {
         points.addAll(hole.getPoints());
      }
      points.trimToSize();
      return Collections.unmodifiableList(points);
   }


   @Override
   public final VectorT getPoint(final int index) {
      return getPoints().get(index);
   }


   @Override
   public final int getPointsCount() {
      int count = _hull.getPointsCount();
      for (final PolytopeT hole : _holes) {
         // System.out.println("hole=" + hole);
         count += hole.getPointsCount();
      }
      return count;
   }


   @Override
   public final Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   @Override
   public final String toString() {
      return getStringName() + " (hull=" + _hull + ", holes=" + _holes + ")";
   }


   protected abstract String getStringName();


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_holes == null) ? 0 : _holes.hashCode());
      result = prime * result + ((_hull == null) ? 0 : _hull.hashCode());
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
      final GComplexPolytope<?, ?, ?, ?> other = (GComplexPolytope<?, ?, ?, ?>) obj;
      if (_holes == null) {
         if (other._holes != null) {
            return false;
         }
      }
      else if (!_holes.equals(other._holes)) {
         return false;
      }
      if (_hull == null) {
         if (other._hull != null) {
            return false;
         }
      }
      else if (!_hull.equals(other._hull)) {
         return false;
      }
      return true;
   }


   @Override
   public PolytopeT getHull() {
      return _hull;
   }


   public List<PolytopeT> getHoles() {
      return Collections.unmodifiableList(_holes);
   }


   @Override
   public VectorT getCentroid() {
      return _hull.getCentroid();
   }


}
