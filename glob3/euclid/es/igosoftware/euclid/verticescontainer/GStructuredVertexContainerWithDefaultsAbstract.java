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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IComparatorInt;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.IListInt;
import es.igosoftware.util.IPredicate;


public abstract class GStructuredVertexContainerWithDefaultsAbstract<

VectorT extends IVector<VectorT, ?>,

GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, IVertexContainer.Vertex<VectorT>, GroupT>,

MutableT extends GStructuredVertexContainerWithDefaultsAbstract<VectorT, GroupT, MutableT>

>
         extends
            GCommonVertexContainerAbstract<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, MutableT>
         implements
            IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, MutableT> {


   /////////////////////////////////////////////////////////////////////////////////

   protected static interface RowColumnHandler {

      public IVectorI2 getRowColumn(final int index);


      public void growBuffer(final int newCapacity);


      public void putRowColumn(final int index,
                               final IVectorI2 vector);


      public void shrinkBuffer(final int size);
   }


   protected final boolean          _storageAsRawData;
   protected final GVectorPrecision _rowColumnPrecision;
   protected final IVectorI2        _defaultRowColumn;
   protected final RowColumnHandler _rowColumnHandler;
   protected final GroupT           _defaultGroup;
   protected ArrayList<GroupT>      _groupsBuffer = new ArrayList<GroupT>(); // store buffer for any vertex
   protected ArrayList<GroupT>      _groups       = new ArrayList<GroupT>(); // List maintaining groups order
   protected HashSet<GroupT>        _groupsSet    = new HashSet<GroupT>();  // Set to optimize contains checks


   protected GStructuredVertexContainerWithDefaultsAbstract(final GVectorPrecision vectorPrecision,
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
                                                            final long defaultUserData,
                                                            final boolean storeAsRawData,
                                                            final GVectorPrecision rowColumnPrecision,
                                                            final boolean withRowColumn,
                                                            final IVectorI2 defaultRowColumn,
                                                            final GroupT defaultGroup) {

      super(vectorPrecision, colorPrecision, projection, initialCapacity, withIntensities, defaultIntensity, withColors,
            defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);

      _storageAsRawData = storeAsRawData;
      _rowColumnPrecision = rowColumnPrecision;
      _defaultRowColumn = defaultRowColumn;
      _defaultGroup = (defaultGroup == null) ? initializeDefaultGroup() : defaultGroup;
      _rowColumnHandler = withRowColumn ? initializeRowColumnHandler() : null;
      _groupsBuffer.ensureCapacity(initialCapacity);
      //      _groups.add(_defaultGroup);
      //      _groupsSet.add(_defaultGroup);

   }


   protected abstract GStructuredVertexContainerWithDefaultsAbstract.RowColumnHandler initializeRowColumnHandler();


   protected abstract GroupT initializeDefaultGroup();


   @Override
   public IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> composedWith(final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> container) {
      final GStructuredCompositeVertexContainer<VectorT, GroupT> composite = new GStructuredCompositeVertexContainer<VectorT, GroupT>();
      composite.addChild(this);
      composite.addChild(container);
      return composite;
   }


   @Override
   public boolean equals(final Object object) {
      if (object == null) {
         return false;
      }

      if (object.getClass() != getClass()) {
         return false;
      }

      final GStructuredVertexContainerWithDefaultsAbstract other = (GStructuredVertexContainerWithDefaultsAbstract) object;

      if (_rowColumnPrecision != other._rowColumnPrecision) {
         return false;
      }

      if (_defaultRowColumn == null) {
         if (other._defaultRowColumn != null) {
            return false;
         }
      }
      else if (!_defaultRowColumn.equals(other._defaultRowColumn)) {
         return false;
      }

      if (_defaultGroup == null) {
         if (other._defaultGroup != null) {
            return false;
         }
      }
      else if (!_defaultGroup.equals(other._defaultGroup)) {
         return false;
      }

      if (_rowColumnHandler == null) {
         if (other._rowColumnHandler != null) {
            return false;
         }
      }
      else if (!_rowColumnHandler.equals(other._rowColumnHandler)) {
         return false;
      }

      if (_groupsBuffer == null) {
         if (other._groupsBuffer != null) {
            return false;
         }
      }
      else if (!_groupsBuffer.equals(other._groupsBuffer)) {
         return false;
      }

      // _group should always be not null
      if (!_groups.equals(other._groups)) {
         return false;
      }

      return basicEquals(object);
   }


   @Override
   protected String getStringPostfix() {
      return ", capacity=" + _capacity + ", projection=" + projection() + ", vectorPrecision=" + _vectorPrecision
             + ", rowColumnPrecision=" + _rowColumnPrecision + ", groups= " + getGroups().size();
   }


   @Override
   public int hashCode() {

      final int prime = 31;
      int result = basicHashCode();
      result = prime * result + ((_rowColumnPrecision == null) ? 0 : _rowColumnPrecision.hashCode());
      result = prime * result + ((_defaultRowColumn == null) ? 0 : _defaultRowColumn.hashCode());
      result = prime * result + ((_defaultGroup == null) ? 0 : _defaultGroup.hashCode());
      result = prime * result + ((_rowColumnHandler == null) ? 0 : _rowColumnHandler.hashCode());
      result = prime * result + ((_groupsBuffer == null) ? 0 : _groupsBuffer.hashCode());
      result = prime * result + ((_groups == null) ? 0 : _groups.hashCode());
      return result;

   }


   //   @Override
   //   public void addGroup(final GroupT group) {
   //
   //          if (!_groupsSet.contains(group)) {
   //      _groups.add(group);
   //      _groupsSet.add(group);
   //          }
   //
   //      final int TODO_DEBERIA_ADEMAS_AÑADIR_TODOS_LOS_PUNTOS_DEL_GRUPO; //?????
   //   }
   //
   //
   //   @Override
   //   public void addGroups(final List<GroupT> groups) {
   //      for (final GroupT group : groups) {
   //         addGroup(group);
   //      }
   //   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final IVectorI2 rowColumn,
                       final GroupT group) {

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't add more points");
      }

      final int position = addBasicPointData(intensity, normal, color, userData);

      if (_rowColumnHandler != null) {
         _rowColumnHandler.putRowColumn(position, rowColumn);
      }

      if (group != null) {

         _pointsHandler.putVector(position, point.sub(group.getReferencePoint()));

         if (!_groupsSet.contains(group)) {
            _groups.add(group);
            _groupsSet.add(group);
         }

         if (_groupsBuffer != null) {
            _groupsBuffer.add(group);
         }

         group.addVertexIndex(position);
      }
      else {

         _pointsHandler.putVector(position, point.sub(_defaultGroup.getReferencePoint()));

         if (!_groupsSet.contains(_defaultGroup)) {
            _groups.add(_defaultGroup);
            _groupsSet.add(_defaultGroup);
         }

         if (_groupsBuffer != null) {
            _groupsBuffer.add(_defaultGroup);
         }

         _defaultGroup.addVertexIndex(position);
      }

      changed();

      return position;
   }


   @Override
   public GroupT getGroup(final int index) {
      rangeCheck(index);
      return _groupsBuffer.get(index);
   }


   @Override
   public List<GroupT> getGroups() {

      return _groups;
   }


   @Override
   public VectorT getPoint(final int index) {

      rangeCheck(index);
      final GroupT group = _groupsBuffer.get(index);
      return group.getReferencePoint().add(_pointsHandler.getVector(index));
   }


   @Override
   public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> getVertex(final int index) {

      rangeCheck(index);
      return new IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>(getPoint(index), getIntensity(index),
               getNormal(index), getColor(index), getUserData(index), getRowColumn(index), getGroup(index));
   }


   @Override
   public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> getRawVertex(final int index) {

      rangeCheck(index);
      final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex = getVertex(index);
      if (_storageAsRawData) {
         return vertex;
      }
      final VectorT rawPoint = vertex._group.inverseTransform(vertex._point);
      return (new IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>(rawPoint, vertex._intensity, vertex._normal,
               vertex._color, vertex._userData, vertex._rowColumn, vertex._group));
   }


   @Override
   public VectorT getRawPoint(final int index) {
      rangeCheck(index);
      return getRawVertex(index)._point;
   }


   @Override
   public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> getTransformedVertex(final int index) {

      rangeCheck(index);
      final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex = getVertex(index);
      if (!_storageAsRawData) {
         return vertex;
      }
      final VectorT transformedPoint = vertex._group.transform(vertex._point);
      return (new IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>(transformedPoint, vertex._intensity,
               vertex._normal, vertex._color, vertex._userData, vertex._rowColumn, vertex._group));
   }


   @Override
   public VectorT getTransformedPoint(final int index) {

      rangeCheck(index);
      return getTransformedVertex(index)._point;
   }


   @Override
   public IVectorI2 getRowColumn(final int index) {
      rangeCheck(index);
      if (_rowColumnHandler != null) {
         return _rowColumnHandler.getRowColumn(index);
      }
      return _defaultRowColumn;
   }


   @Override
   public boolean hasRowColumn() {
      return (_rowColumnHandler != null);
   }


   @Override
   public boolean storageAsRawData() {
      return _storageAsRawData;
   }


   @Override
   public GVectorPrecision rowColumnPrecision() {
      return _rowColumnPrecision;
   }


   @Override
   public int addPoint(final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex) {
      return addPoint(vertex._point, vertex._intensity, vertex._normal, vertex._color, vertex._userData, vertex._rowColumn,
               vertex._group);
   }


   @Override
   public int addPoint(final VectorT point) {
      return addPoint(point, _defaultIntensity, _defaultNormal, _defaultColor, _defaultUserData, _defaultRowColumn, _defaultGroup);
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity) {
      return addPoint(point, intensity, _defaultNormal, _defaultColor, _defaultUserData, _defaultRowColumn, _defaultGroup);
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final IColor color) {
      return addPoint(point, intensity, _defaultNormal, color, _defaultUserData, _defaultRowColumn, _defaultGroup);
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color) {
      return addPoint(point, intensity, normal, color, _defaultUserData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final IColor color) {
      return addPoint(point, _defaultIntensity, _defaultNormal, color, _defaultUserData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final VectorT normal) {
      return addPoint(point, _defaultIntensity, normal, _defaultColor, _defaultUserData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal) {
      return addPoint(point, intensity, normal, _defaultColor, _defaultUserData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final VectorT normal,
                       final IColor color) {
      return addPoint(point, _defaultIntensity, normal, color, _defaultUserData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final long userData) {
      return addPoint(point, _defaultIntensity, _defaultNormal, _defaultColor, userData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final IColor color,
                       final long userData) {
      return addPoint(point, _defaultIntensity, _defaultNormal, color, userData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final long userData) {
      return addPoint(point, intensity, _defaultNormal, _defaultColor, userData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final VectorT normal,
                       final long userData) {
      return addPoint(point, _defaultIntensity, normal, _defaultColor, userData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData) {
      return addPoint(point, intensity, normal, color, userData, _defaultRowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final IVectorI2 rowColumn) {
      return addPoint(point, intensity, normal, color, userData, rowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final GroupT group) {
      return addPoint(point, intensity, normal, color, userData, _defaultRowColumn, group);

   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final IColor color,
                       final IVectorI2 rowColumn,
                       final GroupT group) {

      return addPoint(point, intensity, _defaultNormal, color, _defaultUserData, rowColumn, group);
   }


   @Override
   public int addPoint(final VectorT point,
                       final IVectorI2 rowColumn,
                       final GroupT group) {
      return addPoint(point, _defaultIntensity, _defaultNormal, _defaultColor, _defaultUserData, rowColumn, group);

   }


   @Override
   public int addPoint(final VectorT point,
                       final IVectorI2 rowColumn) {
      return addPoint(point, _defaultIntensity, _defaultNormal, _defaultColor, _defaultUserData, rowColumn, _defaultGroup);

   }


   @Override
   public int addPoint(final VectorT point,
                       final GroupT group) {
      return addPoint(point, _defaultIntensity, _defaultNormal, _defaultColor, _defaultUserData, _defaultRowColumn, group);

   }


   @Override
   public MutableT asMutableCopy() {

      final MutableT result = newEmptyContainer(size());

      for (int index = 0; index < size(); index++) {
         result.addPoint(getVertex(index));
      }

      return result;
   }


   @Override
   public void ensureCapacity(final int minCapacity) {

      final int newCapacity = ensureMinCapacity(minCapacity);

      if (newCapacity <= _capacity) {
         return;
      }

      _groupsBuffer.ensureCapacity(minCapacity);

      if (_rowColumnHandler != null) {
         _rowColumnHandler.growBuffer(newCapacity);
      }

      _capacity = newCapacity;
   }


   @SuppressWarnings("unchecked")
   @Override
   public MutableT reproject(final GProjection targetProjection) {

      if (_projection == targetProjection) {
         return (MutableT) this; // Compiler required cast. Force us to include @SuppressWarnings("unchecked")
      }

      if (hasNormals()) {
         throw new IllegalArgumentException("Reprojection of Vertices Containers with normals is not supported");
      }

      final MutableT result = newEmptyContainer(size(), targetProjection);

      final List<GroupT> groupsList = getGroups();
      final HashMap<GroupT, GroupT> groupsMap = new HashMap<GroupT, GroupT>(groupsList.size());
      for (final GroupT group : groupsList) {
         final GroupT projectedGroup = group.reproject(targetProjection);
         groupsMap.put(group, projectedGroup);
      }

      for (int i = 0; i < size(); i++) {
         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex = getVertex(i);
         final VectorT projectedPoint = vertex._point.reproject(_projection, targetProjection);
         final GroupT projectedGroup = groupsMap.get(vertex._group);

         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> projectedVertex = new IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>(
                  projectedPoint, vertex._intensity, vertex._normal, vertex._color, vertex._userData, vertex._rowColumn,
                  projectedGroup);

         result.addPoint(projectedVertex);
      }

      return result;

      //    setProjection(targetProjection);

      //      return collect(
      //               new ITransformer<VectorT, VectorT>() {
      //                  @Override
      //                  public VectorT transform(final VectorT point) {
      //                     return point.reproject(_projection, targetProjection);
      //                  }
      //               },
      //               new ITransformer<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>>() {
      //                  @Override
      //                  public IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> transform(final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex) {
      //                     final VectorT projectedPoint = vertex._point.reproject(_projection, targetProjection);
      //                     final GroupT projectedGroup = vertex._group.reproject(targetProjection);
      //                     return new IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>(projectedPoint, vertex._intensity,
      //                              vertex._normal, vertex._color, vertex._userData, vertex._rowColumn, projectedGroup);
      //                  }
      //               });
   }


   @Override
   public boolean sameContents(final IVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, ?> that) {

      if (!basicContentsCheck(that)) {
         return false;
      }

      final GStructuredVertexContainerWithDefaultsAbstract<VectorT, GroupT, ?> other = (GStructuredVertexContainerWithDefaultsAbstract<VectorT, GroupT, ?>) that;

      //      if (!this._defaultGroup.equals(other._defaultGroup)) {
      //         return false;
      //      }

      final int size = size();

      final List<GroupT> otherGroups = other.getGroups();

      final Iterator<GroupT> groupsIter = _groups.iterator();
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

      final GStructuredVertexContainerWithDefaultsAbstract other = (GStructuredVertexContainerWithDefaultsAbstract) that;

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

      final GStructuredVertexContainerWithDefaultsAbstract other = (GStructuredVertexContainerWithDefaultsAbstract) that;

      if (hasRowColumn() != other.hasRowColumn()) {
         return false;
      }

      return true;
   }


   @Override
   public void setRowColumn(final int index,
                            final IVectorI2 rowColumn) {
      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      if (_rowColumnHandler != null) {
         _rowColumnHandler.putRowColumn(index, rowColumn);
      }
      else {
         throw new RuntimeException("Invalid data setting for this container");
      }
   }


   @Override
   public void setGroup(final int index,
                        final GroupT group) {
      rangeCheck(index);
      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      getGroup(index).removeVertexIndex(index); // remove from old group list
      group.addVertexIndex(index); // add to new group list

      _groupsBuffer.set(index, group);

      changed();

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

      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      setBasicPointData(index, intensity, normal, color, userData);

      _pointsHandler.putVector(index, point.sub(group.getReferencePoint()));

      _rowColumnHandler.putRowColumn(index, rowColumn);

      setGroup(index, group);

      changed();

   }


   @Override
   public void setPoint(final int index,
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

      final GroupT group = _groupsBuffer.get(index);

      _pointsHandler.putVector(index, point.sub(group.getReferencePoint()));

      changed();
   }


   @Override
   public void setPoint(final int index,
                        final VectorT point) {
      rangeCheck(index);

      if (!isMutable()) {
         throw new RuntimeException("The container is closed, can't modify any point");
      }

      final GroupT group = _groupsBuffer.get(index);

      _pointsHandler.putVector(index, point.sub(group.getReferencePoint()));

      changed();
   }


   @Override
   public void setVertex(final int index,
                         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex) {

      setPoint(index, vertex._point, vertex._intensity, vertex._normal, vertex._color, vertex._userData, vertex._rowColumn,
               vertex._group);

   }


   @Override
   public GStructuredSubVertexContainer<VectorT, GroupT> asSubContainer(final int[] subIndices) {
      return new GStructuredSubVertexContainer<VectorT, GroupT>(this, subIndices);
   }


   @Override
   public MutableT collect(final IFunction<VectorT, VectorT> referencePointTransformer,
                           final IFunction<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> vertexTransformer) {

      final MutableT result = newEmptyContainer(size());

      final List<GroupT> groupsList = getGroups();
      final HashMap<GroupT, GroupT> groupsMap = new HashMap<GroupT, GroupT>(groupsList.size());
      for (final GroupT group : groupsList) {
         final GroupT transformedGroup = group.newEmptyContainer(group.size(),
                  referencePointTransformer.apply(group.getReferencePoint()));
         groupsMap.put(group, transformedGroup);
      }

      for (int i = 0; i < size(); i++) {
         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex = getVertex(i);
         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> transformedVertex = vertexTransformer.apply(vertex);
         final GroupT transformedGroup = groupsMap.get(vertex._group);

         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> collectedVertex = new IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>(
                  transformedVertex._point, transformedVertex._intensity, transformedVertex._normal, transformedVertex._color,
                  transformedVertex._userData, transformedVertex._rowColumn, transformedGroup);

         result.addPoint(collectedVertex);
      }

      return result;
   }


   @Override
   public MutableT select(final IPredicate<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> predicate) {


      final MutableT result = newEmptyContainer(size());

      final int size = size();
      for (int i = 0; i < size; i++) {
         final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> vertex = getVertex(i);
         if (predicate.evaluate(vertex)) {
            result.addPoint(vertex);
         }
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
   public MutableT asSortedContainer(final Comparator<IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> comparator) {
      final int[] indices = GCollections.rangeArray(0, size() - 1);

      GCollections.quickSort(indices, new IComparatorInt() {
         @Override
         public int compare(final int index1,
                            final int index2) {
            return comparator.compare(getVertex(index1), getVertex(index2));
         }
      });

      final MutableT result = newEmptyContainer(size());

      for (final int index : indices) {
         result.addPoint(getVertex(index));
      }

      return result;
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


   @SuppressWarnings("unchecked")
   @Override
   public MutableT asRawContainer() {

      if (_storageAsRawData) {
         return (MutableT) this; // Compiler required cast. Force us to include @SuppressWarnings("unchecked")
      }

      final MutableT result = newEmptyContainer(size(), true);

      final List<GroupT> groupsList = getGroups();

      for (final GroupT group : groupsList) {
         final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> groupVertices = getGroupVertices(group);

         final VectorT newReferencePoint = group.inverseTransform(group.getReferencePoint());
         final GroupT newGroup = group.newEmptyContainer(group.size(), newReferencePoint);

         for (int index = 0; index < groupVertices.size(); index++) {
            final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> newVertex = groupVertices.getRawVertex(index);

            result.addPoint(newVertex._point, newVertex._intensity, newVertex._normal, newVertex._color, newVertex._userData,
                     newVertex._rowColumn, newGroup);
         }
      }

      return result;
   }


   @SuppressWarnings("unchecked")
   @Override
   public MutableT asTransformedContainer() {

      if (!_storageAsRawData) {
         return (MutableT) this; // Compiler required cast. Force us to include @SuppressWarnings("unchecked")
      }

      final MutableT result = newEmptyContainer(size(), false);

      final List<GroupT> groupsList = getGroups();

      for (final GroupT group : groupsList) {
         final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> groupVertices = getGroupVertices(group);

         final VectorT newReferencePoint = group.transform(group.getReferencePoint());
         final GroupT newGroup = group.newEmptyContainer(group.size(), newReferencePoint);

         for (int index = 0; index < groupVertices.size(); index++) {
            final IStructuredVertexContainer.StructuredVertex<VectorT, GroupT> newVertex = groupVertices.getTransformedVertex(index);

            result.addPoint(newVertex._point, newVertex._intensity, newVertex._normal, newVertex._color, newVertex._userData,
                     newVertex._rowColumn, newGroup);
         }
      }

      return result;

      //      final MutableT result = newEmptyContainer(size());
      //
      //      for (int index = 0; index < size(); index++) {
      //         result.addPoint(getTransformedVertex(index));
      //      }
      //
      //      return result;
   }


   @Override
   public void trimToSize() {

      if (_capacity == _size) {
         return;
      }

      basicTrimToSize();

      if (_rowColumnHandler != null) {
         _rowColumnHandler.shrinkBuffer(_size);
      }

      _groupsBuffer.trimToSize();

      _capacity = _size;

   }


   @Override
   public IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> getGroupVertices(final GroupT group) {

      final List<GroupT> groupList = getGroups();

      if (!groupList.contains(group)) {
         return null;
      }

      final IListInt indexList = group.getIndexList();

      indexList.trimToSize();

      return new GStructuredSubVertexContainer<VectorT, GroupT>(this, indexList);


   }


}
