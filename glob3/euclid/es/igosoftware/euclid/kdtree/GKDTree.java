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

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.GStringUtils;


public final class GKDTree<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GLoggerObject {


   private final String                                _name;
   private final IVertexContainer<VectorT, VertexT, ?> _vertices;
   private final boolean                               _verbose;
   private final GKDNode<VectorT, VertexT>             _root;


   public GKDTree(final String name,
                  final IVertexContainer<VectorT, VertexT, ?> vertices,
                  final boolean verbose) {
      GAssert.isTrue(vertices.size() != 0, "Vertices is empty");
      GAssert.isTrue(!vertices.isMutable(), "Vertices is immutable");

      _name = name;
      _vertices = vertices;
      _verbose = verbose;

      final String nameMsg = _name == null ? "" : "\"" + _name + "\" ";
      final int verticesCount = _vertices.size();
      final GProgress progress = new GProgress(verticesCount + (verticesCount - 1)) {
         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            if (_verbose) {
               logInfo("  Creating kd-tree " + nameMsg + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
            }
         }
      };

      final GHolder<int[]> verticesIndexes = new GHolder<int[]>(getVerticesIndexes());

      final byte dimensions = vertices.dimensions();
      final double[][] axisValues = new double[dimensions][vertices.size()];

      for (int i = 0; i < vertices.size(); i++) {
         final VectorT point = vertices.getPoint(i);
         for (byte dim = 0; dim < dimensions; dim++) {
            axisValues[dim][i] = point.get(dim);
         }
      }

      final GAxisAlignedOrthotope<VectorT, ?> bounds = _vertices.getBounds();

      _root = GKDNode.createNode(this, null, _vertices, bounds, verticesIndexes, axisValues, progress);

      if (_verbose) {
         showStatistics();
      }
   }


   private int[] getVerticesIndexes() {
      return GCollections.rangeArray(0, _vertices.size() - 1);
   }


   private void showStatistics() {
      final GIntHolder maxDepth = new GIntHolder(Integer.MIN_VALUE);
      final GIntHolder minLeafDepth = new GIntHolder(Integer.MAX_VALUE);
      final GIntHolder leafNodesCounter = new GIntHolder(0);
      final GIntHolder innerNodesCounter = new GIntHolder(0);
      final GIntHolder totalChildrenCount = new GIntHolder(0);
      final GIntHolder innerNodesWithNoChildren = new GIntHolder(0);
      final GIntHolder innerNodesWithOneChildren = new GIntHolder(0);
      final GIntHolder verticesInLeafCount = new GIntHolder(0);

      final int[] splitAxisCounter = new int[_vertices.dimensions()];

      depthFirstAcceptVisitor(new IKDTreeVisitor<VectorT, VertexT>() {
         private void processNode(final GKDNode<VectorT, VertexT> node) {
            final int currentDepth = node.getDepth();
            if (currentDepth > maxDepth.get()) {
               maxDepth.set(currentDepth);
            }
         }


         @Override
         public void visitInnerNode(final GKDInnerNode<VectorT, VertexT> innerNode) {
            processNode(innerNode);

            innerNodesCounter.increment();

            final int childrenCount = innerNode.getChildrenCount();

            splitAxisCounter[innerNode.getSplitAxis()]++;

            totalChildrenCount.increment(childrenCount);

            if (childrenCount == 0) {
               innerNodesWithNoChildren.increment();
            }
            else if (childrenCount == 1) {
               innerNodesWithOneChildren.increment();
            }
         }


         @Override
         public void visitLeafNode(final GKDLeafNode<VectorT, VertexT> leafNode) {
            processNode(leafNode);

            leafNodesCounter.increment();

            verticesInLeafCount.increment(leafNode.getVerticesIndexes().length);

            final int currentDepth = leafNode.getDepth();
            if (currentDepth < minLeafDepth.get()) {
               minLeafDepth.set(currentDepth);
            }

         }


         @Override
         public void endVisiting(final GKDTree<VectorT, VertexT> kdtree) {
         }


         @Override
         public void startVisiting(final GKDTree<VectorT, VertexT> kdtree) {
         }

      });

      logInfo("---------------------------------------------------------------");

      if (_name != null) {
         logInfo(" KD-Tree \"" + _name + "\":");
      }
      final int verticesCount = _vertices.size();
      logInfo("  Vertices: " + verticesCount);

      logInfo("  Total Nodes: " + (innerNodesCounter.get() + leafNodesCounter.get()));
      logInfo("    Leaf: " + leafNodesCounter.get());
      logInfo("      Vertices Average: " + ((double) verticesInLeafCount.get() / leafNodesCounter.get()));
      logInfo("    Inner Nodes: " + innerNodesCounter.get());
      logInfo("      Child Count Average    : " + ((double) totalChildrenCount.get() / innerNodesCounter.get()));
      logInfo("      Split Axis Count       : " + Arrays.toString(splitAxisCounter));
      logInfo("      Inner with NO children : " + innerNodesWithNoChildren.get());
      logInfo("      Inner with ONE children: " + innerNodesWithOneChildren.get());

      logInfo("  Min Leaf Depth: " + minLeafDepth.get());
      logInfo("  Max Depth: " + maxDepth.get());
      logInfo("---------------------------------------------------------------");
   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   public void depthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor) {
      visitor.startVisiting(this);

      try {
         _root.depthFirstAcceptVisitor(visitor);
      }
      catch (final IKDTreeVisitor.AbortVisiting e) {}

      visitor.endVisiting(this);
   }


   public void breadthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor) {
      visitor.startVisiting(this);

      try {
         _root.breadthFirstAcceptVisitor(visitor);
      }
      catch (final IKDTreeVisitor.AbortVisiting e) {}

      visitor.endVisiting(this);
   }


   @Override
   public String toString() {
      return "GKDTree [name=" + _name + ", root=" + _root + ", vertices=" + _vertices.size() + "]";
   }


   public int getSize() {
      return _root.getSize();
   }


   public static void main(final String[] args) {
      System.out.println("GKDTree 0.1");
      System.out.println("-----------\n");

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = new GVertex3Container(
               GVectorPrecision.FLOAT, GColorPrecision.INT, GProjection.EUCLID, false, 0, false, null, false, null);

      vertices.addPoint(new GVector3F(0, 0, 0));

      vertices.addPoint(new GVector3F(1, 0, 0));
      vertices.addPoint(new GVector3F(0, 1, 0));
      vertices.addPoint(new GVector3F(0, 0, 2));

      vertices.addPoint(new GVector3F(1, 1, 0));
      vertices.addPoint(new GVector3F(1, 0, 1));
      vertices.addPoint(new GVector3F(0, 2, 1));

      vertices.addPoint(new GVector3F(1, 1, 1));

      vertices.makeImmutable();

      final GKDTree<IVector3, IVertexContainer.Vertex<IVector3>> tree = new GKDTree<IVector3, IVertexContainer.Vertex<IVector3>>(
               "Test", vertices, true);

      tree.depthFirstAcceptVisitor(new IKDTreeVisitor<IVector3, IVertexContainer.Vertex<IVector3>>() {

         @Override
         public void endVisiting(final GKDTree<IVector3, IVertexContainer.Vertex<IVector3>> kdtree) {
         }


         @Override
         public void startVisiting(final GKDTree<IVector3, IVertexContainer.Vertex<IVector3>> kdtree) {
         }


         @Override
         public void visitInnerNode(final GKDInnerNode<IVector3, IVertexContainer.Vertex<IVector3>> innerNode) {
            processNode(innerNode);
         }


         @Override
         public void visitLeafNode(final GKDLeafNode<IVector3, IVertexContainer.Vertex<IVector3>> leafNode) {
            processNode(leafNode);
         }


         private void processNode(final GKDNode<IVector3, IVertexContainer.Vertex<IVector3>> node) {
            System.out.println(GStringUtils.spaces(node.getDepth()) + node);
         }
      });
   }


   public IVertexContainer<VectorT, VertexT, ?> getOriginalVertices() {
      return _vertices;
   }

}
