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

import java.util.ArrayList;
import java.util.Comparator;

import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IComparatorInt;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.IPredicate;


public abstract class GVertexContainerWithDefaultsAbstract<

VectorT extends IVector<VectorT, ?>,

MutableT extends GVertexContainerWithDefaultsAbstract<VectorT, MutableT>

>
         extends
            GCommonVertexContainerAbstract<VectorT, IVertexContainer.Vertex<VectorT>, MutableT>
         implements
            IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, MutableT> {


   protected final VectorT _referencePoint;


   ////////////////////////////////////////////////////////////////////////////////

   protected GVertexContainerWithDefaultsAbstract(final GVectorPrecision vectorPrecision,
                                                  final GColorPrecision colorPrecision,
                                                  final GProjection projection,
                                                  final VectorT referencePoint,
                                                  final int initialCapacity,
                                                  final boolean withIntensities,
                                                  final float defaultIntensity,
                                                  final boolean withColors,
                                                  final IColor defaultColor,
                                                  final boolean withNormals,
                                                  final VectorT defaultNormal,
                                                  final boolean withUserData,
                                                  final long defaultUserData) {

      super(vectorPrecision, colorPrecision, projection, initialCapacity, withIntensities, defaultIntensity, withColors,
            defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);

      _referencePoint = referencePoint;
   }


   @Override
   public final int addPoint(final IVertexContainer.Vertex<VectorT> vertex) {
      return addPoint(vertex._point, vertex._intensity, vertex._normal, vertex._color, vertex._userData);
   }


   @Override
   public final int addPoint(final VectorT point) {
      return addPoint(point, _defaultIntensity, _defaultNormal, _defaultColor, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final float intensity) {
      return addPoint(point, intensity, _defaultNormal, _defaultColor, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final float intensity,
                             final IColor color) {
      return addPoint(point, intensity, _defaultNormal, color, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final float intensity,
                             final VectorT normal) {
      return addPoint(point, intensity, normal, _defaultColor, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final float intensity,
                             final VectorT normal,
                             final IColor color,
                             final long userData) {

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't add more points");
      }

      final int position = addBasicPointData(intensity, normal, color, userData);
      _pointsHandler.putVector(position, point.sub(_referencePoint));

      changed();

      return position;
   }


   @Override
   public final int addPoint(final VectorT point,
                             final float intensity,
                             final VectorT normal,
                             final IColor color) {
      return addPoint(point, intensity, normal, color, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final IColor color) {
      return addPoint(point, _defaultIntensity, _defaultNormal, color, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final VectorT normal) {
      return addPoint(point, _defaultIntensity, normal, _defaultColor, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final VectorT normal,
                             final IColor color) {
      return addPoint(point, _defaultIntensity, normal, color, _defaultUserData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final long userData) {
      return addPoint(point, _defaultIntensity, _defaultNormal, _defaultColor, userData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final IColor color,
                             final long userData) {
      return addPoint(point, _defaultIntensity, _defaultNormal, color, userData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final float intensity,
                             final long userData) {
      return addPoint(point, intensity, _defaultNormal, _defaultColor, userData);
   }


   @Override
   public final int addPoint(final VectorT point,
                             final VectorT normal,
                             final long userData) {
      return addPoint(point, _defaultIntensity, normal, _defaultColor, userData);
   }


   @Override
   public final void ensureCapacity(final int minCapacity) {

      final int newCapacity = ensureMinCapacity(minCapacity);

      _capacity = newCapacity;
   }


   @Override
   public boolean equals(final Object obj) {

      return basicEquals(obj);
   }


   @Override
   public final boolean sameShapeThan(final IVertexContainer<?, ?, ?> that) {

      return basicShapeCheck(that);

   }


   @Override
   public final boolean samePrecision(final IVertexContainer<?, ?, ?> that) {

      return basicPrecisionCheck(that);

   }


   @Override
   public final boolean sameContents(final IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> that) {

      return basicContentsCheck(that);
   }


   @Override
   public final VectorT getPoint(final int index) {
      rangeCheck(index);
      return _referencePoint.add(_pointsHandler.getVector(index));
   }


   @Override
   public VectorT getReferencePoint() {
      return _referencePoint;
   }


   @Override
   protected String getStringPostfix() {
      return ", capacity=" + _capacity + ", projection=" + projection() + ", vectorPrecision=" + _vectorPrecision
             + ", referencePoint=" + _referencePoint;
   }


   @Override
   public IVertexContainer.Vertex<VectorT> getVertex(final int index) {
      rangeCheck(index);
      return new IVertexContainer.Vertex<VectorT>(getPoint(index), getIntensity(index), getNormal(index), getColor(index),
               getUserData(index));
   }


   @Override
   public final void setVertex(final int index,
                               final IVertexContainer.Vertex<VectorT> vertex) {
      setPoint(index, vertex._point, vertex._intensity, vertex._normal, vertex._color, vertex._userData);

   }


   @Override
   public final void setPoint(final int index,
                              final VectorT point) {
      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      _pointsHandler.putVector(index, point.sub(_referencePoint));

      changed();

   }


   @Override
   public final void setPoint(final int index,
                              final VectorT point,
                              final float intensity,
                              final VectorT normal,
                              final IColor color,
                              final long userData) {

      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      setBasicPointData(index, intensity, normal, color, userData);

      _pointsHandler.putVector(index, point.sub(_referencePoint));

      changed();

   }


   @Override
   public int hashCode() {
      return basicHashCode();
   }


   @Override
   public final MutableT asMutableCopy() {

      final MutableT result = newEmptyContainer(size(), getReferencePoint());

      for (int index = 0; index < size(); index++) {
         result.addPoint(getVertex(index));
      }

      return result;
   }


   @SuppressWarnings("unchecked")
   @Override
   public final MutableT reproject(final GProjection targetProjection) {

      if (_projection == targetProjection) {
         return (MutableT) this; // Compiler required cast. Force us to include @SuppressWarnings("unchecked")
      }

      if (hasNormals()) {
         throw new IllegalArgumentException("Reprojection of Vertices Containers with normals is not supported");
      }

      final MutableT result = newEmptyContainer(size(), targetProjection,
               _referencePoint.reproject(_projection, targetProjection));

      for (int i = 0; i < size(); i++) {
         final IVertexContainer.Vertex<VectorT> vertex = getVertex(i);
         final VectorT projectedPoint = vertex._point.reproject(_projection, targetProjection);
         final IVertexContainer.Vertex<VectorT> projectedVertex = new IVertexContainer.Vertex<VectorT>(projectedPoint,
                  vertex._intensity, vertex._normal, vertex._color, vertex._userData);
         result.addPoint(projectedVertex);
      }

      return result;

      //      setProjection(targetProjection);
      //
      //      return collect(new ITransformer<VectorT, VectorT>() {
      //         @Override
      //         public VectorT transform(final VectorT point) {
      //            return point.reproject(_projection, targetProjection);
      //         }
      //      }, new ITransformer<IVertexContainer.Vertex<VectorT>, IVertexContainer.Vertex<VectorT>>() {
      //         @Override
      //         public IVertexContainer.Vertex<VectorT> transform(final IVertexContainer.Vertex<VectorT> vertex) {
      //            final VectorT projectedPoint = vertex._point.reproject(_projection, targetProjection);
      //            return new IVertexContainer.Vertex<VectorT>(projectedPoint, vertex._intensity, vertex._normal, vertex._color,
      //                     vertex._userData);
      //         }
      //      });
   }


   @Override
   public MutableT asSortedContainer(final Comparator<IVertexContainer.Vertex<VectorT>> comparator) {

      final int[] indices = GCollections.rangeArray(0, size() - 1);

      GCollections.quickSort(indices, new IComparatorInt() {
         @Override
         public int compare(final int index1,
                            final int index2) {
            return comparator.compare(getVertex(index1), getVertex(index2));
         }
      });

      final MutableT result = newEmptyContainer(size(), getReferencePoint());

      for (final int index : indices) {
         result.addPoint(getVertex(index));
      }

      return result;
   }


   @Override
   public MutableT select(final IPredicate<IVertexContainer.Vertex<VectorT>> predicate) {

      final MutableT result = newEmptyContainer(size(), getReferencePoint());

      final int size = size();
      for (int i = 0; i < size; i++) {
         final IVertexContainer.Vertex<VectorT> vertex = getVertex(i);
         if (predicate.evaluate(vertex)) {
            result.addPoint(vertex);
         }
      }

      return result;
   }


   @Override
   public IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> selectAsSubContainer(final IPredicate<IVertexContainer.Vertex<VectorT>> predicate) {

      final int size = size();
      final ArrayList<Integer> selectedIndices = new ArrayList<Integer>(size);

      for (int i = 0; i < size; i++) {
         final IVertexContainer.Vertex<VectorT> vertex = getVertex(i);
         if (predicate.evaluate(vertex)) {
            selectedIndices.add(i);
         }
      }

      if (selectedIndices.size() == size) {
         return this;
      }

      return new GSubVertexContainer<VectorT>(this, selectedIndices);
   }


   @Override
   public final MutableT collect(final IFunction<VectorT, VectorT> referencePointTransformer,
                                 final IFunction<IVertexContainer.Vertex<VectorT>, IVertexContainer.Vertex<VectorT>> vertexTransformer) {

      final MutableT result = newEmptyContainer(size(), referencePointTransformer.apply(_referencePoint));

      for (int i = 0; i < size(); i++) {
         final IVertexContainer.Vertex<VectorT> collectedVertex = vertexTransformer.apply(getVertex(i));
         result.addPoint(collectedVertex);
      }

      return result;
   }


   @Override
   public final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> composedWith(final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> container) {
      final GCompositeVertexContainer<VectorT> composite = new GCompositeVertexContainer<VectorT>();
      composite.addChild(this);
      composite.addChild(container);
      return composite;
   }


   @Override
   public final GSubVertexContainer<VectorT> asSubContainer(final int[] subIndices) {
      return new GSubVertexContainer<VectorT>(this, subIndices);
   }


   @Override
   public GSubVertexContainer<VectorT> asSortedSubContainer(final Comparator<IVertexContainer.Vertex<VectorT>> comparator) {
      final int[] indices = GCollections.rangeArray(0, size() - 1);

      GCollections.quickSort(indices, new IComparatorInt() {
         @Override
         public int compare(final int index1,
                            final int index2) {
            return comparator.compare(getVertex(index1), getVertex(index2));
         }
      });

      return asSubContainer(indices);
   }


   @Override
   public void trimToSize() {

      basicTrimToSize();

      _capacity = _size;
   }


}
