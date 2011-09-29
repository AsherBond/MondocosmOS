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


package es.igosoftware.globe.modules.view;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeWorldWindModel;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;


public class GCompassNavigationModule
         extends
            GAbstractGlobeModule {


   public GCompassNavigationModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getDescription() {
      return "Compass navigation";
   }


   @Override
   public String getName() {
      return "Compass navigation";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {

      final LayerList layers = context.getWorldWindModel().getLayerList();

      // Find Compass layer and enable picking
      boolean isCompassFound = false;
      for (final Layer layer : layers) {
         if (layer instanceof CompassLayer) {
            layer.setPickEnabled(true);
            isCompassFound = true;
         }
      }

      if (!isCompassFound) {
         return;
      }

      // Add select listener to handle drag events on the compass
      final IGlobeWorldWindModel model = context.getWorldWindModel();
      model.getWorldWindowGLCanvas().addSelectListener(new SelectListener() {
         private Angle _dragStartHeading = null;


         @Override
         public void selected(final SelectEvent event) {
            if (event.getTopObject() instanceof CompassLayer) {
               final View view = model.getView();

               final Angle heading = (Angle) event.getTopPickedObject().getValue("Heading");

               if (event.isDrag() && (_dragStartHeading == null)) {
                  _dragStartHeading = heading;
                  event.consume();
               }
               else if (event.isRollover() && (_dragStartHeading != null)) {
                  view.stopAnimations();
                  view.setHeading(Angle.fromDegrees(-heading.degrees));
               }
               else if (event.isDragEnd()) {
                  _dragStartHeading = null;
               }
               else if (event.isLeftDoubleClick()) {
                  context.getCameraController().goToHeading(Angle.ZERO);
               }
            }
         }
      });

   }

   //   @Override
   //   public void initialize(final IGlobeRunningContext context) {
   //
   //      final IGlobeWorldWindModel worldWindModel = context.getWorldWindModel();
   //
   //      // Find Compass _layer and enable picking
   //      boolean found = false;
   //      for (final Layer layer : worldWindModel.getLayerList()) {
   //         if (layer instanceof CompassLayer) {
   //            layer.setPickEnabled(true);
   //            found = true;
   //            break;
   //         }
   //      }
   //
   //      if (found) {
   //         // Add select listener to handle drag events on the compass
   //         worldWindModel.getWorldWindowGLCanvas().addSelectListener(new SelectListener() {
   //            private Angle _dragStartHeading = null;
   //            private Angle _viewStartHeading = null;
   //
   //
   //            @Override
   //            public void selected(final SelectEvent event) {
   //               final View view = context.getWorldWindModel().getView();
   //
   //               if (event.getTopObject() instanceof CompassLayer) {
   //                  final Angle heading = (Angle) event.getTopPickedObject().getValue("Heading");
   //                  if (event.getEventAction().equals(SelectEvent.DRAG) && (_dragStartHeading == null)) {
   //                     _dragStartHeading = heading;
   //                     _viewStartHeading = view.getHeading();
   //                  }
   //                  else if (event.getEventAction().equals(SelectEvent.ROLLOVER) && (_dragStartHeading != null)) {
   //                     final double move = heading.degrees - _dragStartHeading.degrees;
   //                     double newHeading = _viewStartHeading.degrees - move;
   //                     newHeading = newHeading >= 0 ? newHeading : newHeading + 360;
   //                     view.stopAnimations();
   //                     view.setHeading(Angle.fromDegrees(newHeading));
   //                  }
   //                  else if (event.getEventAction().equals(SelectEvent.DRAG_END)) {
   //                     _dragStartHeading = null;
   //                  }
   //               }
   //            }
   //         });
   //      }
   //
   //   }


}
