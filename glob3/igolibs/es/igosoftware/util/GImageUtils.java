

package es.igosoftware.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;


public class GImageUtils {


   private GImageUtils() {
      // do not instantiate, static methods only
   }


   public static BufferedImage asBufferedImage(final Image image,
                                               final int imageType) {

      if (image == null) {
         return null;
      }

      if (image instanceof BufferedImage) {
         final BufferedImage bufferedImage = (BufferedImage) image;
         if (bufferedImage.getType() == imageType) {
            return bufferedImage;
         }
      }

      final BufferedImage burrefedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), imageType);

      final Graphics2D g2d = burrefedImage.createGraphics();
      g2d.drawImage(image, 0, 0, null);
      g2d.dispose();

      return burrefedImage;
   }


}
