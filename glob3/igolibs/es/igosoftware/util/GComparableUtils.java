

package es.igosoftware.util;

public class GComparableUtils {

   private GComparableUtils() {
   }


   public static <T extends Comparable<T>> T max(final T a,
                                                 final T b) {
      return (a.compareTo(b) > 0) ? a : b;
   }


   public static <T extends Comparable<T>> T min(final T a,
                                                 final T b) {
      return (a.compareTo(b) < 0) ? a : b;
   }
}
