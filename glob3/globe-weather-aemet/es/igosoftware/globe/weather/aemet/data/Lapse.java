

package es.igosoftware.globe.weather.aemet.data;

import java.util.Date;
import java.util.Iterator;

import es.igosoftware.util.GComparableUtils;


public class Lapse
         implements
            Comparable<Lapse> {

   final Date _lower;
   final Date _upper;


   public Lapse(final Date lower,
                final Date upper) {
      final Date max;
      final Date min;
      if (lower.compareTo(upper) > 0) {
         min = upper;
         max = lower;
      }
      else {
         min = lower;
         max = upper;
      }

      _lower = min;
      _upper = max;
   }


   public Date getLower() {
      return _lower;
   }


   public Date getUpper() {
      return _upper;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_lower == null) ? 0 : _lower.hashCode());
      result = prime * result + ((_upper == null) ? 0 : _upper.hashCode());
      return result;
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
      final Lapse other = (Lapse) obj;
      if (_lower == null) {
         if (other._lower != null) {
            return false;
         }
      }
      else if (!_lower.equals(other._lower)) {
         return false;
      }
      if (_upper == null) {
         if (other._upper != null) {
            return false;
         }
      }
      else if (!_upper.equals(other._upper)) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return "Lapse [" + _lower + " -> " + _upper + "]";
   }


   public static Lapse merge(final Iterable<Lapse> ranges) {

      final Iterator<Lapse> iterator = ranges.iterator();
      if (!iterator.hasNext()) {
         return null;
      }

      final Lapse first = iterator.next();
      Date lower = first._lower;
      Date upper = first._upper;

      while (iterator.hasNext()) {
         final Lapse range = iterator.next();

         lower = GComparableUtils.min(lower, range._lower);
         upper = GComparableUtils.max(upper, range._upper);
      }

      return new Lapse(lower, upper);
   }


   @Override
   public int compareTo(final Lapse that) {

      final int lowerCmp = _lower.compareTo(that._lower);
      final int upperCmp = _upper.compareTo(that._upper);

      if ((lowerCmp == 0) && (upperCmp == 0)) {
         return 0;
      }

      if (lowerCmp == 0) {
         return upperCmp;
      }
      return lowerCmp;
   }


}
