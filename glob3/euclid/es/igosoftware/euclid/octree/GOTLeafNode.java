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

import java.util.Arrays;
import java.util.List;

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
import es.igosoftware.util.GMath;
import es.igosoftware.util.GProgress;


public final class GOTLeafNode
         extends
            GOTNode {

   private int[] _verticesIndexes;


   GOTLeafNode(final GOctree octree,
               final GOTInnerNode parent,
               final int[] verticesIndexes,
               final GAxisAlignedBox bounds,
               final GProgress progress) {
      super(parent, bounds);
      _verticesIndexes = octree.removeDuplicates(verticesIndexes);
      Arrays.sort(_verticesIndexes);
      if (progress != null) {
         progress.stepsDone(verticesIndexes.length);
      }

      //      System.out.println("Created " + this);
   }


   @Override
   public String toString() {
      return "Octree Leaf #" + getId() + ", vertices: " + _verticesIndexes.length + ", bounds: " + _bounds;
   }


   @Override
   public int getLeafNodesCount() {
      return 1;
   }


   @Override
   public int getInnerNodesCount() {
      return 0;
   }


   @Override
   public int getVerticesIndexesCount() {
      return _verticesIndexes.length;
   }


   @Override
   protected void putVerticesIndexesIn(final List<Integer> verticesIndexesContainer) {
      GCollections.addAll(verticesIndexesContainer, _verticesIndexes);
   }


   @Override
   protected void putRegionVerticesIndexesIn(final IBounds3D<?> region,
                                             final List<Integer> verticesIndexesContainer) {
      if (!region.touches(_bounds)) {
         return;
      }

      for (final int index : _verticesIndexes) {
         final IVector3 point = getOctree().getPoint(index);
         if (region.contains(point)) {
            verticesIndexesContainer.add(index);
         }
      }
   }


   @Override
   protected void putRegionVerticesIndexesIn(final IBounds3D<?> region,
                                             final List<Integer> verticesIndexesContainer,
                                             final GOTLeafNode excludedLeaf) {
      if (this != excludedLeaf) {
         putRegionVerticesIndexesIn(region, verticesIndexesContainer);
      }
   }


   protected void validate() {
      for (final int index : _verticesIndexes) {
         final IVector3 point = getOctree().getPoint(index);
         if (!_bounds.contains(point)) {
            logSevere("Point " + point + " doesn't fit on " + this);
         }
      }
   }


   @Override
   public void depthFirstAcceptVisitor(final IOctreeVisitorWithFinalization visitor) throws IOctreeVisitor.AbortVisiting {
      visitor.visitLeafNode(this);
   }


   //   @Override
   //   protected boolean getNearestLeaf(final IVector3 point,
   //                                    final GHolder<GOTLeafNode> nearestLeafHolder,
   //                                    final GHolder<Double> shortestSquaredDistance) {
   //      if (isEmpty()) {
   //         return false;
   //      }
   //
   //      final GOTLeafNode nearestLeaf = nearestLeafHolder.get();
   //
   //      if (nearestLeaf == null) {
   //         nearestLeafHolder.set(this);
   //
   //         if (_bounds.contains(point)) {
   //            return true;
   //         }
   //
   //         shortestSquaredDistance.set(_bounds.squaredDistance(point));
   //         return false;
   //      }
   //
   //
   //      if (_bounds.contains(point)) {
   //         nearestLeafHolder.set(this);
   //         return true;
   //      }
   //
   //
   //      final double currentDistance = _bounds.squaredDistance(point);
   //      if (currentDistance < shortestSquaredDistance.get()) {
   //         nearestLeafHolder.set(this);
   //         shortestSquaredDistance.set(currentDistance);
   //      }
   //
   //      return false;
   //   }


   @Override
   public final int[] getVerticesIndexes() {
      return Arrays.copyOf(_verticesIndexes, _verticesIndexes.length);
   }


   @Override
   protected WeightedVertex<IVector3> calculateAverageVertex() {
      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = getOctree().getOriginalVertices().asSubContainer(
               _verticesIndexes);

      return vertices.getAverage();
   }


   @Override
   protected boolean removeVertex(final Vertex<IVector3> vertex,
                                  final int index) {
      if (!_bounds.contains(vertex._point)) {
         return false;
      }

      final int[] filtered = GCollections.removeFromSorted(_verticesIndexes, index);
      if (filtered == _verticesIndexes) {
         return false;
      }
      _verticesIndexes = filtered;
      return true;
   }


   @Override
   public boolean isEmpty() {
      return (_verticesIndexes.length == 0);
   }


   @Override
   protected int getAnyVertexIndex() {
      if (_verticesIndexes.length == 0) {
         return -1;
      }
      return _verticesIndexes[0];
   }


   @Override
   protected boolean getNearestVertexIndex(final GOctree octree,
                                           final GHolder<GBall> hotRegionHolder,
                                           final GIntHolder candidateIndexHolder) {

      //final GBall hotRegion = hotRegionHolder.get();
      if (!hotRegionHolder.get().touches(_bounds)) {
         return false;
      }

      final IVector3 target = hotRegionHolder.get()._center;

      for (final int vertexIndex : _verticesIndexes) {
         final IVector3 currentPoint = octree.getPoint(vertexIndex);
         if (currentPoint.closeTo(target)) {
            candidateIndexHolder.set(vertexIndex);
            return true;
         }

         final double currentDistance = currentPoint.squaredDistance(target);
         final double radius = hotRegionHolder.get()._radius;
         final double squaredRadius = radius * radius;
         if (currentDistance < squaredRadius) {
            candidateIndexHolder.set(vertexIndex);

            final GBall newHotRegion = new GBall(target, GMath.sqrt(currentDistance));
            hotRegionHolder.set(newHotRegion);
         }
      }

      return hotRegionHolder.get().isFullInside(_bounds);

   }


   //   @Override
   //   protected void save(final String rootDirectoryName) throws IOException {
   //      final String fileNameSansExtension = getFileNameSansExtension(rootDirectoryName);
   //
   //      saveData(fileNameSansExtension);
   //   }
   //
   //
   //   private void saveData(final String fileNameSansExtension) throws IOException {
   //      final int WORKING_POINT;
   //
   //      final String boundsFileName = fileNameSansExtension + ".bounds";
   //      _bounds.save(boundsFileName);
   //
   //      final DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileNameSansExtension
   //                                                                                                         + ".leaf")));
   //
   //      GIOUtils.gentlyClose(output);
   //   }
   //
   //
   //   private String getFileNameSansExtension(final String rootDirectoryName) {
   //      if (_parent == null) {
   //         // the root inner node goes to the top directory
   //         return rootDirectoryName;
   //      }
   //
   //      // a non root inner-node goes to a subdirectory of the parent's directory
   //      return _parent.getDirectoryName(rootDirectoryName) + "/" + _parent.getChildIndex(this);
   //   }

}
