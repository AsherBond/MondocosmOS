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
import java.util.List;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IComparatorInt;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.IPredicate;


public final class GSubVertexContainer<VectorT extends IVector<VectorT, ?>>
         extends
            GCommonSubVertexContainer<

            VectorT,

            IVertexContainer.Vertex<VectorT>,

            GSubVertexContainer<VectorT>,

            IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>
            //IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>

            >
         implements
            IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, GSubVertexContainer<VectorT>> {


   public GSubVertexContainer(final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> outer,
                              final List<Integer> indices) {
      super(outer, indices);
   }


   public GSubVertexContainer(final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> outer,
                              final int[] indices) {
      super(outer, indices);
   }


   @Override
   public VectorT getReferencePoint() {
      return _outer.getReferencePoint();
   }


   @Override
   protected String getStringName() {
      return "GSubVertexContainer";
   }


   @Override
   public GSubVertexContainer<VectorT> asSortedSubContainer(final Comparator<IVertexContainer.Vertex<VectorT>> comparator) {

      final int[] sortedIndices = new int[_indices.length];
      System.arraycopy(_indices, 0, sortedIndices, 0, _indices.length);

      GCollections.quickSort(sortedIndices, new IComparatorInt() {
         @Override
         public int compare(final int index1,
                            final int index2) {
            return comparator.compare(_outer.getVertex(index1), _outer.getVertex(index2));
         }
      });

      //return _outer.asSubContainer(sortedIndices);
      return new GSubVertexContainer<VectorT>(_outer, sortedIndices);
   }


   @Override
   public GSubVertexContainer<VectorT> asSortedContainer(final Comparator<IVertexContainer.Vertex<VectorT>> comparator) {


      //    final int[] indices = GCollections.rangeArray(0, size() - 1);
      //
      //      GCollections.quickSort(indices, new IComparatorInt() {
      //         @Override
      //         public int compare(final int index1,
      //                            final int index2) {
      //            return comparator.compare(getVertex(index1), getVertex(index2));
      //         }
      //      });
      //
      //      final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> result = newEmptyContainer(size());
      //
      //      for (final int index : indices) {
      //         result.addPoint(getVertex(index));
      //      }
      //
      //      return result;

      return asSortedSubContainer(comparator);
   }


   @Override
   public GSubVertexContainer<VectorT> collect(final IFunction<VectorT, VectorT> referencePointTransformer,
                                               final IFunction<IVertexContainer.Vertex<VectorT>, IVertexContainer.Vertex<VectorT>> vertexTransformer) {

      final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> result = _outer.collect(
               referencePointTransformer, vertexTransformer);

      final int[] indices = GCollections.rangeArray(0, result.size() - 1);

      return new GSubVertexContainer<VectorT>(result, indices);


   }


   @Override
   public GSubVertexContainer<VectorT> reproject(final GProjection targetProjection) {
      if (projection() == targetProjection) {
         return this;
      }

      if (hasNormals()) {
         throw new IllegalArgumentException("Reprojection of Vertices Containers with normals is not supported");
      }

      final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> result = _outer.reproject(targetProjection);

      final int[] indices = GCollections.rangeArray(0, result.size() - 1);

      return new GSubVertexContainer<VectorT>(result, indices);


      //      final GSubVertexContainer<VectorT> result = collect(new ITransformer<VectorT, VectorT>() {
      //         @Override
      //         public VectorT transform(final VectorT point) {
      //            return point.reproject(projection(), targetProjection);
      //         }
      //      }, new ITransformer<IVertexContainer.Vertex<VectorT>, IVertexContainer.Vertex<VectorT>>() {
      //         @Override
      //         public IVertexContainer.Vertex<VectorT> transform(final IVertexContainer.Vertex<VectorT> vertex) {
      //            final VectorT projectedPoint = vertex._point.reproject(projection(), targetProjection);
      //            return new IVertexContainer.Vertex<VectorT>(projectedPoint, vertex._intensity, vertex._normal, vertex._color,
      //                     vertex._userData);
      //         }
      //      });
      //
      //      result.setProjection(targetProjection);
      //
      //      return result;
   }


   @Override
   public boolean sameContents(final IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> that) {
      return basicContentsCheck(that);
   }


   @Override
   public boolean samePrecision(final IVertexContainer<?, ?, ?> that) {
      return basicPrecisionCheck(that);
   }


   @Override
   public boolean sameShapeThan(final IVertexContainer<?, ?, ?> that) {
      return basicShapeCheck(that);
   }


   @Override
   public IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> composedWith(final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> container) {
      final GCompositeVertexContainer<VectorT> composite = new GCompositeVertexContainer<VectorT>();
      composite.addChild(this);
      composite.addChild(container);
      return composite;
   }


   @Override
   public GSubVertexContainer<VectorT> asSubContainer(final int[] subIndices) {
      return new GSubVertexContainer<VectorT>(this, subIndices);
   }


   @Override
   public GSubVertexContainer<VectorT> newEmptyContainer(final int initialCapacity) {
      //return _outer.newEmptyContainer(initialCapacity);
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GSubVertexContainer<VectorT> newEmptyContainer(final int initialCapacity,
                                                         final GProjection projection) {

      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GSubVertexContainer<VectorT> newEmptyContainer(final int initialCapacity,
                                                         final VectorT referencePoint) {
      //return _outer.newEmptyContainer(initialCapacity, referencePoint);
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GSubVertexContainer<VectorT> newEmptyContainer(final int initialCapacity,
                                                         final GProjection projection,
                                                         final VectorT referencePoint) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GSubVertexContainer<VectorT> select(final IPredicate<IVertexContainer.Vertex<VectorT>> predicate) {

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

      return new GSubVertexContainer<VectorT>(_outer, selectedIndices);
   }


   @Override
   public IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> selectAsSubContainer(final IPredicate<IVertexContainer.Vertex<VectorT>> predicate) {

      return select(predicate);
   }


   @Override
   public GSubVertexContainer<VectorT> asMutableCopy() {

      throw new RuntimeException("Operation not supported");

      // return new GSubVertexContainer<VectorT>(_outer, _indices);
   }


}
