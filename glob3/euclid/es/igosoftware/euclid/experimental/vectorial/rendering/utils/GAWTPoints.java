

package es.igosoftware.euclid.experimental.vectorial.rendering.utils;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Arrays;


public class GAWTPoints {
   public final int[] _xPoints;
   public final int[] _yPoints;


   public GAWTPoints(final int[] xPoints,
                     final int[] yPoints) {
      _xPoints = xPoints;
      _yPoints = yPoints;
   }


   @Override
   public String toString() {
      return "GAWTPoints [_xPoints=" + Arrays.toString(_xPoints) + ", _yPoints=" + Arrays.toString(_yPoints) + "]";
   }


   public Shape asPolygonShape() {
      return new Polygon(_xPoints, _yPoints, _xPoints.length);
   }


   public Area asPolygonArea() {
      return new Area(asPolygonShape());
   }

}
