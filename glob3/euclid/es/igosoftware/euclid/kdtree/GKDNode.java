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


package es.igosoftware.euclid.kdtree;

import java.util.Arrays;
import java.util.LinkedList;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.IComparatorInt;


public abstract class GKDNode<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>> {


   static <VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>> GKDNode<VectorT, VertexT> createNode(final GKDTree<VectorT, VertexT> tree,
                                                                                                                                       final GKDInnerNode<VectorT, VertexT> parent,
                                                                                                                                       final IVertexContainer<VectorT, VertexT, ?> vertices,
                                                                                                                                       final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                                                                                                       final GHolder<int[]> verticesIndexes,
                                                                                                                                       final double[][] axisValues,
                                                                                                                                       final GProgress progress) {

      final int verticesIndexesSize = verticesIndexes.get().length;

      if (verticesIndexesSize == 0) {
         return null;
      }

      if (verticesIndexesSize == 1) {
         final int vertexIndex = verticesIndexes.get()[0];
         return createMonoLeafNode(tree, parent, vertexIndex, progress);
      }

      //      if (verticesIndexesSize <= 4) {
      //         return createMultipleLeafNode(tree, parent, verticesIndexes, progress);
      //      }

      // sort by largestAxis to find the median
      final byte currentLastLargestAxis = sort(vertices, verticesIndexes.get(), axisValues, bounds);

      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = bounds.subdividedByAxis(currentLastLargestAxis);

      final int medianI = (verticesIndexesSize / 2);

      final int[] leftVerticesIndexes = Arrays.copyOfRange(verticesIndexes.get(), 0, medianI);
      final int[] rightVerticesIndexes = Arrays.copyOfRange(verticesIndexes.get(), medianI + 1, verticesIndexesSize);

      final int medianVertexIndex = verticesIndexes.get()[medianI];
      verticesIndexes.clear();


      final GAxisAlignedOrthotope<VectorT, ?> leftBounds = childrenBounds[0];
      final GAxisAlignedOrthotope<VectorT, ?> rightBounds = childrenBounds[1];
      return creatInnerNode(tree, parent, vertices, medianVertexIndex, bounds, axisValues, currentLastLargestAxis,
               leftVerticesIndexes, leftBounds, rightVerticesIndexes, rightBounds, progress);
   }


   private static <VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>> GKDInnerNode<VectorT, VertexT> creatInnerNode(final GKDTree<VectorT, VertexT> tree,
                                                                                                                                                        final GKDInnerNode<VectorT, VertexT> parent,
                                                                                                                                                        final IVertexContainer<VectorT, VertexT, ?> vertices,
                                                                                                                                                        final int vertexIndex,
                                                                                                                                                        final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                                                                                                                        final double[][] axisValues,
                                                                                                                                                        final byte currentLastLargestAxis,
                                                                                                                                                        final int[] leftVerticesIndexes,
                                                                                                                                                        final GAxisAlignedOrthotope<VectorT, ?> leftBounds,
                                                                                                                                                        final int[] rightVerticesIndexes,
                                                                                                                                                        final GAxisAlignedOrthotope<VectorT, ?> rightBounds,
                                                                                                                                                        final GProgress progress) {
      if (parent == null) {
         return new GKDInnerNode<VectorT, VertexT>(tree, null, currentLastLargestAxis, vertexIndex, vertices, bounds, leftBounds,
                  new GHolder<int[]>(leftVerticesIndexes), rightBounds, new GHolder<int[]>(rightVerticesIndexes), axisValues,
                  progress) {
            @Override
            public GKDTree<VectorT, VertexT> getTree() {
               return tree;
            }
         };
      }


      return new GKDInnerNode<VectorT, VertexT>(tree, parent, currentLastLargestAxis, vertexIndex, vertices, bounds, leftBounds,
               new GHolder<int[]>(leftVerticesIndexes), rightBounds, new GHolder<int[]>(rightVerticesIndexes), axisValues,
               progress);
   }


   private static <VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>> GKDMonoLeafNode<VectorT, VertexT> createMonoLeafNode(final GKDTree<VectorT, VertexT> tree,
                                                                                                                                                               final GKDInnerNode<VectorT, VertexT> parent,
                                                                                                                                                               final int vertexIndex,
                                                                                                                                                               final GProgress progress) {
      if (parent == null) {
         return new GKDMonoLeafNode<VectorT, VertexT>(null, vertexIndex, progress) {
            @Override
            public GKDTree<VectorT, VertexT> getTree() {
               return tree;
            }
         };
      }

      return new GKDMonoLeafNode<VectorT, VertexT>(parent, vertexIndex, progress);
   }


   private static <VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>> byte sort(final IVertexContainer<VectorT, VertexT, ?> vertices,
                                                                                                                    final int[] verticesIndexes,
                                                                                                                    final double[][] axisValues,
                                                                                                                    final GAxisAlignedOrthotope<VectorT, ?> bounds) {

      final byte largestAxis = selectLargestAxis(bounds);
      //      System.out.println("largestAxis=" + largestAxis);

      final double[] largestAxisValue = axisValues[largestAxis];

      // sort by largestAxis to find the median
      GCollections.quickSort(verticesIndexes, new IComparatorInt() {
         @Override
         public int compare(final int i1,
                            final int i2) {
            final double axisValue1 = largestAxisValue[i1];
            final double axisValue2 = largestAxisValue[i2];

            final int comparison = Double.compare(axisValue1, axisValue2);
            if (comparison != 0) {
               return comparison;
            }

            // the comparison for the largest axis resolved to "equals", look for the other axis to determine the sort order
            final byte dimensions = vertices.dimensions();
            for (byte dim = 0; dim < dimensions; dim++) {
               if (dim == largestAxis) {
                  continue;
               }
               final double anotherAxisValue1 = axisValues[dim][i1];
               final double anotherAxisValue2 = axisValues[dim][i2];
               final int anotherAxisComparison = Double.compare(anotherAxisValue1, anotherAxisValue2);
               if (anotherAxisComparison != 0) {
                  return anotherAxisComparison;
               }
            }

            return 0;
         }
      });


      return largestAxis;
   }


   private static <VectorT extends IVector<VectorT, ?>> byte selectLargestAxis(final GAxisAlignedOrthotope<VectorT, ?> orthotope) {
      byte largestAxis = 0;
      double largestDistance = orthotope._upper.get((byte) 0) - orthotope._lower.get((byte) 0);

      final byte dimensions = orthotope.dimensions();
      for (byte axis = 1; axis < dimensions; axis++) {
         final double currentDistance = orthotope._upper.get(axis) - orthotope._lower.get(axis);
         if (currentDistance > largestDistance) {
            largestDistance = currentDistance;
            largestAxis = axis;
         }
      }

      return largestAxis;
   }


   public final GKDInnerNode<VectorT, VertexT> _parent;


   protected GKDNode(final GKDInnerNode<VectorT, VertexT> parent) {
      _parent = parent;
   }


   public GKDNode<VectorT, VertexT> getRoot() {
      if (_parent == null) {
         return this;
      }

      return _parent.getRoot();
   }


   public int getKey() {
      if (_parent == null) {
         return 1;
      }
      return (_parent.getKey() << 1) | _parent.getKeyForChild(this);
   }


   public final int getDepth() {
      return (_parent == null) ? 0 : _parent.getDepth() + 1;
   }


   public abstract int getSize();


   @Override
   public abstract String toString();


   void breadthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor) throws IKDTreeVisitor.AbortVisiting {
      final LinkedList<GKDNode<VectorT, VertexT>> queue = new LinkedList<GKDNode<VectorT, VertexT>>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GKDNode<VectorT, VertexT> current = queue.removeFirst();

         current.breadthFirstAcceptVisitor(visitor, queue);
      }
   }


   protected final String getKeyString() {
      final int key = getKey();
      return Integer.toString(key, 2) + " (" + key + ")";
   }


   protected String getParentKeyString() {
      return (_parent == null) ? "null" : _parent.getKeyString();
   }


   protected abstract void breadthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor,
                                                     final LinkedList<GKDNode<VectorT, VertexT>> queue)
                                                                                                       throws IKDTreeVisitor.AbortVisiting;


   protected abstract void depthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor)
                                                                                                  throws IKDTreeVisitor.AbortVisiting;


   public abstract boolean isLeaf();


   protected IVertexContainer<VectorT, VertexT, ?> getOriginalVertices() {
      return getTree().getOriginalVertices();
   }


   public GKDTree<VectorT, VertexT> getTree() {
      return getRoot().getTree();
   }


}
