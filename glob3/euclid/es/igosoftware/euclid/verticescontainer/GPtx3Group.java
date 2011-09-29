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
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GArrayListInt;
import es.igosoftware.util.IListInt;


public final class GPtx3Group
         extends
            GVertexContainerWithDefaultsAbstract<IVector3, GPtx3Group>
         implements
            IStructuredVertexContainer.IVertexGroup<IVector3, IVertexContainer.Vertex<IVector3>, GPtx3Group> {


   private final int           _rowCount;
   private final int           _columnsCount;
   private final IVector3      _translationVector;
   private final GMatrix33D    _rotationMatrix;
   private final GMatrix33D    _inverseRotationMatrix;
   private final GMatrix44D    _transformationMatrix;
   private final GMatrix44D    _inverseTransformationMatrix;
   private final GArrayListInt _indexList = new GArrayListInt();


   public GPtx3Group(final GVectorPrecision vectorPrecision,
                     final GColorPrecision colorPrecision,
                     final GProjection projection,
                     final IVector3 referencePoint,
                     final int initialCapacity,
                     final boolean withIntensities,
                     final boolean withColors,
                     final boolean withNormals,
                     final boolean withUserData,
                     final int rowCount,
                     final int columnCount,
                     final IVector3 translationVector,
                     final GMatrix33D rotationMatrix,
                     final GMatrix44D transformationMatrix) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, 0, withColors, null,
           withNormals, null, withUserData, 0, rowCount, columnCount, translationVector, rotationMatrix, transformationMatrix);
   }


   public GPtx3Group(final GVectorPrecision vectorPrecision,
                     final GColorPrecision colorPrecision,
                     final GProjection projection,
                     final int initialCapacity,
                     final boolean withIntensities,
                     final boolean withColors,
                     final boolean withNormals,
                     final boolean withUserData,
                     final int rowCount,
                     final int columnCount,
                     final IVector3 translationVector,
                     final GMatrix33D rotationMatrix,
                     final GMatrix44D transformationMatrix) {
      this(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, initialCapacity, withIntensities, 0, withColors, null,
           withNormals, null, withUserData, 0, rowCount, columnCount, translationVector, rotationMatrix, transformationMatrix);
   }


   public GPtx3Group(final GVectorPrecision vectorPrecision,
                     final GColorPrecision colorPrecision,
                     final GProjection projection,
                     final boolean withIntensities,
                     final boolean withColors,
                     final boolean withNormals,
                     final boolean withUserData,
                     final int rowCount,
                     final int columnCount,
                     final IVector3 translationVector,
                     final GMatrix33D rotationMatrix,
                     final GMatrix44D transformationMatrix) {
      this(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, 3, withIntensities, 0, withColors, null, withNormals,
           null, withUserData, 0, rowCount, columnCount, translationVector, rotationMatrix, transformationMatrix);
   }


   public GPtx3Group(final GVectorPrecision vectorPrecision,
                     final GColorPrecision colorPrecision,
                     final GProjection projection,
                     final IVector3 referencePoint,
                     final int initialCapacity,
                     final boolean withIntensities,
                     final float defaultIntensity,
                     final boolean withColors,
                     final IColor defaultColor,
                     final boolean withNormals,
                     final IVector3 defaultNormal,
                     final boolean withUserData,
                     final long defaultUserData,
                     final int rowCount,
                     final int columnCount,
                     final IVector3 translationVector,
                     final GMatrix33D rotationMatrix,
                     final GMatrix44D transformationMatrix) {
      super(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, defaultIntensity,
            withColors, defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);
      _rowCount = rowCount;
      _columnsCount = columnCount;
      _translationVector = translationVector;
      _rotationMatrix = rotationMatrix;
      _transformationMatrix = transformationMatrix;
      _inverseRotationMatrix = _rotationMatrix.inverted();
      _inverseTransformationMatrix = _transformationMatrix.inverted();
      _indexList.ensureCapacity(_capacity);
   }


   @Override
   protected GVertexContainerWithDefaultsAbstract.VectorHandler<IVector3> initializePointsHandler() {
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
   protected GVertexContainerWithDefaultsAbstract.VectorHandler<IVector3> initializeNormalsHandler() {
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
      return "GPtx3Group";
   }


   @Override
   public GPtx3Group newEmptyContainer(final int initialCapacity) {
      return newEmptyContainer(initialCapacity, _projection, GVector3D.ZERO);
   }


   @Override
   public GPtx3Group newEmptyContainer(final int initialCapacity,
                                       final IVector3 referencePoint) {
      return newEmptyContainer(initialCapacity, _projection, referencePoint);
   }


   @Override
   public GPtx3Group newEmptyContainer(final int initialCapacity,
                                       final GProjection projection) {
      return newEmptyContainer(initialCapacity, projection, GVector3D.ZERO);
   }


   @Override
   public GPtx3Group newEmptyContainer(final int initialCapacity,
                                       final GProjection projection,
                                       final IVector3 referencePoint) {
      return new GPtx3Group(_vectorPrecision, _colorPrecision, projection, referencePoint, initialCapacity, hasIntensities(),
               _defaultIntensity, hasColors(), _defaultColor, hasNormals(), _defaultNormal, hasUserData(), _defaultUserData,
               _rowCount, _columnsCount, _translationVector, _rotationMatrix, _transformationMatrix);

   }


   @Override
   public byte dimensions() {
      return 3;
   }


   @Override
   public IVector3 translate(final IVector3 point) {

      if (_translationVector == null) {
         return point;
      }

      return point.add(_translationVector);

   }


   @Override
   public IVector3 inverseTranslate(final IVector3 point) {

      if (_translationVector == null) {
         return point;
      }

      return point.sub(_translationVector);

   }


   @Override
   public IVector3 rotate(final IVector3 point) {

      if (_rotationMatrix == null) {
         return point;
      }

      final double vx = point.x() * _rotationMatrix._m00 + point.y() * _rotationMatrix._m10 + point.z() * _rotationMatrix._m20;
      final double vy = point.x() * _rotationMatrix._m01 + point.y() * _rotationMatrix._m11 + point.z() * _rotationMatrix._m21;
      final double vz = point.x() * _rotationMatrix._m02 + point.y() * _rotationMatrix._m12 + point.z() * _rotationMatrix._m22;

      return new GVector3D(vx, vy, vz);
   }


   @Override
   public IVector3 inverseRotate(final IVector3 point) {

      if (_inverseRotationMatrix == null) {
         return point;
      }

      final double vx = point.x() * _inverseRotationMatrix._m00 + point.y() * _inverseRotationMatrix._m10 + point.z()
                        * _inverseRotationMatrix._m20;
      final double vy = point.x() * _inverseRotationMatrix._m01 + point.y() * _inverseRotationMatrix._m11 + point.z()
                        * _inverseRotationMatrix._m21;
      final double vz = point.x() * _inverseRotationMatrix._m02 + point.y() * _inverseRotationMatrix._m12 + point.z()
                        * _inverseRotationMatrix._m22;

      return new GVector3D(vx, vy, vz);
   }


   @Override
   public IVector3 transform(final IVector3 point) {

      if (_transformationMatrix == null) {
         return point;
      }

      final double vx = point.x() * _transformationMatrix._m00 + point.y() * _transformationMatrix._m10 + point.z()
                        * _transformationMatrix._m20 + _transformationMatrix._m30;
      final double vy = point.x() * _transformationMatrix._m01 + point.y() * _transformationMatrix._m11 + point.z()
                        * _transformationMatrix._m21 + _transformationMatrix._m31;
      final double vz = point.x() * _transformationMatrix._m02 + point.y() * _transformationMatrix._m12 + point.z()
                        * _transformationMatrix._m22 + _transformationMatrix._m32;

      return new GVector3D(vx, vy, vz);
   }


   @Override
   public IVector3 inverseTransform(final IVector3 point) {

      if (_inverseTransformationMatrix == null) {
         return point;
      }

      final double vx = point.x() * _inverseTransformationMatrix._m00 + point.y() * _inverseTransformationMatrix._m10 + point.z()
                        * _inverseTransformationMatrix._m20 + _inverseTransformationMatrix._m30;
      final double vy = point.x() * _inverseTransformationMatrix._m01 + point.y() * _inverseTransformationMatrix._m11 + point.z()
                        * _inverseTransformationMatrix._m21 + _inverseTransformationMatrix._m31;
      final double vz = point.x() * _inverseTransformationMatrix._m02 + point.y() * _inverseTransformationMatrix._m12 + point.z()
                        * _inverseTransformationMatrix._m22 + _inverseTransformationMatrix._m32;

      return new GVector3D(vx, vy, vz);
   }


   public int getRows() {
      return _rowCount;
   }


   public int getColumns() {
      return _columnsCount;
   }


   public IVector3 getTranslationVector() {
      return _translationVector;
   }


   public GMatrix33D getRotationMatrix() {
      return _rotationMatrix;
   }


   public GMatrix44D getTransformationMatrix() {
      return _transformationMatrix;
   }


   @Override
   public void addVertexIndex(final int index) {

      _indexList.add(index);
   }


   @Override
   public IListInt getIndexList() {

      return _indexList;
   }


   @Override
   public void removeVertexIndex(final int index) {

      _indexList.removeByValue(index);

   }

}
