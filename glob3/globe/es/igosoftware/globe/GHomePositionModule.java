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


package es.igosoftware.globe;

import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.utils.GOnFirstRenderLayer;
import es.igosoftware.io.GFileName;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;


public class GHomePositionModule
         extends
            GAbstractGlobeModule {


   private static final GFileName DEFAULT_ICON_NAME = GFileName.relative("home.png");
   private static final String    DEFAULT_LABEL     = "Go to home";

   private final boolean          _gotoToHomeOnStartup;
   private final Position         _position;
   private final double           _elevation;
   private final Angle            _heading;
   private final Angle            _pitch;
   private final GFileName        _iconName;
   private final String           _label;


   public GHomePositionModule(final IGlobeRunningContext context,
                              final Position position,
                              final Angle heading,
                              final Angle pitch,
                              final double elevation,
                              final boolean gotoToHomeOnStartup,
                              final GFileName iconName,
                              final String label) {
      super(context);
      _position = position;
      _heading = heading;
      _pitch = pitch;
      _elevation = elevation;
      _gotoToHomeOnStartup = gotoToHomeOnStartup;
      _iconName = iconName;
      _label = label;
   }


   public GHomePositionModule(final IGlobeRunningContext context,
                              final Position position,
                              final Angle heading,
                              final Angle pitch,
                              final double elevation,
                              final boolean gotoToHomeOnStartup) {
      this(context, position, heading, pitch, elevation, gotoToHomeOnStartup, DEFAULT_ICON_NAME, DEFAULT_LABEL);
   }


   public GHomePositionModule(final IGlobeRunningContext context,
                              final Position position,
                              final double elevation,
                              final boolean gotoToHomeOnStartup,
                              final GFileName iconName) {
      this(context, position, Angle.ZERO, Angle.ZERO, elevation, gotoToHomeOnStartup, iconName, DEFAULT_LABEL);
   }


   public GHomePositionModule(final IGlobeRunningContext context,
                              final Position position,
                              final double elevation,
                              final boolean gotoToHomeOnStartup,
                              final GFileName iconName,
                              final String label) {
      this(context, position, Angle.ZERO, Angle.ZERO, elevation, gotoToHomeOnStartup, iconName, label);
   }


   public GHomePositionModule(final IGlobeRunningContext context,
                              final Position position,
                              final double elevation,
                              final boolean gotoToHomeOnStartup) {
      this(context, position, elevation, gotoToHomeOnStartup, DEFAULT_ICON_NAME);
   }


   @Override
   public String getDescription() {
      return "Handler for home-position";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      final IGenericAction gotoHome = new GButtonGenericAction(_label, context.getBitmapFactory().getSmallIcon(_iconName),
               IGenericAction.MenuArea.NAVIGATION, true) {

         @Override
         public void execute() {
            doIt(context);
         }
      };

      return Collections.singletonList(gotoHome);
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {
      super.initialize(context);

      if (_gotoToHomeOnStartup) {
         context.getWorldWindModel().addLayer(new GOnFirstRenderLayer() {
            @Override
            protected void execute(final DrawContext dc) {
               doIt(context);
            }
         });
      }

   }


   @Override
   public String getName() {
      return "Home Position Module";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   private void doIt(final IGlobeRunningContext context) {
      final IGlobeCameraController cameraController = context.getCameraController();
      if ((_heading == null) || (_pitch == null)) {
         cameraController.animatedGoTo(_position, _elevation);
      }
      else {
         cameraController.animatedGoTo(_position, _heading, _pitch, _elevation);
      }
   }


   @Override
   public void initializeTranslations(final IGlobeRunningContext context) {
      final IGlobeTranslator translator = context.getTranslator();
      translator.addTranslation("es", "Go to home", "Ir a casa");
      translator.addTranslation("de", "Go to home", "Nach Hause gehen");
      translator.addTranslation("pt", "Go to home", "Home");
   }

}
