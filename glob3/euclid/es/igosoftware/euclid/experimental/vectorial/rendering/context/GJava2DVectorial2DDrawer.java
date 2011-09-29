

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import es.igosoftware.util.GAssert;


public class GJava2DVectorial2DDrawer
         extends
            GGraphics2DVectorial2DDrawer {


   //   private final BufferedImage _image;


   public GJava2DVectorial2DDrawer(final BufferedImage image) {
      super(initializeG2D(image));

      //      _image = image;
   }


   private static Graphics2D initializeG2D(final BufferedImage image) {
      GAssert.notNull(image, "image");

      final Graphics2D g2d = image.createGraphics();

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      return g2d;
   }


}
