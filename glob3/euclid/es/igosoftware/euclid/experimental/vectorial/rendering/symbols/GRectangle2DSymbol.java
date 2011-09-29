

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
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;


public class GRectangle2DSymbol
         extends
            GSurface2DSymbol<GAxisAlignedRectangle> {


   public GRectangle2DSymbol(final GAxisAlignedRectangle rectangle,
                             final String label,
                             final ISurface2DStyle surfaceStyle,
                             final ICurve2DStyle curveStyle,
                             final int priority,
                             final boolean groupable) {
      super(rectangle, label, surfaceStyle, curveStyle, priority, groupable);
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {
      final IVector2 position = _geometry._lower;
      final IVector2 extent = _geometry._extent;


      // render surface
      final Paint fillPaint = _surfaceStyle.getSurfacePaint();
      if (fillPaint != null) {
         drawer.fillRect(position, extent, fillPaint);
      }


      // render border
      final Stroke borderStroke = _curveStyle.getBorderStroke();
      if (borderStroke != null) {
         final Paint borderPaint = _curveStyle.getBorderPaint();
         drawer.drawRect(position, extent, borderPaint, borderStroke);
      }
   }


   @Override
   public boolean isGroupableWith(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that) {
      if (that instanceof GRectangle2DSymbol) {
         final GRectangle2DSymbol thatRect = (GRectangle2DSymbol) that;
         return _surfaceStyle.isGroupableWith(thatRect._surfaceStyle) && _curveStyle.isGroupableWith(thatRect._curveStyle);
      }

      return false;
   }


   @Override
   public String toString() {
      return "GStyledRectangle2D [rectangle=" + _geometry + ", surfaceStyle=" + _surfaceStyle + ", curveStyle=" + _curveStyle
             + "]";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _geometry;
   }


   @Override
   protected GRectangle2DSymbol getAverageSymbol(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                                 final String label) {
      int maxPriority = Integer.MIN_VALUE;

      GVector2D sumLower = GVector2D.ZERO;
      GVector2D sumExtent = GVector2D.ZERO;
      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         final GRectangle2DSymbol eachEllipse = (GRectangle2DSymbol) each;
         final GAxisAlignedRectangle ellipse = eachEllipse._geometry;
         sumLower = sumLower.add(ellipse._lower);
         sumExtent = sumExtent.add(ellipse._extent);
         maxPriority = Math.max(maxPriority, each.getPriority());
      }

      final GVector2D averageLower = sumLower.div(group.size());
      final GVector2D averageExtent = sumExtent.div(group.size());

      return new GRectangle2DSymbol(new GAxisAlignedRectangle(averageLower, averageLower.add(averageExtent)), label,
               _surfaceStyle, _curveStyle, maxPriority, false);
   }


}
