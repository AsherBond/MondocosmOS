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


package es.igosoftware.globe.modules.locationManager;

import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import gov.nasa.worldwind.geom.Position;


public class GLocationManagerModule
         extends
            GAbstractGlobeModule {


   public GLocationManagerModule(final IGlobeRunningContext context) {
      super(context);
      GLocations.setDefaultLocation(new GNamedLocation("DEFAULT", Position.ZERO, 50000));
   }


   public GLocationManagerModule(final IGlobeRunningContext context,
                                 final Position position,
                                 final double elevation) {
      super(context);
      GLocations.setDefaultLocation(new GNamedLocation("DEFAULT", position, elevation));
   }


   @Override
   public String getDescription() {

      return "Location manager";

   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {

      final IGenericAction action = new GButtonGenericAction("Location manager", ' ', null, IGenericAction.MenuArea.NAVIGATION,
               false) {
         @Override
         public void execute() {
            final JDialog locationManager = new GLocationManagerDialog(context);
            locationManager.setVisible(true);
         }
      };

      return Collections.singletonList(action);
   }


   @Override
   public String getName() {
      return "Location manager";
   }


   @Override
   public String getVersion() {
      return null;
   }


   public void postInitialize() {


   }


}
