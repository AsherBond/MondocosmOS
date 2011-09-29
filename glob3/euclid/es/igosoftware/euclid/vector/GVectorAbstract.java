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


package es.igosoftware.euclid.vector;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.util.GMath;


public abstract class GVectorAbstract<

VectorT extends IVector<VectorT, BoundsT>,

BoundsT extends GAxisAlignedOrthotope<VectorT, BoundsT>

>
         extends
            GGeometryAbstract<VectorT>
         implements
            IVector<VectorT, BoundsT> {

   private static final long serialVersionUID = 1L;


   @Override
   public boolean isNormalized() {
      return (GMath.closeTo(squaredLength(), 1));
   }


   @Override
   public double length() {
      return GMath.sqrt(squaredLength());
   }


   @Override
   public final boolean contains(final VectorT point) {
      return closeTo(point);
   }


   @Override
   public final double angle(final VectorT that) {
      final double normProduct = length() * that.length();
      if (GMath.closeToZero(normProduct)) {
         throw new RuntimeException("the product of the length() of the vectors is zero");
      }

      final double dot = dot(that) / normProduct;
      final double campledDot = GMath.clamp(dot, -1, 1);
      return Math.acos(campledDot);
   }


   @SuppressWarnings("unchecked")
   @Override
   public final VectorT closestPoint(final VectorT point) {
      return (VectorT) this;
   }


   @Override
   public final VectorT clamp(final VectorT min,
                              final VectorT max) {
      return max(min).min(max);
   }


   @Override
   public abstract boolean equals(final Object that);


   @Override
   public abstract int hashCode();


   @SuppressWarnings("unchecked")
   @Override
   public VectorT getCentroid() {
      return (VectorT) this;
   }


   @SuppressWarnings("unchecked")
   @Override
   public VectorT closestPointOnBoundary(final VectorT point) {
      return (VectorT) this;
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
   public final boolean containsOnBoundary(final VectorT point) {
      return closeTo(point);
   }


   @SuppressWarnings("unchecked")
   @Override
   public boolean closeTo(final IBoundedGeometry<VectorT, BoundsT> that) {
      if (that instanceof IVector) {
         return closeTo((VectorT) that);
      }
      return false;
   }


}
