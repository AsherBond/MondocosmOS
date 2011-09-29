

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.Paint;
import java.awt.Stroke;


public class GNullCurve2DStyle
         implements
            ICurve2DStyle {


   public static final GNullCurve2DStyle INSTANCE = new GNullCurve2DStyle();


   private GNullCurve2DStyle() {
   }


   @Override
   public Stroke getBorderStroke() {
      return null;
   }


   @Override
   public Paint getBorderPaint() {
      return null;
   }


   @Override
   public String toString() {
      return "GNullCurve2DStyle";
   }


   @Override
   public boolean isGroupableWith(final ICurve2DStyle that) {
      return (this == that);
   }


}
