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
import es.igosoftware.util.GArrayListInt;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IComparatorInt;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.IListInt;
import es.igosoftware.util.IPredicate;


public final class GStructuredSubVertexContainer<

VectorT extends IVector<VectorT, ?>,

GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, IVertexContainer.Vertex<VectorT>, GroupT>

>
         extends
            GCommonSubVertexContainer<

            VectorT,

            IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>,

            GStructuredSubVertexContainer<VectorT, GroupT>,

            IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>
         //IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, GStructuredSubVertexContainer<VectorT, GroupT>>>

         implements
            IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, GStructuredSubVertexContainer<VectorT, GroupT>> {


   //////////////////////////////////////////////////////////////////////////////////////////////////////////

   public GStructuredSubVertexContainer(final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> outer,
                                        final List<Integer> indices) {
      super(outer, indices);
   }


   public GStructuredSubVertexContainer(final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> outer,
                                        final IListInt indices) {
      super(outer, indices);
   }


   public GStructuredSubVertexContainer(final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> outer,
                                        final int[] indices) {
      super(outer, indices);
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
      return _outer.getGroup(_indices[index]);
   }


   @Override
   public List<GroupT> getGroups() {

      final List<GroupT> groups = new ArrayList<GroupT>();

      for (final int index : _indices) {
         final GroupT group = getGroup(index);
         if (!groups.contains(group)) {
            groups.add(group);
         }
      }

      return groups;

   }


   @Override
   public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> getRawVertex(final int index) {
      return _outer.getRawVertex(_indices[index]);
   }


   @Override
   public VectorT getRawPoint(final int index) {
      return _outer.getRawPoint(_indices[index]);
   }


   @Override
   public VectorT getTransformedPoint(final int index) {
      return _outer.getTransformedPoint(_indices[index]);
   }


   @Override
   public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> getTransformedVertex(final int index) {
      return _outer.getTransformedVertex(_indices[index]);
   }


   @Override
   public IVectorI2 getRowColumn(final int index) {
      return _outer.getRowColumn(_indices[index]);
   }


   @Override
   protected String getStringName() {
      return "GStructuredSubVertexContainer";
   }


   @Override
   public boolean hasRowColumn() {
      return _outer.hasRowColumn();
   }


   @Override
   public boolean storageAsRawData() {
      return _outer.storageAsRawData();
   }


   @Override
   public GVectorPrecision rowColumnPrecision() {
      return _outer.rowColumnPrecision();
   }


   @Override
   public void setGroup(final int index,
                        final GroupT group) {
      //throw new RuntimeException("Operation not supported");
      _outer.setGroup(_indices[index], group);
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
      _outer.setPoint(_indices[index], point, intensity, normal, color, userData, rowColumn, group);

   }


   @Override
   public void setRowColumn(final int index,
                            final IVectorI2 rowColumn) {
      //throw new RuntimeException("Operation not supported");
      _outer.setRowColumn(_indices[index], rowColumn);
   }


   @Override
   public IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> composedWith(final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> container) {
      final GStructuredCompositeVertexContainer<VectorT, GroupT> composite = new GStructuredCompositeVertexContainer<VectorT, GroupT>();
      composite.addChild(this);
      composite.addChild(container);
      return composite;
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> reproject(final GProjection targetProjection) {
      if (projection() == targetProjection) {
         return this;
      }

      if (hasNormals()) {
         throw new IllegalArgumentException("Reprojection of Vertices Containers with normals is not supported");
      }

      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = _outer.reproject(targetProjection);

      final int[] indices = GCollections.rangeArray(0, result.size() - 1);

      return new GStructuredSubVertexContainer<VectorT, GroupT>(result, indices);

      //      final GStructuredSubVertexContainer<VectorT, GroupT> result = collect(
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

      if (!(that instanceof GStructuredSubVertexContainer)) {
         return false;
      }

      final GStructuredSubVertexContainer<VectorT, GroupT> other = (GStructuredSubVertexContainer<VectorT, GroupT>) that;

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

      final GStructuredSubVertexContainer other = (GStructuredSubVertexContainer) that;

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

      final GStructuredSubVertexContainer other = (GStructuredSubVertexContainer) that;

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
   public GStructuredSubVertexContainer<VectorT, GroupT> newEmptyContainer(final int initialCapacity) {
      //return _outer.newEmptyContainer(initialCapacity);
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> newEmptyContainer(final int initialCapacity,
                                                                           final GProjection projection) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GStructuredSubVertexContainer newEmptyContainer(final int initialCapacity,
                                                          final boolean storageAsRawData) {

      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GStructuredSubVertexContainer newEmptyContainer(final int initialCapacity,
                                                          final GProjection projection,
                                                          final boolean storageAsRawData) {

      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> collect(final IFunction<VectorT, VectorT> referencePointTransformer,
                                                                 final IFunction<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> vertexTransformer) {

      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = _outer.collect(
               referencePointTransformer, vertexTransformer);

      final int[] indices = GCollections.rangeArray(0, result.size() - 1);

      return new GStructuredSubVertexContainer<VectorT, GroupT>(result, indices);
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> select(final IPredicate<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> predicate) {

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
   public GStructuredSubVertexContainer<VectorT, GroupT> selectAsSubContainer(final IPredicate<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> predicate) {

      return select(predicate);
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> asSortedContainer(final Comparator<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> comparator) {


      return asSortedSubContainer(comparator);

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
      //      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = newEmptyContainer(size());
      //
      //      for (final int index : indices) {
      //         result.addPoint(getVertex(index));
      //      }
      //
      //      return result;
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> asSortedSubContainer(final Comparator<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> comparator) {

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
   public GStructuredSubVertexContainer<VectorT, GroupT> asMutableCopy() {

      throw new RuntimeException("Operation not supported");

      // return new GStructuredSubVertexContainer<VectorT, GroupT>(_outer, _indices);
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> asRawContainer() {

      //      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = newEmptyContainer(size());
      //
      //      for (int index = 0; index < size(); index++) {
      //         result.addPoint(getRawVertex(index));
      //      }
      //
      //      return result;

      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = _outer.asRawContainer();

      return new GStructuredSubVertexContainer<VectorT, GroupT>(result, _indices);

   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> asTransformedContainer() {

      //      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = newEmptyContainer(size());
      //
      //      for (int index = 0; index < size(); index++) {
      //         result.addPoint(getTransformedVertex(index));
      //      }
      //
      //      return result;

      final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> result = _outer.asTransformedContainer();

      return new GStructuredSubVertexContainer<VectorT, GroupT>(result, _indices);
   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> getGroupVertices(final GroupT group) {

      final List<GroupT> groupList = _outer.getGroups();

      if (!groupList.contains(group)) {
         return null;
      }

      final IListInt groupIndexList = group.getIndexList();
      final IListInt indicesList = new GArrayListInt(_indices);
      final IListInt resultList = new GArrayListInt(groupIndexList.size());

      for (int i = 0; i < groupIndexList.size(); i++) {
         final int groupIndex = groupIndexList.get(i);
         if (indicesList.contains(groupIndex)) {
            resultList.add(groupIndex);
         }
      }

      return new GStructuredSubVertexContainer<VectorT, GroupT>(_outer, resultList);
      //return _outer.getGroupVertices(group);
   }

}
