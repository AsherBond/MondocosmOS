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


package es.igosoftware.globe.utils;

import es.igosoftware.util.GAssert;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.util.ArrayList;
import java.util.List;


public class GAreasEventsLayer
         extends
            AbstractLayer {


   public static interface IAreaEventsListener {
      public void viewEnteredArea(final DrawContext dc,
                                  final String areaName,
                                  final Extent bounds);


      public void viewMovedArea(final DrawContext dc,
                                final String areaName,
                                final Extent bounds);


      public void viewExitedArea(final DrawContext dc,
                                 final String areaName,
                                 final Extent bounds);


      public void boundsChanged(final String areaName,
                                final Extent bounds,
                                final boolean wasInside);
   }


   private static class Area {
      private final String              _name;
      private Extent                    _bounds;

      private final IAreaEventsListener _listener;

      private boolean                   _inside = false;


      private Area(final String name,
                   final Extent bounds,
                   final IAreaEventsListener listener) {
         _name = name;
         _bounds = bounds;
         _listener = listener;
      }


      private void process(final DrawContext dc,
                           final Line token) {
         final boolean viewIsInside = _bounds.intersects(token);

         if (viewIsInside) {
            if (!_inside) {
               _inside = true;
               _listener.viewEnteredArea(dc, _name, _bounds);
            }
            else {
               _listener.viewMovedArea(dc, _name, _bounds);
            }
         }
         else {
            if (_inside) {
               _inside = false;
               _listener.viewExitedArea(dc, _name, _bounds);
            }
         }
      }


      private void changeBounds(final Extent bounds) {
         GAssert.notNull(bounds, "bounds");

         if (bounds.equals(_bounds)) {
            return;
         }

         final boolean wasInside = _inside;
         _inside = false;

         _bounds = bounds;
         _listener.boundsChanged(_name, bounds, wasInside);
      }
   }


   private final List<Area> _areas     = new ArrayList<Area>();

   private Vec4             _lastCenterPoint;

   private boolean          _showAreas = false;


   @Override
   public String getName() {
      return "GAreasEventsLayer";
   }


   public void addArea(final String areaName,
                       final Extent bounds,
                       final GAreasEventsLayer.IAreaEventsListener listener) {
      GAssert.notNull(areaName, "areaName");
      GAssert.notNull(bounds, "bounds");
      GAssert.notNull(listener, "listener");

      for (final Area area : _areas) {
         if (area._name.equals(areaName)) {
            throw new RuntimeException("Already exists another area named \"" + areaName + "\"");
         }
      }

      _areas.add(new Area(areaName, bounds, listener));
   }


   public void changeBounds(final String areaName,
                            final Extent bounds) {
      GAssert.notNull(areaName, "areaName");
      GAssert.notNull(bounds, "bounds");

      for (final Area area : _areas) {
         if (area._name.equals(areaName)) {
            area.changeBounds(bounds);
            return;
         }
      }

      throw new RuntimeException("No area named \"" + areaName + "\"");
   }


   @Override
   protected void doRender(final DrawContext dc) {

      if (!isEnabled()) {
         return;
      }

      if (_showAreas) {
         for (final Area area : _areas) {
            GWWUtils.renderExtent(dc, area._bounds);
         }
      }

      final View view = dc.getView();
      final Vec4 currentCenterPoint = view.getCurrentEyePoint();
      if (!currentCenterPoint.equals(_lastCenterPoint)) {
         _lastCenterPoint = currentCenterPoint;

         //System.out.println(view.getCurrentEyePosition());


         final Line token = new Line(currentCenterPoint, currentCenterPoint);

         for (final Area area : _areas) {
            //System.out.println(area._bounds.getRadius());
            if (view.getCurrentEyePosition().getElevation() < area._bounds.getRadius()) {
               area.process(dc, token);
            }
         }
      }


      GWWUtils.checkGLErrors(dc);
   }


   public boolean isShowAreas() {
      return _showAreas;
   }


   public void setShowAreas(final boolean showAreas) {
      _showAreas = showAreas;
   }


}
