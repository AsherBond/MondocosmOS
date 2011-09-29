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


package es.unex.s3xtante.modules.layers;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.globe.layers.ESRIAsciiFileTools;
import es.igosoftware.globe.layers.GGlobeRasterLayer;
import es.igosoftware.io.GGenericFileFilter;


public class GAddEXtruded3DVectorLayer
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {


   public GAddEXtruded3DVectorLayer(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getName() {
      return "Add 3D vector layer";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Module to open 3d vector layers";
   }


   @Override
   public IGlobeLayer addNewLayer(final IGlobeRunningContext context,
                                  final ILayerInfo layerInfo) {

      final JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new GGenericFileFilter(new String[] {
         "asc"
      }, "ESRI ArcInfo ASCII (*.asc)"));
      final int returnVal = fc.showOpenDialog(context.getApplication().getFrame());

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         final Object[] possibleValues = GProjection.getEPSGProjections();
         final Object selectedValue = JOptionPane.showInputDialog(null, "Choose layer projection", "Projection",
                  JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
         if (selectedValue == null) {
            return null;
         }
         final String sFilename = fc.getSelectedFile().getAbsolutePath();
         try {
            final GGlobeRasterLayer rl = ESRIAsciiFileTools.readFile(new File(sFilename), (GProjection) selectedValue);
            if (rl != null) {
               context.getWorldWindModel().addLayer(rl);
               rl.setPickEnabled(false);
               return rl;
            }
         }
         catch (final Exception e) {
            e.printStackTrace();
         }
      }

      return null;
   }


   @Override
   public List<? extends ILayerInfo> getAvailableLayers(final IGlobeRunningContext context) {
      return Arrays.asList(new GLayerInfo("ESRI ArcInfo ASCII", null));
   }


}
