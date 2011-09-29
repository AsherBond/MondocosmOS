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


package es.igosoftware.globe.modules;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.globe.actions.GCheckBoxGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.io.GFileName;


public class GFullScreenModule
         extends
            GAbstractGlobeModule {


   private static final String DEFAULT_LABEL = "Full Screen";

   private final String        _label;


   public GFullScreenModule(final IGlobeRunningContext context) {
      this(context, DEFAULT_LABEL);
   }


   public GFullScreenModule(final IGlobeRunningContext context,
                            final String label) {
      super(context);
      _label = label;
   }


   @Override
   public String getName() {
      return "Full Screen";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Full Screen behavior module";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {

      if (!isFullScreenSupported()) {
         System.out.println("FULLSCREEN not supported");
         return null;
      }

      final IGlobeApplication application = context.getApplication();

      final IGenericAction switchFullScreen = new GCheckBoxGenericAction(_label, 'F', context.getBitmapFactory().getSmallIcon(
               GFileName.relative("fullscreen.png")), IGenericAction.MenuArea.VIEW, true, false) {


         @Override
         public void execute() {
            final Frame frame = application.getFrame();
            if (frame == null) {
               return;
            }

            final GraphicsDevice gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gs.isFullScreenSupported()) {
               if (isSelected()) {
                  gs.setFullScreenWindow(frame);
                  application.getWidget().prepareForFullScreen();
                  frame.validate();
               }
               else {
                  application.getWidget().prepareForNonFullScreen();
                  gs.setFullScreenWindow(null);
               }
            }
            else {
               // TODO: Full-screen mode will be simulated 
            }
         }
      };

      return Collections.singletonList(switchFullScreen);
   }


   private boolean isFullScreenSupported() {
      final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return ge.getDefaultScreenDevice().isFullScreenSupported();
   }


   @Override
   public void initializeTranslations(final IGlobeRunningContext context) {
      final IGlobeTranslator translator = context.getTranslator();
      translator.addTranslation("es", DEFAULT_LABEL, "Pantalla Completa");
      translator.addTranslation("de", DEFAULT_LABEL, "Vollbild");
      translator.addTranslation("pt", DEFAULT_LABEL, "Ecrã Completo");
   }


}
