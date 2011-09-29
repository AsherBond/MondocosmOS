

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.Paint;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.util.GMath;


public class GSurface2DStyle
         implements
            ISurface2DStyle {


   private final IColor _color;
   private final float  _opacity;


   public GSurface2DStyle(final IColor color,
                          final float opacity) {
      _color = color;
      _opacity = GMath.clamp(opacity, 0, 1);
   }


   @Override
   public Paint getSurfacePaint() {
      return _color.asAWTColor(_opacity);
   }


   @Override
   public String toString() {
      return "GSurface2DStyle [color=" + _color + ", opacity=" + _opacity + "]";
   }


   @Override
   public boolean isGroupableWith(final ISurface2DStyle that) {
      if (that instanceof GSurface2DStyle) {
         final GSurface2DStyle thatStyle = (GSurface2DStyle) that;
         return _color.closeTo(thatStyle._color) && GMath.closeTo(_opacity, thatStyle._opacity);
      }

      return false;
   }


}
