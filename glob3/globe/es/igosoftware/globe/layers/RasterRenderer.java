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


package es.igosoftware.globe.layers;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;


public class RasterRenderer {


   public static final String      COLORING_METHOD            = "COLORING_METHOD";
   public static final int         COLORING_METHOD_RGB        = 0;
   public static final int         COLORING_METHOD_COLOR_RAMP = 1;
   public static final int         COLORING_METHOD_LUT        = 2;

   private final Raster            _raster;
   private final GGlobeRasterLayer _gRasterLayer;
   private int                     _coloringMethod            = COLORING_METHOD_COLOR_RAMP;
   private LinearGradientPaint     _gradient;


   public RasterRenderer(final GGlobeRasterLayer gRasterLayer) {

      _gRasterLayer = gRasterLayer;
      _raster = gRasterLayer.getRaster();
      _gradient = new LinearGradientPaint(0f, 0f, 1f, 1f, new float[] {
                        0f,
                        1f
      }, new Color[] {
                        Color.yellow,
                        Color.red
      });

   }


   public BufferedImage getImage() {

      final BufferedImage bi = new BufferedImage(_raster.getWidth(), _raster.getHeight(), BufferedImage.TYPE_INT_ARGB);

      if (_coloringMethod == COLORING_METHOD_RGB) {

         for (int y = 0; y < _raster.getHeight(); y++) {
            for (int x = 0; x < _raster.getWidth(); x++) {
               final double dValue = _raster.getSampleDouble(x, y, 0);
               bi.setRGB(x, y, (int) dValue);
            }
         }

      }
      else {

         double dMin = Double.MAX_VALUE;
         double dMax = Double.NEGATIVE_INFINITY;

         for (int y = 0; y < _raster.getHeight(); y++) {
            for (int x = 0; x < _raster.getWidth(); x++) {
               final double dValue = _raster.getSampleDouble(x, y, 0);
               dMin = Math.min(dMin, dValue);
               dMax = Math.max(dMax, dValue);
            }
         }

         for (int y = 0; y < _raster.getHeight(); y++) {
            for (int x = 0; x < _raster.getWidth(); x++) {
               double dValue = _raster.getSampleDouble(x, y, 0);
               dValue = (dValue - dMin) / (dMax - dMin);
               final int iColor = getColorFromColorRamp(dValue);
               bi.setRGB(x, y, iColor);
            }
         }
      }

      return bi;

   }


   private int getColorFromColorRamp(final double dValue) {

      final float[] fractions = _gradient.getFractions();
      final Color[] colors = _gradient.getColors();
      for (int i = 0; i < fractions.length - 1; i++) {
         if ((dValue >= fractions[i]) && (dValue <= fractions[i + 1])) {
            return getColorFromColorRamp(fractions[i], fractions[i + 1], colors[i], colors[i + 1], dValue);
         }
      }

      return colors[colors.length - 1].getRGB();

   }


   private int getColorFromColorRamp(final double dMin,
                                     final double dMax,
                                     Color color,
                                     final Color color2,
                                     final double dValue) {

      if (dValue == _gRasterLayer.getNoDataValue()) {
         return 0;//transparent
      }
      if (dValue <= dMin) {
         return color.getRGB();
      }

      if (dValue >= dMax) {
         return color2.getRGB();
      }

      final double dDif = dMax - dMin;
      if (dDif == 0) {
         return color.getRGB();
      }

      final double dDif2 = dValue - dMin;
      final double dRatio = dDif2 / dDif;
      final int dDifR = color2.getRed() - color.getRed();
      final int dDifG = color2.getGreen() - color.getGreen();
      final int dDifB = color2.getBlue() - color.getBlue();
      color = new Color((int) (color.getRed() + dDifR * dRatio), (int) (color.getGreen() + dDifG * dRatio),
               (int) (color.getBlue() + dDifB * dRatio));

      return color.getRGB();

   }


   public int getColoringMethod() {
      return _coloringMethod;
   }


   public LinearGradientPaint getGradient() {
      return _gradient;
   }


   public void setColoringMethod(final int coloringMethod) {
      _coloringMethod = coloringMethod;
   }


   public void setGradient(final LinearGradientPaint gradient) {
      _gradient = gradient;
   }


}
