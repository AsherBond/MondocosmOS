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

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.experimental.pointscloud.rendering.GPointsCloudLayer;
import es.igosoftware.util.GMath;
import es.igosoftware.utils.GGlobeStateKeyCache;
import es.igosoftware.utils.GPositionBox;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Cylinder;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;


public abstract class GSGNode {

   private static final GGlobeStateKeyCache<GSGNode, Cylinder> EXTENTS_CACHE;

   static {
      EXTENTS_CACHE = new GGlobeStateKeyCache<GSGNode, Cylinder>(new GGlobeStateKeyCache.Factory<GSGNode, Cylinder>() {
         @Override
         public Cylinder create(final DrawContext dc,
                                final GSGNode node) {
            final Globe globe = dc.getGlobe();
            final double verticalExaggeration = dc.getVerticalExaggeration();

            final GPositionBox box = node._box;

            return Cylinder.computeVerticalBoundingCylinder(globe, verticalExaggeration, box._sector, box._lower.elevation,
                     GMath.nextUp(box._upper.elevation));
         }
      });
   }


   private final GPositionBox                                  _box;
   protected final GPointsCloudLayer                           _layer;

   private boolean                                             _forceComputationOfProjectedPixels = true;
   private float                                               _computedProjectedPixels           = -1;
   private float                                               _priority                          = Float.NEGATIVE_INFINITY;
   private long                                                _lastTimeStampOfProjectedPixels    = Integer.MIN_VALUE;


   public GSGNode(final GAxisAlignedBox bounds,
                  final GProjection projection,
                  final GPointsCloudLayer layer) {
      _box = new GPositionBox(bounds, projection);
      _layer = layer;
      //      System.out.println("Box: " + _box);
   }


   protected final Cylinder getExtent(final DrawContext dc) {
      return EXTENTS_CACHE.get(dc, this);
   }


   protected final boolean isVisible(final DrawContext dc) {
      final Cylinder extent = getExtent(dc);
      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
      return extent.intersects(frustum);
   }


   protected float getProjectedPixels(final DrawContext dc) {
      if (_forceComputationOfProjectedPixels) {
         computeProjectedPixels(dc);
      }

      return _computedProjectedPixels;
   }


   private void computeProjectedPixels(final DrawContext dc) {
      final GPositionBox box = getBoxForProjectedPixels();
      final Position[] vertices = box.getVertices();


      final Vec4 firstProjectedVertex = GWWUtils.getScreenPoint(dc, vertices[0]);
      double minX = firstProjectedVertex.x;
      double maxX = firstProjectedVertex.x;
      double minY = firstProjectedVertex.y;
      double maxY = firstProjectedVertex.y;

      for (int i = 1; i < vertices.length; i++) {
         final Position vertex = vertices[i];
         final Vec4 projected = GWWUtils.getScreenPoint(dc, vertex);
         final double x = projected.x;
         final double y = projected.y;

         if (x < minX) {
            minX = x;
         }
         if (y < minY) {
            minY = y;
         }
         if (x > maxX) {
            maxX = x;
         }
         if (y > maxY) {
            maxY = y;
         }
      }

      // calculate the area of the rectangle
      final double width = maxX - minX;
      final double height = maxY - minY;
      final double area = width * height;
      _computedProjectedPixels = (float) area;
   }


   protected GPositionBox getBoxForProjectedPixels() {
      return _box;
   }


   private boolean isBigEnough(final DrawContext dc) {
      return (getProjectedPixels(dc) > 0);
   }


   public final void preRender(final DrawContext dc,
                               final boolean changed) {
      final long now = dc.getFrameTimeStamp();
      if (_lastTimeStampOfProjectedPixels + 100 > now) {
         _forceComputationOfProjectedPixels = true;
         _lastTimeStampOfProjectedPixels = now;
      }

      if (!isVisible(dc) || !isBigEnough(dc)) {
         setPriority(Integer.MIN_VALUE);
      }

      doPreRender(dc, changed);
   }


   protected void setPriority(final float priority) {
      _priority = priority;
   }


   public float getPriority() {
      return _priority;
   }


   public final int render(final DrawContext dc) {
      if (!isVisible(dc)) {
         return 0;
      }

      if (!isBigEnough(dc)) {
         return 0;
      }

      if (_layer.isShowExtents()) {
         getExtent(dc).render(dc);
      }

      return doRender(dc);
   }


   protected abstract void doPreRender(final DrawContext dc,
                                       final boolean changed);


   protected abstract int doRender(final DrawContext dc);


   public abstract void initialize(final DrawContext dc);


   public abstract void setColorFromElevation(final boolean colorFromElevation);


   public abstract void reload();


   public GPositionBox getBox() {
      return _box;
   }


   public abstract void setPointsColor(final Color pointsColor);

}
