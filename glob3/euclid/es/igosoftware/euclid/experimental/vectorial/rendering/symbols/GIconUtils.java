

package es.igosoftware.euclid.experimental.vectorial.rendering.symbols;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GImageUtils;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPair;
import es.igosoftware.util.LRUCache;


public class GIconUtils {


   private GIconUtils() {
      // private
   }


   private static class ImageData {
      private final float _percentFilled;
      private final Color _averageColor;


      private ImageData(final float percentFilled,
                        final Color averageColor) {
         _percentFilled = percentFilled;
         _averageColor = averageColor;
      }
   }


   private static final LRUCache<BufferedImage, ImageData, RuntimeException>                       imageDataCache;
   private static final LRUCache<GPair<BufferedImage, GVector2I>, BufferedImage, RuntimeException> scaleCache;


   static {
      imageDataCache = new LRUCache<BufferedImage, ImageData, RuntimeException>(20,
               new LRUCache.ValueFactory<BufferedImage, ImageData, RuntimeException>() {
                  @Override
                  public ImageData create(final BufferedImage image) {
                     return calculateImageData(image);
                  }
               });

      scaleCache = new LRUCache<GPair<BufferedImage, GVector2I>, BufferedImage, RuntimeException>(20,
               new LRUCache.ValueFactory<GPair<BufferedImage, GVector2I>, BufferedImage, RuntimeException>() {
                  @Override
                  public BufferedImage create(final GPair<BufferedImage, GVector2I> key) {
                     final BufferedImage image = key._first;
                     final GVector2I extent = key._second;

                     return GImageUtils.asBufferedImage(image.getScaledInstance(extent.x(), extent.y(), Image.SCALE_SMOOTH),
                              image.getType());
                  }
               });
   }


   private static ImageData calculateImageData(final BufferedImage image) {
      final int pixelsCount = image.getWidth() * image.getHeight();
      final double weightPerPixel = 1d / pixelsCount;

      double percentFilled = 0;

      double acumRed = 0;
      double acumGreen = 0;
      double acumBlue = 0;
      double acumAlpha = 0;

      for (int x = 0; x < image.getWidth(); x++) {
         for (int y = 0; y < image.getHeight(); y++) {
            final int pixel = image.getRGB(x, y);

            final int alpha = (pixel >>> 24) & 0xFF;
            final int red = (pixel >>> 16) & 0xFF;
            final int green = (pixel >>> 8) & 0xFF;
            final int blue = (pixel >>> 0) & 0xFF;

            percentFilled += weightPerPixel * (alpha / 255d);

            acumRed += red;
            acumGreen += green;
            acumBlue += blue;
            acumAlpha += alpha;
         }
      }

      final Color averageColor = new Color(//
               (int) (acumRed / pixelsCount), //
               (int) (acumGreen / pixelsCount), //
               (int) (acumBlue / pixelsCount), //
               (int) (acumAlpha / pixelsCount));

      return new ImageData((float) percentFilled, averageColor);
   }


   private static GIconUtils.ImageData getImageData(final BufferedImage icon) {
      return imageDataCache.get(icon);
   }


   public static BufferedImage getScaledImage(final BufferedImage icon,
                                              final IVector2 extent) {
      final GVector2I extentInt = new GVector2I(GMath.toRoundedInt(extent.x()), GMath.toRoundedInt(extent.y()));
      return scaleCache.get(new GPair<BufferedImage, GVector2I>(icon, extentInt));
   }


   public static float getPercentFilled(final BufferedImage icon) {
      return getImageData(icon)._percentFilled;
   }


   public static Color getAverageColor(final BufferedImage icon) {
      return getImageData(icon)._averageColor;
   }


}
