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


package es.igosoftware.euclid.colors;

import es.igosoftware.util.GMath;


public abstract class GColorAbstract
         implements
            IColor {


   @Override
   public final boolean between(final IColor min,
                                final IColor max) {
      final float precision = GMath.maxF(precision(), min.precision(), max.precision());
      return GMath.between(getRed(), min.getRed(), max.getRed(), precision)
             && GMath.between(getGreen(), min.getGreen(), max.getGreen(), precision)
             && GMath.between(getBlue(), min.getBlue(), max.getBlue(), precision);
   }


   @Override
   public final float getBrightness() {
      return GMath.maxF(getRed(), getGreen(), getBlue());
   }


   @Override
   public final float getLuminance() {
      return 0.299f * getRed() + 0.587f * getGreen() + 0.114f * getBlue();
   }


   @Override
   public final double getHue() {
      final float r = getRed();
      final float g = getGreen();
      final float b = getBlue();

      final float max = GMath.maxF(r, g, b);
      final float min = GMath.minF(r, g, b);

      final float span = (max - min);

      if (GMath.closeToZero(span)) {
         return 0;
      }

      final double h;
      if (r == max) {
         h = ((g - b) / span) * GMath.DEGREES_60;
      }
      else if (g == max) {
         h = (GMath.DEGREES_60 * 2) + (((b - r) / span) * GMath.DEGREES_60);
      }
      else {
         h = (GMath.DEGREES_60 * 4) + (((r - g) / span) * GMath.DEGREES_60);
      }

      if (h < 0) {
         return GMath.DEGREES_360 + h;
      }

      return h;


      //      hue
      //      "Return the hue of this color, an angle in the range [0.0..360.0]."
      //
      //      | r g b max min span h |
      //      r := self privateRed.
      //      g := self privateGreen.
      //      b := self privateBlue. 
      //
      //      max := ((r max: g) max: b).
      //      min := ((r min: g) min: b).
      //      span := (max - min) asFloat.
      //      span = 0.0 ifTrue: [ ^ 0.0 ].
      //
      //      r = max ifTrue: [
      //              h := ((g - b) asFloat / span) * 60.0.
      //      ] ifFalse: [
      //              g = max
      //                      ifTrue: [ h := 120.0 + (((b - r) asFloat / span) * 60.0). ]
      //                      ifFalse: [ h := 240.0 + (((r - g) asFloat / span) * 60.0). ].
      //      ].
      //
      //      h < 0.0 ifTrue: [ h := 360.0 + h ].
      //      ^ h
   }


   @Override
   public final float getSaturation() {
      final float r = getRed();
      final float g = getGreen();
      final float b = getBlue();

      final float max = GMath.maxF(r, g, b);
      final float min = GMath.minF(r, g, b);

      if (GMath.closeToZero(max)) {
         return 0;
      }

      return (max - min) / max;
   }


   @Override
   public final boolean closeTo(final IColor that) {
      final float precision = Math.max(precision(), that.precision());
      return GMath.closeTo(getRed(), that.getRed(), precision) && GMath.closeTo(getGreen(), that.getGreen(), precision)
             && GMath.closeTo(getBlue(), that.getBlue(), precision);
   }


   @Override
   public final IColor darker() {
      return adjustBrightness(-0.08f);
   }


   @Override
   public final IColor twiceDarker() {
      return adjustBrightness(-0.15f);
   }


   @Override
   public final IColor muchDarker() {
      return GColorF.BLACK.mixedWidth(this, 0.5f);
   }


   @Override
   public final IColor lighter() {
      return adjustSaturationBrightness(-0.03f, 0.08f);
   }


   @Override
   public final IColor twiceLighter() {
      return adjustSaturationBrightness(-0.06f, 0.15f);
   }


   @Override
   public final IColor muchLighter() {
      return GColorF.WHITE.mixedWidth(this, 0.233f);
   }


   @Override
   public final java.awt.Color asAWTColor() {
      return new java.awt.Color(getRed(), getGreen(), getBlue());
   }


   @Override
   public final java.awt.Color asAWTColor(final float opacity) {
      return new java.awt.Color(getRed(), getGreen(), getBlue(), opacity);
   }


   @Override
   public String toHexString() {
      final int iRed = Math.round(255 * getRed());
      final int iGreen = Math.round(255 * getGreen());
      final int iBlue = Math.round(255 * getBlue());

      return toHexString(iRed) + toHexString(iGreen) + toHexString(iBlue);
   }


   private static String toHexString(final int value) {
      final String hex = Integer.toHexString(value).toUpperCase();
      if (hex.length() == 1) {
         return "0" + hex;
      }
      return hex;
   }


   @Override
   public String toInt256String() {
      return "(" + Math.round(getRed() * 255) + ", " + Math.round(getGreen() * 255) + ", " + Math.round(getBlue() * 255) + ")";
   }

}
