

package es.igosoftware.euclid.experimental.vectorial.rendering.symbols;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GUtils;


public class GIcon2DSymbol
         extends
            GSymbol2D<IVector2> {


   private final String                _iconName;
   private final BufferedImage         _icon;
   private final float                 _opacity;

   private final float                 _percentFilled;
   private final Color                 _averageColor;

   private final GAxisAlignedRectangle _bounds;


   public GIcon2DSymbol(final IVector2 position,
                        final String label,
                        final String iconName,
                        final BufferedImage icon,
                        final float opacity,
                        final int priority,
                        final boolean groupable) {
      super(position, label, priority, groupable);

      GAssert.notNull(icon, "icon");

      _icon = icon;
      _opacity = opacity;
      _iconName = iconName;
      _percentFilled = GIconUtils.getPercentFilled(icon);
      _averageColor = GIconUtils.getAverageColor(icon);

      final GVector2D iconExtent = new GVector2D(_icon.getWidth(), _icon.getHeight());
      _bounds = new GAxisAlignedRectangle(_geometry, _geometry.add(iconExtent));
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {
      drawer.drawImage(_icon, _geometry, _opacity);
   }


   @Override
   public String toString() {
      return "GStyledIcon2D [position=" + _geometry + ", icon=" + _icon + ", opacity=" + _opacity + "]";
   }


   @Override
   protected boolean isBigger(final double lodMinSize) {
      return (_bounds.area() * _percentFilled > lodMinSize);
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer,
                                final boolean debugRendering) {
      drawer.fillRect(_bounds, debugRendering ? Color.MAGENTA : _averageColor);
   }


   @Override
   public boolean isGroupableWith(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that) {
      if (that instanceof GIcon2DSymbol) {
         final GIcon2DSymbol thatIcon = (GIcon2DSymbol) that;
         return GUtils.equals(_iconName, thatIcon._iconName);
      }

      return false;
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _bounds;
   }


   @Override
   protected GIcon2DSymbol getAverageSymbol(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                            final String label) {

      int maxPriority = Integer.MIN_VALUE;
      GVector2D sumPosition = GVector2D.ZERO;
      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         final GIcon2DSymbol eachEllipse = (GIcon2DSymbol) each;
         final IVector2 point = eachEllipse._geometry;
         sumPosition = sumPosition.add(point);
         maxPriority = Math.max(maxPriority, each.getPriority());
      }

      final GVector2D averagePosition = sumPosition.div(group.size());

      return new GIcon2DSymbol(averagePosition, label, _iconName, _icon, _opacity, maxPriority, false);
   }

}
