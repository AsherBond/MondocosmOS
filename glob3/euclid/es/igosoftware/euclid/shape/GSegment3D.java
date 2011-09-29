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

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorFunction;


public final class GSegment3D
         extends
            GSegment<IVector3, GSegment3D, GAxisAlignedBox>
         implements
            IPolygonalChain3D {

   private static final long serialVersionUID = 1L;


   public GSegment3D(final IVector3 fromPoint,
                     final IVector3 toPoint) {
      super(fromPoint, toPoint);
   }


   @Override
   public GAxisAlignedBox getBounds() {
      return new GAxisAlignedBox(_from, _to);
   }


   public IVector3 getIntersection(final GPlane plane) {

      return plane.getIntersection(this);
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


   @Override
   public List<GSegment3D> getEdges() {
      return Collections.singletonList(this);
   }


   @Override
   public GSegment3D clone() {
      return this;
   }


   @Override
   public GSegment3D transform(final IVectorFunction<IVector3> transformer) {
      if (transformer == null) {
         return this;
      }

      return new GSegment3D(transformer.apply(_from), transformer.apply(_to));
   }


}
