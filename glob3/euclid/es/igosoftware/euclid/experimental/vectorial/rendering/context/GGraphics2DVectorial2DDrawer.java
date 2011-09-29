

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public abstract class GGraphics2DVectorial2DDrawer
         implements
            IVectorial2DDrawer {


   private final Graphics2D _g2d;


   protected GGraphics2DVectorial2DDrawer(final Graphics2D g2d) {
      GAssert.notNull(g2d, "g2d");

      _g2d = g2d;
   }


   @Override
   public final void finalize() {
      if (_g2d != null) {
         _g2d.dispose();
      }
   }


   @Override
   public final void draw(final Shape shape,
                          final Paint paint,
                          final Stroke stroke) {
      _g2d.setPaint(paint);
      _g2d.setStroke(stroke);
      _g2d.draw(shape);
   }


   @Override
   public final void fill(final Shape shape,
                          final Paint paint) {
      _g2d.setPaint(paint);
      _g2d.fill(shape);
   }


   @Override
   public final void drawOval(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint,
                              final Stroke stroke) {
      draw(new Ellipse2D.Double(x, y, width, height), paint, stroke);
   }


   @Override
   public final void drawOval(final IVector2 position,
                              final IVector2 extent,
                              final Paint paint,
                              final Stroke stroke) {
      drawOval(position.x(), position.y(), extent.x(), extent.y(), paint, stroke);
   }


   @Override
   public final void fillOval(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint) {
      fill(new Ellipse2D.Double(x, y, width, height), paint);
   }


   @Override
   public final void fillOval(final IVector2 position,
                              final IVector2 extent,
                              final Paint paint) {
      fillOval(position.x(), position.y(), extent.x(), extent.y(), paint);
   }


   @Override
   public final void drawPolyline(final int[] xPoints,
                                  final int[] yPoints,
                                  final int length,
                                  final Paint paint,
                                  final Stroke stroke) {
      _g2d.setPaint(paint);
      _g2d.setStroke(stroke);
      _g2d.drawPolyline(xPoints, yPoints, length);
   }


   @Override
   public final void drawPolyline(final GAWTPoints points,
                                  final Paint paint,
                                  final Stroke stroke) {
      drawPolyline(points._xPoints, points._yPoints, points._xPoints.length, paint, stroke);
   }


   @Override
   public final void drawPolyline(final IPointsContainer<IVector2> pointsContainer,
                                  final Paint paint,
                                  final Stroke stroke) {
      final int pointsCount = pointsContainer.getPointsCount();
      final int[] xPoints = new int[pointsCount];
      final int[] yPoints = new int[pointsCount];
      for (int i = 0; i < pointsCount; i++) {
         final IVector2 point = pointsContainer.getPoint(i);
         xPoints[i] = GMath.toRoundedInt(point.x());
         yPoints[i] = GMath.toRoundedInt(point.y());
      }


      drawPolyline(xPoints, yPoints, pointsCount, paint, stroke);
   }


   @Override
   public final void drawRect(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint,
                              final Stroke stroke) {
      draw(new Rectangle2D.Double(x, y, width, height), paint, stroke);
   }


   @Override
   public final void drawRect(final IVector2 position,
                              final IVector2 extent,
                              final Paint paint,
                              final Stroke stroke) {
      drawRect(position.x(), position.y(), extent.x(), extent.y(), paint, stroke);
   }


   @Override
   public final void drawRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                              final Paint paint,
                              final Stroke stroke) {
      drawRect(rectangle._lower, rectangle._extent, paint, stroke);
   }


   @Override
   public final void drawRect(final Rectangle2D rectangle,
                              final Paint paint,
                              final Stroke stroke) {
      drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), paint, stroke);
   }


   @Override
   public final void fillRect(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint) {
      fill(new Rectangle2D.Double(x, y, width, height), paint);
   }


   @Override
   public final void fillRect(final IVector2 position,
                              final IVector2 extent,
                              final Paint paint) {
      fillRect(position.x(), position.y(), extent.x(), extent.y(), paint);
   }


   @Override
   public final void fillRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                              final Paint paint) {
      fillRect(rectangle._lower, rectangle._extent, paint);
   }


   @Override
   public final void fillRect(final Rectangle2D rectangle,
                              final Paint paint) {
      fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), paint);
   }


   @Override
   public final void drawImage(final Image image,
                               final double x,
                               final double y) {
      _g2d.drawImage(image, //
               GMath.toRoundedInt(x), GMath.toRoundedInt(y), //
               null);
   }


   @Override
   public final void drawImage(final Image image,
                               final double x,
                               final double y,
                               final double width,
                               final double height) {
      _g2d.drawImage(image, //
               GMath.toRoundedInt(x), GMath.toRoundedInt(y), //
               GMath.toRoundedInt(width), GMath.toRoundedInt(height), //
               null);
   }


   @Override
   public final void drawImage(final BufferedImage image,
                               final double x,
                               final double y,
                               final float opacity) {

      if (opacity <= 0) {
         return;
      }

      if (opacity >= 1) {
         drawImage(image, x, y);
      }

      final float[] scales = {
                        1f,
                        1f,
                        1f,
                        opacity
      };
      final float[] offsets = new float[4];
      final BufferedImageOp rop = new RescaleOp(scales, offsets, null);


      _g2d.drawImage(image, rop, GMath.toRoundedInt(x), GMath.toRoundedInt(y));
   }


   @Override
   public final void drawImage(final Image image,
                               final IVector2 position) {
      drawImage(image, position.x(), position.y());
   }


   @Override
   public final void drawImage(final Image image,
                               final IVector2 position,
                               final IVector2 extent) {
      drawImage(image, position.x(), position.y(), extent.x(), extent.y());
   }


   @Override
   public final void drawImage(final BufferedImage image,
                               final IVector2 position,
                               final float opacity) {
      drawImage(image, position.x(), position.y(), opacity);
   }


   @Override
   public final void drawString(final String str,
                                final double x,
                                final double y,
                                final Paint paint) {
      _g2d.setPaint(paint);

      _g2d.drawString(str, (float) x, (float) y);
   }


   @Override
   public final void drawString(final String str,
                                final IVector2 position,
                                final Paint paint) {
      drawString(str, position.x(), position.y(), paint);
   }


   @Override
   public final void drawShadowedString(final String str,
                                        final IVector2 position,
                                        final Paint paint,
                                        final double shadowOffset,
                                        final Paint shadowPaint) {
      final double x = position.x();
      final double y = position.y();
      if (shadowOffset > 0) {
         drawString(str, x + 1, y - 1, shadowPaint);
         drawString(str, x + 1, y + 1, shadowPaint);
         drawString(str, x - 1, y - 1, shadowPaint);
         drawString(str, x - 1, y + 1, shadowPaint);
      }
      drawString(str, x, y, paint);
   }


   @Override
   public final void drawShadowedStringCentered(final String str,
                                                final IVector2 position,
                                                final Paint paint,
                                                final double shadowOffset,
                                                final Paint shadowPaint) {
      drawShadowedStringCentered(str, position, _g2d.getFont(), paint, shadowOffset, shadowPaint);
   }


   @Override
   public final void drawShadowedStringCentered(final String str,
                                                final IVector2 position,
                                                final Font font,
                                                final Paint paint,
                                                final double shadowOffset,
                                                final Paint shadowPaint) {

      final Font currentFont = _g2d.getFont();
      _g2d.setFont(font);

      final FontRenderContext frc = _g2d.getFontRenderContext();
      final Rectangle2D bounds = font.getStringBounds(str, frc);
      final LineMetrics metrics = font.getLineMetrics(str, frc);
      final double width = bounds.getWidth(); // The width of our text
      final float lineHeight = metrics.getHeight(); // Total line height
      final float ascent = metrics.getAscent(); // Top of text to baseline

      final double x = (position.x() + (0 - width) / 2);
      final double y = (position.y() + (0 - lineHeight) / 2 + ascent);

      if (shadowOffset > 0) {
         drawString(str, x + shadowOffset, y - shadowOffset, shadowPaint);
         drawString(str, x + shadowOffset, y + shadowOffset, shadowPaint);
         drawString(str, x - shadowOffset, y - shadowOffset, shadowPaint);
         drawString(str, x - shadowOffset, y + shadowOffset, shadowPaint);
      }
      drawString(str, x, y, paint);

      _g2d.setFont(currentFont);

   }


}
