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

import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeWorldWindModel;
import es.igosoftware.globe.actions.GCheckBoxGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.view.customView.GFlatCustomView;
import es.igosoftware.globe.view.customView.GView;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.SkyColorLayer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;


public abstract class GFlatWorldModule
         extends
            GAbstractGlobeModule {


   private View _oldView;


   protected GFlatWorldModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getDescription() {
      return "View flat world";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      final IGenericAction action = new GCheckBoxGenericAction("View flat world", ' ', null, IGenericAction.MenuArea.VIEW, false,
               false) {
         @Override
         public void execute() {
            setSelected(context, isSelected());
         }
      };

      return Collections.singletonList(action);
   }


   private void setSelected(final IGlobeRunningContext context,
                            final boolean selected) {
      final IGlobeWorldWindModel wwModel = context.getWorldWindModel();
      final Model model = wwModel.getModel();
      final WorldWindowGLCanvas canvas = wwModel.getWorldWindowGLCanvas();

      if (selected) {
         model.setGlobe(createFlatGlobe());

         _oldView = canvas.getView();
         if (_oldView instanceof GView) {
            final GView oldView = (GView) _oldView;

            final GFlatCustomView newView = new GFlatCustomView();
            copyPositionToNewView(oldView, newView);
            canvas.setView(newView);

            replaceSkyGradientLayerWithSkyColorLayer(model.getLayers());
         }
         else if (_oldView instanceof BasicOrbitView) {
            final BasicOrbitView oldView = (BasicOrbitView) _oldView;

            final GFlatCustomView newView = new GFlatCustomView();
            copyPositionToNewView(oldView, newView);
            canvas.setView(newView);

            replaceSkyGradientLayerWithSkyColorLayer(model.getLayers());
         }
         else {
            context.getLogger().logSevere("Invalid view type (" + _oldView.getClass() + ")");
         }
      }
      else {
         model.setGlobe(createNonFlatGlobe());
         final GFlatCustomView oldView = (GFlatCustomView) canvas.getView();

         if (_oldView instanceof GView) {
            final GView newView = new GView();
            copyPositionToNewView(oldView, newView);
            canvas.setView(newView);

            replaceSkyColorLayerWithSkyGradientLayer(model.getLayers());
         }
         else if (_oldView instanceof BasicOrbitView) {
            final BasicOrbitView newView = new BasicOrbitView();
            copyPositionToNewView(oldView, newView);
            canvas.setView(newView);

            replaceSkyColorLayerWithSkyGradientLayer(model.getLayers());
         }
         else {
            context.getLogger().logSevere("Invalid view type (" + _oldView.getClass() + ")");
         }
      }
   }


   //   protected Globe createNonFlatGlobe() {
   //      return new Earth();
   //   }
   //
   //
   //   protected Globe createFlatGlobe() {
   //      return new EarthFlat();
   //   }
   protected abstract Globe createNonFlatGlobe();


   protected abstract Globe createFlatGlobe();


   private static void copyPositionToNewView(final BasicOrbitView oldView,
                                             final BasicOrbitView newView) {
      newView.setCenterPosition(oldView.getCenterPosition());
      newView.setZoom(oldView.getZoom());
      newView.setHeading(oldView.getHeading());
      newView.setPitch(oldView.getPitch());
   }


   private static void replaceSkyGradientLayerWithSkyColorLayer(final LayerList layers) {
      for (int i = 0; i < layers.size(); i++) {
         if (layers.get(i) instanceof SkyGradientLayer) {
            layers.set(i, new SkyColorLayer());
         }
      }
   }


   private static void replaceSkyColorLayerWithSkyGradientLayer(final LayerList layers) {
      for (int i = 0; i < layers.size(); i++) {
         if (layers.get(i) instanceof SkyColorLayer) {
            layers.set(i, new SkyGradientLayer());
         }
      }
   }


   @Override
   public String getName() {
      return "View flat world";
   }


   @Override
   public String getVersion() {
      return null;
   }


}
