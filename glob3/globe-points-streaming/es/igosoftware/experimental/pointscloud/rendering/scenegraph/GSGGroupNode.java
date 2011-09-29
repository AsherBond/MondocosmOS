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


package es.igosoftware.experimental.pointscloud.rendering.scenegraph;

import java.awt.Color;

import es.igosoftware.euclid.pointscloud.octree.GPCInnerNode;
import es.igosoftware.euclid.pointscloud.octree.GPCLeafNode;
import es.igosoftware.euclid.pointscloud.octree.GPCNode;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.experimental.pointscloud.rendering.GPointsCloudLayer;
import gov.nasa.worldwind.render.DrawContext;


public final class GSGGroupNode
         extends
            GSGNode {

   private final GSGNode[] _children;


   public GSGGroupNode(final GPCInnerNode node,
                       final GProjection projection,
                       final GPointsCloudLayer layer) {
      super(node.getBounds(), projection, layer);

      _children = initializeChildren(node, projection, layer);
   }


   private static GSGNode[] initializeChildren(final GPCInnerNode node,
                                               final GProjection projection,
                                               final GPointsCloudLayer layer) {
      final GPCNode[] nodeChildren = node.getChildren();

      final GSGNode[] children = new GSGNode[nodeChildren.length];

      for (int i = 0; i < nodeChildren.length; i++) {
         children[i] = convert(nodeChildren[i], projection, layer);
      }

      return children;
   }


   private static GSGNode convert(final GPCNode node,
                                  final GProjection projection,
                                  final GPointsCloudLayer layer) {
      if (node == null) {
         return null;
      }

      if (node instanceof GPCInnerNode) {
         return new GSGGroupNode((GPCInnerNode) node, projection, layer);
      }

      if (node instanceof GPCLeafNode) {
         return new GSGPointsNode((GPCLeafNode) node, projection, layer);
      }

      throw new IllegalArgumentException("Invalid node class " + node.getClass());
   }


   @Override
   public final void doPreRender(final DrawContext dc,
                                 final boolean changed) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.doPreRender(dc, changed);
         }
      }
   }


   @Override
   protected final int doRender(final DrawContext dc) {
      int rendered = 0;
      for (final GSGNode child : _children) {
         if (child != null) {
            rendered += child.render(dc);
         }
      }
      return rendered;
   }


   @Override
   protected final void setPriority(final float priority) {
      //      if (priority == _priority) {
      //         return;
      //      }
      super.setPriority(priority);
      for (final GSGNode child : _children) {
         if (child != null) {
            child.setPriority(priority);
         }
      }
   }


   @Override
   public final void initialize(final DrawContext dc) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.initialize(dc);
         }
      }
   }


   @Override
   public void setColorFromElevation(final boolean colorFromElevation) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.setColorFromElevation(colorFromElevation);
         }
      }
   }


   @Override
   public void reload() {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.reload();
         }
      }
   }


   @Override
   public void setPointsColor(final Color pointsColor) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.setPointsColor(pointsColor);
         }
      }
   }


}
