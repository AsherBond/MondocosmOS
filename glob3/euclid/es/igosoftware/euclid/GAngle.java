

package es.igosoftware.euclid;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GAngle
         implements
            Comparable<GAngle> {

   public final static GAngle  ZERO               = GAngle.fromDegrees(0);
   public final static GAngle  POS90              = GAngle.fromDegrees(90);
   public final static GAngle  NEG90              = GAngle.fromDegrees(-90);
   public final static GAngle  POS180             = GAngle.fromDegrees(180);
   public final static GAngle  NEG180             = GAngle.fromDegrees(-180);
   public final static GAngle  POS360             = GAngle.fromDegrees(360);
   public final static GAngle  NEG360             = GAngle.fromDegrees(-360);

   public final static GAngle  MINUTE             = GAngle.fromDegrees(1d / 60d);
   public final static GAngle  SECOND             = GAngle.fromDegrees(1d / 3600d);


   private final static double DEGREES_TO_RADIANS = Math.PI / 180d;
   private final static double RADIANS_TO_DEGREES = 180d / Math.PI;


   public static GAngle fromDegrees(final double degrees) {
      return new GAngle(degrees, DEGREES_TO_RADIANS * degrees);
   }


   public static GAngle fromRadians(final double radians) {
      return new GAngle(RADIANS_TO_DEGREES * radians, radians);
   }


   private final double _degrees;
   private final double _radians;


   private GAngle(final double degrees,
                  final double radians) {
      _degrees = degrees;
      _radians = radians;
   }


   public final double getDegrees() {
      return _degrees;
   }


   public final double getRadians() {
      return _radians;
   }


   public double cos() {
      return GMath.cos(_radians);
   }


   public double sin() {
      return GMath.sin(_radians);
   }


   public double tan() {
      return Math.tan(_radians);
   }


   @Override
   public final int compareTo(final GAngle that) {
      GAssert.notNull(that, "that");

      if (_degrees < that._degrees) {
         return -1;
      }
      else if (_degrees > that._degrees) {
         return 1;
      }
      else {
         return 0;
      }
   }


   @Override
   public final String toString() {
      return Double.toString(_degrees) + '\u00B0';
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(_degrees);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }


   public boolean isZero() {
      return _degrees == 0;
   }


   public boolean closeTo(final GAngle that) {
      return GMath.closeTo(_degrees, that._degrees);
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GAngle other = (GAngle) obj;
      if (Double.doubleToLongBits(_degrees) != Double.doubleToLongBits(other._degrees)) {
         return false;
      }
      return true;
   }


   public GAngle negated() {
      return GAngle.fromDegrees(-_degrees);
   }


   public GAngle add(final GAngle delta) {
      return GAngle.fromDegrees(_degrees + delta._degrees);
   }


   public GAngle sub(final GAngle delta) {
      return GAngle.fromDegrees(_degrees - delta._degrees);
   }


}
