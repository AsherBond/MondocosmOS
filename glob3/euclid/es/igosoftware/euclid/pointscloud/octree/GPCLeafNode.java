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


package es.igosoftware.euclid.pointscloud.octree;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.octree.GOTLeafNode;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GCollections;


public class GPCLeafNode
         extends
            GPCNode {

   private static final long                        serialVersionUID = 1L;

   private final String                             _id;
   private final int                                _pointsCount;
   private int[]                                    _lodIndices;
   private IVector3                                 _referencePoint;
   private final GAxisAlignedOrthotope<IVector3, ?> _minimumBounds;


   GPCLeafNode(final GOTLeafNode node,
               final Map<String, GPCLeafNode> leafNodes) {
      super(node);

      _id = node.getId();
      _pointsCount = node.getVerticesIndexesCount();
      _minimumBounds = node.getVertices().getBounds();

      //      _lodIndices = new int[8];
      //      Arrays.fill(_lodIndices, 0);
      leafNodes.put(_id, this);
   }


   public String getId() {
      return _id;
   }


   public int[] getLodIndices() {
      return _lodIndices;
   }


   public GAxisAlignedOrthotope<IVector3, ?> getMinimumBounds() {
      return _minimumBounds;
   }


   public int getPointsCount() {
      return _pointsCount;
   }


   public IVector3 getReferencePoint() {
      return _referencePoint;
   }


   public void setLodIndices(final List<Integer> lodIndices) {
      _lodIndices = GCollections.toIntArray(lodIndices);
   }


   public void setReferencePoint(final IVector3 point) {
      _referencePoint = point;
   }


   @Override
   public String toString() {
      //      return "GPCLeafNode [id=" + _id + ", pointsCount=" + _pointsCount + ", lodIndices=" + Arrays.toString(_lodIndices)
      //      + ", minimumBounds=" + _minimumBounds + "]";
      return "GPCLeafNode [id=" + _id + ", pointsCount=" + _pointsCount + ", lodIndices=" + Arrays.toString(_lodIndices) + "]";
   }


}
