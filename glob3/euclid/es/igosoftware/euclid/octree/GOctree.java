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


package es.igosoftware.euclid.octree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IBounds3D;
import es.igosoftware.euclid.vector.GMutableVector3;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer.Vertex;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GProgress;


public final class GOctree
         extends
            GOTComponent {


   public static interface CreateLeafPolicy {
      public void beforeStart();


      public boolean acceptLeafCreation(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                        final int[] verticesIndexes);


      public void afterEnd();
   }


   public static interface DuplicatesPolicy {
      public int[] removeDuplicates(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                    final int[] verticesIndexes);
   }


   /**
    * Public interface to define an NodesCreationPolicy.<br/>
    * <br/>
    * <b>IMPORTANT:</b> The <code>getNodeBounds()</code> method must return non-overlapping boxes.
    */
   public static interface NodesCreationPolicy {

      /**
       * Creates an auxiliary object that will be created one time per GOTInnerNode and passed as a parameter for the rest of the
       * methods.<br/>
       * <br/>
       * For a "classical" octree this object is a pivot-point where the bounds-partition occurs.
       * 
       * @param i
       */
      public Object createAuxiliaryObject(final GOctree octree,
                                          final int[] verticesIndexes,
                                          final GAxisAlignedBox innerBounds,
                                          final int depth);


      /**
       * Answer the maximum number of nodes, for a "classical" octree is always 8
       */
      public int getMaxNodes(final Object auxiliaryObject);


      /**
       * Calculates the bounds for the given node<br/>
       * <br/>
       * <b>IMPORTANT:</b> The method must return non-overlapping boxes.<br/>
       */
      public GAxisAlignedBox getNodeBounds(final Object auxiliaryObject,
                                           final GAxisAlignedBox innerBounds,
                                           final byte nodeKey);


      /**
       * Calculates the node-key for the given vertexIndex
       */
      public byte getNodeKey(final Object auxiliaryObject,
                             final GOctree octree,
                             final int vertexIndex);


      //      public void showStatistics();


      public String getStatistics();
   }


   public static class DynamicAxisNodesCreationPolicy
            implements
               NodesCreationPolicy {
      private final boolean _splitByOneAxis;

      private int           _xySubdivisions  = 0;
      private int           _xyzSubdivisions = 0;

      private int           _xSubdivisions   = 0;
      private int           _ySubdivisions   = 0;
      private int           _zSubdivisions   = 0;


      public DynamicAxisNodesCreationPolicy(final boolean splitByOneAxis) {
         _splitByOneAxis = splitByOneAxis;
      }


      @Override
      public Object createAuxiliaryObject(final GOctree octree,
                                          final int[] verticesIndexes,
                                          final GAxisAlignedBox innerBounds,
                                          final int depth) {
         final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> subVertices = octree.getOriginalVertices().asSubContainer(
                  verticesIndexes);
         final IVector3 average = subVertices.getAverage()._point;
         final GAxisAlignedBox bounds = (GAxisAlignedBox) subVertices.getBounds();
         //            final GAxisAlignedBox bounds = innerBounds;
         final IVector3 boundsExtent = getBoundsExtent(bounds);

         if (_splitByOneAxis) {
            final double w = boundsExtent.x();
            final double h = boundsExtent.y();
            final double d = boundsExtent.z();
            final boolean subdividedByX = (w > h) && (w > d);
            final boolean subdividedByY = (h > w) && (h > d);
            final boolean subdividedByZ = (d > w) && (d > h);
            if (subdividedByX) {
               _xSubdivisions++;
               return bounds.subdividedByX(average.x());
            }
            else if (subdividedByY) {
               _ySubdivisions++;
               return bounds.subdividedByY(average.y());
            }
            else if (subdividedByZ) {
               _zSubdivisions++;
               return bounds.subdividedByZ(average.z());
            }
            else {
               System.out.println(">>>>>>> can't split by axis, bound=" + bounds + ", boundsExtent=" + boundsExtent
                                  + " --> forcing X division");

               _xSubdivisions++;
               return bounds.subdividedByX(average.x());
            }
         }


         final double minXYExtent = Math.min(boundsExtent.x(), boundsExtent.y());

         if (minXYExtent >= boundsExtent.z()) {
            _xySubdivisions++;
            return bounds.subdividedByXY(average.asVector2());
         }

         _xyzSubdivisions++;
         return bounds.subdividedAt(average);

      }


      protected IVector3 getBoundsExtent(final GAxisAlignedBox bounds) {
         return bounds._extent;
      }


      @Override
      public int getMaxNodes(final Object auxiliaryObject) {
         return _splitByOneAxis ? 2 : 8;
      }


      @Override
      public byte getNodeKey(final Object auxiliaryObject,
                             final GOctree octree,
                             final int vertexIndex) {
         final GAxisAlignedBox[] bounds = (GAxisAlignedBox[]) auxiliaryObject;

         final IVector3 point = octree.getPoint(vertexIndex);
         for (int i = 0; i < bounds.length; i++) {
            if (bounds[i].contains(point)) {
               return (byte) i;
            }
         }

         throw new RuntimeException("No bounds contains the point=" + point + " Bounds=" + Arrays.toString(bounds));
      }


      @Override
      public GAxisAlignedBox getNodeBounds(final Object auxiliaryObject,
                                           final GAxisAlignedBox innerBounds,
                                           final byte nodeKey) {
         final GAxisAlignedBox[] bounds = (GAxisAlignedBox[]) auxiliaryObject;
         return bounds[nodeKey];
      }


      @Override
      public String getStatistics() {
         return "XY Subdivisions=" + _xySubdivisions + ", XYZ Subdivisions=" + _xyzSubdivisions + ", X Subdivisions="
                + _xSubdivisions + ", Y Subdivisions=" + _ySubdivisions + ", Z Subdivisions=" + _zSubdivisions;
      }
   }


   private static class DefaultNodesCreationPolicy
            implements
               NodesCreationPolicy {

      @Override
      public Object createAuxiliaryObject(final GOctree octree,
                                          final int[] verticesIndexes,
                                          final GAxisAlignedBox innerBounds,
                                          final int depth) {
         return innerBounds._center.asDouble();
      }


      @Override
      public int getMaxNodes(final Object pivot) {
         return 8;
      }


      @Override
      public GAxisAlignedBox getNodeBounds(final Object auxiliaryObject,
                                           final GAxisAlignedBox innerBounds,
                                           final byte nodeKey) {
         final IVector3 pivot = (IVector3) auxiliaryObject;
         final int xKey = nodeKey & 1;
         final int yKey = nodeKey & 2;
         final int zKey = nodeKey & 4;

         final IVector3 lower = innerBounds._lower;
         final IVector3 upper = innerBounds._upper;

         final double nodeLowerX = (xKey == 0) ? lower.x() : pivot.x();
         final double nodeLowerY = (yKey == 0) ? lower.y() : pivot.y();
         final double nodeLowerZ = (zKey == 0) ? lower.z() : pivot.z();

         double nodeUpperX = (xKey == 0) ? pivot.x() : upper.x();
         double nodeUpperY = (yKey == 0) ? pivot.y() : upper.y();
         double nodeUpperZ = (zKey == 0) ? pivot.z() : upper.z();

         if (nodeUpperX < upper.x()) {
            nodeUpperX = GMath.previousDown(nodeUpperX);
         }
         if (nodeUpperY < upper.y()) {
            nodeUpperY = GMath.previousDown(nodeUpperY);
         }
         if (nodeUpperZ < upper.z()) {
            nodeUpperZ = GMath.previousDown(nodeUpperZ);
         }

         return new GAxisAlignedBox(new GVector3D(nodeLowerX, nodeLowerY, nodeLowerZ), new GVector3D(nodeUpperX, nodeUpperY,
                  nodeUpperZ));
      }


      @Override
      public byte getNodeKey(final Object auxiliaryObject,
                             final GOctree octree,
                             final int vertexIndex) {
         final IVector3 pivot = (IVector3) auxiliaryObject;

         final IVector3 point = octree.getPoint(vertexIndex);

         final int nodeX = (point.x() < pivot.x()) ? 0 : 1;
         final int nodeY = (point.y() < pivot.y()) ? 0 : 2;
         final int nodeZ = (point.z() < pivot.z()) ? 0 : 4;

         return (byte) (nodeX | nodeY | nodeZ);
      }


      @Override
      public String getStatistics() {
         return null;
      }

   }


   public static class Parameters {
      private final int                         _maxDepth;
      private final double                      _maxLeafSideLength;
      private final int                         _maxLeafVertices;
      private final boolean                     _verbose;
      private final boolean                     _multithreading;
      private final boolean                     _validate;

      private final GOctree.DuplicatesPolicy    _duplicatesPolicy;
      private final GOctree.NodesCreationPolicy _nodesCreationPolicy;
      private final boolean                     _saveBoundsOnLeafs;


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices) {
         this(-1, maxLeafSideLength, maxLeafVertices, false, true, true, true, null, null);
      }


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose,
                        final boolean multithreading) {
         this(-1, maxLeafSideLength, maxLeafVertices, verbose, multithreading, false, true, null, null);
      }


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose,
                        final boolean multithreading,
                        final GOctree.DuplicatesPolicy duplicatesPolicy) {
         this(-1, maxLeafSideLength, maxLeafVertices, verbose, multithreading, false, true, duplicatesPolicy, null);
      }


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose,
                        final boolean multithreading,
                        final boolean validate,
                        final GOctree.DuplicatesPolicy duplicatesPolicy) {
         this(-1, maxLeafSideLength, maxLeafVertices, verbose, multithreading, validate, true, duplicatesPolicy, null);
      }


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose) {
         this(-1, maxLeafSideLength, maxLeafVertices, verbose, true, false, true, null, null);
      }


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose,
                        final GOctree.NodesCreationPolicy nodesCreationPolicy) {
         this(-1, maxLeafSideLength, maxLeafVertices, verbose, true, false, true, null, nodesCreationPolicy);
      }


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose,
                        final GOctree.DuplicatesPolicy duplicatesPolicy) {
         this(-1, maxLeafSideLength, maxLeafVertices, verbose, true, false, true, duplicatesPolicy, null);
      }


      public Parameters(final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose,
                        final boolean multithreading,
                        final boolean validate) {
         this(-1, maxLeafSideLength, maxLeafVertices, verbose, multithreading, validate, true, null, null);
      }


      public Parameters(final int maxDepth,
                        final double maxLeafSideLength,
                        final int maxLeafVertices,
                        final boolean verbose,
                        final boolean multithreading,
                        final boolean validate,
                        final boolean saveBoundsOnLeafs,
                        final GOctree.DuplicatesPolicy duplicatesPolicy,
                        final GOctree.NodesCreationPolicy nodesCreationPolicy) {
         _maxDepth = (maxDepth <= 0) ? -1 : maxDepth;
         _maxLeafSideLength = (maxLeafSideLength <= 0) ? Double.POSITIVE_INFINITY : maxLeafSideLength;
         _maxLeafVertices = maxLeafVertices;
         _verbose = verbose;
         _multithreading = multithreading;
         _validate = validate;
         _saveBoundsOnLeafs = saveBoundsOnLeafs;

         _duplicatesPolicy = duplicatesPolicy;
         _nodesCreationPolicy = (nodesCreationPolicy == null) ? new GOctree.DefaultNodesCreationPolicy() : nodesCreationPolicy;
      }


      @Override
      public String toString() {
         return "maxDepth=" + _maxDepth + ", maxLeafSideLength=" + _maxLeafSideLength + ", maxLeafVertices=" + _maxLeafVertices
                + ", multithreading=" + _multithreading + ", duplicatesPolicy=" + _duplicatesPolicy + ", validate=" + _validate
                + ", verbose=" + _verbose;
      }


      public double getMaxLeafSideLength() {
         return _maxLeafSideLength;
      }


      public int getMaxLeafVertices() {
         return _maxLeafVertices;
      }


      public boolean isMultithreading() {
         return _multithreading;
      }


      public GOctree.NodesCreationPolicy getNodesCreationPolicy() {
         return _nodesCreationPolicy;
      }


      public boolean saveBoundsOnLeafs() {
         return _saveBoundsOnLeafs;
      }


      public int getMaxDepth() {
         return _maxDepth;
      }

   }


   private static final Object                                                    STATISTICS_MUTEX   = new Object();


   private final String                                                           _name;
   private final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> _vertices;
   //   private final int                              _childrenCounter   = 0;
   private final GOTInnerNode                                                     _root;
   private final GAxisAlignedBox                                                  _bounds;
   private final AtomicInteger                                                    _duplicatesRemoved = new AtomicInteger();
   //GProgress                                      _progress;
   private final GOctree.Parameters                                               _parameters;
   private final GAxisAlignedBox                                                  _pointsBounds;


   public GOctree(final String name,
                  final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                  final GOctree.Parameters parameters) {
      this(name, vertices, null, null, parameters);
   }


   public GOctree(final String name,
                  final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                  final GAxisAlignedBox bounds,
                  final Parameters parameters) {
      this(name, vertices, bounds, null, parameters);
   }


   public GOctree(final String name,
                  final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                  final GAxisAlignedBox bounds,
                  final GOctree.CreateLeafPolicy createLeafPolicy,
                  final GOctree.Parameters parameters) {
      GAssert.isTrue(!vertices.isMutable(), "Vertices is mutable");

      _vertices = vertices;
      _name = name;
      _parameters = parameters;

      final String nameMsg = _name == null ? "" : "\"" + _name + "\" ";
      logInfo("Creating octree " + nameMsg + "with " + _parameters);


      if (createLeafPolicy != null) {
         createLeafPolicy.beforeStart();
      }

      // put the indices in a holder, so the inner can clear it after using. it reduces the needed memory while creating the octree
      final GHolder<int[]> verticesIndexes = new GHolder<int[]>(getVerticesIndexes());

      final GProgress progress = new GProgress(verticesIndexes.get().length) {
         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            if (_parameters._verbose) {
               logInfo("  Creating octree " + nameMsg + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
            }
         }
      };

      _pointsBounds = (GAxisAlignedBox) _vertices.getBounds();

      //final int[] verticesIndexes = getVerticesIndexes();
      _bounds = initializeBounds(bounds);
      //      logInfo("Octree Bounds: " + _bounds);
      try {
         _root = new GOTInnerNode(GOctree.this, null, verticesIndexes, _bounds, createLeafPolicy, parameters, progress) {
            @Override
            public GOctree getOctree() {
               return GOctree.this;
            }
         };
      }
      catch (final InterruptedException e) {
         throw new RuntimeException(e);
      }
      catch (final ExecutionException e) {
         throw new RuntimeException(e);
      }

      if (_parameters._verbose) {
         showStatistics();
      }

      if (createLeafPolicy != null) {
         createLeafPolicy.afterEnd();
      }

      validate();
   }


   @Override
   public int[] getVerticesIndexes() {
      return GCollections.rangeArray(0, _vertices.size() - 1);
   }


   @Override
   public GOTInnerNode getRoot() {
      return _root;
   }


   @Override
   public int getLeafNodesCount() {
      return _root.getLeafNodesCount();
   }


   @Override
   public int getInnerNodesCount() {
      return _root.getInnerNodesCount();
   }


   @Override
   public int getVerticesIndexesCount() {
      return _root.getVerticesIndexesCount();
   }


   public IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> getVerticesInRegion(final IBounds3D<?> region) {
      return _vertices.asSubContainer(getVerticesIndexesInRegion(region));
   }


   @Override
   public int[] getVerticesIndexesInRegion(final IBounds3D<?> region) {
      //return _verticesIndexesInRegionCache.get(region);
      return _root.getVerticesIndexesInRegion(region);
   }


   // calculate a perfect cube bounds that includes all the points
   private GAxisAlignedBox initializeBounds(final GAxisAlignedBox bounds) {
      //      logInfo("Points Bounds: " + pointsBounds);

      if (bounds != null) {
         if (_pointsBounds.isFullInside(bounds)) {
            return bounds;
         }
         throw new IllegalArgumentException("The given bounds is not big enough to hold all the vertices");
      }

      if (_parameters._maxLeafSideLength == Double.POSITIVE_INFINITY) {
         // no maxLeafSideLength, no need to round the bounds
         return _pointsBounds;
      }

      final IVector3 pointsBoundsExtent = _pointsBounds._extent;

      final double temp = Math.max(pointsBoundsExtent.x(), Math.max(pointsBoundsExtent.y(), pointsBoundsExtent.z()));
      final double maxSide = roundToDoubles(temp, _parameters._maxLeafSideLength);
      // System.out.println("\n **** temp=" + temp + ", maxSide=" + maxSide + ", multiplo=" + multiplo + "\n");

      final IVector3 newUpper = _pointsBounds._lower.add(maxSide);
      final GAxisAlignedBox bigBox = new GAxisAlignedBox(_pointsBounds._lower, newUpper);

      // center bigBox
      final IVector3 delta = bigBox.getCenter().sub(_pointsBounds.getCenter());
      return bigBox.translatedBy(delta.negated());
   }


   private static double roundToDoubles(final double maxSide,
                                        final double multiplo) {
      double currentPower = multiplo;

      if (multiplo > 0) {
         while (currentPower < maxSide) {
            currentPower *= 2;
         }
      }
      else {
         while (currentPower < maxSide) {
            currentPower /= 2;
         }
      }

      return currentPower;
   }


   public float getAverageVerticesPerLeaf() {
      final AtomicInteger totalPointsInLeafs = new AtomicInteger(0);
      final AtomicInteger leafsCount = new AtomicInteger(0);

      depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            totalPointsInLeafs.addAndGet(leaf.getVerticesIndexesCount());
            leafsCount.incrementAndGet();
         }


         @Override
         public void finishedInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void finishedOctree(final GOctree octree) {
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void visitOctree(final GOctree octree) {
         }
      });

      return totalPointsInLeafs.floatValue() / leafsCount.get();
   }


   public int getMaxVerticesPerLeaf() {
      final AtomicInteger maxPointsCount = new AtomicInteger(Integer.MIN_VALUE);

      depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            final int pointsCount = leaf.getVerticesIndexesCount();

            if (pointsCount > maxPointsCount.get()) {
               maxPointsCount.set(pointsCount);
            }
         }


         @Override
         public void finishedInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void finishedOctree(final GOctree octree) {
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void visitOctree(final GOctree octree) {
         }
      });

      return maxPointsCount.get();
   }


   public void showStatistics() {
      synchronized (GOctree.STATISTICS_MUTEX) {

         final AtomicInteger totalPoints = new AtomicInteger(0);
         final AtomicInteger maxPointsCount = new AtomicInteger(Integer.MIN_VALUE);
         final AtomicInteger minPointsCount = new AtomicInteger(Integer.MAX_VALUE);

         final AtomicInteger totalDepth = new AtomicInteger(0);
         final AtomicInteger maxDepth = new AtomicInteger(Integer.MIN_VALUE);
         final AtomicInteger minDepth = new AtomicInteger(Integer.MAX_VALUE);

         final IVector3 averageLeafExtent = new GMutableVector3<GVector3D>(GVector3D.ZERO);


         final int[] totalChildrenCount = new int[] {
            0
         };

         final double[] totalDensity = new double[1];
         final double[] minDensity = {
            Double.MAX_VALUE
         };
         final double[] maxDensity = {
            Double.MIN_VALUE
         };

         depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
            @Override
            public void visitLeafNode(final GOTLeafNode leaf) {
               final int pointsCount = leaf.getVerticesIndexesCount();
               totalPoints.addAndGet(pointsCount);
               if (pointsCount > maxPointsCount.get()) {
                  maxPointsCount.set(pointsCount);
               }
               if (pointsCount < minPointsCount.get()) {
                  minPointsCount.set(pointsCount);
               }

               final int depth = leaf.getDepth();
               totalDepth.addAndGet(depth);
               if (depth > maxDepth.get()) {
                  maxDepth.set(depth);
               }
               if (depth < minDepth.get()) {
                  minDepth.set(depth);
               }

               final GAxisAlignedBox leafBounds = leaf.getBounds();
               if (leafBounds != null) {
                  averageLeafExtent.add(leafBounds.getExtent());

                  final IVector3 leafExtent = leafBounds._extent;
                  final double leafArea = leafExtent.x() * leafExtent.y() * leafExtent.z();
                  final double leafDensity = leaf.getVerticesIndexesCount() / leafArea;

                  totalDensity[0] = totalDensity[0] + leafDensity;
                  if (leafDensity > maxDensity[0]) {
                     maxDensity[0] = leafDensity;
                  }
                  if (leafDensity < minDensity[0]) {
                     minDensity[0] = leafDensity;
                  }
               }
            }


            @Override
            public void finishedInnerNode(final GOTInnerNode inner) {
            }


            @Override
            public void finishedOctree(final GOctree octree) {
            }


            @Override
            public void visitInnerNode(final GOTInnerNode inner) {
               final int childrenCount = inner.getNodesCount();
               totalChildrenCount[0] += childrenCount;
            }


            @Override
            public void visitOctree(final GOctree octree) {
            }
         });

         final int leafsCount = getLeafNodesCount();
         final int innersCount = getInnerNodesCount();

         averageLeafExtent.div(leafsCount);

         logInfo("---------------------------------------------------------------");

         if (_name != null) {
            logInfo(" Octree \"" + _name + "\":");
         }

         logInfo("  Total points: " + totalPoints);
         if (_duplicatesRemoved.get() != 0) {
            logInfo("  Duplicates Removed: " + _duplicatesRemoved.get());
         }
         logInfo("  Points Bounds: " + _pointsBounds);
         logInfo("  Points Extents: " + _pointsBounds._extent);

         logInfo("  Bounds: " + _bounds);
         logInfo("  Extents: " + _bounds._extent);

         logInfo("  Inners: " + innersCount);
         logInfo("  Nodes per Inner: (Average=" + ((float) totalChildrenCount[0] / innersCount) + ")");
         logInfo("  Leafs: " + leafsCount);

         logInfo("  Leaf density (points/m3): Max=" + maxDensity[0] + ", Min=" + minDensity[0] + ", Average="
                 + (totalDensity[0] / leafsCount));
         logInfo("  Average leaf extent: " + averageLeafExtent);
         logInfo("  Points per leaf: Max=" + maxPointsCount + ", Min=" + minPointsCount + ", Average="
                 + ((double) totalPoints.get() / leafsCount));
         logInfo("  Depth: Max=" + maxDepth + ", Min=" + minDepth + ", Average=" + ((double) totalDepth.get() / leafsCount));

         final String nodesCreationPolicyStatistics = _parameters._nodesCreationPolicy.getStatistics();
         if ((nodesCreationPolicyStatistics != null) && !nodesCreationPolicyStatistics.isEmpty()) {
            logInfo("");
            logInfo("  Nodes Creation Policy Statistics: " + nodesCreationPolicyStatistics);
            //      _parameters._nodesCreationPolicy.showStatistics();
         }

         logInfo("---------------------------------------------------------------");

      }
   }


   private void validate() {
      if (!_parameters._validate) {
         return;
      }

      logInfo("Validating " + getLeafNodesCount() + " leafs...");

      depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
         @Override
         public void visitLeafNode(final GOTLeafNode leaf1) {
            leaf1.validate();

            depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
               @Override
               public void visitLeafNode(final GOTLeafNode leaf2) {
                  //if (!(leaf1._id == leaf2._id)) {
                  if (leaf1 != leaf2) {
                     if (leaf1.getBounds().touches(leaf2.getBounds())) {
                        logSevere("BOUNDS INTERSECTION between\n\t\t" + leaf1 + "\n\tand\n\t\t" + leaf2 + "\n");
                     }
                  }
               }


               @Override
               public void finishedInnerNode(final GOTInnerNode inner) {
               }


               @Override
               public void finishedOctree(final GOctree octree) {
               }


               @Override
               public void visitInnerNode(final GOTInnerNode inner) {
               }


               @Override
               public void visitOctree(final GOctree octree) {
               }
            });
         }


         @Override
         public void finishedInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void finishedOctree(final GOctree octree) {
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void visitOctree(final GOctree octree) {
         }
      });

      logInfo("Validation done!");
   }


   @Override
   public void depthFirstAcceptVisitor(final IOctreeVisitorWithFinalization visitor) {
      try {
         visitor.visitOctree(this);

         _root.depthFirstAcceptVisitor(visitor);

         visitor.finishedOctree(this);
      }
      catch (final IOctreeVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public void breadthFirstAcceptVisitor(final IOctreeVisitor visitor) {
      try {
         visitor.visitOctree(this);

         _root.breadthFirstAcceptVisitor(visitor);
      }
      catch (final IOctreeVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public int getMaxDepth() {
      final AtomicInteger maxDepth = new AtomicInteger(Integer.MIN_VALUE);

      depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            final int currentDepth = leaf.getDepth();
            if (currentDepth > maxDepth.get()) {
               maxDepth.set(currentDepth);
            }
         }


         @Override
         public void finishedInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void finishedOctree(final GOctree octree) {
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void visitOctree(final GOctree octree) {
         }
      });

      return maxDepth.get();
   }


   public int getMinDepth() {
      final AtomicInteger minDepth = new AtomicInteger(Integer.MAX_VALUE);

      depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            final int currentDepth = leaf.getDepth();
            if (currentDepth < minDepth.get()) {
               minDepth.set(currentDepth);
            }
         }


         @Override
         public void finishedInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void finishedOctree(final GOctree octree) {
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void visitOctree(final GOctree octree) {
         }
      });

      return minDepth.get();
   }


   public Collection<GOTLeafNode> getAllLeafs() {
      final Collection<GOTLeafNode> allLeafs = new ArrayList<GOTLeafNode>();

      depthFirstAcceptVisitor(new IOctreeVisitorWithFinalization() {
         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            allLeafs.add(leaf);
         }


         @Override
         public void finishedInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void finishedOctree(final GOctree octree) {
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void visitOctree(final GOctree octree) {
         }
      });

      return allLeafs;
   }


   @Override
   public boolean logVerbose() {
      return _parameters._verbose;
   }


   public IVector3 getPoint(final int index) {
      return _vertices.getPoint(index);
   }


   @Override
   public IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> getVertices() {
      return _root.getVertices();
   }


   public IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> getOriginalVertices() {
      return _vertices;
   }


   @Override
   public GAxisAlignedBox getBounds() {
      return _bounds;
   }


   @Override
   public int getDepth() {
      return 0;
   }


   int[] removeDuplicates(final int[] verticesIndexes) {
      if (_parameters._duplicatesPolicy == null) {
         return verticesIndexes;
      }

      final int[] depured = _parameters._duplicatesPolicy.removeDuplicates(_vertices, verticesIndexes);

      _duplicatesRemoved.addAndGet(verticesIndexes.length - depured.length);

      return depured;
   }


   @Override
   protected int[] getVerticesIndexesInRegion(final IBounds3D<?> region,
                                              final GOTLeafNode excludedLeaf) {
      return _root.getVerticesIndexesInRegion(region, excludedLeaf);
   }


   public int getNearestVertexIndex(final IVector3 point) {
      return _root.getNearestVertexIndex(point);
   }


   public Vertex<IVector3> getNearestVertex(final IVector3 point) {
      return _vertices.getVertex(getNearestVertexIndex(point));
   }


   public IVector3 getNearestPoint(final IVector3 point) {
      return _vertices.getPoint(getNearestVertexIndex(point));
   }


   public void pruneToDepth(final int depth) {
      logInfo("Prunning to depth " + depth);

      _root.pruneToDepth(depth);

      if (_parameters._verbose) {
         showStatistics();
      }
   }


   public GOctree.Parameters getParameters() {
      return _parameters;
   }


   public boolean removeVertex(final int index) {
      final Vertex<IVector3> vertex = _vertices.getVertex(index);
      return _root.removeVertex(vertex, index);
   }


   public boolean isEmpty() {
      return _root.isEmpty();
   }


   //   public void save(final String directoryName) throws IOException {
   //      _root.save(directoryName);
   //   }

}
