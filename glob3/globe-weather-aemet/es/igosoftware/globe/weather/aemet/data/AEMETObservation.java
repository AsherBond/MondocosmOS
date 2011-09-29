

package es.igosoftware.globe.weather.aemet.data;


public class AEMETObservation
         implements
            Comparable<AEMETObservation> {
   private final Object _value;
   private final Lapse  _lapse;


   AEMETObservation(final Object value,
                    final Lapse lapse) {
      _value = value;
      _lapse = lapse;
   }


   @Override
   public int compareTo(final AEMETObservation that) {
      return _lapse._upper.compareTo(that._lapse._upper);
   }


   @SuppressWarnings("unchecked")
   public <T> T getValue(@SuppressWarnings("unused") final Class<T> type) {
      return (T) _value;
   }


   public Lapse getLapse() {
      return _lapse;
   }


}
