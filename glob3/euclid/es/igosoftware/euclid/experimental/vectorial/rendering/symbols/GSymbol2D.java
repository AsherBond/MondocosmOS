

package es.igosoftware.euclid.experimental.vectorial.rendering.symbols;

import java.awt.Color;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GNullCurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GSurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public abstract class GSymbol2D<

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

> {


   protected final GeometryT _geometry;
   private final String      _label;
   private final int         _priority;
   //   private int               _position = -1;
   private final boolean     _groupable;


   protected GSymbol2D(final GeometryT geometry,
                       final String label,
                       final int priority,
                       final boolean groupable) {
      GAssert.notNull(geometry, "geometry");

      _geometry = geometry;
      _label = label;
      _priority = priority;
      _groupable = groupable;
   }


   //   /* used from renderer */
   //   public void setPosition(final int position) {
   //      _position = position;
   //   }


   public int getPriority() {
      return _priority;
   }


   //   public int getPosition() {
   //      return _position;
   //   }


   public final void draw(final IVectorial2DDrawer drawer,
                          final double lodMinSize,
                          final boolean debugRendering,
                          final boolean renderLODIgnores) {
      //      GAssert.isTrue(_position != -1, "_position not initialized (" + this + ")");

      if (isBigger(lodMinSize)) {
         draw(drawer, debugRendering);
         if (_label != null) {
            drawLabel(drawer);
         }
      }
      else {
         if (renderLODIgnores) {
            drawLODIgnore(drawer, debugRendering);
         }
      }
   }


   private void drawLabel(final IVectorial2DDrawer drawer) {
      final IVector2 position = getBounds().getCentroid();

      drawer.drawShadowedStringCentered(_label, position, Color.BLACK, 1, new Color(255, 255, 255, 127));
   }


   protected abstract boolean isBigger(final double lodMinSize);


   protected abstract void draw(final IVectorial2DDrawer drawer,
                                final boolean debugRendering);


   protected abstract void drawLODIgnore(final IVectorial2DDrawer drawer,
                                         final boolean debugRendering);


   public GeometryT getGeometry() {
      return _geometry;
   }


   public final boolean isGroupable() {
      return _groupable;
   }


   public abstract boolean isGroupableWith(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that);


   public abstract GAxisAlignedRectangle getBounds();


   public final GSymbol2DList createGroupSymbols(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group) {

      boolean allSameClass = true;
      final Class<? extends GSymbol2D> klass = getClass();
      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         if (each.getClass() != klass) {
            allSameClass = false;
            break;
         }
      }

      final String label = Integer.toString(group.size());

      if (allSameClass) {
         final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> averageSymbol = getAverageSymbol(group,
                  label);
         //         averageSymbol.setPosition(_position);
         return new GSymbol2DList(averageSymbol);
      }

      final GSymbol2DList result = new GSymbol2DList();

      GAxisAlignedRectangle mergedBounds = null;
      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {

         final GAxisAlignedRectangle bounds = each.getBounds();
         mergedBounds = (mergedBounds == null) ? bounds : mergedBounds.mergedWith(bounds);

         result.add(each);
      }

      final ISurface2DStyle surfaceStyle = new GSurface2DStyle(GColorF.RED, 0.5f);
      final ICurve2DStyle curveStyle = GNullCurve2DStyle.INSTANCE;
      final GRectangle2DSymbol rectangle = new GRectangle2DSymbol(mergedBounds, label, surfaceStyle, curveStyle, 10000000, false);
      //      rectangle.setPosition(_position);
      result.add(rectangle);

      return result;
   }


   protected abstract GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> getAverageSymbol(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                                                                                             final String label);


}
