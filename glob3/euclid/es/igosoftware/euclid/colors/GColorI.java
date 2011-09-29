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

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


/**
 * IColor implementation using 20 bits of precision for each color component
 * 
 * @author dgd
 * 
 */
public final class GColorI
         extends
            GColorAbstract {


   public static final GColorI BLACK       = new GColorI(0, 0, 0);
   public static final IColor  GRAY        = new GColorI(0.5f, 0.5f, 0.5f);
   public static final GColorI WHITE       = new GColorI(1, 1, 1);
   public static final GColorI RED         = new GColorI(1, 0, 0);
   public static final GColorI GREEN       = new GColorI(0, 1, 0);
   public static final GColorI BLUE        = new GColorI(0, 0, 1);
   public static final GColorI CYAN        = new GColorI(0, 1, 1);
   public static final GColorI YELLOW      = new GColorI(1, 1, 0);
   public static final GColorI MAGENTA     = new GColorI(1, 0, 1);

   private static final int    RED_SHIFT   = 20;
   private static final int    GREEN_SHIFT = 10;
   private static final int    MASK        = 1023;

   private static final float  PRECISION   = 1f / 1024;


   public static GColorI newHueSaturationBrightness(final double hue,
                                                    final float saturation,
                                                    final float brightness) {
      final float s = GMath.clamp(saturation, 0, 1);
      final float v = GMath.clamp(brightness, 0, 1);

      //  zero saturation yields gray with the given brightness
      if (GMath.closeToZero(s)) {
         return newRGB(v, v, v);
      }

      //final float hf = (float) ((hue % GMath.DEGREES_360) / GMath.DEGREES_60);
      final float hf = (float) (GMath.pseudoModule(hue, GMath.DEGREES_360) / GMath.DEGREES_60);

      final int i = (int) hf; //integer part of hue
      final float f = hf - i; //fractional part of hue

      final float p = (1 - s) * v;
      final float q = (1 - (s * f)) * v;
      final float t = (1 - (s * (1 - f))) * v;

      switch (i) {
         case 0:
            return newRGB(v, t, p);
         case 1:
            return newRGB(q, v, p);
         case 2:
            return newRGB(p, v, t);
         case 3:
            return newRGB(p, q, v);
         case 4:
            return newRGB(t, p, v);
         case 5:
            return newRGB(v, p, q);
         default:
            throw new IllegalArgumentException("Implementation error");
      }
   }


   public static GColorI newRGB(final float red,
                                final float green,
                                final float blue) {
      final float clampedRed = GMath.clamp(red, 0, 1);
      final float clampedGreen = GMath.clamp(green, 0, 1);
      final float clampedBlue = GMath.clamp(blue, 0, 1);

      if (GMath.closeToZero(clampedRed, clampedGreen, clampedBlue)) {
         return BLACK;
      }

      if (GMath.closeToOne(clampedRed, clampedGreen, clampedBlue)) {
         return WHITE;
      }

      return new GColorI(clampedRed, clampedGreen, clampedBlue);
   }


   public static GColorI newRGB256(final int red,
                                   final int green,
                                   final int blue) {
      final int clampedRed = GMath.clamp(red, 0, 255);
      final int clampedGreen = GMath.clamp(green, 0, 255);
      final int clampedBlue = GMath.clamp(blue, 0, 255);

      return newRGB((float) clampedRed / 255, (float) clampedGreen / 255, (float) clampedBlue / 255);
   }


   public static GColorI fromAWTColor(final java.awt.Color awtColor) {
      return newRGB256(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
   }


   private final int _rgb;


   private GColorI(final float red,
                   final float green,
                   final float blue) {
      this(((Math.round(red * MASK) & MASK) << RED_SHIFT) + //
           ((Math.round(green * MASK) & MASK) << GREEN_SHIFT) + //  
           ((Math.round(blue * MASK) & MASK)));
   }


   private GColorI(final int rgb) {
      _rgb = rgb;
   }


   @Override
   public float getRed() {
      return (float) ((_rgb >> RED_SHIFT) & MASK) / MASK;
   }


   @Override
   public float getGreen() {
      return (float) ((_rgb >> GREEN_SHIFT) & MASK) / MASK;
   }


   @Override
   public float getBlue() {
      return (float) (_rgb & MASK) / MASK;
   }


   @Override
   public GColorI add(final float delta) {
      return newRGB(getRed() + delta, getGreen() + delta, getBlue() + delta);
   }


   @Override
   public GColorI add(final IColor that) {
      return newRGB(getRed() + that.getRed(), getGreen() + that.getGreen(), getBlue() + that.getBlue());
   }


   @Override
   public GColorI adjustBrightness(final float brightness) {
      final float newBrightness = getBrightness() + brightness;
      return newHueSaturationBrightness(getHue(), getSaturation(), newBrightness);
   }


   @Override
   public GColorI adjustSaturation(final float saturation) {
      final float newSaturation = getSaturation() + saturation;
      return newHueSaturationBrightness(getHue(), newSaturation, getBrightness());
   }


   @Override
   public GColorI adjustSaturationBrightness(final float saturation,
                                             final float brightness) {
      final float newSaturation = getSaturation() + saturation;
      final float newBrightness = getBrightness() + brightness;
      return newHueSaturationBrightness(getHue(), newSaturation, newBrightness);
   }


   @Override
   public GColorI clamp(final IColor lower,
                        final IColor upper) {
      final float newRed = GMath.clamp(getRed(), lower.getRed(), upper.getRed());
      final float newGreen = GMath.clamp(getGreen(), lower.getGreen(), upper.getGreen());
      final float newBlue = GMath.clamp(getBlue(), lower.getBlue(), upper.getBlue());

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorI div(final float delta) {
      return newRGB(getRed() / delta, getGreen() / delta, getBlue() / delta);
   }


   @Override
   public GColorI mixedWidth(final IColor that,
                             final float alpha) {
      final float frac1 = GMath.clamp(alpha, 0, 1);
      final float frac2 = 1f - frac1;
      final float newRed = (getRed() * frac2) + (that.getRed() * frac1);
      final float newGreen = (getGreen() * frac2) + (that.getGreen() * frac1);
      final float newBlue = (getBlue() * frac2) + (that.getBlue() * frac1);

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorI max(final IColor that) {
      final float newRed = Math.max(getRed(), that.getRed());
      final float newGreen = Math.max(getGreen(), that.getGreen());
      final float newBlue = Math.max(getBlue(), that.getBlue());

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorI min(final IColor that) {
      final float newRed = Math.min(getRed(), that.getRed());
      final float newGreen = Math.min(getGreen(), that.getGreen());
      final float newBlue = Math.min(getBlue(), that.getBlue());

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorI mul(final float delta) {
      return newRGB(getRed() * delta, getGreen() * delta, getBlue() * delta);
   }


   @Override
   public GColorI sub(final float delta) {
      return newRGB(getRed() - delta, getGreen() - delta, getBlue() - delta);
   }


   @Override
   public GColorI sub(final IColor that) {
      return newRGB(getRed() - that.getRed(), getGreen() - that.getGreen(), getBlue() - that.getBlue());
   }


   @Override
   public String toString() {
      return "GColorI [" + getRed() + ", " + getGreen() + ", " + getBlue() + "]";
   }


   @Override
   public int hashCode() {
      return _rgb;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GColorI other = (GColorI) obj;
      return _rgb == other._rgb;
   }


   @Override
   public float precision() {
      return PRECISION;
   }


   public static GColorI newRGB(final int rgb) {
      return new GColorI(rgb);
   }


   public static int getRGB(final IColor color) {
      final float red = color.getRed();
      final float green = color.getGreen();
      final float blue = color.getBlue();
      return ((Math.round(red * MASK) & MASK) << RED_SHIFT) + //
             ((Math.round(green * MASK) & MASK) << GREEN_SHIFT) + //  
             ((Math.round(blue * MASK) & MASK));
   }


   @Override
   public GColorI[] wheel(final int thisMany) {
      GAssert.isPositive(thisMany, "thisMany");

      final float sat = getSaturation();
      final float bri = getBrightness();
      double hue = getHue();
      final double step = GMath.DEGREES_360 / thisMany;

      final GColorI[] wheel = new GColorI[thisMany];

      for (int i = 0; i < thisMany; i++) {
         final GColorI c = newHueSaturationBrightness(hue, sat, bri);
         wheel[i] = c;
         hue += step;
      }

      return wheel;
   }


   @Override
   public GColorI[] mix(final IColor otherColor,
                        final int thisMany) {
      GAssert.isPositive(thisMany, "thisMany");

      final GColorI[] result = new GColorI[thisMany];

      if (thisMany == 1) {
         result[0] = (GColorI) otherColor;
         return result;
      }

      final float redInc = (otherColor.getRed() - getRed()) / (thisMany - 1);
      final float greenInc = (otherColor.getGreen() - getGreen()) / (thisMany - 1);
      final float blueInc = (otherColor.getBlue() - getBlue()) / (thisMany - 1);

      float rr = getRed();
      float gg = getGreen();
      float bb = getBlue();

      for (int i = 0; i < thisMany; i++) {
         final GColorI c = newRGB(rr, gg, bb);
         result[i] = c;
         rr += redInc;
         gg += greenInc;
         bb += blueInc;
      }

      return result;
   }


   //   public static void main(final String[] args) {
   //      System.out.println(WHITE);
   //
   //      System.out.println(BLACK);
   //
   //      System.out.println(newRGB(0.1f, 0.1f, 0.1f));
   //
   //      System.out.println(newRGB(0.5f, 0.5f, 0.5f));
   //
   //      System.out.println(newRGB(0.7f, 0.7f, 0.7f));
   //
   //      System.out.println(newRGB(0.9f, 0.9f, 0.9f));
   //
   //      System.out.println(GColorF.newRGB(0.1f, 0.1f, 0.1f).closeTo(GColorI.newRGB(0.1f, 0.1f, 0.1f)));
   //      System.out.println(GColorI.newRGB(0.1f, 0.1f, 0.1f).closeTo(GColorF.newRGB(0.1f, 0.1f, 0.1f)));
   //   }


}
