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

import java.util.Arrays;
import java.util.List;

import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IListInt;


public abstract class GCommonSubVertexContainer<

VectorT extends IVector<VectorT, ?>,

VertexT extends IVertexContainer.Vertex<VectorT>,

MutableT extends GCommonSubVertexContainer<VectorT, VertexT, MutableT, OuterT>,

OuterT extends IVertexContainer<VectorT, VertexT, ?>

>
         extends
            GVertexContainerAbstract<VectorT, VertexT, MutableT> {


   protected final OuterT _outer;
   protected final int[]  _indices;


   public GCommonSubVertexContainer(final OuterT outer,
                                    final List<Integer> indices) {
      this(outer, GCollections.toIntArray(indices));
   }


   public GCommonSubVertexContainer(final OuterT outer,
                                    final IListInt indices) {
      this(outer, indices.toArray());
   }


   public GCommonSubVertexContainer(final OuterT outer,
                                    final int[] indices) {
      for (final int index : indices) {
         GAssert.isTrue(index < outer.size(), "index #" + index + " is out of range");
      }
      _outer = outer;
      _indices = indices;

      makeImmutable();
   }


   @Override
   public int addPoint(final VectorT point) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final IColor color) {
      throw new RuntimeException("Operation not supported");

   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color) {
      throw new RuntimeException("Operation not supported");

   }


   @Override
   public int addPoint(final VectorT point,
                       final IColor color) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final VectorT normal) {
      throw new RuntimeException("Operation not supported");

   }


   @Override
   public int addPoint(final VectorT point,
                       final VectorT normal,
                       final IColor color) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VertexT vertex) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final long userData) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final IColor color,
                       final long userData) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final long userData) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final VectorT normal,
                       final long userData) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData) {
      throw new RuntimeException("Operation not supported");
   }


   @Override
   public GColorPrecision colorPrecision() {
      return _outer.colorPrecision();
   }


   @Override
   public GProjection projection() {
      return _outer.projection();
   }


   @Override
   public IColor getColor(final int index) {
      return _outer.getColor(_indices[index]);
   }


   @Override
   public VertexT getVertex(final int index) {
      return _outer.getVertex(_indices[index]);
   }


   @Override
   public float getIntensity(final int index) {
      return _outer.getIntensity(_indices[index]);
   }


   @Override
   public long getUserData(final int index) {
      return _outer.getUserData(_indices[index]);
   }


   @Override
   public double getUserDataAsDouble(final int index) {
      return _outer.getUserDataAsDouble(_indices[index]);
   }


   @Override
   public VectorT getNormal(final int index) {
      return _outer.getNormal(_indices[index]);
   }


   @Override
   public VectorT getPoint(final int index) {
      return _outer.getPoint(_indices[index]);
   }


   @Override
   public void setColor(final int index,
                        final IColor color) {
      //throw new RuntimeException("Operation not supported");
      _outer.setColor(_indices[index], color);

   }


   @Override
   public void setIntensity(final int index,
                            final float intensity) {
      //throw new RuntimeException("Operation not supported");
      _outer.setIntensity(_indices[index], intensity);
   }


   @Override
   public void setNormal(final int index,
                         final VectorT normal) {
      //throw new RuntimeException("Operation not supported");
      _outer.setNormal(_indices[index], normal);

   }


   @Override
   public void setPoint(final int index,
                        final VectorT point) {
      //throw new RuntimeException("Operation not supported");
      _outer.setPoint(_indices[index], point);
   }


   @Override
   public void setUserData(final int index,
                           final long userData) {
      //throw new RuntimeException("Operation not supported");
      _outer.setUserData(_indices[index], userData);

   }


   @Override
   public void setUserDataFromDouble(final int index,
                                     final double userData) {
      //throw new RuntimeException("Operation not supported");
      _outer.setUserDataFromDouble(_indices[index], userData);
   }


   @Override
   public void setVertex(final int index,
                         final VertexT vertex) {
      //throw new RuntimeException("Operation not supported");
      _outer.setVertex(_indices[index], vertex);
   }


   @Override
   public void setPoint(final int index,
                        final VectorT point,
                        final float intensity,
                        final VectorT normal,
                        final IColor color,
                        final long userData) {
      //throw new RuntimeException("Operation not supported");
      _outer.setPoint(_indices[index], point, intensity, normal, color, userData);

   }


   //   @Override
   //   public void setProjection(final GProjection projection) {
   //      _outer.setProjection(projection);
   //   }


   @Override
   public boolean hasColors() {
      return _outer.hasColors();
   }


   @Override
   public boolean hasIntensities() {
      return _outer.hasIntensities();
   }


   @Override
   public boolean hasUserData() {
      return _outer.hasUserData();
   }


   @Override
   public boolean hasNormals() {
      return _outer.hasNormals();
   }


   @Override
   public int size() {
      return _indices.length;
   }


   @Override
   public GVectorPrecision vectorPrecision() {
      return _outer.vectorPrecision();
   }


   @Override
   public byte dimensions() {
      return _outer.dimensions();
   }


   @Override
   public void ensureCapacity(final int minCapacity) {
      _outer.ensureCapacity(minCapacity);
   }


   //   @Override
   //   protected String getStringName() {
   //      return "GSubVertexContainer";
   //   }


   @Override
   protected String getStringPostfix() {
      return ", outer=[" + _outer + "]";//+ ", indices=" + Arrays.toString(_indices);
   }


   @Override
   public void trimToSize() {
      _outer.trimToSize();
   }


   //   @Override
   //   public VectorT getReferencePoint() {
   //      return _outer.getReferencePoint();
   //   }


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> newEmptyContainer(final int initialCapacity) {
   //      return _outer.newEmptyContainer(initialCapacity);
   //   }


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> newEmptyContainer(final int initialCapacity,
   //                                                                  final VectorT referencePoint) {
   //      return _outer.newEmptyContainer(initialCapacity, referencePoint);
   //   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(_indices);
      result = prime * result + ((_outer == null) ? 0 : _outer.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (obj == null) {
         return false;
      }
      if (this == obj) {
         return true;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GCommonSubVertexContainer other = (GCommonSubVertexContainer) obj;
      if (!Arrays.equals(_indices, other._indices)) {
         return false;
      }
      if (_outer == null) {
         if (other._outer != null) {
            return false;
         }
      }
      else if (!_outer.equals(other._outer)) {
         return false;
      }
      return true;
   }


   //   @Override
   //   public GCommonSubVertexContainer<VectorT, VertexT> asSortedSubContainer(final Comparator<VertexT> comparator) {
   //
   //      final int[] sortedIndices = new int[_indices.length];
   //      System.arraycopy(_indices, 0, sortedIndices, 0, _indices.length);
   //
   //      GCollections.quickSort(sortedIndices, new IComparatorInt() {
   //         @Override
   //         public int compare(final int index1,
   //                            final int index2) {
   //            return comparator.compare(_outer.getVertex(index1), _outer.getVertex(index2));
   //         }
   //      });
   //
   //      return _outer.asSubContainer(sortedIndices);
   //   }


   //   @Override
   //   public GCommonSubVertexContainer<VectorT, VertexT, MutableT, OuterT> asMutableCopy() {
   //
   //      throw new RuntimeException("Operation not supported");
   //
   //      // return new GSubVertexContainer<VectorT>(_outer, _indices);
   //   }


}
