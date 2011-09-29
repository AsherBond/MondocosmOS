

package es.igosoftware.util;

import java.awt.Color;


public class GAWTUtils {


   private GAWTUtils() {

   }


   public static Color mix(final Color color1,
                           final Color color2) {
      return new Color(//
               (color1.getRed() + color2.getRed()) / 2, //
               (color1.getGreen() + color2.getGreen()) / 2, //
               (color1.getBlue() + color2.getBlue()) / 2, //
               (color1.getAlpha() + color2.getAlpha()) / 2);
   }


   public static Color mixAlpha(final Color color,
                                final float opacity) {
      return new Color(//
               color.getRed(), //
               color.getGreen(), //
               color.getBlue(), //
               (color.getAlpha() + (int) (opacity * 255)) / 2);
   }


}
