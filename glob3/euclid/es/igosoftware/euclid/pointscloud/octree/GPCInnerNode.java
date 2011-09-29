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
import java.util.Map;

import es.igosoftware.euclid.octree.GOTInnerNode;
import es.igosoftware.euclid.octree.GOTLeafNode;
import es.igosoftware.euclid.octree.GOTNode;


public class GPCInnerNode
         extends
            GPCNode {

   private static final long serialVersionUID = 1L;


   private static GPCNode convert(final GOTNode node,
                                  final Map<String, GPCLeafNode> leafNodes) {
      if (node == null) {
         return null;
      }

      if (node instanceof GOTInnerNode) {
         return new GPCInnerNode((GOTInnerNode) node, leafNodes);
      }

      if (node instanceof GOTLeafNode) {
         return new GPCLeafNode((GOTLeafNode) node, leafNodes);
      }

      throw new IllegalArgumentException("Invalid node class: " + node.getClass());
   }


   private static GPCNode[] initializeChildren(final GOTInnerNode node,
                                               final Map<String, GPCLeafNode> leafNodes) {
      final GOTNode[] nodeChildren = node.getChildren();

      final GPCNode[] children = new GPCNode[nodeChildren.length];

      for (int i = 0; i < nodeChildren.length; i++) {
         children[i] = convert(nodeChildren[i], leafNodes);
      }

      return children;
   }


   private final GPCNode[] _children;


   GPCInnerNode(final GOTInnerNode node,
                final Map<String, GPCLeafNode> leafNodes) {
      super(node);

      _children = initializeChildren(node, leafNodes);
   }


   public GPCNode[] getChildren() {
      return _children;
   }


   @Override
   public String toString() {
      return "GPCInnerNode [children=" + Arrays.toString(_children) + "]";
   }


}
