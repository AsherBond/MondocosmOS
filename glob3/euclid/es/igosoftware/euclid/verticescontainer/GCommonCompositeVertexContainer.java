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
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.IEvaluator;
import es.igosoftware.util.IFunction;


public abstract class GCommonCompositeVertexContainer<

VectorT extends IVector<VectorT, ?>,

VertexT extends IVertexContainer.Vertex<VectorT>,

MutableT extends GCommonCompositeVertexContainer<VectorT, VertexT, MutableT, ChildrenT>,

ChildrenT extends IVertexContainer<VectorT, VertexT, ?>

>
         extends
            GVertexContainerAbstract<VectorT, VertexT, MutableT> {


   /////////////////////////////////////////////////////////////////////////////////////////////////

   protected final List<ChildrenT> _children;


   public GCommonCompositeVertexContainer() {
      _children = new ArrayList<ChildrenT>();
   }


   public GCommonCompositeVertexContainer(final int initialChildrenCapacity) {
      _children = new ArrayList<ChildrenT>(initialChildrenCapacity);
   }


   public GCommonCompositeVertexContainer(final List<ChildrenT> children) {
      _children = new ArrayList<ChildrenT>(children.size());
      for (final ChildrenT child : children) {
         addChild(child);
      }
   }


   public void makeAllImmutable() {
      makeImmutable();

      GCollections.evaluate(_children, new IEvaluator<ChildrenT>() {
         //GCollections.evaluate(_children, new IEvaluator<IVertexContainer<VectorT, VertexT, ?>>() {
         @Override
         public void evaluate(final ChildrenT element) {
            if (element instanceof GCommonCompositeVertexContainer) {
               ((GCommonCompositeVertexContainer<?, ?, ?, ?>) element).makeAllImmutable();
            }
            else {
               element.makeImmutable();
            }
         }
      });
   }


   @Override
   public boolean isMutable() {
      return super.isMutable() && GCollections.allSatisfy(_children, new GPredicate<ChildrenT>() {
         @Override
         public boolean evaluate(final ChildrenT element) {
            return element.isMutable();
         }
      });
   }


   public void addChild(final ChildrenT child) {
      if (!super.isMutable()) {
         throw new RuntimeException("The container is closed, can't add more children");
      }

      if (!_children.isEmpty()) {
         final ChildrenT reference = _children.get(0);

         if (!child.sameShapeThan(reference)) {
            throw new IllegalArgumentException("invalid child type");
         }
      }

      _children.add(child);
      changed();
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
      return _children.get(0).colorPrecision();
   }


   @Override
   public GProjection projection() {
      return _children.get(0).projection();
   }


   //   @Override
   //   public ChildrenT composedWith(final ChildrenT container) {
   //      addChild(container);
   //      return this;
   //   }


   @Override
   public IColor getColor(final int index) {
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getColor(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public VertexT getVertex(final int index) {
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getVertex(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public float getIntensity(final int index) {
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getIntensity(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public long getUserData(final int index) {
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getUserData(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public double getUserDataAsDouble(final int index) {
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getUserDataAsDouble(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public VectorT getNormal(final int index) {
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getNormal(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public VectorT getPoint(final int index) {
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            return child.getPoint(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setColor(final int index,
                        final IColor color) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setColor(childIndex, color);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);

   }


   @Override
   public void setIntensity(final int index,
                            final float intensity) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setIntensity(childIndex, intensity);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setNormal(final int index,
                         final VectorT normal) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setNormal(childIndex, normal);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setPoint(final int index,
                        final VectorT point) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setPoint(childIndex, point);
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
                        final long userData) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setPoint(childIndex, point, intensity, normal, color, userData);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setUserData(final int index,
                           final long userData) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setUserData(childIndex, userData);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setUserDataFromDouble(final int index,
                                     final double userData) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setUserDataFromDouble(childIndex, userData);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public void setVertex(final int index,
                         final VertexT vertex) {
      //throw new RuntimeException("Operation not supported");
      int acumIndex = 0;
      for (final ChildrenT child : _children) {
         final int childIndex = index - acumIndex;
         final int childSize = child.size();
         if (childIndex < childSize) {
            child.setVertex(childIndex, vertex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   //   @Override
   //   public void setProjection(final GProjection projection) {
   //
   //      for (final ChildrenT child : _children) {
   //         child.setProjection(projection);
   //      }
   //
   //   }


   @Override
   public boolean hasColors() {
      return _children.get(0).hasColors();
   }


   @Override
   public boolean hasIntensities() {
      return _children.get(0).hasIntensities();
   }


   @Override
   public boolean hasUserData() {
      return _children.get(0).hasUserData();
   }


   @Override
   public boolean hasNormals() {
      return _children.get(0).hasNormals();
   }


   @Override
   public int size() {
      int size = 0;
      for (final ChildrenT child : _children) {
         size += child.size();
      }
      return size;
   }


   @Override
   public GVectorPrecision vectorPrecision() {
      return _children.get(0).vectorPrecision();
   }


   @Override
   public byte dimensions() {
      return _children.get(0).dimensions();
   }


   @Override
   public void ensureCapacity(final int minCapacity) {
      // do nothing
   }


   //   @Override
   //   protected String getStringName() {
   //      return "GCompositeVertexContainer";
   //   }


   @Override
   protected String getStringPostfix() {
      return ", children=" + _children.size();
   }


   @Override
   public void trimToSize() {
      for (final ChildrenT child : _children) {
         child.trimToSize();
      }
   }


   public ChildrenT getChild(final int index) {
      return _children.get(index);
   }


   public int childrenCount() {
      return _children.size();
   }


   @Override
   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      final List<GAxisAlignedOrthotope<VectorT, ?>> childrenBounds = GCollections.concurrentCollect(_children,
               new IFunction<ChildrenT, GAxisAlignedOrthotope<VectorT, ?>>() {
                  @Override
                  public GAxisAlignedOrthotope<VectorT, ?> apply(final ChildrenT element) {
                     return element.getBounds();
                  }
               });

      return GAxisAlignedOrthotope.merge(childrenBounds);
   }


   @Override
   public IVertexContainer.WeightedVertex<VectorT> getAverage() {
      final List<IVertexContainer.WeightedVertex<VectorT>> childrenAverages = GCollections.concurrentCollect(_children,
               new IFunction<ChildrenT, IVertexContainer.WeightedVertex<VectorT>>() {
                  @Override
                  public IVertexContainer.WeightedVertex<VectorT> apply(final ChildrenT element) {
                     return element.getAverage();
                  }
               });

      return IVertexContainer.WeightedVertex.getAverage(childrenAverages);
   }


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> newEmptyContainer(final int initialCapacity) {
   //      return _children.get(0).newEmptyContainer(initialCapacity);
   //   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_children == null) ? 0 : _children.hashCode());
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
      final GCommonCompositeVertexContainer other = (GCommonCompositeVertexContainer) obj;
      if (_children == null) {
         if (other._children != null) {
            return false;
         }
      }
      else if (!_children.equals(other._children)) {
         return false;
      }
      return true;
   }


   //   @Override
   //   public GCommonCompositeVertexContainer<VectorT, VertexT, MutableT, ChildrenT> asMutableCopy() {
   //
   //      throw new RuntimeException("Operation not supported");
   //
   //      //      final List<IVertexContainer<VectorT, VertexT, ?>> childrenList = new ArrayList<IVertexContainer<VectorT, VertexT, ?>>();
   //      //
   //      //      for (final IVertexContainer<VectorT, VertexT, ?> child : _children) {
   //      //         final IVertexContainer<VectorT, VertexT, ?> childResult = newEmptyContainer(child.size(), child.getReferencePoint());
   //      //
   //      //         for (int index = 0; index < child.size(); index++) {
   //      //            childResult.addPoint(child.getVertex(index));
   //      //         }
   //      //         childrenList.add(childResult);
   //      //      }
   //      //
   //      //      return new GCompositeVertexContainer<VectorT>(childrenList);
   //   }


}
