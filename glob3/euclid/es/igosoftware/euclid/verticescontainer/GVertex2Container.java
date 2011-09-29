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


package es.igosoftware.euclid.verticescontainer;

import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector2;


public final class GVertex2Container
         extends
            GVertexContainerWithDefaultsAbstract<IVector2, GVertex2Container> {


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector2 referencePoint,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, withIntensities, 0, withColors, null, withNormals, null);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector2 referencePoint,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals,
                            final boolean withUserData) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, withIntensities, 0, withColors, null, withNormals, null,
           withUserData, 0);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector2 referencePoint,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector2 referencePoint,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal,
                            final boolean withUserData,
                            final long userData) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal, withUserData, userData);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals) {
      this(vectorPrecision, colorPrecision, projection, GVector2D.ZERO, withIntensities, 0, withColors, null, withNormals, null);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals,
                            final boolean withUserData) {
      this(vectorPrecision, colorPrecision, projection, GVector2D.ZERO, withIntensities, 0, withColors, null, withNormals, null,
           withUserData, 0);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, GVector2D.ZERO, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal,
                            final boolean withUserData,
                            final long userData) {
      this(vectorPrecision, colorPrecision, projection, GVector2D.ZERO, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal, withUserData, userData);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector2 referencePoint,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, defaultIntensity,
           withColors, defaultColor, withNormals, defaultNormal, false, 0);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, GVector2D.ZERO, initialCapacity, withIntensities, defaultIntensity,
           withColors, defaultColor, withNormals, defaultNormal, false, 0);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector2 referencePoint,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal,
                            final boolean withUserData,
                            final long defaultUserData) {
      super(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, defaultIntensity,
            withColors, defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);
   }


   public GVertex2Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector2 defaultNormal,
                            final boolean withUserData,
                            final long defaultUserData) {
      super(vectorPrecision, colorPrecision, projection, GVector2D.ZERO, initialCapacity, withIntensities, defaultIntensity,
            withColors, defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);
   }


   @Override
   public byte dimensions() {
      return 2;
   }


   @Override
   protected GVertexContainerWithDefaultsAbstract.VectorHandler<IVector2> initializePointsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector2HandlerF(_capacity);
         case DOUBLE:
            return new Vector2HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected GVertexContainerWithDefaultsAbstract.VectorHandler<IVector2> initializeNormalsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector2HandlerF(_capacity);
         case DOUBLE:
            return new Vector2HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected String getStringName() {
      return "GVertex2Container";
   }


   @Override
   public GVertex2Container newEmptyContainer(final int initialCapacity) {
      return newEmptyContainer(initialCapacity, _projection, GVector2D.ZERO);
   }


   @Override
   public GVertex2Container newEmptyContainer(final int initialCapacity,
                                              final IVector2 referencePoint) {
      return newEmptyContainer(initialCapacity, _projection, referencePoint);
   }


   @Override
   public GVertex2Container newEmptyContainer(final int initialCapacity,
                                              final GProjection projection) {
      return newEmptyContainer(initialCapacity, projection, GVector2D.ZERO);
   }


   @Override
   public GVertex2Container newEmptyContainer(final int initialCapacity,
                                              final GProjection projection,
                                              final IVector2 referencePoint) {

      return new GVertex2Container(_vectorPrecision, _colorPrecision, projection, referencePoint, initialCapacity,
               hasIntensities(), _defaultIntensity, hasColors(), _defaultColor, hasNormals(), _defaultNormal, hasUserData(),
               _defaultUserData);
   }

}
