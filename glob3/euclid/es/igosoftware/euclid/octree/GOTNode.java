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


public abstract class GOTNode
         extends
            GOTComponent {

   protected final GOTInnerNode     _parent;
   protected final GAxisAlignedBox  _bounds;


   private WeightedVertex<IVector3> _averageVertex;


   protected GOTNode(final GOTInnerNode parent,
                     final GAxisAlignedBox bounds) {
      _parent = parent;
      _bounds = bounds;
   }


   public final GOTInnerNode getParent() {
      return _parent;
   }


   @Override
   public final GAxisAlignedBox getBounds() {
      return _bounds;
   }


   @Override
   public final int getDepth() {
      if (_parent == null) {
         return 0;
      }
      return _parent.getDepth() + 1;
   }


   public final String getId() {
      if (_parent == null) {
         return "";
      }

      final byte myId = _parent.getChildIndex(this);

      final String parentId = _parent.getId();
      if ((parentId == null) || parentId.isEmpty()) {
         return Byte.toString(myId);
      }

      return parentId + "-" + myId;
   }


   @Override
   public int[] getVerticesIndexes() {
      final List<Integer> list = new ArrayList<Integer>(getVerticesIndexesCount());
      putVerticesIndexesIn(list);
      return GCollections.toIntArray(list);
   }


   @Override
   public final GOTInnerNode getRoot() {
      if (_parent == null) {
         return (GOTInnerNode) this;
      }
      return _parent.getRoot();
   }


   @Override
   public final int[] getVerticesIndexesInRegion(final IBounds3D<?> region) {
      final List<Integer> list = new ArrayList<Integer>();
      putRegionVerticesIndexesIn(region, list);
      return GCollections.toIntArray(list);
   }


   @Override
   protected final int[] getVerticesIndexesInRegion(final IBounds3D<?> region,
                                                    final GOTLeafNode excludedLeaf) {
      final List<Integer> list = new ArrayList<Integer>();
      putRegionVerticesIndexesIn(region, list, excludedLeaf);
      return GCollections.toIntArray(list);
   }


   @Override
   public final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> getVertices() {
      return getOctree().getOriginalVertices().asSubContainer(getVerticesIndexes());
   }


   protected abstract void putVerticesIndexesIn(final List<Integer> verticesIndexesContainer);


   protected abstract void putRegionVerticesIndexesIn(final IBounds3D<?> region,
                                                      final List<Integer> verticesIndexesContainer);


   protected abstract void putRegionVerticesIndexesIn(final IBounds3D<?> region,
                                                      final List<Integer> verticesIndexesContainer,
                                                      final GOTLeafNode excludedLeaf);


   @Override
   public final boolean logVerbose() {
      return getOctree().logVerbose();
   }


   //   protected abstract boolean getNearestLeaf(final IVector3 point,
   //                                             final GHolder<GOTLeafNode> nearestLeafHolder,
   //                                             final GHolder<Double> shortestSquaredDistance);


   protected abstract WeightedVertex<IVector3> calculateAverageVertex();


   public final synchronized WeightedVertex<IVector3> getAverageVertex() {
      if (_averageVertex == null) {
         _averageVertex = calculateAverageVertex();
      }
      return _averageVertex;

      //      return calculateAverageVertex();
   }


   public GOctree getOctree() {
      return _parent.getOctree();
   }


   protected abstract boolean removeVertex(final Vertex<IVector3> vertex,
                                           final int index);


   public abstract boolean isEmpty();


   protected abstract int getAnyVertexIndex();


   protected abstract boolean getNearestVertexIndex(final GOctree octree,
                                                    final GHolder<GBall> hotRegionHolder,
                                                    final GIntHolder candidateIndexHolder);


   //   protected abstract void save(final String rootDirectoryName) throws IOException;

}
