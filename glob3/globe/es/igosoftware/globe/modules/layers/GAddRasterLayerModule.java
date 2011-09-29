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


package es.igosoftware.globe.modules.layers;

import java.awt.LinearGradientPaint;
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
import es.igosoftware.globe.attributes.GColorRampLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GSelectionLayerAttribute;
import es.igosoftware.globe.attributes.GStringLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.layers.GESRIAsciiFileTools;
import es.igosoftware.globe.layers.GGlobeRasterLayer;
import es.igosoftware.globe.layers.GRasterRenderer;
import es.igosoftware.io.GGenericFileFilter;
import gov.nasa.worldwind.layers.Layer;


public class GAddRasterLayerModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {


   private static final String COLOR_RAMP      = "COLOR_RAMP";
   private static final String COLORING_METHOD = "COLORING_METHOD";


   public GAddRasterLayerModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getName() {
      return "Add raster layer";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Module to open raster layers";
   }


   @SuppressWarnings("unchecked")
   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context,
                                                      final Layer layer) {

      final ILayerAttribute<?> rows = new GStringLayerAttribute("Rows", null, null, true) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public String get() {
            return Integer.toString(((GGlobeRasterLayer) layer).getRasterGeodata()._rows);
         }


         @Override
         public void set(final String value) {
         }

      };

      final ILayerAttribute<?> cols = new GStringLayerAttribute("Cols", null, null, true) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public String get() {
            return Integer.toString(((GGlobeRasterLayer) layer).getRasterGeodata()._cols);
         }


         @Override
         public void set(final String value) {
         }

      };

      final ILayerAttribute<?> nodata = new GFloatLayerAttribute("No-data value", null, null, false, Float.NEGATIVE_INFINITY,
               Float.POSITIVE_INFINITY, GFloatLayerAttribute.WidgetType.TEXTBOX, Float.MIN_VALUE) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public Float get() {
            return new Float(((GGlobeRasterLayer) layer).getNoDataValue());
         }


         @Override
         public void set(final Float value) {
            ((GGlobeRasterLayer) layer).setNoDataValue(value.doubleValue());
            ((GGlobeRasterLayer) layer).redraw();
         }

      };

      final String[] coloringMethods = new String[] {
                        "RGB",
                        "Color ramp",
                        "Lookup table"
      };
      final ILayerAttribute<?> method = new GSelectionLayerAttribute<String>("Coloring method", null, COLORING_METHOD,
               coloringMethods) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public String get() {
            return coloringMethods[(((GGlobeRasterLayer) layer).getRenderer().getColoringMethod())];
         }


         @Override
         public void set(final String value) {
            int iMethod = GRasterRenderer.COLORING_METHOD_COLOR_RAMP;
            if (value.equals("RGB")) {
               iMethod = GRasterRenderer.COLORING_METHOD_RGB;
            }
            else if (value.equals("Color ramp")) {
               iMethod = GRasterRenderer.COLORING_METHOD_COLOR_RAMP;
            }
            else if (value.equals("Lookup table")) {
               iMethod = GRasterRenderer.COLORING_METHOD_LUT;
            }
            ((GGlobeRasterLayer) layer).getRenderer().setColoringMethod(iMethod);
            ((GGlobeRasterLayer) layer).redraw();
         }

      };

      final ILayerAttribute<?> ramp = new GColorRampLayerAttribute("Color ramp", null, COLOR_RAMP) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public LinearGradientPaint get() {
            final GGlobeRasterLayer gRasterLayer = (GGlobeRasterLayer) layer;
            return gRasterLayer.getRenderer().getGradient();
         }


         @Override
         public void set(final LinearGradientPaint gradient) {
            ((GGlobeRasterLayer) layer).getRenderer().setGradient(gradient);
            ((GGlobeRasterLayer) layer).redraw();
         }

      };


      return Arrays.asList(rows, cols, nodata, method, ramp);
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
            final GGlobeRasterLayer rl = GESRIAsciiFileTools.readFile(new File(sFilename), (GProjection) selectedValue);
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
