

package es.igosoftware.euclid.experimental.vectorial.rendering.symbols;


import java.awt.Color;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.util.GAssert;


public abstract class GSurface2DSymbol<

GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>

>

         extends
            GSymbol2D<GeometryT> {

   protected final ISurface2DStyle       _surfaceStyle;
   protected final ICurve2DStyle         _curveStyle;
   protected final GAxisAlignedRectangle _bounds;


   protected GSurface2DSymbol(final GeometryT geometry,
                              final String label,
                              final ISurface2DStyle surfaceStyle,
                              final ICurve2DStyle curveStyle,
                              final int priority,
                              final boolean groupable) {
      super(geometry, label, priority, groupable);

      GAssert.notNull(surfaceStyle, "surfaceStyle");
      GAssert.notNull(curveStyle, "curveStyle");

      _surfaceStyle = surfaceStyle;
      _curveStyle = curveStyle;

      _bounds = _geometry.getBounds().asAxisAlignedOrthotope();
   }


   @Override
   protected boolean isBigger(final double lodMinSize) {
      return (_bounds.area() > lodMinSize);
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer,
                                final boolean debugRendering) {
      drawer.fillRect(_bounds, debugRendering ? Color.MAGENTA : _surfaceStyle.getSurfacePaint());
   }


}
