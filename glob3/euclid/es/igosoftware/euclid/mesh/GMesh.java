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


package es.igosoftware.euclid.mesh;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.euclid.shape.GQuad3D;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.shape.GTriangle3D;
import es.igosoftware.euclid.shape.IPolygon;
import es.igosoftware.euclid.shape.IPolygon3D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IFunction;


public class GMesh<

VertexT extends IVector<VertexT, ?>,

FaceT extends IPolygon<VertexT, EdgeT, ?>,

EdgeT extends GSegment<VertexT, EdgeT, ?>

>

{


   private final class IndexedFace {
      final private List<VertexNode> _vertexNodes;


      private IndexedFace(final List<VertexNode> vertexNodes) {
         _vertexNodes = vertexNodes;
         for (final VertexNode node : _vertexNodes) {
            node._usedByFaces.add(this);
         }
      }


      private void showStatistics() {
         final List<Integer> indices = GCollections.concurrentCollect(_vertexNodes, new IFunction<VertexNode, Integer>() {
            @Override
            public Integer apply(final VertexNode element) {
               return element._index;
            }
         });

         System.out.println("  IndexedFace: indices=" + indices);
      }
   }


   private final class VertexNode {
      private final VertexT           _vertex;
      private final int               _index;
      private final List<IndexedFace> _usedByFaces = new ArrayList<IndexedFace>();


      private VertexNode(final VertexT vertex,
                         final int index) {
         _vertex = vertex;
         _index = index;
      }


      private void showStatistics() {
         System.out.println("  VertexNode #" + _index + " " + _vertex + ", used by " + _usedByFaces.size() + " faces");
      }
   }


   private final List<VertexNode>  _verticesNodes;


   private final List<IndexedFace> _indexedFaces;


   public GMesh() {
      _verticesNodes = new ArrayList<VertexNode>();
      _indexedFaces = new ArrayList<IndexedFace>();
   }


   public void addFace(final FaceT face) {
      final List<VertexNode> vertexNodes = getVertexNodes(face);

      final IndexedFace iface = new IndexedFace(vertexNodes);
      _indexedFaces.add(iface);
   }


   private List<VertexNode> getVertexNodes(final FaceT face) {
      final int facePointsCount = face.getPointsCount();
      final List<VertexNode> indices = new ArrayList<VertexNode>(facePointsCount);
      for (int i = 0; i < facePointsCount; i++) {
         indices.add(getVertexNode(face.getPoint(i)));
      }
      return indices;
   }


   private VertexNode getVertexNode(final VertexT vertex) {
      for (final VertexNode vertexNode : _verticesNodes) {
         if (vertex.closeTo(vertexNode._vertex)) {
            return vertexNode;
         }
      }

      final VertexNode vertexNode = new VertexNode(vertex, _verticesNodes.size());
      _verticesNodes.add(vertexNode);
      return vertexNode;
   }


   private void showStatistics() {
      System.out.println("Mesh");
      System.out.println("----");
      System.out.println("Faces: " + _indexedFaces.size());

      for (final IndexedFace iface : _indexedFaces) {
         iface.showStatistics();
      }

      System.out.println("Vertices: " + _verticesNodes.size());
      for (final VertexNode vertexNode : _verticesNodes) {
         vertexNode.showStatistics();
      }
   }


   public static void main(final String[] args) {
      System.out.println("GMesh 0.1");
      System.out.println("---------\n");

      final GMesh<IVector3, IPolygon3D, GSegment3D> mesh = new GMesh<IVector3, IPolygon3D, GSegment3D>();

      mesh.addFace(new GTriangle3D(GVector3D.ZERO, new GVector3D(0, 1, 0), new GVector3D(0, 0, 1)));
      mesh.addFace(new GTriangle3D(GVector3D.ZERO, new GVector3D(1, 1, 0), new GVector3D(0, 0, 1)));
      //      mesh.addFace(new GTriangle3D(GVector3D.ZERO, new GVector3D(1, 1, 1), new GVector3D(1, 1, 0)));
      mesh.addFace(new GQuad3D(GVector3D.ZERO, new GVector3D(1, 1, 1), new GVector3D(1, 1, 0), new GVector3D(1, 1, 10)));

      mesh.showStatistics();
   }

}
