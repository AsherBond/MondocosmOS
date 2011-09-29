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
import es.igosoftware.globe.actions.GCheckBoxGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import gov.nasa.worldwind.layers.LatLonGraticuleLayer;
import gov.nasa.worldwind.layers.Layer;


public class GShowLatLonGraticuleModule
         extends
            GAbstractGlobeModule {


   public GShowLatLonGraticuleModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getDescription() {
      return "Show Lat-Lon Graticule";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {

      final IGenericAction graticule = new GCheckBoxGenericAction("Show Lat-Lon Graticule", ' ', null,
               IGenericAction.MenuArea.VIEW, false, false) {

         @Override
         public void execute() {
            final List<Layer> layers = context.getWorldWindModel().getLayerList().getLayersByClass(LatLonGraticuleLayer.class);
            if (layers.size() != 0) {
               layers.get(0).setEnabled(isSelected());
            }
         }

      };

      //      return new IGenericAction[] { graticule };
      return Collections.singletonList(graticule);
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public String getName() {

      return "Show Lat-Lon Graticule";
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {
      final LatLonGraticuleLayer graticuleLayer = new LatLonGraticuleLayer();
      graticuleLayer.setEnabled(false);
      context.getWorldWindModel().addLayer(graticuleLayer);
   }


}
