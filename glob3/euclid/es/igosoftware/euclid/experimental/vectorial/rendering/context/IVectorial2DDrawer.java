

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


public interface IVectorial2DDrawer {


   /* -------------------------------------------------------------------------------------- */
   /* Shape drawing */
   public void draw(final Shape shape,
                    final Paint paint,
                    final Stroke stroke);


   public void fill(final Shape shape,
                    final Paint paint);


   /* -------------------------------------------------------------------------------------- */
   /* Oval drawing */
   public void drawOval(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Paint paint,
                        final Stroke stroke);


   public void drawOval(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint,
                        final Stroke stroke);


   public void fillOval(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Paint paint);


   public void fillOval(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint);


   /* -------------------------------------------------------------------------------------- */
   /* PolyLine drawing */
   public void drawPolyline(final int[] xPoints,
                            final int[] yPoints,
                            final int length,
                            final Paint paint,
                            final Stroke stroke);


   public void drawPolyline(final GAWTPoints points,
                            final Paint paint,
                            final Stroke stroke);


   public void drawPolyline(final IPointsContainer<IVector2> pointsContainer,
                            final Paint paint,
                            final Stroke stroke);


   /* -------------------------------------------------------------------------------------- */
   /* Rect drawing */
   public void drawRect(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Paint paint,
                        final Stroke stroke);


   public void drawRect(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint,
                        final Stroke stroke);


   public void drawRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                        final Paint paint,
                        final Stroke stroke);


   public void drawRect(final Rectangle2D rectangle,
                        final Paint paint,
                        final Stroke stroke);


   public void fillRect(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Paint paint);


   public void fillRect(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint);


   public void fillRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                        final Paint paint);


   public void fillRect(final Rectangle2D rectangle,
                        final Paint paint);


   /* -------------------------------------------------------------------------------------- */
   /* Image drawing */
   public void drawImage(final Image image,
                         final double x,
                         final double y);


   public void drawImage(final Image image,
                         final double x,
                         final double y,
                         final double width,
                         final double height);


   public void drawImage(final Image image,
                         final IVector2 position);


   public void drawImage(final Image image,
                         final IVector2 position,
                         final IVector2 extent);


   public void drawImage(final BufferedImage image,
                         final double x,
                         final double y,
                         final float opacity);


   public void drawImage(final BufferedImage image,
                         final IVector2 position,
                         final float opacity);


   /* -------------------------------------------------------------------------------------- */
   /* String drawing */
   public void drawString(final String str,
                          final double x,
                          final double y,
                          final Paint paint);


   public void drawString(final String str,
                          final IVector2 position,
                          final Paint paint);


   public void drawShadowedString(final String str,
                                  final IVector2 position,
                                  final Paint paint,
                                  final double shadowOffset,
                                  final Paint shadowPaint);


   public void drawShadowedStringCentered(final String str,
                                          final IVector2 position,
                                          final Paint paint,
                                          final double shadowOffset,
                                          final Paint shadowPaint);


   public void drawShadowedStringCentered(final String str,
                                          final IVector2 position,
                                          final Font font,
                                          final Paint paint,
                                          final double shadowOffset,
                                          final Paint shadowPaint);


}
