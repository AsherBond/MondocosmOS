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


package es.igosoftware.experimental.ndimensional;


import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;


public class GMultidimensionalDataModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {

   private final IMultidimensionalData[] _multidimentionalDatas;


   //   private boolean                       _isVerticalExagerated = true;


   public GMultidimensionalDataModule(final IGlobeRunningContext context,
                                      final IMultidimensionalData... multidimentionalData) {
      super(context);

      GAssert.notEmpty(multidimentionalData, "multidimentionalData");
      _multidimentionalDatas = multidimentionalData;
   }


   @Override
   public String getName() {
      return "Multidimensional Data Module";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Module to handle multidimensional data";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {


      //      final Icon icon = null;
      //      final IGenericAction action = new GCheckBoxGenericAction("Vertical Exageration", ' ', icon, IGenericAction.MenuArea.VIEW,
      //               true, _isVerticalExagerated) {
      //
      //         @Override
      //         public void execute() {
      //            _isVerticalExagerated = !_isVerticalExagerated;
      //
      //            application.getWorldWindowGLCanvas().getSceneController().setVerticalExaggeration(_isVerticalExagerated ? 16 : 1);
      //            application.redraw();
      //         }
      //      };
      //
      //      return Collections.singletonList(action);
      return null;
   }


   @Override
   public List<? extends ILayerInfo> getAvailableLayers(final IGlobeRunningContext context) {
      final String[] names = new String[_multidimentionalDatas.length];

      for (int i = 0; i < _multidimentionalDatas.length; i++) {
         names[i] = _multidimentionalDatas[i].getName();
      }

      return GLayerInfo.createFromNames(context.getBitmapFactory().getSmallIcon(GFileName.relative("pointscloud.png")), names);
   }


   @Override
   public IGlobeLayer addNewLayer(final IGlobeRunningContext context,
                                  final ILayerInfo layerInfo) {


      IMultidimensionalData data = null;
      for (final IMultidimensionalData each : _multidimentionalDatas) {
         if (each.getName().equals(layerInfo.getName())) {
            data = each;
            break;
         }
      }

      if (data == null) {
         return null;
      }


      final GMultidimensionalViewerLayer layer = new GMultidimensionalViewerLayer(context, data);
      context.getWorldWindModel().addLayer(layer);
      return layer;
   }


}
