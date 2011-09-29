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

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.utils.GBufferUtils;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorI2;


public final class GStructuredPtxVertex3Container
         extends
            GStructuredVertexContainerWithDefaultsAbstract<IVector3, GPtx3Group, GStructuredPtxVertex3Container> {

   //----------------------------------------------------------------------------
   // Strategy-pattern classes to handle short/int vectors
   private static final class RowColumnHandlerS
            implements
               RowColumnHandler {

      private static final int ELEMENT_SIZE = 2;

      private ShortBuffer      _buffer;


      private RowColumnHandlerS(final int initialCapacity) {
         _buffer = GBufferUtils.createShortBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVectorI2 getRowColumn(final int index) {
         return GBufferUtils.getVector(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putRowColumn(final int index,
                               final IVectorI2 vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   private static final class RowColumnHandlerI
            implements
               RowColumnHandler {

      private static final int ELEMENT_SIZE = 2;

      private IntBuffer        _buffer;


      private RowColumnHandlerI(final int initialCapacity) {
         _buffer = GBufferUtils.createIntBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVectorI2 getRowColumn(final int index) {
         return GBufferUtils.getVector(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putRowColumn(final int index,
                               final IVectorI2 vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final int initialCapacity,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, initialCapacity, withIntensities, 0, withColors, null, withNormals, null,
           withUserData, 0, storeAsRawData, rowColumnPrecision, withRowColumn, defaultRowColumn, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, defaultRowColumn, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, defaultRowColumn, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, GVector2I.ZERO, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, GVector2I.ZERO, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, defaultRowColumn, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, defaultRowColumn, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, GVector2I.ZERO, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, GVector2I.ZERO, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final float defaultIntensity,
                                         final boolean withColors,
                                         final IColor defaultColor,
                                         final boolean withNormals,
                                         final IVector3 defaultNormal,
                                         final boolean withUserData,
                                         final long defaultUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      super(vectorPrecision, colorPrecision, projection, 3, withIntensities, defaultIntensity, withColors, defaultColor,
            withNormals, defaultNormal, withUserData, defaultUserData, storeAsRawData, rowColumnPrecision, withRowColumn,
            defaultRowColumn, defaultGroup);

   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final int initialCapacity,
                                         final boolean withIntensities,
                                         final float defaultIntensity,
                                         final boolean withColors,
                                         final IColor defaultColor,
                                         final boolean withNormals,
                                         final IVector3 defaultNormal,
                                         final boolean withUserData,
                                         final long defaultUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      super(vectorPrecision, colorPrecision, projection, initialCapacity, withIntensities, defaultIntensity, withColors,
            defaultColor, withNormals, defaultNormal, withUserData, defaultUserData, storeAsRawData, rowColumnPrecision,
            withRowColumn, defaultRowColumn, defaultGroup);

   }


   @Override
   protected GPtx3Group initializeDefaultGroup() {

      return new GPtx3Group(_vectorPrecision, _colorPrecision, _projection, GVector3D.ZERO, 3, hasIntensities(),
               _defaultIntensity, hasColors(), _defaultColor, hasNormals(), _defaultNormal, hasUserData(), _defaultUserData, 0,
               0, GVector3D.ZERO, GMatrix33D.IDENTITY, GMatrix44D.IDENTITY);
   }


   @Override
   protected GStructuredVertexContainerWithDefaultsAbstract.RowColumnHandler initializeRowColumnHandler() {
      switch (_rowColumnPrecision) {
         case SHORT:
            return new RowColumnHandlerS(_capacity);
         case INT:
            return new RowColumnHandlerI(_capacity);
         default:
            throw new IllegalArgumentException("Invalid rowColumn precision");
      }
   }


   @Override
   protected GCommonVertexContainerAbstract.VectorHandler<IVector3> initializeNormalsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector3HandlerF(_capacity);
         case DOUBLE:
            return new Vector3HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected GCommonVertexContainerAbstract.VectorHandler<IVector3> initializePointsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector3HandlerF(_capacity);
         case DOUBLE:
            return new Vector3HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected String getStringName() {
      return "GStructuredPtxVertex3Container";
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity) {

      return newEmptyContainer(initialCapacity, _projection, storageAsRawData());
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity,
                                                           final GProjection projection) {

      return newEmptyContainer(initialCapacity, projection, storageAsRawData());
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity,
                                                           final boolean storageAsRawData) {

      return newEmptyContainer(initialCapacity, _projection, storageAsRawData);
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity,
                                                           final GProjection projection,
                                                           final boolean storageAsRawData) {

      return new GStructuredPtxVertex3Container(_vectorPrecision, _colorPrecision, projection, initialCapacity, hasIntensities(),
               _defaultIntensity, hasColors(), _defaultColor, hasNormals(), _defaultNormal, hasUserData(), _defaultUserData,
               storageAsRawData, _rowColumnPrecision, hasRowColumn(), _defaultRowColumn, null);
   }


   @Override
   public byte dimensions() {
      return 3;
   }


}
