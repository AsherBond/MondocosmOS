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

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;


public interface IVector<

VectorT extends IVector<VectorT, BoundsT>,

BoundsT extends GAxisAlignedOrthotope<VectorT, BoundsT>

>
         extends
            IBoundedGeometry<VectorT, BoundsT>

{


   public VectorT absoluted();


   public VectorT add(final double delta);


   public VectorT add(final VectorT that);


   public double angle(final VectorT that);


   public VectorT asMutable();


   public String asParseableString();


   public boolean between(final VectorT min,
                          final VectorT max);


   public VectorT clamp(final VectorT lower,
                        final VectorT upper);


   public boolean closeTo(final VectorT that);


   public boolean closeTo(final VectorT that,
                          final double precision);


   public boolean closeToZero();


   public VectorT div(final double scale);


   public VectorT div(final VectorT that);


   public double dot(final VectorT that);


   public double get(final byte i);


   public double[] getCoordinates();


   public VectorT interpolatedTo(final VectorT that,
                                 final double alpha);


   public boolean isNormalized();


   //   public boolean isZero();


   public double length();


   public VectorT max(final VectorT that);


   public VectorT min(final VectorT that);


   public VectorT negated();


   public VectorT normalized();


   public VectorT reciprocal();


   public VectorT rounded();


   public VectorT scale(final double scale);


   public VectorT scale(final VectorT that);


   public double squaredLength();


   public VectorT sub(final double delta);


   public VectorT sub(final VectorT that);


   public VectorT transformedBy(final GMatrix33D matrix);


   public VectorT transformedBy(final GMatrix44D matrix);


   public IVector2 asVector2();


   public VectorT previousDown();


   public VectorT nextUp();


   public VectorT asDouble();


   public boolean greaterOrEquals(final VectorT that);


   public boolean lessOrEquals(final VectorT that);


   public VectorT reproject(final GProjection sourceProjection,
                            final GProjection targetProjection);


   public VectorT squared();


}
