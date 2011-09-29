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
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.globe.actions.GCheckBoxGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.io.GFileName;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.SceneController;
import gov.nasa.worldwind.StereoSceneController;
import gov.nasa.worldwind.avlist.AVKey;


public class GStereoViewerModule
         extends
            GAbstractGlobeModule {

   private static final String DEFAULT_LABEL = "View stereo";

   private final String        _label;
   private final boolean       _initialState;


   public GStereoViewerModule(final IGlobeRunningContext context,
                              final boolean isActive) {
      this(context, DEFAULT_LABEL, isActive);
   }


   public GStereoViewerModule(final IGlobeRunningContext context,
                              final String label,
                              final boolean isActive) {
      super(context);

      _label = label;
      _initialState = isActive;
   }


   static {
      Configuration.setValue(AVKey.SCENE_CONTROLLER_CLASS_NAME, StereoSceneController.class.getName());
   }


   @Override
   public String getDescription() {
      return DEFAULT_LABEL;
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {
      super.initialize(context);

      doIt(context, _initialState);
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      final IGenericAction action = new GCheckBoxGenericAction(_label, ' ', context.getBitmapFactory().getSmallIcon(
               GFileName.relative("stereo.png")), IGenericAction.MenuArea.VIEW, true, _initialState) {
         @Override
         public void execute() {
            doIt(context, isSelected());
         }
      };

      return Collections.singletonList(action);
   }


   @Override
   public String getName() {
      return DEFAULT_LABEL;
   }


   @Override
   public String getVersion() {
      return null;
   }


   private void doIt(final IGlobeRunningContext context,
                     final boolean selected) {

      final SceneController controller = context.getWorldWindModel().getWorldWindowGLCanvas().getSceneController();
      if (selected) {
         if (controller instanceof StereoSceneController) {
            ((StereoSceneController) controller).setStereoMode(AVKey.STEREO_MODE_RED_BLUE);
         }
      }
      else {
         if (controller instanceof StereoSceneController) {
            ((StereoSceneController) controller).setStereoMode(AVKey.STEREO_MODE_NONE);
         }
      }

      context.getApplication().redraw();
   }


   @Override
   public void initializeTranslations(final IGlobeRunningContext context) {
      final IGlobeTranslator translator = context.getTranslator();
      translator.addTranslation("es", DEFAULT_LABEL, "Ver Stereo");
      translator.addTranslation("de", DEFAULT_LABEL, "3D-Stereo-Sicht");
   }


}
