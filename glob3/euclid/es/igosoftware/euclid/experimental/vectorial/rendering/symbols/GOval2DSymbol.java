

package es.igosoftware.euclid.experimental.vectorial.rendering.symbols;

import java.awt.Paint;
import java.awt.Stroke;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.vector.GVector2D;


public class GOval2DSymbol
         extends
            GSurface2DSymbol<GAxisAlignedOval2D> {


   public GOval2DSymbol(final GAxisAlignedOval2D oval,
                        final String label,
                        final ISurface2DStyle surfaceStyle,
                        final ICurve2DStyle curveStyle,
                        final int priority,
                        final boolean groupable) {
      super(oval, label, surfaceStyle, curveStyle, priority, groupable);
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {

      final GAxisAlignedRectangle bounds = _geometry.getBounds();

      // render surface
      final Paint fillPaint = _surfaceStyle.getSurfacePaint();
      if (fillPaint != null) {
         drawer.fillOval(bounds._lower, bounds._extent, fillPaint);
      }


      // render border
      final Stroke borderStroke = _curveStyle.getBorderStroke();
      if (borderStroke != null) {
         final Paint borderPaint = _curveStyle.getBorderPaint();
         drawer.drawOval(bounds._lower, bounds._extent, borderPaint, borderStroke);
      }
   }


   @Override
   public boolean isGroupableWith(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that) {
      if (that instanceof GOval2DSymbol) {
         final GOval2DSymbol thatOval = (GOval2DSymbol) that;
         return _surfaceStyle.isGroupableWith(thatOval._surfaceStyle) && _curveStyle.isGroupableWith(thatOval._curveStyle);
      }

      return false;
   }


   @Override
   public String toString() {
      return "GStyledEllipse2D [oval=" + _geometry + ", surfaceStyle=" + _surfaceStyle + ", curveStyle=" + _curveStyle + "]";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _geometry.getBounds();
   }


   @Override
   protected GOval2DSymbol getAverageSymbol(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                            final String label) {

      int maxPriority = Integer.MIN_VALUE;
      GVector2D sumCenter = GVector2D.ZERO;
      GVector2D sumRadius = GVector2D.ZERO;
      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         final GOval2DSymbol eachEllipse = (GOval2DSymbol) each;
         final GAxisAlignedOval2D oval = eachEllipse._geometry;
         sumCenter = sumCenter.add(oval._center);
         sumRadius = sumRadius.add(oval._radius);
         maxPriority = Math.max(maxPriority, each.getPriority());
      }

      final GVector2D averageCenter = sumCenter.div(group.size());
      final GVector2D averageRadius = sumRadius.div(group.size());

      return new GOval2DSymbol(new GAxisAlignedOval2D(averageCenter, averageRadius), label, _surfaceStyle, _curveStyle,
               maxPriority, false);
   }


}
