

package es.igosoftware.euclid.experimental.vectorial.rendering.symbols;


import java.awt.Color;

import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.util.GAssert;


public abstract class GCurve2DSymbol<

GeometryT extends ICurve2D<? extends IFinite2DBounds<?>>

>

         extends
            GSymbol2D<GeometryT> {


   protected final ICurve2DStyle         _curveStyle;
   protected final GAxisAlignedRectangle _bounds;


   protected GCurve2DSymbol(final GeometryT geometry,
                            final String label,
                            final ICurve2DStyle curveStyle,
                            final int priority,
                            final boolean groupable) {
      super(geometry, label, priority, groupable);

      GAssert.notNull(curveStyle, "curveStyle");

      _curveStyle = curveStyle;

      _bounds = geometry.getBounds().asAxisAlignedOrthotope();
   }


   @Override
   protected boolean isBigger(final double lodMinSize) {
      //      return (_bounds.area() > lodMinSize) || (_bounds.perimeter() > lodMinSize);
      //      if (!(_bounds.perimeter() > lodMinSize)) {
      //         System.out.println("rejecting perimeter=" + _bounds.perimeter() + " -> " + _geometry);
      //      }
      return (_bounds.perimeter() > lodMinSize);
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer,
                                final boolean debugRendering) {
      drawer.fillRect(_bounds, debugRendering ? Color.MAGENTA : _curveStyle.getBorderPaint());
   }


}
