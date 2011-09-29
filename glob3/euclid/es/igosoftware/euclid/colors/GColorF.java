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

import java.util.Arrays;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public final class GColorF
         extends
            GColorAbstract {


   public static final GColorF BLACK   = new GColorF(0, 0, 0);
   public static final GColorF WHITE   = new GColorF(1, 1, 1);
   public static final GColorF RED     = new GColorF(1, 0, 0);
   public static final GColorF GREEN   = new GColorF(0, 1, 1);
   public static final GColorF BLUE    = new GColorF(0, 0, 1);
   public static final GColorF CYAN    = new GColorF(0, 1, 1);
   public static final GColorF YELLOW  = new GColorF(1, 1, 0);
   public static final GColorF MAGENTA = new GColorF(1, 0, 1);
   public static final GColorF GRAY    = new GColorF(0.5f, 0.5f, 0.5f);


   public static GColorF newHueSaturationBrightness(final double hue,
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


   public static GColorF newRGB(final float red,
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

      return new GColorF(clampedRed, clampedGreen, clampedBlue);
   }


   public static GColorF newRGB256(final int red,
                                   final int green,
                                   final int blue) {
      final int clampedRed = GMath.clamp(red, 0, 255);
      final int clampedGreen = GMath.clamp(green, 0, 255);
      final int clampedBlue = GMath.clamp(blue, 0, 255);

      return newRGB((float) clampedRed / 255, (float) clampedGreen / 255, (float) clampedBlue / 255);
   }


   public static GColorF fromAWTColor(final java.awt.Color awtColor) {
      return newRGB256(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
   }


   private final float _red;
   private final float _green;
   private final float _blue;


   private GColorF(final float red,
                   final float green,
                   final float blue) {
      GAssert.notNan(red, "red");
      GAssert.notNan(green, "green");
      GAssert.notNan(blue, "blue");

      _red = red;
      _green = green;
      _blue = blue;
   }


   @Override
   public float getRed() {
      return _red;
   }


   @Override
   public float getGreen() {
      return _green;
   }


   @Override
   public float getBlue() {
      return _blue;
   }


   @Override
   public GColorF add(final float delta) {
      return newRGB(getRed() + delta, getGreen() + delta, getBlue() + delta);
   }


   @Override
   public GColorF add(final IColor that) {
      return newRGB(getRed() + that.getRed(), getGreen() + that.getGreen(), getBlue() + that.getBlue());
   }


   @Override
   public GColorF adjustBrightness(final float brightness) {
      final float newBrightness = getBrightness() + brightness;
      return newHueSaturationBrightness(getHue(), getSaturation(), newBrightness);
   }


   @Override
   public GColorF adjustSaturation(final float saturation) {
      final float newSaturation = getSaturation() + saturation;
      return newHueSaturationBrightness(getHue(), newSaturation, getBrightness());
   }


   @Override
   public GColorF adjustSaturationBrightness(final float saturation,
                                             final float brightness) {
      final float newSaturation = getSaturation() + saturation;
      final float newBrightness = getBrightness() + brightness;
      return newHueSaturationBrightness(getHue(), newSaturation, newBrightness);
   }


   @Override
   public GColorF clamp(final IColor lower,
                        final IColor upper) {
      final float newRed = GMath.clamp(getRed(), lower.getRed(), upper.getRed());
      final float newGreen = GMath.clamp(getGreen(), lower.getGreen(), upper.getGreen());
      final float newBlue = GMath.clamp(getBlue(), lower.getBlue(), upper.getBlue());

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorF div(final float delta) {
      return newRGB(getRed() / delta, getGreen() / delta, getBlue() / delta);
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
      final GColorF other = (GColorF) obj;
      if (Float.floatToIntBits(_blue) != Float.floatToIntBits(other._blue)) {
         return false;
      }
      if (Float.floatToIntBits(_green) != Float.floatToIntBits(other._green)) {
         return false;
      }
      if (Float.floatToIntBits(_red) != Float.floatToIntBits(other._red)) {
         return false;
      }
      return true;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(_blue);
      result = prime * result + Float.floatToIntBits(_green);
      result = prime * result + Float.floatToIntBits(_red);
      return result;
   }


   @Override
   public GColorF mixedWidth(final IColor that,
                             final float alpha) {
      final float frac1 = GMath.clamp(alpha, 0, 1);
      final float frac2 = 1f - frac1;
      final float newRed = (getRed() * frac2) + (that.getRed() * frac1);
      final float newGreen = (getGreen() * frac2) + (that.getGreen() * frac1);
      final float newBlue = (getBlue() * frac2) + (that.getBlue() * frac1);

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorF max(final IColor that) {
      final float newRed = Math.max(getRed(), that.getRed());
      final float newGreen = Math.max(getGreen(), that.getGreen());
      final float newBlue = Math.max(getBlue(), that.getBlue());

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorF min(final IColor that) {
      final float newRed = Math.min(getRed(), that.getRed());
      final float newGreen = Math.min(getGreen(), that.getGreen());
      final float newBlue = Math.min(getBlue(), that.getBlue());

      return newRGB(newRed, newGreen, newBlue);
   }


   @Override
   public GColorF mul(final float delta) {
      return newRGB(getRed() * delta, getGreen() * delta, getBlue() * delta);
   }


   @Override
   public GColorF sub(final float delta) {
      return newRGB(getRed() - delta, getGreen() - delta, getBlue() - delta);
   }


   @Override
   public GColorF sub(final IColor that) {
      return newRGB(getRed() - that.getRed(), getGreen() - that.getGreen(), getBlue() - that.getBlue());
   }


   @Override
   public String toString() {
      return "GColorF [" + getRed() + ", " + getGreen() + ", " + getBlue() + "]";
   }


   @Override
   public float precision() {
      return GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT;
   }


   @Override
   public GColorF[] wheel(final int thisMany) {
      GAssert.isPositive(thisMany, "thisMany");


      //      | sat bri hue step c |
      //      sat := self saturation.
      //      bri := self brightness.
      //      hue := self hue.
      //      step := 360.0 / (thisMany max: 1).
      //      ^ (1 to: thisMany) collect: [:num |
      //              c := Color h: hue s: sat v: bri.  "hue is taken mod 360"
      //              hue := hue + step.
      //              c].

      final float sat = getSaturation();
      final float bri = getBrightness();
      double hue = getHue();
      final double step = GMath.DEGREES_360 / thisMany;

      final GColorF[] wheel = new GColorF[thisMany];

      for (int i = 0; i < thisMany; i++) {
         final GColorF c = newHueSaturationBrightness(hue, sat, bri);
         wheel[i] = c;
         hue += step;
      }

      return wheel;
   }


   //   mix: color2 shades: thisMany
   //   "Return an array of thisMany colors from self to color2. Very useful for displaying color based on a variable in your program.  "
   //   "Color showColors: (Color red mix: Color green shades: 12)"
   //
   //   | redInc greenInc blueInc rr gg bb c out |
   //   thisMany = 1 ifTrue: [^ Array with: color2].
   //   redInc := color2 red - self red / (thisMany-1).
   //   greenInc := color2 green - self green / (thisMany-1).
   //   blueInc := color2 blue - self blue / (thisMany-1).
   //   rr := self red.  gg := self green.  bb := self blue.
   //   out := (1 to: thisMany) collect: [:num |
   //           c := Color r: rr g: gg b: bb.
   //           rr := rr + redInc.
   //           gg := gg + greenInc.
   //           bb := bb + blueInc.
   //           c].
   //   out at: out size put: color2.   "hide roundoff errors"
   //   ^ out

   @Override
   public GColorF[] mix(final IColor otherColor,
                        final int thisMany) {
      GAssert.isPositive(thisMany, "thisMany");

      final GColorF[] result = new GColorF[thisMany];

      if (thisMany == 1) {
         result[0] = (GColorF) otherColor;
         return result;
      }

      final float redInc = (otherColor.getRed() - getRed()) / (thisMany - 1);
      final float greenInc = (otherColor.getGreen() - getGreen()) / (thisMany - 1);
      final float blueInc = (otherColor.getBlue() - getBlue()) / (thisMany - 1);

      float rr = getRed();
      float gg = getGreen();
      float bb = getBlue();

      for (int i = 0; i < thisMany; i++) {
         final GColorF c = newRGB(rr, gg, bb);
         result[i] = c;
         rr += redInc;
         gg += greenInc;
         bb += blueInc;
      }

      return result;
   }


   public static void main(final String[] args) {

      System.out.println(Math.toDegrees(GColorF.BLUE.getHue()));

      final GColorF[] wheel = GColorF.BLUE.wheel(8);

      System.out.println(Arrays.toString(wheel));

      final GColorF[] mix = GColorF.GREEN.mix(GColorF.GREEN.muchDarker(), 8);

      System.out.println(Arrays.toString(mix));
   }
}
