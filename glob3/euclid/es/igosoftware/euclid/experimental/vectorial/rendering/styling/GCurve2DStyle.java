

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.util.GMath;


public class GCurve2DStyle
         implements
            ICurve2DStyle {


   private final float  _borderWidth;
   private final IColor _color;
   private final float  _opacity;


   public GCurve2DStyle(final float borderWidth,
                        final IColor color,
                        final float opacity) {
      _borderWidth = borderWidth;
      _color = color;
      _opacity = GMath.clamp(opacity, 0, 1);
   }


   @Override
   public Stroke getBorderStroke() {
      return new BasicStroke(_borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
   }


   @Override
   public Paint getBorderPaint() {
      return _color.asAWTColor(_opacity);
   }


   @Override
   public String toString() {
      return "GCurve2DStyle [borderWidth=" + _borderWidth + ", _color=" + _color + ", opacity=" + _opacity + "]";
   }


   @Override
   public boolean isGroupableWith(final ICurve2DStyle that) {
      if (that instanceof GCurve2DStyle) {
         final GCurve2DStyle thatStyle = (GCurve2DStyle) that;
         return GMath.closeTo(_borderWidth, thatStyle._borderWidth) && _color.closeTo(thatStyle._color)
                && GMath.closeTo(_opacity, thatStyle._opacity);
      }
      return false;
   }

}
