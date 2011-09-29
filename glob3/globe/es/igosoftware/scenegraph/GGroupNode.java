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


package es.igosoftware.scenegraph;

import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.scenegraph.GPositionRenderableLayer.PickResult;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IFunction;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.render.DrawContext;

import java.util.ArrayList;
import java.util.List;


public final class GGroupNode
         extends
            GMutableNodeAbstract<GGroupNode> {

   private final List<INode>             _children                  = new ArrayList<INode>(1);

   //   private Sphere                        _bounds                    = null;
   private Sphere                        _boundsInGlobalCoordinates = null;

   private final IMutable.ChangeListener _childListener             = new IMutable.ChangeListener() {
                                                                       @Override
                                                                       public void mutableChanged() {
                                                                          cleanCaches();
                                                                          changed();
                                                                       }
                                                                    };

   private GPositionRenderableLayer      _layer;


   public GGroupNode(final String name,
                     final GTransformationOrder order) {
      super(name, order);
   }


   @Override
   public void changed() {
      super.changed();
      cleanCaches();
   }


   //   public <ImmutableChildT extends IImmutableNode> void addChild(final ImmutableChildT immutableChild) {
   //      rawAddChild(immutableChild);
   //   }


   public <MutableChildT extends IMutableNode<MutableChildT>> void addChild(final MutableChildT mutableChild) {
      rawAddChild(mutableChild);

      if (mutableChild.isMutable()) {
         mutableChild.addChangeListener(_childListener);
      }
      redraw();
   }


   //   public <ImmutableChildT extends IImmutableNode> void removeChild(final ImmutableChildT immutableChild) {
   //      rawRemoveChild(immutableChild);
   //   }


   public <MutableChildT extends IMutableNode<MutableChildT>> void removeChild(final MutableChildT mutableChild) {
      rawRemoveChild(mutableChild);

      if (mutableChild.isMutable()) {
         mutableChild.removeChangeListener(_childListener);
      }
   }


   private void rawAddChild(final INode child) {
      synchronized (_children) {
         checkMutable();

         GAssert.notNull(child, "child");

         cleanCaches();
         _children.add(child);

         child.setParent(this);
         changed();
      }
   }


   private void rawRemoveChild(final INode child) {
      synchronized (_children) {
         checkMutable();

         GAssert.notNull(child, "child");

         cleanCaches();
         _children.remove(child);

         child.dispose();
         child.setParent(null);
         changed();
      }
   }


   //   @Override
   //   public Sphere getBounds() {
   //      if (_bounds == null) {
   //         final List<Extent> childrenBounds = GCollections.collect(_children, new ITransformer<INode, Extent>() {
   //            @Override
   //            public Extent transform(final INode element) {
   //               return element.getBounds();
   //            }
   //         });
   //
   //         _bounds = Sphere.createBoundingSphere(childrenBounds);
   //      }
   //
   //      return _bounds;
   //   }


   @Override
   public Sphere getBoundsInModelCoordinates(final Matrix parentMatrix,
                                             final boolean matrixChanged) {

      if (matrixChanged || (_boundsInGlobalCoordinates == null)) {
         final Matrix globalMatrix = getGlobalMatrix(parentMatrix);

         synchronized (_children) {
            final List<Extent> childrenBounds = GCollections.collect(_children, new IFunction<INode, Extent>() {
               @Override
               public Extent apply(final INode element) {
                  return element.getBoundsInModelCoordinates(globalMatrix, true);
               }
            });

            _boundsInGlobalCoordinates = Sphere.createBoundingSphere(childrenBounds);
         }
      }

      return _boundsInGlobalCoordinates;
   }


   @Override
   public String toString() {
      return "GGroupNode [name=" + getName() + ", children=" + _children.size() + getTransformationString() + "]";
   }


   @Override
   public void dispose() {
      super.dispose();

      synchronized (_children) {
         for (final INode child : _children) {
            child.dispose();
         }
      }
   }


   @Override
   public void cleanCaches() {
      super.cleanCaches();

      //      _bounds = null;
      _boundsInGlobalCoordinates = null;

      synchronized (_children) {
         for (final INode child : _children) {
            child.cleanCaches();
         }
      }
   }


   @Override
   protected void doRender(final DrawContext dc,
                           final Matrix parentMatrix,
                           final boolean terrainChanged) {
      final Matrix globalMatrix = getGlobalMatrix(parentMatrix);
      synchronized (_children) {
         for (final INode child : _children) {
            child.render(dc, globalMatrix, terrainChanged);
         }
      }
   }


   @Override
   public void preRender(final DrawContext dc,
                         final Matrix parentMatrix,
                         final boolean terrainChanged) {
      synchronized (_children) {
         for (final INode child : _children) {
            child.preRender(dc, parentMatrix, terrainChanged);
         }
      }
   }


   @Override
   public boolean isPickable() {
      synchronized (_children) {
         for (final INode child : _children) {
            if (child.isPickable()) {
               return true;
            }
         }
      }

      return false;
   }


   @Override
   protected boolean doPick(final DrawContext dc,
                            final Matrix parentMatrix,
                            final boolean terrainChanged,
                            final Line ray,
                            final List<PickResult> pickResults) {

      boolean picked = false;

      final Matrix globalMatrix = getGlobalMatrix(parentMatrix);
      synchronized (_children) {
         for (final INode child : _children) {
            if (child.pick(dc, globalMatrix, terrainChanged, ray, pickResults)) {
               picked = true;
            }
         }
      }

      return picked;
   }


   public void setLayer(final GPositionRenderableLayer layer) {
      if (_layer != null) {
         throw new RuntimeException("Can't set more than one layer to " + this);
      }
      _layer = layer;
   }


   public GPositionRenderableLayer getLayer() {
      return _layer;
   }


   @Override
   public void acceptVisitor(final IVisitor visitor) {
      super.acceptVisitor(visitor);

      synchronized (_children) {
         for (final INode child : _children) {
            child.acceptVisitor(visitor);
         }
      }

   }


}
