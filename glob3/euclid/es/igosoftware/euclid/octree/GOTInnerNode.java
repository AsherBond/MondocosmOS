

package es.igosoftware.euclid.octree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GBall;
import es.igosoftware.euclid.bounding.IBounds3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer.Vertex;
import es.igosoftware.euclid.verticescontainer.IVertexContainer.WeightedVertex;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GProgress;


public class GOTInnerNode
         extends
            GOTNode {

   private GOTNode[] _children;


   protected GOTInnerNode(final GOctree octree,
                          final GOTInnerNode parent,
                          final GHolder<int[]> verticesIndexes,
                          final GAxisAlignedBox bounds,
                          final GOctree.CreateLeafPolicy createLeafPolicy,
                          final GOctree.Parameters parameters,
                          final GProgress progress) throws InterruptedException, ExecutionException {
      super(parent, bounds);

      final GOctree.NodesCreationPolicy nodesCreationPolicy = parameters.getNodesCreationPolicy();
      final Object auxiliaryObject = nodesCreationPolicy.createAuxiliaryObject(getOctree(), verticesIndexes.get(), bounds,
               getDepth());

      _children = new GOTNode[nodesCreationPolicy.getMaxNodes(auxiliaryObject)];

      if (parameters.isMultithreading() && (verticesIndexes.get().length > 256)) {
         concurrentDistributeVertices(octree, verticesIndexes, createLeafPolicy, parameters, auxiliaryObject, progress,
                  octree.getOriginalVertices());
      }
      else {
         distributeVertices(octree, verticesIndexes, createLeafPolicy, parameters, auxiliaryObject, progress,
                  octree.getOriginalVertices());
      }

      _children = GCollections.rtrim(_children);

      //      System.out.println("Created " + this);
   }


   private static boolean acceptLeafCreation(final GOctree.Parameters parameters,
                                             final GOctree.CreateLeafPolicy createLeafPolicy,
                                             final int[] nodeVerticesIndexes,
                                             final GAxisAlignedBox nodeBounds,
                                             final int depth,
                                             final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices) {

      // just one vertex, create a leaf
      if (nodeVerticesIndexes.length == 1) {
         return true;
      }

      final int maxDepth = parameters.getMaxDepth();
      if ((maxDepth != -1) && (depth == (maxDepth - 1))) {
         return true;
      }

      final IVector3 nodeExtent = nodeBounds._extent;

      // if the extent if too small, force a leaf creation
      if (((nodeExtent.x() <= 0.000001) || (nodeExtent.y() <= 0.000001) || (nodeExtent.z() <= 0.000001))) {
         return true;
      }

      // too many vertices to create a leaf
      if (nodeVerticesIndexes.length > parameters.getMaxLeafVertices()) {
         return false;
      }

      // the bounds is too big to create a leaf
      final double maxLeafSideLength = parameters.getMaxLeafSideLength();
      if ((nodeExtent.x() > maxLeafSideLength) || (nodeExtent.y() > maxLeafSideLength) || (nodeExtent.z() > maxLeafSideLength)) {
         return false;
      }

      return (createLeafPolicy == null) || createLeafPolicy.acceptLeafCreation(vertices, nodeVerticesIndexes);
   }


   public void breadthFirstAcceptVisitor(final IOctreeVisitor visitor) throws IOctreeVisitor.AbortVisiting {

      final LinkedList<GOTNode> queue = new LinkedList<GOTNode>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GOTNode current = queue.removeFirst();

         if (current instanceof GOTInnerNode) {
            final GOTInnerNode currentInner = (GOTInnerNode) current;

            visitor.visitInnerNode(currentInner);

            for (final GOTNode child : currentInner._children) {
               if (child != null) {
                  queue.addLast(child);
               }
            }
         }
         else if (current instanceof GOTLeafNode) {
            final GOTLeafNode currentLeaf = (GOTLeafNode) current;
            visitor.visitLeafNode(currentLeaf);
         }
         else {
            throw new IllegalArgumentException();
         }
      }
   }


   @Override
   public final void depthFirstAcceptVisitor(final IOctreeVisitorWithFinalization visitor) throws IOctreeVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      for (final GOTNode child : _children) {
         if (child != null) {
            child.depthFirstAcceptVisitor(visitor);
         }
      }

      visitor.finishedInnerNode(this);
   }


   private void concurrentDistributeVertices(final GOctree octree,
                                             final GHolder<int[]> verticesIndexes,
                                             final GOctree.CreateLeafPolicy createLeafPolicy,
                                             final GOctree.Parameters parameters,
                                             final Object auxiliaryObject,
                                             final GProgress progress,
                                             final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices)
                                                                                                                             throws InterruptedException,
                                                                                                                             ExecutionException {

      final ExecutorService executor = GConcurrent.getDefaultExecutor();

      final GOctree.NodesCreationPolicy nodesCreationPolicy = parameters.getNodesCreationPolicy();
      final int maxNodes = nodesCreationPolicy.getMaxNodes(auxiliaryObject);

      final List<Future<GOTNode>> futuresNodes = new ArrayList<Future<GOTNode>>(maxNodes);
      for (int nodeKey = 0; nodeKey < maxNodes; nodeKey++) {
         futuresNodes.add(null);
      }

      //      synchronized (_children) {
      final int[][] nodesVerticesIndexes = splitVerticexIndexesIntoNodes(verticesIndexes.get(), parameters, auxiliaryObject);
      verticesIndexes.clear(); // release some memory

      for (byte nodeKey = 0; nodeKey < maxNodes; nodeKey++) {
         final int[] nodeVerticesIndexes = nodesVerticesIndexes[nodeKey];

         //if (nodeVerticesIndexes.length == 0) {
         if (nodeVerticesIndexes == null) {
            continue;
         }

         final byte finalNodeKey = nodeKey;
         final Future<GOTNode> futureNode = executor.submit(new Callable<GOTNode>() {
            @Override
            public GOTNode call() throws Exception {
               //final GAxisAlignedBox nodeBounds = getNodeBounds(nodeKey);
               final GAxisAlignedBox nodeBounds = nodesCreationPolicy.getNodeBounds(auxiliaryObject, _bounds, finalNodeKey);

               if (nodeBounds.equals(_bounds)) {
                  throw new RuntimeException("Node " + finalNodeKey + " of Inner #" + getId() + " has same bounds than the inner");
               }

               final GOTNode result;

               if (acceptLeafCreation(parameters, createLeafPolicy, nodeVerticesIndexes, nodeBounds, getDepth(), vertices)) {
                  result = new GOTLeafNode(octree, GOTInnerNode.this, nodeVerticesIndexes,
                           parameters.saveBoundsOnLeafs() ? nodeBounds : null, progress);
               }
               else {
                  result = new GOTInnerNode(octree, GOTInnerNode.this, new GHolder<int[]>(nodeVerticesIndexes), nodeBounds,
                           createLeafPolicy, parameters, progress);
               }

               nodesVerticesIndexes[finalNodeKey] = null; // release some memory

               return result;
            }
         });

         futuresNodes.set(finalNodeKey, futureNode);

      }

      for (int nodeKey = 0; nodeKey < maxNodes; nodeKey++) {
         final Future<GOTNode> future = futuresNodes.get(nodeKey);
         if (future != null) {
            _children[nodeKey] = future.get();
         }
      }

   }


   private void distributeVertices(final GOctree octree,
                                   final GHolder<int[]> verticesIndexes,
                                   final GOctree.CreateLeafPolicy createLeafPolicy,
                                   final GOctree.Parameters parameters,
                                   final Object auxiliaryObject,
                                   final GProgress progress,
                                   final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices)
                                                                                                                   throws InterruptedException,
                                                                                                                   ExecutionException {


      final int[][] nodesVerticesIndexes = splitVerticexIndexesIntoNodes(verticesIndexes.get(), parameters, auxiliaryObject);
      verticesIndexes.clear(); // release some memory


      final GOctree.NodesCreationPolicy nodesCreationPolicy = parameters.getNodesCreationPolicy();
      final int maxNodes = nodesCreationPolicy.getMaxNodes(auxiliaryObject);


      for (byte nodeKey = 0; nodeKey < maxNodes; nodeKey++) {
         final int[] nodeVerticesIndexes = nodesVerticesIndexes[nodeKey];

         //         if (nodeVerticesIndexes.length == 0) {
         if (nodeVerticesIndexes == null) {
            continue;
         }

         final GAxisAlignedBox nodeBounds = nodesCreationPolicy.getNodeBounds(auxiliaryObject, _bounds, nodeKey);

         if (nodeBounds.equals(_bounds)) {
            throw new RuntimeException("Node " + nodeKey + " of Inner #" + getId() + " has same bounds than the inner");
         }


         if (acceptLeafCreation(parameters, createLeafPolicy, nodeVerticesIndexes, nodeBounds, getDepth(), vertices)) {
            _children[nodeKey] = new GOTLeafNode(octree, GOTInnerNode.this, nodeVerticesIndexes,
                     parameters.saveBoundsOnLeafs() ? nodeBounds : null, progress);
         }
         else {
            _children[nodeKey] = new GOTInnerNode(octree, GOTInnerNode.this, new GHolder<int[]>(nodeVerticesIndexes), nodeBounds,
                     createLeafPolicy, parameters, progress);
         }

         nodesVerticesIndexes[nodeKey] = null; // release some memory
      }

   }


   @Override
   public final int getLeafNodesCount() {
      int counter = 0;
      for (final GOTNode child : _children) {
         if (child != null) {
            counter += child.getLeafNodesCount();
         }
      }
      return counter;
   }


   @Override
   public final int getInnerNodesCount() {
      int counter = 0;
      for (final GOTNode child : _children) {
         if (child != null) {
            counter += child.getInnerNodesCount();
         }
      }
      return counter + 1;
   }


   private int[][] splitVerticexIndexesIntoNodes(final int[] verticesIndexes,
                                                 final GOctree.Parameters parameters,
                                                 final Object auxiliaryObject) {

      final GOctree.NodesCreationPolicy nodesCreationPolicy = parameters.getNodesCreationPolicy();

      final int maxNodes = nodesCreationPolicy.getMaxNodes(auxiliaryObject);

      final byte[] nodesKeys = new byte[verticesIndexes.length]; // the node key for each vertex index
      final int[] nodesKeysCounter = new int[maxNodes]; // count how many vertices are in each node
      for (int i = 0; i < verticesIndexes.length; i++) {
         final int vertexIndex = verticesIndexes[i];

         final byte nodeKey = nodesCreationPolicy.getNodeKey(auxiliaryObject, getOctree(), vertexIndex);

         nodesKeys[i] = nodeKey;

         nodesKeysCounter[nodeKey]++;
      }


      // creates an int array to hold the indices per node
      final int[][] result = new int[maxNodes][];
      for (byte nodeKey = 0; nodeKey < maxNodes; nodeKey++) {
         final int size = nodesKeysCounter[nodeKey];
         if (size > 0) {
            result[nodeKey] = new int[size];
         }
      }


      final int[] resultJ = new int[maxNodes]; // J index, one per node, start in zero
      for (int i = 0; i < nodesKeys.length; i++) {
         final byte nodeKey = nodesKeys[i];
         final int j = resultJ[nodeKey]++;
         result[nodeKey][j] = verticesIndexes[i];
      }

      return result;
   }


   @Override
   public final int getVerticesIndexesCount() {
      int count = 0;
      for (final GOTNode child : _children) {
         if (child != null) {
            count += child.getVerticesIndexesCount();
         }
      }
      return count;
   }


   @Override
   final protected void putRegionVerticesIndexesIn(final IBounds3D<?> region,
                                                   final List<Integer> verticesIndexesContainer) {
      if (!region.touches(_bounds)) {
         return;
      }

      for (final GOTNode child : _children) {
         if (child != null) {
            child.putRegionVerticesIndexesIn(region, verticesIndexesContainer);
         }
      }
   }


   @Override
   final protected void putRegionVerticesIndexesIn(final IBounds3D<?> region,
                                                   final List<Integer> verticesIndexesContainer,
                                                   final GOTLeafNode excludedLeaf) {

      if (!region.touches(_bounds)) {
         return;
      }

      for (final GOTNode child : _children) {
         if ((child != null) && (child != excludedLeaf)) {
            child.putRegionVerticesIndexesIn(region, verticesIndexesContainer, excludedLeaf);
         }
      }
   }


   @Override
   final protected void putVerticesIndexesIn(final List<Integer> verticesIndexesContainer) {
      for (final GOTNode child : _children) {
         if (child != null) {
            child.putVerticesIndexesIn(verticesIndexesContainer);
         }
      }
   }


   final protected int getNearestVertexIndex(final IVector3 target) {
      //      final GOTLeafNode nearestLeaf = getNearestLeaf(point);
      //      GAssert.notNull(nearestLeaf, "nearestLeaf");
      //
      //      int closestVertexIndex = -1;
      //      double minDistance = Double.POSITIVE_INFINITY;
      //
      //      for (final int vertexIndex : nearestLeaf.getVerticesIndexes()) {
      //         final IVector3 currentPoint = getOctree().getPoint(vertexIndex);
      //         final double currentDistance = currentPoint.squaredDistance(point);
      //         if (currentDistance < minDistance) {
      //            minDistance = currentDistance;
      //            closestVertexIndex = vertexIndex;
      //         }
      //      }
      //
      //      final GBall candidatesRegion = new GBall(point, GMath.sqrt(minDistance));
      //      if (candidatesRegion.isFullInside(nearestLeaf._bounds)) {
      //         return closestVertexIndex;
      //      }
      //
      //      final int[] candidatesVerticesIndices = getOctree().getVerticesIndexesInRegion(candidatesRegion, nearestLeaf);
      //      for (final int candidateVertexIndex : candidatesVerticesIndices) {
      //         final IVector3 currentPoint = getOctree().getPoint(candidateVertexIndex);
      //         final double currentDistance = currentPoint.squaredDistance(point);
      //         if (currentDistance < minDistance) {
      //            minDistance = currentDistance;
      //            closestVertexIndex = candidateVertexIndex;
      //         }
      //      }
      //
      //      return closestVertexIndex;

      //      final GOTLeafNode nearestLeaf = getNearestLeaf(target);
      //
      //      int closestVertexIndex = -1;
      //      double minDistance = Double.POSITIVE_INFINITY;
      //
      //      for (final int vertexIndex : nearestLeaf.getVerticesIndexes()) {
      //         final IVector3 currentPoint = getOctree().getPoint(vertexIndex);
      //         final double currentDistance = currentPoint.squaredDistance(target);
      //         if (currentDistance < minDistance) {
      //            minDistance = currentDistance;
      //            closestVertexIndex = vertexIndex;
      //         }
      //      }

      final GOctree octree = getOctree();
      final int candidateIndex = getAnyVertexIndex();
      //      final int candidateIndex = nearestLeaf.getAnyVertexIndex();
      //final int candidateIndex = closestVertexIndex;
      final double distance = target.distance(octree.getPoint(candidateIndex));
      final GBall hotRegion = new GBall(target, distance);

      final GHolder<GBall> hotRegionHolder = new GHolder<GBall>(hotRegion);
      final GIntHolder candidateIndexHolder = new GIntHolder(candidateIndex);

      getNearestVertexIndex(octree, hotRegionHolder, candidateIndexHolder);
      return candidateIndexHolder.get();
   }


   @Override
   protected boolean getNearestVertexIndex(final GOctree octree,
                                           final GHolder<GBall> hotRegionHolder,
                                           final GIntHolder candidateIndexHolder) {

      final GBall hotRegion = hotRegionHolder.get();
      if (!hotRegion.touches(_bounds)) {
         return false;
      }

      for (final GOTNode child : _children) {
         if (child != null) {
            if (child.getNearestVertexIndex(octree, hotRegionHolder, candidateIndexHolder)) {
               return true;
            }
         }
      }

      return false;
   }


   @Override
   protected int getAnyVertexIndex() {
      for (final GOTNode child : _children) {
         if (child != null) {
            final int result = child.getAnyVertexIndex();
            if (result >= 0) {
               return result;
            }
         }
      }
      return -1;
   }


   //   final private GOTLeafNode getNearestLeaf(final IVector3 point) {
   //      final GHolder<GOTLeafNode> nearestLeafHolder = new GHolder<GOTLeafNode>(null);
   //      final GHolder<Double> shortestSquaredDistance = new GHolder<Double>(Double.POSITIVE_INFINITY);
   //
   //      getNearestLeaf(point, nearestLeafHolder, shortestSquaredDistance);
   //
   //      return nearestLeafHolder.get();
   //   }


   //   @Override
   //   final protected boolean getNearestLeaf(final IVector3 point,
   //                                          final GHolder<GOTLeafNode> nearestLeafHolder,
   //                                          final GHolder<Double> shortestSquaredDistance) {
   //
   //
   //      for (final GOTNode child : _children) {
   //         if (node != null) {
   //            GBall hotRegion;
   //            if (nearestLeafHolder.isEmpty()) {
   //               hotRegion = null;
   //            }
   //            else {
   //               hotRegion = new GBall(point, GMath.sqrt(shortestSquaredDistance.get()));
   //            }
   //
   //            if ((hotRegion == null) || hotRegion.touches(node._bounds)) {
   //               final boolean found = node.getNearestLeaf(point, nearestLeafHolder, shortestSquaredDistance);
   //               if (found) {
   //                  return true;
   //               }
   //
   //               hotRegion = new GBall(point, GMath.sqrt(shortestSquaredDistance.get()));
   //            }
   //         }
   //      }
   //
   //      return false;
   //   }


   @Override
   public final String toString() {
      //return "Octree Inner #" + getId() + ", pivot: " + _pivot + ", bounds: " + _bounds;
      return "Octree Inner #" + getId() + ", bounds: " + _bounds;
   }


   public final int getNodesCount() {
      int result = 0;
      for (final GOTNode child : _children) {
         if (child != null) {
            result++;
         }
      }
      return result;
   }


   @Override
   protected WeightedVertex<IVector3> calculateAverageVertex() {
      for (final GOTNode child : _children) {
         if (child != null) {
            child.getAverageVertex(); // force lazy initialization
         }
      }

      final List<WeightedVertex<IVector3>> nodesAverages = new ArrayList<WeightedVertex<IVector3>>(_children.length);

      for (final GOTNode child : _children) {
         if (child != null) {
            nodesAverages.add(child.getAverageVertex());
         }
      }

      return IVertexContainer.WeightedVertex.getAverage(nodesAverages);

      //      final List<Future<WeightedVertex<IVector3>>> futures = new ArrayList<Future<WeightedVertex<IVector3>>>(
      //               _children.length);
      //      final ExecutorService executor = GConcurrent.getDefaultExecutor();
      //      for (final GOTNode child : _children) {
      //         if (node != null) {
      //            final Future<WeightedVertex<IVector3>> future = executor.submit(new Callable<WeightedVertex<IVector3>>() {
      //               @Override
      //               public WeightedVertex<IVector3> call() throws Exception {
      //                  return node.getAverageVertex();
      //               }
      //            });
      //            futures.add(future);
      //         }
      //      }
      //
      //      return IVertexContainer.WeightedVertex.getAverage(GConcurrent.resolve(futures));
   }


   public final byte getChildIndex(final GOTNode node) {
      for (byte i = 0; i < _children.length; i++) {
         if (node == _children[i]) {
            return i;
         }
      }
      return -1;
   }


   public final void pruneToDepth(final int depth) {
      if ((depth - 1) == getDepth()) {
         prune();
      }
      else {
         for (byte i = 0; i < _children.length; i++) {
            final GOTNode node = _children[i];
            if (node instanceof GOTInnerNode) {
               final GOTInnerNode inner = (GOTInnerNode) node;
               inner.pruneToDepth(depth);
            }
         }
      }
   }


   private void prune() {
      final GOctree octree = getOctree();
      for (byte i = 0; i < _children.length; i++) {
         final GOTNode node = _children[i];
         if (node instanceof GOTInnerNode) {
            final GOTInnerNode inner = (GOTInnerNode) node;
            _children[i] = new GOTLeafNode(octree, this, inner.getVerticesIndexes(),
                     octree.getParameters().saveBoundsOnLeafs() ? inner.getBounds() : null, null);
         }
      }
   }


   @Override
   protected boolean removeVertex(final Vertex<IVector3> vertex,
                                  final int index) {
      if (!_bounds.contains(vertex._point)) {
         return false;
      }

      for (int i = 0; i < _children.length; i++) {
         final GOTNode node = _children[i];
         if (node != null) {
            if (node.removeVertex(vertex, index)) {
               if (node.isEmpty()) {
                  _children[i] = null;
               }
               return true;
            }
         }
      }
      return false;
   }


   @Override
   public boolean isEmpty() {
      for (final GOTNode child : _children) {
         if (child != null) {
            //            if (!node.isEmpty()) {
            return false;
            //            }
         }
      }

      return true;
   }


   public GOTNode[] getChildren() {
      return Arrays.copyOf(_children, _children.length);
   }


   //   @Override
   //   protected void save(final String rootDirectoryName) throws IOException {
   //      final String directoryName = getDirectoryName(rootDirectoryName);
   //      GIOUtils.assureEmptyDirectory(directoryName);
   //
   //
   //      saveData(directoryName);
   //
   //      for (final GOTNode child : _children) {
   //         if (child != null) {
   //            child.save(rootDirectoryName);
   //         }
   //      }
   //   }
   //
   //
   //   private void saveData(final String directoryName) throws IOException {
   //      final int WORKING_POINT;
   //
   //      final String boundsFileName = directoryName + "/inner.bounds";
   //      _bounds.save(boundsFileName);
   //   }
   //
   //
   //   /**
   //    * Answer the directory name of the receiver
   //    * 
   //    * @return
   //    */
   //   String getDirectoryName(final String rootDirectoryName) {
   //      if (_parent == null) {
   //         // the root inner node goes to the top directory
   //         return rootDirectoryName;
   //      }
   //
   //      // a non root inner-node goes to a subdirectory of the parent's directory
   //      return _parent.getDirectoryName(rootDirectoryName) + "/" + _parent.getChildIndex(this);
   //   }


}
