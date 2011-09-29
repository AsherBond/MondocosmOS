

package es.igosoftware.euclid.utils;

import java.awt.Color;


public class GColorUtil {
   private GColorUtil() {
   }


   public static Color mix(final Color colorA,
                           final Color colorB,
                           final float alpha) {

      final float oneMinusAlpha = 1 - alpha;
      final int r = Math.round((colorA.getRed() * alpha) + (colorB.getRed() * oneMinusAlpha));
      final int g = Math.round((colorA.getGreen() * alpha) + (colorB.getGreen() * oneMinusAlpha));
      final int b = Math.round((colorA.getBlue() * alpha) + (colorB.getBlue() * oneMinusAlpha));
      final int a = Math.round((colorA.getAlpha() * alpha) + (colorB.getAlpha() * oneMinusAlpha));

      return new Color(r, g, b, a);
   }


}
