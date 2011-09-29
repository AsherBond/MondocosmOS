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
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IComparatorInt;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.IPredicate;


public final class GStructuredCompositeVertexContainer<

VectorT extends IVector<VectorT, ?>,

GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, IVertexContainer.Vertex<VectorT>, GroupT>

>
         extends
            GCommonCompositeVertexContainer<

            VectorT,

            IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>,

            GStructuredCompositeVertexContainer<VectorT, GroupT>,

            IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>
         //IVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, ?>>
         implements
            IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, GStructuredCompositeVertexContainer<VectorT, GroupT>> {


   /////////////////////////////////////////////////////////////////////////////////////////////////////////

   public GStructuredCompositeVertexContainer() {
      super();
   }


   public GStructuredCompositeVertexContainer(final int initialChildrenCapacity) {
      super(initialChildrenCapacity);
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final IVectorI2 rowColumn,
                       final GroupT group) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final IVectorI2 rowColumn) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final GroupT group) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final IColor color,
                       final IVectorI2 rowColumn,
                       final GroupT group) {

      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final IVectorI2 rowColumn,
                       final GroupT group) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final IVectorI2 rowColumn) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final GroupT group) {
      throw new RuntimeException("Operation not supported");
   }


   //   @Override
   //   public void addGroup(final GroupT group) {
   //      throw new RuntimeException("Operation not supported");
   //   }
   //
   //
   //   @Override
   //   public void addGroups(final List<GroupT> groups) {
   //      throw new RuntimeException("Operation not supported");
   //   }


   @Override
   public GroupT getGroup(final int index) {
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getGroup(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public List<GroupT> getGroups() {

      final List<GroupT> allGroups = new ArrayList<GroupT>();

      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         allGroups.addAll(child.getGroups());
      }

      return allGroups;
   }


   @Override
   public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> getRawVertex(final int index) {
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getRawVertex(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public VectorT getRawPoint(final int index) {
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getRawPoint(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public VectorT getTransformedPoint(final int index) {
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getTransformedPoint(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> getTransformedVertex(final int index) {
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getTransformedVertex(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public IVectorI2 getRowColumn(final int index) {
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getRowColumn(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   protected String getStringName() {
      return "GStructuredCompositeVertexContainer";
   }


   @Override
   public boolean hasRowColumn() {
      return _children.get(0).hasRowColumn();
   }


   @Override
   public boolean storageAsRawData() {
      return _children.get(0).storageAsRawData();
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> newEmptyContainer(final int initialCapacity) {
      //return _children.get(0).newEmptyContainer(initialCapacity);
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> newEmptyContainer(final int initialCapacity,
                                                                                 final GProjection projection) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GStructuredCompositeVertexContainer newEmptyContainer(final int initialCapacity,
                                                                final boolean storageAsRawData) {

      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GStructuredCompositeVertexContainer newEmptyContainer(final int initialCapacity,
                                                                final GProjection projection,
                                                                final boolean storageAsRawData) {

      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GVectorPrecision rowColumnPrecision() {
      return _children.get(0).rowColumnPrecision();
   }


   @Override
   public void setGroup(final int index,
                        final GroupT group) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setGroup(childIndex, group);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setPoint(final int index,
                        final VectorT point,
                        final float intensity,
                        final VectorT normal,
                        final IColor color,
                        final long userData,
                        final IVectorI2 rowColumn,
                        final GroupT group) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setPoint(childIndex, point, intensity, normal, color, userData, rowColumn, group);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setRowColumn(final int index,
                            final IVectorI2 rowColumn) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setRowColumn(childIndex, rowColumn);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> reproject(final GProjection targetProjection) {

      if (projection() == targetProjection) {
         return this;
      }

      if (hasNormals()) {
         throw new IllegalArgumentException("Reprojection of Vertices Containers with normals is not supported");
      }

      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = newEmptyContainer(_children.size());

      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         result.addChild(child.reproject(targetProjection));
      }

      return result;

      //      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = collect(
      //               new ITransformer<VectorT, VectorT>() {
      //                  @Override
      //                  public VectorT transform(final VectorT point) {
      //                     return point.reproject(projection(), targetProjection);
      //                  }
      //               },
      //               new ITransformer<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>>() {
      //                  @Override
      //                  public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> transform(final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex) {
      //                     final VectorT projectedPoint = vertex._point.reproject(projection(), targetProjection);
      //                     final GroupT projectedGroup = vertex._group.reproject(targetProjection);
      //                     return new IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>(projectedPoint, vertex._intensity,
      //                              vertex._normal, vertex._color, vertex._userData, vertex._rowColumn, projectedGroup);
      //                  }
      //               });
      //
      //      result.setProjection(targetProjection);
      //
      //      return result;
   }


   @SuppressWarnings("unchecked")
   @Override
   public boolean sameContents(final IVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, ?> that) {

      if (!basicContentsCheck(that)) {
         return false;
      }

      if (!(that instanceof GStructuredCompositeVertexContainer)) {
         return false;
      }

      final GStructuredCompositeVertexContainer<VectorT, GroupT> other = (GStructuredCompositeVertexContainer<VectorT, GroupT>) that;

      final int size = size();

      final List<GroupT> otherGroups = other.getGroups();

      final Iterator<GroupT> groupsIter = getGroups().iterator();
      final Iterator<GroupT> otherIter = otherGroups.iterator();

      while (groupsIter.hasNext()) {
         if (otherIter.hasNext()) {
            final GroupT groupThis = groupsIter.next();
            final GroupT groupOther = otherIter.next();
            if (!groupThis.equals(groupOther)) {
               return false;
            }
         }
         else {
            return false;
         }

      }

      for (int i = 0; i < size; i++) {

         if (hasRowColumn()) {
            if (!getRowColumn(i).equals(other.getRowColumn(i))) {
               return false;
            }
         }

         if (!getGroup(i).equals(other.getGroup(i))) {
            return false;
         }
      }

      return true;
   }


   @Override
   public boolean samePrecision(final IVertexContainer<?, ?, ?> that) {

      if (!basicPrecisionCheck(that)) {
         return false;
      }

      final GStructuredCompositeVertexContainer other = (GStructuredCompositeVertexContainer) that;

      if (rowColumnPrecision() != other.rowColumnPrecision()) {
         return false;
      }

      return true;
   }


   @Override
   public boolean sameShapeThan(final IVertexContainer<?, ?, ?> that) {

      if (!basicShapeCheck(that)) {
         return false;
      }

      final GStructuredCompositeVertexContainer other = (GStructuredCompositeVertexContainer) that;

      if (hasRowColumn() != other.hasRowColumn()) {
         return false;
      }

      return true;
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> asSubContainer(final int[] subIndices) {
      return new GStructuredSubVertexContainer<VectorT, GroupT>(this, subIndices);
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> collect(final IFunction<VectorT, VectorT> referencePointTransformer,
                                                                       final IFunction<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> vertexTransformer) {

      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = newEmptyContainer(_children.size());

      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         result.addChild(child.collect(referencePointTransformer, vertexTransformer));
      }

      return result;
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> select(final IPredicate<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> predicate) {

      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = newEmptyContainer(_children.size());

      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         result.addChild(child.select(predicate));
      }

      return result;
   }


   @Override
   public IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> selectAsSubContainer(final IPredicate<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> predicate) {

      final int size = size();
      final ArrayList<Integer> selectedIndices = new ArrayList<Integer>(size);

      for (int i = 0; i < size; i++) {
         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex = getVertex(i);
         if (predicate.evaluate(vertex)) {
            selectedIndices.add(i);
         }
      }

      if (selectedIndices.size() == size) {
         return this;
      }

      return new GStructuredSubVertexContainer<VectorT, GroupT>(this, selectedIndices);
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> asSortedContainer(final Comparator<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> comparator) {

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
      //      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> childResult = newEmptyContainer(size());
      //
      //      for (final int index : indices) {
      //         childResult.addPoint(getVertex(index));
      //      }
      //
      //      // return a composite with only one child with sorted vertex
      //      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = newEmptyContainer(size());
      //
      //      result.addChild(result);
      //
      //      return result;
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> asSortedSubContainer(final Comparator<StructuredVertex<VectorT, GroupT>> comparator) {

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
   public final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> composedWith(final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> container) {
      final GStructuredCompositeVertexContainer<VectorT, GroupT> composite = new GStructuredCompositeVertexContainer<VectorT, GroupT>();
      composite.addChild(this);
      composite.addChild(container);
      return composite;
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> asMutableCopy() {

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


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> asRawContainer() {

      //      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = newEmptyContainer(size());
      //
      //      for (int index = 0; index < size(); index++) {
      //         result.addPoint(getRawVertex(index));
      //      }
      //
      //      return result;

      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = new GStructuredCompositeVertexContainer<VectorT, GroupT>(
               _children.size());

      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         result.addChild(child.asRawContainer());
      }

      return result;
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> asTransformedContainer() {

      //      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = newEmptyContainer(size());
      //
      //      for (int index = 0; index < size(); index++) {
      //         result.addPoint(getTransformedVertex(index));
      //      }
      //
      //      return result;

      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = new GStructuredCompositeVertexContainer<VectorT, GroupT>(
               _children.size());

      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         result.addChild(child.asTransformedContainer());
      }

      return result;
   }


   @Override
   public GStructuredCompositeVertexContainer<VectorT, GroupT> getGroupVertices(final GroupT group) {

      final GStructuredCompositeVertexContainer<VectorT, GroupT> result = new GStructuredCompositeVertexContainer<VectorT, GroupT>(
               _children.size());

      for (final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> child : _children) {
         result.addChild(child.getGroupVertices(group));
      }

      return result;

   }


}
