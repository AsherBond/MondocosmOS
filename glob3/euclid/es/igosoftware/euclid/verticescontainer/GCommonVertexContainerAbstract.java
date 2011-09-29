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

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.utils.GBufferUtils;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;


public abstract class GCommonVertexContainerAbstract<

VectorT extends IVector<VectorT, ?>,

VertexT extends IVertexContainer.Vertex<VectorT>,

MutableT extends GCommonVertexContainerAbstract<VectorT, VertexT, MutableT>

>
         extends
            GVertexContainerAbstract<VectorT, VertexT, MutableT> {


   //----------------------------------------------------------------------------
   // Strategy-pattern classes to handle int/float colors
   protected static interface ColorHandler {
      public IColor getColor(final int index);


      public void growBuffer(final int newCapacity);


      public void putColor(final int index,
                           final IColor color);


      public void shrinkBuffer(final int size);

   }


   protected static final class ColorHandlerF
            implements
               ColorHandler {
      private static final int ELEMENT_SIZE = 3;

      private FloatBuffer      _buffer;


      protected ColorHandlerF(final int initialCapacity) {
         _buffer = GBufferUtils.createFloatBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IColor getColor(final int index) {
         return GBufferUtils.getColor(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putColor(final int index,
                           final IColor color) {
         GBufferUtils.putColor(_buffer, index, color);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   protected static final class ColorHandlerI
            implements
               ColorHandler {
      private static final int ELEMENT_SIZE = 1;

      private IntBuffer        _buffer;


      protected ColorHandlerI(final int initialCapacity) {
         _buffer = GBufferUtils.createIntBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IColor getColor(final int index) {
         return GBufferUtils.getColor(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putColor(final int index,
                           final IColor color) {
         GBufferUtils.putColor(_buffer, index, color);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   //----------------------------------------------------------------------------
   // Strategy-pattern classes to handle 2d/3d float/double vectors
   protected static interface VectorHandler<VectorT extends IVector<VectorT, ?>> {
      public VectorT getVector(final int index);


      public void growBuffer(final int newCapacity);


      public void putVector(final int index,
                            final VectorT vector);


      public void shrinkBuffer(final int size);
   }

   //----------------------------------------------------------------------------
   // Strategy-pattern classes to handle float/double vectors
   protected static final class Vector3HandlerD
            implements
               VectorHandler<IVector3> {
      private static final int ELEMENT_SIZE = 3;

      private DoubleBuffer     _buffer;


      protected Vector3HandlerD(final int initialCapacity) {
         _buffer = GBufferUtils.createDoubleBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVector3 getVector(final int index) {
         return GBufferUtils.getVector3(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putVector(final int index,
                            final IVector3 vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   protected static final class Vector3HandlerF
            implements
               VectorHandler<IVector3> {
      private static final int ELEMENT_SIZE = 3;
      private FloatBuffer      _buffer;


      protected Vector3HandlerF(final int initialCapacity) {
         _buffer = GBufferUtils.createFloatBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVector3 getVector(final int index) {
         return GBufferUtils.getVector3(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putVector(final int index,
                            final IVector3 vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }

   }

   //----------------------------------------------------------------------------
   // Strategy-pattern classes to handle float/double vectors
   protected static final class Vector2HandlerD
            implements
               VectorHandler<IVector2> {
      private static final int ELEMENT_SIZE = 2;

      private DoubleBuffer     _buffer;


      protected Vector2HandlerD(final int initialCapacity) {
         _buffer = GBufferUtils.createDoubleBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVector2 getVector(final int index) {
         return GBufferUtils.getVector2(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putVector(final int index,
                            final IVector2 vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   protected static final class Vector2HandlerF
            implements
               VectorHandler<IVector2> {
      private static final int ELEMENT_SIZE = 2;

      private FloatBuffer      _buffer;


      protected Vector2HandlerF(final int initialCapacity) {
         _buffer = GBufferUtils.createFloatBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVector2 getVector(final int index) {
         return GBufferUtils.getVector2(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putVector(final int index,
                            final IVector2 vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   protected final GVectorPrecision       _vectorPrecision;
   protected final GColorPrecision        _colorPrecision;
   protected GProjection                  _projection;

   //protected final VectorT                _referencePoint;

   protected final float                  _defaultIntensity;
   protected final VectorT                _defaultNormal;
   protected final IColor                 _defaultColor;
   protected final long                   _defaultUserData;

   protected final VectorHandler<VectorT> _pointsHandler;
   protected FloatBuffer                  _intensitiesBuffer;
   protected final VectorHandler<VectorT> _normalsHandler;
   protected final ColorHandler           _colorsHandler;
   protected LongBuffer                   _userDataBuffer;

   protected int                          _size;
   protected int                          _capacity;


   protected GCommonVertexContainerAbstract(final GVectorPrecision vectorPrecision,
                                            final GColorPrecision colorPrecision,
                                            final GProjection projection,
                                            final int initialCapacity,
                                            final boolean withIntensities,
                                            final float defaultIntensity,
                                            final boolean withColors,
                                            final IColor defaultColor,
                                            final boolean withNormals,
                                            final VectorT defaultNormal,
                                            final boolean withUserData,
                                            final long defaultUserData) {
      if (defaultNormal != null) {
         GAssert.isTrue(defaultNormal.isNormalized(), "defaultNormal isNormalized");
      }

      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
      }

      _size = 0;
      _capacity = initialCapacity;

      // _referencePoint = referencePoint;

      _vectorPrecision = vectorPrecision;
      _colorPrecision = colorPrecision;
      _projection = projection;

      _defaultIntensity = defaultIntensity;
      _defaultColor = defaultColor;
      _defaultNormal = defaultNormal;

      _defaultUserData = defaultUserData;

      _pointsHandler = initializePointsHandler();

      _intensitiesBuffer = withIntensities ? GBufferUtils.createFloatBuffer(initialCapacity, false) : null;
      _colorsHandler = withColors ? initializeColorsHandler() : null;
      _normalsHandler = withNormals ? initializeNormalsHandler() : null;
      _userDataBuffer = withUserData ? GBufferUtils.createLongBuffer(initialCapacity, false) : null;
   }


   protected final ColorHandler initializeColorsHandler() {
      switch (_colorPrecision) {
         case INT:
            return new ColorHandlerI(_capacity);
         case FLOAT3:
            return new ColorHandlerF(_capacity);
         default:
            throw new IllegalArgumentException("Invalid color precision");
      }
   }


   protected abstract VectorHandler<VectorT> initializeNormalsHandler();


   protected abstract VectorHandler<VectorT> initializePointsHandler();


   protected void rangeCheck(final int index) {
      if (index >= _size) {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + _size);
      }
   }


   @Override
   public final GColorPrecision colorPrecision() {
      return _colorPrecision;
   }


   @Override
   public final GProjection projection() {
      return _projection;
   }


   protected final int ensureMinCapacity(final int minCapacity) {

      if (minCapacity <= _capacity) {
         return _capacity;
      }

      int newCapacity = (_capacity * 3) / 2 + 1;

      if (newCapacity < minCapacity) {
         newCapacity = minCapacity;
      }
      //System.out.println("growing from " + _capacity + " to " + newCapacity);

      _pointsHandler.growBuffer(newCapacity);
      _intensitiesBuffer = GBufferUtils.growBuffer(newCapacity, _intensitiesBuffer);

      if (_normalsHandler != null) {
         _normalsHandler.growBuffer(newCapacity);
      }
      if (_colorsHandler != null) {
         _colorsHandler.growBuffer(newCapacity);
      }

      _userDataBuffer = GBufferUtils.growBuffer(newCapacity, _userDataBuffer);

      //      _capacity = newCapacity;
      //
      //      return _capacity;

      return newCapacity;
   }


   @Override
   public final IColor getColor(final int index) {
      rangeCheck(index);
      if (_colorsHandler != null) {
         return _colorsHandler.getColor(index);
      }
      return _defaultColor;
   }


   @Override
   public final float getIntensity(final int index) {
      rangeCheck(index);
      if (_intensitiesBuffer != null) {
         return _intensitiesBuffer.get(index);
      }
      return _defaultIntensity;
   }


   @Override
   public final long getUserData(final int index) {
      rangeCheck(index);
      if (_userDataBuffer != null) {
         return _userDataBuffer.get(index);
      }
      return _defaultUserData;
   }


   @Override
   public final double getUserDataAsDouble(final int index) {
      rangeCheck(index);
      if (_userDataBuffer != null) {
         return Double.longBitsToDouble(_userDataBuffer.get(index));
      }
      return Double.longBitsToDouble(_defaultUserData);
   }


   @Override
   public final VectorT getNormal(final int index) {
      rangeCheck(index);
      if (_normalsHandler != null) {
         return _normalsHandler.getVector(index);
      }
      return _defaultNormal;
   }


   @Override
   public final void setColor(final int index,
                              final IColor color) {
      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      if (_colorsHandler != null) {
         _colorsHandler.putColor(index, color);
      }
      else {
         throw new RuntimeException("Invalid data setting for this container");
      }

      changed();
   }


   @Override
   public final void setIntensity(final int index,
                                  final float intensity) {
      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      if (_intensitiesBuffer != null) {
         _intensitiesBuffer.put(index, intensity);
      }
      else {
         throw new RuntimeException("Invalid data setting for this container");
      }

      changed();

   }


   @Override
   public final void setNormal(final int index,
                               final VectorT normal) {
      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      if (_normalsHandler != null) {
         GAssert.isTrue(normal.isNormalized(), "normal isNormalized");
         _normalsHandler.putVector(index, normal);
      }
      else {
         throw new RuntimeException("Invalid data setting for this container");
      }

      changed();
   }


   //   @Override
   //   public final void setPoint(final int index,
   //                              final VectorT point) {
   //      rangeCheck(index);
   //
   //      if (!isMutable()) {
   //         throw new RuntimeException("The container is closed, can't modify any point");
   //      }
   //
   //      _pointsHandler.putVector(index, point.sub(_referencePoint));
   //
   //      changed();
   //
   //   }


   @Override
   public final void setUserData(final int index,
                                 final long userData) {
      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      if (_userDataBuffer != null) {
         _userDataBuffer.put(index, userData);
      }
      else {
         throw new RuntimeException("Invalid data setting for this container");
      }

      changed();
   }


   @Override
   public final void setUserDataFromDouble(final int index,
                                           final double userData) {

      setUserData(index, Double.doubleToRawLongBits(userData));
   }


   //   @Override
   //   public void setProjection(final GProjection projection) {
   //      _projection = projection;
   //   }


   @Override
   public final boolean hasColors() {
      return (_colorsHandler != null);
   }


   @Override
   public final boolean hasIntensities() {
      return (_intensitiesBuffer != null);
   }


   @Override
   public final boolean hasUserData() {
      return (_userDataBuffer != null);
   }


   @Override
   public final boolean hasNormals() {
      return (_normalsHandler != null);
   }


   @Override
   public final int size() {
      return _size;
   }


   @Override
   public final GVectorPrecision vectorPrecision() {
      return _vectorPrecision;
   }


   ////////////////////////////////////////////////////////////////////////////////////

   protected int addBasicPointData(final float intensity,
                                   final VectorT normal,
                                   final IColor color,
                                   final long userData) {

      //      if (!isMutable()) {
      //         throw new RuntimeException("The container is closed, can't add more points");
      //      }

      ensureCapacity(_size + 1);

      final int position = _size;

      //_pointsHandler.putVector(position, point.sub(_referencePoint));

      if (_intensitiesBuffer != null) {
         _intensitiesBuffer.put(position, intensity);
      }

      if (_normalsHandler != null) {
         GAssert.isTrue(normal.isNormalized(), "normal isNormalized");
         _normalsHandler.putVector(position, normal);
      }

      if (_colorsHandler != null) {
         _colorsHandler.putColor(position, color);
      }

      if (_userDataBuffer != null) {
         _userDataBuffer.put(position, userData);
      }

      _size++;

      //changed();

      return position;
   }


   protected void setBasicPointData(final int index,
                                    final float intensity,
                                    final VectorT normal,
                                    final IColor color,
                                    final long userData) {
      //      rangeCheck(index);
      //
      //      if (!isMutable()) {
      //         throw new RuntimeException("The container is closed, can't modify any point");
      //      }

      //_pointsHandler.putVector(index, point.sub(_referencePoint));

      if (_intensitiesBuffer != null) {
         _intensitiesBuffer.put(index, intensity);
      }

      if (_normalsHandler != null) {
         GAssert.isTrue(normal.isNormalized(), "normal isNormalized");
         _normalsHandler.putVector(index, normal);
      }

      if (_colorsHandler != null) {
         _colorsHandler.putColor(index, color);
      }

      if (_userDataBuffer != null) {
         _userDataBuffer.put(index, userData);
      }
   }


   protected boolean basicEquals(final Object obj) {

      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      final GVertexContainerWithDefaultsAbstract<?, ?> other = (GVertexContainerWithDefaultsAbstract<?, ?>) obj;

      if (_size != other._size) {
         return false;
      }

      if (_vectorPrecision != other._vectorPrecision) {
         return false;
      }

      if (_colorPrecision != other._colorPrecision) {
         return false;
      }


      if (_defaultColor == null) {
         if (other._defaultColor != null) {
            return false;
         }
      }
      else if (!_defaultColor.equals(other._defaultColor)) {
         return false;
      }

      if (Float.floatToIntBits(_defaultIntensity) != Float.floatToIntBits(other._defaultIntensity)) {
         return false;
      }

      if (_defaultUserData != other._defaultUserData) {
         return false;
      }

      if (_defaultNormal == null) {
         if (other._defaultNormal != null) {
            return false;
         }
      }
      else if (!_defaultNormal.equals(other._defaultNormal)) {
         return false;
      }


      if (_colorsHandler == null) {
         if (other._colorsHandler != null) {
            return false;
         }
      }
      else if (!_colorsHandler.equals(other._colorsHandler)) {
         return false;
      }


      if (_intensitiesBuffer == null) {
         if (other._intensitiesBuffer != null) {
            return false;
         }
      }
      else if (!_intensitiesBuffer.equals(other._intensitiesBuffer)) {
         return false;
      }

      if (_userDataBuffer == null) {
         if (other._userDataBuffer != null) {
            return false;
         }
      }
      else if (!_userDataBuffer.equals(other._userDataBuffer)) {
         return false;
      }

      if (_normalsHandler == null) {
         if (other._normalsHandler != null) {
            return false;
         }
      }
      else if (!_normalsHandler.equals(other._normalsHandler)) {
         return false;
      }

      if (_pointsHandler == null) {
         if (other._pointsHandler != null) {
            return false;
         }
      }
      else if (!_pointsHandler.equals(other._pointsHandler)) {
         return false;
      }

      return true;
   }


   protected int basicHashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_colorPrecision == null) ? 0 : _colorPrecision.hashCode());
      result = prime * result + ((_colorsHandler == null) ? 0 : _colorsHandler.hashCode());
      result = prime * result + ((_defaultColor == null) ? 0 : _defaultColor.hashCode());
      result = prime * result + Float.floatToIntBits(_defaultIntensity);
      result = prime * result + ((_defaultNormal == null) ? 0 : _defaultNormal.hashCode());
      result = prime * result + ((_intensitiesBuffer == null) ? 0 : _intensitiesBuffer.hashCode());
      result = prime * result + ((_normalsHandler == null) ? 0 : _normalsHandler.hashCode());
      result = prime * result + ((_pointsHandler == null) ? 0 : _pointsHandler.hashCode());
      result = prime * result + _size;
      result = prime * result + ((_vectorPrecision == null) ? 0 : _vectorPrecision.hashCode());
      result = prime * result + (int) _defaultUserData;
      result = prime * result + ((_userDataBuffer == null) ? 0 : _userDataBuffer.hashCode());
      return result;
   }


   protected void basicTrimToSize() {

      if (_capacity == _size) {
         return;
      }

      _pointsHandler.shrinkBuffer(_size);
      _intensitiesBuffer = GBufferUtils.shrinkBuffer(_size, _intensitiesBuffer);
      _userDataBuffer = GBufferUtils.shrinkBuffer(_size, _userDataBuffer);
      if (_normalsHandler != null) {
         _normalsHandler.shrinkBuffer(_size);
      }
      if (_colorsHandler != null) {
         _colorsHandler.shrinkBuffer(_size);
      }

      //      _capacity = _size;

   }


}
