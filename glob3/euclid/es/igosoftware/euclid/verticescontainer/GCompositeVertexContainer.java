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

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IComparatorInt;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.IPredicate;


public final class GCompositeVertexContainer<VectorT extends IVector<VectorT, ?>>
         extends
            GCommonCompositeVertexContainer<VectorT,

            IVertexContainer.Vertex<VectorT>,

            GCompositeVertexContainer<VectorT>,

            IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>
            //IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>

            >
         implements
            IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, GCompositeVertexContainer<VectorT>> {


   public GCompositeVertexContainer() {
      super();
   }


   public GCompositeVertexContainer(final int initialChildrenCapacity) {
      super(initialChildrenCapacity);
   }


   @SuppressWarnings("unchecked")
   @Override
   public VectorT getReferencePoint() {
      final byte dimensions = dimensions();
      switch (dimensions) {
         case 2:
            return (VectorT) GVector2D.ZERO;
         case 3:
            return (VectorT) GVector3D.ZERO;
         default:
            throw new RuntimeException("Dimensions " + dimensions + " not supported");
      }
   }


   @Override
   protected String getStringName() {
      return "GCompositeVertexContainer";
   }


   @Override
   public GCompositeVertexContainer<VectorT> newEmptyContainer(final int initialCapacity,
                                                               final VectorT referencePoint) {
      //      return _children.get(0).newEmptyContainer(initialCapacity, referencePoint);
      throw new RuntimeException("Operation not supported");

   }


   @Override
   public GCompositeVertexContainer<VectorT> newEmptyContainer(final int initialCapacity,
                                                               final GProjection projection) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GCompositeVertexContainer<VectorT> newEmptyContainer(final int initialChildrenCapacity) {
      //      return _children.get(0).newEmptyContainer(initialCapacity);
      throw new RuntimeException("Operation not supported");
      //      return new GCompositeVertexContainer<VectorT>(initialChildrenCapacity);
   }


   @Override
   public GCompositeVertexContainer<VectorT> newEmptyContainer(final int initialCapacity,
                                                               final GProjection projection,
                                                               final VectorT referencePoint) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GCompositeVertexContainer<VectorT> collect(final IFunction<VectorT, VectorT> referencePointTransformer,
                                                     final IFunction<IVertexContainer.Vertex<VectorT>, IVertexContainer.Vertex<VectorT>> vertexTransformer) {


      final GCompositeVertexContainer<VectorT> result = new GCompositeVertexContainer<VectorT>(_children.size());

      for (final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> child : _children) {
         result.addChild(child.collect(referencePointTransformer, vertexTransformer));
      }

      return result;
   }


   @Override
   public GCompositeVertexContainer<VectorT> select(final IPredicate<IVertexContainer.Vertex<VectorT>> predicate) {

      final GCompositeVertexContainer<VectorT> result = new GCompositeVertexContainer<VectorT>(_children.size());

      for (final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> child : _children) {
         result.addChild(child.select(predicate));
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
   public GCompositeVertexContainer<VectorT> asSortedContainer(final Comparator<IVertexContainer.Vertex<VectorT>> comparator) {

      throw new RuntimeException("Operation not supported");
      //      final int[] indices = GCollections.rangeArray(0, size() - 1);
      //
      //      GCollections.quickSort(indices, new IComparatorInt() {
      //         @Override
      //         public int compare(final int index1,
      //                            final int index2) {
      //            return comparator.compare(getVertex(index1), getVertex(index2));
      //         }
      //      });
      //
      //      final int TODO_revisar_si_es_esto_lo_que_debe_hacer;
      //
      //      //      final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> resultChild = _children.get(0).newEmptyContainer(
      //      //               size());
      //      //
      //      //      for (final int index : indices) {
      //      //         resultChild.addPoint(getVertex(index));
      //      //      }
      //      //
      //      //      // return a composite with only one child with sorted vertex
      //      //      final GCompositeVertexContainer<VectorT> result = new GCompositeVertexContainer<VectorT>();
      //      //
      //      //      result.addChild(resultChild);
      //      //
      //      //      return result;
      //
      //      final GCompositeVertexContainer<VectorT> result = new GCompositeVertexContainer<VectorT>();
      //
      //      int index = 0;
      //      for (final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> child : _children) {
      //
      //         final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> resultChild = child.newEmptyContainer(child.size());
      //
      //         int resultIndex = 0;
      //         while (resultIndex < child.size()) {
      //            resultChild.addPoint(getVertex(indices[index]));
      //            resultIndex++;
      //            index++;
      //         }
      //
      //         result.addChild(resultChild);
      //
      //      }
      //
      //      return result;
   }


   @Override
   public GCompositeVertexContainer<VectorT> reproject(final GProjection targetProjection) {

      if (projection() == targetProjection) {
         return this;
      }

      if (hasNormals()) {
         throw new IllegalArgumentException("Reprojection of Vertices Containers with normals is not supported");
      }

      final GCompositeVertexContainer<VectorT> result = new GCompositeVertexContainer<VectorT>(_children.size());

      for (final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> child : _children) {
         result.addChild(child.reproject(targetProjection));
      }

      return result;

      //      final GCompositeVertexContainer<VectorT> result = collect(new ITransformer<VectorT, VectorT>() {
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
   public GSubVertexContainer<VectorT> asSubContainer(final int[] subIndices) {
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
   public IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> composedWith(final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> container) {
      final GCompositeVertexContainer<VectorT> composite = new GCompositeVertexContainer<VectorT>();
      composite.addChild(this);
      composite.addChild(container);
      return composite;
   }


   @Override
   public GCompositeVertexContainer<VectorT> asMutableCopy() {

      throw new RuntimeException("Operation not supported");

      //      final List<IVertexContainer<VectorT, VertexT, ?>> childrenList = new ArrayList<IVertexContainer<VectorT, VertexT, ?>>();
      //
      //      for (final IVertexContainer<VectorT, VertexT, ?> child : _children) {
      //         final IVertexContainer<VectorT, VertexT, ?> childResult = newEmptyContainer(child.size(), child.getReferencePoint());
      //
      //         for (int index = 0; index < child.size(); index++) {
      //            childResult.addPoint(child.getVertex(index));
      //         }
      //         childrenList.add(childResult);
      //      }
      //
      //      return new GCompositeVertexContainer<VectorT>(childrenList);
   }


}
